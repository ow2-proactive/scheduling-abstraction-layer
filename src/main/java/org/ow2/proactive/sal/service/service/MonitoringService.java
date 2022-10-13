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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.Validate;
import org.ow2.proactive.sal.service.model.Deployment;
import org.ow2.proactive.sal.service.model.EmsDeploymentRequest;
import org.ow2.proactive.sal.service.model.OperatingSystemFamily;
import org.ow2.proactive.sal.service.model.PACloud;
import org.ow2.proactive.sal.service.util.EntityManagerHelper;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service("MonitoringService")
public class MonitoringService {

    @Autowired
    private PAGatewayService paGatewayService;

    /**
     * Add an EMS deployment to a defined job
     * @param sessionId A valid session id
     * @param nodeNames Names of the nodes to which to add EMS deployment
     * @param authorizationBearer The authorization bearer used by upperware's components to authenticate with each other. Needed by the EMS.
     * @return return 0 if the deployment task is properly added.
     */
    public Integer addEmsDeployment(String sessionId, List<String> nodeNames, String authorizationBearer)
            throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Validate.notNull(authorizationBearer, "The provided authorization bearer cannot be empty");
        EntityManagerHelper.begin();

        AtomicInteger failedDeploymentIdentification = new AtomicInteger();
        URL endpointPa;
        try {
            endpointPa = (new URL(paGatewayService.getPaURL()));
            String baguetteIp = endpointPa.getHost();
            int baguettePort = 8111;
            boolean isUsingHttps = true;

            // For supplied node ...
            nodeNames.forEach(node -> {
                Deployment deployment = EntityManagerHelper.find(Deployment.class, node);
                PACloud cloud = deployment.getPaCloud();

                // TODO Info to fetch from from nodeCandidate which should be linked to a deployment/node
                String operatingSystem = "UBUNTU";
                String targetType = "IAAS";

                EmsDeploymentRequest req = new EmsDeploymentRequest(authorizationBearer,
                                                                    baguetteIp,
                                                                    baguettePort,
                                                                    OperatingSystemFamily.fromValue(operatingSystem),
                                                                    targetType,
                                                                    deployment.getNodeName(),
                                                                    EmsDeploymentRequest.TargetProvider.fromValue(cloud.getCloudProviderName()),
                                                                    deployment.getLocationName(),
                                                                    isUsingHttps,
                                                                    deployment.getNodeName());
                deployment.setEmsDeployment(req);
                EntityManagerHelper.persist(req);
                EntityManagerHelper.persist(deployment);
            });

            EntityManagerHelper.commit();
        } catch (MalformedURLException me) {
            LOGGER.error(String.valueOf(me.getStackTrace()));
        }
        LOGGER.info("EMS deployment definition finished.");
        return failedDeploymentIdentification.get();
    }

    /**
     *
     * @return the list of all available EMS deployment monitor requests
     */
    public List<EmsDeploymentRequest> getMonitorsList(String sessionId) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        return EntityManagerHelper.createQuery("SELECT emsdr FROM EmsDeploymentRequest emsdr",
                                               EmsDeploymentRequest.class)
                                  .getResultList();
    }
}
