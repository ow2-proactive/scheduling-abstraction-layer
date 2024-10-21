/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CLUSTER")
public class Cluster {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "CLUSTER_ID")
    private String clusterId = null;

    @Column(name = "NAME")
    @JsonProperty("name")
    private String name = null;

    @Column(name = "MASTER_NODE")
    @JsonProperty("master-node")
    private String masterNode;

    @Column(name = "NODES")
    @JsonProperty("nodes")
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.REFRESH)
    private List<ClusterNodeDefinition> nodes;

    // TODO: Change this into Enum
    @Column(name = "STATUS")
    @JsonProperty("status")
    private String status = "defined";

    @Column(name = "ENV", columnDefinition = "text", length = 65535)
    @JsonProperty("env-var-script")
    private String envVars;
}
