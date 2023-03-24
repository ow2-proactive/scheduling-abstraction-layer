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

import java.util.Map;
import javax.ws.rs.core.MediaType;
import org.ow2.proactive.sal.model.ByonNode;
import org.ow2.proactive.sal.service.service.VaultService;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
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
    final Map<String, String> secret) throws NotConnectedException {
        return ResponseEntity.ok(vaultService.registerNewSecrets(sessionId, secret));
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Remove a secret from the ProActive vault", response = ByonNode.class)
    public ResponseEntity<Boolean>
            removeSecret(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId, @ApiParam(value = "Key of a secret to be removed", required = true)
    @PathVariable
    final String key) throws NotConnectedException {
        return ResponseEntity.ok(vaultService.removeSecret(sessionId, key));
    }

    @RequestMapping(value = "/remove", method = RequestMethod.DELETE)
    @ApiOperation(value = "Remove all secrets from the ProActive vault", response = ByonNode.class)
    public ResponseEntity<Boolean>
            removeAllSecrets(@ApiParam(value = "Proactive authentication session id", required = true)
    @RequestHeader(value = "sessionid")
    final String sessionId) throws NotConnectedException {
        return ResponseEntity.ok(vaultService.removeAllSecrets(sessionId));
    }
}
