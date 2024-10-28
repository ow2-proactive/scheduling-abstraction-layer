/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.rest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.ow2.proactive.sal.model.*;
import org.ow2.proactive.sal.service.service.ClusterService;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@RestController
@RequestMapping(value = "/cluster")
@Api(description = "Operations on Kubernetes cluster", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
public class ClusterRest {

    @Autowired
    private ClusterService clusterService;

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Define a Kubernetes Cluster")
    public ResponseEntity<Boolean>
            defineCluster(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A cluster skeleton definition", required = true)
    @RequestBody
    final ClusterDefinition clusterDefinition) throws NotConnectedException, IOException {
        return ResponseEntity.ok(clusterService.defineCluster(sessionId, clusterDefinition));
    }

    @RequestMapping(value = "/{clusterName}", method = RequestMethod.POST)
    @ApiOperation(value = "Deploy a Kubernetes Cluster")
    public ResponseEntity<Boolean>
            deployCluster(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @PathVariable
    final String clusterName) throws NotConnectedException {
        return ResponseEntity.ok(clusterService.deployCluster(sessionId, clusterName));
    }

    @RequestMapping(value = "/{clusterName}", method = RequestMethod.GET)
    @ApiOperation(value = "get a defined Kubernetes Cluster")
    public ResponseEntity<Cluster> getCluster(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @PathVariable
    final String clusterName) throws NotConnectedException {
        return ResponseEntity.ok(clusterService.getCluster(sessionId, clusterName));
    }

    @RequestMapping(value = "/{clusterName}/scaleout", method = RequestMethod.POST)
    @ApiOperation(value = "Add nodes to an already existing cluster")
    public ResponseEntity<Cluster>
            scaleOutCluster(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @PathVariable
    final String clusterName, @RequestBody
    final List<ClusterNodeDefinition> newNodes) throws NotConnectedException, IOException {
        return ResponseEntity.ok(clusterService.scaleOutCluster(sessionId, clusterName, newNodes));
    }

    @RequestMapping(value = "/{clusterName}/scalein", method = RequestMethod.POST)
    @ApiOperation(value = "Add nodes from an already existing cluster")
    public ResponseEntity<Cluster>
            scaleInCluster(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @PathVariable
    final String clusterName, @RequestBody
    final List<String> nodes) throws NotConnectedException {
        return ResponseEntity.ok(clusterService.scaleInCluster(sessionId, clusterName, nodes));
    }

    @RequestMapping(value = "/{clusterName}/label", method = RequestMethod.POST)
    @ApiOperation(value = "Label nodes in the cluster")
    public ResponseEntity<Long> labelNodes(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @PathVariable
    final String clusterName, @RequestBody
    final List<Map<String, String>> nodesLabels) throws NotConnectedException, IOException {
        return ResponseEntity.ok(clusterService.labelNodes(sessionId, clusterName, nodesLabels));
    }

    @RequestMapping(value = "/{clusterName}/app", method = RequestMethod.POST)
    @ApiOperation(value = "run an application in the cluster")
    public ResponseEntity<Long>
            deployApplication(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @PathVariable
    final String clusterName, @RequestBody
    final ClusterApplication application) throws NotConnectedException {
        return ResponseEntity.ok(clusterService.deployApplication(sessionId, clusterName, application));
    }

    @RequestMapping(value = "/{clusterName}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete the cluster")
    public ResponseEntity<Boolean>
            deleteCluster(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @PathVariable
    final String clusterName) throws NotConnectedException {
        return ResponseEntity.ok(clusterService.deleteCluster(sessionId, clusterName));
    }

}
