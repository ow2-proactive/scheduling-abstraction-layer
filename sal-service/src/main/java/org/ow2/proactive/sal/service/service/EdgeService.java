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
import org.ow2.proactive.sal.service.util.ByonUtils;
import org.ow2.proactive.sal.service.util.TemporaryFilesHelper;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.ow2.proactive.scheduler.common.job.JobId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;


@Log4j2
@Service("EdgeService")
public class EdgeService {

    @Autowired
    private PAGatewayService paGatewayService;

    @Autowired
    private PASchedulerGateway schedulerGateway;

    @Autowired
    private ServiceConfiguration serviceConfiguration;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * Register new Edge nodes passed as EdgeDefinition object
     *
     * @param sessionId A valid session id
     * @param edgeNodeDefinition objects of class ByonDefinition that contains the detials of the nodes to be registered.
     * @return newEdgeNode      EdgeNode object that contains information about the registered Node
     */
    public EdgeNode registerNewEdgeNode(String sessionId, EdgeDefinition edgeNodeDefinition)
            throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Validate.notNull(edgeNodeDefinition, "The received EDGE node definition is empty. Nothing to be registered.");
        LOGGER.info("registerNewEdgeNode endpoint is called, Registering a new EDGE definition ...");
        EdgeNode newEdgeNode = new EdgeNode();
        String jobId;
        if (edgeNodeDefinition.getJobId() == null || edgeNodeDefinition.getJobId().isEmpty()) {
            jobId = EdgeDefinition.ANY_JOB_ID;
        } else {
            jobId = edgeNodeDefinition.getJobId();
        }
        newEdgeNode.setName(edgeNodeDefinition.getName());
        newEdgeNode.setLoginCredential(edgeNodeDefinition.getLoginCredential());
        newEdgeNode.setIpAddresses(edgeNodeDefinition.getIpAddresses());
        newEdgeNode.setNodeProperties(edgeNodeDefinition.getNodeProperties());
        newEdgeNode.setJobId(jobId);
        newEdgeNode.setPort(edgeNodeDefinition.getPort());
        newEdgeNode.setSystemArch(edgeNodeDefinition.getSystemArch());
        newEdgeNode.setScriptURL(edgeNodeDefinition.getScriptURL());
        newEdgeNode.setJarURL(edgeNodeDefinition.getJarURL());

        NodeCandidate edgeNC = ByonUtils.createNodeCandidate(edgeNodeDefinition.getNodeProperties(),
                                                             jobId,
                                                             "edge",
                                                             newEdgeNode.getId(),
                                                             edgeNodeDefinition.getName());
        newEdgeNode.setNodeCandidate(edgeNC);

        repositoryService.saveEdgeNode(newEdgeNode);
        repositoryService.flush();
        LOGGER.info("EDGE node registered.");

