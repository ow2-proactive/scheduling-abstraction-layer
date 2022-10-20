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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class PAGatewayInitThread extends Thread {

    @Autowired
    private PAGatewayService paGatewayService;

    @Autowired
    private ServiceConfiguration serviceConfiguration;

    @SneakyThrows
    @Override
    public void run() {
        paGatewayService.init(serviceConfiguration.getPaUrl());

        int retries = 0;
        boolean isConnected = false;
        Exception exception = new Exception("");
        while (!isConnected && retries < ServiceConfiguration.MAX_CONNECTION_RETRIES) {
            Thread.sleep(ServiceConfiguration.INTERVAL);
            try {
                LOGGER.debug("Trying connection to: {} with login: {} and password: {}",
                            serviceConfiguration.getPaUrl(),
                            serviceConfiguration.getPaLogin(),
                            serviceConfiguration.getPaPassword());
                paGatewayService.connect(serviceConfiguration.getPaLogin(), serviceConfiguration.getPaPassword());
                isConnected = true;
            } catch (RuntimeException re) {
                LOGGER.warn("Not able to connect to ProActive Scheduler : ", re);
                exception = re;
            }
            retries++;
        }
        if (isConnected) {
            LOGGER.info("Thread ProActive connection ended properly after {} attempts.", retries);
        } else {
            LOGGER.warn("Thread ProActive connection did not succeed after {} retries due to:", retries, exception);
        }
    }
}
