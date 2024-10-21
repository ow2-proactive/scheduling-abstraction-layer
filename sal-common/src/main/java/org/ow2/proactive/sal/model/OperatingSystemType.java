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
    @Column(name = "OPERATING_SYSTEM_FAMILY")
    @JsonProperty("operatingSystemFamily")
    private String operatingSystemFamily;

    @Column(name = "OPERATING_SYSTEM_VERSION")
    @JsonProperty("operatingSystemVersion")
    private float operatingSystemVersion;
}
