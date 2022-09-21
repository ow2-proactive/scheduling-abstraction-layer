/*
 * ProActive Parallel Suite(TM):
 * The Open Source library for parallel and distributed
 * Workflows & Scheduling, Orchestration, Cloud Automation
 * and Big Data Analysis on Enterprise Grids & Clouds.
 *
 * Copyright (c) 2007 - 2017 ActiveEon
 * Contact: contact@activeeon.com
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation: version 3 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 */
package org.ow2.proactive.sal.service.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;


/**
 * Subtype of Requirement
 */
@Getter
@Setter
public class NodeTypeRequirement extends org.ow2.proactive.sal.service.model.Requirement {
    @JsonProperty("nodeType")
    private List<org.ow2.proactive.sal.service.model.NodeType> nodeTypes;

    @JsonProperty("jobIdForByon")
    private String jobIdForBYON;

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
               Objects.equals(this.jobIdForBYON, nodeTypeRequirement.jobIdForBYON) && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeTypes, jobIdForBYON, super.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class NodeTypeRequirement {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    nodeType: ").append(toIndentedString(nodeTypes)).append("\n");
        sb.append("    jobIdForBYON: ").append(toIndentedString(jobIdForBYON)).append("\n");
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
