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

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.ow2.proactive.sal.model.*;
import org.ow2.proactive.sal.service.util.ClusterUtils;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;


@Log4j2
@Service("ClusterService")
public class ClusterService {
    @Autowired
    private PAGatewayService paGatewayService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private JobService jobService;

    public boolean defineCluster(String sessionId, ClusterDefinition clusterDefinition)
            throws NotConnectedException, IOException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Validate.notNull(clusterDefinition, "The received Byon node definition is empty. Nothing to be defined.");
        LOGGER.info("defineCluster endpoint is called to define the cluster: " + clusterDefinition.getName());

        Cluster newCluster = new Cluster();
        newCluster.setName(clusterDefinition.getName());
        newCluster.setMasterNode(clusterDefinition.getMasterNode());
        newCluster.setStatus("defined");
        clusterDefinition.getNodes()
                         .forEach(clusterNodeDef -> repositoryService.saveClusterNodeDefinition(clusterNodeDef));
        newCluster.setNodes(clusterDefinition.getNodes());
        repositoryService.saveCluster(newCluster);
        ClusterNodeDefinition masterNode = ClusterUtils.getNodeByName(newCluster, newCluster.getMasterNode());
        if (masterNode != null) {
            PACloud cloud = repositoryService.getPACloud(masterNode.getCloudId());
            Job masterNodeJob = ClusterUtils.createMasterNodeJob(newCluster.getName(), masterNode, cloud);
            masterNodeJob.getTasks().forEach(repositoryService::saveTask);
            repositoryService.saveJob(masterNodeJob);
        } else {
            LOGGER.error("The cluster Definition of {} contains the master node name {}," +
                         "but this node is not found in the list of defined nodes",
                         newCluster.getName(),
                         newCluster.getMasterNode());
            throw new IllegalArgumentException("The master node [%s] was not passed in the list of the defined nodes");
        }
        List<ClusterNodeDefinition> workerNodes = ClusterUtils.getWrokerNodes(newCluster);
        if (workerNodes.isEmpty()) {
            LOGGER.warn("The cluster does not has any worker nodes, only the master will be deployed");
        } else {
            for (ClusterNodeDefinition node : workerNodes) {
                PACloud cloud = repositoryService.getPACloud(node.getCloudId());
                Job workerNodeJob = ClusterUtils.createWorkerNodeJob(newCluster.getName(), node, cloud);
                workerNodeJob.getTasks().forEach(repositoryService::saveTask);
                repositoryService.saveJob(workerNodeJob);
            }
        }
        repositoryService.flush();

