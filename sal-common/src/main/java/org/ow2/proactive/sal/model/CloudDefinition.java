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
 * Attributes defining a Cloud`
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class CloudDefinition implements Serializable {

    @JsonProperty("cloudId")
    private String cloudId = null;

    // @JsonProperty("cloudProviderName")
    // private String cloudProviderName = null;

    @JsonProperty("cloudProvider")
    private CloudProviderType cloudProvider = null;

    @JsonProperty("cloudType")
    private CloudType cloudType = null;

    @JsonProperty("securityGroup")
    private String securityGroup = null;

    @JsonProperty("subnet")
    private String subnet = null;

    @Embedded
    @JsonProperty("sshCredentials")
    private SSHCredentials sshCredentials = null;

    @JsonProperty("endpoint")
    private String endpoint = null;

    @Embedded
    @JsonProperty("scope")
    private Scope scope = null;

    @JsonProperty("identityVersion")
    private String identityVersion;

    @JsonProperty("defaultNetwork")
    private String defaultNetwork;

    @JsonProperty("credentials")
    private Credential credentials;

    @JsonProperty("blacklist")
    private String blacklist;
}
