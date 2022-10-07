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

import org.ow2.proactive.sal.service.model.*;
import org.ow2.proactive.sal.service.util.EntityManagerHelper;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service("PersistenceService")
public class PersistenceService {

    @Autowired
    private PAGatewayService paGatewayService;

    /**
     * Clean all DB entries
     * @param sessionId A valid session id
     */
    public void cleanAll(String sessionId) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        EntityManagerHelper.begin();
        LOGGER.info("Cleaning Jobs ...");
        Job.clean();
        LOGGER.info("Cleaning Tasks ...");
        Task.clean();
        LOGGER.info("Cleaning PAClouds ...");
        PACloud.clean();
        LOGGER.info("Cleaning Deployments ...");
        Deployment.clean();
        LOGGER.info("Cleaning Ports ...");
        Port.clean();
        LOGGER.info("Cleaning Credentials ...");
        Credentials.clean();
        LOGGER.info("Cleaning EmsDeploymentRequests ...");
        EmsDeploymentRequest.clean();
        LOGGER.info("Cleaning NodeCandidates ...");
        NodeCandidate.clean();
        LOGGER.info("Cleaning Clouds ...");
        Cloud.clean();
        LOGGER.info("Cleaning Images ...");
        Image.clean();
        LOGGER.info("Cleaning Hardwares ...");
        Hardware.clean();
        LOGGER.info("Cleaning Jobs ...");
        Location.clean();
        LOGGER.info("Cleaning ByonNodes ...");
        ByonNode.clean();
        LOGGER.info("Cleaning EdgeNodes ...");
        EdgeNode.clean();
        EntityManagerHelper.commit();
        LOGGER.info("Done.");
    }
}
