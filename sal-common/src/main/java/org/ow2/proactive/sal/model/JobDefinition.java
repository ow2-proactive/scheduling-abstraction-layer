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
 * Attributes defining a Job
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class JobDefinition implements Serializable {

    // JSON field constants
    public static final String JSON_COMMUNICATIONS = "communications";
    public static final String JSON_JOB_INFORMATION = "jobInformation";
    public static final String JSON_TASKS = "tasks";

    @JsonProperty(JSON_COMMUNICATIONS)
    private List<Communication> communications;

    @JsonProperty(JSON_JOB_INFORMATION)
    private JobInformation jobInformation;

    @JsonProperty(JSON_TASKS)
    private List<TaskDefinition> tasks;
}
