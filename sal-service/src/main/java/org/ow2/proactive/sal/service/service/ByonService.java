/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.ow2.proactive.sal.model.*;
import org.ow2.proactive.sal.service.service.application.PASchedulerGateway;
import org.ow2.proactive.sal.service.util.ByonAgentAutomation;
import org.ow2.proactive.sal.service.util.ByonUtils;
import org.ow2.proactive.sal.service.util.TemporaryFilesHelper;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.ow2.proactive.scheduler.common.job.JobId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;


@Log4j2
@Service("ByonService")
public class ByonService {

    @Autowired
    private PAGatewayService paGatewayService;

    @Autowired
    private PASchedulerGateway schedulerGateway;

    @Autowired
    private ServiceConfiguration serviceConfiguration;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * Register new BYON nodes passed as ByonDefinition object
     *
     * @param sessionId A valid session id
     * @param byonNodeDefinition objects of class ByonDefinition that contains the details of the nodes to be registered.
     * @param jobId              A constructed job identifier
     * @param automate           the Byon agent will be deployed automatically if the value is set to True
     * @return newByonNode      ByonNode object that contains information about the registered Node
     */
    public ByonNode registerNewByonNode(String sessionId, ByonDefinition byonNodeDefinition, String jobId,
            Boolean automate) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Validate.notNull(byonNodeDefinition, "The received Byon node definition is empty. Nothing to be registered.");
        Validate.notNull(jobId, "The received jobId is empty. Nothing to be registered.");
        LOGGER.info("registerNewByonNode endpoint is called with Automate set to " + automate +
                    ", Registering a new BYON definition related to job " + jobId + " ...");
        ByonNode newByonNode = new ByonNode();
        newByonNode.setName(byonNodeDefinition.getName());
        newByonNode.setLoginCredential(byonNodeDefinition.getLoginCredential());
        newByonNode.setIpAddresses(byonNodeDefinition.getIpAddresses());
        newByonNode.setNodeProperties(byonNodeDefinition.getNodeProperties());
        newByonNode.setJobId(jobId);

        NodeCandidate byonNC = ByonUtils.createNodeCandidate(byonNodeDefinition.getNodeProperties(),
                                                             jobId,
                                                             "byon",
                                                             newByonNode.getId(),
                                                             byonNodeDefinition.getName());
        newByonNode.setNodeCandidate(byonNC);

