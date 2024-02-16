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

import java.util.ArrayList;
import java.util.List;

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

    public boolean defineCluster(String sessionId, ClusterDefinition clusterDefinition) throws NotConnectedException {
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
            Job masterNodeJob = ClusterUtils.createMasterNodeJob(newCluster.getName(), masterNode);
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
                Job workerNodeJob = ClusterUtils.createWorkerNodeJob(newCluster.getName(), node);
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
        } else {
            List<ClusterNodeDefinition> workerNodes = ClusterUtils.getWrokerNodes(toDeployClutser);
            LOGGER.info("Deploying the master node of the cluster [{}]", toDeployClutser.getName());
            submitClutserNode(sessionId, toDeployClutser, toDeployClutser.getMasterNode());
            LOGGER.info("Deploying the worker nodes of the cluster [{}]", toDeployClutser.getName());
            for (ClusterNodeDefinition node : workerNodes) {
                submitClutserNode(sessionId, toDeployClutser, node.getName());
            }
        }
        return true;
    }

    private void submitClutserNode(String sessionId, Cluster cluster, String nodeName) throws NotConnectedException {
        LOGGER.info("Deploying the node {}...", nodeName);
        ClusterNodeDefinition node = ClusterUtils.getNodeByName(cluster, nodeName);
        if (node != null) {
            String jobId = node.getNodeJobName(cluster.getName());
            List<IaasDefinition> defs = ClusterUtils.getNodeIaasDefinition(sessionId, cluster, node.getName());
            nodeService.addNodes(sessionId, defs, jobId);

            // submit job
            jobService.submitJob(sessionId, jobId);
            LOGGER.info("Node {} is submitted for deployment", nodeName);
        } else {
            LOGGER.error("The node {} was not found in the cluster {} definition", nodeName, cluster.getName());
        }
    }

    public Cluster getCluster(String sessionId, String clusterName) {
        return ClusterUtils.getClusterByName(clusterName, repositoryService.listCluster());
    }

}
