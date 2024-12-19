/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class ClusterDefinition {

    // JSON field constants
    public static final String JSON_NAME = "name";

    public static final String JSON_NODES = "nodes";

    public static final String JSON_MASTER_NODE = "master-node";

    public static final String JSON_ENV_VAR = "env-var";

    @JsonProperty(JSON_NAME)
    private String name = null;

    @JsonProperty(JSON_NODES)
    private List<ClusterNodeDefinition> nodes;

    @JsonProperty(JSON_MASTER_NODE)
    private String masterNode;

    @JsonProperty(JSON_ENV_VAR)
    private Map<String, String> envVars;

}
