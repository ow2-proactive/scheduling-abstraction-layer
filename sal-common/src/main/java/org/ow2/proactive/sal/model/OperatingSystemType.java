/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Getter
@Setter
@Embeddable
public class OperatingSystemType implements Serializable {
    // JSON property names as constants
    public static final String JSON_OPERATING_SYSTEM_FAMILY = "operatingSystemFamily";

    public static final String JSON_OPERATING_SYSTEM_VERSION = "operatingSystemVersion";

    @Column(name = "OPERATING_SYSTEM_FAMILY")
    @JsonProperty(JSON_OPERATING_SYSTEM_FAMILY)
    private String operatingSystemFamily;

    @Column(name = "OPERATING_SYSTEM_VERSION")
    @JsonProperty(JSON_OPERATING_SYSTEM_VERSION)
    private float operatingSystemVersion;
}
