/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.Optional;

import javax.persistence.*;
import javax.ws.rs.NotSupportedException;

import com.fasterxml.jackson.annotation.*;

import lombok.*;


/**
 * Represents a Deployment with its associated details.
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "DEPLOYMENT")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = Deployment.JSON_NODE_NAME, scope = Deployment.class)
public class Deployment implements Serializable {

    // JSON field constants
    public static final String JSON_NODE_NAME = "nodeName";

    public static final String JSON_CLOUD_ID = "cloudId";

    public static final String JSON_TASK_ID = "taskId";

    public static final String JSON_IS_DEPLOYED = "isDeployed";

    public static final String JSON_INSTANCE_ID = "instanceId";

    public static final String JSON_IP_ADDRESS = "ipAddress";

    @Id
    @Column(name = "NODE_NAME")
    @JsonProperty(JSON_NODE_NAME)
    private String nodeName;

    @OneToOne(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.REFRESH)
    private EmsDeploymentRequest emsDeployment;

    @Column(name = "WORKER")
    private Boolean worker = null;

    @Column(name = "MASTER_TOKEN")
    private String masterToken = "";

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty(JSON_CLOUD_ID)
    private PACloud paCloud;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty(JSON_TASK_ID)
    private Task task;

    @Column(name = "IS_DEPLOYED")
    @JsonProperty(JSON_IS_DEPLOYED)
    private Boolean isDeployed = false;

    @Column(name = "NODE_ACCESS_TOKEN")
    private String nodeAccessToken;

    @Column(name = "NUMBER")
    private Long number;

    @Column(name = "INSTANCE_ID")
    @JsonProperty(JSON_INSTANCE_ID)
    private String instanceId;

    @Embedded
    @JsonProperty(JSON_IP_ADDRESS)
    private IpAddress ipAddress = null;

    @Column(name = "NODE_TYPE")
    @Enumerated(EnumType.STRING)
    private NodeType deploymentType;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    private IaasNode iaasNode;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    private ByonNode byonNode;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    private EdgeNode edgeNode;

    @JsonIgnore
    public Node getNode() {
        switch (deploymentType) {
            case IAAS:
                return getIaasNode();
            case BYON:
                return getByonNode();
            case EDGE:
                return getEdgeNode();
            default:
                throw new NotSupportedException(String.format("Deployment type [%s] not supported yet.",
                                                              deploymentType));
        }
    }

    // Added for deserialization testing purpose
    public Deployment(String nodeName) {
        this.nodeName = nodeName;
    }

    // Added for deserialization testing purpose
    @JsonSetter(JSON_CLOUD_ID)
    private void setPaCloudById(String cloudId) {
        this.paCloud = new PACloud(cloudId);
        this.paCloud.addDeployment(this);
    }

    // Added for deserialization testing purpose
    @JsonSetter(JSON_TASK_ID)
    private void setTaskById(String taskId) {
        this.task = new Task(taskId);
        this.task.addDeployment(this);
    }

    @Override
    public String toString() {
        String commonFields = JSON_NODE_NAME + "='" + nodeName + "', " + JSON_IS_DEPLOYED + "='" + isDeployed + "', " +
                              JSON_INSTANCE_ID + "='" + instanceId + "', " + JSON_IP_ADDRESS + "='" + ipAddress +
                              "', " + "nodeAccessToken='" + nodeAccessToken + "', " + "number='" + number + "', " +
                              JSON_CLOUD_ID + "='" +
                              Optional.ofNullable(paCloud).map(PACloud::getNodeSourceNamePrefix).orElse(null) + "', " +
                              JSON_TASK_ID + "='" + Optional.ofNullable(task).map(Task::getName).orElse(null) + "'";

        switch (deploymentType) {
            case IAAS:
                return "Deployment{" + commonFields + ", iaasNode='" + iaasNode + "'}";
            case BYON:
                return "Deployment{" + commonFields + ", byonNode='" +
                       Optional.ofNullable(byonNode).map(ByonNode::getName).orElse(null) + "'}";
            case EDGE:
                return "Deployment{" + commonFields + ", edgeNode='" +
                       Optional.ofNullable(edgeNode).map(EdgeNode::getName).orElse(null) + "'}";
            default:
                return "Deployment{" + JSON_NODE_NAME + "='" + nodeName + "'}";
        }
    }
}
