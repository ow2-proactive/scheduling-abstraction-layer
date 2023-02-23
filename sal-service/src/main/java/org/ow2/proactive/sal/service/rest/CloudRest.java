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
import java.util.Optional;

import javax.ws.rs.core.MediaType;

import org.ow2.proactive.sal.model.*;
import org.ow2.proactive.sal.service.service.CloudService;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@RestController
@RequestMapping(value = "/cloud")
@Api(description = "Operations on clouds", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
public class CloudRest {

    @Autowired
    private CloudService cloudService;

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Add clouds to SAL", response = Integer.class)
    public ResponseEntity<Integer> addClouds(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A list of CloudDefinition instances in json format", required = true)
    @RequestBody
    final List<CloudDefinition> clouds) throws NotConnectedException {
        return ResponseEntity.ok(cloudService.addClouds(sessionId, clouds));
    }

    @RequestMapping(value = "/async", method = RequestMethod.GET)
    @ApiOperation(value = "Is any async node candidates process in progress?", response = Integer.class)
    public ResponseEntity<Boolean> isAnyAsyncNodeCandidatesProcessesInProgress(
            @ApiParam(value = "Proactive authentication session id", required = true)
            @RequestHeader(value = "sessionid")
            final String sessionId) throws NotConnectedException {
        return ResponseEntity.ok(cloudService.isAnyAsyncNodeCandidatesProcessesInProgress(sessionId));
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "Get all registered clouds", response = PACloud.class, responseContainer = "List")
    public ResponseEntity<List<PACloud>>
            getAllClouds(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId) throws NotConnectedException {
        return ResponseEntity.ok(cloudService.getAllClouds(sessionId));
    }

    @RequestMapping(value = "/undeploy", method = RequestMethod.POST)
    @ApiOperation(value = "Undeploy clouds", response = Boolean.class)
    public ResponseEntity<Boolean>
            undeployClouds(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "List of cloud IDs to undeploy", required = true)
    @RequestBody
    final List<String> cloudIDs,
                    @ApiParam(value = "If true undeploy node source immediately without waiting for nodes to be freed", defaultValue = "false")
                    @RequestHeader(value = "preempt", defaultValue = "false")
                    final Boolean preempt) throws NotConnectedException {
        return ResponseEntity.ok(cloudService.undeployClouds(sessionId, cloudIDs, preempt));
    }

    @RequestMapping(value = "/remove", method = RequestMethod.DELETE)
    @ApiOperation(value = "Remove clouds", response = Boolean.class)
    public ResponseEntity<Boolean>
            removeClouds(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "List of cloud IDs to remove", required = true)
    @RequestBody
    final List<String> cloudIDs,
                    @ApiParam(value = "If true undeploy node source immediately without waiting for nodes to be freed", defaultValue = "false")
                    @RequestHeader(value = "preempt", defaultValue = "false")
                    final Boolean preempt) throws NotConnectedException {
        return ResponseEntity.ok(cloudService.removeClouds(sessionId, cloudIDs, preempt));
    }

    @RequestMapping(value = "/images", method = RequestMethod.GET)
    @ApiOperation(value = "Get the list of all available images related to a registered cloud", response = Image.class, responseContainer = "List")
    public ResponseEntity<List<Image>>
            getCloudImages(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A valid cloud identifier")
    @RequestParam(value = "cloudid")
    final Optional<String> cloudID) throws NotConnectedException {
        if (cloudID.isPresent()) {
            return ResponseEntity.ok(cloudService.getCloudImages(sessionId, cloudID.get()));
        } else {
            return ResponseEntity.ok(cloudService.getAllCloudImages(sessionId));
        }
    }

    @RequestMapping(value = "/hardware", method = RequestMethod.GET)
    @ApiOperation(value = "Get the list of all available hardwares related to a registered cloud", response = Hardware.class, responseContainer = "List")
    public ResponseEntity<List<Hardware>>
            getCloudHardwares(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A valid cloud identifier")
    @RequestParam(value = "cloudid")
    final Optional<String> cloudID) throws NotConnectedException {
        if (cloudID.isPresent()) {
            return ResponseEntity.ok(cloudService.getCloudHardwares(sessionId, cloudID.get()));
        } else {
            return ResponseEntity.ok(cloudService.getAllCloudHardwares(sessionId));
        }
    }

    @RequestMapping(value = "/location", method = RequestMethod.GET)
    @ApiOperation(value = "Get the list of all available locations related to a registered cloud", response = Location.class, responseContainer = "List")
    public ResponseEntity<List<Location>>
            getCloudLocations(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A valid cloud identifier")
    @RequestParam(value = "cloudid")
    final Optional<String> cloudID) throws NotConnectedException {
        if (cloudID.isPresent()) {
            return ResponseEntity.ok(cloudService.getCloudLocations(sessionId, cloudID.get()));
        } else {
            return ResponseEntity.ok(cloudService.getAllCloudLocations(sessionId));
        }
    }
}
