/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.ow2.proactive.sal.model.*;
import org.ow2.proactive.sal.service.nc.WhiteListedInstanceTypesUtils;
import org.ow2.proactive.sal.service.service.application.PAFactory;
import org.ow2.proactive.sal.service.util.ByonUtils;
import org.ow2.proactive.sal.service.util.ClusterUtils;
import org.ow2.proactive.sal.service.util.Utils;
import org.ow2.proactive.scheduler.common.task.ScriptTask;
import org.ow2.proactive.scheduler.common.task.TaskVariable;
import org.ow2.proactive.scripting.*;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import lombok.extern.log4j.Log4j2;


@Log4j2
@Service("TaskBuilder")
public class TaskBuilder {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private static final String SCRIPTS_SEPARATION_BASH = NEW_LINE + NEW_LINE + "# Separation script" + NEW_LINE +
                                                          NEW_LINE;

    private static final String EMS_DEPLOY_PRE_SCRIPT = "emsdeploy_prescript.sh";

    private static final String EMS_DEPLOY_PRIVATE_PRE_SCRIPT = "emsdeploy_prescript_private.sh";

    private static final String EMS_DEPLOY_MAIN_SCRIPT = "emsdeploy_mainscript.groovy";

    private static final String EMS_DEPLOY_POST_SCRIPT = "emsdeploy_postscript.sh";

    private static final String EXPORT_ENV_VAR_SCRIPT = "export_env_var_script.sh";

    private static final String COLLECT_IP_ADDR_TO_ENV_VARS_SCRIPT = "collect_ip_addr_to_env_vars_script.groovy";

    private static final String START_DOCKER_APP_SCRIPT = "start_docker_app.sh";

    private static final String CHECK_NODE_SOURCE_REGEXP_SCRIPT = "check_node_source_regexp.groovy";

    private static final String ACQUIRE_NODE_SCRIPT = "acquire_node_script.groovy";

    private static final String REMOVE_NODE_SCRIPT = "remove_node_script.groovy";

    private static final String PRE_ACQUIRE_NODE_SCRIPT = "pre_acquire_node_script.groovy";

    private static final String ACQUIRE_NODE_BYON_SCRIPT = "acquire_node_BYON_script.groovy";

    private static final String POST_PREPARE_INFRA_SCRIPT = "post_prepare_infra_script.groovy";

    private static final String PREPARE_INFRA_SCRIPT = "prepare_infra_script.sh";

    private static final String PROVIDED_PORTS_VARIABLE_NAME = "providedPorts";

    private static final String INIT_SYNC_CHANNELS_SCRIPT = "init_synchronization_channels_script.groovy";

    private static final String CLEAN_SYNC_CHANNELS_SCRIPT = "clean_synchronization_channels_script.groovy";

    private static final String WAIT_FOR_LOCK_SCRIPT = "wait_for_lock_script.sh";

    private static final String DRIAN_NODE_SCRIPT = "drain_node_script.sh";

    private static final String WAIT_FOR_MASTER_SCRIPT = "wait_for_master.groovy";

    private static final String WAIT_FOR_MASTER_K3S_SCRIPT = "wait_for_master_k3s.groovy";

    private static final String SET_TOKEN_SCRIPT = "set_token.groovy";

    private static final String DELETE_NODE_SCRIPT = "delete_node.groovy";

    private static final String NODE_SOURCE_NAME_REGEX = "^local$|^Default$|^LocalNodes$|^Server-Static-Nodes$|^On-Prem-Server-Static-Nodes$";

    private static final String COMPONENT_NAME_VARIABLE_NAME = "ComponentName";

    private ScriptTask createEmsDeploymentTask(EmsDeploymentRequest emsDeploymentRequest, String taskNameSuffix,
            String nodeToken) {
        LOGGER.debug("Preparing EMS deployment task");
        String preScriptFileName = EMS_DEPLOY_PRE_SCRIPT;
        if (emsDeploymentRequest.isPrivateIP()) {
            preScriptFileName = EMS_DEPLOY_PRIVATE_PRE_SCRIPT;
        }
        ScriptTask emsDeploymentTask = PAFactory.createComplexScriptTaskFromFiles("emsDeployment" + taskNameSuffix,
                                                                                  EMS_DEPLOY_MAIN_SCRIPT,
                                                                                  "groovy",
                                                                                  preScriptFileName,
                                                                                  "bash",
                                                                                  EMS_DEPLOY_POST_SCRIPT,
                                                                                  "bash");
        Map<String, TaskVariable> variablesMap = emsDeploymentRequest.getWorkflowMap();
        emsDeploymentTask.addGenericInformation("NODE_ACCESS_TOKEN", nodeToken);
        emsDeploymentTask.setVariables(variablesMap);
        return emsDeploymentTask;
    }

    private List<ScriptTask> createAppTasks(Task task, String taskNameSuffix, String taskToken) {
        LOGGER.debug("Creating app PA task: {}, with Type: {} ", task.getTaskId(), task.getType());
        switch (Objects.requireNonNull(task.getType())) {
            case COMMANDS:
                return createCommandsTask(task, taskNameSuffix, taskToken);
            case DOCKER:
                return createDockerTask(task, taskNameSuffix, taskToken);
            default:
                return new LinkedList<>();
        }
    }

