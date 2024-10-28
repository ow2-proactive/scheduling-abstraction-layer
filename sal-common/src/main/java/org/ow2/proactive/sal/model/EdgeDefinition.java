/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;


/**
 * Attributes defining a BYON node
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class EdgeDefinition {
    @JsonProperty("name")
    private String name = null;

    @JsonProperty("jobId")
    private String jobId = null;

    @JsonProperty("systemArch")
    private String systemArch = null;

    @JsonProperty("scriptURL")
    private String scriptURL = null;

    @JsonProperty("jarURL")
    private String jarURL = null;

    @JsonProperty("loginCredential")
    private LoginCredential loginCredential = null;

    @JsonProperty("ipAddresses")
    private List<IpAddress> ipAddresses = null;

    @JsonProperty("port")
    private String port = "22";

    @JsonProperty("nodeProperties")
    private NodeProperties nodeProperties = null;
}
