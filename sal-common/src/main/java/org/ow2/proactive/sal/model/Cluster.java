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

    // JSON field constants
    public static final String JSON_CLUSTER_ID = "clusterId";

    public static final String JSON_NAME = "name";

    public static final String JSON_MASTER_NODE = "master-node";

    public static final String JSON_NODES = "nodes";

    public static final String JSON_STATUS = "status";

    public static final String JSON_ENV_VARS = "env-var-script";

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "CLUSTER_ID")
    @JsonProperty(JSON_CLUSTER_ID)
    private String clusterId = null;

    @Column(name = "NAME")
    @JsonProperty(JSON_NAME)
    private String name = null;

    @Column(name = "MASTER_NODE")
    @JsonProperty(JSON_MASTER_NODE)
    private String masterNode;

    @Column(name = "NODES")
    @JsonProperty(JSON_NODES)
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.REFRESH)
    private List<ClusterNodeDefinition> nodes;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    @JsonProperty(JSON_STATUS)
    private ClusterStatus status = ClusterStatus.DEFINED;

    @Column(name = "ENV", columnDefinition = "text", length = 65535)
    @JsonProperty(JSON_ENV_VARS)
    private String envVars;
}
