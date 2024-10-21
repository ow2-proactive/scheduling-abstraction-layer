/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = CommandsInstallation.class, name = "commands"),
                @JsonSubTypes.Type(value = DockerEnvironment.class, name = "docker") })
public abstract class AbstractInstallation implements Installation {

    @Column(name = "INSTALLATION_TYPE")
    @JsonProperty("type")
    protected InstallationType type;

}
