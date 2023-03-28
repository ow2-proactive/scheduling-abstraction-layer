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
package org.ow2.proactive.sal.service.service;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.ow2.proactive.sal.model.VaultKey;
import org.ow2.proactive.sal.service.service.application.PASchedulerGateway;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.ow2.proactive.scheduler.common.exception.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;


@Log4j2
@Service("VaultService")
public class VaultService {

    @Autowired
    private PAGatewayService paGatewayService;

    @Autowired
    private PASchedulerGateway schedulerGateway;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * Get all added vault keys
     * @param sessionId A valid session id
     * @return List of all table VaultKey's entries
     */
    public List<VaultKey> getVaultKeys(String sessionId) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        return repositoryService.listVaultKeys();
    }

    /**
     * Register new secrets in the ProActive Vault
     * @param sessionId A valid session id
     * @param secrets A map of keys/values to be added as secrets to ProActive Vault
     * @return true if the process finished with no errors
     */
    public Boolean registerNewSecrets(String sessionId, Map<String, String> secrets) throws SchedulerException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Validate.notNull(secrets, "No secret where received in the request body.");
        Set<String> keys = getVaultKeys(sessionId).stream().map(VaultKey::getKeyName).collect(Collectors.toSet());
        for (String key : secrets.keySet()) {
            String value = secrets.get(key);
            LOGGER.info("Registering vault key: \"{}\".", key);
            try {
                schedulerGateway.putThirdPartyCredential(key, value);
            } catch (SchedulerException e) {
                LOGGER.error("An error occurred while adding a secret.\n", e);
                throw e;
            }
            if (keys.contains(key)) {
                LOGGER.warn("A secret with the same key \"{}\" exist! the value will be overwritten!", key);
            } else {
                repositoryService.saveVaultKey(new VaultKey(key));
            }
            LOGGER.info("Vault key \"{}\" registered successfully.", key);
        }
        repositoryService.flush();
        return true;
    }

    /**
     * Remove a secret from the ProActive Vault
     * @param sessionId A valid session id
     * @param key A key of a secret to be deleted
     * @return true if the process finished with no errors
     */
    public Boolean removeSecret(String sessionId, String key) throws SchedulerException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Validate.notNull(key, "No key was received in the request path.");
        LOGGER.info("Removing vault key: \"{}\".", key);
        VaultKey vaultKey = repositoryService.getVaultKey(key);
        if (vaultKey == null) {
            LOGGER.error("VaultKey {} not found, nothing to be removed.", key);
            return false;
        }
        try {
            schedulerGateway.removeThirdPartyCredential(key);
        } catch (SchedulerException e) {
            LOGGER.error("An error occurred while removing the key.\n", e);
            throw e;
        }
        repositoryService.deleteVaultKey(key);
        LOGGER.info("Vault key \"{}\" deleted.", key);
        return true;
    }
}