    private List<ScriptTask> createDockerTask(Task task, String taskNameSuffix, String taskToken) {
        List<ScriptTask> scriptTasks = new LinkedList<>();
        ScriptTask scriptTask = PAFactory.createBashScriptTask(task.getName() + "_start" +
                                                               taskNameSuffix,
                                                               Utils.getContentWithFileName(EXPORT_ENV_VAR_SCRIPT) +
                                                                               SCRIPTS_SEPARATION_BASH +
                                                                               Utils.getContentWithFileName(START_DOCKER_APP_SCRIPT));
        scriptTask.setPreScript(PAFactory.createSimpleScriptFromFIle(COLLECT_IP_ADDR_TO_ENV_VARS_SCRIPT, "groovy"));
        Map<String, TaskVariable> taskVariablesMap = new HashMap<>();
        taskVariablesMap.put(COMPONENT_NAME_VARIABLE_NAME,
                             new TaskVariable(COMPONENT_NAME_VARIABLE_NAME, task.getName()));

        taskVariablesMap.put("INSTANCE_NAME", new TaskVariable("INSTANCE_NAME", task.getTaskId() + "-$PA_JOB_ID"));
        taskVariablesMap.put("DOCKER_IMAGE", new TaskVariable("DOCKER_IMAGE", task.getEnvironment().getDockerImage()));
        taskVariablesMap.put("PORTS", new TaskVariable("PORTS", task.getEnvironment().getPort()));
        taskVariablesMap.put("ENV_VARS",
                             new TaskVariable("ENV_VARS", task.getEnvironment().getEnvVarsAsCommandString()));
        scriptTask.setVariables(taskVariablesMap);
        scriptTask.addGenericInformation("NODE_ACCESS_TOKEN", taskToken);
        scriptTasks.add(scriptTask);
        return scriptTasks;
    }

    private List<ScriptTask> createCommandsTask(Task task, String taskNameSuffix, String taskToken) {
        List<ScriptTask> scriptTasks = new LinkedList<>();
        ScriptTask scriptTaskStart = null;
        ScriptTask scriptTaskInstall = null;

        Map<String, TaskVariable> taskVariablesMap = new HashMap<>();
        taskVariablesMap.put(COMPONENT_NAME_VARIABLE_NAME,
                             new TaskVariable(COMPONENT_NAME_VARIABLE_NAME, task.getName()));

        if (!(Strings.isNullOrEmpty(task.getInstallation().getInstall()) &&
              Strings.isNullOrEmpty(task.getInstallation().getPreInstall()) &&
              Strings.isNullOrEmpty(task.getInstallation().getPostInstall()))) {
            String implementationScript;
            if (!Strings.isNullOrEmpty(task.getInstallation().getInstall())) {
                implementationScript = task.getInstallation().getInstall();
            } else {
                implementationScript = "echo \"Installation script is empty. Nothing to be executed.\"";
            }

            if (!Strings.isNullOrEmpty(task.getInstallation().getPreInstall())) {
                implementationScript = task.getInstallation().getPreInstall() + SCRIPTS_SEPARATION_BASH +
                                       implementationScript;
            }
            if (!Strings.isNullOrEmpty(task.getInstallation().getPostInstall())) {
                implementationScript = implementationScript + SCRIPTS_SEPARATION_BASH +
                                       task.getInstallation().getPostInstall();
            }
            implementationScript = Utils.getContentWithFileName(EXPORT_ENV_VAR_SCRIPT) + SCRIPTS_SEPARATION_BASH +
                                   implementationScript;

            scriptTaskInstall = PAFactory.createBashScriptTask(task.getName() + "_install" + taskNameSuffix,
                                                               implementationScript);
            scriptTaskInstall.setPreScript(PAFactory.createSimpleScriptFromFIle(COLLECT_IP_ADDR_TO_ENV_VARS_SCRIPT,
                                                                                "groovy"));
            scriptTaskInstall.setVariables(taskVariablesMap);
            scriptTaskInstall.addGenericInformation("NODE_ACCESS_TOKEN", taskToken);
            scriptTasks.add(scriptTaskInstall);
        }

        if (!(Strings.isNullOrEmpty(task.getInstallation().getStart()) &&
              Strings.isNullOrEmpty(task.getInstallation().getPreStart()) &&
              Strings.isNullOrEmpty(task.getInstallation().getPostStart()))) {
            String implementationScript;
            if (!Strings.isNullOrEmpty(task.getInstallation().getStart())) {
                implementationScript = task.getInstallation().getStart();
            } else {
                implementationScript = "echo \"Starting script is empty. Nothing to be executed.\"";
            }

            if (!Strings.isNullOrEmpty(task.getInstallation().getPreStart())) {
                implementationScript = task.getInstallation().getPreStart() + SCRIPTS_SEPARATION_BASH +
                                       implementationScript;
            }
            if (!Strings.isNullOrEmpty(task.getInstallation().getPostStart())) {
                implementationScript = implementationScript + SCRIPTS_SEPARATION_BASH +
                                       task.getInstallation().getPostStart();
            }

            implementationScript = Utils.getContentWithFileName(EXPORT_ENV_VAR_SCRIPT) + SCRIPTS_SEPARATION_BASH +
                                   implementationScript;

            scriptTaskStart = PAFactory.createBashScriptTask(task.getName() + "_start" + taskNameSuffix,
                                                             implementationScript);
            scriptTaskStart.setPreScript(PAFactory.createSimpleScriptFromFIle(COLLECT_IP_ADDR_TO_ENV_VARS_SCRIPT,
                                                                              "groovy"));
            scriptTaskStart.setVariables(taskVariablesMap);
            if (scriptTaskInstall != null) {
                scriptTaskStart.addDependence(scriptTaskInstall);
            }
            scriptTaskStart.addGenericInformation("NODE_ACCESS_TOKEN", taskToken);
            scriptTasks.add(scriptTaskStart);
        }
        return scriptTasks;
    }

    private ScriptTask createInfraTask(Task task, Deployment deployment, String taskNameSuffix, String nodeToken) {
        switch (deployment.getDeploymentType()) {
            case IAAS:
                return createInfraIAASTask(task, deployment, taskNameSuffix, nodeToken);
            case BYON:
            case EDGE:
                return createInfraBYONandEDGETask(task, deployment, taskNameSuffix, nodeToken);
            default:
                return new ScriptTask();
        }
    }

