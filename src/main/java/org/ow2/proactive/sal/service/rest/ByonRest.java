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

import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.MediaType;

import org.ow2.proactive.sal.service.model.ByonDefinition;
import org.ow2.proactive.sal.service.model.ByonNode;
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
@Api(description = "Operations on BYON", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
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

    //    @RequestMapping(method = RequestMethod.PUT)
    //    @ApiOperation(value = "Define a BYON node source", response = Boolean.class)
    //    public ResponseEntity<Boolean>
    //            defineByonNodeSource(@ApiParam(value = "Proactive authentication session id", required = true)
    //    @RequestHeader(value = "sessionid")
    //    final String sessionId, @ApiParam(value = "A list of BYON nodes to be connected to the server", required = true)
    //    @RequestBody
    //    final List<ByonNode> byonNodeList, @ApiParam(value = "The name of the node source", required = true)
    //    @RequestParam
    //    final String nodeSourceName) throws NotConnectedException {
    //        return ResponseEntity.ok(byonService.defineByonNodeSource(sessionId, byonNodeList, nodeSourceName));
    //    }

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

    @RequestMapping(method = RequestMethod.DELETE)
    @ApiOperation(value = "Remove Byon nodes", response = Boolean.class)
    public ResponseEntity<Boolean>
            deleteByonNode(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "The id of the node to be removed", required = true)
    @RequestParam
    final String byonId) throws NotConnectedException {
        return ResponseEntity.ok(byonService.deleteByonNode(sessionId, byonId));
    }

}
