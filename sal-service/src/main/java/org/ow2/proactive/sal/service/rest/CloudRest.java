/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
@Api(tags = "Cloud Operations", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
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
    final String sessionId, @RequestParam
    final Optional<List<String>> cloudIds) throws NotConnectedException {
        if (cloudIds.isPresent()) {
            return ResponseEntity.ok(cloudService.findCloudsByIds(sessionId, cloudIds.get()));
        } else {
            return ResponseEntity.ok(cloudService.getAllClouds(sessionId));
        }
    }

    @RequestMapping(value = "/undeploy", method = RequestMethod.POST)
    @ApiOperation(value = "Undeploy clouds", response = Boolean.class)
    public ResponseEntity<Boolean>
            undeployClouds(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "List of cloud IDs to undeploy", required = true)
    @RequestBody
    final List<String> cloudIds,
                    @ApiParam(value = "If true undeploy node source immediately without waiting for nodes to be freed", defaultValue = "false")
                    @RequestHeader(value = "preempt", defaultValue = "false")
                    final Boolean preempt) throws NotConnectedException {
        return ResponseEntity.ok(cloudService.undeployClouds(sessionId, cloudIds, preempt));
    }

    @RequestMapping(value = "/remove", method = RequestMethod.DELETE)
    @ApiOperation(value = "Remove clouds", response = Boolean.class)
    public ResponseEntity<Boolean>
            removeClouds(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "List of cloud IDs to remove", required = true)
    @RequestBody
    final List<String> cloudIds,
                    @ApiParam(value = "If true undeploy node source immediately without waiting for nodes to be freed", defaultValue = "false")
                    @RequestHeader(value = "preempt", defaultValue = "false")
                    final Boolean preempt) throws NotConnectedException {
        return ResponseEntity.ok(cloudService.removeClouds(sessionId, cloudIds, preempt));
    }

    @RequestMapping(value = "/images", method = RequestMethod.GET)
    @ApiOperation(value = "Get the list of all available images related to a registered cloud", response = Image.class, responseContainer = "List")
    public ResponseEntity<List<Image>>
            getCloudImages(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A valid cloud identifier")
    @RequestParam(value = "cloudid")
    final Optional<String> cloudId) throws NotConnectedException {
        if (cloudId.isPresent()) {
            return ResponseEntity.ok(cloudService.getCloudImages(sessionId, cloudId.get()));
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
    final Optional<String> cloudId) throws NotConnectedException {
        if (cloudId.isPresent()) {
            return ResponseEntity.ok(cloudService.getCloudHardware(sessionId, cloudId.get()));
        } else {
            return ResponseEntity.ok(cloudService.getAllCloudHardware(sessionId));
        }
    }

    @RequestMapping(value = "/location", method = RequestMethod.GET)
    @ApiOperation(value = "Get the list of all available locations related to a registered cloud", response = Location.class, responseContainer = "List")
    public ResponseEntity<List<Location>>
            getCloudLocations(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A valid cloud identifier")
    @RequestParam(value = "cloudid")
    final Optional<String> cloudId) throws NotConnectedException {
        if (cloudId.isPresent()) {
            return ResponseEntity.ok(cloudService.getCloudLocations(sessionId, cloudId.get()));
        } else {
            return ResponseEntity.ok(cloudService.getAllCloudLocations(sessionId));
        }
    }
}
