/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.util;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import lombok.extern.log4j.Log4j2;


/*
 * This class creates an SSH connection using jsch package.
 *
 * Developed by Activeeon in the context of H2020 MORPHEMIC project.
 * @author Activeeon R&D Department
 */

@Log4j2
public class SSHConnection {
    private final String username;

    private final String password;

    private final String host;

    private final String logFile;

    private final int port;

    public SSHConnection(String username, String password, String host, int port, String logFile) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.logFile = logFile;
    }

    public void executeSSHCommand(String command, boolean logFileFlag) throws JSchException, InterruptedException {

        Session session = null;
        ChannelExec channel = null;
        if (logFileFlag) {
            command += " >> " + logFile;
        }
        command += " 2>&1";

        try {
            session = new JSch().getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            LOGGER.info("Executing on {}: {}", host, command);
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setOutputStream(responseStream, true);
            channel.setErrStream(responseStream, true);
            channel.connect();

            while (!channel.isClosed()) {
                Thread.sleep(100);
            }
            if (channel.getExitStatus() != 0) {
                LOGGER.error("An error occurred while executing the command, please check the logfile: {}", logFile);
            }
            String responseString = new String(responseStream.toByteArray());
            if (!responseString.equals("")) {
                LOGGER.info("The Output of the command is: {}", responseString);
            }
        } catch (JSchException | InterruptedException e) {
            LOGGER.error("An error occurred while Executing an SSH command on {}", host);
            throw e;
        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }
    }
}
