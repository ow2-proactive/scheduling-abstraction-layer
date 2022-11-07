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
package org.ow2.proactive.sal.service.service.infrastructure;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.security.auth.login.LoginException;

import org.ow2.proactive.db.SortOrder;
import org.ow2.proactive.db.SortParameter;
import org.ow2.proactive.sal.service.util.SchedulerConnectionHelper;
import org.ow2.proactive.scheduler.common.JobFilterCriteria;
import org.ow2.proactive.scheduler.common.JobSortParameter;
import org.ow2.proactive.scheduler.common.Page;
import org.ow2.proactive.scheduler.common.exception.*;
import org.ow2.proactive.scheduler.common.job.*;
import org.ow2.proactive.scheduler.common.task.TaskResult;
import org.ow2.proactive_grid_cloud_portal.smartproxy.RestSmartProxyImpl;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service("PASchedulerGatewayService")
public class PASchedulerGateway {

    private RestSmartProxyImpl restSmartProxy;

    protected static final List<SortParameter<JobSortParameter>> DEFAULT_JOB_SORT_PARAMS = Arrays.asList(new SortParameter<>(JobSortParameter.STATE,
                                                                                                                             SortOrder.ASC),
                                                                                                         new SortParameter<>(JobSortParameter.ID,
                                                                                                                             SortOrder.DESC));

    /**
     * Init a gateway to the ProActive Scheduler
     * @param paUrl ProActive URL (exp: http://try.activeeon.com:8080/)
     */
    public void init(String paUrl) {
        SchedulerConnectionHelper.init(paUrl);
    }

    /**
     * Submit a ProActive job to the scheduler
     * @param job A ProActive job
     * @return JobId
     */
    public JobId submit(Job job) {
        reconnectIfDisconnected();
        JobId jobId = null;
        LOGGER.debug("Submitting job: " + job.toString());
        try {
            jobId = restSmartProxy.submit(job);
        } catch (NotConnectedException nce) {
            LOGGER.error("ERROR: Not able to submit the job due to a NotConnectedException: " + nce.toString());
        } catch (PermissionException pe) {
            LOGGER.error("ERROR: Not able to submit the job due to a PermissionException: " + pe.toString());
        } catch (SubmissionClosedException sce) {
            LOGGER.error("ERROR: Not able to submit the job due to a SubmissionClosedException: " + sce.toString());
        } catch (JobCreationException jce) {
            LOGGER.error("ERROR: Not able to submit the job due to a JobCreationException: " + jce.toString());
        }
        return jobId;
    }

    /**
     * Submit a ProActive job to the scheduler
     * @param xmlFile A ProActive job xml file
     * @return JobId
     */
    public JobId submit(File xmlFile) {
        reconnectIfDisconnected();
        JobId jobId = null;
        LOGGER.debug("Submitting job: " + xmlFile.toString());
        try {
            jobId = restSmartProxy.submit(xmlFile);
        } catch (NotConnectedException nce) {
            LOGGER.error("ERROR: Not able to submit the job due to a NotConnectedException: " + nce.toString());
        } catch (PermissionException pe) {
            LOGGER.error("ERROR: Not able to submit the job due to a PermissionException: " + pe.toString());
        } catch (SubmissionClosedException sce) {
            LOGGER.error("ERROR: Not able to submit the job due to a SubmissionClosedException: " + sce.toString());
        } catch (JobCreationException jce) {
            LOGGER.error("ERROR: Not able to submit the job due to a JobCreationException: " + jce.toString());
        }
        return jobId;
    }

    /**
     * Submit a ProActive job to the scheduler
     * @param xmlFile   A ProActive job xml file
     * @param variables A variables map
     * @return JobId
     */
    public JobId submit(File xmlFile, Map<String, String> variables) {
        reconnectIfDisconnected();
        JobId jobId = null;
        LOGGER.debug("Submitting job: " + xmlFile.toString());
        LOGGER.debug("  with variables: " + variables.toString());
        try {
            jobId = restSmartProxy.submit(xmlFile, variables);
        } catch (NotConnectedException nce) {
            LOGGER.error("ERROR: Not able to submit the job due to a NotConnectedException: " + nce.toString());
        } catch (PermissionException pe) {
            LOGGER.error("ERROR: Not able to submit the job due to a PermissionException: " + pe.toString());
        } catch (SubmissionClosedException sce) {
            LOGGER.error("ERROR: Not able to submit the job due to a SubmissionClosedException: " + sce.toString());
        } catch (JobCreationException jce) {
            LOGGER.error("ERROR: Not able to submit the job due to a JobCreationException: " + jce.toString());
        }
        return jobId;
    }

