/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import lombok.experimental.Accessors;


/**
 * Attributes defining an EDGE node
 */
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode
@Getter
@Setter
@ToString(callSuper = true)
public class EdgeDefinition {

    public static final String DEFAULT_PORT = "22";

    public static final String ANY_JOB_ID = "any";

    public static final String JSON_NAME = "name";

    public static final String JSON_LOGIN_CREDENTIAL = "loginCredential";

    public static final String JSON_IP_ADDRESSES = "ipAddresses";

    public static final String JSON_NODE_PROPERTIES = "nodeProperties";

    public static final String JSON_PORT = "port";

    public static final String JSON_REASON = "reason";

    public static final String JSON_DIAGNOSTIC = "diagnostic";

    public static final String JSON_USER_ID = "userId";

    public static final String JSON_ALLOCATED = "allocated";

    // edge jobID corresponds to the ProActive job name
    public static final String JSON_JOB_ID = "jobId";

    public static final String JSON_SYSTEM_ARCH = "systemArch";

    public static final String JSON_SCRIPT_URL = "scriptURL";

    public static final String JSON_JAR_URL = "jarURL";

    @JsonProperty(JSON_NAME)
    private String name = null;

    @JsonProperty(JSON_JOB_ID)
    private String jobId = null;

    @JsonProperty(JSON_SYSTEM_ARCH)
    private String systemArch = null;

    @JsonProperty(JSON_SCRIPT_URL)
    private String scriptURL = null;

    @JsonProperty(JSON_JAR_URL)
    private String jarURL = null;

    @JsonProperty(JSON_LOGIN_CREDENTIAL)
    private LoginCredential loginCredential = null;

    @JsonProperty(JSON_IP_ADDRESSES)
    private List<IpAddress> ipAddresses = null;

    @JsonProperty(JSON_PORT)
    private String port = DEFAULT_PORT;

    @JsonProperty(JSON_NODE_PROPERTIES)
    private NodeProperties nodeProperties = null;
}
