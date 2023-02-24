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
import java.io.IOException;
import java.util.*;

import org.ow2.proactive.sal.model.*;
import org.ow2.proactive.sal.service.nc.WhiteListedInstanceTypesUtils;
import org.ow2.proactive.sal.service.service.application.PAFactory;
import org.ow2.proactive.sal.service.util.ByonUtils;
import org.ow2.proactive.sal.service.util.Utils;
import org.ow2.proactive.scheduler.common.task.ScriptTask;
import org.ow2.proactive.scheduler.common.task.TaskVariable;
import org.ow2.proactive.scripting.InvalidScriptException;
import org.ow2.proactive.scripting.SelectionScript;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import lombok.extern.log4j.Log4j2;


@Log4j2
@Service("TaskBuilder")
public class TaskBuilder {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private static final String SCRIPTS_SEPARATION_BASH = NEW_LINE + NEW_LINE + "# Main script" + NEW_LINE + NEW_LINE;

    private static final String SCRIPTS_SEPARATION_GROOVY = NEW_LINE + NEW_LINE + "// Separation script" + NEW_LINE +
                                                            NEW_LINE;

    private static final String EMS_DEPLOY_PRE_SCRIPT = "emsdeploy_prescript.sh";

    private static final String EMS_DEPLOY_PRIVATE_PRE_SCRIPT = "emsdeploy_prescript_private.sh";

    private static final String EMS_DEPLOY_MAIN_SCRIPT = "emsdeploy_mainscript.groovy";

    private static final String EMS_DEPLOY_POST_SCRIPT = "emsdeploy_postscript.sh";

    private static final String EXPORT_ENV_VAR_SCRIPT = "export_env_var_script.sh";

    private static final String COLLECT_IP_ADDR_RESULTS_SCRIPT = "collect_ip_addr_results.groovy";

    private static final String START_DOCKER_APP_SCRIPT = "start_docker_app.sh";

    private static final String CHECK_NODE_SOURCE_REGEXP_SCRIPT = "check_node_source_regexp.groovy";

    private static final String ACQUIRE_NODE_AWS_SCRIPT = "acquire_node_aws_script.groovy";

    private static final String PRE_ACQUIRE_NODE_SCRIPT = "pre_acquire_node_script.groovy";

    private static final String ACQUIRE_NODE_BYON_SCRIPT = "acquire_node_BYON_script.groovy";

    private static final String POST_PREPARE_INFRA_SCRIPT = "post_prepare_infra_script.groovy";

    private static final String PREPARE_INFRA_SCRIPT = "prepare_infra_script.sh";

    private static final String WAIT_FOR_LOCK_SCRIPT = "wait_for_lock_script.sh";

    private static final String NODE_SOURCE_NAME_REGEX = "^local$|^Default$|^LocalNodes$|^Server-Static-Nodes$";

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

    private List<ScriptTask> createAppTasks(Task task, String taskNameSuffix, String taskToken, Job job) {
        switch (task.getType()) {
            case "commands":
                return createCommandsTask(task, taskNameSuffix, taskToken, job);
            case "docker":
                return createDockerTask(task, taskNameSuffix, taskToken, job);
        }

        return new LinkedList<>();
    }

