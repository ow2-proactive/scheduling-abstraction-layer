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

import javax.ws.rs.core.MediaType;

import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@RestController
@RequestMapping(value = "/scale")
@Api(description = "Scaling operations", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
public class ScalingRest {

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
    @RequestParam(value = "nodeNames")
    final List<String> nodeNames) throws NotConnectedException {
        return ResponseEntity.ok(true);
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
    @RequestParam(value = "nodeNames")
    final List<String> nodeNames) throws NotConnectedException {
        return ResponseEntity.ok(true);
    }
}
