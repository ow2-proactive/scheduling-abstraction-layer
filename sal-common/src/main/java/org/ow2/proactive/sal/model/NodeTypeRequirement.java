/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ow2.proactive.sal.util.ModelUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


/**
 * Subtype of Requirement
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@JsonTypeName(value = NodeTypeRequirement.CLASS_NAME)
public class NodeTypeRequirement extends Requirement {
    // Define class name constant for reuse
    public static final String CLASS_NAME = "NodeTypeRequirement";

    // JSON property constants
    public static final String JSON_NODE_TYPES = "nodeTypes";

    public static final String JSON_JOB_ID_FOR_BYON = "jobIdForBYON";

    public static final String JSON_JOB_ID_FOR_EDGE = "jobIdForEDGE";

    @JsonProperty(JSON_NODE_TYPES)
    private List<NodeType> nodeTypes;

    @JsonProperty(JSON_JOB_ID_FOR_BYON)
    private String jobIdForBYON;

    @JsonProperty(JSON_JOB_ID_FOR_EDGE)
    private String jobIdForEDGE;

    public NodeTypeRequirement() {
        this.type = RequirementType.NODE_TYPE;
    }

    public NodeTypeRequirement(List<NodeType> nodeTypes, String jobIdForBYON, String jobIdForEDGE) {
        this.type = RequirementType.NODE_TYPE;
        this.nodeTypes = nodeTypes;
        this.jobIdForBYON = jobIdForBYON;
        this.jobIdForEDGE = jobIdForEDGE;
    }

    /**
     * Custom toString() method for the NodeTypeRequirement class to format the output.
     * This method uses {@link ModelUtils#buildToString} to generate the string representation of the object.
     * @return A formatted string representation of the NodeTypeRequirement object.
     */
    @Override
    public String toString() {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put(JSON_NODE_TYPES, nodeTypes);
        fields.put(JSON_JOB_ID_FOR_BYON, jobIdForBYON);
        fields.put(JSON_JOB_ID_FOR_EDGE, jobIdForEDGE);

        return ModelUtils.buildToString(CLASS_NAME, fields);
    }

}
