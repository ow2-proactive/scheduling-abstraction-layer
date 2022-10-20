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

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;

import org.apache.commons.lang3.Validate;
import org.ow2.proactive.sal.service.model.*;
import org.ow2.proactive.sal.service.service.infrastructure.PASchedulerGateway;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.ow2.proactive.scheduler.common.exception.UserException;
import org.ow2.proactive.scheduler.common.job.TaskFlowJob;
import org.ow2.proactive.scheduler.common.task.ScriptTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service("ScalingService")
public class ScalingService {

    @Autowired
    private PAGatewayService paGatewayService;

    @Autowired
    private PASchedulerGateway schedulerGateway;

    @Autowired
    private JobService jobService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private TaskBuilder taskBuilder;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * Register a set of node as an operation for scale up
     * @param sessionId A valid session id
     * @param nodeNames Name of the nodes to be created and provisioned
     * @param jobId The name of the Job to be allocated
     * @param taskName the name of the task whose node are to be allocated
     * @return 0 if the operation went successful, 1 if the scaling failed because no job/task was node found, 2 if the scaling failed because no deployment to clone are available.
     */
    public Boolean addScaleOutTask(String sessionId, String jobId, String taskName, List<String> nodeNames)
            throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Validate.notEmpty(nodeNames, "The provided nodes list should not be empty");
        Validate.notNull(jobId, "The provided jobId should not be null.");

        // Let's find the jobId to retrieve the task
        Optional<Job> optJob = Optional.ofNullable(repositoryService.getJob(jobId));
        if (!optJob.isPresent()) {
            LOGGER.error(String.format("Job [%s] not found", jobId));
            return false;
        }
        //        No way to refresh the DB entry
        //        EntityManagerHelper.refresh(optJob.get());

        // Let's find the task:
        Optional<Task> optTask = Optional.ofNullable(optJob.get().findTask(taskName));
        if (!optTask.isPresent()) {
            LOGGER.error(String.format("Task [%s] not found", taskName));
            return false;
        }

        // Let's retrieve the deployment to clone
        if (optTask.get().getDeployments() == null || optTask.get().getDeployments().isEmpty()) {
            LOGGER.error(String.format("No previous deployment found in task [%s] ", taskName));
            return false;
        }

        // Saving suffix IDs of new nodes
        List<Long> newNodesNumbers = new LinkedList<>();

        // Let's clone the deployment/node as needed
        Deployment oldDeployment = optTask.get().getDeployments().get(0);
        if (NodeType.BYON.equals(oldDeployment.getDeploymentType()) ||
            NodeType.EDGE.equals(oldDeployment.getDeploymentType())) {
            LOGGER.error(String.format("The previous deployment is a BYON/EDGE node [%s] ", oldDeployment));
            return false;
        }
        nodeNames.stream().map(nodeName -> {
            EmsDeploymentRequest newEmsDeploymentReq = oldDeployment.getEmsDeployment() == null ? null
                                                                                                : oldDeployment.getEmsDeployment()
                                                                                                               .clone(nodeName);
            oldDeployment.getIaasNode().incDeployedNodes(1L);
            return new Deployment(nodeName,
                                  newEmsDeploymentReq,
                                  oldDeployment.getPaCloud(),
                                  oldDeployment.getTask(),
                                  false,
                                  null,
                                  null,
                                  null,
                                  null,
                                  NodeType.IAAS,
                                  oldDeployment.getIaasNode(),
                                  null,
                                  null);
        }).forEach(deployment -> {
            // Persist new deployment data
            deployment.setNumber(optTask.get().getNextDeploymentID());
            newNodesNumbers.add(optTask.get().getNextDeploymentID());
            optTask.get().addDeployment(deployment);
            if (deployment.getEmsDeployment() != null) {
                repositoryService.updateEmsDeploymentRequest(deployment.getEmsDeployment());
            }
            deployment.getPaCloud().addDeployment(deployment);
            repositoryService.updateDeployment(deployment);
            repositoryService.updateTask(optTask.get());
            repositoryService.updateNode(deployment.getNode());
            repositoryService.updatePACloud(deployment.getPaCloud());
        });

        repositoryService.flush();

        // Let's deploy the VMS
        submitScalingOutJob(optJob.get(), taskName, newNodesNumbers);

