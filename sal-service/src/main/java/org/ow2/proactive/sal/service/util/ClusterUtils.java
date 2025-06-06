/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.*;
import org.ow2.proactive.sal.model.*;

import lombok.extern.log4j.Log4j2;


@Log4j2
public class ClusterUtils {

    public static final String CLUSTER_TYPE_ENV = "CLUSTER_TYPE";

    public static final String CLUSTER_TYPE_K3S = "k3s";

    public static final String CLUSTER_TYPE_K8S = "k8s";

    private static final String SCRIPTS_PATH = "/usr/local/tomcat/scripts/";

    // TO be changed, the hardcoding of the ubuntu user is a bad practice.
    private static final String KUBE_LABEL_COMMAND = "kubectl label nodes --overwrite";

    private static final String CLI_USER_SELECTION = "sudo -H -u ubuntu bash -c";

    private static final String FILE_PATH = "/home/ubuntu/.profile";

    // K3s-related commands
    private static final String CLI_K3s_USER_SELECTION = "$dau bash -c";

    private static final String K3S_COMMANDS = "dau=\"sudo -H -E -u ubuntu\"\n" +
                                               "export KUBECONFIG=/etc/rancher/k3s/k3s.yaml\n" +
                                               "echo \"KUBECONFIG=${KUBECONFIG}\" | sudo tee -a /etc/environment\n";

    public static Job createMasterNodeJob(String clusterName, ClusterNodeDefinition masterNode, PACloud cloud,
            String envVars) throws IOException {
        Job masterNodeJob = new Job();
        masterNodeJob.setJobId(masterNode.getNodeJobName(clusterName));
        masterNodeJob.setName(masterNode.getNodeJobName(clusterName));
        Task masterNodeTask = createMasterNodeTask(clusterName, masterNode, cloud, envVars);
        List<Task> tasks = new LinkedList<>();
        tasks.add(masterNodeTask);
        masterNodeJob.setTasks(tasks);
        return masterNodeJob;
    }

    private static Task createMasterNodeTask(String clusterName, ClusterNodeDefinition masterNode, PACloud cloud,
            String envVars) throws IOException {
        Task masterNodeTask = new Task();
        masterNodeTask.setTaskId(masterNode.getNodeTaskName(clusterName));
        masterNodeTask.setName(masterNode.getNodeTaskName(clusterName));
        masterNodeTask.setType(Installation.InstallationType.COMMANDS);
        masterNodeTask.setInstallationByType(createMasterInstallation(envVars));
        masterNodeTask.setSecurityGroup(cloud.getSecurityGroup());
        return masterNodeTask;
    }

    private static CommandsInstallation createMasterInstallation(String envVars) throws IOException {
        CommandsInstallation masterInstallation = new CommandsInstallation();
        OperatingSystemType os = new OperatingSystemType();
        masterInstallation.setPreInstall(envVars + getBashFilesContent("MASTER_PRE_INSTALL_SCRIPT.sh"));
        masterInstallation.setInstall(getBashFilesContent("MASTER_INSTALL_SCRIPT.sh"));
        masterInstallation.setPostInstall(getBashFilesContent("MASTER_POST_INSTALL_SCRIPT.sh"));
        masterInstallation.setStart(String.format("source %s\n", FILE_PATH) +
                                    getBashFilesContent("MASTER_START_SCRIPT.sh"));
        masterInstallation.setStop(String.format("source %s\n", FILE_PATH) +
                                   getBashFilesContent("MASTER_STOP_SCRIPT.sh"));
        masterInstallation.setUpdateCmd(String.format("source %s\n", FILE_PATH) +
                                        getBashFilesContent("MASTER_UPDATE_SCRIPT.sh"));
        os.setOperatingSystemFamily("ubuntu");
        os.setOperatingSystemVersion((float) 22.04);
        masterInstallation.setOperatingSystemType(os);
        return masterInstallation;
    }

    public static Job createWorkerNodeJob(String clusterName, ClusterNodeDefinition workerNode, PACloud cloud,
            String envVars) throws IOException {
        Job workerNodeJob = new Job();
        workerNodeJob.setJobId(workerNode.getNodeJobName(clusterName));
        workerNodeJob.setName(workerNode.getNodeJobName(clusterName));
        Task workerNodeTask = createWorkerNodeTask(clusterName, workerNode, cloud, envVars);
        List<Task> tasks = new LinkedList<>();
        tasks.add(workerNodeTask);
        workerNodeJob.setTasks(tasks);
        return workerNodeJob;
    }

    private static Task createWorkerNodeTask(String clusterName, ClusterNodeDefinition workerNode, PACloud cloud,
            String envVars) throws IOException {
        Task workerNodeTask = new Task();
        workerNodeTask.setTaskId(workerNode.getNodeTaskName(clusterName));
        workerNodeTask.setName(workerNode.getNodeTaskName(clusterName));
        workerNodeTask.setType(Installation.InstallationType.COMMANDS);
        workerNodeTask.setInstallationByType(createWorkerInstallation(envVars));
        if (cloud != null && cloud.getSecurityGroup() != null && !cloud.getSecurityGroup().isEmpty()) {
            workerNodeTask.setSecurityGroup(cloud.getSecurityGroup());
        }
        return workerNodeTask;
    }