    /**
     * Get a ProActive job state
     * @param jobId A ProActive job ID
     * @return The job state
     */
    public JobState getJobState(String jobId) {
        reconnectIfDisconnected();
        JobState jobState = null;
        try {
            LOGGER.info("Getting job " + jobId + " state.");
            jobState = restSmartProxy.getJobState(jobId);
            LOGGER.info("Job " + jobId + " is in state: " + jobState.getStatus().toString());
        } catch (NotConnectedException nce) {
            LOGGER.error("ERROR: Not able to get the job state due to a NotConnectedException: " + nce.toString());
        } catch (UnknownJobException uje) {
            LOGGER.error("ERROR: Not able to get the job state due to an unknown job ID: " + uje.toString());
        } catch (PermissionException pe) {
            LOGGER.error("ERROR: Not able to submit the job due to a PermissionException: " + pe.toString());
        }
        return jobState;
    }

    /**
     * Wait for a job
     * @param jobId   A ProActive job ID
     * @param timeout The waiting timeout
     * @return The job result
     */
    public JobResult waitForJob(String jobId, long timeout) {
        reconnectIfDisconnected();
        JobResult jobResult = null;
        try {
            jobResult = restSmartProxy.waitForJob(jobId, timeout);
        } catch (NotConnectedException nce) {
            LOGGER.error("ERROR: Not able to wait for the job due to a NotConnectedException: " + nce.toString());
        } catch (PermissionException pe) {
            LOGGER.error("ERROR: Not able to wait for the job due to a PermissionException: " + pe.toString());
        } catch (UnknownJobException uje) {
            LOGGER.error("ERROR: Unknown job ID: " + uje.toString());
        } catch (TimeoutException te) {
            LOGGER.warn("WARNING: Not able to wait for the job due to timeout exceed: " + te.toString());
        }
        return jobResult;
    }

    /**
     * Get job results map
     * @param jobsId A list of ProActive jobs ID
     * @return The jobs results map
     */
    public Map<Long, Map<String, Serializable>> getJobResultMaps(List<String> jobsId) {
        reconnectIfDisconnected();
        Map<Long, Map<String, Serializable>> jobResults = null;
        try {
            jobResults = restSmartProxy.getJobResultMaps(jobsId);
        } catch (SchedulerException se) {
            LOGGER.error("ERROR: Not able to get jobs results due to : " + se.toString());
        }
        return jobResults;
    }

    /**
     * Kill the job represented by jobId
     * @param jobId A ProActive job ID
     * @return true if success, false if not.
     */
    public boolean killJob(String jobId) {
        reconnectIfDisconnected();
        boolean result = false;
        LOGGER.debug("Killing ProActive job: " + jobId);
        try {
            result = restSmartProxy.killJob(jobId);
            LOGGER.info("ProActive job " + jobId + " killed successfully.");
        } catch (NotConnectedException nce) {
            LOGGER.error("ERROR: Not able to kill the job due to a NotConnectedException: " + nce.toString());
        } catch (UnknownJobException uje) {
            LOGGER.error("ERROR: Unknown job ID: " + uje.toString());
        } catch (PermissionException pe) {
            LOGGER.error("ERROR: Not able to kill the job due to a PermissionException: " + pe.toString());
        }
        return result;
    }

    /**
     * Delete a job
     * @param jobId The ID of the job to delete
     * @return true if success, false if the job not yet finished (not removed, kill the job then remove it
     */
    public boolean removeJob(String jobId) {
        reconnectIfDisconnected();
        boolean result = false;
        LOGGER.debug("Removing ProActive job: " + jobId);
        try {
            result = restSmartProxy.removeJob(jobId);
            LOGGER.info("ProActive job " + jobId + " removed successfully.");
        } catch (NotConnectedException nce) {
            LOGGER.error("ERROR: Not able to remove the job due to a NotConnectedException: " +
                         Arrays.toString(nce.getStackTrace()));
        } catch (UnknownJobException uje) {
            LOGGER.error("ERROR: Unknown job ID: " + Arrays.toString(uje.getStackTrace()));
        } catch (PermissionException pe) {
            LOGGER.error("ERROR: Not able to remove the job due to a PermissionException: " +
                         Arrays.toString(pe.getStackTrace()));
        }
        return result;
    }

    /**
     * Retrieves a job list of the scheduler.
     *
     * @param index says to start from this job is
     * @param limit max number of jobs to retrieve
     * @return jobs list according to all criteria
     */
    public Page<JobInfo> getJobs(int index, int limit) {
        Page<JobInfo> jobInfos = null;
        LOGGER.debug("Retrieving from ProActive Scheduler the list of " + limit + " active jobs, starting from index " +
                     index);
        try {
            jobInfos = restSmartProxy.getJobs(index,
                                              limit,
                                              new JobFilterCriteria(false,
                                                                    true,
                                                                    true,
                                                                    true,
                                                                    false,
                                                                    true,
                                                                    "",
                                                                    "",
                                                                    "",
                                                                    "",
                                                                    null),
                                              DEFAULT_JOB_SORT_PARAMS);
            LOGGER.info("List of jobs retrieved: " + jobInfos.toString());
        } catch (NotConnectedException nce) {
            LOGGER.error("ERROR: Not able to retrieve jobs due to a NotConnectedException: " +
                         Arrays.toString(nce.getStackTrace()));
        } catch (PermissionException pe) {
            LOGGER.error("ERROR: Not able to remove the job due to a PermissionException: " +
                         Arrays.toString(pe.getStackTrace()));
        }
        return jobInfos;
    }

