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

import java.util.*;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;

import org.ow2.proactive.sal.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;


@Log4j2
@Service("TaskService")
public class TaskService {

    @Autowired
    private RepositoryService repositoryService;

    /**
     * Update an unchanged task for reconfiguration
     * @param unchangedTask An unchanged task
     * @param reconfigurationPlan The reconfiguration plan
     * @param job The reconfigured job
     */
    @Transactional
    public synchronized void updateUnchangedTask(Task unchangedTask, ReconfigurationJobDefinition reconfigurationPlan,
            Job job) {
        LOGGER.info("Handling unchanged task [{}] ...", unchangedTask.getTaskId());
        Map<String, String> updatedParentTasks;

        // Clean parent tasks that will be removed
        updatedParentTasks = unchangedTask.getParentTasks()
                                          .entrySet()
                                          .stream()
                                          .filter(parentTaskEntry -> !reconfigurationPlan.getDeletedTasks()
                                                                                         .contains(parentTaskEntry.getValue()))
                                          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Add new added parent tasks
        Set<Task> deletedParentTasks = unchangedTask.getParentTasks()
                                                    .values()
                                                    .stream()
                                                    .filter(parentTaskName -> reconfigurationPlan.getDeletedTasks()
                                                                                                 .contains(parentTaskName))
                                                    .map(parentTaskName -> repositoryService.getTask(job.getJobId() +
                                                                                                     parentTaskName))
                                                    .collect(Collectors.toSet());
        updatedParentTasks.putAll(extractNewParentTasks(deletedParentTasks, reconfigurationPlan, job));

        // Update parent tasks list
        unchangedTask.setParentTasks(updatedParentTasks);

        // Update ports to open with new added children tasks
        unchangedTask.getPortsToOpen().forEach(port -> {
            Map.Entry<String, String> requestedPortInformation = findRequiredPort(reconfigurationPlan,
                                                                                  job,
                                                                                  port.getName());
            port.setRequestedName(requestedPortInformation.getValue());
            port.setRequiringComponentName(requestedPortInformation.getKey());
        });
    }

    /**
     * Create a new task during job reconfiguration
     * @param taskDefinition A task definition
     * @param reconfigurationPlan The reconfiguration plan
     * @param job The reconfigured job
     */
    @Transactional
    public void createNewTask(TaskDefinition taskDefinition, ReconfigurationJobDefinition reconfigurationPlan,
            Job job) {
        LOGGER.info("Creating for job [{}] new task [{}] ...", job.getJobId(), taskDefinition);
        Task newTask = new Task();
        newTask.setTaskId(job.getJobId() + taskDefinition.getName());
        newTask.setName(taskDefinition.getName());
        Installation installation = taskDefinition.getInstallation();
        newTask.setType(installation.getType());
        LOGGER.info("New task [{}] from job [{}] identified as a [{}] task ...",
                    taskDefinition.getName(),
                    job.getJobId(),
                    newTask.getType());
        newTask.setInstallationByType(taskDefinition.getInstallation());

        List<Port> portsToOpen = extractListOfPortsToOpen(taskDefinition.getPorts(), reconfigurationPlan, job);
        portsToOpen.forEach(repositoryService::savePort);
        newTask.setPortsToOpen(portsToOpen);
        newTask.setParentTasks(extractParentTasks(taskDefinition, reconfigurationPlan, job));

        repositoryService.saveTask(newTask);
        job.addTask(newTask);
        repositoryService.flush();
        LOGGER.info("Task [{}] created for job [{}]", newTask.getTaskId(), job);
    }

    @Transactional
    public void cleanDeletedTasksAndDeployments(Set<Task> deletedTasks) {
        deletedTasks.forEach(repositoryService::deleteTask);
    }

    private List<Port> extractListOfPortsToOpen(List<AbstractPortDefinition> ports,
            ReconfigurationJobDefinition reconfigurationPlan, Job job) {
        List<Port> portsToOpen = new LinkedList<>();
        if (ports == null)
            return portsToOpen;

        ports.forEach(portDefinition -> {
            if (portDefinition instanceof PortProvided) {
                Port portToOpen = new Port(((PortProvided) portDefinition).getName(),
                                           ((PortProvided) portDefinition).getPort());
                Map.Entry<String, String> requestedPortInformation = findRequiredPort(reconfigurationPlan,
                                                                                      job,
                                                                                      ((PortProvided) portDefinition).getName());
                portToOpen.setRequestedName(requestedPortInformation.getValue());
                portToOpen.setRequiringComponentName(requestedPortInformation.getKey());
                portsToOpen.add(portToOpen);
            }
        });

        return portsToOpen;
    }