    private void addLocalDefaultNSRegexSelectionScript(ScriptTask scriptTask) {
        try {
            String[] nodeSourceNameRegex = { NODE_SOURCE_NAME_REGEX };
            SelectionScript selectionScript = new SelectionScript(Utils.getContentWithFileName(CHECK_NODE_SOURCE_REGEXP_SCRIPT),
                                                                  "groovy",
                                                                  nodeSourceNameRegex,
                                                                  true);
            scriptTask.setSelectionScript(selectionScript);
        } catch (InvalidScriptException e) {
            LOGGER.warn("Selection script could not have been added.");
        }
    }

    private String createIAASNodeConfigJson(Task task, Deployment deployment) {
        String nodeConfigJson = "{";
        ObjectMapper mapper = new ObjectMapper();
        String imageId;
        switch (deployment.getPaCloud().getCloudProvider()) {
            case AWS_EC2:
                // imageId
                if (WhiteListedInstanceTypesUtils.isHandledHardwareInstanceType(deployment.getNode()
                                                                                          .getNodeCandidate()
                                                                                          .getHardware()
                                                                                          .getProviderId())) {
                    imageId = deployment.getNode().getNodeCandidate().getImage().getProviderId();
                } else {
                    imageId = deployment.getNode().getNodeCandidate().getLocation().getName() + "/" +
                              deployment.getNode().getNodeCandidate().getImage().getProviderId();
                }
                nodeConfigJson += "\"image\": \"" + imageId + "\"";
                // vmType
                nodeConfigJson += ", \"vmType\": \"" +
                                  deployment.getNode().getNodeCandidate().getHardware().getProviderId() + "\"";
                break;
            case OPENSTACK:
                // imageId
                nodeConfigJson += "\"image\": \"" + deployment.getNode().getNodeCandidate().getImage().getProviderId() +
                                  "\"";
                // vmType
                nodeConfigJson += ", \"vmType\": \"" +
                                  deployment.getNode().getNodeCandidate().getHardware().getProviderId() + "\"";
                break;
            case AZURE:
                // imageId
                nodeConfigJson += "\"image\": \"" + deployment.getNode().getNodeCandidate().getImage().getId() + "\"";
                // vmSizeType
                nodeConfigJson += ", \"vmSizeType\": \"" +
                                  deployment.getNode().getNodeCandidate().getHardware().getProviderId() + "\"";
                break;
            default:
                // imageId
                nodeConfigJson += "\"image\": \"" + deployment.getNode().getNodeCandidate().getImage().getProviderId() +
                                  "\"";
                // vmType
                nodeConfigJson += ", \"vmType\": \"" +
                                  deployment.getNode().getNodeCandidate().getHardware().getProviderId() + "\"";
        }

        // nodeTags
        nodeConfigJson += ", \"nodeTags\": \"" + deployment.getNodeName() + "\"";

        // portsToOpen
        if (task.getPortsToOpen() != null && !task.getPortsToOpen().isEmpty()) {
            try {
                nodeConfigJson += ", \"portsToOpen\": " + mapper.writeValueAsString(task.getPortsToOpen());
            } catch (IOException e) {
                LOGGER.error(Arrays.toString(e.getStackTrace()));
            }
        }

        // securityGroups
        if (task.getSecurityGroup() != null) {
            nodeConfigJson += ", \"securityGroups\": [\"" + task.getSecurityGroup() + "\"]";
        }
        nodeConfigJson += "}";
        return (nodeConfigJson);
    }

    private Map<String, TaskVariable> createVariablesMapForAcquiringIAASNode(Task task, Deployment deployment,
            String nodeToken) {
        Map<String, TaskVariable> variablesMap = new HashMap<>();
        if (WhiteListedInstanceTypesUtils.isHandledHardwareInstanceType(deployment.getNode()
                                                                                  .getNodeCandidate()
                                                                                  .getHardware()
                                                                                  .getProviderId())) {
            variablesMap.put("NS_name",
                             new TaskVariable("NS_name",
                                              PACloud.WHITE_LISTED_NAME_PREFIX +
                                                         deployment.getPaCloud().getNodeSourceNamePrefix() + "-" +
                                                         deployment.getNode()
                                                                   .getNodeCandidate()
                                                                   .getLocation()
                                                                   .getName()));
        } else {
            variablesMap.put("NS_name",
                             new TaskVariable("NS_name",
                                              deployment.getPaCloud().getNodeSourceNamePrefix() + "-" +
                                                         deployment.getNode()
                                                                   .getNodeCandidate()
                                                                   .getLocation()
                                                                   .getName()));
        }
        variablesMap.put("nVMs", new TaskVariable("nVMs", "1", "PA:Integer", false));
        variablesMap.put("synchronous", new TaskVariable("synchronous", "true", "PA:Boolean", false));
        variablesMap.put("timeout", new TaskVariable("timeout", "700", "PA:Long", false));
        String nodeConfigJson = createIAASNodeConfigJson(task, deployment);
        variablesMap.put("nodeConfigJson", new TaskVariable("nodeConfigJson", nodeConfigJson, "PA:JSON", false));
        variablesMap.put("token", new TaskVariable("token", nodeToken));

        return (variablesMap);
    }

    private ScriptTask createInfraIAASTaskForCloudProvider(Task task, Deployment deployment, String taskNameSuffix,
            String nodeToken, String cloudProvider) {
        LOGGER.debug("Acquiring node " + cloudProvider + " script file: " +
                     getClass().getResource(File.separator + ACQUIRE_NODE_SCRIPT).toString());
        ScriptTask deployNodeTask = PAFactory.createGroovyScriptTaskFromFile("acquire" + cloudProvider + "Node_" +
                                                                             task.getName() + taskNameSuffix,
                                                                             ACQUIRE_NODE_SCRIPT);

        deployNodeTask.setPreScript(PAFactory.createSimpleScriptFromFIle(PRE_ACQUIRE_NODE_SCRIPT, "groovy"));

        Map<String, TaskVariable> variablesMap = createVariablesMapForAcquiringIAASNode(task, deployment, nodeToken);
        LOGGER.debug("Variables to be added to the task acquiring " + cloudProvider + " IAAS node: " +
                     variablesMap.toString());
        deployNodeTask.setVariables(variablesMap);

        addLocalDefaultNSRegexSelectionScript(deployNodeTask);

        return deployNodeTask;
    }

