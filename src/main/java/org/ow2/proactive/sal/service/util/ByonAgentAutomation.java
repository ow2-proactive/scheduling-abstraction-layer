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

import org.ow2.proactive.sal.common.model.ByonNode;

import com.jcraft.jsch.JSchException;

import lombok.extern.log4j.Log4j2;


/*
 * This class Automates the deployment of ProActive node agents.
 */
@Log4j2
public class ByonAgentAutomation {
    private final String username;

    private final String logFile;

    private boolean logFileFlag;

    private String scriptURL;

    private final SSHConnection sshConnection;

    private final int PORT = 22;

    private final String SCRIPT_DIR = "/tmp/proactive-agent.sh";

    private final String DEBIAN_SCRIPT = "https://raw.githubusercontent.com/alijawadfahs/Byon-setup/main/scripts/configure-byon-Debian.sh";

    public ByonAgentAutomation(ByonNode byonNode) {
        logFile = "/var/log/byonSSH." + byonNode.getId() + ".log";
        logFileFlag = false;
        username = byonNode.getLoginCredential().getUsername();
        selectScriptURL(byonNode);
        sshConnection = new SSHConnection(username, // ssh user name
                                          byonNode.getLoginCredential().getPassword(), // ssh password
                                          byonNode.getIpAddresses().get(0).getValue(), // ssh host name
                                          PORT, // ssh port
                                          logFile); // Log file directory
    }

    /**
     * A function to automatically deploy ProActive node agent using an SSH connection.
     */
    public void prepareByonNode() {
        try {
            createLogFile();
            LOGGER.info("Trying to download the script to the BYON node ");
            sshConnection.executeSSHCommand("wget -nv -O " + SCRIPT_DIR + " " + scriptURL, logFileFlag);
            sshConnection.executeSSHCommand("chmod +x " + SCRIPT_DIR, logFileFlag);
            LOGGER.info("Running the script on the BYON node ");
            sshConnection.executeSSHCommand(SCRIPT_DIR, false);
        } catch (JSchException | InterruptedException e) {
            LOGGER.error("An error while preparing the node: ", e);
        }
    }

    /**
     * Create a log file for the commands to be executed.
     */
    private void createLogFile() {
        if (!logFileFlag) {
            try {
                LOGGER.info("Trying to create the byonSSH logfile");
                sshConnection.executeSSHCommand("sudo touch " + logFile, logFileFlag);
                sshConnection.executeSSHCommand("sudo chown " + username + ":" + username + " " + logFile, logFileFlag);

            } catch (JSchException | InterruptedException e) {
                LOGGER.error("An error while creating the log file: ", e);
            } finally {
                logFileFlag = true;
                LOGGER.info("Byon SSH logs are saved at: {}", logFile);
            }
        }
    }

    /**
     * select the script URL according to the node operating system
     */
    private void selectScriptURL(ByonNode byonNode) {
        String os = byonNode.getNodeProperties().getOperatingSystem().getOperatingSystemFamily().toString();
        switch (os) {
            case "UBUNTU":
            case "DEBIAN":
                scriptURL = DEBIAN_SCRIPT;
                break;
            default:
                LOGGER.error("The Operating System \"{}\" does not support automated configuration, the installation of ProActive node agent should be done manually",
                             os);
                throw new IllegalArgumentException("The OS does not support automated configuration");
                // code block
        }
    }
}
