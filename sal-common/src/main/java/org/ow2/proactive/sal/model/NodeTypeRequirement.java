/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Getter;
import lombok.Setter;


/**
 * Subtype of Requirement
 */
@Getter
@Setter
@JsonTypeName(value = "NodeTypeRequirement")
public class NodeTypeRequirement extends Requirement {
    @JsonProperty("nodeTypes")
    private List<NodeType> nodeTypes;

    @JsonProperty("jobIdForByon")
    private String jobIdForBYON;

    @JsonProperty("jobIdForEDGE")
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
     * Get nodeType
     * @return nodeType
     **/

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NodeTypeRequirement nodeTypeRequirement = (NodeTypeRequirement) o;
        return Objects.equals(this.nodeTypes, nodeTypeRequirement.nodeTypes) &&
               Objects.equals(this.jobIdForBYON, nodeTypeRequirement.jobIdForBYON) &&
               Objects.equals(this.jobIdForEDGE, nodeTypeRequirement.jobIdForEDGE) && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeTypes, jobIdForBYON, jobIdForEDGE, super.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class NodeTypeRequirement {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    nodeType: ").append(toIndentedString(nodeTypes)).append("\n");
        sb.append("    jobIdForBYON: ").append(toIndentedString(jobIdForBYON)).append("\n");
        sb.append("    jobIdForEDGE: ").append(toIndentedString(jobIdForEDGE)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
