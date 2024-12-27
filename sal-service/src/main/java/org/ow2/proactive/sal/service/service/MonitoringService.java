/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.ow2.proactive.sal.model.*;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

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

    private String baguetteIp;

    private static final int BAGUETTE_PORT = 8111;

    private static final boolean IS_USING_HTTPS = true;

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
        // For supplied node ...
        emsDeploymentDefinition.getNodeNames().forEach(nodeName -> {
            Deployment deployment = repositoryService.getDeployment(nodeName);
            addEmsDeploymentForNode(deployment,
                                    emsDeploymentDefinition.getAuthorizationBearer(),
                                    emsDeploymentDefinition.isPrivateIP());
            repositoryService.saveDeployment(deployment);
        });

        repositoryService.flush();

        LOGGER.info("EMS deployment definition finished for nodes: [{}].", emsDeploymentDefinition.getNodeNames());
        return failedDeploymentIdentification.get();
    }

    /**
     * Add an EMS deployment to a defined node
     * @param deployment A deployment
     * @param authorizationBearer The ems authorization bearer
     * @param isPrivateIp If private ip is needed
     */
    public Deployment addEmsDeploymentForNode(Deployment deployment, String authorizationBearer, boolean isPrivateIp) {
        LOGGER.info("Adding monitors for node [{}] ...", deployment.getNodeName());

        PACloud cloud = deployment.getPaCloud();
        LOGGER.info("The monitors isPrivateIp is set to [{}] ...", isPrivateIp);

        EmsDeploymentRequest req = new EmsDeploymentRequest(authorizationBearer,
                                                            getBaguetteIp(),
                                                            BAGUETTE_PORT,
                                                            deployment.getDeploymentType().getName(),
                                                            deployment.getNode().getNodeCandidate(),
                                                            deployment.getNodeName(),
                                                            EmsDeploymentRequest.TargetProvider.fromValue(cloud.getCloudProvider()
                                                                                                               .toString()),
                                                            deployment.getTask()
                                                                      .getPortsToOpen()
                                                                      .stream()
                                                                      .map(Port::getValue)
                                                                      .collect(Collectors.toList())
                                                                      .toString(),
                                                            IS_USING_HTTPS,
                                                            isPrivateIp,
                                                            deployment.getNodeName());
        req = repositoryService.saveEmsDeploymentRequest(req);
        deployment.setEmsDeployment(req);

        LOGGER.info("Monitors added for node [{}].", deployment.getNodeName());
        return deployment;
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

    private String getBaguetteIp() {
        if (!Strings.isNullOrEmpty(this.baguetteIp))
            return this.baguetteIp;

        URL endpointPa;
        try {
            endpointPa = (new URL(serviceConfiguration.getPaUrl()));
            this.baguetteIp = endpointPa.getHost();
        } catch (MalformedURLException me) {
            LOGGER.error(Arrays.toString(me.getStackTrace()));
        }
        return this.baguetteIp;
    }
}
