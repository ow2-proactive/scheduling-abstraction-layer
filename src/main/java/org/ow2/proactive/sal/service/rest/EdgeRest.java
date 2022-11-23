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

import org.ow2.proactive.sal.service.model.EdgeDefinition;
import org.ow2.proactive.sal.service.model.EdgeNode;
import org.ow2.proactive.sal.service.service.EdgeService;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@RestController
@RequestMapping(value = "/edge")
@Api(description = "Operations on BYON", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
public class EdgeRest {

    @Autowired
    private EdgeService edgeService;

    @RequestMapping(value = "/{jobId}", method = RequestMethod.POST)
    @ApiOperation(value = "Register new BYON nodes passed as EdgeDefinition object", response = EdgeNode.class)
    public ResponseEntity<EdgeNode>
            registerNewEdgeNode(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A constructed job identifier", required = true)
    @PathVariable
    final String jobId,
                    @ApiParam(value = "object of class EdgeDefinition that contains the details of the nodes to be registered.", required = true)
                    @RequestBody
                    final EdgeDefinition edgeNodeDefinition) throws NotConnectedException {
        return ResponseEntity.ok(edgeService.registerNewEdgeNode(sessionId, edgeNodeDefinition, jobId));
    }

    @RequestMapping(value = "/{jobId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get all registered clouds", response = EdgeNode.class, responseContainer = "List")
    public ResponseEntity<List<EdgeNode>>
            getEdgeNodes(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A constructed job identifier", required = true)
    @PathVariable
    final String jobId) throws NotConnectedException {
        return ResponseEntity.ok(edgeService.getEdgeNodes(sessionId, jobId));
    }

    @RequestMapping(value = "/{jobId}", method = RequestMethod.PUT)
    @ApiOperation(value = "Adding BYON nodes to a job component", response = Boolean.class)
    public ResponseEntity<Boolean>
            addEdgeNodes(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A mapping between edge nodes and job components", required = true)
    @RequestBody
    final Map<String, String> edgeIdPerComponent, @ApiParam(value = "A constructed job identifier", required = true)
    @PathVariable
    final String jobId) throws NotConnectedException {
        return ResponseEntity.ok(edgeService.addEdgeNodes(sessionId, edgeIdPerComponent, jobId));
    }
}