    private ScriptTask createInfraIAASTask(Task task, Deployment deployment, String taskNameSuffix, String nodeToken) {
        switch (deployment.getPaCloud().getCloudProvider()) {
            case AWS_EC2:
                return createInfraIAASTaskForCloudProvider(task, deployment, taskNameSuffix, nodeToken, "AWS");
            case OPENSTACK:
                return createInfraIAASTaskForCloudProvider(task, deployment, taskNameSuffix, nodeToken, "OS");
            case AZURE:
                return createInfraIAASTaskForCloudProvider(task, deployment, taskNameSuffix, nodeToken, "Azure");
            case GCE:
                return createInfraIAASTaskForCloudProvider(task, deployment, taskNameSuffix, nodeToken, "GCE");
            default:
                return new ScriptTask();
        }
    }

    private ScriptTask createInfraBYONandEDGETask(Task task, Deployment deployment, String taskNameSuffix,
            String nodeToken) {
        String nodeType = deployment.getDeploymentType().getName();
        LOGGER.info("the nodeType name is: " + nodeType);
        LOGGER.debug("Acquiring node " + nodeType + " script file: " +
                     getClass().getResource(File.separator + ACQUIRE_NODE_BYON_SCRIPT).toString());
        ScriptTask deployNodeTask = PAFactory.createGroovyScriptTaskFromFile("acquire" + nodeType + "Node_" +
                                                                             task.getName() + taskNameSuffix,
                                                                             ACQUIRE_NODE_BYON_SCRIPT);

        deployNodeTask.setPreScript(PAFactory.createSimpleScriptFromFIle(PRE_ACQUIRE_NODE_SCRIPT, "groovy"));

        Map<String, TaskVariable> variablesMap = new HashMap<>();
        String NsName = deployment.getPaCloud().getNodeSourceNamePrefix();
        variablesMap.put("NS_name", new TaskVariable("NS_name", NsName));
        variablesMap.put("host_name", new TaskVariable("host_name", ByonUtils.getBYONHostname(NsName)));
        variablesMap.put("token", new TaskVariable("token", nodeToken));

        LOGGER.debug("Variables to be added to the task: " + variablesMap.toString());
        deployNodeTask.setVariables(variablesMap);

        addLocalDefaultNSRegexSelectionScript(deployNodeTask);

        return deployNodeTask;
    }

    private List<ScriptTask> createChildScaledTask(Task task) {
        List<ScriptTask> scriptTasks = new LinkedList<>();
        task.getDeployments().stream().filter(Deployment::getIsDeployed).forEach(deployment -> {
            // Creating infra deployment tasks
            String token = task.getTaskId() + deployment.getNumber();
            String suffix = "_" + deployment.getNumber();
            scriptTasks.add(createScalingChildSaveTask(task, suffix, token));
        });
        task.setDeploymentFirstSubmittedTaskName(scriptTasks.get(0)
                                                            .getName()
                                                            .substring(0,
                                                                       scriptTasks.get(0).getName().lastIndexOf("_")));
        task.setDeploymentLastSubmittedTaskName(scriptTasks.get(0)
                                                           .getName()
                                                           .substring(0,
                                                                      scriptTasks.get(0).getName().lastIndexOf("_")));
        return scriptTasks;
    }

    private ScriptTask createScalingChildSaveTask(Task task, String suffix, String token) {
        ScriptTask scriptTaskUpdate = null;
        Map<String, TaskVariable> taskVariablesMap = new HashMap<>();
        taskVariablesMap.put(COMPONENT_NAME_VARIABLE_NAME,
                             new TaskVariable(COMPONENT_NAME_VARIABLE_NAME, task.getName()));

        if (task.getType() == Installation.InstallationType.COMMANDS &&
            !Strings.isNullOrEmpty(task.getInstallation().getUpdateCmd())) {
            scriptTaskUpdate = PAFactory.createBashScriptTask(task.getName() + "_update" +
                                                              suffix,
                                                              Utils.getContentWithFileName(EXPORT_ENV_VAR_SCRIPT) +
                                                                      SCRIPTS_SEPARATION_BASH +
                                                                      task.getInstallation().getUpdateCmd());
        } else {
            scriptTaskUpdate = PAFactory.createBashScriptTask(task.getName() + "_update" + suffix,
                                                              "echo \"Update script is empty. Nothing to be executed.\"");
        }

        scriptTaskUpdate.setPreScript(PAFactory.createSimpleScriptFromFIle(COLLECT_IP_ADDR_TO_ENV_VARS_SCRIPT,
                                                                           "groovy"));

        scriptTaskUpdate.setVariables(taskVariablesMap);
        scriptTaskUpdate.addGenericInformation("NODE_ACCESS_TOKEN", token);

        return scriptTaskUpdate;
    }

