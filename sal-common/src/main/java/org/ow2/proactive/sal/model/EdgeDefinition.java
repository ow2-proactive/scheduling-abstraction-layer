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
 * Attributes defining a EDGE node
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class EdgeDefinition {
    // Default constants
    public static final String DEFAULT_PORT = "22";

    @JsonProperty(EdgeNode.JSON_NAME)
    private String name = null;

    @JsonProperty(EdgeNode.JSON_JOB_ID)
    private String jobId = null;

    @JsonProperty(EdgeNode.JSON_SYSTEM_ARCH)
    private String systemArch = null;

    @JsonProperty(EdgeNode.JSON_SCRIPT_URL)
    private String scriptURL = null;

    @JsonProperty(EdgeNode.JSON_JAR_URL)
    private String jarURL = null;

    @JsonProperty(EdgeNode.JSON_LOGIN_CREDENTIAL)
    private LoginCredential loginCredential = null;

    @JsonProperty(EdgeNode.JSON_IP_ADDRESSES)
    private List<IpAddress> ipAddresses = null;

    @JsonProperty(EdgeNode.JSON_PORT)
    private String port = DEFAULT_PORT;

    @JsonProperty(EdgeNode.JSON_NODE_PROPERTIES)
    private NodeProperties nodeProperties = null;
}