        return true;
    }

    public boolean deployCluster(String sessionId, String clusterName) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Validate.notNull(clusterName, "The received clusterName is empty. Nothing to be defined.");
        LOGGER.info("deployCluster endpoint is called to deploy the cluster: " + clusterName);
        Cluster toDeployClutser = ClusterUtils.getClusterByName(clusterName, repositoryService.listCluster());

        // add nodes
        if (toDeployClutser == null) {
            LOGGER.error("No Cluster was found! Nothing is deployed!");
            return false;
        } else {
            List<ClusterNodeDefinition> workerNodes = ClusterUtils.getWrokerNodes(toDeployClutser);
            LOGGER.info("Deploying the master node of the cluster [{}]", toDeployClutser.getName());
            submitClutserNode(sessionId, toDeployClutser, toDeployClutser.getMasterNode(), false);
            LOGGER.info("Deploying the worker nodes of the cluster [{}]", toDeployClutser.getName());
            for (ClusterNodeDefinition node : workerNodes) {
                submitClutserNode(sessionId, toDeployClutser, node.getName(), true);
            }
            toDeployClutser.setStatus("submited");
            repositoryService.saveCluster(toDeployClutser);
            repositoryService.flush();
        }
        return true;
    }

    private void submitClutserNode(String sessionId, Cluster cluster, String nodeName, boolean worker)
            throws NotConnectedException {
        LOGGER.info("Deploying the node {}...", nodeName);
        ClusterNodeDefinition node = ClusterUtils.getNodeByName(cluster, nodeName);
        if (node != null) {
            String jobId = node.getNodeJobName(cluster.getName());
            List<IaasDefinition> defs = ClusterUtils.getNodeIaasDefinition(sessionId, cluster, node.getName());
            nodeService.addNodes(sessionId, defs, jobId);
            Deployment currentDeployment = repositoryService.getDeployment(defs.get(0).getName());
            String masterNodeToken = cluster.getMasterNode() + "_" + cluster.getName();
            currentDeployment.setWorker(worker);
            currentDeployment.setMasterToken(masterNodeToken);
            repositoryService.saveDeployment(currentDeployment);
            repositoryService.flush();
            // submit job
            jobService.submitJob(sessionId, jobId);
            LOGGER.info("Node {} is submitted for deployment", nodeName);
        } else {
            LOGGER.error("The node {} was not found in the cluster {} definition", nodeName, cluster.getName());
        }
    }

    public Cluster getCluster(String sessionId, String clusterName) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        LOGGER.info("getCluster endpoint is called to deploy the cluster: " + clusterName);
        Cluster getCluster = ClusterUtils.getClusterByName(clusterName, repositoryService.listCluster());
        List<ClusterNodeDefinition> nodes = getCluster.getNodes();
        int i = 0;
        List<String> states = new ArrayList<>();
        for (ClusterNodeDefinition node : nodes) {
            JobState state = jobService.getJobState(sessionId, node.getNodeJobName(clusterName));
            if (state != null && state.getJobStatus() != null) {
                node.setState(state.getJobStatus().toString());
                states.add(state.getJobStatus().toString());
            } else {
                node.setState("defined");
            }
            nodes.set(i, node);
            i += 1;
            node.setNodeUrl(getNodeUrl(sessionId, clusterName, node));
        }
        if (states.contains("In-Error") || states.contains("Failed") || states.contains("Canceled")) {
            getCluster.setStatus("failed");
        } else {
            if (checkAllStates(states)) {
                getCluster.setStatus("deployed");
            }
        }
        getCluster.setNodes(nodes);
        repositoryService.saveCluster(getCluster);
        repositoryService.flush();
        return getCluster;
    }

    public Cluster scaleOutCluster(String sessionId, String clusterName, List<ClusterNodeDefinition> newNodes)
            throws NotConnectedException, IOException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        LOGGER.info("scaleOutCluster endpoint is called for the cluster: " + clusterName);
        Cluster toScaleCluster = ClusterUtils.getClusterByName(clusterName, repositoryService.listCluster());
        repositoryService.deleteCluster(toScaleCluster);
        repositoryService.flush();
        List<ClusterNodeDefinition> newList = new ArrayList<ClusterNodeDefinition>(toScaleCluster.getNodes());
        newList.addAll(newNodes);
        newList.forEach(clusterNodeDef -> repositoryService.saveClusterNodeDefinition(clusterNodeDef));
        toScaleCluster.setNodes(newList);
        toScaleCluster.setStatus("scaling");
        repositoryService.saveCluster(toScaleCluster);
        LOGGER.info("Scaling out the worker nodes of the cluster [{}]", clusterName);
        for (ClusterNodeDefinition node : newNodes) {
            PACloud cloud = repositoryService.getPACloud(node.getCloudId());
            Job workerNodeJob = ClusterUtils.createWorkerNodeJob(toScaleCluster.getName(), node, cloud);
            workerNodeJob.getTasks().forEach(repositoryService::saveTask);
            repositoryService.saveJob(workerNodeJob);
        }
        repositoryService.flush();
        for (ClusterNodeDefinition node : newNodes) {
            submitClutserNode(sessionId, toScaleCluster, node.getName(), true);
        }
        return toScaleCluster;
    }

    public Cluster scaleInCluster(String sessionId, String clusterName, List<String> nodesToDelete)
            throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        LOGGER.info("scaleDownCluster endpoint is called for the cluster: " + clusterName);
        Cluster toScaleCluster = getCluster(sessionId, clusterName);
        repositoryService.deleteCluster(toScaleCluster);
        repositoryService.flush();
        List<ClusterNodeDefinition> clusterNodes = toScaleCluster.getNodes();
        for (String nodeName : nodesToDelete) {
            ClusterNodeDefinition node = getNodeFromCluster(toScaleCluster, nodeName);
            if (node != null && !node.getName().equals(toScaleCluster.getMasterNode())) {
                try {
                    deleteNode(sessionId, clusterName, node);
                } catch (NotConnectedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                LOGGER.warn("unable to delete node {}, the node was not found in the cluster {}",
                            nodeName,
                            clusterName);
                if (node != null && node.getName().equals(toScaleCluster.getMasterNode())) {
                    LOGGER.warn("removing the master node {} of the cluster {} is not allowed!", nodeName, clusterName);
                }
            }
            clusterNodes = deleteNodeFromCluster(clusterNodes, nodeName);
        }
        toScaleCluster.setNodes(clusterNodes);
        toScaleCluster.setStatus("scaling");
        clusterNodes.forEach(clusterNodeDef -> repositoryService.saveClusterNodeDefinition(clusterNodeDef));
        repositoryService.saveCluster(toScaleCluster);
        repositoryService.flush();
        return toScaleCluster;
    }

    public Long labelNodes(String sessionId, String clusterName, List<Map<String, String>> nodeLabels)
            throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        LOGGER.info("labelNodes endpoint is called to label nodes in the cluster: " + clusterName);
        String masterNodeToken = "";
        Cluster cluster = ClusterUtils.getClusterByName(clusterName, repositoryService.listCluster());
        if (cluster != null) {
            masterNodeToken = cluster.getMasterNode() + "_" + clusterName;
        } else {
            LOGGER.error("The cluster with the name {} was not found!", clusterName);
            return -1L;
        }
        String script = ClusterUtils.createLabelNodesScript(nodeLabels, clusterName);

        try {
            return jobService.submitOneTaskJob(sessionId,
                                               script,
                                               masterNodeToken,
                                               "label_nodes_" + clusterName,
                                               "basic");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Long deployApplication(String sessionId, String clusterName, ClusterApplication application)
            throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        LOGGER.info("deployApplication endpoint is called to deploy the cluster: " + clusterName);
        String masterNodeToken = "";
        Cluster cluster = ClusterUtils.getClusterByName(clusterName, repositoryService.listCluster());
        if (cluster != null) {
            masterNodeToken = cluster.getMasterNode() + "_" + clusterName;
        } else {
            LOGGER.error("The cluster with the name {} was not found!", clusterName);
            return -1L;
        }
        String script = "";
        try {
            script = ClusterUtils.createDeployApplicationScript(application);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            return jobService.submitOneTaskJob(sessionId,
                                               script,
                                               masterNodeToken,
                                               "deploy_app_" + clusterName,
                                               "basic");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteCluster(String sessionId, String clusterName) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        LOGGER.info("deleteCluster endpoint is called to remove the cluster: " + clusterName);
        Cluster toScaleCluster = getCluster(sessionId, clusterName);
        for (ClusterNodeDefinition node : toScaleCluster.getNodes()) {
            if (node != null) {
                deleteNode(sessionId, clusterName, node);
            }
        }
        repositoryService.deleteCluster(toScaleCluster);
        repositoryService.flush();
        return true;
    }

    private boolean checkAllStates(List<String> states) {
        if (states.isEmpty()) {
            return false;
        }
        for (String state : states) {
            if (!state.equals("Finished")) {
                return false;
            }
        }
        return true;
    }

    private String getNodeUrl(String sessionId, String clusterName, ClusterNodeDefinition node)
            throws NotConnectedException {

        Map<String, Serializable> map = jobService.getJobResultMaps(sessionId, node.getNodeJobName(clusterName), 200L);
        if (!map.isEmpty()) {
            return Optional.ofNullable(map.get("nodeURL")).orElse("").toString();
        }
        return "";
    }

    private Long deleteNode(String sessionId, String clusterName, ClusterNodeDefinition node)
            throws NotConnectedException {
        String nodeUrl = getNodeUrl(sessionId, clusterName, node);
        if (nodeUrl != null && !nodeUrl.isEmpty()) {
            try {
                return jobService.submitOneTaskJob(sessionId, nodeUrl, "", "delete_node_" + node.getName(), "delete");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            LOGGER.warn("unable to delete node {}, wiht the node url \"{}\" !", node.getName(), node.getNodeUrl());
            return -1L;
        }
    }

    private ClusterNodeDefinition getNodeFromCluster(Cluster cluster, String nodeName) {
        for (ClusterNodeDefinition node : cluster.getNodes()) {
            if (Objects.equals(node.getName(), nodeName)) {
                return node;
            }
        }
        LOGGER.warn("The node {} was not found!", nodeName);
        return null;
    }

    private List<ClusterNodeDefinition> deleteNodeFromCluster(List<ClusterNodeDefinition> clusterNodes,
            String nodeName) {
        List<ClusterNodeDefinition> newClusterNodes = new ArrayList<>();
        for (ClusterNodeDefinition node : clusterNodes) {
            if (!node.getName().equals(nodeName)) {
                newClusterNodes.add(node);
            }
        }
        return newClusterNodes;
    }

}
