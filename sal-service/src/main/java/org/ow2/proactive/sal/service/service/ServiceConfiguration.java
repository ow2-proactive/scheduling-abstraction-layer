/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import lombok.Getter;
import lombok.Setter;


@Configuration
@PropertySources({ @PropertySource(value = "classpath:application.properties"),
                   @PropertySource(value = "file:${EXTERNAL_CONFIG_DIR}/${PROPERTIES_FILENAME}.properties", ignoreResourceNotFound = true) })
@Getter
@Setter
public class ServiceConfiguration {

    public static final int MAX_CONNECTION_RETRIES = 5;

    public static final int INTERVAL = 5000;

    public static final int TIMEOUT = 10;

    @Value("${pa.url}")
    private String paUrl;

    @Value("${pa.login}")
    private String paLogin;

    @Value("${pa.password}")
    private String paPassword;
}
