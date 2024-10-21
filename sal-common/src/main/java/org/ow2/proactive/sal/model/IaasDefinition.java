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

    @JsonProperty("nodeName")
    private String name = null;

    @JsonProperty("taskName")
    private String taskName = null;

    @JsonProperty("nodeCandidateId")
    private String nodeCandidateId = null;

    @JsonProperty("cloudId")
    private String cloudId = null;
}
