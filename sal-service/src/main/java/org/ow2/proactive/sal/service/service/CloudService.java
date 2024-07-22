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
package org.ow2.proactive.sal.service.service;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.Validate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ow2.proactive.sal.model.*;
import org.ow2.proactive.sal.service.nc.NodeCandidateUtils;
import org.ow2.proactive.sal.service.nc.UpdatingNodeCandidatesUtils;
import org.ow2.proactive.sal.service.nc.WhiteListedInstanceTypesUtils;
import org.ow2.proactive.sal.service.service.infrastructure.PAConnectorIaasGateway;
import org.ow2.proactive.sal.service.service.infrastructure.PAResourceManagerGateway;
import org.ow2.proactive.sal.service.util.JCloudsInstancesUtils;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.ow2.proactive_grid_cloud_portal.scheduler.exception.PermissionRestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;


@Log4j2
@Service("CloudService")
public class CloudService {

    private static List<Future<Boolean>> asyncNodeCandidatesProcessesResults = new ArrayList<>();

    @Autowired
    private PAGatewayService paGatewayService;

    @Autowired
    private PAConnectorIaasGateway connectorIaasGateway;

    @Autowired
    private PAResourceManagerGateway resourceManagerGateway;

    @Autowired
    private UpdatingNodeCandidatesUtils updatingNodeCandidatesUtils;

    @Autowired
    private NodeCandidateUtils nodeCandidateUtils;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * Add clouds to the ProActive Resource Manager
     * @param sessionId A valid session id
     * @param clouds A list of CloudDefinition instances
     * @return 0 if clouds has been added properly. A greater than 0 value otherwise.
     */
    public Integer addClouds(String sessionId, List<CloudDefinition> clouds) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Validate.notNull(clouds, "The received clouds structure is empty. Nothing to be created.");

        List<String> savedCloudIds = new LinkedList<>();
        clouds.forEach(cloud -> {
            PACloud newCloud = new PACloud();
            String nodeSourceNamePrefix = cloud.getCloudProviderName() + "-" + cloud.getCloudId();
            newCloud.setNodeSourceNamePrefix(nodeSourceNamePrefix);
            newCloud.setCloudId(cloud.getCloudId());
            newCloud.setCloudProviderName(cloud.getCloudProviderName());
            newCloud.setCloudType(cloud.getCloudType());
            newCloud.setDeployedRegions(new HashMap<>());
            newCloud.setDeployedWhiteListedRegions(new HashMap<>());
            newCloud.setSubnet(cloud.getSubnet());
            newCloud.setSecurityGroup(cloud.getSecurityGroup());
            newCloud.setEndpoint(cloud.getEndpoint());
            newCloud.setScopePrefix(cloud.getScope().getPrefix());
            newCloud.setScopeValue(cloud.getScope().getValue());
            newCloud.setIdentityVersion(cloud.getIdentityVersion());
            newCloud.setDefaultNetwork(cloud.getDefaultNetwork());
            newCloud.setBlacklist(Optional.ofNullable(cloud.getBlacklist()).orElse(""));

            newCloud.setSshCredentials(cloud.getSshCredentials());

            Credentials credentials = new Credentials();
            credentials.setUserName(cloud.getCredentials().getUser());
            credentials.setPrivateKey(cloud.getCredentials().getSecret());
            credentials.setDomain(cloud.getCredentials().getDomain());
            repositoryService.saveCredentials(credentials);
            newCloud.setCredentials(credentials);

            String dummyInfraName = "iamadummy" + newCloud.getCloudProviderName() + "_" + newCloud.getCloudId();
            connectorIaasGateway.defineInfrastructure(dummyInfraName, newCloud, "");
            newCloud.setDummyInfrastructureName(dummyInfraName);

            repositoryService.savePACloud(newCloud);
            LOGGER.debug("Cloud created: " + newCloud.toString());
            savedCloudIds.add(newCloud.getCloudId());
        });

        repositoryService.flush();

        LOGGER.info("Clouds created properly.");

        cleanDoneAsyncProcesses();
        try {
            asyncNodeCandidatesProcessesResults.add(updatingNodeCandidatesUtils.asyncUpdate(savedCloudIds));
        } catch (InterruptedException ie) {
            LOGGER.warn("Thread updating node candidates interrupted!", ie);
        }

