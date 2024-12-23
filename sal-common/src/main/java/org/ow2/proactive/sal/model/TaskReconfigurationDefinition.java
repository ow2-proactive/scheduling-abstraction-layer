/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskReconfigurationDefinition implements Serializable {

    // JSON field constants
    public static final String JSON_TASK = "task";

    public static final String JSON_IAAS_NODE_SELECTION = "iaasNodeSelection";

    public static final String JSON_EMS_DEPLOYMENT_DEFINITION = "emsDeploymentDefinition";

    @JsonProperty(JSON_TASK)
    private TaskDefinition task;

    @JsonProperty(JSON_IAAS_NODE_SELECTION)
    private IaasDefinition iaasNodeSelection;

    @JsonProperty(JSON_EMS_DEPLOYMENT_DEFINITION)
    private EmsDeploymentDefinitionForNode emsDeploymentDefinition;

}
