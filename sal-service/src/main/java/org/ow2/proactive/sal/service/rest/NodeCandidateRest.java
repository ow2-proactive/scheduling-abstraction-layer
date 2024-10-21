/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.rest;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.ow2.proactive.sal.model.NodeCandidate;
import org.ow2.proactive.sal.model.Requirement;
import org.ow2.proactive.sal.service.service.NodeCandidateService;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@RestController
@RequestMapping(value = "/nodecandidates")
@Api(description = "Operations on node candidates", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
public class NodeCandidateRest {

    @Autowired
    private NodeCandidateService nodeCandidateService;

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Find node candidates", response = NodeCandidate.class, responseContainer = "List")
    public ResponseEntity<List<NodeCandidate>>
            findNodeCandidates(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "List of NodeType or Attribute requirements", required = true)
    @RequestBody
    final List<Requirement> requirements) throws NotConnectedException {
        return ResponseEntity.ok(nodeCandidateService.findNodeCandidates(sessionId, requirements));
    }

    @RequestMapping(value = "/length", method = RequestMethod.GET)
    @ApiOperation(value = "This function returns the number of available node candidates according to the added clouds")
    public ResponseEntity<Long>
            getLengthOfNodeCandidates(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId) throws NotConnectedException {
        return ResponseEntity.ok(nodeCandidateService.getLengthOfNodeCandidates(sessionId));
    }
}
