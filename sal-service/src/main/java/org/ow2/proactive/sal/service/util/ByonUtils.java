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
package org.ow2.proactive.sal.service.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomStringUtils;
import org.ow2.proactive.sal.model.*;
import org.ow2.proactive.sal.service.service.RepositoryService;
import org.ow2.proactive.sal.service.service.infrastructure.PAResourceManagerGateway;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.ow2.proactive_grid_cloud_portal.scheduler.exception.PermissionRestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;


@Log4j2
@Component
public class ByonUtils {

    private static PAResourceManagerGateway resourceManagerGateway;

    private static RepositoryService repositoryService;

    @Autowired
    private PAResourceManagerGateway tempResourceManagerGateway;

    @Autowired
    private RepositoryService tempRepositoryService;

    private ByonUtils() {
    }

    @PostConstruct
    private void initStaticAttributes() {
        resourceManagerGateway = this.tempResourceManagerGateway;
        repositoryService = this.tempRepositoryService;
    }

    static final int MAX_CONNECTION_RETRIES = 10;

    static final int INTERVAL = 20000;

    /**
     * @param np an Object of class NodeProperties that contains all the nodes properties needed for the candidate declaration
     * @param jobId a String identifier of the node candidate job
     * @param nodeType a String of the node type (byon or edge)
     * @return an object of class NodeCandidate
     */
    public static NodeCandidate createNodeCandidate(NodeProperties np, String jobId, String nodeType, String nodeId) {
        LOGGER.debug("Creating the {} node candidate ...", nodeType.toUpperCase());
        //Start by setting the universal nodes properties
        NodeCandidate nc = new NodeCandidate();
        nc.setPrice(0.0);
        nc.setMemoryPrice(0.0);
        nc.setPricePerInvocation(0.0);
        nc.setNodeId(nodeId);

        //create a dummy cloud definition for BYON nodes
        Cloud dummyCloud = ByonUtils.getOrCreateDummyCloud(nodeType);
        //Create a dummy image
        Image image = new Image();
        image.setOperatingSystem(np.getOperatingSystem());
        //Define the hardware
        Hardware hardware = new Hardware();
        hardware.setCores(np.getNumberOfCores());
        hardware.setDisk((double) np.getDisk());
        hardware.setRam(np.getMemory());
        //Define the location
        Location location = new Location();
        location.setGeoLocation(np.getGeoLocation());

        //Define the properties that depend on the node type

        if (nodeType.equals("byon")) {
            String bid = RandomStringUtils.randomAlphanumeric(16);
            //set the image name
            image.setId("byon-image-" + bid);
            //set the image Name
            image.setName("byon-image-name-" + np.getOperatingSystem().getOperatingSystemFamily() + "-" +
                          np.getOperatingSystem().getOperatingSystemArchitecture());
            //set the hardware
            hardware.setId("byon-hardware-" + bid);
            //set the location
            location.setId("byon-location-" + bid);
            //set the nc parameters
            nc.setNodeCandidateType(NodeCandidate.NodeCandidateTypeEnum.BYON);
            // set the nc jobIdForBYON
            nc.setJobIdForBYON(jobId);
            // set the nc jobIdForEDGE
            nc.setJobIdForEDGE(null);
        } else { //the node type is EDGE
            String eid = RandomStringUtils.randomAlphanumeric(16);
            //set the image id
            image.setId("edge-image-" + eid);
            //set the image Name
            image.setName("edge-image-name-" + np.getOperatingSystem().getOperatingSystemFamily() + "-" +
                          np.getOperatingSystem().getOperatingSystemArchitecture());
            //set the hardware
            hardware.setId("edge-hardware-" + eid);
            //set the location
            location.setId("edge-location-" + eid);
            //set the nc parameters
            nc.setNodeCandidateType(NodeCandidate.NodeCandidateTypeEnum.EDGE);
            // set the nc jobIdForBYON
            nc.setJobIdForBYON(null);
            // set the nc jobIdForEDGE
            nc.setJobIdForEDGE(jobId);
        }

        nc.setCloud(dummyCloud);
        repositoryService.saveImage(image);
        nc.setImage(image);
        repositoryService.saveHardware(hardware);
        nc.setHardware(hardware);
        repositoryService.saveLocation(location);
        nc.setLocation(location);
        repositoryService.saveNodeCandidate(nc);
        LOGGER.info("{} node candidate created.", nodeType.toUpperCase());
        return nc;
    }

