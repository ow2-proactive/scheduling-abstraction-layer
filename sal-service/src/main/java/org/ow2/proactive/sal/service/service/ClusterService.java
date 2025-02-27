/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import org.apache.commons.lang3.Validate;
import org.ow2.proactive.sal.model.*;
import org.ow2.proactive.sal.service.util.ByonUtils;
import org.ow2.proactive.sal.service.util.ClusterUtils;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.ow2.proactive.scheduler.common.job.JobStatus;
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

    @Autowired
    private EdgeService edgeService;

    private boolean isValidClusterName(String name) {
        return name != null && !name.isEmpty() && name.matches("^[a-z0-9-]+$");
    }

    public boolean defineCluster(String sessionId, ClusterDefinition clusterDefinition)
            throws NotConnectedException, IOException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Validate.notNull(clusterDefinition, "The received Cluster definition is empty. Nothing to be defined.");
        LOGGER.info("defineCluster endpoint is called to define the cluster: " + clusterDefinition.getName());

        Cluster cluster = new Cluster();

        //Validate cluster name
        if (!isValidClusterName(clusterDefinition.getName())) {
            throw new IllegalArgumentException("Invalid cluster name: " + clusterDefinition.getName() +
                                               ". Must contain only lowercase letters, numbers, and hyphens.");
        }
        cluster.setName(clusterDefinition.getName());

        //Validate master node is defined and it's name
        if (!isValidClusterName(clusterDefinition.getMasterNode())) {
            throw new IllegalArgumentException("Invalid master node name [" + clusterDefinition.getMasterNode() +
                                               "]. Must contain only lowercase letters, numbers, and hyphens.");
        }
        boolean masterNodeExists = clusterDefinition.getNodes()
                                                    .stream()
                                                    .anyMatch(node -> node.getName()
                                                                          .equals(clusterDefinition.getMasterNode()));
        if (!masterNodeExists) {
            throw new IllegalArgumentException("The master node [" + clusterDefinition.getMasterNode() +
                                               "] is not found in the list of defined nodes.");
        }
        cluster.setMasterNode(clusterDefinition.getMasterNode());

        // Validate all node names and check if NodeCandidate exists
        List<ClusterNodeDefinition> nodes = clusterDefinition.getNodes();
        for (ClusterNodeDefinition node : nodes) {
            if (!isValidClusterName(node.getName())) {
                throw new IllegalArgumentException("Invalid node name [" + node.getName() +
                                                   "]. Must contain only lowercase letters, numbers, and hyphens.");
            }

            NodeCandidate nc = repositoryService.getNodeCandidate(node.getNodeCandidateId());
            if (nc == null) {
                throw new IllegalArgumentException("No NodeCandidate found for node [" + node.getName() +
                                                   "] with candidate ID [" + node.getNodeCandidateId() + "].");
            }
        }

        cluster.setStatus(ClusterStatus.DEFINED);
        cluster.setEnvVars(ClusterUtils.createEnvVarsScript(clusterDefinition.getEnvVars()));
        nodes.forEach(repositoryService::saveClusterNodeDefinition);
        cluster.setNodes(nodes);
        repositoryService.saveCluster(cluster);

        //Create masterNode job
        ClusterNodeDefinition masterNode = ClusterUtils.getNodeByName(cluster, cluster.getMasterNode());
        if (masterNode != null) {
            PACloud cloud = repositoryService.getPACloud(masterNode.getCloudId());
            Job masterNodeJob = ClusterUtils.createMasterNodeJob(cluster.getName(),
                                                                 masterNode,
                                                                 cloud,
                                                                 cluster.getEnvVars());
            masterNodeJob.getTasks().forEach(repositoryService::saveTask);
            repositoryService.saveJob(masterNodeJob);
        }

        //Create workerNode job
        List<ClusterNodeDefinition> workerNodes = ClusterUtils.getWorkerNodes(cluster);
        if (workerNodes.isEmpty()) {
            LOGGER.warn("The cluster does not has any worker nodes, only the master will be deployed");
        } else {
            for (ClusterNodeDefinition node : workerNodes) {
                NodeCandidate nc = repositoryService.getNodeCandidate(node.getNodeCandidateId());
                createWorkerNodeJob(cluster, node, nc);
            }
        }
        repositoryService.flush();

        return true;
    }

    private void createWorkerNodeJob(Cluster cluster, ClusterNodeDefinition node, NodeCandidate nc) {
        try {
            String clusterName = cluster.getName();
            Job workerNodeJob;

            if (nc.getCloud().getCloudType().equals(CloudType.EDGE)) {
                EdgeNode edgeNode = ByonUtils.getEdgeNodeFromNC(nc);
                String jobId = node.getNodeJobName(clusterName);

                edgeNode.setJobId(jobId);
                repositoryService.saveEdgeNode(edgeNode);

                nc.setJobIdForEDGE(jobId);
                repositoryService.saveNodeCandidate(nc);

                workerNodeJob = ClusterUtils.createWorkerNodeJob(clusterName, node, null, cluster.getEnvVars());
            } else {
                PACloud cloud = repositoryService.getPACloud(node.getCloudId());
                workerNodeJob = ClusterUtils.createWorkerNodeJob(clusterName, node, cloud, cluster.getEnvVars());
            }

            workerNodeJob.getTasks().forEach(repositoryService::saveTask);
            repositoryService.saveJob(workerNodeJob);
        } catch (IOException e) {
            LOGGER.error("Failed to create worker node job for node [{}]: {}", node.getName(), e.getMessage(), e);
            throw new RuntimeException("Error processing worker node: " + node.getName(), e);
        }
    }

    public boolean deployCluster(String sessionId, String clusterName) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Validate.notNull(clusterName, "The received clusterName is empty. Nothing to be deployed.");
        //todo: validate that we exist the code here
        LOGGER.info("deployCluster endpoint is called to deploy the cluster: " + clusterName);
        Cluster cluster = ClusterUtils.getClusterByName(clusterName, repositoryService.listCluster());

        // add nodes
        if (cluster == null) {
            LOGGER.error("No Cluster definition was found! Nothing is deployed!");
            return false;
        } else {
            LOGGER.info("Deploying the master node of the cluster [{}]", cluster.getName());
            submitClusterNode(sessionId, cluster, cluster.getMasterNode(), false);

            List<ClusterNodeDefinition> workerNodes = ClusterUtils.getWorkerNodes(cluster);
            LOGGER.info("Deploying the worker nodes of the cluster [{}]", cluster.getName());
            for (ClusterNodeDefinition node : workerNodes) {
                submitClusterNode(sessionId, cluster, node.getName(), true);
            }
            cluster.setStatus(ClusterStatus.SUBMITTED);
            repositoryService.saveCluster(cluster);
            repositoryService.flush();
        }
        return true;
    }

    private void submitClusterNode(String sessionId, Cluster cluster, String nodeName, boolean worker)
            throws NotConnectedException {
        LOGGER.info("Deploying the node {}...", nodeName);
        ClusterNodeDefinition node = ClusterUtils.getNodeByName(cluster, nodeName);
        if (node != null) {
            NodeCandidate nc = repositoryService.getNodeCandidate(node.getNodeCandidateId());
            String jobId = node.getNodeJobName(cluster.getName());
            Deployment currentDeployment;
            if (nc.getCloud().getCloudType().equals(CloudType.EDGE)) {
                EdgeNode edgeNode = ByonUtils.getEdgeNodeFromNC(nc);
                Map<String, String> edgeNodeMap = new HashMap<>();
                edgeNodeMap.put(edgeNode.getId(), edgeNode.getName() + "/_Task");
                edgeService.addEdgeNodes(sessionId, edgeNodeMap, jobId);
                currentDeployment = repositoryService.getDeployment(edgeNode.getName());
            } else {
                List<IaasDefinition> defs = ClusterUtils.getNodeIaasDefinition(sessionId, cluster, node.getName());
                nodeService.addNodes(sessionId, defs, jobId);
                currentDeployment = repositoryService.getDeployment(defs.get(0).getName());
            }
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

        LOGGER.info("cluster endpoint is called to get the cluster status: " + clusterName);
        Cluster cluster = ClusterUtils.getClusterByName(clusterName, repositoryService.listCluster());
        if (cluster == null) {
            LOGGER.error("No Cluster was found! Nothing is deployed!");
            return null;
        } else {
            List<ClusterNodeDefinition> nodes = cluster.getNodes();
            int i = 0;
            List<JobStatus> states = new ArrayList<>();
            for (ClusterNodeDefinition node : nodes) {
                JobState state = jobService.getJobState(sessionId, node.getNodeJobName(clusterName));
                if (state != null && state.getJobStatus() != null) {
                    node.setState(state.getJobStatus().toString());
                    states.add(state.getJobStatus());
                } else {
                    node.setState(ClusterStatus.OTHER.toString()); //todo: check node states
                }
                nodes.set(i, node);
                i += 1;
                node.setNodeUrl(getNodeUrl(sessionId, clusterName, node));
            }
            if (states.contains(JobStatus.IN_ERROR) || states.contains(JobStatus.FAILED) ||
                states.contains(JobStatus.KILLED) || states.contains(JobStatus.CANCELED) ||
                states.contains(ClusterStatus.OTHER.toString())) {
                cluster.setStatus(ClusterStatus.FAILED);
            } else {
                if (checkAllStatesFinished(states)) {
                    cluster.setStatus(ClusterStatus.DEPLOYED);
                }
            }
            cluster.setNodes(nodes);
            repositoryService.saveCluster(cluster);
            repositoryService.flush();
            return cluster;
        }
    }

    public Cluster scaleOutCluster(String sessionId, String clusterName, List<ClusterNodeDefinition> newNodes)
            throws NotConnectedException, IOException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        LOGGER.info("scaleOutCluster endpoint is called for the cluster: " + clusterName);
        Cluster cluster = ClusterUtils.getClusterByName(clusterName, repositoryService.listCluster());
        repositoryService.deleteCluster(cluster);
        repositoryService.flush();

        List<ClusterNodeDefinition> newList = new ArrayList<ClusterNodeDefinition>(cluster.getNodes());
        newList.addAll(newNodes);
        newList.forEach(clusterNodeDef -> repositoryService.saveClusterNodeDefinition(clusterNodeDef));
        cluster.setNodes(newList);
        cluster.setStatus(ClusterStatus.SCALING);
        repositoryService.saveCluster(cluster);

        LOGGER.info("Scaling out the worker nodes of the cluster [{}]", clusterName);
        for (ClusterNodeDefinition node : newNodes) {
            PACloud cloud = repositoryService.getPACloud(node.getCloudId());
            Job workerNodeJob = ClusterUtils.createWorkerNodeJob(cluster.getName(), node, cloud, cluster.getEnvVars());
            workerNodeJob.getTasks().forEach(repositoryService::saveTask);
            repositoryService.saveJob(workerNodeJob);
        }
        repositoryService.flush();
        for (ClusterNodeDefinition node : newNodes) {
            submitClusterNode(sessionId, cluster, node.getName(), true);
        }
        return cluster;
    }

    public Cluster scaleInCluster(String sessionId, String clusterName, List<String> nodesToDelete)
            throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        LOGGER.info("scaleInCluster endpoint is called for the cluster: " + clusterName);
        Cluster cluster = ClusterUtils.getClusterByName(clusterName, repositoryService.listCluster());
        repositoryService.deleteCluster(cluster);
        repositoryService.flush();

        String masterNodeToken = "";
        if (cluster != null) {
            masterNodeToken = cluster.getMasterNode() + "_" + clusterName;
        }
        List<ClusterNodeDefinition> clusterNodes = cluster.getNodes();

        for (String nodeName : nodesToDelete) {
            ClusterNodeDefinition node = getNodeFromCluster(cluster, nodeName);
            if (node != null && !node.getName().equals(cluster.getMasterNode())) {
                try {
                    if (deleteNode(sessionId, clusterName, node, masterNodeToken, true) != -1L) {
                        clusterNodes = deleteNodeFromCluster(clusterNodes, nodeName);
                    }
                } catch (NotConnectedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                LOGGER.warn("unable to delete node {}, the node was not found in the cluster {}",
                            nodeName,
                            clusterName);
                if (node != null && node.getName().equals(cluster.getMasterNode())) {
                    LOGGER.warn("removing the master node {} of the cluster {} is not allowed!", nodeName, clusterName);
                }
            }
        }
        cluster.setNodes(clusterNodes);
        cluster.setStatus(ClusterStatus.SCALING);
        clusterNodes.forEach(clusterNodeDef -> repositoryService.saveClusterNodeDefinition(clusterNodeDef));
        repositoryService.saveCluster(cluster);
        repositoryService.flush();
        return cluster;
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
            String paJobName = "label_nodes_" + clusterName;
            return jobService.submitOneTaskJob(sessionId, script, masterNodeToken, paJobName, "basic", "");

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
                                               "basic",
                                               "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteCluster(String sessionId, String clusterName) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        LOGGER.info("deleteCluster endpoint is called to remove the cluster: " + clusterName);
        Cluster cluster = getCluster(sessionId, clusterName);
        for (ClusterNodeDefinition node : cluster.getNodes()) {
            if (node != null) {
                deleteNode(sessionId, clusterName, node, "", false);
            } else
                LOGGER.warn("Cannot delete a null node.");
        }
        repositoryService.deleteCluster(cluster);
        repositoryService.flush();
        return true;
    }

    private boolean checkAllStatesFinished(List<JobStatus> states) {
        if (states.isEmpty()) {
            return false;
        }
        for (JobStatus state : states) {
            if (!state.equals(JobStatus.FINISHED)) {
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

    private Long deleteNode(String sessionId, String clusterName, ClusterNodeDefinition node, String masterNodeToken,
            boolean drain) throws NotConnectedException {
        String nodeUrl = getNodeUrl(sessionId, clusterName, node);
        if (nodeUrl == null || nodeUrl.isEmpty()) {
            LOGGER.warn("Unable to delete node {}, as the node URL is empty or null.", node.getName());
        }

        Long jobId = -1L;
        try {
            // Submit the job to delete the node (either drain-delete or delete)
            String jobName = "delete_node_" + node.getName();
            String jobType = drain ? "drain-delete" : "delete";
            String nodeJobName = drain ? node.getNodeJobName(clusterName) : "";

            jobId = jobService.submitOneTaskJob(sessionId, nodeUrl, masterNodeToken, jobName, jobType, nodeJobName);

            // Proceed to clean up tasks and deployments if the job was submitted
            cleanupNodeJob(sessionId, clusterName, node);

        } catch (IOException e) {
            LOGGER.error("Failed to submit delete job for node {}: {}", node.getName(), e.getMessage());
            throw new RuntimeException("Error submitting delete job for node " + node.getName(), e);
        } catch (Exception e) {
            LOGGER.error("Unexpected error occurred while deleting node {}: {}", node.getName(), e.getMessage());
            throw new RuntimeException("Unexpected error occurred while deleting node " + node.getName(), e);
        }

        return jobId;
    }

    private void cleanupNodeJob(String sessionId, String clusterName, ClusterNodeDefinition node)
            throws NotConnectedException {
        Job nodeJob = repositoryService.getJob(node.getNodeJobName(clusterName));
        if (nodeJob == null) {
            LOGGER.info("No job found for node {}, skipping cleanup.", node.getName());
            return;
        }

        try {
            // Check if job is alive and kill it if necessary
            if (nodeJob.getSubmittedJobId() != 0L) {
                JobState jobState = jobService.getJobState(sessionId, nodeJob.getJobId());
                if (jobState.getJobStatus().isJobAlive()) {
                    jobService.killJob(sessionId, nodeJob.getJobId());
                }
            }

            // Gather all tasks and deployments for cleanup
            List<Task> nodeTasks = nodeJob.getTasks();
            List<Deployment> nodeDeployments = new ArrayList<>();
            for (Task task : nodeTasks) {
                nodeDeployments.addAll(task.getDeployments());
            }

            // Delete deployments, tasks, and the job in sequence
            nodeDeployments.forEach(deployment -> {
                try {
                    repositoryService.deleteDeployment(deployment);
                } catch (Exception e) {
                    LOGGER.warn("Failed to delete deployment for node {}: {}", node.getName(), e.getMessage());
                }
            });

            repositoryService.deleteJob(node.getNodeJobName(clusterName));
            nodeTasks.forEach(task -> repositoryService.deleteTask(task));
            repositoryService.flush();

            //undeploy edge node source and delete representing resource from ProActive Resource Manager
            NodeCandidate nc = repositoryService.getNodeCandidate(node.getNodeCandidateId());
            if (nc.getCloud().getCloudType().equals(CloudType.EDGE)) {
                EdgeNode edgeNode = ByonUtils.getEdgeNodeFromNC(nc);
                edgeService.handlePACloudDeletion(edgeNode);
                //remove ProActive JobId from edge device
                edgeNode.setJobId(EdgeDefinition.ANY_JOB_ID);
                repositoryService.saveEdgeNode(edgeNode);
                nc.setJobIdForEDGE(EdgeDefinition.ANY_JOB_ID);
                repositoryService.saveNodeCandidate(nc);
            }

            LOGGER.info("Cleanup completed for node {}", node.getName());

        } catch (Exception e) {
            LOGGER.error("Error occurred during cleanup for node {}: {}", node.getName(), e.getMessage());
            throw new RuntimeException("Error during cleanup for node " + node.getName(), e);
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
