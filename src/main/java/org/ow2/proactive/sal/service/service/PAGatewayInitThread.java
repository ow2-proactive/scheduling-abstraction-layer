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

import java.util.concurrent.*;

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
        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<String> task = () -> paGatewayService.connect(serviceConfiguration.getPaLogin(),
                                                               serviceConfiguration.getPaPassword());
        Future<String> future = executor.submit(task);
        while (!isConnected && retries < ServiceConfiguration.MAX_CONNECTION_RETRIES) {
            Thread.sleep(ServiceConfiguration.INTERVAL);
            try {
                LOGGER.debug("Trying connection to: {} with login: {} and password: {}",
                             serviceConfiguration.getPaUrl(),
                             serviceConfiguration.getPaLogin(),
                             serviceConfiguration.getPaPassword());
                future.get(ServiceConfiguration.TIMEOUT, TimeUnit.SECONDS);
                isConnected = true;
            } catch (RuntimeException re) {
                LOGGER.warn("Connection failed due to a RuntimeException: ", re);
                exception = re;
            } catch (TimeoutException ex) {
                LOGGER.warn("Connection failed due to a TimeoutException: ", ex);
            } catch (InterruptedException ie) {
                LOGGER.warn("Connection failed due to an InterruptedException: ", ie);
            } catch (ExecutionException ee) {
                LOGGER.warn("Connection failed due to an ExecutionException: ", ee);
            } finally {
                future.cancel(true); // may or may not desire this
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