    /**
     * Create a dummy object of class Cloud to be used for the node candidates
     * @return the created byonCloud object
     */
    public static Cloud getOrCreateDummyCloud(String nodeType) {
        LOGGER.debug("Searching for the dummy cloud ...");
        //Check if the Byon cloud already exists
        Optional<Cloud> optCloud = Optional.ofNullable(repositoryService.getCloud(nodeType));
        if (optCloud.isPresent()) {
            LOGGER.info("Dummy cloud for {} was found!", nodeType);
            return optCloud.get();
        }

        LOGGER.debug("Creating the dummy cloud for {} Nodes ...", nodeType);
        //else, Byon cloud will be created
        Cloud newCloud = new Cloud();
        newCloud.setCloudType((nodeType.equals("byon")) ? CloudType.BYON : CloudType.EDGE);
        newCloud.setOwner((nodeType.equals("byon")) ? "BYON" : "EDGE");
        newCloud.setId(nodeType);

        //Add the Byon cloud to the database
        repositoryService.saveCloud(newCloud);
        LOGGER.info("Dummy {} cloud created.", nodeType.toUpperCase());
        return newCloud;

        /*
         * TODO :
         * Check if we have to add other variables to the new cloud
         */
    }

    /**
     * @param nsName A valid Node Source name
     * @return The BYON Host Name
     */
    public static String getBYONHostname(String nsName) {
        LOGGER.info("Getting the byon node host name for: " + nsName);
        List<String> nodeSourcesNames = new LinkedList<>();
        List<String> nodeHostNames = new LinkedList<>();
        List<String> nodeStates = new LinkedList<>();
        int retries = 0;
        while (retries < MAX_CONNECTION_RETRIES) {
            retries++;
            //Check if the node source exist
            nodeSourcesNames = resourceManagerGateway.getDeployedNodeSourcesNames();
            if (!nodeSourcesNames.contains(nsName)) {
                LOGGER.warn("The node source " + nsName + " is not deployed");
            } else {
                LOGGER.info("Found the node source");
                //check if the node source have nodes
                nodeHostNames = resourceManagerGateway.getNodeHostNames(nsName);
                if (nodeHostNames == null) {
                    LOGGER.warn("The node Source " + nsName + " Does not have any nodes");
                } else {
                    LOGGER.info("List of node names is returned successfully");
                    //check the number of nodes
                    if (nodeHostNames.size() != 1) {
                        if (nodeHostNames.size() == 0) {
                            LOGGER.warn("The node Source " + nsName + " Does not have any nodes");
                        } else {
                            LOGGER.error("The node Source " + nsName + " has more than one node");
                            throw new IllegalStateException("Node source has multiple nodes");
                        }
                    } else {
                        LOGGER.info("One node name was returned");
                        //check if the host name is not empty
                        if (nodeHostNames.get(0).equals("")) {
                            nodeStates = resourceManagerGateway.getNodeStates(nsName);
                            LOGGER.warn("The node is in " + nodeStates.get(0) +
                                        " state => host name is empty, retrying to get node information");
                        } else {
                            return nodeHostNames.get(0);
                        }
                    }
                }
            }
            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
                LOGGER.error("The sleep thread was interrupted");
            }
        }
        LOGGER.error("The node host name is not retrieved after " + retries + " retries");
        throw new IllegalStateException("Node hostname is empty");

        /*
         * TODO
         * change the get getNodeStates and getNodeHostNames to getNodeEvents
         * to limit the number of connections to the RM
         * nodeStates.get(0) may lead to IndexOutOfRangeException => to be changed
         */
    }

    /**
     * Undeploy or remove the node source of a BYON node
     * @param byonNode an object of class ByonNode to be undeployed or removed.
     * @param preempt If true undeploy or remove node source immediately without waiting for nodes to be freed
     * @param remove If true completely remove the node source, if false only undeply the node source
     * @return  true if the resourceManagerGateway return no errors, false otherwise
     */
    public static Boolean undeployByonNs(ByonNode byonNode, Boolean preempt, Boolean remove) {
        assert byonNode != null : "A null value was passed for byonNode, A node source must have a BYON ID";
        String nodeSourceName = "BYON_NS_" + byonNode.getId();
        if (remove) {
            try {
                LOGGER.info("Removing BYON node source " + nodeSourceName + " from the ProActive server");
                if (resourceManagerGateway.getNodeSourceNames("all").contains(nodeSourceName)) {
                    resourceManagerGateway.removeNodeSource(nodeSourceName, preempt);
                } else {
                    LOGGER.warn("The node source \"" + nodeSourceName + "\" does not exist in the RM");
                }
            } catch (NotConnectedException | PermissionRestException e) {
                LOGGER.error(Arrays.toString(e.getStackTrace()));
                return false;
            }
        } else {
            try {
                LOGGER.info("Undeploying BYON node source " + nodeSourceName + " from the ProActive server");
                if (resourceManagerGateway.getNodeSourceNames("deployed").contains(nodeSourceName)) {
                    resourceManagerGateway.undeployNodeSource(nodeSourceName, preempt);
                } else {
                    LOGGER.warn("The node source \"" + nodeSourceName + "\" is not deployed in the RM");
                }
            } catch (NotConnectedException | PermissionRestException e) {
                LOGGER.error(Arrays.toString(e.getStackTrace()));
                return false;
            }
        }
        LOGGER.info("BYON node source was removed with no errors");
        return true;
    }
}
