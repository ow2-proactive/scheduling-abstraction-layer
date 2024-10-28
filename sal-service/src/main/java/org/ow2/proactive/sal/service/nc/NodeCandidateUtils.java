/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.nc;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.javatuples.Quartet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ow2.proactive.sal.model.*;
import org.ow2.proactive.sal.service.service.RepositoryService;
import org.ow2.proactive.sal.service.service.infrastructure.PAConnectorIaasGateway;
import org.ow2.proactive.sal.service.util.GeoLocationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import lombok.extern.log4j.Log4j2;


@Log4j2
@Component
public class NodeCandidateUtils {

    @Autowired
    private PAConnectorIaasGateway connectorIaasGateway;

    @Autowired
    private RepositoryService repositoryService;

    private static RepositoryService staticRepositoryService;

    private GeoLocationUtils geoLocationUtils;

    private LoadingCache<Quartet<PACloud, String, String, String>, JSONArray> nodeCandidatesCache;

    @PostConstruct
    private void initStaticAttributes() {
        staticRepositoryService = this.repositoryService;
    }

    public void initNodeCandidateUtils() {
        geoLocationUtils = new GeoLocationUtils();
        nodeCandidatesCache = CacheBuilder.newBuilder()
                                          .maximumSize(100)
                                          .expireAfterWrite(60, TimeUnit.MINUTES)
                                          .build(new CacheLoader<Quartet<PACloud, String, String, String>, JSONArray>() {
                                              @Override
                                              public JSONArray load(Quartet<PACloud, String, String, String> key) {
                                                  return getAllPagedNodeCandidates(key.getValue0(),
                                                                                   key.getValue1(),
                                                                                   key.getValue2(),
                                                                                   key.getValue3());
                                              }
                                          });
    }

    public static boolean verifyAllFilters(List<Requirement> requirements, NodeCandidate nodeCandidate) {
        if (requirements == null || requirements.isEmpty()) {
            LOGGER.info("   Satisfy all requirements!!!");
            return true;
        }
        if (requirements.get(0) instanceof NodeTypeRequirement) {
            if (satisfyNodeTypeRequirement((NodeTypeRequirement) requirements.get(0), nodeCandidate)) {
                LOGGER.info("   satisfyNodeTypeRequirement:       YES");
                return verifyAllFilters(requirements.subList(1, requirements.size()), nodeCandidate);
            }
            LOGGER.info("   satisfyNodeTypeRequirement:       NO");
            return false;
        }
        if (requirements.get(0) instanceof AttributeRequirement) {
            if (satisfyAttributeRequirement((AttributeRequirement) requirements.get(0), nodeCandidate)) {
                LOGGER.info("   satisfyAttributeRequirement:       YES");
                return verifyAllFilters(requirements.subList(1, requirements.size()), nodeCandidate);
            }
            LOGGER.info("   satisfyAttributeRequirement:       NO");
            return false;
        }
        LOGGER.warn("Unknown requirement type. It could not be applied: " + requirements.get(0).toString());
        return verifyAllFilters(requirements.subList(1, requirements.size()), nodeCandidate);
    }