        return newEdgeNode;
        /*
         * TODO:
         * Avoid duplicate nodes in the database
         */
    }

    /**
     * Return the List of registered EDGE nodes
     * @param sessionId A valid session id
     * @param jobId A constructed job identifier, If "0" is passed as the JobId all the Edge Nodes will be returned
     * @return List of EdgeNode objects that contains information about the registered Nodes
     */
    public List<EdgeNode> getEdgeNodes(String sessionId, String jobId) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        List<EdgeNode> filteredEdgeNodes = new LinkedList<>();
        List<EdgeNode> listEdgeNodes = repositoryService.listEdgeNodes();
        if (jobId.equals("0")) {
            return listEdgeNodes;
        } else {
            for (EdgeNode edgeNode : listEdgeNodes) {
                if (jobId.equals(edgeNode.getJobId())) {
                    filteredEdgeNodes.add(edgeNode);
                }
            }
            return filteredEdgeNodes;
        }
        /*
         * TODO:
         * Add Logging info
         */
    }

    /**
     * Adding EDGE nodes to a job component
     * @param sessionId A valid session id
     * @param edgeIdPerComponent a mapping between byon nodes and job components
     * @param jobId A constructed job identifier
     * @return  0 if nodes has been added properly. A greater than 0 value otherwise.
     */
    public Boolean addEdgeNodes(String sessionId, Map<String, String> edgeIdPerComponent, String jobId)
            throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Validate.notNull(edgeIdPerComponent,
                         "The received byonIdPerComponent structure is empty. Nothing to be added.");

        edgeIdPerComponent.forEach((edgeNodeId, nodeNameAndComponentName) -> {
            EdgeNode edgeNode = repositoryService.getEdgeNode(edgeNodeId);
            if (StringUtils.countMatches(nodeNameAndComponentName, "/") != 1) {
                LOGGER.error("Invalid nodeNameAndComponentName \"{}\"! the string should contain exactly 1 \"/\" char.",
                             nodeNameAndComponentName);
                throw new IllegalArgumentException(String.format("Invalid nodeNameAndComponentName \"%s\"! the string should contain exactly 1 \"/\" char.",
                                                                 nodeNameAndComponentName));
            }
            String nodeName = nodeNameAndComponentName.split("/")[0];
            String componentName = nodeNameAndComponentName.split("/")[1];
            LOGGER.info("Edge Node {} to be assigned to {}.", nodeName, componentName);
            Task task = repositoryService.getTask(jobId + componentName);

            if (edgeNode == null) {
                LOGGER.error("The passed edgeNodeId \"{}\"is not Found in the database", edgeNodeId);
                throw new IllegalArgumentException(String.format("The passed edgeNodeId \"%s\" is not Found in the database",
                                                                 edgeNodeId));
            }
            if (task == null) {
                LOGGER.error("The passed componentName \"{}\"is not Found in the database", componentName);
                throw new IllegalArgumentException(String.format("The passed componentName \"%s\" is not Found in the database",
                                                                 componentName));
            }

            Deployment newDeployment = new Deployment();
            newDeployment.setNodeName(nodeName);
            newDeployment.setDeploymentType(NodeType.EDGE);
            newDeployment.setEdgeNode(edgeNode);

            SSHCredentials sshCred = new SSHCredentials();
            sshCred.setUsername(edgeNode.getLoginCredential().getUsername());
            sshCred.setUsername(edgeNode.getLoginCredential().getPassword());
            sshCred.setUsername(edgeNode.getLoginCredential().getPrivateKey());

            PACloud cloud = new PACloud();
            String nodeSourceName = edgeNode.composeNodeSourceName();
            cloud.setCloudId(nodeSourceName);
            cloud.setNodeSourceNamePrefix(nodeSourceName);
            cloud.setCloudType(CloudType.EDGE);
            cloud.setCloudProvider(CloudProviderType.BYON);
            cloud.setSshCredentials(sshCred);
            cloud.addDeployment(newDeployment);
            newDeployment.setPaCloud(cloud);
            repositoryService.savePACloud(cloud);

            List<EdgeNode> edgeNodeList = new LinkedList<>();
            edgeNodeList.add(edgeNode);
            LOGGER.info("EDGE node Added: " + edgeNode.getName() + " Ip: " +
                        edgeNode.getIpAddresses().get(0).getValue());
            defineEdgeNodeSource(edgeNodeList);
            LOGGER.info("EDGE node source {} is defined.", nodeSourceName);

            newDeployment.setTask(task);
            newDeployment.setNumber(task.getNextDeploymentID());
            repositoryService.saveDeployment(newDeployment);
            LOGGER.debug("Deployment created: " + newDeployment.toString());

            task.addDeployment(newDeployment);
            repositoryService.saveTask(task);
        });

        repositoryService.flush();

        LOGGER.info("EDGE nodes added properly.");
        return true;
    }

    /**
     * Define an EDGE node source
     * @param edgeNodeList a list of EDGE nodes to be connected to the server.
     */
    private void defineEdgeNodeSource(List<EdgeNode> edgeNodeList) {
        Map<String, String> variables = new HashMap<>();
        String filename;
        String edgeIPs = "";
        // Prepare the ip addresses for all the nodes to be added
        for (EdgeNode edgeNode : edgeNodeList) {
            List<IpAddress> tempListIP = edgeNode.getIpAddresses();
            assert !tempListIP.isEmpty();
            edgeIPs = edgeIPs + tempListIP.get(0).getValue().replace(" ", "") + ",";
        }
        // Collect the pamr router address and port number
        try {
            URL endpointPa = (new URL(serviceConfiguration.getPaUrl()));
            variables.put("rm_host_name", endpointPa.getHost());
            variables.put("pa_port", "" + endpointPa.getPort());
        } catch (MalformedURLException e) {
            LOGGER.error(String.valueOf(e.getStackTrace()));
        }

        assert !edgeNodeList.isEmpty();
        EdgeNode edgeNode = edgeNodeList.get(0);
        filename = File.separator + "Define_NS_EDGE.xml";
        variables.put("NS_name", edgeNode.composeNodeSourceName());
        variables.put("pa_protocol", "http");
        variables.put("tokens", "EDGE_" + edgeNode.getJobId());
        variables.put("ssh_username", edgeNode.getLoginCredential().getUsername());
        variables.put("ssh_password", edgeNode.getLoginCredential().getPassword());
        /*
         * IMPORTANT: Later we should relay only on the ssh_key. For now all the nodes must have the
         * same login
         * credentials. later by automating the node deployment process we can add the server key
         * automatically.
         * TODO
         */
        variables.put("ssh_key", "");
        variables.put("ssh_port", edgeNode.getPort());
        variables.put("list_of_ips", edgeIPs);
        switch (edgeNode.getSystemArch()) {
            case "AMD":
                variables.put("deployment_mode", "useNodeJarStartupScript");
                variables.put("jar_url", edgeNode.getJarURL());
                variables.put("jre_url",
                              "https://ci-materials.s3.amazonaws.com/Latest_jre/jre-8u382b05-linux-x64.tar.gz");
                break;
            case "ARMv7":
                variables.put("deployment_mode", "useNodeJarStartupScript");
                variables.put("jar_url", edgeNode.getJarURL());
                variables.put("jre_url",
                              "https://ci-materials.s3.amazonaws.com/Latest_jre/jre-8u322b06-linux-arm.tar.gz");
                break;
            case "ARMv8":
                variables.put("deployment_mode", "useNodeJarStartupScript");
                variables.put("jar_url", edgeNode.getJarURL());
                variables.put("jre_url",
                              "https://ci-materials.s3.amazonaws.com/Latest_jre/jre-8u322b06-linux-aarch64.tar.gz");
                break;
            default:
                LOGGER.error("The Edge node system architecture {} is not supported!", edgeNode.getSystemArch());
                throw new IllegalArgumentException("The Edge node system architecture " + edgeNode.getSystemArch() +
                                                   " is not supported!");
        }

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
        LOGGER.info("Trying to deploy the NS: " + edgeNode.composeNodeSourceName());
        JobId jobId = schedulerGateway.submit(fXmlFile, variables);
        LOGGER.info("Job submitted with ID: " + jobId);
        TemporaryFilesHelper.delete(fXmlFile);
    }

    /**
     * Delete Edge nodes
     * @param sessionId A valid session id
     * @param edgeId the id of the node to be removed
     * @return  true if the deletion was done with no errors, false otherwise
     */
    public boolean deleteEdgeNode(String sessionId, String edgeId) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        EdgeNode edgeNode = repositoryService.getEdgeNode(edgeId);

        if (edgeNode == null) {
            LOGGER.error("The passed EDGE ID is not Found in the database");
            throw new IllegalArgumentException("The passed EDGE ID \"" + edgeId + "\" is not Found in the database");
        }

        handlePACloudDeletion(edgeNode);

        repositoryService.deleteEdgeNode(edgeNode);

        return true;
    }

    /**
     * Handle the deletion of a PACloud related to an EdgeNode
     * @param edgeNode The edge node whose related PACloud is to be deleted
     */
    public void handlePACloudDeletion(EdgeNode edgeNode) {
        LOGGER.info("Deleting the corresponding Edge Node PACloud from the database ...");
        PACloud paCloud = repositoryService.getPACloud(edgeNode.composeNodeSourceName());
        if (paCloud != null) {
            if (paCloud.getDeployments() != null) {
                LOGGER.info("Cleaning Edge Node deployments from related tasks {}",
                            paCloud.getDeployments().toString());
                paCloud.getDeployments().forEach(deployment -> deployment.getTask().removeDeployment(deployment));
                LOGGER.info("Cleaning Edge Node deployments from paCloud {}", paCloud.getCloudId());
                paCloud.clearDeployments();
            }
            repositoryService.deletePACloud(paCloud);
        } else {
            LOGGER.info("The PACloud related to the Edge Node  {} is not found.", edgeNode.getName());
        }

        if (Boolean.FALSE.equals(ByonUtils.undeployNs(edgeNode.composeNodeSourceName(), false, true))) {
            LOGGER.warn("The Edge node source undeploy finished with errors!");
        }
    }

}
