
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

import javax.sql.DataSource;

import org.ow2.proactive.sal.service.service.RepositoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;


/**
 * @author ActiveEon Team
 * @since 23/06/2017
 */
@Profile("test")
@EnableAutoConfiguration
@EntityScan(basePackages = { "org.ow2.proactive.sal.service.model" })
@PropertySource("classpath:application-test.properties")
public class IntegrationTestConfig {

    @Value("${spring.datasource.driverClassName:}")
    private String dataSourceDriverClassName;

    @Value("${spring.datasource.url:}")
    private String dataSourceUrl;

    @Value("${spring.datasource.username:}")
    private String dataSourceUsername;

    @Value("${spring.datasource.password:}")
    private String dataSourcePassword;

    @Bean
    public DataSource testDataSource() {
        return createDataSource();
    }

    private DataSource createDataSource() {
        return DataSourceBuilder.create()
                                .username(dataSourceUsername)
                                .password(dataSourcePassword)
                                .url(dataSourceUrl)
                                .driverClassName(dataSourceDriverClassName)
                                .build();
    }

    @Bean
    public RepositoryService repositoriesService() {
        return new RepositoryService();
    }

}
