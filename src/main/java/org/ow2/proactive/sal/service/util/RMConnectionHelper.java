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

import java.security.KeyException;
import java.util.prefs.Preferences;

import javax.security.auth.login.LoginException;

import org.ow2.proactive.resourcemanager.exception.RMException;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.ow2.proactive_grid_cloud_portal.common.RMRestInterface;
import org.ow2.proactive_grid_cloud_portal.rm.client.RMRestClient;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class RMConnectionHelper {

    private static final Preferences userPreferences = Preferences.userRoot().node("USER_PREFERENCES");

    private static String sessionPreferencesId;

    private static final String RESOURCE_MANAGER_REST_PATH = "/rest";

    private static String sessionId = "";

    private static RMRestInterface rmRestInterface;

    /**
     * Initialize the API to RM
     *
     * @param paURL PA rest URL
     * @return The initialized RM Interface to be used for sending request to the platform
     */
    public static RMRestInterface init(String paURL) {
        if (paURL.contains("trydev2.activeeon")) {
            sessionPreferencesId = "RM_sessionId_trydev2";
        } else if (paURL.contains("trydev.activeeon")) {
            sessionPreferencesId = "RM_sessionId";
        } else {
            sessionPreferencesId = "TESTING_PREF";
        }
        // Initialize the client
        rmRestInterface = new RMRestClient(paURL + RESOURCE_MANAGER_REST_PATH, null).getRm();
        // Get the user session ID
        sessionId = userPreferences.get(sessionPreferencesId, "");
        LOGGER.debug("Gateway to the ProActive Resource Manager is established");
        return rmRestInterface;
    }

    /**
     * Connect to the RM
     *
     * @param username Username
     * @param password Password
     * @throws LoginException In case the login is not valid
     * @throws KeyException   In case the password is not valid
     * @throws RMException    In case an error happens in the RM
     */
    public static synchronized void connect(String username, String password)
            throws LoginException, KeyException, RMException {
        LOGGER.debug("Connecting to RM ...");
        try {
            // If the sessionId is equals to "" (empty), an exception will occurs.
            // If the sessionId is valid ==> Already connected
            // If the sessionId is invalid we create a new session by establishing a new connection to the RM
            if (isActive()) {
                LOGGER.info("Already Connected to RM");
            } else {
                // Connect and create a new session
                sessionId = rmRestInterface.rmConnect(username, password);
                // Save the session
                userPreferences.put(sessionPreferencesId, sessionId);
                LOGGER.info("Connected to RM");
            }
        } catch (Exception NAE) {
            // Exception is triggered when the sessionId is equal to ""
            sessionId = rmRestInterface.rmConnect(username, password);
            userPreferences.put(sessionPreferencesId, sessionId);
            LOGGER.info("Connected to RM");
        }
    }

    /**
     * Disconnect from the RM API
     */
    public static synchronized void disconnect() {
        try {
            sessionId = userPreferences.get(sessionPreferencesId, "");
            // Check if the session still active
            if (isActive()) {
                try {
                    LOGGER.debug("Disconnecting from RM...");
                    rmRestInterface.rmDisconnect(sessionId);
                    LOGGER.info("Disconnected from RM.");

                } catch (NotConnectedException nce) {
                    LOGGER.warn("WARNING: Not able to disconnect due to: " + nce.toString());
                }
            } else {
                LOGGER.info("Already disconnected from RM");
            }
            // Clear local session
            sessionId = "";
            // Remove the stored session
            userPreferences.remove(sessionPreferencesId);
        } catch (Exception e) {
            // Exception will trigger if the sessionId is empty
            LOGGER.info("Already disconnected from RM");
            // Clear local session
            sessionId = "";
            // Remove the stored session
            userPreferences.remove(sessionPreferencesId);
        }
    }

    public static Boolean isActive() throws NotConnectedException {
        return rmRestInterface.isActive(sessionId);
    }

    public static Boolean isActive(String sessionId) throws NotConnectedException {
        return rmRestInterface.isActive(sessionId);
    }

    public static String getSessionId() {
        return sessionId;
    }

    // For Testing Dev

    public static void setSessionPreferencesId(String sessionPreferencesId) {
        RMConnectionHelper.sessionPreferencesId = sessionPreferencesId;
    }

    public static void setRmRestInterface(RMRestInterface rmRestInterface) {
        RMConnectionHelper.rmRestInterface = rmRestInterface;
    }
}
