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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import lombok.Getter;
import lombok.Setter;


@Configuration
@PropertySources({ @PropertySource(value = "classpath:application.properties"),
                   @PropertySource(value = "file:${MELODIC_CONFIG_DIR}/eu.morphemic.schedulingAbstractionLayer.properties", ignoreResourceNotFound = true) })
@Getter
@Setter
public class ServiceConfiguration {

    public static final int MAX_CONNECTION_RETRIES = 10;

    public static final int INTERVAL = 10000;

    @Value("${pa.url}")
    private String paUrl;

    @Value("${pa.login}")
    private String paLogin;

    @Value("${pa.password}")
    private String paPassword;
}
