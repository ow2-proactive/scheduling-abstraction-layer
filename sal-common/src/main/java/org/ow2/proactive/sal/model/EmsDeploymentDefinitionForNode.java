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
 * Attributes defining an EMS request for a specific node
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class EmsDeploymentDefinitionForNode implements Serializable {

    public static final String JSON_AUTHORIZATION_BEARER = "authorizationBearer";

    public static final String JSON_IS_PRIVATE_IP = "isPrivateIp";

    @JsonProperty(JSON_AUTHORIZATION_BEARER)
    private String authorizationBearer;

    @JsonProperty(JSON_IS_PRIVATE_IP)
    private boolean isPrivateIP;

}
