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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.ow2.proactive.sal.model.*;
import org.ow2.proactive.sal.service.service.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.log4j.Log4j2;


@Log4j2
public class ClusterUtils {

    private static final String MASTER_PRE_INSTALL_SCRIPT = "echo \"Pre Install script\"";

    private static final String MASTER_INSTALL_SCRIPT = "wget https://raw.githubusercontent.com/alijawadfahs/scripts/main/nebulous/install-kube-u22.sh && chmod +x ./install-kube-u22.sh && ./install-kube-u22.sh\n";

    private static final String MASTER_POST_INSTALL_SCRIPT = "echo \"Post Install script\"";

    private static final String MASTER_START_SCRIPT = "sudo kubeadm init --pod-network-cidr=10.244.0.0/16\n" +
                                                      "echo \"HOME: $(pwd), USERE: $(id -u -n)\"\n" +
                                                      "mkdir -p ~/.kube && sudo cp -i /etc/kubernetes/admin.conf ~/.kube/config && sudo chown $(id -u):$(id -g) ~/.kube/config\n" +
                                                      "id -u ubuntu &> /dev/null\n" + "\n" + "if [[ $? -eq 0 ]]\n" +
                                                      "then\n" + "#USER ubuntu is found\n" +
                                                      "mkdir -p /home/ubuntu/.kube && sudo cp -i /etc/kubernetes/admin.conf /home/ubuntu/.kube/config && sudo chown ubuntu:ubuntu /home/ubuntu/.kube/config\n" +
                                                      "else\n" + "echo \"User Ubuntu is not found\"\n" + "fi\n" + "\n" +
                                                      "sudo -u ubuntu kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml;";

    private static final String MASTER_STOP_SCRIPT = "echo \"Stop Install script\"";

    private static final String MASTER_UPDATE_SCRIPT = "echo \"Update Install script\"";

    private static final String WORKER_PRE_INSTALL_SCRIPT = "echo \"Pre Install script\"";

    private static final String WORKER_INSTALL_SCRIPT = "wget https://raw.githubusercontent.com/alijawadfahs/scripts/main/nebulous/install-kube-u22.sh && chmod +x ./install-kube-u22.sh && ./install-kube-u22.sh\n";

    private static final String WORKER_POST_INSTALL_SCRIPT = "echo \"Post Install script\"";

    private static final String WORKER_START_SCRIPT = "echo $variables_kubeCommand\n" + "sudo $variables_kubeCommand";

    private static final String WORKER_STOP_SCRIPT = "echo \"Stop Install script\"";

    private static final String WORKER_UPDATE_SCRIPT = "echo \"Update Install script\"";

    @Autowired
    private RepositoryService repositoryService;

    public static Job createMasterNodeJob(String clusterName, ClusterNodeDefinition masterNode, PACloud cloud) {
        Job masterNodeJob = new Job();
        masterNodeJob.setJobId(masterNode.getNodeJobName(clusterName));
        masterNodeJob.setName(masterNode.getNodeJobName(clusterName));
        Task masterNodeTask = createMasterNodeTask(clusterName, masterNode, cloud);
        List<Task> tasks = new LinkedList<>();
        tasks.add(masterNodeTask);
        masterNodeJob.setTasks(tasks);
        return masterNodeJob;
    }

    private static Task createMasterNodeTask(String clusterName, ClusterNodeDefinition masterNode, PACloud cloud) {
        Task masterNodeTask = new Task();
        masterNodeTask.setTaskId(masterNode.getNodeTaskName(clusterName));
        masterNodeTask.setName(masterNode.getNodeTaskName(clusterName));
        masterNodeTask.setType(Installation.InstallationType.COMMANDS);
        masterNodeTask.setInstallationByType(createMasterInstallation());
        masterNodeTask.setSecurityGroup(cloud.getSecurityGroup());
        return masterNodeTask;
    }

    private static CommandsInstallation createMasterInstallation() {
        CommandsInstallation masterInstallation = new CommandsInstallation();
        OperatingSystemType os = new OperatingSystemType();
        masterInstallation.setPreInstall(MASTER_PRE_INSTALL_SCRIPT);
        masterInstallation.setInstall(MASTER_INSTALL_SCRIPT);
        masterInstallation.setPostInstall(MASTER_POST_INSTALL_SCRIPT);
        masterInstallation.setStart(MASTER_START_SCRIPT);
        masterInstallation.setStop(MASTER_STOP_SCRIPT);
        masterInstallation.setUpdateCmd(MASTER_UPDATE_SCRIPT);
        os.setOperatingSystemFamily("ubuntu");
        os.setOperatingSystemVersion((float) 22.04);
        masterInstallation.setOperatingSystemType(os);
        return masterInstallation;
    }