    private List<ScriptTask> createDockerTask(Task task, String taskNameSuffix, String taskToken, Job job) {
        List<ScriptTask> scriptTasks = new LinkedList<>();
        ScriptTask scriptTask = PAFactory.createBashScriptTask(task.getName() + "_start" +
                                                               taskNameSuffix,
                                                               Utils.getContentWithFileName(EXPORT_ENV_VAR_SCRIPT) +
                                                                               SCRIPTS_SEPARATION_BASH +
                                                                               Utils.getContentWithFileName(START_DOCKER_APP_SCRIPT));
        Map<String, TaskVariable> taskVariablesMap = new HashMap<>();

        if (!task.getParentTasks().isEmpty()) {
            //TODO: Taking into consideration multiple parent tasks with multiple communications
            taskVariablesMap.put("requestedPortName",
                                 new TaskVariable("requestedPortName",
                                                  job.findTask(task.getParentTasks().get(0))
                                                     .getPortsToOpen()
                                                     .get(0)
                                                     .getRequestedName()));
        }

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

    private List<ScriptTask> createCommandsTask(Task task, String taskNameSuffix, String taskToken, Job job) {
        List<ScriptTask> scriptTasks = new LinkedList<>();
        ScriptTask scriptTaskStart = null;
        ScriptTask scriptTaskInstall = null;

        Map<String, TaskVariable> taskVariablesMap = new HashMap<>();
        if (!task.getParentTasks().isEmpty()) {
            //TODO: Taking into consideration multiple parent tasks with multiple communications
            taskVariablesMap.put("requestedPortName",
                                 new TaskVariable("requestedPortName",
                                                  job.findTask(task.getParentTasks().get(0))
                                                     .getPortsToOpen()
                                                     .get(0)
                                                     .getRequestedName()));
        }

        if (!(Strings.isNullOrEmpty(task.getInstallation().getInstall()) &&
              Strings.isNullOrEmpty(task.getInstallation().getPreInstall()) &&
              Strings.isNullOrEmpty(task.getInstallation().getPostInstall()))) {
            if (!Strings.isNullOrEmpty(task.getInstallation().getInstall())) {
                scriptTaskInstall = PAFactory.createBashScriptTask(task.getName() + "_install" +
                                                                   taskNameSuffix,
                                                                   Utils.getContentWithFileName(EXPORT_ENV_VAR_SCRIPT) +
                                                                                   SCRIPTS_SEPARATION_BASH +
                                                                                   task.getInstallation().getInstall());
            } else {
                scriptTaskInstall = PAFactory.createBashScriptTask(task.getName() + "_install" + taskNameSuffix,
                                                                   "echo \"Installation script is empty. Nothing to be executed.\"");
            }

            if (!Strings.isNullOrEmpty(task.getInstallation().getPreInstall())) {
                scriptTaskInstall.setPreScript(PAFactory.createSimpleScript(Utils.getContentWithFileName(EXPORT_ENV_VAR_SCRIPT) +
                                                                            SCRIPTS_SEPARATION_BASH +
                                                                            task.getInstallation().getPreInstall(),
                                                                            "bash"));
            }
            if (!Strings.isNullOrEmpty(task.getInstallation().getPostInstall())) {
                scriptTaskInstall.setPostScript(PAFactory.createSimpleScript(Utils.getContentWithFileName(EXPORT_ENV_VAR_SCRIPT) +
                                                                             SCRIPTS_SEPARATION_BASH +
                                                                             task.getInstallation().getPostInstall(),
                                                                             "bash"));
            }
            if (!task.getParentTasks().isEmpty()) {
                scriptTaskInstall.setVariables(taskVariablesMap);
            }
            scriptTaskInstall.addGenericInformation("NODE_ACCESS_TOKEN", taskToken);
            scriptTasks.add(scriptTaskInstall);
        }

        if (!(Strings.isNullOrEmpty(task.getInstallation().getStart()) &&
              Strings.isNullOrEmpty(task.getInstallation().getPreStart()) &&
              Strings.isNullOrEmpty(task.getInstallation().getPostStart()))) {
            if (!Strings.isNullOrEmpty(task.getInstallation().getStart())) {
                scriptTaskStart = PAFactory.createBashScriptTask(task.getName() + "_start" +
                                                                 taskNameSuffix,
                                                                 Utils.getContentWithFileName(EXPORT_ENV_VAR_SCRIPT) +
                                                                                 SCRIPTS_SEPARATION_BASH +
                                                                                 task.getInstallation().getStart());
            } else {
                scriptTaskStart = PAFactory.createBashScriptTask(task.getName() + "_start" + taskNameSuffix,
                                                                 "echo \"Installation script is empty. Nothing to be executed.\"");
            }

            if (!Strings.isNullOrEmpty(task.getInstallation().getPreStart())) {
                scriptTaskStart.setPreScript(PAFactory.createSimpleScript(Utils.getContentWithFileName(EXPORT_ENV_VAR_SCRIPT) +
                                                                          SCRIPTS_SEPARATION_BASH +
                                                                          task.getInstallation().getPreStart(),
                                                                          "bash"));
            }
            if (!Strings.isNullOrEmpty(task.getInstallation().getPostStart())) {
                scriptTaskStart.setPostScript(PAFactory.createSimpleScript(Utils.getContentWithFileName(EXPORT_ENV_VAR_SCRIPT) +
                                                                           SCRIPTS_SEPARATION_BASH +
                                                                           task.getInstallation().getPostStart(),
                                                                           "bash"));
            }
            if (!task.getParentTasks().isEmpty()) {
                scriptTaskStart.setVariables(taskVariablesMap);
            }
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
        }

        return new ScriptTask();
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
        ObjectMapper mapper = new ObjectMapper();
        String imageId;
        switch (deployment.getPaCloud().getCloudProviderName()) {
            case "aws-ec2":
                if (WhiteListedInstanceTypesUtils.isHandledHardwareInstanceType(deployment.getNode()
                                                                                          .getNodeCandidate()
                                                                                          .getHardware()
                                                                                          .getProviderId())) {
                    imageId = deployment.getNode().getNodeCandidate().getImage().getProviderId();
                } else {
                    imageId = deployment.getNode().getNodeCandidate().getLocation().getName() + "/" +
                              deployment.getNode().getNodeCandidate().getImage().getProviderId();
                }
                break;
            case "openstack":
                imageId = deployment.getNode().getNodeCandidate().getImage().getProviderId();
                break;
            default:
                imageId = deployment.getNode().getNodeCandidate().getImage().getProviderId();
        }
        String nodeConfigJson = "{\"image\": \"" + imageId + "\", " + "\"vmType\": \"" +
                                deployment.getNode().getNodeCandidate().getHardware().getProviderId() + "\", " +
                                "\"nodeTags\": \"" + deployment.getNodeName();
        if (task.getPortsToOpen() == null || task.getPortsToOpen().isEmpty()) {
            nodeConfigJson += "\"}";
        } else {
            try {
                nodeConfigJson += "\", \"portsToOpen\": " + mapper.writeValueAsString(task.getPortsToOpen()) + "}";
            } catch (IOException e) {
                LOGGER.error(Arrays.toString(e.getStackTrace()));
            }
        }
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
                                                         deployment.getPaCloud().getNodeSourceNamePrefix() +
                                                         deployment.getNode()
                                                                   .getNodeCandidate()
                                                                   .getLocation()
                                                                   .getName()));
        } else {
            variablesMap.put("NS_name",
                             new TaskVariable("NS_name",
                                              deployment.getPaCloud().getNodeSourceNamePrefix() + deployment.getNode()
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

    private ScriptTask createInfraIAASTaskForAWS(Task task, Deployment deployment, String taskNameSuffix,
            String nodeToken) {
        LOGGER.debug("Acquiring node AWS script file: " +
                     getClass().getResource(File.separator + ACQUIRE_NODE_AWS_SCRIPT).toString());
        ScriptTask deployNodeTask = PAFactory.createGroovyScriptTaskFromFile("acquireAWSNode_" + task.getName() +
                                                                             taskNameSuffix, ACQUIRE_NODE_AWS_SCRIPT);

        deployNodeTask.setPreScript(PAFactory.createSimpleScriptFromFIle(PRE_ACQUIRE_NODE_SCRIPT, "groovy"));

        Map<String, TaskVariable> variablesMap = createVariablesMapForAcquiringIAASNode(task, deployment, nodeToken);
        LOGGER.debug("Variables to be added to the task acquiring AWS IAAS node: " + variablesMap.toString());
        deployNodeTask.setVariables(variablesMap);

        addLocalDefaultNSRegexSelectionScript(deployNodeTask);

        return deployNodeTask;
    }

    private ScriptTask createInfraIAASTaskForOS(Task task, Deployment deployment, String taskNameSuffix,
            String nodeToken) {
        LOGGER.debug("Acquiring node OS script file: " +
                     getClass().getResource(File.separator + ACQUIRE_NODE_AWS_SCRIPT).toString());
        ScriptTask deployNodeTask = PAFactory.createGroovyScriptTaskFromFile("acquireOSNode_" + task.getName() +
                                                                             taskNameSuffix, ACQUIRE_NODE_AWS_SCRIPT);

        deployNodeTask.setPreScript(PAFactory.createSimpleScriptFromFIle(PRE_ACQUIRE_NODE_SCRIPT, "groovy"));

        Map<String, TaskVariable> variablesMap = createVariablesMapForAcquiringIAASNode(task, deployment, nodeToken);
        LOGGER.debug("Variables to be added to the task acquiring OS IAAS node: " + variablesMap.toString());
        deployNodeTask.setVariables(variablesMap);

        addLocalDefaultNSRegexSelectionScript(deployNodeTask);

        return deployNodeTask;
    }

    private ScriptTask createInfraIAASTask(Task task, Deployment deployment, String taskNameSuffix, String nodeToken) {
        switch (deployment.getPaCloud().getCloudProviderName()) {
            case "aws-ec2":
                return createInfraIAASTaskForAWS(task, deployment, taskNameSuffix, nodeToken);
            case "openstack":
                return createInfraIAASTaskForOS(task, deployment, taskNameSuffix, nodeToken);
        }
        return new ScriptTask();
    }

    private ScriptTask createInfraBYONandEDGETask(Task task, Deployment deployment, String taskNameSuffix,
            String nodeToken) {
        String nodeType = deployment.getDeploymentType().getName();
        System.out.println("the nodeType name is: " + nodeType);
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

    private List<ScriptTask> createChildScaledTask(Task task, Job job) {
        List<ScriptTask> scriptTasks = new LinkedList<>();
        task.getDeployments().stream().filter(Deployment::getIsDeployed).forEach(deployment -> {
            // Creating infra deployment tasks
            String token = task.getTaskId() + deployment.getNumber();
            String suffix = "_" + deployment.getNumber();
            scriptTasks.add(createScalingChildsaveTask(task, suffix, token, job));
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

    private ScriptTask createScalingChildsaveTask(Task task, String suffix, String token, Job job) {
        ScriptTask scriptTaskUpdate = null;

        Map<String, TaskVariable> taskVariablesMap = new HashMap<>();
        //TODO: Taking into consideration multiple parent tasks with multiple communications
        taskVariablesMap.put("requestedPortName",
                             new TaskVariable("requestedPortName",
                                              job.findTask(task.getParentTasks().get(0))
                                                 .getPortsToOpen()
                                                 .get(0)
                                                 .getRequestedName()));

        if (!Strings.isNullOrEmpty(task.getInstallation().getUpdateCmd())) {
            scriptTaskUpdate = PAFactory.createBashScriptTask(task.getName() + "_update" +
                                                              suffix,
                                                              Utils.getContentWithFileName(EXPORT_ENV_VAR_SCRIPT) +
                                                                      SCRIPTS_SEPARATION_BASH +
                                                                      task.getInstallation().getUpdateCmd());
        } else {
            scriptTaskUpdate = PAFactory.createBashScriptTask(task.getName() + "_install" + suffix,
                                                              "echo \"Installation script is empty. Nothing to be executed.\"");
        }

        scriptTaskUpdate.setPreScript(PAFactory.createSimpleScriptFromFIle(COLLECT_IP_ADDR_RESULTS_SCRIPT, "groovy"));

        scriptTaskUpdate.setVariables(taskVariablesMap);
        scriptTaskUpdate.addGenericInformation("NODE_ACCESS_TOKEN", token);

        return scriptTaskUpdate;
    }

    private List<ScriptTask> buildScaledPATask(Task task, Job job) {
        List<ScriptTask> scriptTasks = new LinkedList<>();

        task.getDeployments().stream().filter(Deployment::getIsDeployed).forEach(deployment -> {
            String token = task.getTaskId() + deployment.getNumber();
            String suffix = "_" + deployment.getNumber();

            // Creating infra preparation task
            scriptTasks.add(createInfraPreparationTask(task, suffix, token, job));
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
            createAndAddAppDeploymentTasks(task, suffix, token, scriptTasks, job);
        });

        scriptTasks.forEach(scriptTask -> task.addSubmittedTaskName(scriptTask.getName()));

        return scriptTasks;
    }

    private void createAndAddAppDeploymentTasks(Task task, String suffix, String token, List<ScriptTask> scriptTasks,
            Job job) {
        List<ScriptTask> appTasks = createAppTasks(task, suffix, token, job);
        task.setDeploymentLastSubmittedTaskName(appTasks.get(appTasks.size() - 1)
                                                        .getName()
                                                        .substring(0,
                                                                   appTasks.get(appTasks.size() - 1)
                                                                           .getName()
                                                                           .lastIndexOf(suffix)));

        // Creating infra preparation task
        appTasks.add(0, createInfraPreparationTask(task, suffix, token, job));
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

        if (!task.getPortsToOpen().isEmpty()) {
            prepareInfraTask = PAFactory.createGroovyScriptTaskFromFile(taskName, POST_PREPARE_INFRA_SCRIPT);
            prepareInfraTask.setPreScript(PAFactory.createSimpleScriptFromFIle(PREPARE_INFRA_SCRIPT, "bash"));
            //TODO: Taking into consideration multiple provided ports
            taskVariablesMap.put("providedPortName",
                                 new TaskVariable("providedPortName", task.getPortsToOpen().get(0).getRequestedName()));
            taskVariablesMap.put("providedPortValue",
                                 new TaskVariable("providedPortValue",
                                                  task.getPortsToOpen().get(0).getValue().toString()));
        } else {
            prepareInfraTask = PAFactory.createBashScriptTask(taskName,
                                                              "echo \"No ports to open and not parent tasks. Nothing to be prepared in VM.\"");
        }

        prepareInfraTask.setVariables(taskVariablesMap);
        prepareInfraTask.addGenericInformation("NODE_ACCESS_TOKEN", token);

        return prepareInfraTask;
    }

    private ScriptTask createInfraPreparationTask(Task task, String suffix, String token, Job job) {
        ScriptTask prepareInfraTask;
        Map<String, TaskVariable> taskVariablesMap = new HashMap<>();
        String taskName = "prepareInfra_" + task.getName() + suffix;

        if (!task.getPortsToOpen().isEmpty()) {
            prepareInfraTask = PAFactory.createBashScriptTaskFromFile(taskName, PREPARE_INFRA_SCRIPT);
            prepareInfraTask.setPostScript(PAFactory.createSimpleScript(Utils.getContentWithFileName(POST_PREPARE_INFRA_SCRIPT) +
                                                                        SCRIPTS_SEPARATION_GROOVY +
                                                                        Utils.getContentWithFileName(COLLECT_IP_ADDR_RESULTS_SCRIPT),
                                                                        "groovy"));
            //TODO: Taking into consideration multiple provided ports
            taskVariablesMap.put("providedPortName",
                                 new TaskVariable("providedPortName", task.getPortsToOpen().get(0).getRequestedName()));
            taskVariablesMap.put("providedPortValue",
                                 new TaskVariable("providedPortValue",
                                                  task.getPortsToOpen().get(0).getValue().toString()));
            if (!task.getParentTasks().isEmpty()) {
                //TODO: Taking into consideration multiple parent tasks with multiple communications
                taskVariablesMap.put("requestedPortName",
                                     new TaskVariable("requestedPortName",
                                                      job.findTask(task.getParentTasks().get(0))
                                                         .getPortsToOpen()
                                                         .get(0)
                                                         .getRequestedName()));
            }
        } else if (!task.getParentTasks().isEmpty()) {
            prepareInfraTask = PAFactory.createBashScriptTaskFromFile(taskName, PREPARE_INFRA_SCRIPT);
            prepareInfraTask.setPostScript(PAFactory.createSimpleScript(Utils.getContentWithFileName(COLLECT_IP_ADDR_RESULTS_SCRIPT),
                                                                        "groovy"));
            //TODO: Taking into consideration multiple parent tasks with multiple communications
            taskVariablesMap.put("requestedPortName",
                                 new TaskVariable("requestedPortName",
                                                  job.findTask(task.getParentTasks().get(0))
                                                     .getPortsToOpen()
                                                     .get(0)
                                                     .getRequestedName()));
        } else {
            prepareInfraTask = PAFactory.createBashScriptTask(taskName,
                                                              "echo \"No ports to open and not parent tasks. Nothing to be prepared in VM.\"");
        }

        if (task.getType().equals("commands")) {
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

        if (scaledTask.getParentTasks().contains(task.getName())) {
            // When the scaled task is a child the task to be built
            LOGGER.info("Building task " + task.getName() + " as a parent of task " + scaledTaskName);
            scriptTasks.addAll(createParentScaledTask(task));
        } else {
            // Using buildScalingInPATask because it handles all the remaining cases
            LOGGER.info("Moving to building with buildScalingInPATask() method");
            scriptTasks.addAll(buildScalingInPATask(task, job, scaledTaskName));
        }

        return scriptTasks;
    }

    /**
     * Translate a Morphemic task skeleton into a list of ProActive tasks when the job is being scaled in
     * @param task A Morphemic task skeleton
     * @param job The related job skeleton
     * @param scaledTaskName The scaled task name
     * @return A list of ProActive tasks
     */
    public List<ScriptTask> buildScalingInPATask(Task task, Job job, String scaledTaskName) {
        List<ScriptTask> scriptTasks = new LinkedList<>();

        if (scaledTaskName.equals(task.getName())) {
            // When the scaled task is the task to be built
            LOGGER.info("Building task " + task.getName() + " as it is scaled out");
            scriptTasks.addAll(buildScaledPATask(task, job));
        } else if (task.getParentTasks().contains(scaledTaskName)) {
            // When the scaled task is a parent of the task to be built
            LOGGER.info("Building task " + task.getName() + " as a child of task " + scaledTaskName);
            scriptTasks.addAll(createChildScaledTask(task, job));
        } else {
            LOGGER.debug("Task " + task.getName() + " is not impacted by the scaling of task " + scaledTaskName);
        }

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

        if (task.getDeployments() == null || task.getDeployments().isEmpty()) {
            LOGGER.warn("The task " + task.getName() +
                        " does not have a deployment. It will be scheduled on any free node.");
            scriptTasks.addAll(createAppTasks(task, "", "", job));
            task.setDeploymentFirstSubmittedTaskName(scriptTasks.get(0).getName());
            task.setDeploymentLastSubmittedTaskName(scriptTasks.get(scriptTasks.size() - 1).getName());
        } else {
            task.getDeployments().stream().filter(deployment -> !deployment.getIsDeployed()).forEach(deployment -> {
                // Creating infra deployment tasks
                String token = task.getTaskId() + deployment.getNumber();
                String suffix = "_" + deployment.getNumber();
                scriptTasks.add(createInfraTask(task, deployment, suffix, token));
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
                createAndAddAppDeploymentTasks(task, suffix, token, scriptTasks, job);
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
}