    private List<ScriptTask> buildScaledPATask(Task task) {
        List<ScriptTask> scriptTasks = new LinkedList<>();

        task.getDeployments().stream().filter(Deployment::getIsDeployed).forEach(deployment -> {
            String token = task.getTaskId() + deployment.getNumber();
            String suffix = "_" + deployment.getNumber();

            // Creating infra preparation task
            scriptTasks.add(createInfraPreparationTask(task, suffix, token));
        });

        task.setDeploymentLastSubmittedTaskName(scriptTasks.get(0)
                                                           .getName()
                                                           .substring(0,
                                                                      scriptTasks.get(0).getName().lastIndexOf("_")));
        task.setDeploymentFirstSubmittedTaskName(scriptTasks.get(0)
                                                            .getName()
                                                            .substring(0,
                                                                       scriptTasks.get(0).getName().lastIndexOf("_")));

        task.getDeployments().stream().filter(deployment -> !deployment.getIsDeployed()).forEach(deployment -> {
            // Creating infra deployment tasks
            String token = task.getTaskId() + deployment.getNumber();
            String suffix = "_" + deployment.getNumber();
            scriptTasks.add(createInfraTask(task, deployment, suffix, token));
            task.setDeploymentFirstSubmittedTaskName(scriptTasks.get(scriptTasks.size() - 1)
                                                                .getName()
                                                                .substring(0,
                                                                           scriptTasks.get(scriptTasks.size() - 1)
                                                                                      .getName()
                                                                                      .lastIndexOf("_")));
            // If the infrastructure comes with the deployment of the EMS, we set it up.
            Optional.ofNullable(deployment.getEmsDeployment()).ifPresent(emsDeploymentRequest -> {
                String emsTaskSuffix = "_" + task.getName() + suffix;
                ScriptTask emsScriptTask = createEmsDeploymentTask(emsDeploymentRequest, emsTaskSuffix, token);
                emsScriptTask.addDependence(scriptTasks.get(scriptTasks.size() - 1));
                scriptTasks.add(emsScriptTask);
            });
            LOGGER.info("Token added: " + token);
            deployment.setIsDeployed(true);
            deployment.setNodeAccessToken(token);

            // Creating application deployment tasks
            createAndAddAppDeploymentTasks(task, suffix, token, scriptTasks);

        });

        scriptTasks.forEach(scriptTask -> task.addSubmittedTaskName(scriptTask.getName()));

        return scriptTasks;
    }

    private void createAndAddAppDeploymentTasks(Task task, String suffix, String token, List<ScriptTask> scriptTasks) {
        List<ScriptTask> appTasks = createAppTasks(task, suffix, token);
        task.setDeploymentLastSubmittedTaskName(appTasks.get(appTasks.size() - 1)
                                                        .getName()
                                                        .substring(0,
                                                                   appTasks.get(appTasks.size() - 1)
                                                                           .getName()
                                                                           .lastIndexOf(suffix)));

        // Creating infra preparation task
        appTasks.add(0, createInfraPreparationTask(task, suffix, token));
        appTasks.get(1).addDependence(appTasks.get(0));

        // Add dependency between infra and application deployment tasks
        appTasks.get(0).addDependence(scriptTasks.get(scriptTasks.size() - 1));

        scriptTasks.addAll(appTasks);
    }

    private List<ScriptTask> createParentScaledTask(Task task) {
        List<ScriptTask> scriptTasks = new LinkedList<>();
        task.getDeployments().stream().filter(Deployment::getIsDeployed).forEach(deployment -> {
            // Creating infra deployment tasks
            String token = task.getTaskId() + deployment.getNumber();
            String suffix = "_" + deployment.getNumber();
            scriptTasks.add(createScalingParentInfraPreparationTask(task, suffix, token));
        });
        task.setDeploymentFirstSubmittedTaskName(scriptTasks.get(0)
                                                            .getName()
                                                            .substring(0,
                                                                       scriptTasks.get(0).getName().lastIndexOf("_")));
        task.setDeploymentLastSubmittedTaskName(scriptTasks.get(0)
                                                           .getName()
                                                           .substring(0,
                                                                      scriptTasks.get(0).getName().lastIndexOf("_")));
        return scriptTasks;
    }

    private ScriptTask createScalingParentInfraPreparationTask(Task task, String suffix, String token) {
        ScriptTask prepareInfraTask;
        Map<String, TaskVariable> taskVariablesMap = new HashMap<>();
        String taskName = "parentPrepareInfra_" + task.getName() + suffix;
        taskVariablesMap.put(COMPONENT_NAME_VARIABLE_NAME,
                             new TaskVariable(COMPONENT_NAME_VARIABLE_NAME, task.getName()));

        if (!task.getPortsToOpen().isEmpty()) {
            prepareInfraTask = PAFactory.createBashScriptTaskFromFile(taskName, PREPARE_INFRA_SCRIPT);
            prepareInfraTask.setPostScript(PAFactory.createSimpleScriptFromFIle(POST_PREPARE_INFRA_SCRIPT, "groovy"));
            taskVariablesMap.put(PROVIDED_PORTS_VARIABLE_NAME,
                                 new TaskVariable(PROVIDED_PORTS_VARIABLE_NAME,
                                                  task.serializePortsToOpenToVariableMap()));
        } else {
            prepareInfraTask = PAFactory.createBashScriptTask(taskName,
                                                              "echo \"No ports to open and not parent tasks. Nothing to be prepared in VM.\"");
        }

        prepareInfraTask.setVariables(taskVariablesMap);
        prepareInfraTask.addGenericInformation("NODE_ACCESS_TOKEN", token);

        return prepareInfraTask;
    }

