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
package org.ow2.proactive.sal.model;

import java.io.Serializable;

import javax.persistence.Embedded;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;


/**
 * Attributes defining a Cloud`
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class CloudDefinition implements Serializable {

    @JsonProperty("cloudID")
    private String cloudID = null;

    @JsonProperty("cloudProviderName")
    private String cloudProviderName = null;

    @JsonProperty("cloudType")
    private CloudType cloudType = null;

    @JsonProperty("securityGroup")
    private String securityGroup = null;

    @JsonProperty("subnet")
    private String subnet = null;

    @Embedded
    @JsonProperty("sshCredentials")
    private SSHCredentials sshCredentials = null;

    @JsonProperty("endpoint")
    private String endpoint = null;

    @Embedded
    @JsonProperty("scope")
    private Scope scope = null;

    @JsonProperty("identityVersion")
    private String identityVersion;

    @JsonProperty("defaultNetwork")
    private String defaultNetwork;

    @JsonProperty("credentials")
    private Credential credentials;

    @JsonProperty("blacklist")
    private String blacklist;
}
