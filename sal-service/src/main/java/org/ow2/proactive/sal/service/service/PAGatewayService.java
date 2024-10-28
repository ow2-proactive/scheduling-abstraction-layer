/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.service;

import java.security.KeyException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;

import org.ow2.proactive.resourcemanager.common.event.RMNodeEvent;
import org.ow2.proactive.resourcemanager.exception.RMException;
import org.ow2.proactive.sal.service.service.application.PASchedulerGateway;
import org.ow2.proactive.sal.service.service.infrastructure.PAConnectorIaasGateway;
import org.ow2.proactive.sal.service.service.infrastructure.PAResourceManagerGateway;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.ow2.proactive_grid_cloud_portal.scheduler.exception.PermissionRestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;


@Log4j2
@Service("PAGatewayServiceService")
public class PAGatewayService {

    @Autowired
    private PAResourceManagerGateway resourceManagerGateway;

    @Autowired
    private PASchedulerGateway schedulerGateway;

    @Autowired
    private PAConnectorIaasGateway connectorIaasGateway;

    @Autowired
    private ServiceConfiguration serviceConfiguration;

    @Autowired
    private PAGatewayInitThread paGatewayInitThread;

    /**
     * Init a gateway to the ProActive server in an automatic way on application startup
     */
    @PostConstruct
    public void init() {
        paGatewayInitThread.start();
    }

    /**
     * Init a gateway to the ProActive server
     * @param paURL ProActive server URL (exp: https://try.activeeon.com:8443/)
     */
    public Boolean init(String paURL) {
        serviceConfiguration.setPaUrl(paURL);
        LOGGER.debug("Init ProActive's Resource Manager");
        resourceManagerGateway.init(paURL);
        LOGGER.debug("Init ProActive's Connector IAAS");
        connectorIaasGateway.init(paURL);
        LOGGER.debug("Init ProActive's Scheduler");
        schedulerGateway.init(paURL);
        return true;
    }

    /**
     * Connect to the ProActive server
     * @param login The user's username
     * @param password The user's password
     * @return The new session id
     * @throws LoginException In case the login is not valid
     * @throws KeyException In case the password is not valid
     * @throws RMException In case an error happens in the RM
     */
    public String connectAndInsist(String login, String password) throws LoginException, KeyException, RMException {
        int retries = 0;
        boolean isConnected = false;
        String sessionId = "";
        while (!isConnected && retries < ServiceConfiguration.MAX_CONNECTION_RETRIES) {
            try {
                sessionId = this.connect(login, password);
                isConnected = true;
                serviceConfiguration.setPaLogin(login);
                serviceConfiguration.setPaPassword(password);
            } catch (RuntimeException re) {
                LOGGER.warn("Not able to connect to ProActive Scheduler : ", re);
            }
            retries++;
        }
        if (isConnected) {
            LOGGER.info("Connected to RM and Scheduler after {} attempts.", retries);
        } else {
            LOGGER.info("Connection to RM and Scheduler failed after {} attempts.", retries);
        }
        return sessionId;
    }

    protected String connect(String username, String password) throws LoginException, KeyException, RMException {
        LOGGER.debug("Connecting to ProActive's Resource Manager");
        resourceManagerGateway.connect(username, password);
        LOGGER.debug("Connecting to ProActive's Scheduler");
        schedulerGateway.connect(username, password);
        return resourceManagerGateway.getSessionId();
    }

    /**
     * Disconnect from the ProActive server
     * @param sessionId A valid session id
     * @throws NotConnectedException In case the user is not connected
     */
    public void disconnect(String sessionId) throws NotConnectedException {
        if (!isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        LOGGER.debug("Disconnecting from RM...");
        resourceManagerGateway.disconnect();
        LOGGER.debug("Disconnecting from Scheduler...");
        schedulerGateway.disconnect();
    }

    /**
     * This function returns a list of available VMs
     * @param sessionId A session id
     * @return rmNodeEvents a list of available Nodes and their associate parameters
     * @throws NotConnectedException In case the user is not connected
     * @throws PermissionRestException In case the user does not have valid permissions
     */
    public List<RMNodeEvent> getActiveVMs(String sessionId) throws NotConnectedException, PermissionRestException {
        return resourceManagerGateway.getListOfNodesEvents();
    }

    /**
     * Verify that the provided sessionId corresponds to an active session
     * @param sessionId A session id
     * @return True if the connexion session is active, false otherwise
     * @throws NotConnectedException In case the user is not connected
     */
    public boolean isConnectionActive(String sessionId) throws NotConnectedException {
        boolean isActive = resourceManagerGateway.isActive(sessionId);
        if (isActive) {
            schedulerGateway.renewSession();
        }
        return isActive;
    }
}
