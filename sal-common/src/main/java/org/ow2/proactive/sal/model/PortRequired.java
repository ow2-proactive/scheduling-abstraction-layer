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
 * Attributes defining a required port`
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
@JsonTypeName(value = "PortRequired")
public class PortRequired extends AbstractPortDefinition {

    @JsonProperty("isMandatory")
    private boolean isMandatory;

    @Override
    public PortType getType() {
        return PortType.REQUIRED;
    }
}
