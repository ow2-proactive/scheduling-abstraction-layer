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

import org.ow2.proactive.sal.common.model.EmsDeploymentRequest;
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
    final String sessionId, @ApiParam(value = "Names of the nodes to which to add EMS deployment", required = true)
    @RequestParam(value = "nodeNames")
    final List<String> nodeNames,
                    @ApiParam(value = "The authorization bearer used by upperware's components to authenticate with each other. Needed by the EMS.", required = true)
                    @RequestParam
                    final String authorizationBearer) throws NotConnectedException {
        return ResponseEntity.ok(monitoringService.addEmsDeployment(sessionId, nodeNames, authorizationBearer));
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
