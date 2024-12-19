/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "CLUSTER_NODE_DEF")
public class ClusterNodeDefinition {

    // JSON field constants
    public static final String JSON_NODE_NAME = "nodeName";

    public static final String JSON_NODE_CANDIDATE_ID = "nodeCandidateId";

    public static final String JSON_CLOUD_ID = "cloudId";

    @Id
    @Column(name = "NAME")
    @JsonProperty(JSON_NODE_NAME)
    private String name = null;

    @Column(name = "NC")
    @JsonProperty(JSON_NODE_CANDIDATE_ID)
    private String nodeCandidateId = null;

    @Column(name = "CLOUD_ID")
    @JsonProperty(JSON_CLOUD_ID)
    private String cloudId = null;

    private String state = "";

    private String nodeUrl = "";

    public String getNodeJobName(String clusterName) {
        return this.name + "-" + clusterName;
    }

    public String getNodeTaskName(String clusterName) {
        return this.name + "-" + clusterName + "_Task";
    }
}