    private static boolean satisfyAttributeRequirement(AttributeRequirement attributeRequirement,
            NodeCandidate nodeCandidate) {
        // THIS LOG IS ADDED FOR TESTING,TO BE IMPROVED LATER
        LOGGER.info("Checking the attribute requirement: \n {} \n for node candidate \"{}\" ",
                    attributeRequirement.toString(),
                    nodeCandidate.getId());
        // THIS LOG IS ADDED FOR TESTING,TO BE IMPROVED LATER
        if (attributeRequirement.getRequirementClass().equals("hardware")) {
            switch (attributeRequirement.getRequirementAttribute()) {
                case "ram":
                    return attributeRequirement.getRequirementOperator()
                                               .compare(nodeCandidate.getHardware().getRam(),
                                                        Long.valueOf(attributeRequirement.getValue()));
                case "cores":
                    return attributeRequirement.getRequirementOperator()
                                               .compare(nodeCandidate.getHardware().getCores(),
                                                        Integer.valueOf(attributeRequirement.getValue()));
                case "disk":
                    return attributeRequirement.getRequirementOperator()
                                               .compare(nodeCandidate.getHardware().getDisk(),
                                                        Double.valueOf(attributeRequirement.getValue()));
                case "fpga":
                    return attributeRequirement.getRequirementOperator()
                                               .compare(nodeCandidate.getHardware().getFpga(),
                                                        Integer.valueOf(attributeRequirement.getValue()));
                case "name":
                    return attributeRequirement.getRequirementOperator().compare(nodeCandidate.getHardware().getName(),
                                                                                 attributeRequirement.getValue());
            }
        }
        if (attributeRequirement.getRequirementClass().equals("location")) {
            //            if (attributeRequirement.getRequirementAttribute().equals("geoLocation.country")) {
            switch (attributeRequirement.getRequirementAttribute()) {
                case "geoLocation.country":
                    return attributeRequirement.getRequirementOperator()
                                               .compare(nodeCandidate.getLocation().getGeoLocation().getCountry(),
                                                        attributeRequirement.getValue());
                case "name":
                    return attributeRequirement.getRequirementOperator().compare(nodeCandidate.getLocation().getName(),
                                                                                 attributeRequirement.getValue());
            }
        }
        if (attributeRequirement.getRequirementClass().equals("image")) {
            switch (attributeRequirement.getRequirementAttribute()) {
                case "name":
                    return attributeRequirement.getRequirementOperator().compare(nodeCandidate.getImage().getName(),
                                                                                 attributeRequirement.getValue());
                case "id":
                    return attributeRequirement.getRequirementOperator().compare(nodeCandidate.getImage().getId(),
                                                                                 attributeRequirement.getValue());
                case "operatingSystem.family":
                    return attributeRequirement.getRequirementOperator().compare(nodeCandidate.getImage()
                                                                                              .getOperatingSystem()
                                                                                              .getOperatingSystemFamily()
                                                                                              .name(),
                                                                                 attributeRequirement.getValue());
                case "operatingSystem.version":
                    return attributeRequirement.getRequirementOperator().compare(nodeCandidate.getImage()
                                                                                              .getOperatingSystem()
                                                                                              .getOperatingSystemVersion()
                                                                                              .toString(),
                                                                                 attributeRequirement.getValue());
            }
        }
        if (attributeRequirement.getRequirementClass().toLowerCase(Locale.ROOT).equals("cloud")) {
            if (attributeRequirement.getRequirementAttribute().equals("type")) {
                return attributeRequirement.getRequirementOperator()
                                           .compare(nodeCandidate.getCloud().getCloudType().name(),
                                                    attributeRequirement.getValue());
            }
            if (attributeRequirement.getRequirementAttribute().equals("id")) {
                return attributeRequirement.getRequirementOperator().compare(nodeCandidate.getCloud().getId(),
                                                                             attributeRequirement.getValue());
            }
        }
        if (attributeRequirement.getRequirementClass().toLowerCase(Locale.ROOT).equals("environment")) {
            if (attributeRequirement.getRequirementAttribute().equals("runtime")) {
                return attributeRequirement.getRequirementOperator()
                                           .compare(nodeCandidate.getEnvironment().getRuntime().name(),
                                                    attributeRequirement.getValue());
            }
        }
        if (attributeRequirement.getRequirementClass().toLowerCase(Locale.ROOT).equals("name")) {
            if (attributeRequirement.getRequirementAttribute().equals("placementName")) {
                if (nodeCandidate.getNodeCandidateType() == NodeCandidate.NodeCandidateTypeEnum.BYON ||
                    nodeCandidate.getNodeCandidateType() == NodeCandidate.NodeCandidateTypeEnum.EDGE) {
                    return attributeRequirement.getRequirementOperator().compare(nodeCandidate.getHardware().getName(),
                                                                                 attributeRequirement.getValue());
                }
            }
        }
        LOGGER.warn("Unknown requirement type. It could not be applied: " + attributeRequirement.toString());
        return true;

    }

