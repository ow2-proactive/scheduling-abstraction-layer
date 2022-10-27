/*
 * ProActive Parallel Suite(TM):
 * The Open Source library for parallel and distributed
 * Workflows & Scheduling, Orchestration, Cloud Automation
 * and Big Data Analysis on Enterprise Grids & Clouds.
 *
 * Copyright (c) 2007 - 2017 ActiveEon
 * Contact: contact@activeeon.com
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation: version 3 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
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
import org.ow2.proactive.sal.service.model.*;
import org.ow2.proactive.sal.service.service.RepositoryService;
import org.ow2.proactive.sal.service.service.application.PAConnectorIaasGateway;
import org.ow2.proactive.sal.service.util.GeoLocationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import lombok.extern.slf4j.Slf4j;


@Slf4j
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
            }
        }
        if (attributeRequirement.getRequirementClass().equals("location")) {
            if (attributeRequirement.getRequirementAttribute().equals("geoLocation.country")) {
                return attributeRequirement.getRequirementOperator()
                                           .compare(nodeCandidate.getLocation().getGeoLocation().getCountry(),
                                                    attributeRequirement.getValue());
            }
        }
        if (attributeRequirement.getRequirementClass().equals("image")) {
            switch (attributeRequirement.getRequirementAttribute()) {
                case "name":
                    return attributeRequirement.getRequirementOperator().compare(nodeCandidate.getImage().getName(),
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
                if (nodeCandidate.getNodeCandidateType() == NodeCandidate.NodeCandidateTypeEnum.BYON) {
                    ByonNode byonNode = getByonNodeFromNC(nodeCandidate);
                    if (byonNode == null) {
                        LOGGER.error("no node candiddates match this Byon node");
                        return false;
                    }
                    return attributeRequirement.getRequirementOperator().compare(byonNode.getName(),
                                                                                 attributeRequirement.getValue());
                }
                if (nodeCandidate.getNodeCandidateType() == NodeCandidate.NodeCandidateTypeEnum.EDGE) {
                    EdgeNode edgeNode = getEdgeNodeFromNC(nodeCandidate);
                    if (edgeNode == null) {
                        LOGGER.error("no node candiddates match this Edge node");
                        return false;
                    }
                    return attributeRequirement.getRequirementOperator().compare(edgeNode.getName(),
                                                                                 attributeRequirement.getValue());
                }
            }
        }
        LOGGER.warn("Unknown requirement type. It could not be applied: " + attributeRequirement.toString());
        return true;
    }

    private static boolean satisfyNodeTypeRequirement(NodeTypeRequirement requirement, NodeCandidate nodeCandidate) {
        return (requirement.getNodeTypes().stream().anyMatch(nodeType -> {
            if (nodeType.getLiteral().equals(nodeCandidate.getNodeCandidateType().name()) &&
                ((nodeType.equals(NodeType.BYON) &&
                  requirement.getJobIdForBYON()
                             .equals(nodeCandidate.getJobIdForBYON())) ||
                 (nodeType.equals(NodeType.EDGE) &&
                  requirement.getJobIdForEDGE().equals(nodeCandidate.getJobIdForEDGE())))) {
                return true;
            }
            // THIS LOG IS ADDED FOR TESTING,TO BE IMPROVED LATER
            else {
                if (!nodeType.equals(NodeType.BYON) && !nodeType.equals(NodeType.EDGE)) {
                    if (nodeType.getLiteral().equals(nodeCandidate.getNodeCandidateType().name())) {
                        return true;
                    } else {
                        LOGGER.info("the nodeType in the requirement \"{}\" is mismatched with the node candidate \"{}\" nodeType \"{}\"",
                                    nodeType.getLiteral(),
                                    nodeCandidate.getId(),
                                    nodeCandidate.getNodeCandidateType().name());
                        return false;
                    }
                } else {
                    LOGGER.info("nodeType or jobId mismatch, \n " +
                                "Required: nodeType \"{}\", jobID for BYON and EDGE: \"{}\", \"{}\" \n" +
                                "Node candidate: nodeType \"{}\", jobID for BYON and EDGE: \"{}\", \"{}\" \n",
                                nodeType.getLiteral(),
                                requirement.getJobIdForBYON(),
                                requirement.getJobIdForEDGE(),
                                nodeCandidate.getNodeCandidateType().name(),
                                nodeCandidate.getJobIdForBYON(),
                                nodeCandidate.getJobIdForEDGE());
                    return false;
                }

            } // THIS LOG IS ADDED FOR TESTING,TO BE IMPROVED LATER
        }));
    }

    private Hardware createHardware(JSONObject nodeCandidateJSON, PACloud paCloud) {
        JSONObject hardwareJSON = nodeCandidateJSON.optJSONObject("hw");
        String hardwareId = paCloud.getCloudID() + "/" + nodeCandidateJSON.optString("region") + "/" +
                            hardwareJSON.optString("type");
        Hardware hardware = repositoryService.getHardware(hardwareId);
        if (hardware == null) {
            hardware = new Hardware();
            hardware.setId(hardwareId);
            hardware.setName(hardwareJSON.optString("type"));
            hardware.setProviderId(hardwareJSON.optString("type"));
            hardware.setCores(Math.round(Float.parseFloat(hardwareJSON.optString("minCores"))));
            hardware.setRam(Long.valueOf(hardwareJSON.optString("minRam")));
            if ("aws-ec2".equals(nodeCandidateJSON.optString("cloud"))) {
                hardware.setDisk((double) 8);
            } else {
                hardware.setDisk((double) 0);
            }
            hardware.setLocation(createLocation(nodeCandidateJSON, paCloud));

            repositoryService.updateHardware(hardware);
        }

        return hardware;
    }

    private Location createLocation(JSONObject nodeCandidateJSON, PACloud paCloud) {
        String locationId = paCloud.getCloudID() + "/" + nodeCandidateJSON.optString("region");
        Location location = repositoryService.getLocation(locationId);
        if (location == null) {
            location = new Location();
            location.setId(locationId);
            location.name(nodeCandidateJSON.optString("region"));
            location.setProviderId(nodeCandidateJSON.optString("region"));
            location.setLocationScope(Location.LocationScopeEnum.REGION);
            location.setIsAssignable(true);
            location.setGeoLocation(createGeoLocation(paCloud.getCloudProviderName(), location.getName()));

            repositoryService.updateLocation(location);
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
        String imageId = paCloud.getCloudID() + "/" + imageJSON.optString("id");
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

            repositoryService.updateImage(image);
        }

        return image;
    }

    private Cloud createCloud(JSONObject nodeCandidateJSON, PACloud paCloud) {
        Cloud cloud = repositoryService.getCloud(paCloud.getCloudID());
        if (cloud == null) {
            cloud = new Cloud();
            cloud.setId(paCloud.getCloudID());
            cloud.setCloudType(paCloud.getCloudType());
            cloud.setApi(new Api(nodeCandidateJSON.optString("cloud")));
            cloud.setCredential(new CloudCredential());
            cloud.setCloudConfiguration(new CloudConfiguration("", new HashMap<>()));

            repositoryService.updateCloud(cloud);
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

    public void updateNodeCandidates(List<String> newCloudIds) {
        newCloudIds.forEach(newCloudId -> {
            PACloud paCloud = repositoryService.getPACloud(newCloudId);
            LOGGER.info("Getting blacklisted regions...");
            List<String> blacklistedRegions = Arrays.asList(paCloud.getBlacklist().split(","));
            LOGGER.info("Blacklisted regions: {}", blacklistedRegions);

            LOGGER.info("Getting images from Proactive ...");
            JSONArray images = connectorIaasGateway.getImages(paCloud.getDummyInfrastructureName());
            if (images == null) {
                LOGGER.warn(String.format("No available images were found for the cloud [%s]. Please check your configuration.",
                                          paCloud.getCloudID()));
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
                repositoryService.updateNodeCandidate(newNodeCandidate);
                IaasNode newIaasNode = new IaasNode(newNodeCandidate);
                repositoryService.updateIaasNode(newIaasNode);
                newNodeCandidate.setNodeId(newIaasNode.getId());
                repositoryService.updateNodeCandidate(newNodeCandidate);
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

    private static ByonNode getByonNodeFromNC(NodeCandidate nodeCandidate) {
        List<ByonNode> allByonNodes = staticRepositoryService.listByonNodes();
        for (ByonNode byonNode : allByonNodes) {
            if (byonNode.getNodeCandidate().getId().equals(nodeCandidate.getId())) {
                return byonNode;
            }
        }
        return null;
    }

    private static EdgeNode getEdgeNodeFromNC(NodeCandidate nodeCandidate) {
        List<EdgeNode> allEdgeNodes = staticRepositoryService.listEdgeNodes();
        for (EdgeNode edgeNode : allEdgeNodes) {
            if (edgeNode.getNodeCandidate().getId().equals(nodeCandidate.getId())) {
                return edgeNode;
            }
        }
        return null;
    }

    public long cleanNodeCandidates(List<String> newCloudIds) {
        return repositoryService.listNodeCandidates()
                                .stream()
                                .filter(nodeCandidate -> newCloudIds.contains(nodeCandidate.getCloud().getId()))
                                .map(nodeCandidate -> repositoryService.deleteNodeCandidate(nodeCandidate.getId()))
                                .count();
    }
}
