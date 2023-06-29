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

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.ow2.proactive.sal.model.*;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;


@Log4j2
@Service("ReconfigurationService")
public class ReconfigurationService {

    @Autowired
    private PAGatewayService paGatewayService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private MonitoringService monitoringService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private JobService jobService;

    /**
     * Reconfigure job
     * @param sessionId A valid session id
     * @param jobId The ID of the job
     * @param reconfigurationPlan A reconfiguration plan
     * @return True if reconfiguration job submitted successfully, false oterwise
     */
    @Transactional
    public synchronized Boolean reconfigureJob(String sessionId, String jobId,
            ReconfigurationJobDefinition reconfigurationPlan) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Validate.notNull(jobId, "The job id received is null. Nothing to be created.");
        Validate.notEmpty(jobId, "The job id received is empty. Nothing to be created.");
        Validate.notNull(reconfigurationPlan, "The reconfiguration plan received is empty. Nothing to be created.");

        LOGGER.info("Reconfiguration of job [{}] started ...", jobId);

        // Finding the job to reconfigure
        Optional<Job> optJob = Optional.ofNullable(repositoryService.getJob(jobId));
        if (!optJob.isPresent()) {
            LOGGER.error(String.format("Job [%s] not found", jobId));
            throw new IllegalArgumentException(String.format("Job [%s] not found", jobId));
        }
        Job job = optJob.get();

        updateUnchangedTasks(job, reconfigurationPlan);

        Set<Task> deletedTasks = handleDeletedTasks(job, reconfigurationPlan);

        addNewTasksAndDeployments(job, reconfigurationPlan);

        jobService.submitReconfigurationJob(job, reconfigurationPlan, deletedTasks);

        taskService.cleanDeletedTasksAndDeployments(deletedTasks);

        return true;
    }

    private void updateUnchangedTasks(Job job, ReconfigurationJobDefinition reconfigurationPlan) {
        // Getting unchanged tasks from DB
        Set<Task> unchangedTasks = reconfigurationPlan.getUnchangedTasks()
                                                      .stream()
                                                      .map(unchangedTaskName -> repositoryService.getTask(job.getJobId() +
                                                                                                          unchangedTaskName))
                                                      .collect(Collectors.toSet());

        unchangedTasks.forEach(unchangedTask -> taskService.updateUnchangedTask(unchangedTask,
                                                                                reconfigurationPlan,
                                                                                job));
    }

    private Set<Task> handleDeletedTasks(Job job, ReconfigurationJobDefinition reconfigurationPlan) {
        // Getting deleted tasks from DB
        Set<Task> deletedTasks = reconfigurationPlan.getDeletedTasks()
                                                    .stream()
                                                    .map(deletedTaskName -> repositoryService.getTask(job.getJobId() +
                                                                                                      deletedTaskName))
                                                    .collect(Collectors.toSet());

        deletedTasks.forEach(deletedTask -> jobService.removeTask(deletedTask, job));

        return deletedTasks;
    }

    private void addNewTasksAndDeployments(Job job, ReconfigurationJobDefinition reconfigurationPlan) {
        // Creating new tasks
        reconfigurationPlan.getAddedTasks()
                           .forEach(taskReconfigurationDefinition -> taskService.createNewTask(taskReconfigurationDefinition.getTask(),
                                                                                               reconfigurationPlan,
                                                                                               job));

        // Creating new deployments
        reconfigurationPlan.getAddedTasks()
                           .stream()
                           .map(TaskReconfigurationDefinition::getIaasNodeSelection)
                           .forEach(iaasDefinition -> nodeService.addNode(iaasDefinition, job));

        // Adding new monitors
        reconfigurationPlan.getAddedTasks()
                           .stream()
                           .filter(taskReconfigurationDefinition -> taskReconfigurationDefinition.getEmsDeploymentDefinition() != null)
                           .forEach(taskReconfigurationDefinition -> monitoringService.addEmsDeploymentForNode(taskReconfigurationDefinition.getIaasNodeSelection()
                                                                                                                                            .getName(),
                                                                                                               taskReconfigurationDefinition.getEmsDeploymentDefinition()
                                                                                                                                            .getAuthorizationBearer(),
                                                                                                               taskReconfigurationDefinition.getEmsDeploymentDefinition()
                                                                                                                                            .isPrivateIP()));
    }

    /**
     * Check if a specific job is in reconfiguration
     * @param sessionId a valid session id
     * @param jobId The ID of the job
     * @return true or false
     */
    public Boolean isJobInReconfiguration(String sessionId, String jobId) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Validate.notNull(jobId, "The job id received is null. Nothing to be created.");
        JobState jobState = jobService.getJobState(sessionId, jobId);
        return SubmittedJobType.RECONFIGURATION.equals(jobState.getSubmittedJobType()) &&
               jobState.getJobStatus().isJobAlive();
    }
}
