/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;


/**
 * Attributes defining an EMS request
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class EmsDeploymentDefinition implements Serializable {

    // Expose JSON field names as constants
    public static final String JSON_NODE_NAMES = "nodeNames";

    public static final String JSON_AUTHORIZATION_BEARER = "authorizationBearer";

    public static final String JSON_IS_PRIVATE_IP = "isPrivateIp";

    @JsonProperty(JSON_NODE_NAMES)
    private List<String> nodeNames;

    //TODO: This should be refactored to extend EmsDeploymentDefinitionForNode class (After Morphemic)
    @JsonProperty(JSON_AUTHORIZATION_BEARER)
    private String authorizationBearer;

    @JsonProperty(JSON_IS_PRIVATE_IP)
    private boolean isPrivateIP;
}
