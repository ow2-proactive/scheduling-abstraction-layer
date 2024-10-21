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
    @JsonProperty("name")
    private String name = null;

    @JsonProperty("nodes")
    private List<ClusterNodeDefinition> nodes;

    @JsonProperty("master-node")
    private String masterNode;

    @JsonProperty("env-var")
    private Map<String, String> envVars;

}
