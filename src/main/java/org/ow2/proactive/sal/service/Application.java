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
package org.ow2.proactive.sal.service;

import javax.servlet.ServletContext;

import org.ow2.proactive.sal.service.util.EntityScanRoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.google.common.base.Predicates;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * @author ActiveEon Team
 */
@SpringBootApplication(scanBasePackages = { "org.ow2.proactive.sal.service" })
@EnableAutoConfiguration(exclude = { MultipartAutoConfiguration.class })
@EnableSwagger2
@EnableAsync
@PropertySources({ @PropertySource(value = "classpath:application.properties"),
                   @PropertySource(value = "file:${EXTERNAL_CONFIG_DIR}/${PROPERTIES_FILENAME}.properties", ignoreResourceNotFound = true) })
@EntityScan(basePackages = "org.ow2.proactive.sal.service.model")
@EntityScanRoot("classpath:/org/ow2/proactive/sal/service/model")
@EnableTransactionManagement
public class Application extends WebMvcConfigurerAdapter {

    @Autowired
    ServletContext context;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false)
                  .favorParameter(true)
                  .parameterName("format")
                  .ignoreAcceptHeader(true)
                  .useJaf(false)
                  .defaultContentType(MediaType.APPLICATION_JSON)
                  .mediaType("json", MediaType.APPLICATION_JSON);
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new CommonsMultipartResolver();
    }

    /*
     * The following code is for Swagger documentation
     */
    @Bean
    public Docket microserviceApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
                                                      .groupName("scheduling-abstraction-layer")
                                                      .select()
                                                      .apis(RequestHandlerSelectors.any())
                                                      .paths(PathSelectors.any())
                                                      .paths(Predicates.not(PathSelectors.regex("/error")))
                                                      .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Scheduling Abstraction Layer (SAL) Service API")
                                   .description("The purpose of this module is to allow users to interact with a running Proactive server in a lazy mode\n")
                                   .license("Activeeon")
                                   .licenseUrl("https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/LICENSE")
                                   .version("1.0")
                                   .build();
    }
}
