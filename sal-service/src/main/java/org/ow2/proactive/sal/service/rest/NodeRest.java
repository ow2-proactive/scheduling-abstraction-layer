/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.rest;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.MediaType;

import org.ow2.proactive.sal.model.Deployment;
import org.ow2.proactive.sal.model.IaasDefinition;
import org.ow2.proactive.sal.service.service.NodeService;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@RestController
@RequestMapping(value = "/node")
@Api(description = "Operations on nodes", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
public class NodeRest {

    @Autowired
    private NodeService nodeService;

    @RequestMapping(value = "/{jobId}", method = RequestMethod.POST)
    @ApiOperation(value = "Add nodes to the tasks of a defined job")
    public synchronized ResponseEntity<Boolean>
            addNodes(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A list IaasDefinition instances in json format", required = true)
    @RequestBody
    final List<IaasDefinition> nodes, @ApiParam(value = "A job identifier", required = true)
    @PathVariable
    final String jobId) throws NotConnectedException {
        return ResponseEntity.ok(nodeService.addNodes(sessionId, nodes, jobId));
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "Get all added nodes", response = Deployment.class, responseContainer = "List")
    public ResponseEntity<List<Deployment>>
            getNodes(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @RequestParam
    final Optional<List<String>> nodeNames) throws NotConnectedException {
        if (nodeNames.isPresent()) {
            return ResponseEntity.ok(nodeService.getNodesByNames(sessionId, nodeNames.get()));
        } else {
            return ResponseEntity.ok(nodeService.getNodes(sessionId));
        }
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ApiOperation(value = "Remove nodes")
    public ResponseEntity<Boolean> removeNodes(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A comma separated list of node names to remove", required = true)
    @RequestBody
    final List<String> nodeNames,
            @ApiParam(value = "If true remove node immediately without waiting for it to be freed", defaultValue = "false")
            @RequestHeader(value = "preempt", defaultValue = "false")
            final Boolean preempt) throws NotConnectedException {
        return ResponseEntity.ok(nodeService.removeNodes(sessionId, nodeNames, preempt));
    }
}
