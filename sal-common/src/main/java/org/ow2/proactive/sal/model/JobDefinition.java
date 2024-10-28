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
 * Attributes defining a Job`
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class JobDefinition implements Serializable {

    @JsonProperty("communications")
    private List<Communication> communications;

    @JsonProperty("jobInformation")
    private JobInformation jobInformation;

    @JsonProperty("tasks")
    private List<TaskDefinition> tasks;
}
