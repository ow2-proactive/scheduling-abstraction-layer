/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "IAAS_NODE")
public class IaasNode extends AbstractNode {

    @Column(name = "NUM_DEPLOYMENTS")
    @JsonProperty("numDeployments")
    private Long numDeployments = 0L;

    public void incDeployedNodes(Long number) {
        numDeployments = numDeployments + number;
    }

    public void decDeployedNodes(Long number) {
        numDeployments = numDeployments > number ? numDeployments - number : 0L;
    }

    public IaasNode(NodeCandidate nodeCandidate) {
        this.nodeCandidate = nodeCandidate;
        nodeCandidate.setNodeId(this.id);
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IaasNode iaasNode = (IaasNode) o;
        return Objects.equals(this.id, iaasNode.id) && Objects.equals(this.nodeCandidate, iaasNode.nodeCandidate) &&
               Objects.equals(this.numDeployments, iaasNode.numDeployments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nodeCandidate, numDeployments);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class IaasNode {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    nodeCandidate: ").append(toIndentedString(nodeCandidate)).append("\n");
        sb.append("    numDeployments: ").append(toIndentedString(numDeployments)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