    private static boolean satisfyNodeTypeRequirement(NodeTypeRequirement requirement, NodeCandidate nodeCandidate) {
        return (requirement.getNodeTypes().stream().anyMatch(nodeType -> {
            if (nodeType.getLiteral().equals(nodeCandidate.getNodeCandidateType().name())) {
                return true;
            } else { // THIS LOG IS ADDED FOR TESTING,TO BE IMPROVED LATER
                LOGGER.info("the nodeType in the requirement \"{}\" is mismatched with the node candidate \"{}\" nodeType \"{}\"",
                            nodeType.getLiteral(),
                            nodeCandidate.getId(),
                            nodeCandidate.getNodeCandidateType().name());
                return false;
            }
        }));
    }

    private Hardware createHardware(JSONObject nodeCandidateJSON, PACloud paCloud) {
        JSONObject hardwareJSON = nodeCandidateJSON.optJSONObject("hw");
        String hardwareId = paCloud.getCloudId() + "/" + nodeCandidateJSON.optString("region") + "/" +
                            hardwareJSON.optString("type");
        Hardware hardware = repositoryService.getHardware(hardwareId);
        if (hardware == null) {
            hardware = new Hardware();
            hardware.setId(hardwareId);
            hardware.setName(hardwareJSON.optString("type"));
            hardware.setProviderId(hardwareJSON.optString("type"));
            hardware.setCores(Math.round(Float.parseFloat(hardwareJSON.optString("minCores"))));
            String minRam = hardwareJSON.optString("minRam");
            if (minRam.endsWith(".0")) {
                minRam = minRam.replace(".0", "");
            }
            hardware.setRam(Long.valueOf(minRam));
            hardware.setFpga(hardwareJSON.optString("type"));

            if ("aws-ec2".equals(nodeCandidateJSON.optString("cloud"))) {
                hardware.setDisk((double) 8);
            } else {
                hardware.setDisk((double) 0);
            }
            hardware.setLocation(createLocation(nodeCandidateJSON, paCloud));

            repositoryService.saveHardware(hardware);
        }

        return hardware;
    }

    private Location createLocation(JSONObject nodeCandidateJSON, PACloud paCloud) {
        String locationId = paCloud.getCloudId() + "/" + nodeCandidateJSON.optString("region");
        Location location = repositoryService.getLocation(locationId);
        if (location == null) {
            location = new Location();
            location.setId(locationId);
            location.name(nodeCandidateJSON.optString("region"));
            location.setProviderId(nodeCandidateJSON.optString("region"));
            location.setLocationScope(Location.LocationScopeEnum.REGION);
            location.setIsAssignable(true);
            location.setGeoLocation(createGeoLocation(paCloud.getCloudProviderName(), location.getName()));

            repositoryService.saveLocation(location);
        }
        return location;
    }

    private GeoLocation createGeoLocation(String cloud, String region) {
        switch (cloud) {
            case "aws-ec2":
                return new GeoLocation(geoLocationUtils.findGeoLocation("AWS", region));
            case "azure":
                return new GeoLocation(geoLocationUtils.findGeoLocation("Azure", region));
            case "gce":
                return new GeoLocation(geoLocationUtils.findGeoLocation("GCE", region));
            case "openstack":
                return new GeoLocation(geoLocationUtils.findGeoLocation("OVH", region));
        }
        LOGGER.warn("Cloud provider name no handled for Geo Location.");
        return new GeoLocation();
    }

    private Image createImage(JSONObject nodeCandidateJSON, JSONObject imageJSON, PACloud paCloud) {
        String imageId = paCloud.getCloudId() + "/" + imageJSON.optString("id");
        Image image = repositoryService.getImage(imageId);
        if (image == null) {
            image = new Image();
            image.setId(imageId);
            image.setName(imageJSON.optString("name"));
            image.setProviderId(StringUtils.substringAfterLast(imageJSON.optString("id"), "/"));
            OperatingSystem os = new OperatingSystem();
            JSONObject osJSON = imageJSON.optJSONObject("operatingSystem");
            os.setOperatingSystemFamily(OperatingSystemFamily.fromValue(osJSON.optString("family")));

            String arch = "";
            if ("aws-ec2".equals(nodeCandidateJSON.optString("cloud"))) {
                if (nodeCandidateJSON.optJSONObject("hw").optString("type").startsWith("a")) {
                    arch = osJSON.optBoolean("is64Bit") ? "ARM64" : "ARM";
                } else {
                    arch = osJSON.optBoolean("is64Bit") ? "AMD64" : "i386";
                }
            }
            os.setOperatingSystemArchitecture(OperatingSystemArchitecture.fromValue(arch));
            os.setOperatingSystemVersion(osJSON.optBigDecimal("version", BigDecimal.valueOf(0)));
            image.setOperatingSystem(os);
            image.setLocation(createLocation(nodeCandidateJSON, paCloud));

            repositoryService.saveImage(image);
        }

        return image;
    }