    private Map<String, String> extractParentTasks(TaskDefinition taskDefinition,
            ReconfigurationJobDefinition reconfigurationPlan, Job job) {
        Map<String, String> parentTasks = new HashMap<>();
        if (taskDefinition.getPorts() != null) {
            taskDefinition.getPorts().forEach(portDefinition -> {
                if (portDefinition instanceof PortRequired) {
                    LOGGER.debug("Mandatory required port [{}] detected for task [{}]",
                                 portDefinition.getName(),
                                 taskDefinition.getName());
                    String providedPortName = findProvidedPortName(reconfigurationPlan,
                                                                   ((PortRequired) portDefinition).getName(),
                                                                   job.getJobId());
                    parentTasks.put(portDefinition.getName(),
                                    findTaskByProvidedPortName(reconfigurationPlan, providedPortName, job));
                }
            });
        }

        return parentTasks;
    }

    private String findProvidedPortName(ReconfigurationJobDefinition reconfigurationPlan, String requestedPortName,
            String jobId) {
        for (Communication communication : reconfigurationPlan.getCommunications()) {
            if (Objects.equals(requestedPortName, communication.getPortRequired()))
                return communication.getPortProvided();
        }
        LOGGER.error("Required port [{}] not found in communications of reconfiguration plan for job [{}]",
                     requestedPortName,
                     jobId);
        throw new NotFoundException("Required port [" + requestedPortName +
                                    "] not found iin communications of reconfiguration plan for job: " + jobId);
    }

    private String findTaskByProvidedPortName(ReconfigurationJobDefinition reconfigurationPlan, String providedPortName,
            Job job) {
        // Check from reconfiguration plan if the parent task is newly added
        List<TaskReconfigurationDefinition> tasksReconfiguration = reconfigurationPlan.getAddedTasks();
        for (TaskReconfigurationDefinition taskReconfiguration : tasksReconfiguration) {
            if (taskProvidesPort(taskReconfiguration, providedPortName))
                return taskReconfiguration.getTask().getName();
        }

        // Check from job if task is unchanged
        for (Task task : job.getTasks()) {
            if (taskProvidesPort(task, providedPortName))
                return task.getName();
        }

        LOGGER.error("Task that provides port [{}] was not found in reconfiguration plan for job [{}].",
                     providedPortName,
                     job.getJobId());
        throw new NotFoundException("Task that provides port [" + providedPortName +
                                    "] was not found in reconfiguration plan for job: " + job.getJobId());
    }

    private Map<String, String> extractNewParentTasks(Set<Task> deletedParentTasks,
            ReconfigurationJobDefinition reconfigurationPlan, Job job) {
        Map<String, String> newParentTasks = new HashMap<>();
        deletedParentTasks.forEach(deletedParentTask -> {
            LOGGER.debug("Mandatory required port detected");
            deletedParentTask.getPortsToOpen().forEach(portToOpen -> {
                String providedPortName = findProvidedPortName(reconfigurationPlan,
                                                               portToOpen.getRequestedName(),
                                                               job.getJobId());
                newParentTasks.put(portToOpen.getRequestedName(),
                                   findTaskByProvidedPortName(reconfigurationPlan, providedPortName, job));
            });
        });
        return newParentTasks;
    }

    private boolean taskProvidesPort(TaskReconfigurationDefinition taskReconfiguration, String providedPortName) {
        for (AbstractPortDefinition port : taskReconfiguration.getTask().getPorts()) {
            if (port instanceof PortProvided && Objects.equals(providedPortName, ((PortProvided) port).getName()))
                return true;
        }
        return false;
    }

    private Map.Entry<String, String> findRequiredPort(ReconfigurationJobDefinition reconfigurationPlan, Job job,
            String providedPortName) {
        for (Communication communication : reconfigurationPlan.getCommunications()) {
            if (Objects.equals(providedPortName, communication.getPortProvided())) {
                return new AbstractMap.SimpleEntry<>(findRequiringComponent(reconfigurationPlan,
                                                                            job,
                                                                            communication.getPortRequired()),
                                                     communication.getPortRequired());
            }
        }
        return new AbstractMap.SimpleEntry<>("", "NOTREQUESTED_" + providedPortName);
    }

    private String findRequiringComponent(ReconfigurationJobDefinition reconfigurationPlan, Job job,
            String portRequired) {
        for (TaskReconfigurationDefinition task : reconfigurationPlan.getAddedTasks()) {
            for (AbstractPortDefinition portDefinition : task.getTask().getPorts()) {
                if (portDefinition instanceof PortRequired && portRequired.equals(portDefinition.getName())) {
                    return task.getTask().getName();
                }
            }
        }
        for (Task task : job.getTasks()) {
            for (Port port : task.getPortsToOpen())
                if (port.getRequestedName().equals(portRequired))
                    return !reconfigurationPlan.getDeletedTasks().contains(port.getRequiringComponentName())
                                                                                                             ? port.getRequiringComponentName()
                                                                                                             : "";
            if (task.getParentTasks().keySet().stream().anyMatch(key -> key.equals(portRequired)))
                return !reconfigurationPlan.getDeletedTasks().contains(task.getName()) ? task.getName() : "";
        }

        return "";
    }

    private boolean taskProvidesPort(Task task, String providedPortName) {
        for (Port port : task.getPortsToOpen()) {
            if (providedPortName.equals(port.getName()))
                return true;
        }
        return false;
    }
}
