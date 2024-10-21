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

    @JsonProperty("communications")
    private List<Communication> communications;

    @JsonProperty("unchangedTasks")
    private List<String> unchangedTasks;

    @JsonProperty("deleteTasks")
    private List<String> deletedTasks;

    @JsonProperty("addTasks")
    private List<TaskReconfigurationDefinition> addedTasks;
}
