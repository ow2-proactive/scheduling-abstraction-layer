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
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.ow2.proactive.sal.model.*;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;


@Log4j2
@Service("MonitoringService")
public class MonitoringService {

    @Autowired
    private PAGatewayService paGatewayService;

    @Autowired
    private ServiceConfiguration serviceConfiguration;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * Add an EMS deployment to a defined job
     * @param sessionId A valid session id
     * @param emsDeploymentDefinition An EMS deployment definition
     * @return return 0 if the deployment task is properly added.
     */
    public Integer addEmsDeployment(String sessionId, EmsDeploymentDefinition emsDeploymentDefinition)
            throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        Validate.notNull(emsDeploymentDefinition.getAuthorizationBearer(),
                         "The provided authorization bearer cannot be empty");

        LOGGER.info("Adding EMS monitors for request definition: {}", emsDeploymentDefinition);

        AtomicInteger failedDeploymentIdentification = new AtomicInteger();
        URL endpointPa;
        try {
            endpointPa = (new URL(serviceConfiguration.getPaUrl()));
            String baguetteIp = endpointPa.getHost();
            int baguettePort = 8111;
            boolean isUsingHttps = true;

            // For supplied node ...
            emsDeploymentDefinition.getNodeNames().forEach(node -> {
                LOGGER.info("Adding monitors for node [{}] ...", node);

                Deployment deployment = repositoryService.getDeployment(node);
                PACloud cloud = deployment.getPaCloud();
                LOGGER.info("The Monitors isPrivateIp is set to [{}] ...", emsDeploymentDefinition.isPrivateIP());

                EmsDeploymentRequest req = new EmsDeploymentRequest(emsDeploymentDefinition.getAuthorizationBearer(),
                                                                    baguetteIp,
                                                                    baguettePort,
                                                                    deployment.getDeploymentType().getName(),
                                                                    deployment.getNode().getNodeCandidate(),
                                                                    deployment.getNodeName(),
                                                                    EmsDeploymentRequest.TargetProvider.fromValue(cloud.getCloudProviderName()),
                                                                    deployment.getTask()
                                                                              .getPortsToOpen()
                                                                              .stream()
                                                                              .map(Port::getValue)
                                                                              .collect(Collectors.toList())
                                                                              .toString(),
                                                                    isUsingHttps,
                                                                    emsDeploymentDefinition.isPrivateIP(),
                                                                    deployment.getNodeName());
                deployment.setEmsDeployment(req);
                repositoryService.saveEmsDeploymentRequest(req);
                repositoryService.saveDeployment(deployment);
                LOGGER.info("Monitors added for node [{}].", node);
            });

            repositoryService.flush();
        } catch (MalformedURLException me) {
            LOGGER.error(String.valueOf(me.getStackTrace()));
        }
        LOGGER.info("EMS deployment definition finished for nodes: [{}].", emsDeploymentDefinition.getNodeNames());
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
        return repositoryService.listEmsDeploymentRequests();
    }
}
