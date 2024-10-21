/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.rest;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.ow2.proactive.sal.model.ByonNode;
import org.ow2.proactive.sal.model.VaultKey;
import org.ow2.proactive.sal.service.service.VaultService;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.ow2.proactive.scheduler.common.exception.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@RestController
@RequestMapping(value = "/vault")
@Api(description = "Operations on ProActive secrets vault", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
public class VaultRest {
    @Autowired
    private VaultService vaultService;

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Register new secrets in ProActive vault", response = ByonNode.class)
    public ResponseEntity<Boolean>
            registerNewSecrets(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "A key/value mapping of the secret", required = true)
    @RequestBody
    final Map<String, String> secret) throws SchedulerException {
        return ResponseEntity.ok(vaultService.registerNewSecrets(sessionId, secret));
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "Get all added vaults", response = VaultKey.class, responseContainer = "List")
    public ResponseEntity<List<VaultKey>>
            getVaultKeys(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId) throws NotConnectedException {
        return ResponseEntity.ok(vaultService.getVaultKeys(sessionId));
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Remove a secret from the ProActive vault", response = ByonNode.class)
    public ResponseEntity<Boolean>
            removeSecret(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "Key of a secret to be removed", required = true)
    @PathVariable
    final String key) throws SchedulerException {
        return ResponseEntity.ok(vaultService.removeSecret(sessionId, key));
    }
}