    private static CommandsInstallation createWorkerInstallation(String envVars) throws IOException {
        CommandsInstallation workerInstallation = new CommandsInstallation();
        OperatingSystemType os = new OperatingSystemType();
        workerInstallation.setPreInstall(envVars + getBashFilesContent("WORKER_PRE_INSTALL_SCRIPT.sh"));
        workerInstallation.setInstall(getBashFilesContent("WORKER_INSTALL_SCRIPT.sh"));
        workerInstallation.setPostInstall(getBashFilesContent("WORKER_POST_INSTALL_SCRIPT.sh"));
        workerInstallation.setStart(String.format("source %s\n", FILE_PATH) +
                                    getBashFilesContent("WORKER_START_SCRIPT.sh"));
        workerInstallation.setStop(String.format("source %s\n", FILE_PATH) +
                                   getBashFilesContent("WORKER_STOP_SCRIPT.sh"));
        workerInstallation.setUpdateCmd(String.format("source %s\n", FILE_PATH) +
                                        getBashFilesContent("WORKER_UPDATE_SCRIPT.sh"));
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

    public static List<ClusterNodeDefinition> getWorkerNodes(Cluster cluster) {
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
        IaasDefinition masterIaasDefinition = createIaasDefinition(node, nodeName + "-" + clusterName + "_Task");
        List<IaasDefinition> defs = new ArrayList<>();
        defs.add(masterIaasDefinition);
        return defs;
    }

    private static String getBashFilesContent(String fileName) throws IOException {
        String filePath = SCRIPTS_PATH + fileName;
        File file = new File(filePath);
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8);

    }

    public static String createLabelNodesScript(List<Map<String, String>> nodeLabels, String clusterName,
            String containerizationFlavor) {
        String clusterType = (containerizationFlavor != null) ? containerizationFlavor
                                                              : System.getenv(CLUSTER_TYPE_ENV);

        if (CLUSTER_TYPE_K3S.equalsIgnoreCase(clusterType)) {
            return createK3sLabelNodesScript(nodeLabels, clusterName);
        } else {
            return createK8sLabelNodesScript(nodeLabels, clusterName);
        }
    }

    public static String createK3sLabelNodesScript(List<Map<String, String>> nodeLabels, String clusterName) {
        StringBuilder script = new StringBuilder();
        script.append(K3S_COMMANDS).append("\n");
        for (Map<String, String> nodeLabelPair : nodeLabels) {
            for (String nodeName : nodeLabelPair.keySet()) {
                String label = nodeLabelPair.get(nodeName);
                script.append(String.format("%s '%s %s-%s %s' \n",
                                            CLI_K3s_USER_SELECTION,
                                            KUBE_LABEL_COMMAND,
                                            nodeName.toLowerCase(),
                                            clusterName,
                                            label));
            }
        }
        return script.toString();
    }

    public static String createK8sLabelNodesScript(List<Map<String, String>> nodeLabels, String clusterName) {
        StringBuilder script = new StringBuilder();
        for (Map<String, String> nodeLabelPair : nodeLabels) {
            for (String nodeName : nodeLabelPair.keySet()) {
                String label = nodeLabelPair.get(nodeName);
                script.append(String.format("%s '%s %s-%s %s' \n",
                                            CLI_USER_SELECTION,
                                            KUBE_LABEL_COMMAND,
                                            nodeName.toLowerCase(),
                                            clusterName,
                                            label));
            }
        }
        return script.toString();
    }

    public static String createDeployApplication(ClusterApplication application, String containerizationFlavor)
            throws IOException {
        String clusterType = (containerizationFlavor != null) ? containerizationFlavor
                                                              : System.getenv(CLUSTER_TYPE_ENV);
        return createDeployApplicationScript(application, clusterType);
    }

    public static String createDeployApplicationScript(ClusterApplication application, String clusterType)
            throws IOException {
        String fileName = "/home/ubuntu/" + application.getAppName() + ".yaml";
        application.setYamlManager(ClusterApplication.PackageManagerEnum.getPackageManagerEnumByName(application.getPackageManager()));
        String appCommand = createAppCommand(application.getYamlManager(), fileName, clusterType);

        if (appCommand == null) {
            LOGGER.error("\"{}\" is not supported!", application.getPackageManager());
            throw new IOException("yaml executor is not supported!");
        }

        StringBuilder script = new StringBuilder();
        script.append("sudo rm -f ").append(fileName).append(" || echo 'file was not found.' \n");

        // Start heredoc
        script.append("cat <<'EOF' >").append(fileName).append("\n");
        script.append(application.getAppFile()); // Embed the YAML file content
        script.append("\nEOF\n");

        script.append("sudo chown ubuntu:ubuntu ").append(fileName).append("\n");

        // Insert K3s-specific commands if needed
        if (CLUSTER_TYPE_K3S.equalsIgnoreCase(clusterType)) {
            script.append(K3S_COMMANDS).append("\n");
        }

        script.append(appCommand);
        return script.toString();
    }

    private static String createAppCommand(ClusterApplication.PackageManagerEnum yamlManager, String fileName,
            String clusterType) {
        if (yamlManager != null) {
            String cliSelection = CLUSTER_TYPE_K3S.equalsIgnoreCase(clusterType) ? CLI_K3s_USER_SELECTION
                                                                                 : CLI_USER_SELECTION;
            return String.format("%s '%s %s'", cliSelection, yamlManager.getCommand(), fileName);
        } else {
            LOGGER.error("The selected yaml executor is not supported!");
            return null;
        }
    }

    public static String createEnvVarsScript(Map<String, String> envVars) {
        String filePath = FILE_PATH;
        StringBuilder script = new StringBuilder();
        if (envVars == null || envVars.isEmpty()) {
            return "";
        }
        for (String key : envVars.keySet()) {
            script.append(String.format("echo 'export %s=\"%s\"' >> %s\n", key, envVars.get(key), filePath));
        }
        script.append(String.format("source %s\n", filePath));
        return script.toString();
    }

}
