/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.rest;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.ow2.proactive.sal.model.EmsDeploymentDefinition;
import org.ow2.proactive.sal.model.EmsDeploymentRequest;
import org.ow2.proactive.sal.service.service.MonitoringService;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@RestController
@RequestMapping(value = "/monitor")
@Api(description = "Operations on EMS monitors", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
public class MonitoringRest {

    @Autowired
    private MonitoringService monitoringService;

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Add an EMS deployment to a defined job")
    public ResponseEntity<Integer>
            addEmsDeployment(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "An EMS deployment definition", required = true)
    @RequestBody
    final EmsDeploymentDefinition emsDeploymentDefinition) throws NotConnectedException {
        return ResponseEntity.ok(monitoringService.addEmsDeployment(sessionId, emsDeploymentDefinition));
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "Get the list of all available EMS deployment monitor requests", response = EmsDeploymentRequest.class, responseContainer = "List")
    public ResponseEntity<List<EmsDeploymentRequest>>
            getMonitorsList(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId) throws NotConnectedException {
        return ResponseEntity.ok(monitoringService.getMonitorsList(sessionId));
    }
}
