/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.rest;

import javax.ws.rs.core.MediaType;

import org.ow2.proactive.sal.service.service.PersistenceService;
import org.ow2.proactive.sal.service.service.RepositoryService;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
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
@RequestMapping(value = "/persistence")
@Api(description = "Operations to clean the cloud, clusters and SAL database", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
public class PersistenceRest {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private PersistenceService persistenceService;

    @RequestMapping(value = "/cleanall", method = RequestMethod.DELETE)
    @ApiOperation(value = "Clean all clusters, clouds, and edge devices", response = Boolean.class)
    public ResponseEntity<Boolean> cleanAll(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId) throws NotConnectedException {
        return ResponseEntity.ok(persistenceService.cleanAll(sessionId));
    }

    @RequestMapping(value = "/cleanallclouds", method = RequestMethod.DELETE)
    @ApiOperation(value = "Clean all clouds and undeploy cloud nodes", response = Boolean.class)
    public ResponseEntity<Boolean>
            cleanAllClouds(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId) throws NotConnectedException {
        return ResponseEntity.ok(persistenceService.cleanAllClouds(sessionId));
    }

    @RequestMapping(value = "/cleanallclusters", method = RequestMethod.DELETE)
    @ApiOperation(value = "Clean all clouds and undeploy cloud nodes", response = Boolean.class)
    public ResponseEntity<Boolean>
            cleanAllClusters(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId) throws NotConnectedException {
        return ResponseEntity.ok(persistenceService.cleanAllClusters(sessionId));
    }

    @RequestMapping(value = "/cleanalledges", method = RequestMethod.DELETE)
    @ApiOperation(value = "Deregister all edge devices", response = Boolean.class)
    public ResponseEntity<Boolean>
            cleanAllEdges(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId) throws NotConnectedException {
        return ResponseEntity.ok(persistenceService.cleanAllEdges(sessionId));
    }

    @RequestMapping(value = "/cleanSALdatabase", method = RequestMethod.DELETE)
    @ApiOperation(value = "Clean all the DB entries in SAL")
    public void cleanSalDatabase(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId) throws NotConnectedException {
        repositoryService.cleanAll(sessionId);
    }

}
