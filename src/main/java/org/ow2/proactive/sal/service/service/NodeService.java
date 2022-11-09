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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.apache.commons.lang3.Validate;
import org.ow2.proactive.sal.service.model.*;
import org.ow2.proactive.sal.service.nc.WhiteListedInstanceTypesUtils;
import org.ow2.proactive.sal.service.service.infrastructure.PAResourceManagerGateway;
import org.ow2.proactive.sal.service.service.infrastructure.PASchedulerGateway;
import org.ow2.proactive.sal.service.util.TemporaryFilesHelper;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.ow2.proactive.scheduler.common.job.JobId;
import org.ow2.proactive_grid_cloud_portal.scheduler.exception.RestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;


@Log4j2
@Service("NodeService")
public class NodeService {

    @Autowired
    private PAGatewayService paGatewayService;

    @Autowired
    private PAResourceManagerGateway resourceManagerGateway;

    @Autowired
    private PASchedulerGateway schedulerGateway;

    @Autowired
    private ServiceConfiguration serviceConfiguration;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * Add nodes to the tasks of a defined job
     * @param sessionId A valid session id
     * @param nodes A list of IaasDefinition instances
     * @param jobId A constructed job identifier
     * @return 0 if nodes has been added properly. A greater than 0 value otherwise.
     */
    public synchronized Boolean addNodes(String sessionId, List<IaasDefinition> nodes, String jobId)
            throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Validate.notNull(nodes, "The received nodes structure is empty. Nothing to be created.");

        nodes.forEach(node -> {
            Deployment newDeployment = new Deployment();
            newDeployment.setNodeName(node.getName());
            newDeployment.setDeploymentType(NodeType.IAAS);

            LOGGER.info("Trying to retrieve node candidate and related iaas node: " + node.getNodeCandidateId());
            NodeCandidate nodeCandidate = repositoryService.getNodeCandidate(node.getNodeCandidateId());
            if (nodeCandidate == null) {
                LOGGER.error("Node candidate [{}] does not exist.", node.getNodeCandidateId());
                throw new IllegalArgumentException(String.format("NodeCandidateID [%s] not valid.",
                                                                 node.getNodeCandidateId()));
            }
            IaasNode iaasNode = repositoryService.getIaasNode(nodeCandidate.getNodeId());

            newDeployment.setIaasNode(iaasNode);
            iaasNode.incDeployedNodes(1L);
            repositoryService.updateIaasNode(iaasNode);

            PACloud cloud = repositoryService.getPACloud(node.getCloudID());
            cloud.addDeployment(newDeployment);
            if (WhiteListedInstanceTypesUtils.isHandledHardwareInstanceType(newDeployment.getNode()
                                                                                         .getNodeCandidate()
                                                                                         .getHardware()
                                                                                         .getProviderId())) {
                if (!cloud.isWhiteListedRegionDeployed(newDeployment.getNode()
                                                                    .getNodeCandidate()
                                                                    .getLocation()
                                                                    .getName())) {
                    String nodeSourceName = PACloud.WHITE_LISTED_NAME_PREFIX + cloud.getNodeSourceNamePrefix() +
                                            newDeployment.getNode().getNodeCandidate().getLocation().getName();
                    this.defineNSWithDeploymentInfo(nodeSourceName, cloud, newDeployment);
                    cloud.addWhiteListedDeployedRegion(newDeployment.getNode()
                                                                    .getNodeCandidate()
                                                                    .getLocation()
                                                                    .getName(),
                                                       newDeployment.getNode()
                                                                    .getNodeCandidate()
                                                                    .getImage()
                                                                    .getProviderId());
                }
            } else {
                if (!cloud.isRegionDeployed(newDeployment.getNode().getNodeCandidate().getLocation().getName())) {
                    String nodeSourceName = cloud.getNodeSourceNamePrefix() +
                                            newDeployment.getNode().getNodeCandidate().getLocation().getName();
                    this.defineNSWithDeploymentInfo(nodeSourceName, cloud, newDeployment);
                    cloud.addDeployedRegion(newDeployment.getNode().getNodeCandidate().getLocation().getName(),
                                            newDeployment.getNode()
                                                         .getNodeCandidate()
                                                         .getLocation()
                                                         .getName() + "/" + newDeployment.getNode()
                                                                                         .getNodeCandidate()
                                                                                         .getImage()
                                                                                         .getProviderId());
                }
            }

            LOGGER.info("Node source defined.");

            LOGGER.info("Trying to retrieve task: " + node.getTaskName());
            Task task = repositoryService.getJob(jobId).findTask(node.getTaskName());

            newDeployment.setPaCloud(cloud);
            newDeployment.setTask(task);
            newDeployment.setNumber(task.getNextDeploymentID());
            repositoryService.updateDeployment(newDeployment);
            LOGGER.debug("Deployment created: " + newDeployment.toString());

            repositoryService.updatePACloud(cloud);
            LOGGER.info("Deployment added to the related cloud: " + cloud.toString());

            task.addDeployment(newDeployment);
            repositoryService.updateTask(task);
        });

        repositoryService.flush();

        LOGGER.info("Nodes added properly.");

