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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.Validate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ow2.proactive.sal.service.model.*;
import org.ow2.proactive.sal.service.nc.UpdatingNodeCandidatesThread;
import org.ow2.proactive.sal.service.nc.WhiteListedInstanceTypesUtils;
import org.ow2.proactive.sal.service.service.application.PAConnectorIaasGateway;
import org.ow2.proactive.sal.service.service.infrastructure.PAResourceManagerGateway;
import org.ow2.proactive.sal.service.util.JCloudsInstancesUtils;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.ow2.proactive_grid_cloud_portal.scheduler.exception.PermissionRestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service("CloudService")
public class CloudService {

    @Autowired
    private PAGatewayService paGatewayService;

    @Autowired
    private PAConnectorIaasGateway connectorIaasGateway;

    @Autowired
    private PAResourceManagerGateway resourceManagerGateway;

    @Autowired
    private ServiceConfiguration serviceConfiguration;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * Add clouds to the ProActive Resource Manager
     * @param sessionId A valid session id
     * @param clouds A list of clouds information in JSONObject format
     * @return 0 if clouds has been added properly. A greater than 0 value otherwise.
     */
    public Integer addClouds(String sessionId, List<JSONObject> clouds) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Validate.notNull(clouds, "The received clouds structure is empty. Nothing to be created.");

        List<String> savedCloudIds = new LinkedList<>();
        clouds.forEach(cloud -> {
            PACloud newCloud = new PACloud();
            String nodeSourceNamePrefix = cloud.optString("cloudProviderName") + cloud.optString("cloudID");
            newCloud.setNodeSourceNamePrefix(nodeSourceNamePrefix);
            newCloud.setCloudID(cloud.optString("cloudID"));
            newCloud.setCloudProviderName(cloud.optString("cloudProviderName"));
            newCloud.setCloudType(CloudType.fromValue(cloud.optString("cloudType")));
            newCloud.setDeployedRegions(new HashMap<>());
            newCloud.setDeployedWhiteListedRegions(new HashMap<>());
            newCloud.setSubnet(cloud.optString("subnet"));
            newCloud.setSecurityGroup(cloud.optString("securityGroup"));
            newCloud.setEndpoint(cloud.optString("endpoint"));
            newCloud.setScopePrefix(cloud.optJSONObject("scope").optString("prefix"));
            newCloud.setScopeValue(cloud.optJSONObject("scope").optString("value"));
            newCloud.setIdentityVersion(cloud.optString("identityVersion"));
            newCloud.setDefaultNetwork(cloud.optString("defaultNetwork"));
            newCloud.setBlacklist(cloud.optString("blacklist"));

            SSHCredentials sshCredentials = new SSHCredentials();
            sshCredentials.setUsername(cloud.optJSONObject("sshCredentials").optString("username"));
            sshCredentials.setKeyPairName(cloud.optJSONObject("sshCredentials").optString("keyPairName"));
            sshCredentials.setPrivateKey(cloud.optJSONObject("sshCredentials").optString("privateKey"));
            newCloud.setSshCredentials(sshCredentials);

            Credentials credentials = new Credentials();
            credentials.setUserName(cloud.optJSONObject("credentials").optString("user"));
            credentials.setPrivateKey(cloud.optJSONObject("credentials").optString("secret"));
            credentials.setDomain(cloud.optJSONObject("credentials").optString("domain"));
            repositoryService.updateCredentials(credentials);
            newCloud.setCredentials(credentials);

            String dummyInfraName = "iamadummy" + newCloud.getCloudProviderName();
            connectorIaasGateway.defineInfrastructure(dummyInfraName, newCloud, "");
            newCloud.setDummyInfrastructureName(dummyInfraName);

            repositoryService.updatePACloud(newCloud);
            LOGGER.debug("Cloud created: " + newCloud.toString());
            savedCloudIds.add(newCloud.getCloudID());
        });

        repositoryService.flush();

        LOGGER.info("Clouds created properly.");

        updateNodeCandidatesAsync(savedCloudIds);