    private ScriptTask createInfraPreparationTask(Task task, String suffix, String token) {
        ScriptTask prepareInfraTask;
        Map<String, TaskVariable> taskVariablesMap = new HashMap<>();
        String taskName = "prepareInfra_" + task.getName() + suffix;
        taskVariablesMap.put(COMPONENT_NAME_VARIABLE_NAME,
                             new TaskVariable(COMPONENT_NAME_VARIABLE_NAME, task.getName()));

        if (!task.getPortsToOpen().isEmpty()) {
            prepareInfraTask = PAFactory.createBashScriptTaskFromFile(taskName, PREPARE_INFRA_SCRIPT);
            prepareInfraTask.setPostScript(PAFactory.createSimpleScript(Utils.getContentWithFileName(POST_PREPARE_INFRA_SCRIPT),
                                                                        "groovy"));
            taskVariablesMap.put(PROVIDED_PORTS_VARIABLE_NAME,
                                 new TaskVariable(PROVIDED_PORTS_VARIABLE_NAME,
                                                  task.serializePortsToOpenToVariableMap()));
        } else {
            prepareInfraTask = PAFactory.createBashScriptTask(taskName,
                                                              "echo \"No ports to open and not parent tasks. Nothing to be prepared in VM.\"");
        }

        if (task.getType() == Installation.InstallationType.COMMANDS) {
            if (task.getInstallation()
                    .getOperatingSystemType()
                    .getOperatingSystemFamily()
                    .toLowerCase(Locale.ROOT)
                    .equals("ubuntu") &&
                task.getInstallation().getOperatingSystemType().getOperatingSystemVersion() < 2000) {
                LOGGER.info("Adding apt lock handler script since task: " + task.getName() +
                            " is meant to be executed in: " +
                            task.getInstallation().getOperatingSystemType().getOperatingSystemFamily() + " version: " +
                            task.getInstallation().getOperatingSystemType().getOperatingSystemVersion());
                prepareInfraTask.setPreScript(PAFactory.createSimpleScriptFromFIle(WAIT_FOR_LOCK_SCRIPT, "bash"));
            }
        }

        prepareInfraTask.setVariables(taskVariablesMap);
        prepareInfraTask.addGenericInformation("NODE_ACCESS_TOKEN", token);

        return prepareInfraTask;
    }

    private ScriptTask createNodeRemovalTask(Task deletedTask, Deployment deployment, String taskNameSuffix) {
        LOGGER.debug("Removing node script file: " +
                     Objects.requireNonNull(getClass().getResource(File.separator + REMOVE_NODE_SCRIPT)));
        ScriptTask removeNodeTask = PAFactory.createGroovyScriptTaskFromFile("removeNode_" + deletedTask.getName() +
                                                                             taskNameSuffix, REMOVE_NODE_SCRIPT);

        Map<String, TaskVariable> variablesMap = createVariablesMapForIAASNodeRemoval(deployment);
        LOGGER.debug("Variables to be added to the task remove IAAS node: {}", variablesMap);
        removeNodeTask.setVariables(variablesMap);

        addLocalDefaultNSRegexSelectionScript(removeNodeTask);

        return removeNodeTask;
    }

    private Map<String, TaskVariable> createVariablesMapForIAASNodeRemoval(Deployment deployment) {
        Map<String, TaskVariable> variablesMap = new HashMap<>();
        variablesMap.put(Deployment.JSON_NODE_NAME,
                         new TaskVariable(Deployment.JSON_NODE_NAME,
                                          deployment.getNodeName(),
                                          "PA:NOT_EMPTY_STRING",
                                          false));
        variablesMap.put("preempt", new TaskVariable("preempt", "true", "PA:Boolean", false));
        return (variablesMap);
    }

    private List<ScriptTask> buildUnchangedPATask(Task task) {
        List<ScriptTask> scriptTasks = new LinkedList<>();
        task.getDeployments().forEach(deployment -> {
            // Creating infra deployment tasks
            String token = task.getTaskId() + deployment.getNumber();
            String suffix = "_" + deployment.getNumber();
            scriptTasks.add(createScalingParentInfraPreparationTask(task, suffix, token));
            scriptTasks.add(createScalingChildSaveTask(task, suffix, token));
            scriptTasks.get(scriptTasks.size() - 1).addDependence(scriptTasks.get(scriptTasks.size() - 2));
        });
        return setFirstAndLastSubmittedTaskNamesFromScriptTasks(task, scriptTasks);
    }

    /**
     * Translate a Morphemic task skeleton into a list of ProActive tasks when the job is being scaled out
     * @param task A Morphemic task skeleton
     * @param job The related job skeleton
     * @param scaledTaskName The scaled task name
     * @return A list of ProActive tasks
     */
    public List<ScriptTask> buildScalingOutPATask(Task task, Job job, String scaledTaskName) {
        List<ScriptTask> scriptTasks = new LinkedList<>();
        Task scaledTask = job.findTask(scaledTaskName);

        if (scaledTask.getParentTasks().containsValue(task.getName())) {
            // When the scaled task is a child the task to be built
            LOGGER.info("Building task " + task.getName() + " as a parent of task " + scaledTaskName);
            scriptTasks.addAll(createParentScaledTask(task));
        } else {
            // Using buildScalingInPATask because it handles all the remaining cases
            LOGGER.info("Moving to building with buildScalingInPATask() method");
            scriptTasks.addAll(buildScalingInPATask(task, scaledTaskName));
        }

        return scriptTasks;
    }

    /**
     * Translate a Morphemic task skeleton into a list of ProActive tasks when the job is being scaled in
     * @param task A Morphemic task skeleton
     * @param scaledTaskName The scaled task name
     * @return A list of ProActive tasks
     */
    public List<ScriptTask> buildScalingInPATask(Task task, String scaledTaskName) {
        List<ScriptTask> scriptTasks = new LinkedList<>();

        if (scaledTaskName.equals(task.getName())) {
            // When the scaled task is the task to be built
            LOGGER.info("Building task " + task.getName() + " as it is scaled out");
            scriptTasks.addAll(buildScaledPATask(task));
        } else if (task.getParentTasks().containsValue(scaledTaskName)) {
            // When the scaled task is a parent of the task to be built
            LOGGER.info("Building task " + task.getName() + " as a child of task " + scaledTaskName);
            scriptTasks.addAll(createChildScaledTask(task));
        } else {
            LOGGER.debug("Task " + task.getName() + " is not impacted by the scaling of task " + scaledTaskName);
        }

        return scriptTasks;
    }