        return true;
    }

    /**
     * Define a node source in PA server related to a deployment information
     * @param nodeSourceName A valid and unique node source name
     * @param cloud The cloud information object
     * @param deployment The deployment information object
     */
    private void defineNSWithDeploymentInfo(String nodeSourceName, PACloud cloud, Deployment deployment) {
        String filename;
        Map<String, String> variables = new HashMap<>();
        variables.put("NS_name", nodeSourceName);
        variables.put("security_group", Optional.ofNullable(cloud.getSecurityGroup()).orElse(""));
        variables.put("sshUsername", Optional.ofNullable(cloud.getSshCredentials().getUsername()).orElse(""));
        variables.put("sshKeyPairName", Optional.ofNullable(cloud.getSshCredentials().getKeyPairName()).orElse(""));
        variables.put("sshPrivateKey", Optional.ofNullable(cloud.getSshCredentials().getPrivateKey()).orElse(""));
        try {
            URL endpointPa = (new URL(serviceConfiguration.getPaUrl()));
            variables.put("rm_host_name", endpointPa.getHost());
            variables.put("pa_port", "" + endpointPa.getPort());
        } catch (MalformedURLException e) {
            LOGGER.error("MalformedURLException: ", e);
        }
        if (WhiteListedInstanceTypesUtils.isHandledHardwareInstanceType(deployment.getNode()
                                                                                  .getNodeCandidate()
                                                                                  .getHardware()
                                                                                  .getProviderId())) {
            switch (cloud.getCloudProviderName()) {
                case "aws-ec2":
                    filename = File.separator + "Define_NS_AWS_AutoScale.xml";
                    variables.put("aws_username", cloud.getCredentials().getUserName());
                    variables.put("aws_secret", cloud.getCredentials().getPrivateKey());
                    variables.put("image", deployment.getNode().getNodeCandidate().getImage().getProviderId());
                    variables.put("instance_type",
                                  deployment.getNode().getNodeCandidate().getHardware().getProviderId());
                    variables.put("region", deployment.getNode().getNodeCandidate().getLocation().getName());
                    variables.put("subnet", Optional.ofNullable(cloud.getSubnet()).orElse(""));
                    break;
                default:
                    throw new IllegalArgumentException("Unhandled white listed instance type for cloud provider: " +
                                                       cloud.getCloudProviderName());
            }
        } else {
            variables.put("NS_nVMs", "0");
            variables.put("image",
                          deployment.getNode().getNodeCandidate().getLocation().getName() + File.separator +
                                   deployment.getNode().getNodeCandidate().getImage().getProviderId());
            switch (cloud.getCloudProviderName()) {
                case "aws-ec2":
                    filename = File.separator + "Define_NS_AWS.xml";
                    variables.put("aws_username", cloud.getCredentials().getUserName());
                    variables.put("aws_secret", cloud.getCredentials().getPrivateKey());
                    variables.put("subnet", Optional.ofNullable(cloud.getSubnet()).orElse(""));
                    break;
                case "openstack":
                    filename = File.separator + "Define_NS_OS.xml";
                    variables.put("os_endpoint", cloud.getEndpoint());
                    variables.put("os_scopePrefix", cloud.getScopePrefix());
                    variables.put("os_scopeValue", cloud.getScopeValue());
                    variables.put("os_identityVersion", cloud.getIdentityVersion());
                    variables.put("os_username", cloud.getCredentials().getUserName());
                    variables.put("os_password", cloud.getCredentials().getPrivateKey());
                    variables.put("os_domain", cloud.getCredentials().getDomain());
                    variables.put("os_region", deployment.getNode().getNodeCandidate().getLocation().getName());
                    variables.put("os_networkId", cloud.getDefaultNetwork());
                    break;
                default:
                    throw new IllegalArgumentException("Unhandled cloud provider: " + cloud.getCloudProviderName());
            }
        }
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
        LOGGER.info("Trying to deploy the NS: " + nodeSourceName);
        JobId jobId = schedulerGateway.submit(fXmlFile, variables);
        LOGGER.info("Job submitted with ID: " + jobId);
        TemporaryFilesHelper.delete(fXmlFile);
    }

    /**
     * Get all added nodes
     * @param sessionId A valid session id
     * @return List of all table Deployment's entries
     */
    public List<Deployment> getNodes(String sessionId) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        resourceManagerGateway.synchronizeDeploymentsIPAddresses(schedulerGateway);
        resourceManagerGateway.synchronizeDeploymentsInstanceIDs();
        List<Deployment> allDeployments = repositoryService.listDeployments();
        LOGGER.info("Fetched deployments size: {}", allDeployments.size());
        return allDeployments;
    }

    /**
     * Remove nodes
     * @param sessionId A valid session id
     * @param nodeNames List of node names to remove
     * @param preempt If true remove node immediately without waiting for it to be freed
     */
    public Boolean removeNodes(String sessionId, List<String> nodeNames, boolean preempt) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        nodeNames.forEach(nodeName -> {
            try {
                List<String> nodeURLs = resourceManagerGateway.searchNodes(Collections.singletonList(nodeName), true);
                if (!nodeURLs.isEmpty()) {
                    String nodeUrl = nodeURLs.get(0);
                    resourceManagerGateway.removeNode(nodeUrl, preempt);
                    LOGGER.info("Node " + nodeName + " with URL: " + nodeUrl + " has been removed successfully.");
                } else {
                    LOGGER.warn("No Nodes with tag " + nodeName + " has been found in RM. Nothing to be removed here.");
                }

            } catch (NotConnectedException | RestException e) {
                LOGGER.error(String.valueOf(e.getStackTrace()));
            }
        });
        return true;
    }
}