    /**
     * Retrieves a job list of the scheduler.
     *
     * @param index says to start from this job is
     * @param limit max number of jobs to retrieve
     * @return jobs list according to all criteria
     */
    public List<JobInfo> getActiveJobs(int index, int limit) {
        List<JobInfo> activeJobInfos = this.getJobs(index, limit)
                                           .getList()
                                           .stream()
                                           .filter(activeJobInfo -> activeJobInfo.getStatus().isJobAlive())
                                           .collect(Collectors.toList());
        LOGGER.info("Job list filtered to only active ones: " + activeJobInfos.toString());
        return activeJobInfos;
    }

    /**
     * Wait for a task
     * @param jobId    A ProActive job ID
     * @param taskName A task name
     * @param timeout  The waiting timeout
     * @return The task result
     */
    public TaskResult waitForTask(String jobId, String taskName, long timeout) {
        reconnectIfDisconnected();
        TaskResult taskResult = null;
        try {
            taskResult = restSmartProxy.waitForTask(jobId, taskName, timeout);
        } catch (NotConnectedException nce) {
            LOGGER.error("ERROR: Not able to wait for the task due to a NotConnectedException: " + nce.toString());
        } catch (PermissionException pe) {
            LOGGER.error("ERROR: Not able to wait for the task due to a PermissionException: " + pe.toString());
        } catch (UnknownJobException uje) {
            LOGGER.error("ERROR: Unknown job ID: " + uje.toString());
        } catch (UnknownTaskException ute) {
            LOGGER.error("ERROR: Unknown task name: " + ute.toString());
        } catch (TimeoutException te) {
            LOGGER.warn("WARNING: Not able to wait for the task due to timeout exceed: " + te.toString());
        }
        return taskResult;
    }

    /**
     * Get a task result
     * @param jobId    A ProActive job ID
     * @param taskName A task name
     * @return The task result
     */
    public TaskResult getTaskResult(String jobId, String taskName) {
        reconnectIfDisconnected();
        TaskResult taskResult = null;
        try {
            taskResult = restSmartProxy.getTaskResult(jobId, taskName);
        } catch (NotConnectedException nce) {
            LOGGER.error("ERROR: Not able to wait for the task due to a NotConnectedException: " + nce.toString());
        } catch (PermissionException pe) {
            LOGGER.error("ERROR: Not able to wait for the task due to a PermissionException: " + pe.toString());
        } catch (UnknownJobException uje) {
            LOGGER.error("ERROR: Unknown job ID: " + uje.toString());
        } catch (UnknownTaskException ute) {
            LOGGER.error("ERROR: Unknown task name: " + ute.toString());
        }
        return taskResult;
    }

    /**
     * Connect to the ProActive server
     * @param username The user's username
     * @param password The user's password
     */
    public void connect(String username, String password) {
        // Connect to the Scheduler API
        restSmartProxy = SchedulerConnectionHelper.connect(username, password);
    }

    /**
     * Disconnect from the ProActive server
     */
    public void disconnect() {
        restSmartProxy = SchedulerConnectionHelper.disconnect();
    }

    private synchronized void reconnectIfDisconnected() {
        if (!restSmartProxy.isConnected()) {
            try {
                LOGGER.warn("WARNING: Not connected to the scheduler. Reconnecting to Scheduler ...");
                restSmartProxy.reconnect();
                LOGGER.info("Reconnected to ProActive Scheduler.");
            } catch (SchedulerException | LoginException e) {
                LOGGER.error("ERROR: Not able to reconnect to Scheduler due to: " + Arrays.toString(e.getStackTrace()));
            }
        } else {
            LOGGER.info("The Scheduler is already connected.");
        }
    }

    private void renewSession() {
        try {
            LOGGER.debug("Renewing connexion ...");
            restSmartProxy.renewSession();
            LOGGER.info("Connexion to ProActive Scheduler renewed.");
        } catch (NotConnectedException nce) {
            LOGGER.error("ERROR: Not able to renew connexion to Scheduler due to: " +
                         Arrays.toString(nce.getStackTrace()));
        }
    }

    // For testing purpose
    public RestSmartProxyImpl getRestSmartProxy() {
        return restSmartProxy;
    }

    public void setRestSmartProxy(RestSmartProxyImpl restSmartProxy) {
        this.restSmartProxy = restSmartProxy;
    }
}
