/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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

    public static final String JSON_DOCKER_IMAGE = "dockerImage";

    public static final String JSON_PORT = "port";

    @JsonProperty(JSON_DOCKER_IMAGE)
    private String dockerImage;

    @JsonProperty(JSON_PORT)
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
        if (this.environmentVars != null) {
            this.environmentVars.clear();
        }
    }
}