    /**
     * Translate a Morphemic task skeleton into a list of ProActive tasks when the job is being reconfigured
     * @param task A Morphemic task skeleton
     * @param job  The related job skeleton
     * @param reconfigurationPlan The corresponding reconfiguration plan
     * @return A list of ProActive tasks
     */
    public List<ScriptTask> buildReconfigurationPATask(Task task, Job job,
            ReconfigurationJobDefinition reconfigurationPlan) {
        List<ScriptTask> scriptTasks = new LinkedList<>();

        Set<String> addedTaskNames = reconfigurationPlan.getAddedTasks()
                                                        .stream()
                                                        .map(TaskReconfigurationDefinition::getTask)
                                                        .map(TaskDefinition::getName)
                                                        .collect(Collectors.toSet());

        if (reconfigurationPlan.getUnchangedTasks().contains(task.getName())) {
            // When the scaled task is the task to be built
            LOGGER.info("Building task " + task.getName() + " as it is unchanged");
            scriptTasks.addAll(buildUnchangedPATask(task));
        } else if (addedTaskNames.contains(task.getName())) {
            // When the scaled task is a parent of the task to be built
            LOGGER.info("Building task [{}] as a new added task ", task.getTaskId());
            scriptTasks.addAll(buildPATask(task, job));
        } else {
            LOGGER.warn("Task [{}] is neither unchanged nor added. This should not figure ine job!", task.getTaskId());
        }

        return scriptTasks;
    }

    /**
     * Translate a Morphemic task skeleton into a list of ProActive tasks when the job is being reconfigured
     * @param deletedTask A Morphemic task skeleton
     * @param job  The related job skeleton
     * @param reconfigurationPlan The corresponding reconfiguration plan
     * @return A list of ProActive tasks
     */
    public List<ScriptTask> buildReconfigurationDeletedTask(Task deletedTask, Job job,
            ReconfigurationJobDefinition reconfigurationPlan) {
        LOGGER.info("Building task " + deletedTask.getName() + " as it will be deleted");
        List<ScriptTask> scriptTasks = new LinkedList<>();
        deletedTask.getDeployments().forEach(deployment -> {
            // Creating infra removal task
            String suffix = "_" + deployment.getNumber();
            scriptTasks.add(createNodeRemovalTask(deletedTask, deployment, suffix));
        });
        return setFirstAndLastSubmittedTaskNamesFromScriptTasks(deletedTask, scriptTasks);
    }

    private List<ScriptTask> setFirstAndLastSubmittedTaskNamesFromScriptTasks(Task task, List<ScriptTask> scriptTasks) {
        if (scriptTasks.isEmpty()) {
            LOGGER.warn("Could not set First and Last Submitted Task Names because scriptTasks related to task [{}] is empty.",
                        task.getTaskId());
            return scriptTasks;
        }
        task.setDeploymentFirstSubmittedTaskName(scriptTasks.get(0)
                                                            .getName()
                                                            .substring(0,
                                                                       scriptTasks.get(0).getName().lastIndexOf("_")));
        task.setDeploymentLastSubmittedTaskName(scriptTasks.get(scriptTasks.size() - 1)
                                                           .getName()
                                                           .substring(0,
                                                                      scriptTasks.get(scriptTasks.size() - 1)
                                                                                 .getName()
                                                                                 .lastIndexOf("_")));

        return scriptTasks;
    }

    /**
     * Translate a Morphemic task skeleton into a list of ProActive tasks
     * @param task A Morphemic task skeleton
     * @param job The related job skeleton
     * @return A list of ProActive tasks
     */
    public List<ScriptTask> buildPATask(Task task, Job job) {
        List<ScriptTask> scriptTasks = new LinkedList<>();
        LOGGER.debug("Building PA task for: {}", task.getTaskId());
        if (task.getDeployments() == null || task.getDeployments().isEmpty()) {
            LOGGER.warn("The task " + task.getName() +
                        " does not have a deployment. It will be scheduled on any free node.");
            scriptTasks.addAll(createAppTasks(task, "", ""));
            task.setDeploymentFirstSubmittedTaskName(scriptTasks.get(0).getName());
            task.setDeploymentLastSubmittedTaskName(scriptTasks.get(scriptTasks.size() - 1).getName());
        } else {
            task.getDeployments().stream().filter(deployment -> !deployment.getIsDeployed()).forEach(deployment -> {
                // Creating infra deployment tasks
                String token = task.getTaskId() + deployment.getNumber();
                String suffix = "_" + deployment.getNumber();
                scriptTasks.add(createInfraTask(task, deployment, suffix, token));
                if (deployment.getWorker() != null && deployment.getWorker()) {
                    ScriptTask waitForMasterTask = createWaitForMasterTask(deployment.getMasterToken());
                    waitForMasterTask.addDependence(scriptTasks.get(scriptTasks.size() - 1));
                    scriptTasks.add(waitForMasterTask);
                }
                // If the infrastructure comes with the deployment of the EMS, we set it up.
                Optional.ofNullable(deployment.getEmsDeployment()).ifPresent(emsDeploymentRequest -> {
                    String emsTaskSuffix = "_" + task.getName() + suffix;
                    ScriptTask emsScriptTask = createEmsDeploymentTask(emsDeploymentRequest, emsTaskSuffix, token);
                    emsScriptTask.addDependence(scriptTasks.get(scriptTasks.size() - 1));
                    scriptTasks.add(emsScriptTask);
                });
                LOGGER.info("Token added: " + token);
                deployment.setIsDeployed(true);
                deployment.setNodeAccessToken(token);

                LOGGER.info("+++ Deployment number: " + deployment.getNumber());

                // Creating application deployment tasks
                createAndAddAppDeploymentTasks(task, suffix, token, scriptTasks);
                if (deployment.getWorker() != null && deployment.getWorker().equals(false)) {
                    ScriptTask setTokenTask = createSetTokenTask(deployment.getMasterToken());
                    setTokenTask.addDependence(scriptTasks.get(scriptTasks.size() - 1));
                    scriptTasks.add(setTokenTask);
                }
            });
            if (!scriptTasks.isEmpty()) {
                task.setDeploymentFirstSubmittedTaskName(scriptTasks.get(0)
                                                                    .getName()
                                                                    .substring(0,
                                                                               scriptTasks.get(0)
                                                                                          .getName()
                                                                                          .lastIndexOf("_")));
            }
        }

        scriptTasks.forEach(scriptTask -> task.addSubmittedTaskName(scriptTask.getName()));

        return scriptTasks;
    }

