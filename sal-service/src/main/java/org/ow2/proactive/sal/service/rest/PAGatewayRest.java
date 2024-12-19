/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
@Api(tags = "Operations on Proactive gateway", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
public class PAGatewayRest {

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
