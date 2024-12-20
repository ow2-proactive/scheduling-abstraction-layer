/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;


/**
 * Attributes defining a IAAS node
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class IaasDefinition {

    public static final String JSON_NODE_NAME = "nodeName";

    public static final String JSON_TASK_NAME = "taskName";

    public static final String JSON_NODE_CANDIDATE_ID = "nodeCandidateId";

    public static final String JSON_CLOUD_ID = "cloudId";

    @JsonProperty(JSON_NODE_NAME)
    private String name = null;

    @JsonProperty(JSON_TASK_NAME)
    private String taskName = null;

    @JsonProperty(JSON_NODE_CANDIDATE_ID)
    private String nodeCandidateId = null;

    @JsonProperty(JSON_CLOUD_ID)
    private String cloudId = null;
}
