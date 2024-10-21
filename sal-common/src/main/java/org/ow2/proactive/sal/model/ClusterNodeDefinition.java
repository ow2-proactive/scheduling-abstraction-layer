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
    @Id
    @Column(name = "NAME")
    @JsonProperty("nodeName")
    private String name = null;

    @Column(name = "NC")
    @JsonProperty("nodeCandidateId")
    private String nodeCandidateId = null;

    @Column(name = "CLOUD_ID")
    @JsonProperty("cloudId")
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
