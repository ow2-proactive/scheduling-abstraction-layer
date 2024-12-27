/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.rest;

import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.MediaType;

import org.ow2.proactive.sal.model.ByonDefinition;
import org.ow2.proactive.sal.model.ByonNode;
import org.ow2.proactive.sal.service.service.ByonService;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@RestController
@RequestMapping(value = "/byon")
@Api(tags = "Operations on BYON", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
public class ByonRest {

    @Autowired
    private ByonService byonService;

    @RequestMapping(value = "/{jobId}", method = RequestMethod.POST)
    @ApiOperation(value = "Register new BYON nodes passed as ByonDefinition object", response = ByonNode.class)
    public ResponseEntity<ByonNode>
            registerNewByonNode(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A constructed job identifier", required = true)
    @PathVariable
    final String jobId,
                    @ApiParam(value = "object of class ByonDefinition that contains the details of the nodes to be registered.", required = true)
                    @RequestBody
                    final ByonDefinition byonNodeDefinition, @ApiParam(value = "the Byon agent will be deployed automatically if the value is set to True", defaultValue = "true")
                    @RequestParam
                    @DefaultValue("true")
                    final Boolean automate) throws NotConnectedException {
        return ResponseEntity.ok(byonService.registerNewByonNode(sessionId, byonNodeDefinition, jobId, automate));
    }

    @RequestMapping(value = "/{jobId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get all registered clouds", response = ByonNode.class, responseContainer = "List")
    public ResponseEntity<List<ByonNode>>
            getByonNodes(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A constructed job identifier", required = true)
    @PathVariable
    final String jobId) throws NotConnectedException {
        return ResponseEntity.ok(byonService.getByonNodes(sessionId, jobId));
    }

    @RequestMapping(value = "/{jobId}", method = RequestMethod.PUT)
    @ApiOperation(value = "Adding BYON nodes to a job component", response = Boolean.class)
    public ResponseEntity<Boolean>
            addByonNodes(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A mapping between byon nodes and job components", required = true)
    @RequestBody
    final Map<String, String> byonIdPerComponent, @ApiParam(value = "A constructed job identifier", required = true)
    @PathVariable
    final String jobId) throws NotConnectedException {
        return ResponseEntity.ok(byonService.addByonNodes(sessionId, byonIdPerComponent, jobId));
    }

    @RequestMapping(value = "/{byonId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Remove Byon nodes", response = Boolean.class)
    public ResponseEntity<Boolean>
            deleteByonNode(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "The id of the node to be removed", required = true)
    @PathVariable
    final String byonId) throws NotConnectedException {
        return ResponseEntity.ok(byonService.deleteByonNode(sessionId, byonId));
    }

}
