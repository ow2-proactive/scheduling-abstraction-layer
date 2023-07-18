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

import java.util.Map;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Getter
@Setter
@Embeddable
@JsonTypeName(value = "docker")
public class DockerEnvironment extends AbstractInstallation {

    @Column(name = "DOCKER_IMAGE")
    @JsonProperty("dockerImage")
    private String dockerImage;

    @Column(name = "PORT")
    @JsonProperty("port")
    private String port;

    @ElementCollection
    @CollectionTable(name = "DOCKER_ENV_VARS_MAPPING", joinColumns = { @JoinColumn(name = "DOCKER_TASK_ID") })
    @MapKeyColumn(name = "ENVVAR_KEY")
    @Column(name = "ENVVAR_VALUE")
    private Map<String, String> environmentVars;

    @Override
    public InstallationType getType() {
        return InstallationType.DOCKER;
    }

    @JsonIgnore
    public String getEnvVarsAsCommandString() {
        StringBuilder commandString = new StringBuilder();
        for (Map.Entry<String, String> entry : environmentVars.entrySet()) {
            commandString.append("-e ").append(entry.getKey()).append("=").append(entry.getValue()).append(" ");
        }
        return commandString.toString();
    }

    @PreRemove
    private void cleanMappedDataFirst() {
        this.environmentVars.clear();
    }
}
