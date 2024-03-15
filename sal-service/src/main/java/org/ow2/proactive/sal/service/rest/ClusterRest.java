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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.ow2.proactive.sal.model.Cluster;
import org.ow2.proactive.sal.model.ClusterDefinition;
import org.ow2.proactive.sal.model.ClusterNodeDefinition;
import org.ow2.proactive.sal.model.JobDefinition;
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
    @ApiOperation(value = "Add node to an already existing cluster")
    public ResponseEntity<Cluster>
            scaleOutCluster(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @PathVariable
    final String clusterName, @RequestBody
    final List<ClusterNodeDefinition> newNodes) throws NotConnectedException, IOException {
        return ResponseEntity.ok(clusterService.scaleOutCluster(sessionId, clusterName, newNodes));
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
}
