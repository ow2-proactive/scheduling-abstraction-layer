/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.rest;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.ow2.proactive.sal.model.EdgeDefinition;
import org.ow2.proactive.sal.model.EdgeNode;
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
@Api(tags = "Operations on Edge", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
public class EdgeRest {

    @Autowired
    private EdgeService edgeService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ApiOperation(value = "Register new Edge nodes passed as EdgeDefinition object", response = EdgeNode.class)
    public ResponseEntity<EdgeNode>
            registerNewEdgeNode(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId,
                    @ApiParam(value = "object of class EdgeDefinition that contains the details of the nodes to be registered.", required = true)
                    @RequestBody
                    final EdgeDefinition edgeNodeDefinition) throws NotConnectedException {
        return ResponseEntity.ok(edgeService.registerNewEdgeNode(sessionId, edgeNodeDefinition));
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
    @ApiOperation(value = "Adding Edge nodes to a job component", response = Boolean.class)
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

    @RequestMapping(value = "/{edgeId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Remove Edge nodes", response = Boolean.class)
    public ResponseEntity<Boolean>
            deleteEdgeNode(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "The id of the node to be removed", required = true)
    @PathVariable
    final String edgeId) throws NotConnectedException {
        return ResponseEntity.ok(edgeService.deleteEdgeNode(sessionId, edgeId));
    }
}
