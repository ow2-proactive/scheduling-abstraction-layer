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
 * Attributes defining a Job Reconfiguration Plan
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReconfigurationJobDefinition implements Serializable {

    // JSON field constants
    public static final String JSON_COMMUNICATIONS = "communications";

    public static final String JSON_UNCHANGED_TASKS = "unchangedTasks";

    public static final String JSON_DELETE_TASKS = "deleteTasks";

    public static final String JSON_ADD_TASKS = "addTasks";

    @JsonProperty(JSON_COMMUNICATIONS)
    private List<Communication> communications;

    @JsonProperty(JSON_UNCHANGED_TASKS)
    private List<String> unchangedTasks;

    @JsonProperty(JSON_DELETE_TASKS)
    private List<String> deletedTasks;

    @JsonProperty(JSON_ADD_TASKS)
    private List<TaskReconfigurationDefinition> addedTasks;
}
