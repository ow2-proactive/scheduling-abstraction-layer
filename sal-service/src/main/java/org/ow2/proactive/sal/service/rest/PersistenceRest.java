/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.rest;

import javax.ws.rs.core.MediaType;

import org.ow2.proactive.sal.service.service.RepositoryService;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@RestController
@RequestMapping(value = "/persistence")
@Api(description = "Operations on the DB", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
public class PersistenceRest {

    @Autowired
    private RepositoryService repositoryService;

    @RequestMapping(value = "/cleanall", method = RequestMethod.DELETE)
    @ApiOperation(value = "Clean all the DB entries")
    public void cleanAll(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId) throws NotConnectedException {
        repositoryService.cleanAll(sessionId);
    }

}
