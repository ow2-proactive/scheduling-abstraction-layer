/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;


@Log4j2
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