        return 0;
    }

    private void updateNodeCandidatesAsync(List<String> newCloudIds) {
        UpdatingNodeCandidatesThread updatingThread = new UpdatingNodeCandidatesThread(serviceConfiguration.getPaUrl(),
                                                                                       newCloudIds);
        updatingThread.start();
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
        return repositoryService.listPACloud();
    }

    /**
     * Undeploy clouds
     * @param sessionId A valid session id
     * @param cloudIDs List of cloud IDs to remove
     * @param preempt If true undeploy node source immediately without waiting for nodes to be freed
     */
    public Boolean undeployClouds(String sessionId, List<String> cloudIDs, Boolean preempt)
            throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        cloudIDs.forEach(cloudID -> {
            PACloud cloud = repositoryService.getPACloud(cloudID);
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
     * @param cloudIDs List of cloud IDs to remove
     * @param preempt If true undeploy node source immediately without waiting for nodes to be freed
     */
    public Boolean removeClouds(String sessionId, List<String> cloudIDs, Boolean preempt) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        cloudIDs.forEach(cloudID -> {
            PACloud cloud = repositoryService.getPACloud(cloudID);
            if (cloud == null) {
                LOGGER.info("Cloud {} not found, nothing to be removed.", cloudID);
                return;
            }
            LOGGER.info("Removing cloud : " + cloud.toString());
            connectorIaasGateway.deleteInfrastructure(cloud.getDummyInfrastructureName());
            for (Map.Entry<String, String> entry : cloud.getDeployedRegions().entrySet()) {
                try {
                    String nodeSourceName = cloud.getNodeSourceNamePrefix() + entry.getKey();
                    LOGGER.info("Removing node source " + nodeSourceName + " from the ProActive server.");
                    resourceManagerGateway.removeNodeSource(nodeSourceName, preempt);
                } catch (NotConnectedException | PermissionRestException | IllegalArgumentException e) {
                    LOGGER.error("Removing cloud crashed. Error: ", e);
                }
            }
            for (Map.Entry<String, String> entry : cloud.getDeployedWhiteListedRegions().entrySet()) {
                try {
                    String nodeSourceName = PACloud.WHITE_LISTED_NAME_PREFIX + cloud.getNodeSourceNamePrefix() +
                                            entry.getKey();
                    LOGGER.info("Removing node source " + nodeSourceName + " from the ProActive server.");
                    resourceManagerGateway.removeNodeSource(nodeSourceName, preempt);
                } catch (NotConnectedException | PermissionRestException | IllegalArgumentException e) {
                    LOGGER.error("Removing WL cloud crashed. Error: ", e);
                }
            }
            if (cloud.getDeployments() != null) {
                LOGGER.info("Cleaning deployments from related tasks " + cloud.getDeployments().toString());
                cloud.getDeployments().forEach(deployment -> deployment.getTask().removeDeployment(deployment));
            }
            LOGGER.info("Cleaning deployments from the cloud entry");
            cloud.clearDeployments();
            repositoryService.deletePACloud(cloud);
            LOGGER.info("Cloud removed.");
        });
        repositoryService.flush();
        return true;
    }

    /**
     * This function returns the list of all available images related to a registered cloud
     * @param sessionId A valid session id
     * @param cloudID A valid cloud identifier
     * @return A list of available images
     */
    public List<Image> getCloudImages(String sessionId, String cloudID) throws NotConnectedException {
        List<Image> allImages = getAllCloudImages(sessionId);
        List<Image> filteredImages = new LinkedList<>();
        PACloud paCloud = repositoryService.getPACloud(cloudID);
        if (paCloud != null) {
            JSONArray imagesArray = connectorIaasGateway.getImages(paCloud.getDummyInfrastructureName());
            List<String> imagesIDs = IntStream.range(0, imagesArray.length())
                                              .mapToObj(imagesArray::get)
                                              .map(image -> cloudID + "/" + ((JSONObject) image).optString("id"))
                                              .collect(Collectors.toList());
            LOGGER.debug("Filtering images related to cloud ID \'" + cloudID + "\'.");
            allImages.stream().filter(blaTest -> imagesIDs.contains(blaTest.getId())).forEach(filteredImages::add);
            return filteredImages;
        } else {
            LOGGER.warn("Cloud ID \'" + cloudID + "\' is not found in DB. getAllCloudImages() will return all images.");
            return allImages;
        }
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
     * @param cloudID A valid cloud identifier
     * @return A list of available hardwares
     */
    public List<Hardware> getCloudHardwares(String sessionId, String cloudID) throws NotConnectedException {
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
     * @param cloudID A valid cloud identifier
     * @return A list of available locations
     */
    public List<Location> getCloudLocations(String sessionId, String cloudID) throws NotConnectedException {
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
}