        repositoryService.saveByonNode(newByonNode);
        repositoryService.flush();
        LOGGER.info("BYON node registered.");
        if (automate) {
            ByonAgentAutomation byonAA = new ByonAgentAutomation(newByonNode);
            byonAA.prepareByonNode();
        }
        return newByonNode;
        /*
         * TODO:
         * Avoid duplicate nodes in the database
         */
    }

    /**
     * Return the List of registered BYON nodes
     * @param sessionId A valid session id
     * @param jobId A constructed job identifier, If "0" is passed as the JobId all the Byon Nodes will be returned
     * @return List of ByonNode objects that contains information about the registered Nodes
     */
    public List<ByonNode> getByonNodes(String sessionId, String jobId) throws NotConnectedException {
        if (jobId.equals("0")) {
            LOGGER.info("getByonNodes endpoint is called for all Byon Nodes");
        } else {
            LOGGER.info("getByonNodes endpoint is called for job " + jobId);
        }
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        List<ByonNode> filteredByonNodes = new LinkedList<>();
        List<ByonNode> listByonNodes = repositoryService.listByonNodes();
        if (jobId.equals("0")) {
            return listByonNodes;
        } else {
            for (ByonNode byonNode : listByonNodes) {
                if (jobId.equals(byonNode.getJobId())) {
                    filteredByonNodes.add(byonNode);
                }
            }
            return filteredByonNodes;
        }
        /*
         * TODO:
         * Add Logging info
         */
    }

    /**
     * Adding BYON nodes to a job component
     * @param sessionId A valid session id
     * @param byonIdPerComponent a mapping between byon nodes and job components
     * @param jobId A constructed job identifier
     * @return  0 if nodes has been added properly. A greater than 0 value otherwise.
     */
    public Boolean addByonNodes(String sessionId, Map<String, String> byonIdPerComponent, String jobId)
            throws NotConnectedException {
        LOGGER.info("addByonNodes endpoint is called for job {}.", jobId);
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Validate.notNull(byonIdPerComponent,
                         "The received byonIdPerComponent structure is empty. Nothing to be added.");
        byonIdPerComponent.forEach((byonNodeId, nodeNameAndComponentName) -> {
            ByonNode byonNode = repositoryService.getByonNode(byonNodeId);
            if (StringUtils.countMatches(nodeNameAndComponentName, "/") != 1) {
                LOGGER.error("Invalid nodeNameAndComponentName \"{}\"! the string should contain exactly 1 \"/\" char.",
                             nodeNameAndComponentName);
                throw new IllegalArgumentException(String.format("Invalid nodeNameAndComponentName \"%s\"! the string should contain exactly 1 \"/\" char.",
                                                                 nodeNameAndComponentName));
            }
            String nodeName = nodeNameAndComponentName.split("/")[0];
            String componentName = nodeNameAndComponentName.split("/")[1];
            LOGGER.info("Byon Node {} to be assigned to {}.", nodeName, componentName);
            Task task = repositoryService.getTask(jobId + componentName);
            if (byonNode == null) {
                LOGGER.error("The passed byonNodeId \"{}\"is not Found in the database", byonNodeId);
                throw new IllegalArgumentException(String.format("The passed byonNodeId \"%s\" is not Found in the database",
                                                                 byonNodeId));
            }
            if (task == null) {
                LOGGER.error("The passed componentName \"{}\"is not Found in the database", componentName);
                throw new IllegalArgumentException(String.format("The passed componentName \"%s\" is not Found in the database",
                                                                 componentName));
            }

            Deployment newDeployment = new Deployment();
            newDeployment.setNodeName(nodeName);
            newDeployment.setDeploymentType(NodeType.BYON);
            newDeployment.setByonNode(byonNode);
            SSHCredentials sshCred = new SSHCredentials();
            sshCred.setUsername(byonNode.getLoginCredential().getUsername());
            sshCred.setUsername(byonNode.getLoginCredential().getPassword());
            sshCred.setUsername(byonNode.getLoginCredential().getPrivateKey());

            PACloud cloud = new PACloud();
            String nodeSourceName = byonNode.composeNodeSourceName();
            cloud.setCloudId(nodeSourceName);
            cloud.setNodeSourceNamePrefix(nodeSourceName);
            cloud.setCloudType(CloudType.BYON);
            cloud.setCloudProvider(CloudProviderType.BYON);
            cloud.setSshCredentials(sshCred);
            cloud.addDeployment(newDeployment);
            newDeployment.setPaCloud(cloud);
            repositoryService.savePACloud(cloud);

            List<ByonNode> byonNodeList = new LinkedList<>();
            byonNodeList.add(byonNode);
            LOGGER.info("BYON node Added: " + byonNode.getName() + " Ip: " +
                        byonNode.getIpAddresses().get(0).getValue());
            defineByonNodeSource(byonNodeList);
            LOGGER.info("BYON node source {} is defined.", byonNode.composeNodeSourceName());

            newDeployment.setTask(task);
            newDeployment.setNumber(task.getNextDeploymentID());
            repositoryService.saveDeployment(newDeployment);
            LOGGER.debug("Deployment created: " + newDeployment.toString());

            task.addDeployment(newDeployment);
            repositoryService.saveTask(task);
        });

        repositoryService.flush();

        LOGGER.info("BYON nodes added properly.");
        return true;
    }

    /**
     * Define a BYON node source
     * @param byonNodeList a list of BYON nodes to be connected to the server.
     */
    private void defineByonNodeSource(List<ByonNode> byonNodeList) {
        Map<String, String> variables = new HashMap<>();
        String filename;
        String byonIPs = "";
        // Prepare the ip addresses for all the nodes to be added
        for (ByonNode byonNode : byonNodeList) {
            List<IpAddress> tempListIP = byonNode.getIpAddresses();
            assert !tempListIP.isEmpty();
            byonIPs = byonIPs + tempListIP.get(0).getValue().replace(" ", "") + ",";
        }
        // Collect the pamr router address and port number
        try {
            URL endpointPa = (new URL(serviceConfiguration.getPaUrl()));
            variables.put("rm_host_name", endpointPa.getHost());
            variables.put("pa_port", "" + endpointPa.getPort());
        } catch (MalformedURLException e) {
            LOGGER.error(String.valueOf(e.getStackTrace()));
        }
        /*
         * IMPORTANT: we consider that all of the byon nodes that belong to the same nodesource are
         * going to have the
         * same info and as a result we will get all the nodesource info from one node, later this
         * should be improved.
         * TODO
         */
        assert !byonNodeList.isEmpty();
        ByonNode byonNode = byonNodeList.get(0);
        filename = File.separator + "Define_NS_BYON.xml";
        variables.put("NS_name", byonNode.composeNodeSourceName());
        variables.put("pa_protocol", "http");
        variables.put("tokens", "BYON_" + byonNode.getJobId());
        variables.put("ssh_username", byonNode.getLoginCredential().getUsername());
        variables.put("ssh_password", byonNode.getLoginCredential().getPassword());
        /*
         * IMPORTANT: Later we should relay only on the ssh_key. For now all the nodes must have the
         * same login
         * credentials. later by automating the node deployment process we can add the server key
         * automatically.
         * TODO
         */
        variables.put("ssh_key", "");
        variables.put("ssh_port", "22");
        variables.put("list_of_ips", byonIPs);
        // Create the xml file
        File fXmlFile = null;
        LOGGER.info("NodeSource deployment workflow filename: " + filename);
        try {
            fXmlFile = TemporaryFilesHelper.createTempFileFromResource(filename);
        } catch (IOException ioe) {
            LOGGER.error("Opening the NS deployment workflow file failed due to : " +
                         Arrays.toString(ioe.getStackTrace()));
        }
        assert fXmlFile != null;
        LOGGER.info("Submitting the file: " + fXmlFile.toString());
        LOGGER.info("Trying to deploy the NS: " + byonNode.composeNodeSourceName());
        JobId jobId = schedulerGateway.submit(fXmlFile, variables);
        LOGGER.info("Job submitted with ID: " + jobId);
        TemporaryFilesHelper.delete(fXmlFile);
    }

    /**
     * Remove Byon nodes
     * @param sessionId A valid session id
     * @param byonId the id of the node to be removed
     * @return  true if the deletion was done with no errors, false otherwise
     */
    public Boolean deleteByonNode(String sessionId, String byonId) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        ByonNode byonNode = repositoryService.getByonNode(byonId);

        if (byonNode == null) {
            LOGGER.error("The passed BYON ID is not Found in the database");
            throw new IllegalArgumentException("The passed BYON ID \"" + byonId + "\" is not Found in the database");
        }

        LOGGER.info("Deleting the corresponding PACloud from the database ...");
        PACloud paCloud = repositoryService.getPACloud(byonNode.composeNodeSourceName());
        if (paCloud != null) {
            if (paCloud.getDeployments() != null) {
                LOGGER.info("Cleaning deployments from related tasks {}", paCloud.getDeployments().toString());
                paCloud.getDeployments().forEach(deployment -> deployment.getTask().removeDeployment(deployment));
                LOGGER.info("Cleaning deployments from paCloud {}", paCloud.getCloudId());
                paCloud.clearDeployments();
            }
            repositoryService.deletePACloud(paCloud);
        } else {
            LOGGER.warn("The PACloud related to the byonNode {} is not found.", byonNode.getName());
        }

        if (Boolean.FALSE.equals(ByonUtils.undeployNs(byonNode.composeNodeSourceName(), false, true))) {
            LOGGER.warn("The BYON node source undeploy finished with errors!");
        }
        repositoryService.deleteByonNode(byonNode);

        return true;

        /*
         * TODO:
         * change the hardcoding for preempt and remove variables
         */}
}
