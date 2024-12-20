/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.ow2.proactive.sal.util.ModelUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true) // Includes `id` from AbstractNode
@Table(name = "IAAS_NODE")
public class IaasNode extends AbstractNode {

    // JSON field constants
    public static final String JSON_NUM_DEPLOYMENTS = "numDeployments";

    @Column(name = "NUM_DEPLOYMENTS")
    @JsonProperty(JSON_NUM_DEPLOYMENTS)
    private Long numDeployments = 0L;

    /**
     * Increment the number of deployed nodes.
     *
     * @param number The number to increment.
     */
    public void incDeployedNodes(Long number) {
        numDeployments = numDeployments + number;
    }

    /**
     * Decrement the number of deployed nodes, ensuring it doesn't drop below 0.
     *
     * @param number The number to decrement.
     */
    public void decDeployedNodes(Long number) {
        numDeployments = numDeployments > number ? numDeployments - number : 0L;
    }

    /**
     * Constructor to create an IaasNode from a NodeCandidate.
     *
     * @param nodeCandidate The node candidate to initialize from.
     */
    public IaasNode(NodeCandidate nodeCandidate) {
        this.nodeCandidate = nodeCandidate;
        nodeCandidate.setNodeId(this.id);
    }

    @Override
    public String toString() {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put(AbstractNode.JSON_ID, id);
        fields.put(AbstractNode.JSON_NODE_CANDIDATE, nodeCandidate);
        fields.put(JSON_NUM_DEPLOYMENTS, numDeployments);
        return ModelUtils.buildToString(getClass().getSimpleName(), fields);
    }
}
