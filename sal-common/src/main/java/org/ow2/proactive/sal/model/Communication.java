/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;


/**
 * Attributes defining a Job Communication`
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class Communication implements Serializable {

    // JSON field constants
    public static final String JSON_PORT_PROVIDED = "portProvided";

    public static final String JSON_PORT_REQUIRED = "portRequired";

    @JsonProperty(JSON_PORT_PROVIDED)
    private String portProvided;

    @JsonProperty(JSON_PORT_REQUIRED)
    private String portRequired;
}
