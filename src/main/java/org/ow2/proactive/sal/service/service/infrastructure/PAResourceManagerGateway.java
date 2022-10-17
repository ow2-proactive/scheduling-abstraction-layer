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
package org.ow2.proactive.sal.service.service.infrastructure;

import java.security.KeyException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import javax.security.auth.login.LoginException;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.util.InetAddressUtils;
import org.ow2.proactive.resourcemanager.common.NSState;
import org.ow2.proactive.resourcemanager.common.event.RMNodeEvent;
import org.ow2.proactive.resourcemanager.common.event.RMNodeSourceEvent;
import org.ow2.proactive.resourcemanager.common.event.dto.RMStateFull;
import org.ow2.proactive.resourcemanager.exception.RMException;
import org.ow2.proactive.resourcemanager.exception.RMNodeException;
import org.ow2.proactive.sal.service.model.Deployment;
import org.ow2.proactive.sal.service.model.IpAddress;
import org.ow2.proactive.sal.service.model.IpAddressType;
import org.ow2.proactive.sal.service.model.IpVersion;
import org.ow2.proactive.sal.service.service.ServiceConfiguration;
import org.ow2.proactive.sal.service.service.application.PAFactory;
import org.ow2.proactive.sal.service.util.EntityManagerHelper;
import org.ow2.proactive.sal.service.util.RMConnectionHelper;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.ow2.proactive.scheduler.common.exception.UserException;
import org.ow2.proactive.scheduler.common.job.TaskFlowJob;
import org.ow2.proactive.scheduler.common.task.ScriptTask;
import org.ow2.proactive.scheduler.common.task.TaskResult;
import org.ow2.proactive_grid_cloud_portal.common.RMRestInterface;
import org.ow2.proactive_grid_cloud_portal.scheduler.exception.PermissionRestException;
import org.ow2.proactive_grid_cloud_portal.scheduler.exception.RestException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service("PAResourceManagerGatewayService")
public class PAResourceManagerGateway {

    private RMRestInterface rmRestInterface;

    private String username;

    private String password;

    /**
     * Get, in an asynchronous way, deployed nodes names
     * @param nodeSource The name of the node source
     * @return List of deployed nodes names
     * @throws InterruptedException In case the process is interrupted
     */
    public List<String> getAsyncDeployedNodesInformation(String nodeSource, String option) throws InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Callable<List<String>> callable = () -> {
            int retries = 0;

            List<String> deployedNodes = null;
            boolean gotResponse = false;

            while (!gotResponse && retries <= ServiceConfiguration.MAX_CONNECTION_RETRIES) {
                try {
                    Thread.sleep(ServiceConfiguration.INTERVAL);
                    deployedNodes = getDeployedNodesInformation(nodeSource, option);
                    if (!deployedNodes.isEmpty()) {
                        gotResponse = true;
                    } else {
                        retries++;
                    }
                } catch (Exception e) {
                    if (e.getClass().getSimpleName().equals("IllegalArgumentException")) {
                        throw e;
                    } else {
                        retries++;
                    }
                }
            }
            if (gotResponse) {
                LOGGER.info("Got nodes names after " + retries + " failed attempts.");
                return deployedNodes;
            }
            throw new ConnectTimeoutException("Impossible to get deployed nodes names from RM.");
        };

        Future<List<String>> connectionResponse = executorService.submit(callable);
        List<String> deployedNodes = null;
        try {
            deployedNodes = connectionResponse.get();
        } catch (ExecutionException ee) {
            LOGGER.error("ExecutionException: " + ee);
        }
        executorService.shutdown();
        return deployedNodes;