    public ScriptTask buildInitChannelsTask(Job job) {
        LOGGER.debug("Init channels script file: " +
                     Objects.requireNonNull(getClass().getResource(File.separator + INIT_SYNC_CHANNELS_SCRIPT)));
        ScriptTask cleanChannelsTask = PAFactory.createGroovyScriptTaskFromFile("InitChannels",
                                                                                INIT_SYNC_CHANNELS_SCRIPT);

        Map<String, TaskVariable> variablesMap = createVariablesMapForSynchronizationChannels(job);
        LOGGER.debug("Variables to be added to the task Init Channels: {}", variablesMap);
        cleanChannelsTask.setVariables(variablesMap);

        addLocalDefaultNSRegexSelectionScript(cleanChannelsTask);

        return cleanChannelsTask;
    }

    public ScriptTask buildCleanChannelsTask(Job job) {
        LOGGER.debug("Cleaning channels script file: " +
                     Objects.requireNonNull(getClass().getResource(File.separator + CLEAN_SYNC_CHANNELS_SCRIPT)));
        ScriptTask cleanChannelsTask = PAFactory.createGroovyScriptTaskFromFile("CleanChannels",
                                                                                CLEAN_SYNC_CHANNELS_SCRIPT);

        Map<String, TaskVariable> variablesMap = createVariablesMapForSynchronizationChannels(job);
        LOGGER.debug("Variables to be added to the task clean Channels: {}", variablesMap);
        cleanChannelsTask.setVariables(variablesMap);

        addLocalDefaultNSRegexSelectionScript(cleanChannelsTask);

        return cleanChannelsTask;
    }

    public static <T> Consumer<T> withCounter(BiConsumer<Integer, T> consumer) {
        AtomicInteger counter = new AtomicInteger(0);
        return item -> consumer.accept(counter.getAndIncrement(), item);
    }

    private Map<String, TaskVariable> createVariablesMapForSynchronizationChannels(Job job) {
        Map<String, TaskVariable> variablesMap = new HashMap<>();
        job.getTasks().forEach(withCounter((i, task) -> {
            String variableName = COMPONENT_NAME_VARIABLE_NAME + i;
            variablesMap.put(variableName,
                             new TaskVariable(variableName, task.getName(), "PA:NOT_EMPTY_STRING", false));
        }));
        return (variablesMap);
    }

    private ScriptTask createWaitForMasterTask(String masterNodeToken) {
        String clusterType = System.getenv(ClusterUtils.CLUSTER_TYPE_ENV);
        String waitForMasterScript;

        if (ClusterUtils.CLUSTER_TYPE_K3S.equalsIgnoreCase(clusterType)) {
            waitForMasterScript = WAIT_FOR_MASTER_K3S_SCRIPT;
        } else {
            waitForMasterScript = WAIT_FOR_MASTER_SCRIPT;
        }

        ScriptTask waitForMasterTask = PAFactory.createGroovyScriptTaskFromFile("wait_for_master", waitForMasterScript);
        waitForMasterTask.addGenericInformation("NODE_ACCESS_TOKEN", masterNodeToken);

        return waitForMasterTask;
    }

    public ScriptTask createOneNodeTask(String script, String token, String taskName) {
        ScriptTask oneNodeTask = PAFactory.createBashScriptTask(taskName, script);
        oneNodeTask.addGenericInformation("NODE_ACCESS_TOKEN", token);
        return oneNodeTask;
    }

    private ScriptTask createSetTokenTask(String masterNodeToken) {
        ScriptTask setTokenTask = PAFactory.createGroovyScriptTaskFromFile("set_token", SET_TOKEN_SCRIPT);
        Map<String, TaskVariable> variablesMap = new HashMap<>();
        variablesMap.put("nodeToken", new TaskVariable("nodeToken", masterNodeToken));
        setTokenTask.setVariables(variablesMap);
        return setTokenTask;
    }

    public ScriptTask createDeleteNodeTask(String nodeUrl) {
        ScriptTask deleteNodeTask = PAFactory.createGroovyScriptTaskFromFile("Delete-Node", DELETE_NODE_SCRIPT);
        Map<String, TaskVariable> variablesMap = new HashMap<>();
        variablesMap.put("nodeURL", new TaskVariable("nodeURL", nodeUrl));
        deleteNodeTask.setVariables(variablesMap);
        return deleteNodeTask;
    }

    public ScriptTask createDrainNodeTask(String nodeName, String masterNodeToken) {
        ScriptTask drainNodeTask = PAFactory.createBashScriptTaskFromFile("Drain-Node", DRIAN_NODE_SCRIPT);
        Map<String, TaskVariable> variablesMap = new HashMap<>();
        variablesMap.put(Deployment.JSON_NODE_NAME, new TaskVariable(Deployment.JSON_NODE_NAME, nodeName));
        drainNodeTask.setVariables(variablesMap);
        drainNodeTask.addGenericInformation("NODE_ACCESS_TOKEN", masterNodeToken);
        return drainNodeTask;
    }
}
