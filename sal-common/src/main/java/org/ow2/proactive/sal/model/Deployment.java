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
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.Optional;

import javax.persistence.*;
import javax.ws.rs.NotSupportedException;

import com.fasterxml.jackson.annotation.*;

import lombok.*;


@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "DEPLOYMENT")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "nodeName", scope = Deployment.class)
public class Deployment implements Serializable {

    @Id
    @Column(name = "NODE_NAME")
    private String nodeName;

    @OneToOne(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.REFRESH)
    private EmsDeploymentRequest emsDeployment;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("cloudId")
    private PACloud paCloud;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("taskId")
    private Task task;

    @Column(name = "IS_DEPLOYED")
    private Boolean isDeployed = false;

    @Column(name = "NODE_ACCESS_TOKEN")
    private String nodeAccessToken;

    @Column(name = "NUMBER")
    private Long number;

    @Column(name = "INSTANCE_ID")
    private String instanceId;

    @Embedded
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

    //    This is added for deserialization testing purpose
    public Deployment(String nodeName) {
        this.nodeName = nodeName;
    }

    //    This is added for deserialization testing purpose
    @JsonSetter("cloudId")
    private void setPaCloudById(String cloudId) {
        this.paCloud = new PACloud(cloudId);
        this.paCloud.addDeployment(this);
    }

    //    This is added for deserialization testing purpose
    @JsonSetter("taskId")
    private void setTaskById(String taskId) {
        this.task = new Task(taskId);
        this.task.addDeployment(this);
    }

    @Override
    public String toString() {
        switch (deploymentType) {
            case IAAS:
                return "Deployment{" + "nodeName='" + nodeName + '\'' + ", isDeployed='" + isDeployed.toString() +
                       '\'' + ", instanceId='" + instanceId + '\'' + ", ipAddress='" + ipAddress + '\'' +
                       ", nodeAccessToken='" + nodeAccessToken + '\'' + ", number='" + number + '\'' + ", paCloud='" +
                       Optional.ofNullable(paCloud).map(PACloud::getNodeSourceNamePrefix).orElse(null) + '\'' +
                       ", task='" + Optional.ofNullable(task).map(Task::getName).orElse(null) + '\'' + ", iaasNode='" +
                       iaasNode + '\'' + '}';
            case BYON:
                return "Deployment{" + "nodeName='" + nodeName + '\'' + ", isDeployed='" + isDeployed.toString() +
                       '\'' + ", instanceId='" + instanceId + '\'' + ", ipAddress='" + ipAddress + '\'' +
                       ", nodeAccessToken='" + nodeAccessToken + '\'' + ", number='" + number + '\'' + ", paCloud='" +
                       paCloud + '\'' + ", task='" + Optional.ofNullable(task).map(Task::getName).orElse(null) + '\'' +
                       ", byonNode='" + Optional.ofNullable(byonNode).map(ByonNode::getName).orElse(null) + '\'' + '}';

            case EDGE:
                return "Deployment{" + "nodeName='" + nodeName + '\'' + ", isDeployed='" + isDeployed.toString() +
                       '\'' + ", instanceId='" + instanceId + '\'' + ", ipAddress='" + ipAddress + '\'' +
                       ", nodeAccessToken='" + nodeAccessToken + '\'' + ", number='" + number + '\'' + ", paCloud='" +
                       paCloud + '\'' + ", task='" + Optional.ofNullable(task).map(Task::getName).orElse(null) + '\'' +
                       ", edgeNode='" + Optional.ofNullable(edgeNode).map(EdgeNode::getName).orElse(null) + '\'' + '}';
            default:
                return "Deployment{nodeName='" + nodeName + '}';
        }
    }
}