        return true;
    }

    private void submitScalingOutJob(Job job, String scaledTaskName, List<Long> newNodesNumbers) {
        //        No way to refresh the DB entry
        //        EntityManagerHelper.refresh(job);
        LOGGER.info("Task: " + scaledTaskName + " of job " + job.toString() + " to be scaled out.");

        TaskFlowJob paJob = new TaskFlowJob();
        paJob.setName(job.getName() + "_" + scaledTaskName + "_ScaleOut");
        LOGGER.info("Job created: " + paJob.toString());

        job.getTasks().forEach(task -> {
            List<ScriptTask> scriptTasks = taskBuilder.buildScalingOutPATask(task, job, scaledTaskName);

            if (scriptTasks != null && !scriptTasks.isEmpty()) {
                addAllScriptTasksToPAJob(paJob, task, scriptTasks);
                repositoryService.updateTask(task);
            }
        });

        setAllScalingOutMandatoryDependencies(paJob, job, scaledTaskName, newNodesNumbers);

        paJob.setMaxNumberOfExecution(2);
        paJob.setProjectName("Morphemic");

        long submittedJobId = schedulerGateway.submit(paJob).longValue();
        job.setSubmittedJobId(submittedJobId);
        job.setSubmittedJobType(SubmittedJobType.SCALE_OUT);

        repositoryService.updateJob(job);
        repositoryService.flush();
        LOGGER.info("Scaling out of task \'" + scaledTaskName + "\' job, submitted successfully. ID = " +
                    submittedJobId);
    }

    private void addAllScriptTasksToPAJob(TaskFlowJob paJob, Task task, List<ScriptTask> scriptTasks) {
        scriptTasks.forEach(scriptTask -> {
            try {
                paJob.addTask(scriptTask);
            } catch (UserException e) {
                LOGGER.error("Task " + task.getName() + " could not be added due to: " + e.toString());
            }
        });
    }

    private void setAllScalingOutMandatoryDependencies(TaskFlowJob paJob, Job jobToSubmit, String scaledTaskName,
            List<Long> newNodesNumbers) {
        jobToSubmit.getTasks().forEach(task -> {
            if (task.getParentTasks() != null && !task.getParentTasks().isEmpty()) {
                task.getParentTasks().forEach(parentTaskName -> {
                    paJob.getTasks().forEach(paTask -> {
                        paJob.getTasks().forEach(paParentTask -> {
                            if (paTask.getName().contains(task.getName()) &&
                                paParentTask.getName().contains(parentTaskName)) {
                                if (paParentTask.getName().contains(scaledTaskName)) {
                                    if (newNodesNumbers.stream()
                                                       .anyMatch(entry -> paParentTask.getName()
                                                                                      .endsWith(entry.toString()))) {
                                        if (paTask.getName().contains(task.getDeploymentFirstSubmittedTaskName()) &&
                                            paParentTask.getName()
                                                        .contains(jobToSubmit.findTask(parentTaskName)
                                                                             .getDeploymentLastSubmittedTaskName())) {
                                            paTask.addDependence(paParentTask);
                                        }
                                    } else {
                                        if (paTask.getName().contains(task.getDeploymentFirstSubmittedTaskName()) &&
                                            paParentTask.getName().startsWith("prepareInfra")) {
                                            paTask.addDependence(paParentTask);
                                        }
                                    }
                                } else if (paTask.getName().contains(scaledTaskName)) {
                                    if (newNodesNumbers.stream().anyMatch(entry -> paTask.getName()
                                                                                         .endsWith(entry.toString()))) {
                                        if (paTask.getName().contains(task.getDeploymentFirstSubmittedTaskName()) &&
                                            paParentTask.getName()
                                                        .contains(jobToSubmit.findTask(parentTaskName)
                                                                             .getDeploymentLastSubmittedTaskName())) {
                                            paTask.addDependence(paParentTask);
                                        }
                                    } else {
                                        if (paTask.getName().startsWith("prepareInfra") &&
                                            paParentTask.getName()
                                                        .contains(jobToSubmit.findTask(parentTaskName)
                                                                             .getDeploymentLastSubmittedTaskName())) {
                                            paTask.addDependence(paParentTask);
                                        }
                                    }
                                }
                            }
                        });
                    });
                });
            }
        });
    }

    /**
     * Unregister a set of node as a scale-down operation
     * @param sessionId A valid session id
     * @param nodeNames A list of node to be removed
     * @param jobId The name of the job to scale down the nodes
     * @param taskName the name of the task whose nodes are to be removed
     * @return 0 if the operation went successful, 2 if the operation avorted to prevent last node to be removed.
     */
    public Boolean addScaleInTask(String sessionId, String jobId, String taskName, List<String> nodeNames)
            throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Validate.notEmpty(nodeNames, "The provided nodes list should not be empty");
        Validate.notNull(jobId, "The provided jobId should not be null.");

        // Let's find the jobId to retrieve the task
        Optional<Job> optJob = Optional.ofNullable(repositoryService.getJob(jobId));
        if (!optJob.isPresent()) {
            LOGGER.error(String.format("Job [%s] not found", jobId));
            throw new NotFoundException("Job " + jobId + " not found");
        }
        //        No way to refresh the DB entry
        //        EntityManagerHelper.refresh(optJob.get());

        // Let's find the task:
        Optional<Task> optTask = Optional.ofNullable(optJob.get().findTask(taskName));
        if (!optTask.isPresent()) {
            LOGGER.error(String.format("Task [%s] not found", taskName));
            throw new NotFoundException("Task " + taskName + " not found");
        }

        // Validating there will still be at least one deployment in the task
        if (optTask.get().getDeployments().size() - nodeNames.size() < 1) {
            LOGGER.error("I stop the scale-in: the task will loose its last deployment");
            throw new IllegalArgumentException("Task would loose its last deployment.");
        }

        // For supplied node, I retrieve their deployment
        List<Deployment> deployments = nodeNames.stream()
                                                .map(node -> repositoryService.getDeployment(node))
                                                .filter(Objects::nonNull)
                                                .collect(Collectors.toList());

        deployments = deployments.stream().filter(deployment -> {
            if (NodeType.BYON.equals(deployment.getDeploymentType()) ||
                NodeType.EDGE.equals(deployment.getDeploymentType())) {
                LOGGER.warn("Deployment " + deployment.getNodeName() +
                            " is a BYON/EDGE node and can't be removed in scaling in.");
                return false;
            }
            return true;
        }).collect(Collectors.toList());

        // For deployed node, I flag their removal
        List<String> nodesToBeRemoved = deployments.stream()
                                                   .filter(Deployment::getIsDeployed)
                                                   .map(Deployment::getNodeName)
                                                   .collect(Collectors.toList());

        LOGGER.info("Nodes to be removed are : " + nodesToBeRemoved);
        // For every node, I remove the deployment entree
        deployments.forEach(deployment -> {
            deployment.getTask().removeDeployment(deployment);
            repositoryService.updateTask(deployment.getTask());
            deployment.getPaCloud().removeDeployment(deployment);
            repositoryService.updatePACloud(deployment.getPaCloud());
            deployment.getIaasNode().decDeployedNodes(1L);
            repositoryService.updateIaasNode(deployment.getIaasNode());
            repositoryService.deleteDeployment(deployment);
        });
        // I commit the removal of deployed node
        nodeService.removeNodes(sessionId, nodesToBeRemoved, true);

        repositoryService.flush();

        // Let's deploy the VMS
        submitScalingInJob(optJob.get(), taskName);

        return true;
    }

    private void submitScalingInJob(Job job, String scaledTaskName) {
        //        No way to refresh the DB entry
        //        EntityManagerHelper.refresh(job);
        LOGGER.info("Task: " + scaledTaskName + " of job " + job.toString() + " to be scaled in.");

        TaskFlowJob paJob = new TaskFlowJob();
        paJob.setName(job.getName() + "_" + scaledTaskName + "_ScaleIn");
        LOGGER.info("Job created: " + paJob.toString());

        job.getTasks().forEach(task -> {
            List<ScriptTask> scriptTasks = taskBuilder.buildScalingInPATask(task, job, scaledTaskName);

            if (scriptTasks != null && !scriptTasks.isEmpty()) {
                addAllScriptTasksToPAJob(paJob, task, scriptTasks);
                repositoryService.updateTask(task);
            }
        });

        jobService.setAllMandatoryDependencies(paJob, job);

        paJob.setMaxNumberOfExecution(2);
        paJob.setProjectName("Morphemic");

        long submittedJobId = schedulerGateway.submit(paJob).longValue();
        job.setSubmittedJobId(submittedJobId);
        job.setSubmittedJobType(SubmittedJobType.SCALE_IN);

        repositoryService.updateJob(job);
        repositoryService.flush();
        LOGGER.info("Scaling out of task \'" + scaledTaskName + "\' job, submitted successfully. ID = " +
                    submittedJobId);
    }
}
