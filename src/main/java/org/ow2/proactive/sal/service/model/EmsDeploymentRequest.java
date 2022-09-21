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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.ow2.proactive.sal.service.util.EntityManagerHelper;
import org.ow2.proactive.scheduler.common.task.TaskVariable;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Getter
@Setter
@Entity
@Table(name = "EMSDEPLOYMENTREQUEST")
public class EmsDeploymentRequest implements Serializable {

    public static void clean() {
        List<EmsDeploymentRequest> allEmsDeploymentRequests = EntityManagerHelper.createQuery("SELECT emsdr FROM EmsDeploymentRequest emsdr",
                                                                                              EmsDeploymentRequest.class)
                                                                                 .getResultList();
        allEmsDeploymentRequests.forEach(EntityManagerHelper::remove);
    }

    public enum TargetType {
        vm("IAAS"),
        container("PAAS"),
        edge("EDGE"),
        baremetal("BYON"),
        faas("FAAS");

        TargetType(String adapterVal) {
            this.adapterVal = adapterVal;
        }

        String adapterVal;

        public static TargetType fromValue(String text) {
            for (TargetType b : TargetType.values()) {
                if (String.valueOf(b.adapterVal).equals(text.toUpperCase(Locale.ROOT))) {
                    return b;
                }
            }
            return null;
        }

    }

    public enum TargetProvider {
        // Amazon Web Service Elastic Compute Cloud
        AWSEC2("aws-ec2"),
        // Azure VM
        AZUREVM("azure"),
        // Google CLoud Engine
        GCE("gce"),
        // OpenStack NOVA
        OPENSTACKNOVA("openstack"),
        // BYON, to be used for on-premise baremetal & Edge
        BYON("byon");

        TargetProvider(String upperwareVal) {
            this.upperwareValue = upperwareVal;
        }

        String upperwareValue;

        public static TargetProvider fromValue(String text) {
            for (TargetProvider b : TargetProvider.values()) {
                if (String.valueOf(b.upperwareValue).toUpperCase(Locale.ROOT).equals(text.toUpperCase(Locale.ROOT))) {
                    return b;
                }
            }
            return null;
        }
    }

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    @Column(name = "AUTHORIZATIONBEARER")
    private String authorizationBearer;

    @Column(name = "BAGUETTEIP")
    private String baguetteIp;

    @Column(name = "BAGUETTEPORT")
    private int baguette_port;

    @Column(name = "TARGETOS")
    @Enumerated(EnumType.STRING)
    private org.ow2.proactive.sal.service.model.OperatingSystemFamily targetOs;

    @Column(name = "TARGETTYPE")
    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    @Column(name = "TARGETNAME")
    private String targetName;

    @Column(name = "TARGETPROVIDER")
    @Enumerated(EnumType.STRING)
    private TargetProvider targetProvider;

    @Column(name = "LOCATION")
    private String location;

    @Column(name = "ISUSINGHTTP")
    private boolean isUsingHttps;

    @Column(name = "NODEID")
    private String nodeId;

    private EmsDeploymentRequest(String authorizationBearer, String baguetteIp, int baguette_port,
            org.ow2.proactive.sal.service.model.OperatingSystemFamily targetOs, TargetType targetType,
            String targetName, TargetProvider targetProvider, String location, boolean isUsingHttps, String id) {
        this.authorizationBearer = authorizationBearer;
        this.baguetteIp = baguetteIp;
        this.baguette_port = baguette_port;
        this.targetOs = targetOs;
        this.targetType = targetType;
        this.targetName = targetName;
        this.targetProvider = targetProvider;
        this.location = location;
        this.isUsingHttps = isUsingHttps;
        this.nodeId = id;
    }

    public EmsDeploymentRequest(String authorizationBearer, String baguetteIp, int baguette_port,
            org.ow2.proactive.sal.service.model.OperatingSystemFamily targetOs, String targetType, String targetName,
            TargetProvider targetProvider, String location, boolean isUsingHttps, String id) {
        this.authorizationBearer = authorizationBearer;
        this.baguetteIp = baguetteIp;
        this.baguette_port = baguette_port;
        this.targetOs = targetOs;
        this.targetType = TargetType.fromValue(targetType);
        this.targetName = targetName;
        this.targetProvider = targetProvider;
        this.location = location;
        this.isUsingHttps = isUsingHttps;
        this.nodeId = id;
    }

    /**
     * Provide the variable Array to be used in the EMS deployment workflow, structured to be ysed with the submit PA API
     * @return The structured map.
     */
    public Map<String, TaskVariable> getWorkflowMap() {
        Map<String, TaskVariable> result = new HashMap<>();
        result.put("authorization_bearer",
                   new TaskVariable("authorization_bearer", this.authorizationBearer, "", false));
        result.put("baguette_ip", new TaskVariable("baguette_ip", baguetteIp.toString(), "", false));
        result.put("baguette_port", new TaskVariable("baguette_port", String.valueOf(baguette_port), "", false));
        result.put("target_operating_system", new TaskVariable("target_operating_system", targetOs.name(), "", false));
        result.put("target_type", new TaskVariable("target_type", targetType.name(), "", false));
        result.put("target_name", new TaskVariable("target_name", targetName, "", false));
        result.put("target_provider", new TaskVariable("target_provider", targetProvider.name(), "", false));
        result.put("location", new TaskVariable("location", location, "", false));
        result.put("using_https", new TaskVariable("using_https", isUsingHttps + "", "PA:Boolean", false));
        result.put("id", new TaskVariable("id", nodeId, "", false));
        return result;
    }

    public EmsDeploymentRequest clone(String nodeId) {
        EmsDeploymentRequest req = new EmsDeploymentRequest(authorizationBearer,
                                                            baguetteIp,
                                                            baguette_port,
                                                            targetOs,
                                                            targetType,
                                                            targetName,
                                                            targetProvider,
                                                            location,
                                                            isUsingHttps,
                                                            nodeId);
        return req;
    }
}