        return 0;
    }

    private static void cleanDoneAsyncProcesses() {
        LOGGER.info("Cleaning asyncNodeCandidatesProcessesResults structure ...");
        List<Future<Boolean>> updatedList = new ArrayList<>();
        for (Future<Boolean> f : asyncNodeCandidatesProcessesResults) {
            if (!f.isDone()) {
                updatedList.add(f);
            }
        }
        asyncNodeCandidatesProcessesResults = updatedList;
    }

    /**
     * Verify if there is any asynchronous fetching/cleaning node candidates process in progress
     * @param sessionId A valid session id
     * @return true if at least one asynchronous node candidates process is in progress, false otherwise
     */
    public Boolean isAnyAsyncNodeCandidatesProcessesInProgress(String sessionId) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        return asyncNodeCandidatesProcessesResults.stream().parallel().anyMatch(result -> !result.isDone());
    }

    /**
     * Get all registered clouds
     * @param sessionId A valid session id
     * @return List of all table PACloud's entries
     */
    public List<PACloud> getAllClouds(String sessionId) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        List<PACloud> clouds = new ArrayList<>();
        for (PACloud cloud : repositoryService.listPACloud()) {
            cloud.setCredentials(hideCredentials(cloud.getCredentials()));
            cloud.setSshCredentials(hideSshCredentials(cloud.getSshCredentials()));
            clouds.add(cloud);
        }
        return clouds;
    }

    /**
     * Get all added clouds with specific ids
     * @param sessionId A valid session id
     * @param cloudIds Valid cloud ids
     * @return List of all table PACloud's entries
     */
    public List<PACloud> findCloudsByIds(String sessionId, List<String> cloudIds) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        List<PACloud> clouds = new ArrayList<>();
        for (PACloud cloud : repositoryService.findAllPAClouds(cloudIds)) {
            cloud.setCredentials(hideCredentials(cloud.getCredentials()));
            cloud.setSshCredentials(hideSshCredentials(cloud.getSshCredentials()));
            clouds.add(cloud);
        }
        return clouds;
    }

    /**
     * Undeploy clouds
     * @param sessionId A valid session id
     * @param cloudIds List of cloud IDs to remove
     * @param preempt If true undeploy node source immediately without waiting for nodes to be freed
     */
    public Boolean undeployClouds(String sessionId, List<String> cloudIds, Boolean preempt)
            throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        cloudIds.forEach(cloudId -> {
            PACloud cloud = repositoryService.getPACloud(cloudId);
            for (Map.Entry<String, String> entry : cloud.getDeployedRegions().entrySet()) {
                try {
                    resourceManagerGateway.undeployNodeSource(cloud.getNodeSourceNamePrefix() + entry.getKey(),
                                                              preempt);
                } catch (NotConnectedException | PermissionRestException e) {
                    LOGGER.error(Arrays.toString(e.getStackTrace()));
                }
            }
            for (Map.Entry<String, String> entry : cloud.getDeployedWhiteListedRegions().entrySet()) {
                try {
                    resourceManagerGateway.undeployNodeSource(PACloud.WHITE_LISTED_NAME_PREFIX +
                                                              cloud.getNodeSourceNamePrefix() + entry.getKey(),
                                                              preempt);
                } catch (NotConnectedException | PermissionRestException e) {
                    LOGGER.error(Arrays.toString(e.getStackTrace()));
                }
            }
        });
        return true;
    }

    /**
     * Remove clouds
     * @param sessionId A valid session id
     * @param cloudIds List of cloud IDs to remove
     * @param preempt If true undeploy node source immediately without waiting for nodes to be freed
     */
    public Boolean removeClouds(String sessionId, List<String> cloudIds, Boolean preempt) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Boolean flag = true;
        Boolean tempFlag;
        for (String cloudId : cloudIds) {
            PACloud cloud = repositoryService.getPACloud(cloudId);
            if (cloud == null) {
                LOGGER.info("Cloud {} not found, nothing to be removed.", cloudId);
                flag = tempFlag = false;
            } else if (cloud.getCloudType() == CloudType.PUBLIC || cloud.getCloudType() == CloudType.PRIVATE) {
                tempFlag = removeIaasCloudNS(sessionId, cloud, preempt);
                flag = flag && tempFlag;
            } else if (cloud.getCloudType() == CloudType.BYON || cloud.getCloudType() == CloudType.EDGE) {
                tempFlag = removeByonCloudNS(sessionId, cloud, preempt);
                flag = flag && tempFlag;
            } else {
                flag = tempFlag = false;
                LOGGER.error("The provided Cloud Type \"{}\" is not supported by the removeClouds endpoint",
                             cloud.getCloudType());
            }
            if (Boolean.TRUE.equals(tempFlag)) {
                List<Deployment> toBeRemovedDeployments = new ArrayList<>();
                if (cloud.getDeployments() != null && !cloud.getDeployments().isEmpty()) {
                    LOGGER.info("Cleaning all deployments related to cloud \"{}\"", cloud.getCloudId());
                    toBeRemovedDeployments.addAll(cloud.getDeployments());
                    for (Deployment deployment : toBeRemovedDeployments) {
                        repositoryService.deleteDeployment(deployment);
                    }
                }
                LOGGER.info("Cleaning node candidates");
                List<String> toBeRemovedClouds = Collections.singletonList(cloud.getCloudId());
                try {
                    long cleaned = nodeCandidateUtils.cleanNodeCandidates(toBeRemovedClouds);
                    LOGGER.info("Cleaning node candidates related to clouds {} ended properly with {} NC cleaned.",
                                toBeRemovedClouds.get(0),
                                cleaned);
                } catch (Exception e) {
                    LOGGER.warn("Cleaning node candidates for cloud {} returned an exception!",
                                toBeRemovedClouds.get(0),
                                e);
                }
                repositoryService.deletePACloud(cloud);
                LOGGER.info("Cloud removed.");
                repositoryService.flush();
            }
        }
        return flag;
    }

    /**
     * Remove an IAAS Cloud
     * @param sessionId A valid session id
     * @param cloud A PACloud Object for a cloud to be deleted
     * @param preempt If true undeploy node source immediately without waiting for nodes to be freed
     */
    public Boolean removeIaasCloudNS(String sessionId, PACloud cloud, Boolean preempt) {
        LOGGER.info("Removing {} cloud : {}", cloud.getCloudType(), cloud.toString());
        connectorIaasGateway.deleteInfrastructure(cloud.getDummyInfrastructureName());
        Boolean flag = true;
        for (Map.Entry<String, String> entry : cloud.getDeployedRegions().entrySet()) {
            try {
                String nodeSourceName = cloud.getNodeSourceNamePrefix() + entry.getKey();
                LOGGER.info("Removing IAAS node source \"{}\" from the ProActive server.", nodeSourceName);
                resourceManagerGateway.removeNodeSource(nodeSourceName, preempt);
            } catch (NotConnectedException | PermissionRestException | IllegalArgumentException e) {
                LOGGER.error("Removing cloud crashed. Error: ", e);
                flag = false;
            }
        }
        for (Map.Entry<String, String> entry : cloud.getDeployedWhiteListedRegions().entrySet()) {
            try {
                String nodeSourceName = PACloud.WHITE_LISTED_NAME_PREFIX + cloud.getNodeSourceNamePrefix() +
                                        entry.getKey();
                LOGGER.info("Removing white listed IAAS node source \"{}\" from the ProActive server.", nodeSourceName);
                resourceManagerGateway.removeNodeSource(nodeSourceName, preempt);
            } catch (NotConnectedException | PermissionRestException | IllegalArgumentException e) {
                LOGGER.error("Removing WL cloud crashed. Error: ", e);
                flag = false;
            }
        }
        return flag;
    }

    /**
     * Remove an BYON/EDGE Cloud
     * @param sessionId A valid session id
     * @param cloud A PACloud Object for a cloud to be deleted
     * @param preempt If true undeploy node source immediately without waiting for nodes to be freed
     */
    public Boolean removeByonCloudNS(String sessionId, PACloud cloud, Boolean preempt) {
        LOGGER.info("Removing {} cloud : {}", cloud.getCloudType(), cloud.toString());
        Boolean flag = true;
        try {
            String nodeSourceName = cloud.getCloudId();
            LOGGER.info("Removing {} node source \"{}\" from the ProActive server.",
                        cloud.getCloudType(),
                        nodeSourceName);
            resourceManagerGateway.removeNodeSource(nodeSourceName, preempt);
        } catch (NotConnectedException | PermissionRestException | IllegalArgumentException e) {
            LOGGER.error("Removing cloud crashed. Error: ", e);
            flag = false;
        }

        return flag;
    }

    /**
     * This function returns the list of all available images related to a registered cloud
     * @param sessionId A valid session id
     * @param cloudId A valid cloud identifier
     * @return A list of available images for a registered cloud
     */

    public List<Image> getCloudImages(String sessionId, String cloudId) throws NotConnectedException {
        List<Image> allImages = getAllCloudImages(sessionId);
        List<Image> filteredImages = new LinkedList<>();
        PACloud paCloud = repositoryService.getPACloud(cloudId);

        if (paCloud != null) {
            try {
                JSONArray imagesArray = connectorIaasGateway.getImages(paCloud.getDummyInfrastructureName());
                List<String> imagesIDs = IntStream.range(0, imagesArray.length())
                                                  .mapToObj(imagesArray::get)
                                                  .map(image -> cloudId + "/" + ((JSONObject) image).optString("id"))
                                                  .collect(Collectors.toList());
                LOGGER.debug("Filtering images related to cloud ID '{}'.", cloudId);
                allImages.stream().filter(image -> imagesIDs.contains(image.getId())).forEach(filteredImages::add);
            } catch (RuntimeException e) {
                LOGGER.error("Failed to get images for cloud {}: {}", cloudId, e.getMessage(), e);
                throw new InternalServerErrorException("Error while retrieving images for cloud: " + cloudId, e);
            }
        } else {
            LOGGER.warn("Cloud ID '{}' is not found in SAL DB.", cloudId);
            throw new InternalServerErrorException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                                           .entity("Cloud ID '" + cloudId + "' is not found in SAL DB.")
                                                           .build());
        }

        return filteredImages;
    }

    /**
     * This function returns the list of all available images
     * @param sessionId A valid session id
     * @return A list of all available images
     */
    public List<Image> getAllCloudImages(String sessionId) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        return repositoryService.listImages();
    }

    /**
     * This function returns the list of all available hardwares related to a registered cloud
     * @param sessionId A valid session id
     * @param cloudId A valid cloud identifier
     * @return A list of available hardwares
     */
    public List<Hardware> getCloudHardwares(String sessionId, String cloudId) throws NotConnectedException {
        LOGGER.warn("Feature not implemented yet. All hardwares will be returned.");
        return getAllCloudHardwares(sessionId);
    }

    /**
     * This function returns the list of all available hardwares
     * @param sessionId A valid session id
     * @return A list of all available hardwares
     */
    public List<Hardware> getAllCloudHardwares(String sessionId) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        List<Hardware> allHardwares = repositoryService.listHardwares();

        return allHardwares.stream()
                           .filter(hardware -> JCloudsInstancesUtils.isHandledHardwareInstanceType(repositoryService.findFirstNodeCandidateWithHardware(hardware)
                                                                                                                    .getCloud()
                                                                                                                    .getApi()
                                                                                                                    .getProviderName(),
                                                                                                   hardware.getName()) ||
                                               WhiteListedInstanceTypesUtils.isHandledHardwareInstanceType(hardware.getName()))
                           .collect(Collectors.toList());
    }

    /**
     * This function returns the list of all available locations related to a registered cloud
     * @param sessionId A valid session id
     * @param cloudId A valid cloud identifier
     * @return A list of available locations
     */
    public List<Location> getCloudLocations(String sessionId, String cloudId) throws NotConnectedException {
        LOGGER.warn("Feature not implemented yet. All locations will be returned.");
        return getAllCloudLocations(sessionId);
    }

    /**
     * This function returns the list of all available locations
     * @param sessionId A valid session id
     * @return A list of all available locations
     */
    public List<Location> getAllCloudLocations(String sessionId) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        return repositoryService.listLocations();
    }

    private Credentials hideCredentials(Credentials creds) {
        Credentials newCreds = new Credentials();
        if (creds != null) {
            if (creds.getPassword() != null) {
                newCreds.setPassword(hideString(creds.getPassword(), 2));
            }
            if (creds.getPrivateKey() != null) {
                newCreds.setPrivateKey(hideString(creds.getPrivateKey(), 2));
            }
            if (creds.getUserName() != null) {
                newCreds.setUserName(hideString(creds.getUserName(), 5));
            }
        }
        return newCreds;
    }

    private SSHCredentials hideSshCredentials(SSHCredentials creds) {
        SSHCredentials newCreds = new SSHCredentials();
        if (creds != null) {
            newCreds.setKeyPairName(creds.getKeyPairName());
            newCreds.setUsername(creds.getUsername());
            if (creds.getPrivateKey() != null) {
                newCreds.setPrivateKey(hideString(creds.getPrivateKey(), 3));
            }
        }
        return newCreds;
    }

    private String hideString(String cred, int expose) {
        String hiddenCreds = "";
        int i = 0;
        for (char c : cred.toCharArray()) {
            if (i >= expose) {
                hiddenCreds += "*";
            } else {
                hiddenCreds += c;
            }
            i++;
        }
        return hiddenCreds;
    }
}
