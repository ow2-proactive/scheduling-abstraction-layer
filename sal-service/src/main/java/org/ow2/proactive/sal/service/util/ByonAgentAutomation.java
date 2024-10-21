/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.util;

import org.ow2.proactive.sal.model.ByonNode;

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

    private final String DEBIAN_SCRIPT = "https://raw.githubusercontent.com/ow2-proactive/utility-scripts/main/morphemic-scripts/configure-byon-Debian-13.1.0-SNAPSHOT.sh";

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
