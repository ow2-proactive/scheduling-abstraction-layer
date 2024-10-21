/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.service;

import java.io.File;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;


@PropertySources({ @PropertySource(value = "classpath:application.properties"),
                   @PropertySource(value = "file:${EXTERNAL_CONFIG_DIR}/${PROPERTIES_FILENAME}.properties", ignoreResourceNotFound = true) })
@Configuration
public class RepositoryConfiguration {

    @Value("${spring.datasource.driverClassName:}")
    private String dataSourceDriverClassName;

    @Value("${spring.datasource.url:}")
    private String dataSourceUrl;

    @Value("${spring.datasource.username:}")
    private String dataSourceUsername;

    @Value("${spring.datasource.password:}")
    private String dataSourcePassword;

    @Bean
    @Profile("default")
    public DataSource defaultDataSource() {
        String jdbcUrl = dataSourceUrl;

        if (jdbcUrl.isEmpty()) {
            jdbcUrl = "jdbc:hsqldb:file:" + getDatabaseDirectory() +
                      ";create=true;hsqldb.tx=mvcc;hsqldb.applog=1;hsqldb.sqllog=0;hsqldb.write_delay=false";
        }

        return DataSourceBuilder.create()
                                .username(dataSourceUsername)
                                .password(dataSourcePassword)
                                .url(jdbcUrl)
                                .driverClassName(dataSourceDriverClassName)
                                .build();
    }

    @Bean
    @Profile("mem")
    public DataSource memDataSource() {
        return createMemDataSource();
    }

    @Bean
    @Profile("test")
    public DataSource testDataSource() {
        return createMemDataSource();
    }

    private DataSource createMemDataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        EmbeddedDatabase db = builder.setType(EmbeddedDatabaseType.HSQL).build();
        return db;
    }

    private String getDatabaseDirectory() {
        String proactiveHome = System.getProperty("proactive.home");

        if (proactiveHome == null) {
            return System.getProperty("java.io.tmpdir") + File.separator + "proactive" + File.separator +
                   "microservice-template";
        }

        return proactiveHome + File.separator + "data" + File.separator + "db" + File.separator +
               "microservice-template";
    }
}
