/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;

import org.apache.commons.lang3.Validate;
import org.ow2.proactive.sal.model.*;
import org.ow2.proactive.sal.service.service.application.PASchedulerGateway;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.ow2.proactive.scheduler.common.exception.UserException;
import org.ow2.proactive.scheduler.common.job.JobInfo;
import org.ow2.proactive.scheduler.common.job.JobResult;
import org.ow2.proactive.scheduler.common.job.JobStatus;
import org.ow2.proactive.scheduler.common.job.TaskFlowJob;
import org.ow2.proactive.scheduler.common.task.ScriptTask;
import org.ow2.proactive.scheduler.common.task.TaskResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;


@Log4j2
@Service("JobService")
public class JobService {

    @Autowired
    private PAGatewayService paGatewayService;

    @Autowired
    private PASchedulerGateway schedulerGateway;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private TaskBuilder taskBuilder;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * Create a ProActive job skeleton
     * @param sessionId A valid session id
     * @param job A job skeleton definition in JSON format
     */
    @Transactional
    public Boolean createJob(String sessionId, JobDefinition job) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Validate.notNull(job, "The job received is empty. Nothing to be created.");

        if (repositoryService.getJob(job.getJobInformation().getId()) != null) {
            LOGGER.warn("Job {} already present. Nothing to be done", job.getJobInformation().getId());
            return false;
        }

        LOGGER.info("Creating new job [{}] ...", job);

        Job newJob = new Job();
        newJob.setJobId(job.getJobInformation().getId());
        newJob.setName(job.getJobInformation().getName());
        newJob.setSubmittedJobType(SubmittedJobType.CREATED);
        newJob.setCommunications(job.getCommunications()
                                    .stream()
                                    .collect(Collectors.toMap(Communication::getPortProvided,
                                                              Communication::getPortRequired)));
        List<Task> tasks = new LinkedList<>();
        job.getTasks().forEach(taskDefinition -> {
            LOGGER.info("Creating for job [{}] new task [{}] ...", job.getJobInformation().getId(), taskDefinition);
            Task newTask = new Task();
            newTask.setTaskId(newJob.getJobId() + taskDefinition.getName());
            newTask.setName(taskDefinition.getName());
            Installation installation = taskDefinition.getInstallation();
            newTask.setType(installation.getType());
            LOGGER.info("New task [{}] from job [{}] identified as a [{}] task ...",
                        taskDefinition.getName(),
                        job.getJobInformation().getId(),
                        newTask.getType());
            newTask.setInstallationByType(taskDefinition.getInstallation());

            List<Port> portsToOpen = extractListOfPortsToOpen(taskDefinition.getPorts(), job);
            portsToOpen.forEach(repositoryService::savePort);
            newTask.setPortsToOpen(portsToOpen);
            newTask.setParentTasks(extractParentTasks(job, taskDefinition));

            repositoryService.saveTask(newTask);
            tasks.add(newTask);
            LOGGER.info("Task [{}] created for job [{}]", newTask.getTaskId(), job);
        });

        newJob.setTasks(tasks);

        repositoryService.saveJob(newJob);

        // Trying to fix env vars saving issue
        tasks.forEach(repositoryService::saveTask);

        repositoryService.flush();

        LOGGER.info("Job created: " + newJob.toString());

