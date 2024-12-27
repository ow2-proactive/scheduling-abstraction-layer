/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Represents the scope used to for a cloud
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class Scope implements Serializable {

    // JSON field constants
    public static final String JSON_PREFIX = "prefix";

    public static final String JSON_VALUE = "value";

    @JsonProperty(JSON_PREFIX)
    private String prefix = null;

    @JsonProperty(JSON_VALUE)
    private String value = null;
}
