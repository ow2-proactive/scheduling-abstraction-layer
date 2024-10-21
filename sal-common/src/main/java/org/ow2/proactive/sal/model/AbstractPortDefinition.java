/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = PortProvided.class, name = "PortProvided"),
                @JsonSubTypes.Type(value = PortRequired.class, name = "PortRequired") })
public abstract class AbstractPortDefinition implements PortDefinition {

    @JsonProperty("name")
    protected String name = null;

    @JsonProperty("type")
    protected PortDefinition.PortType type = null;

}