        return true;
    }

    private List<Port> extractListOfPortsToOpen(List<AbstractPortDefinition> ports, JobDefinition job) {
        List<Port> portsToOpen = new LinkedList<>();
        if (ports != null) {
            ports.forEach(portDefinition -> {
                if (portDefinition instanceof PortProvided) {
                    Port portToOpen = new Port(((PortProvided) portDefinition).getName(),
                                               ((PortProvided) portDefinition).getPort());
                    Map.Entry<String, String> requestedPortInformation = findRequiredPortAndComponent(job,
                                                                                                      ((PortProvided) portDefinition).getName());
                    portToOpen.setRequestedName(requestedPortInformation.getValue());
                    portToOpen.setRequiringComponentName(requestedPortInformation.getKey());
                    portsToOpen.add(portToOpen);
                }
            });
        }
        return portsToOpen;
    }

    private Map.Entry<String, String> findRequiredPortAndComponent(JobDefinition job, String providedPortName) {
        for (Communication communication : job.getCommunications()) {
            if (Objects.equals(providedPortName, communication.getPortProvided())) {
                return new AbstractMap.SimpleEntry<>(findRequiringComponent(job, communication.getPortRequired()),
                                                     communication.getPortRequired());
            }
        }
        return new AbstractMap.SimpleEntry<>("", "NOTREQUESTED_" + providedPortName);
    }

    private String findRequiringComponent(JobDefinition job, String portRequired) {
        for (TaskDefinition task : job.getTasks()) {
            for (AbstractPortDefinition portDefinition : task.getPorts()) {
                if (portDefinition instanceof PortRequired && portRequired.equals(portDefinition.getName())) {
                    return task.getName();
                }
            }
        }
        return "";
    }

    private Map<String, String> extractParentTasks(JobDefinition job, TaskDefinition task) {
        Map<String, String> parentTasks = new HashMap<>();
        if (task.getPorts() != null) {
            task.getPorts().forEach(portDefinition -> {
                //                                if (portDefinition instanceof PortRequired
                //                                        && ((PortRequired) portDefinition).isMandatory()) {
                if (portDefinition instanceof PortRequired) {
                    LOGGER.debug("Mandatory required port detected");
                    String providedPortName = findProvidedPort(job, ((PortRequired) portDefinition).getName());
                    parentTasks.put(portDefinition.getName(), findTaskByProvidedPort(job, providedPortName));
                }
            });
        }

        return parentTasks;
    }

    private String findProvidedPort(JobDefinition job, String requiredPortName) {
        for (Communication communication : job.getCommunications()) {
            if (Objects.equals(requiredPortName, communication.getPortRequired()))
                return communication.getPortProvided();
        }
        LOGGER.error("Required port [{}] not found in communications of job [{}]",
                     requiredPortName,
                     job.getJobInformation().getId());
        throw new NotFoundException("Required port [" + requiredPortName + "] not found in communications of job: " +
                                    job.getJobInformation().getId());
    }

    private String findTaskByProvidedPort(JobDefinition job, String providedPortName) {
        List<TaskDefinition> tasks = job.getTasks();
        for (TaskDefinition task : tasks) {
            if (taskProvidesPort(task, providedPortName))
                return task.getName();
        }
        LOGGER.error("Task that provides port [{}] was not found in job [{}].",
                     providedPortName,
                     job.getJobInformation().getId());
        throw new NotFoundException("Task that provides port [" + providedPortName + "] was not found in job: " +
                                    job.getJobInformation().getId());
    }

    private boolean taskProvidesPort(TaskDefinition task, String providedPortName) {
        for (PortDefinition port : task.getPorts()) {
            if (port instanceof PortProvided && Objects.equals(providedPortName, ((PortProvided) port).getName()))
                return true;
        }
        return false;
    }

    /**
     * Get all job skeletons
     * @param sessionId A valid session id
     * @return List of all table Job's entries
     */
    public List<Job> getJobs(String sessionId) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        return repositoryService.listJobs();
    }

    /**
     * Stop jobs
     * @param sessionId A valid session id
     * @param jobIds List of job IDs to stop
     */
    public Long stopJobs(String sessionId, List<String> jobIds) throws NotConnectedException {
        for (String jobId : jobIds)
            stopJob(sessionId, jobId);
        return 0L;
    }

    /**
     * Get a specific job skeleton
     * @param sessionId A valid session id
     * @param jobId A valid job identifier
     * @return A Job instance
     */
    public Job getJob(String sessionId, String jobId) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        return repositoryService.getJob(jobId);
    }

    /**
     * Return the dot format of the application's graph
     * @param sessionId A valid session id
     * @param jobId The ID of the job
     * @return The graph in dot format
     */
    public String getGraphInDotFormat(String sessionId, String jobId) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        // StringBuilder used to write the syntax of the application graph in dot format
        StringBuilder dotGraphSyntax = new StringBuilder();

        // Get the job by jobId from the DB
        Job applicationJob = repositoryService.getJob(jobId);

        LOGGER.debug("Dot graph creation for the job: " + applicationJob.toString());

        // Write the dot file header
        dotGraphSyntax.append("digraph g {\n");

        // Get the job tasks
        LinkedList<Task> jobTasks = new LinkedList<>(applicationJob.getTasks());

        // Add the mandatory connections between the tasks
        jobTasks.forEach(task -> {

            // Write the dot representation of the task
            dotGraphSyntax.append(task.getName() + ";\n");

            // Get the current task name
            String childTask = task.getName();

            // Get the list of the parent tasks
            Map<String, String> parentTasks = task.getParentTasks();

            // Check for Mandatory connections
            // If the list is empty there are no mandatory connections
            parentTasks.values().forEach(parentTask -> {

                // Write the dot syntax of the connection between the two tasks
                dotGraphSyntax.append(parentTask + "->" + childTask + " [fillcolor=red, fontcolor=red, color=red]" +
                                      ";");

                dotGraphSyntax.append("\n");
            });

        });

        // Write the dot file end character
        dotGraphSyntax.append("}\n");

        LOGGER.debug("Dot graph created");

        return dotGraphSyntax.toString();
    }

    /**
     * Submit a job constructed in lazy-mode to the ProActive Scheduler
     * @param sessionId A valid session id
     * @param jobId A constructed job identifier
     * @return The submitted job id
     */
    @Transactional
    public Long submitJob(String sessionId, String jobId) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Job jobToSubmit = repositoryService.getJob(jobId);
        LOGGER.info("Job found to submit: " + jobToSubmit.toString());

        TaskFlowJob paJob = new TaskFlowJob();
        paJob.setName(jobToSubmit.getName());
        LOGGER.info("Job created: " + paJob.toString());

        jobToSubmit.getTasks()
                   .stream()
                   .filter(task -> task.getDeployments() != null && !task.getDeployments().isEmpty())
                   .forEach(task -> {
                       List<ScriptTask> scriptTasks = taskBuilder.buildPATask(task, jobToSubmit);

                       addAllScriptTasksToPAJob(paJob, task, scriptTasks);
                       repositoryService.saveTask(task);
                   });

        setAllMandatoryDependencies(paJob, jobToSubmit);

        addInitChannelsTaskWithDependencies(paJob, jobToSubmit);
        addCleanChannelsTaskWithDependencies(paJob, jobToSubmit);

        paJob.setMaxNumberOfExecution(2);
        paJob.setProjectName("NebulOuS");

        long submittedJobId = -1L;
        if (!paJob.getTasks().isEmpty()) {
            submittedJobId = schedulerGateway.submit(paJob).longValue();
            jobToSubmit.setSubmittedJobId(submittedJobId);
            jobToSubmit.setSubmittedJobType(SubmittedJobType.FIRST_DEPLOYMENT);
            LOGGER.info("Job submitted successfully. ID = " + submittedJobId);
        } else {
            LOGGER.warn("The job " + jobId + " is already deployed. Nothing to be submitted here.");
        }

        repositoryService.saveJob(jobToSubmit);
        repositoryService.flush();

        return (submittedJobId);
    }

    private void addAllScriptTasksToPAJob(TaskFlowJob paJob, Task task, List<ScriptTask> scriptTasks) {
        scriptTasks.forEach(scriptTask -> {
            try {
                paJob.addTask(scriptTask);
            } catch (UserException e) {
                LOGGER.error("Task [{}] could not be added due to: {}", task.getName(), e.toString());
            }
        });
    }

    protected void setAllMandatoryDependencies(TaskFlowJob paJob, Job jobToSubmit) {
        jobToSubmit.getTasks().stream().filter(task -> !task.getDeployments().isEmpty()).forEach(task -> {
            if (task.getParentTasks() != null && !task.getParentTasks().isEmpty()) {
                task.getParentTasks().forEach((requiredPortName, parentTaskName) -> {
                    paJob.getTasks().forEach(paTask -> {
                        paJob.getTasks().forEach(paParentTask -> {
                            if (paTask.getName().contains(task.getName()) &&
                                paParentTask.getName().contains(parentTaskName)) {
                                if (paTask.getName().contains(task.getDeploymentFirstSubmittedTaskName())) {
                                    Task maybeParentTask = jobToSubmit.findTask(parentTaskName);
                                    if (maybeParentTask.getDeploymentLastSubmittedTaskName() != null &&
                                        paParentTask.getName()
                                                    .contains(maybeParentTask.getDeploymentLastSubmittedTaskName())) {
                                        paTask.addDependence(paParentTask);
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
     * Get a ProActive job state
     * @param sessionId A valid session id
     * @param jobId A job ID
     * @return The job state
     */
    public JobState getJobState(String sessionId, String jobId) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        LOGGER.info("Getting job " + jobId + " state ");
        Optional<Job> optJob = Optional.ofNullable(repositoryService.getJob(jobId));
        if (!optJob.isPresent()) {
            LOGGER.error(String.format("Job [%s] not found", jobId));
            return new JobState(SubmittedJobType.UNKNOWN, null);
        }
        Job submittedJob = optJob.get();
        LOGGER.info("Job " + jobId + " mapped to the submitted ProActive job: " + submittedJob.getSubmittedJobId() +
                    " of type: " + submittedJob.getSubmittedJobType());
        JobStatus jobStatus = null;
        if (submittedJob.getSubmittedJobId() != 0L) {
            jobStatus = schedulerGateway.getJobState(String.valueOf(submittedJob.getSubmittedJobId())).getStatus();
            LOGGER.info("Returned state: " + jobStatus.toString() + " for job: " + jobId);
        }
        return new JobState(submittedJob.getSubmittedJobType(), jobStatus);
    }

    /**
     * Wait for execution and get results of a job
     * @param sessionId A valid session id
     * @param jobId A job ID
     * @param timeout The timeout
     * @return The job result
     */
    public JobResult waitForJob(String sessionId, String jobId, long timeout) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Job submittedJob = repositoryService.getJob(jobId);
        JobResult jobResult = schedulerGateway.waitForJob(String.valueOf(submittedJob.getSubmittedJobId()), timeout);
        LOGGER.info("Results of job: " + jobId + " fetched successfully: " +
                    Optional.ofNullable(jobResult).map(JobResult::toString).orElse(null));
        return jobResult;
    }

    public Map<String, Serializable> getJobResultMaps(String sessionId, String jobId, long timeout)
            throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException("Session is not active for ID: " + sessionId);
        }
        // Fetch the job from the repository
        Job submittedJob = repositoryService.getJob(jobId);
        if (submittedJob == null) {
            LOGGER.warn("No job found with ID: {}", jobId);
            return Collections.emptyMap(); // Return an empty map if no job is found
        }

        // Prepare the job ID list for the scheduler query
        List<String> jobIds = Collections.singletonList(String.valueOf(submittedJob.getSubmittedJobId()));

        // Fetch job results
        Map<Long, Map<String, Serializable>> jobResult;
        try {
            jobResult = schedulerGateway.getJobResultMaps(jobIds);
        } catch (Exception e) {
            LOGGER.error("Failed to fetch results for job ID: {}. Error: {}", jobId, e.getMessage(), e);
            throw new RuntimeException("Error fetching job results for job ID: " + jobId, e);
        }

        // Log and return results
        LOGGER.info("Results of job {} fetched successfully: {}",
                    jobId,
                    Optional.ofNullable(jobResult).map(Object::toString).orElse("No results available"));

        // Return the result for the specific job ID, or an empty map if no results are available
        return jobResult != null ? jobResult.getOrDefault(submittedJob.getSubmittedJobId(), Collections.emptyMap())
                                 : Collections.emptyMap();
    }

    /**
     * Stop the deployed job
     * @param sessionId A valid session id
     * @param jobId A deployed job identifier
     * @return The submitted stopping job id
     */
    public Long stopJob(String sessionId, String jobId) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        LOGGER.info("Stopping job " + jobId);
        Optional<Job> optJob = Optional.ofNullable(repositoryService.getJob(jobId));
        if (!optJob.isPresent()) {
            LOGGER.error(String.format("Job [%s] not found", jobId));
            return 0L;
        }
        Job job = optJob.get();
        job.getTasks().forEach(task -> {
            List<String> iaasNodesToBeRemoved = task.getDeployments()
                                                    .stream()
                                                    .filter(deployment -> NodeType.IAAS.equals(deployment.getDeploymentType()))
                                                    .filter(Deployment::getIsDeployed)
                                                    .map(Deployment::getNodeName)
                                                    .collect(Collectors.toList());

            List<String> byonNodesToBeRemoved = task.getDeployments()
                                                    .stream()
                                                    .filter(deployment -> NodeType.BYON.equals(deployment.getDeploymentType()))
                                                    .filter(Deployment::getIsDeployed)
                                                    .map(Deployment::getNodeName)
                                                    .collect(Collectors.toList());

            List<Deployment> taskIaasDeployments = task.getDeployments()
                                                       .stream()
                                                       .filter(deployment -> NodeType.IAAS.equals(deployment.getDeploymentType()))
                                                       .collect(Collectors.toList());

            List<Deployment> taskByonDeployments = task.getDeployments()
                                                       .stream()
                                                       .filter(deployment -> NodeType.BYON.equals(deployment.getDeploymentType()))
                                                       .collect(Collectors.toList());

            taskIaasDeployments.forEach(deployment -> {
                deployment.getTask().removeDeployment(deployment);
                repositoryService.saveTask(deployment.getTask());
                deployment.getPaCloud().removeDeployment(deployment);
                repositoryService.savePACloud(deployment.getPaCloud());
                repositoryService.deleteDeployment(deployment);
            });

            taskByonDeployments.forEach(deployment -> {
                deployment.getTask().removeDeployment(deployment);
                repositoryService.saveTask(deployment.getTask());
                repositoryService.deleteDeployment(deployment);
            });

            task.getDeployments()
                .stream()
                .filter(deployment -> NodeType.IAAS.equals(deployment.getDeploymentType()))
                .filter(Deployment::getIsDeployed)
                .forEach(deployment -> {
                    deployment.getIaasNode().decDeployedNodes(1L);
                    repositoryService.saveIaasNode(deployment.getIaasNode());
                }

            );

            try {
                nodeService.removeNodes(sessionId, iaasNodesToBeRemoved, true);

                ////////////////////////////////////////////////
                // Is that correct ???
                // Should we just delete the token set on the node ???
                nodeService.removeNodes(sessionId, byonNodesToBeRemoved, true);
                // To verify the behavior needed in upperware.
                // If we don't remove BYON nodes we should stop the component in it with a script.
                ////////////////////////////////////////////////
            } catch (NotConnectedException nce) {
                LOGGER.error("Not connected exception during nodes removal: " + Arrays.toString(nce.getStackTrace()));
            }
        });

        job.setSubmittedJobType(SubmittedJobType.STOP);
        //TODO: This should be updated if a stopping workflow will be submitted in future.
        // Please think of the impact of this on PaGateway.getJobState()
        //        job.setSubmittedJobId(0L);
        repositoryService.saveJob(job);

        repositoryService.flush();

        // This is kept for future improvement.
        // It is in case we want to return the submitted stopping job's ID
        return 0L;
    }

    /**
     * Kill a job
     * @param sessionId A valid session id
     * @param jobId A job ID
     * @return True if the job has been killed, False otherwise
     */
    public Boolean killJob(String sessionId, String jobId) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        boolean result = false;
        Job submittedJob = repositoryService.getJob(jobId);
        if (submittedJob != null && submittedJob.getSubmittedJobId() > 0L) {
            result = schedulerGateway.killJob(String.valueOf(submittedJob.getSubmittedJobId()));
            if (result) {
                LOGGER.info("The job : {} could be killed successfully.", jobId);
            } else {
                LOGGER.error("The job : {} could not be killed.", jobId);
            }
        } else {
            LOGGER.warn("The job : {} has not been submitted. Nothing to be killed.", jobId);
        }

        return result;
    }

    /**
     * Get a specific job skeleton
     * @param sessionId A valid session id
     * @param taskId A valid task identifier
     * @return A Job instance
     */
    public Task getTask(String sessionId, String taskId) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        return repositoryService.getTask(taskId);
    }

    /**
     * Wait for a task
     * @param sessionId A valid session id
     * @param jobId A job ID
     * @param taskName A task name
     * @param timeout The waiting timeout
     * @return The task results
     */
    public Map<String, TaskResult> waitForTask(String sessionId, String jobId, String taskName, long timeout)
            throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Job submittedJob = repositoryService.getJob(jobId);
        Task createdTask = submittedJob.findTask(taskName);
        Map<String, TaskResult> taskResultsMap = new HashMap<>();
        createdTask.getSubmittedTaskNames()
                   .forEach(submittedTaskName -> taskResultsMap.put(submittedTaskName,
                                                                    schedulerGateway.waitForTask(String.valueOf(submittedJob.getSubmittedJobId()),
                                                                                                 submittedTaskName,
                                                                                                 timeout)));
        LOGGER.info("Results of task: " + taskName + " fetched successfully: " + taskResultsMap.toString());
        return taskResultsMap;
    }

    /**
     * Get a task result
     * @param sessionId A valid session id
     * @param jobId A job ID
     * @param taskName A task name
     * @return The task results
     */
    public Map<String, TaskResult> getTaskResult(String sessionId, String jobId, String taskName)
            throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Job submittedJob = repositoryService.getJob(jobId);
        Task createdTask = submittedJob.findTask(taskName);
        Map<String, TaskResult> taskResultsMap = new HashMap<>();
        createdTask.getSubmittedTaskNames()
                   .forEach(submittedTaskName -> taskResultsMap.put(submittedTaskName,
                                                                    schedulerGateway.getTaskResult(String.valueOf(submittedJob.getSubmittedJobId()),
                                                                                                   submittedTaskName)));
        LOGGER.info("Results of task: " + taskName + " fetched successfully: " + taskResultsMap.toString());
        return taskResultsMap;
    }

    /**
     * Kill all active jobs in ProActive Scheduler
     * @param sessionId A valid session id
     */
    public Boolean killAllActivePAJobs(String sessionId) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        List<JobInfo> activeJobInfos = schedulerGateway.getActiveJobs(0, 1000);
        activeJobInfos.forEach(activeJobInfo -> schedulerGateway.killJob(activeJobInfo.getJobId().value()));
        return true;
    }

    /**
     * Remove all jobs from the ProActive Scheduler
     * @param sessionId A valid session id
     */
    public Boolean removeAllPAJobs(String sessionId) throws NotConnectedException {
        killAllActivePAJobs(sessionId);
        schedulerGateway.getJobs(0, 1000)
                        .getList()
                        .forEach(jobInfo -> schedulerGateway.removeJob(jobInfo.getJobId().value()));
        return true;
    }

    /**
     * Delete a task from a job
     * @param deletedTask The task to be deleted
     * @param job The job
     */
    public void removeTask(Task deletedTask, Job job) {
        if (job.getTasks().stream().anyMatch(task -> task.getParentTasks().containsValue(deletedTask.getName())))
            throw new DataIntegrityViolationException("Some of job [{}] task depends on task [{}]. Task removal from job aborted.");
        job.removeTask(deletedTask);
    }

    /**
     * Submit a reconfiguration job to PA Scheduler
     * @param job The job to reconfigure
     * @param reconfigurationPlan The reconfiguration plan
     * @param deletedTasks The deleted tasks
     */
    @Transactional
    public void submitReconfigurationJob(Job job, ReconfigurationJobDefinition reconfigurationPlan,
            Set<Task> deletedTasks) {
        LOGGER.info("Job [{}] to be reconfigured ...", job.getJobId());

        TaskFlowJob paJob = new TaskFlowJob();
        paJob.setName(job.getName() + "_Reconfiguration");
        LOGGER.info("Reconfiguration job created: " + paJob.toString());

        job.getTasks()
           .stream()
           .filter(task -> task.getDeployments() != null && !task.getDeployments().isEmpty())
           .forEach(task -> {
               List<ScriptTask> scriptTasks = taskBuilder.buildReconfigurationPATask(task, job, reconfigurationPlan);

               if (scriptTasks != null && !scriptTasks.isEmpty()) {
                   addAllScriptTasksToPAJob(paJob, task, scriptTasks);
                   repositoryService.saveTask(task);
               }
           });

        setAllReconfigurationMandatoryDependencies(paJob, job);

        deletedTasks.forEach(deletedTask -> {
            List<ScriptTask> scriptTasks = taskBuilder.buildReconfigurationDeletedTask(deletedTask,
                                                                                       job,
                                                                                       reconfigurationPlan);

            if (scriptTasks != null && !scriptTasks.isEmpty()) {
                addAllScriptTasksToPAJob(paJob, deletedTask, scriptTasks);
            }
        });

        setAllReconfigurationDeletedTaskDependencies(paJob, job.getSinkTasks());

        addInitChannelsTaskWithDependencies(paJob, job);
        addCleanChannelsTaskWithDependencies(paJob, job);

        paJob.setMaxNumberOfExecution(2);
        paJob.setProjectName("NebulOuS");

        long submittedJobId = schedulerGateway.submit(paJob).longValue();
        job.setSubmittedJobId(submittedJobId);
        job.setSubmittedJobType(SubmittedJobType.RECONFIGURATION);

        repositoryService.saveJob(job);
        repositoryService.flush();
        LOGGER.info("Reconfiguration job with [{}] submitted successfully. ID = {}", job.getJobId(), submittedJobId);
    }

    private void setAllReconfigurationDeletedTaskDependencies(TaskFlowJob paJob, Set<Task> sinkTasks) {
        Set<String> lastSubmittedSinkTaskNames = sinkTasks.stream()
                                                          .map(Task::getDeploymentLastSubmittedTaskName)
                                                          .collect(Collectors.toSet());
        paJob.getTasks()
             .stream()
             .filter(paTask -> paTask.getName().startsWith("removeNode_"))
             .forEach(paTask -> paJob.getTasks()
                                     .stream()
                                     .filter(paParentTask -> lastSubmittedSinkTaskNames.stream()
                                                                                       .anyMatch(lastSubmittedLeafTaskName -> paParentTask.getName()
                                                                                                                                          .startsWith(lastSubmittedLeafTaskName)))
                                     .forEach(paTask::addDependence));
    }

    public void addInitChannelsTaskWithDependencies(TaskFlowJob paJob, Job job) {
        ScriptTask scriptTask = taskBuilder.buildInitChannelsTask(job);
        Set<String> firstSubmittedRootTaskNames = job.getRootTasks()
                                                     .stream()
                                                     .filter(task -> task.getDeployments() != null &&
                                                                     !task.getDeployments().isEmpty())
                                                     .map(Task::getDeploymentFirstSubmittedTaskName)
                                                     .collect(Collectors.toSet());
        //TODO: This to be improved in case of scaling (
        // lastSubmitted and firstSubmitted task names does not match ALL deployments since some are scaled
        // and others not.
        paJob.getTasks()
             .stream()
             .filter(paParentTask -> firstSubmittedRootTaskNames.stream()
                                                                .anyMatch(firstSubmittedRootTaskName -> paParentTask.getName()
                                                                                                                    .startsWith(firstSubmittedRootTaskName)))
             .forEach(rootTask -> rootTask.addDependence(scriptTask));
        try {
            paJob.addTask(scriptTask);
        } catch (UserException e) {
            LOGGER.error("Task [InitChannels] could not be added due to: {}", e.toString());
        }
    }

    public void addCleanChannelsTaskWithDependencies(TaskFlowJob paJob, Job job) {
        ScriptTask scriptTask = taskBuilder.buildCleanChannelsTask(job);
        Set<String> lastSubmittedSinkTaskNames = job.getSinkTasks()
                                                    .stream()
                                                    .filter(task -> task.getDeployments() != null &&
                                                                    !task.getDeployments().isEmpty())
                                                    .map(Task::getDeploymentLastSubmittedTaskName)
                                                    .collect(Collectors.toSet());
        //TODO: This to be improved in case of scaling (
        // lastSubmitted and firstSubmitted task names does not match ALL deployments since some are scaled
        // and others not.
        paJob.getTasks()
             .stream()
             .filter(paParentTask -> lastSubmittedSinkTaskNames.stream()
                                                               .anyMatch(lastSubmittedSinkTaskName -> paParentTask.getName()
                                                                                                                  .startsWith(lastSubmittedSinkTaskName)))
             .forEach(scriptTask::addDependence);
        try {
            paJob.addTask(scriptTask);
        } catch (UserException e) {
            LOGGER.error("Task [CleanChannels] could not be added due to: {}", e.toString());
        }
    }

    private void setAllReconfigurationMandatoryDependencies(TaskFlowJob paJob, Job job) {
        setAllMandatoryDependencies(paJob, job);
    }

    public Long submitOneTaskJob(String sessionId, String input, String token, String jobName, String type,
            String nodeName) throws NotConnectedException, IOException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        TaskFlowJob paJob = new TaskFlowJob();
        paJob.setName(jobName);
        LOGGER.info("Job created: " + paJob);
        try {
            if (Objects.equals(type, "basic")) {
                paJob.addTask(taskBuilder.createOneNodeTask(input, token, jobName));
            } else if (Objects.equals(type, "delete")) {
                paJob.addTask(taskBuilder.createDeleteNodeTask(input));
            } else if (Objects.equals(type, "drain")) {
                paJob.addTask(taskBuilder.createDrainNodeTask(input, token));
            } else if (Objects.equals(type, "drain-delete")) {
                ScriptTask parentTask = taskBuilder.createDrainNodeTask(nodeName, token);
                ScriptTask childTask = taskBuilder.createDeleteNodeTask(input);
                childTask.addDependence(parentTask);
                paJob.addTask(parentTask);
                paJob.addTask(childTask);
            } else {
                LOGGER.error("type of submitOneTaskJob \"{}\" is not supported!", type);
                throw new IOException();
            }

        } catch (UserException e) {
            throw new RuntimeException(e);
        }
        paJob.setMaxNumberOfExecution(2);
        paJob.setProjectName("NebulOuS");
        long submittedJobId = -1L;
        if (!paJob.getTasks().isEmpty()) {
            submittedJobId = schedulerGateway.submit(paJob).longValue();
            LOGGER.info("One Task Job submitted successfully. ID = " + submittedJobId);
        } else {
            LOGGER.warn("The job is empty!");
        }

        return submittedJobId;

    }

}
