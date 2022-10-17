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

import java.security.KeyException;
import java.util.List;

import javax.security.auth.login.LoginException;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.ow2.proactive.resourcemanager.common.event.RMNodeEvent;
import org.ow2.proactive.resourcemanager.exception.RMException;
import org.ow2.proactive.sal.service.service.PAGatewayService;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.ow2.proactive_grid_cloud_portal.scheduler.exception.PermissionRestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@RestController
@RequestMapping(value = "/pagateway")
@Api(description = "Operations on Proactive gateway", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
public class PAGatewayRest {

    //TODO: Make init and connect automatically configurable if values are set

    @Autowired
    private PAGatewayService paGatewayService;

    @RequestMapping(value = "/init", method = RequestMethod.POST)
    @ApiOperation(value = "Init a gateway to the ProActive server", response = String.class)
    public ResponseEntity<Boolean>
            init(@ApiParam(value = "ProActive server URL (exp: http://try.activeeon.com:8080/)", required = true)
    @FormParam(value = "paURL")
    final String paURL) {
        return ResponseEntity.ok(paGatewayService.init(paURL));
    }

    @RequestMapping(value = "/connect", method = RequestMethod.POST)
    @ApiOperation(value = "Construct and connect a gateway to the ProActive server", response = String.class)
    public ResponseEntity<String> connect(@ApiParam(value = "Proactive authentication username", required = true)
    @FormParam(value = "username")
    final String username, @ApiParam(value = "Proactive authentication password", required = true)
    @FormParam(value = "password")
    final String password) throws LoginException, KeyException, RMException {
        return ResponseEntity.ok(paGatewayService.connectAndInsist(username, password));
    }

    @RequestMapping(value = "/disconnect", method = RequestMethod.POST)
    @ApiOperation(value = "Disconnect gateway from the ProActive server")
    public void disconnect(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId) throws NotConnectedException {
        paGatewayService.disconnect(sessionId);
    }

    @RequestMapping(value = "/activevms", method = RequestMethod.GET)
    @ApiOperation(value = "Returns a list of all available VMs", response = RMNodeEvent.class, responseContainer = "List")
    public ResponseEntity<List<RMNodeEvent>>
            getActiveVMs(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId) throws NotConnectedException, PermissionRestException {
        return ResponseEntity.ok(paGatewayService.getActiveVMs(sessionId));
    }
}
