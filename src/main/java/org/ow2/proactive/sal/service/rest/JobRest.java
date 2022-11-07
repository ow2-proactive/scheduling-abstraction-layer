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
package org.ow2.proactive.sal.service.rest;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.tuple.Pair;
import org.ow2.proactive.sal.service.model.Job;
import org.ow2.proactive.sal.service.model.JobDefinition;
import org.ow2.proactive.sal.service.model.SubmittedJobType;
import org.ow2.proactive.sal.service.service.JobService;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.ow2.proactive.scheduler.common.job.JobResult;
import org.ow2.proactive.scheduler.common.job.JobState;
import org.ow2.proactive.scheduler.common.task.TaskResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@RestController
@RequestMapping(value = "/job")
@Api(description = "Operations on jobs", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
public class JobRest {

    @Autowired
    private JobService jobService;

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Create a ProActive job skeleton")
    public ResponseEntity<Boolean> createJob(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A job skeleton definition in JSON format", required = true)
    @RequestBody
    final JobDefinition job) throws NotConnectedException {
        return ResponseEntity.ok(jobService.createJob(sessionId, job));
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "Get all job skeletons", response = Job.class, responseContainer = "List")
    public ResponseEntity<List<Job>> getJobs(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId) throws NotConnectedException {
        return ResponseEntity.ok(jobService.getJobs(sessionId));
    }

    @RequestMapping(value = "/stop", method = RequestMethod.PUT)
    @ApiOperation(value = "Stop jobs")
    public ResponseEntity<Long> stopJobs(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "List of job IDs to stop", required = true)
    @RequestParam(value = "jobIds")
    final List<String> jobIds) throws NotConnectedException {
        return ResponseEntity.ok(jobService.stopJobs(sessionId, jobIds));
    }

    @RequestMapping(value = "/{jobId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get a specific job", response = Job.class)
    public ResponseEntity<Job> getJob(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A job identifier", required = true)
    @PathVariable
    final String jobId) throws NotConnectedException {
        return ResponseEntity.ok(jobService.getJob(sessionId, jobId));
    }

    @RequestMapping(value = "/{jobId}/dot", method = RequestMethod.GET)
    @ApiOperation(value = "Return the dot format of the application's graph")
    public ResponseEntity<String>
            getGraphInDotFormat(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A job identifier", required = true)
    @PathVariable
    final String jobId) throws NotConnectedException {
        return ResponseEntity.ok(jobService.getGraphInDotFormat(sessionId, jobId));
    }

    @RequestMapping(value = "/{jobId}/submit", method = RequestMethod.POST)
    @ApiOperation(value = "Submit a job constructed in lazy-mode to the ProActive Scheduler")
    public ResponseEntity<Long> submitJob(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A job identifier", required = true)
    @PathVariable
    final String jobId) throws NotConnectedException {
        return ResponseEntity.ok(jobService.submitJob(sessionId, jobId));
    }

    @RequestMapping(value = "/{jobId}/status", method = RequestMethod.GET)
    @ApiOperation(value = "Get a ProActive job state", response = Pair.class)
    public ResponseEntity<Pair<SubmittedJobType, JobState>>
            getJobState(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A job ID", required = true)
    @PathVariable
    final String jobId) throws NotConnectedException {
        return ResponseEntity.ok(jobService.getJobState(sessionId, jobId));
    }

    @RequestMapping(value = "/{jobId}/wait", method = RequestMethod.GET)
    @ApiOperation(value = "Wait for execution and get results of a job", response = JobResult.class)
    public ResponseEntity<JobResult>
            waitForJob(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A job ID", required = true)
    @PathVariable
    final String jobId, @ApiParam(value = "The timeout", required = true)
    @RequestParam(value = "timeout")
    final long timeout) throws NotConnectedException {
        return ResponseEntity.ok(jobService.waitForJob(sessionId, jobId, timeout));
    }

    @RequestMapping(value = "/{jobId}/stop", method = RequestMethod.PUT)
    @ApiOperation(value = "Stop a job")
    public ResponseEntity<Long> stopJob(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A job ID", required = true)
    @PathVariable
    final String jobId) throws NotConnectedException {
        return ResponseEntity.ok(jobService.stopJob(sessionId, jobId));
    }

    @RequestMapping(value = "/{jobId}/kill", method = RequestMethod.PUT)
    @ApiOperation(value = "Kill a job")
    public ResponseEntity<Boolean> killJob(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A job ID", required = true)
    @PathVariable
    final String jobId) throws NotConnectedException {
        return ResponseEntity.ok(jobService.killJob(sessionId, jobId));
    }

    @RequestMapping(value = "/{jobId}/{taskName}/wait", method = RequestMethod.GET)
    @ApiOperation(value = "Wait for execution and get results of a task")
    public ResponseEntity<Map<String, TaskResult>>
            waitForTask(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A job ID", required = true)
    @PathVariable
    final String jobId, @ApiParam(value = "A task name", required = true)
    @PathVariable
    final String taskName, @ApiParam(value = "The timeout", required = true)
    @RequestParam(value = "timeout")
    final long timeout) throws NotConnectedException {
        return ResponseEntity.ok(jobService.waitForTask(sessionId, jobId, taskName, timeout));
    }

    @RequestMapping(value = "/{jobId}/{taskName}/result", method = RequestMethod.GET)
    @ApiOperation(value = "Get a task result")
    public ResponseEntity<Map<String, TaskResult>>
            getTaskResult(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A job ID", required = true)
    @PathVariable
    final String jobId, @ApiParam(value = "A task name", required = true)
    @PathVariable
    final String taskName) throws NotConnectedException {
        return ResponseEntity.ok(jobService.getTaskResult(sessionId, jobId, taskName));
    }

    @RequestMapping(value = "/kill", method = RequestMethod.PUT)
    @ApiOperation(value = "Kill all active jobs in ProActive Scheduler")
    public ResponseEntity<Boolean>
            killAllActivePAJobs(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId) throws NotConnectedException {
        return ResponseEntity.ok(jobService.killAllActivePAJobs(sessionId));
    }

    @RequestMapping(value = "/remove", method = RequestMethod.DELETE)
    @ApiOperation(value = "Remove all jobs from the ProActive Scheduler")
    public ResponseEntity<Boolean>
            removeAllPAJobs(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId) throws NotConnectedException {
        return ResponseEntity.ok(jobService.removeAllPAJobs(sessionId));
    }
}
