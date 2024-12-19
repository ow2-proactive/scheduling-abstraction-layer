/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.rest;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.ow2.proactive.sal.service.service.ScalingService;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@RestController
@RequestMapping(value = "/scale")
@Api(tags = "Scaling operations", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
public class ScalingRest {

    @Autowired
    private ScalingService scalingService;

    @RequestMapping(value = "/{jobId}/{taskName}/out", method = RequestMethod.POST)
    @ApiOperation(value = "Register a set of node as an operation for scale up")
    public synchronized ResponseEntity<Boolean>
            addScaleOutTask(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "The name of the Job to be allocated", required = true)
    @PathVariable
    final String jobId, @ApiParam(value = "The name of the task whose node are to be allocated", required = true)
    @PathVariable
    final String taskName, @ApiParam(value = "Name of the nodes to be created and provisioned", required = true)
    @RequestBody
    final List<String> nodeNames) throws NotConnectedException {
        return ResponseEntity.ok(scalingService.addScaleOutTask(sessionId, jobId, taskName, nodeNames));
    }

    @RequestMapping(value = "/{jobId}/{taskName}/in", method = RequestMethod.POST)
    @ApiOperation(value = "Unregister a set of node as a scale-down operation")
    public synchronized ResponseEntity<Boolean>
            addScaleInTask(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "The name of the job to scale down the nodes", required = true)
    @PathVariable
    final String jobId, @ApiParam(value = "The name of the task whose nodes are to be removed", required = true)
    @PathVariable
    final String taskName, @ApiParam(value = "A list of node to be removed", required = true)
    @RequestBody
    final List<String> nodeNames) throws NotConnectedException {
        return ResponseEntity.ok(scalingService.addScaleInTask(sessionId, jobId, taskName, nodeNames));
    }
}
