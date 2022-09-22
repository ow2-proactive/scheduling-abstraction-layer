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

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import org.ow2.proactive.sal.service.util.EntityManagerHelper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "DEPLOYMENT")
public class Deployment implements Serializable {

    @Id
    @Column(name = "NODE_NAME")
    private String nodeName;

    @Column(name = "LOCATION_NAME")
    private String locationName;

    @Column(name = "IMAGE_PROVIDER_ID")
    private String imageProviderId;

    @Column(name = "HARDWARE_PROVIDER_ID")
    private String hardwareProviderId;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    private EmsDeploymentRequest emsDeployment;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    private PACloud paCloud;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
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

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    private ByonNode byonNode;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    private EdgeNode edgeNode;

    public static void clean() {
        List<Deployment> allDeployments = EntityManagerHelper.createQuery("SELECT d FROM Deployment d",
                                                                          Deployment.class)
                                                             .getResultList();
        allDeployments.forEach(EntityManagerHelper::remove);
    }

    @Override
    public String toString() {
        switch (deploymentType) {
            case IAAS:
                return "Deployment{" + "nodeName='" + nodeName + '\'' + ", locationName='" + locationName + '\'' +
                       ", imageProviderId='" + imageProviderId + '\'' + ", hardwareProviderId='" + hardwareProviderId +
                       '\'' + ", isDeployed='" + isDeployed.toString() + '\'' + ", instanceId='" + instanceId + '\'' +
                       ", ipAddress='" + ipAddress + '\'' + ", nodeAccessToken='" + nodeAccessToken + '\'' +
                       ", number='" + number + '\'' + ", paCloud='" + paCloud.getNodeSourceNamePrefix() + '\'' +
                       ", task='" + task.getName() + '\'' + ", byonNode='" + byonNode + '\'' + '}';
            case BYON:
                return "Deployment{" + "nodeName='" + nodeName + '\'' + ", locationName='" + locationName + '\'' +
                       ", imageProviderId='" + imageProviderId + '\'' + ", hardwareProviderId='" + hardwareProviderId +
                       '\'' + ", isDeployed='" + isDeployed.toString() + '\'' + ", instanceId='" + instanceId + '\'' +
                       ", ipAddress='" + ipAddress + '\'' + ", nodeAccessToken='" + nodeAccessToken + '\'' +
                       ", number='" + number + '\'' + ", paCloud='" + paCloud + '\'' + ", task='" + task.getName() +
                       '\'' + ", byonNode='" + byonNode.getName() + '\'' + '}';

            case EDGE:
                return "Deployment{" + "nodeName='" + nodeName + '\'' + ", locationName='" + locationName + '\'' +
                       ", imageProviderId='" + imageProviderId + '\'' + ", hardwareProviderId='" + hardwareProviderId +
                       '\'' + ", isDeployed='" + isDeployed.toString() + '\'' + ", instanceId='" + instanceId + '\'' +
                       ", ipAddress='" + ipAddress + '\'' + ", nodeAccessToken='" + nodeAccessToken + '\'' +
                       ", number='" + number + '\'' + ", paCloud='" + paCloud + '\'' + ", task='" + task.getName() +
                       '\'' + ", edgeNode='" + edgeNode.getName() + '\'' + '}';
            default:
                return "Deployment{nodeName='" + nodeName + '}';
        }
    }
}
