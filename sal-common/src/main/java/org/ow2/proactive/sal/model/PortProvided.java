/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.*;


/**
 * Attributes defining a provided port`
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
@JsonTypeName(value = "PortProvided")
public class PortProvided extends AbstractPortDefinition {

    @JsonProperty("port")
    private Integer port;

    @Override
    public PortType getType() {
        return PortType.PROVIDED;
    }
}