    private Cloud createCloud(JSONObject nodeCandidateJSON, PACloud paCloud) {
        Cloud cloud = repositoryService.getCloud(paCloud.getCloudId());
        if (cloud == null) {
            cloud = new Cloud();
            cloud.setId(paCloud.getCloudId());
            cloud.setCloudType(paCloud.getCloudType());
            cloud.setApi(new Api(nodeCandidateJSON.optString("cloud")));
            cloud.setCredential(new CloudCredential());
            cloud.setCloudConfiguration(new CloudConfiguration("", new HashMap<>()));

            repositoryService.saveCloud(cloud);
        }
        return cloud;
    }

    public NodeCandidate createNodeCandidate(JSONObject nodeCandidateJSON, JSONObject imageJSON, PACloud paCloud) {
        NodeCandidate nodeCandidate = new NodeCandidate();
        nodeCandidate.setNodeCandidateType(NodeCandidate.NodeCandidateTypeEnum.IAAS);
        nodeCandidate.setPrice(nodeCandidateJSON.optDouble("price"));
        nodeCandidate.setCloud(createCloud(nodeCandidateJSON, paCloud));

        nodeCandidate.setLocation(createLocation(nodeCandidateJSON, paCloud));
        nodeCandidate.setImage(createImage(nodeCandidateJSON, imageJSON, paCloud));
        nodeCandidate.setHardware(createHardware(nodeCandidateJSON, paCloud));

        nodeCandidate.setPricePerInvocation((double) 0);
        nodeCandidate.setMemoryPrice((double) 0);
        nodeCandidate.setEnvironment(new Environment());
        return nodeCandidate;
    }

    private static JSONObject convertObjectToJson(Object object) {
        JSONObject myJson = null;
        try {
            myJson = new JSONObject(new ObjectMapper().writeValueAsString(object));
        } catch (IOException e) {
            LOGGER.error("Error in casting Hashmap to JSON: " + Arrays.toString(e.getStackTrace()));
        }
        return myJson;
    }

    public void saveNodeCandidates(List<String> newCloudIds) {
        newCloudIds.forEach(newCloudId -> {
            PACloud paCloud = repositoryService.getPACloud(newCloudId);
            LOGGER.info("Getting blacklisted regions...");
            List<String> blacklistedRegions = Arrays.asList(paCloud.getBlacklist().split(","));
            LOGGER.info("Blacklisted regions: {}", blacklistedRegions);

            LOGGER.info("Getting images from Proactive ...");
            JSONArray images = connectorIaasGateway.getImages(paCloud.getDummyInfrastructureName());
            if (images == null) {
                LOGGER.warn(String.format("No available images were found for the cloud [%s]. Please check your configuration.",
                                          paCloud.getCloudId()));
                return;
            }
            LOGGER.info("Returned images: {}", images);
            List<JSONObject> consolidatedImages = images.toList()
                                                        .parallelStream()
                                                        .map(NodeCandidateUtils::convertObjectToJson)
                                                        .filter(record -> !blacklistedRegions.contains(record.get("location")))
                                                        .collect(Collectors.toList());
            LOGGER.info("Consolidated images: {}", consolidatedImages);

            //TODO: (Optimization) An images per region map structure <region,[image1,image2]> could be the best here.
            // It can reduce the getNodeCandidates calls to PA.
            List<String> entries = new LinkedList<>();
            List<String> openstackOsList = Arrays.asList("Ubuntu", "Fedora", "Centos", "Debian");
            consolidatedImages.forEach(image -> {
                String region = image.optString("location");
                String imageReq;
                String os = (String) ((JSONObject) image.get("operatingSystem")).get("family");
                os = os.substring(0, 1).toUpperCase() + os.substring(1);
                String pair = os + ":" + region;
                switch (paCloud.getCloudProviderName()) {
                    case "aws-ec2":
                        imageReq = "Linux";
                        break;
                    case "openstack":
                        imageReq = os;
                        break;
                    default:
                        throw new IllegalArgumentException("The infrastructure " + paCloud.getCloudProviderName() +
                                                           " is not handled yet.");
                }

                if (paCloud.getCloudProviderName().equals("openstack")) {
                    entries.add(pair);
                }
                populateNodeCandidatesFromCache(paCloud, region, imageReq, image);
            });
        });

        repositoryService.flush();
    }

