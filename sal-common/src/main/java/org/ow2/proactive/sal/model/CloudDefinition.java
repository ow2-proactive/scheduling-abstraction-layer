/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;

import javax.persistence.Embedded;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;


/**
 * Attributes defining a Cloud
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class CloudDefinition implements Serializable {

    // JSON field constants
    public static final String JSON_CLOUD_ID = "cloudId";

    public static final String JSON_CLOUD_PROVIDER_NAME = "cloudProviderName";

    public static final String JSON_CLOUD_TYPE = "cloudType";

    public static final String JSON_SECURITY_GROUP = "securityGroup";

    public static final String JSON_SUBNET = "subnet";

    public static final String JSON_SSH_CREDENTIALS = "sshCredentials";

    public static final String JSON_ENDPOINT = "endpoint";

    public static final String JSON_SCOPE = "scope";

    public static final String JSON_IDENTITY_VERSION = "identityVersion";

    public static final String JSON_DEFAULT_NETWORK = "defaultNetwork";

    public static final String JSON_CREDENTIALS = "credentials";

    public static final String JSON_BLACKLIST = "blacklist";

    @JsonProperty(JSON_CLOUD_ID)
    private String cloudId = null;

    @JsonProperty(JSON_CLOUD_PROVIDER_NAME)
    private CloudProviderType cloudProvider = null;

    @JsonProperty(JSON_CLOUD_TYPE)
    private CloudType cloudType = null;

    @JsonProperty(JSON_SECURITY_GROUP)
    private String securityGroup = null;

    @JsonProperty(JSON_SUBNET)
    private String subnet = null;

    @Embedded
    @JsonProperty(JSON_SSH_CREDENTIALS)
    private SSHCredentials sshCredentials = null;

    @JsonProperty(JSON_ENDPOINT)
    private String endpoint = null;

    @Embedded
    @JsonProperty(JSON_SCOPE)
    private Scope scope = null;

    @JsonProperty(JSON_IDENTITY_VERSION)
    private String identityVersion;

    @JsonProperty(JSON_DEFAULT_NETWORK)
    private String defaultNetwork;

    @JsonProperty(JSON_CREDENTIALS)
    private Credential credentials;

    @JsonProperty(JSON_BLACKLIST)
    private String blacklist;
}