        /*
         * TODO
         * This method throws an error if a node source is deployed with no nodes attached.
         * The better approach should be returning an empty list instead of connectTimeoutException.
         */
    }

    /**
     * Init a gateway to the ProActive Resource Manager
     * @param paURL ProActive URL (exp: https://try.activeeon.com:8443/)
     */
    public void init(String paURL) {
        // Initialize the gateway from the RMConnectionHelper class
        rmRestInterface = RMConnectionHelper.init(paURL);
    }

    /**
     * Get the available VMs at the proactive server
     * @return rmNodeEvents the list of the available VMs
     * @throws NotConnectedException In case the user is not connected
     * @throws PermissionRestException In case the user does not have valid permissions
     */
    public List<RMNodeEvent> getListOfNodesEvents() throws NotConnectedException, PermissionRestException {
        reconnectIfDisconnected();
        RMStateFull rmStateFull = rmRestInterface.getRMStateFull(RMConnectionHelper.getSessionId());
        List<RMNodeEvent> rmNodeEvents = rmStateFull.getNodesEvents();
        return rmNodeEvents;
    }

    /**
     * Connect to the ProActive server
     * @param username Username
     * @param password Password
     * @throws LoginException In case the login is not valid
     * @throws KeyException In case the password is not valid
     * @throws RMException In case an error happens in the RM
     */
    public void connect(String username, String password) throws LoginException, KeyException, RMException {
        this.username = username;
        this.password = password;
        RMConnectionHelper.connect(this.username, this.password);
    }

    /**
     * Disconnect from the ProActive server
     */
    public void disconnect() {
        RMConnectionHelper.disconnect();
    }

    /**
     * Verify that the provided sessionId corresponds to an active session
     * * @param sessionId A session id
     * @return True if the connexion session is active, false otherwise
     * @throws NotConnectedException In case the user is not connected
     */
    public Boolean isActive(String sessionId) throws NotConnectedException {
        return RMConnectionHelper.isActive(sessionId);
    }

    /**
     * Get deployed nodes names
     * @param nodeSource The name of the node source
     * @param option needed (either "hostname" or "name")
     * @return List of deployed nodes names
     * @throws NotConnectedException In case the user is not connected
     * @throws PermissionRestException In case the user does not have valid permissions
     * @throws IllegalArgumentException if the option passed was not supported
     */
    private List<String> getDeployedNodesInformation(String nodeSource, String option)
            throws IllegalArgumentException, NotConnectedException, PermissionRestException {
        reconnectIfDisconnected();
        List<String> deployedNodes = new ArrayList<>();
        RMStateFull rmState = getFullMonitoring();
        String nodeInfo = "none";
        LOGGER.debug("Searching for deployed nodes information ...");
        for (RMNodeEvent rmNodeEvent : rmState.getNodesEvents()) {
            if (rmNodeEvent.getNodeSource().equals(nodeSource)) {
                if (option.equals("name")) {
                    nodeInfo = rmNodeEvent.getNodeUrl();
                }
                if (option.equals("hostname")) {
                    nodeInfo = rmNodeEvent.getHostName();
                }
                if (option.equals("state")) {
                    nodeInfo = rmNodeEvent.getNodeState().toString();
                }
                if (nodeInfo.equals("none")) {
                    LOGGER.error("A wrong option was passed and the nodeInfo was now changed");
                    throw new IllegalArgumentException("The option passed \"" + option + "\" is not found!");
                } else {
                    deployedNodes.add(nodeInfo.substring(nodeInfo.lastIndexOf('/') + 1));
                }
            }
        }
        LOGGER.info(deployedNodes.size() + " nodes found!");

        return deployedNodes;

        /*
         * TODO
         * This method can be improved to including all RMNodeEvent variables.
         * For now, all what we need are the name and Hostname, but later it can include other
         * variables.
         */
    }

    private RMStateFull getFullMonitoring() throws NotConnectedException, PermissionRestException {
        reconnectIfDisconnected();
        LOGGER.debug("Getting full RM state ...");
        RMStateFull rmState = rmRestInterface.getRMStateFull(RMConnectionHelper.getSessionId());
        LOGGER.debug("Full monitoring got.");
        return rmState;
    }

    /**
     * Deploy a simple AWS node source
     * @param awsUsername A valid AWS user name
     * @param awsKey A valid AWS secret key
     * @param rmHostname The RM host name
     * @param nodeSourceName The name of the node source
     * @param numberVMs The number of needed VMs
     * @throws NotConnectedException In case the user is not connected
     * @throws PermissionRestException In case the user does not have valid permissions
     */
    public void deploySimpleAWSNodeSource(String awsUsername, String awsKey, String rmHostname, String nodeSourceName,
            Integer numberVMs) throws NotConnectedException, PermissionRestException {
        reconnectIfDisconnected();
        // Getting NS configuration settings
        String infrastructureType = "org.ow2.proactive.resourcemanager.nodesource.infrastructure.AWSEC2Infrastructure";
        String[] infrastructureParameters = { awsUsername, //username
                                              awsKey, //secret
                                              numberVMs.toString(), //N of VMs
                                              "1", //N VMs per node
                                              "", //image
                                              "", //OS
                                              "", //awsKeyPair
                                              "", //ram
                                              "", //Ncore
                                              "", //sg
                                              "", //subnet
                                              rmHostname, //host
                                              "http://" + rmHostname + ":8080/connector-iaas", //connector-iaas url
                                              "http://" + rmHostname + ":8080/rest/node.jar", //node jar url
                                              "", "300000", //timeout
                                              "" };
        LOGGER.debug("infrastructureParameters: " + Arrays.toString(infrastructureParameters));
        String[] infrastructureFileParameters = { "" };
        String policyType = "org.ow2.proactive.resourcemanager.nodesource.policy.StaticPolicy";
        String[] policyParameters = { "ALL", "ME" };
        String[] policyFileParameters = {};
        String nodesRecoverable = "true";

        LOGGER.debug("Creating NodeSource ...");
        rmRestInterface.defineNodeSource(RMConnectionHelper.getSessionId(),
                                         nodeSourceName,
                                         infrastructureType,
                                         infrastructureParameters,
                                         infrastructureFileParameters,
                                         policyType,
                                         policyParameters,
                                         policyFileParameters,
                                         nodesRecoverable);
        LOGGER.info("NodeSource created.");

        LOGGER.debug("Deploying the NodeSource ...");
        rmRestInterface.deployNodeSource(RMConnectionHelper.getSessionId(), nodeSourceName);
        LOGGER.info("NodeSource VMs deployed.");

    }

    /**
     * Search the nodes with specific tags.
     * @param tags a list of tags which the nodes should contain. When not specified or an empty list, all the nodes known urls are returned
     * @param all When true, the search return nodes which contain all tags;
     *            when false, the search return nodes which contain any tag among the list tags.
     * @return the set of urls which match the search condition
     * @throws NotConnectedException In case the user is not connected
     * @throws RestException In case a Rest exception is thrown
     */
    public List<String> searchNodes(List<String> tags, boolean all) throws NotConnectedException, RestException {
        reconnectIfDisconnected();
        LOGGER.debug("Search for nodes with tags " + tags + " ...");
        List<String> nodesUrls = new ArrayList<>(rmRestInterface.searchNodes(RMConnectionHelper.getSessionId(),
                                                                             tags,
                                                                             all));
        LOGGER.debug("Nodes found: " + nodesUrls);
        return nodesUrls;
    }

    /**
     * Undeploy a node source
     * @param nodeSourceName The name of the node source to undeploy
     * @param preempt If true undeploy node source immediately without waiting for nodes to be freed
     * @return The result of the action, possibly containing the error message
     * @throws NotConnectedException In case the user is not connected
     * @throws PermissionRestException In case the user does not have valid permissions
     */
    public NSState undeployNodeSource(String nodeSourceName, Boolean preempt)
            throws NotConnectedException, PermissionRestException {
        reconnectIfDisconnected();
        LOGGER.debug("Undeploying node source ...");
        NSState nsState = rmRestInterface.undeployNodeSource(RMConnectionHelper.getSessionId(),
                                                             nodeSourceName,
                                                             preempt);
        LOGGER.info("Node source undeployed!");
        return nsState;
    }

    /**
     * Remove a node source
     * @param nodeSourceName The name of the node source to remove
     * @param preempt If true remove node source immediately without waiting for nodes to be freed
     * @return True if the node source is removed successfully, false or exception otherwise
     * @throws NotConnectedException In case the user is not connected
     * @throws PermissionRestException In case the user does not have valid permissions
     */
    public Boolean removeNodeSource(String nodeSourceName, Boolean preempt)
            throws NotConnectedException, PermissionRestException, IllegalArgumentException {
        reconnectIfDisconnected();
        LOGGER.debug("Removing node source ...");
        Boolean result = rmRestInterface.removeNodeSource(RMConnectionHelper.getSessionId(), nodeSourceName, preempt);
        LOGGER.info("Node source removed!");
        return result;
    }

    /**
     * Release a node
     * @param nodeUrl The URL of the node to remove
     * @return True if the node is removed successfully, false or exception otherwise
     * @throws NotConnectedException In case the user is not connected
     * @throws PermissionRestException In case the user does not have valid permissions
     * @throws RMNodeException In case the RM throws a Node exception
     */
    public Boolean releaseNode(String nodeUrl) throws NotConnectedException, PermissionRestException, RMNodeException {
        reconnectIfDisconnected();
        LOGGER.debug("Releasing node ...");
        Boolean result = rmRestInterface.releaseNode(RMConnectionHelper.getSessionId(), nodeUrl);
        LOGGER.info("Node released!");
        return result;
    }

    /**
     * Remove a node
     * @param nodeUrl The URL of the node to remove
     * @param preempt If true remove node immediately without waiting for node to be freed
     * @return True if the node is removed successfully, false or exception otherwise
     * @throws NotConnectedException In case the user is not connected
     * @throws PermissionRestException In case the user does not have valid permissions
     */
    public Boolean removeNode(String nodeUrl, Boolean preempt) throws NotConnectedException, PermissionRestException {
        reconnectIfDisconnected();
        LOGGER.debug("Removing node \'" + nodeUrl + "\' ...");
        Boolean result = rmRestInterface.removeNode(RMConnectionHelper.getSessionId(), nodeUrl, preempt);
        LOGGER.info("Node removed!");
        return result;
    }

    void reconnectIfDisconnected() {
        try {
            if (RMConnectionHelper.isActive()) {
                LOGGER.info("Connexion to ProActive RM is active.");
            } else {
                LOGGER.warn("WARNING: ProActive Resource Manager is not reachable.");
            }
            LOGGER.debug("Connexion to ProActive RM renewed.");
        } catch (NotConnectedException | RuntimeException nce) {
            try {
                LOGGER.info("Reconnecting to ProActive RM ...");
                RMConnectionHelper.connect(this.username, this.password);
            } catch (LoginException | KeyException | RMException e) {
                LOGGER.error("ERROR: Not able to reconnect to RM due to: " + Arrays.toString(e.getStackTrace()));
            }
        }
    }

    private void renewSession() {
        try {
            LOGGER.debug("Renewing connexion ...");
            if (RMConnectionHelper.isActive()) {
                LOGGER.debug("Connexion to ProActive RM is active.");
            } else {
                LOGGER.warn("WARNING: ProActive Resource Manager is not reachable.");
            }
            LOGGER.info("Connexion to ProActive RM renewed.");
        } catch (NotConnectedException | RuntimeException nce) {
            LOGGER.error("ERROR: Not able to renew connexion to RM due to: " + Arrays.toString(nce.getStackTrace()));
        }
    }

    // For testing purpose only

    public RMRestInterface getRmRestInterface() {
        return rmRestInterface;
    }

    public void setRmRestInterface(RMRestInterface rmRestInterface) {
        this.rmRestInterface = rmRestInterface;
    }

    public String getSessionId() {
        return RMConnectionHelper.getSessionId();
    }

    public void synchronizeDeploymentsIPAddresses(PASchedulerGateway schedulerGateway) {
        List<Deployment> deployments = EntityManagerHelper.createQuery("SELECT d FROM Deployment d", Deployment.class)
                                                          .getResultList();
        EntityManagerHelper.begin();
        deployments.parallelStream().forEach(deployment -> {
            if (deployment.getIsDeployed()) {
                try {
                    if (deployment.getIpAddress() != null) {
                        LOGGER.info("Deployment " + deployment.getNodeName() + "already synchronized. IP: " +
                                    deployment.getIpAddress());
                    } else {
                        List<String> nodeURLs = searchNodes(Collections.singletonList(deployment.getNodeName()), true);
                        if (!nodeURLs.isEmpty()) {
                            TaskFlowJob paIPJob = createIPAddrGetterWorkflow(deployment);
                            if (!paIPJob.getTasks().isEmpty()) {
                                long submittedJobId = schedulerGateway.submit(paIPJob).longValue();
                                LOGGER.info("Public ip getter job submitted successfully. ID = " + submittedJobId);
                                TaskResult taskResult = schedulerGateway.waitForTask(Long.toString(submittedJobId),
                                                                                     "get_ip_addr_task",
                                                                                     60000);
                                String publicIPAddr = taskResult.getValue().toString();
                                LOGGER.info("Task result value to string: " + publicIPAddr);
                                if (!InetAddressUtils.isIPv4Address(publicIPAddr)) {
                                    LOGGER.warn(String.format("IPAddrGetter job could not retrieve the public ip for deployment [%s]",
                                                              deployment.getNodeName()));
                                    LOGGER.warn("     Public ip address will be set to 999.999.999.999");
                                    publicIPAddr = "999.999.999.999";
                                }
                                IpAddress ipAddress = new IpAddress(IpAddressType.PUBLIC_IP,
                                                                    IpVersion.V4,
                                                                    publicIPAddr);
                                LOGGER.info("ipAddress: " + ipAddress.toString());
                                deployment.setIpAddress(ipAddress);
                                EntityManagerHelper.persist(deployment);
                            }
                        } else {
                            LOGGER.warn("The node " + deployment.getNodeName() + " is not reachable in RM.");
                        }
                    }
                } catch (NotConnectedException nce) {
                    LOGGER.error("ERROR: Not able to search for a node due to a NotConnectedException: " +
                                 Arrays.toString(nce.getStackTrace()));
                } catch (RestException re) {
                    LOGGER.error("ERROR: Not able to search for a node due to a RestException: " +
                                 Arrays.toString(re.getStackTrace()));
                } catch (Throwable throwable) {
                    LOGGER.error("ERROR: Not able to access task result due to a Throwable: " +
                                 Arrays.toString(throwable.getStackTrace()));
                }
            }
        });
        EntityManagerHelper.commit();
    }

    private TaskFlowJob createIPAddrGetterWorkflow(Deployment deployment) {
        ScriptTask ipAddrGetterTask = PAFactory.createGroovyScriptTaskFromFile("get_ip_addr_task",
                                                                               "get_ip_addr_script.groovy");
        ipAddrGetterTask.addGenericInformation("NODE_ACCESS_TOKEN", deployment.getNodeAccessToken());
        TaskFlowJob paJob = new TaskFlowJob();
        try {
            paJob.addTask(ipAddrGetterTask);
        } catch (UserException ue) {
            LOGGER.error("Task " + ipAddrGetterTask.getName() + " could not be added due to: " +
                         Arrays.toString(ue.getStackTrace()));
        }
        paJob.setName(deployment.getNodeName() + "_ip_getter_job");
        paJob.setMaxNumberOfExecution(2);
        paJob.setProjectName("Public-ip-getter");
        LOGGER.info("Job created: " + paJob.toString());
        return paJob;
    }

    public void synchronizeDeploymentsInstanceIDs() {
        List<Deployment> deployments = EntityManagerHelper.createQuery("SELECT d FROM Deployment d", Deployment.class)
                                                          .getResultList();
        EntityManagerHelper.begin();
        deployments.parallelStream().forEach(deployment -> {
            if (deployment.getIsDeployed()) {
                try {
                    if (deployment.getInstanceId() != null) {
                        LOGGER.info("Deployment " + deployment.getNodeName() + " already synchronized. Instance ID: " +
                                    deployment.getInstanceId());
                    } else {
                        List<String> nodeURLs = searchNodes(Collections.singletonList(deployment.getNodeName()), true);
                        if (!nodeURLs.isEmpty()) {
                            String instanceId = nodeURLs.get(0).substring(nodeURLs.get(0).lastIndexOf("__") + 2);
                            deployment.setInstanceId(instanceId);
                            EntityManagerHelper.persist(deployment);
                            LOGGER.info("Deployment " + deployment.getNodeName() + "instance ID set to: " + instanceId);
                        } else {
                            LOGGER.warn("The node " + deployment.getNodeName() + " is not reachable in RM.");
                        }
                    }
                } catch (NotConnectedException nce) {
                    LOGGER.error("ERROR: Not able to search for a node due to a NotConnectedException: " +
                                 Arrays.toString(nce.getStackTrace()));
                } catch (RestException re) {
                    LOGGER.error("ERROR: Not able to search for a node due to a RestException: " +
                                 Arrays.toString(re.getStackTrace()));
                }
            }
        });
        EntityManagerHelper.commit();
    }

    /**
     * Get a list of node source names in a given status from the ProActive Resource Manager
     * @param status defines the status of node sources to be returned, can take ["deployed", "undeployed", "all"]
     * @return listNodeSourceNames a string list of names of the deployed node sources
     * @throws NotConnectedException In case the user is not connected
     * @throws PermissionRestException In case the user does not have valid permissions
     */
    public List<String> getNodeSourceNames(String status) throws NotConnectedException, PermissionRestException {
        LOGGER.info("Getting the node sources names from the resource manager");
        if (!Arrays.asList("deployed", "undeployed", "all").contains(status)) {
            LOGGER.error("The passed status \"" + status + "\" is incorrect");
            throw new IllegalArgumentException("The passed status \"" + status + "\" is incorrect");
        }
        return getFullMonitoring().getNodeSource()
                                  .stream()
                                  .filter(nodeSourceEvent -> nodeSourceEvent.getNodeSourceStatus().equals(status) ||
                                                             status.equals("all"))
                                  .map(RMNodeSourceEvent::getNodeSourceName)
                                  .collect(Collectors.toList());
    }

    /**
     * Get a list of deployed nodes hostnames from the ProActive Resource Manager
     * @param nsName a String of the node source name
     * @return nodeHostNames a string list of names of the deployed nodes hostnames
     */
    public List<String> getNodeHostNames(String nsName) {
        List<String> nodeHostNames = new LinkedList<>();
        try {
            nodeHostNames = getAsyncDeployedNodesInformation(nsName, "hostname");
        } catch (Exception e) {
            LOGGER.error(" resourceManagerGateway threw an exception: " + e);
        }
        return nodeHostNames;
    }

    /**
     * Get a list of deployed nodes states from the ProActive Resource Manager
     * @param nsName a String of the node source name
     * @return nodeStates a string list of names of the deployed node states
     */
    public List<String> getNodeStates(String nsName) {
        List<String> nodeStates = new LinkedList<>();
        try {
            nodeStates = getAsyncDeployedNodesInformation(nsName, "state");
        } catch (Exception e) {
            LOGGER.error(" resourceManagerGateway threw an exception: " + e);
        }
        return nodeStates;
    }

    /**
     * Get a list of deployed nodes sources from the ProActive Resource Manager
     * @return nodeSourcesNames a string list of names of the deployed node sources
     */
    public List<String> getDeployedNodeSourcesNames() {
        List<String> nodeSourcesNames = new LinkedList<>();
        try {
            nodeSourcesNames = getNodeSourceNames("deployed");
        } catch (NotConnectedException | PermissionRestException e) {
            LOGGER.error(Arrays.toString(e.getStackTrace()));
        }
        return nodeSourcesNames;
    }
}
