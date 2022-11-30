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

import org.ow2.proactive.sal.common.model.NodeCandidate;
import org.ow2.proactive.sal.common.model.Requirement;
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
