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
package org.ow2.proactive.sal.service.util;

import org.ow2.proactive.authentication.ConnectionInfo;
import org.ow2.proactive.scheduler.common.exception.PermissionException;
import org.ow2.proactive_grid_cloud_portal.smartproxy.RestSmartProxyImpl;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class SchedulerConnectionHelper {

    private static final String SCHEDULER_REST_PATH = "/rest";

    private static RestSmartProxyImpl restSmartProxy = new RestSmartProxyImpl();

    private static String paURL;

    // For testing purpose only
    private static boolean isActive;

    /**
     * Initialize the gateway URL
     *
     * @param url URL for the ProActive Rest service
     */
    public static void init(String url) {
        paURL = url;
    }

    /**
     * Connect to the Scheduler gateway
     *
     * @param username Username
     * @param password Password
     * @return The initialized Scheduler gateway
     */
    public static synchronized RestSmartProxyImpl connect(String username, String password) {
        LOGGER.debug("Connecting to Scheduler ...");
        //TODO: TO improve the concatenation of URLs
        ConnectionInfo connectionInfo = new ConnectionInfo(paURL + SCHEDULER_REST_PATH, username, password, null, true);
        // Check if the proxy is connected
        // If not make a new connection
        if (!restSmartProxy.isConnected()) {
            restSmartProxy.init(connectionInfo);
            LOGGER.info("Connected to Scheduler");
        } else {
            LOGGER.info("Already connected to Scheduler");
        }
        isActive = true;
        return restSmartProxy;
    }

    /**
     * Disconnect from the Scheduler
     *
     * @return The disconnected Scheduler gateway
     */
    public static synchronized RestSmartProxyImpl disconnect() {
        try {
            if (restSmartProxy.isConnected()) {
                restSmartProxy.disconnect();
                isActive = false;
                LOGGER.info("Disconnected from Scheduler");
            } else {
                LOGGER.info("Already disconnected from Scheduler");
            }
        } catch (PermissionException e) {
            LOGGER.warn("WARNING: Not able to disconnect due to: " + e.toString());
        }
        return restSmartProxy;
    }

    // All of the following methods are used for Test Driven Dev
    public static boolean getIsActive() {
        return isActive;
    }

    public static String getPaURL() {
        return paURL;
    }

    public static void setRestSmartProxy(RestSmartProxyImpl restSmartProxy) {
        SchedulerConnectionHelper.restSmartProxy = restSmartProxy;
    }
}
