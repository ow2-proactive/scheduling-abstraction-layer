/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.rest;

import javax.ws.rs.core.MediaType;

import org.ow2.proactive.sal.model.ReconfigurationJobDefinition;
import org.ow2.proactive.sal.service.service.ReconfigurationService;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@RestController
@RequestMapping(value = "/reconfigure")
@Api(tags = "Reconfiguration operations", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
public class ReconfigurationRest {

    @Autowired
    private ReconfigurationService reconfigurationService;

    @PostMapping()
    @ApiOperation(value = "Create a ProActive job skeleton")
    public ResponseEntity<Boolean>
            reconfigureJob(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A job identifier", required = true)
    @RequestParam(value = "jobId")
    final String jobId, @ApiParam(value = "A reconfiguration plan definition", required = true)
    @RequestBody
    final ReconfigurationJobDefinition reconfigurationPlan) throws NotConnectedException {
        return ResponseEntity.ok(reconfigurationService.reconfigureJob(sessionId, jobId, reconfigurationPlan));
    }

    @GetMapping(value = "/async")
    @ApiOperation(value = "Is any async node candidates process in progress?", response = Integer.class)
    public ResponseEntity<Boolean>
            isJobInReconfiguration(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A job identifier", required = true)
    @RequestParam(value = "jobId")
    final String jobId) throws NotConnectedException {
        return ResponseEntity.ok(reconfigurationService.isJobInReconfiguration(sessionId, jobId));
    }

}