    public static Job createWorkerNodeJob(String clusterName, ClusterNodeDefinition workerNode, PACloud cloud) {
        Job workerNodeJob = new Job();
        workerNodeJob.setJobId(workerNode.getNodeJobName(clusterName));
        workerNodeJob.setName(workerNode.getNodeJobName(clusterName));
        Task workerNodeTask = createWorkerNodeTask(clusterName, workerNode, cloud);
        List<Task> tasks = new LinkedList<>();
        tasks.add(workerNodeTask);
        workerNodeJob.setTasks(tasks);
        return workerNodeJob;
    }

    private static Task createWorkerNodeTask(String clusterName, ClusterNodeDefinition workerNode, PACloud cloud) {
        Task workerNodeTask = new Task();
        workerNodeTask.setTaskId(workerNode.getNodeTaskName(clusterName));
        workerNodeTask.setName(workerNode.getNodeTaskName(clusterName));
        workerNodeTask.setType(Installation.InstallationType.COMMANDS);
        workerNodeTask.setInstallationByType(createWorkerInstallation());
        workerNodeTask.setSecurityGroup(cloud.getSecurityGroup());
        return workerNodeTask;
    }

    private static CommandsInstallation createWorkerInstallation() {
        CommandsInstallation workerInstallation = new CommandsInstallation();
        OperatingSystemType os = new OperatingSystemType();
        workerInstallation.setPreInstall(WORKER_PRE_INSTALL_SCRIPT);
        workerInstallation.setInstall(WORKER_INSTALL_SCRIPT);
        workerInstallation.setPostInstall(WORKER_POST_INSTALL_SCRIPT);
        workerInstallation.setStart(WORKER_START_SCRIPT);
        workerInstallation.setStop(WORKER_STOP_SCRIPT);
        workerInstallation.setUpdateCmd(WORKER_UPDATE_SCRIPT);
        os.setOperatingSystemFamily("ubuntu");
        os.setOperatingSystemVersion((float) 22.04);
        workerInstallation.setOperatingSystemType(os);
        return workerInstallation;
    }

    public static IaasDefinition createIaasDefinition(ClusterNodeDefinition node, String taskname) {
        IaasDefinition newIaasDefinition = new IaasDefinition();
        newIaasDefinition.setName(node.getName());
        newIaasDefinition.setCloudId(node.getCloudId());
        newIaasDefinition.setNodeCandidateId(node.getNodeCandidateId());
        newIaasDefinition.setTaskName(taskname);

        return newIaasDefinition;
    }

    public static ClusterNodeDefinition getNodeByName(Cluster cluster, String nodeName) {
        for (ClusterNodeDefinition node : cluster.getNodes()) {
            if (node.getName().equals(nodeName)) {
                return node;
            }
        }
        return null;
    }

    public static Cluster getClusterByName(String clusterName, List<Cluster> clusters) {
        for (Cluster cluster : clusters) {
            if (cluster.getName().equals(clusterName)) {
                return cluster;
            }
        }
        LOGGER.error("No cluster was found with the name: " + clusterName);
        return null;
    }

    public static List<ClusterNodeDefinition> getWrokerNodes(Cluster cluster) {
        String masterNodeName = cluster.getMasterNode();
        List<ClusterNodeDefinition> workerNodes = new ArrayList<>();
        for (ClusterNodeDefinition node : cluster.getNodes()) {
            if (!node.getName().equals(masterNodeName)) {
                workerNodes.add(node);
            }
        }
        return workerNodes;
    }

    public static List<IaasDefinition> getNodeIaasDefinition(String sessionId, Cluster cluster, String nodeName) {
        ClusterNodeDefinition node = getNodeByName(cluster, nodeName);
        String clusterName = cluster.getName();
        IaasDefinition masterIaasDefinition = createIaasDefinition(node, nodeName + "_" + clusterName + "_Task");
        List<IaasDefinition> defs = new ArrayList<>();
        defs.add(masterIaasDefinition);
        return defs;
    }
}
