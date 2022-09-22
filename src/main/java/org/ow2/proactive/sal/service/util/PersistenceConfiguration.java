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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class PersistenceConfiguration {

    private static final ListDelimiterHandler DELIMITER = new DefaultListDelimiterHandler(';');

    public static String PERSISTENCE_URL = "sal.persistence.url";

    public static String PERSISTENCE_USERNAME = "sal.persistence.username";

    public static String PERSISTENCE_PASSWORD = "sal.persistence.password";

    public static Configuration loadPersistenceConfiguration() throws ConfigurationException {
        String PERSISTENCE_PROPERTIES_FILE_PATH = PathHelper.getPersistencePropertiesFilePath();
        PropertiesBuilderParameters propertyParameters = new Parameters().properties();
        propertyParameters.setPath(PERSISTENCE_PROPERTIES_FILE_PATH);
        propertyParameters.setThrowExceptionOnMissing(true);
        propertyParameters.setListDelimiterHandler(DELIMITER);

        FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class);

        builder.configure(propertyParameters);

        LOGGER.debug("Persistence configuration loaded");

        return builder.getConfiguration();
    }

    public static Map<String, String> getAllPersistenceConfigurationPropertiesAsMap() throws ConfigurationException {
        final String JAVAX_URL_PROP = "javax.persistence.jdbc.url";
        final String JAVAX_USERNAME_PROP = "javax.persistence.jdbc.user";
        final String JAVAX_PASSWORD_PROP = "javax.persistence.jdbc.password";

        Map<String, String> persistenceProperties = new HashMap<>();

        Configuration persistenceConfiguration = loadPersistenceConfiguration();

        persistenceProperties.put(JAVAX_URL_PROP, persistenceConfiguration.getString(PERSISTENCE_URL));
        persistenceProperties.put(JAVAX_USERNAME_PROP, persistenceConfiguration.getString(PERSISTENCE_USERNAME));
        persistenceProperties.put(JAVAX_PASSWORD_PROP, persistenceConfiguration.getString(PERSISTENCE_PASSWORD));

        return persistenceProperties;
    }
}