    private void populateNodeCandidatesFromCache(PACloud paCloud, String region, String imageReq, JSONObject image) {
        try {
            JSONArray nodeCandidates = nodeCandidatesCache.get(Quartet.with(paCloud, region, imageReq, ""));
            nodeCandidates.forEach(nc -> {
                JSONObject nodeCandidate = (JSONObject) nc;
                createLocation(nodeCandidate, paCloud);
                NodeCandidate newNodeCandidate = createNodeCandidate(nodeCandidate, image, paCloud);
                repositoryService.saveNodeCandidate(newNodeCandidate);
                IaasNode newIaasNode = new IaasNode(newNodeCandidate);
                repositoryService.saveIaasNode(newIaasNode);
                newNodeCandidate.setNodeId(newIaasNode.getId());
                repositoryService.saveNodeCandidate(newNodeCandidate);
            });
        } catch (ExecutionException ee) {
            LOGGER.error("Could not get node candidates from cache: ", ee);
        }
    }

    private JSONArray getAllPagedNodeCandidates(PACloud paCloud, String region, String imageReq, String token) {
        JSONObject nodeCandidates = connectorIaasGateway.getNodeCandidates(paCloud.getDummyInfrastructureName(),
                                                                           region,
                                                                           imageReq,
                                                                           token);
        try {
            if (!nodeCandidates.optString("nextToken").isEmpty()) {
                return (joinJSONArrays(nodeCandidates.optJSONArray("nodeCandidates"),
                                       nodeCandidatesCache.get(Quartet.with(paCloud,
                                                                            region,
                                                                            imageReq,
                                                                            nodeCandidates.optString("nextToken")))));
            }
        } catch (ExecutionException ee) {
            LOGGER.error("Could not get node candidates from cache: ", ee);
        }
        return (nodeCandidates.optJSONArray("nodeCandidates"));
    }

    private JSONArray joinJSONArrays(JSONArray... jsonArrays) {
        JSONArray resultJSONArray = new JSONArray();
        Arrays.stream(jsonArrays)
              .forEach(jsonArray -> IntStream.range(0, jsonArray.length())
                                             .mapToObj(jsonArray::get)
                                             .forEach(resultJSONArray::put));
        return resultJSONArray;
    }

    public long cleanNodeCandidates(List<String> newCloudIds) {
        List<NodeCandidate> nodeCandidatesToBeRemoved = repositoryService.listNodeCandidates()
                                                                         .stream()
                                                                         .filter(nodeCandidate -> newCloudIds.contains(nodeCandidate.getCloud()
                                                                                                                                    .getId()))
                                                                         .collect(Collectors.toList());
        try {
            LOGGER.info("Deleting nodes associated with the clouds {}", newCloudIds);
            // TODO: try finding a way to delete the nodes in batch rather than one by one
            repositoryService.deleteBatchNodes(nodeCandidatesToBeRemoved);
        } catch (Exception e) {
            LOGGER.error("An error occurred when deleting the nodes of the clouds {}: {}", newCloudIds, e);
        }
        try {
            LOGGER.info("Deleting node candidates associated with the clouds {}", newCloudIds);
            repositoryService.deleteBatchNodeCandidates(newCloudIds);
        } catch (Exception e) {
            LOGGER.error("An error occurred when deleting the node candidates of the clouds {}: {}", newCloudIds, e);
        }
        return nodeCandidatesToBeRemoved.size();
    }
}
