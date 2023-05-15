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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.ow2.proactive.scheduler.common.task.TaskVariable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Getter
@Setter
@Entity
@Table(name = "EMSDEPLOYMENTREQUEST")
public class EmsDeploymentRequest implements Serializable {

    public enum TargetType {
        VM("IAAS"),
        CONTAINER("PAAS"),
        EDGE("EDGE"),
        BAREMETAL("BYON"),
        FAAS("FAAS");

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
        // BYON, to be used for on-premise baremetal
        BYON("byon"),
        // EDGE nodes
        EDGE("edge");

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
    private int baguettePort;

    @Column(name = "TARGETTYPE")
    @Enumerated(EnumType.STRING)
    private TargetType targetType;
    
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    private NodeCandidate targetNodeCandidate;

    @Column(name = "TARGETNAME")
    private String targetName;

    @Column(name = "TARGETPROVIDER")
    @Enumerated(EnumType.STRING)
    private TargetProvider targetProvider;

    @Column(name = "TARGETOPENPORTS")
    private String targetOpenPorts;

    @Column(name = "ISUSINGHTTP")
    private boolean isUsingHttps;

    @Column(name = "ISPRIVATEIP")
    private boolean isPrivateIP;

    @Column(name = "NODEID")
    private String nodeId;

    private EmsDeploymentRequest(String authorizationBearer, String baguetteIp, int baguettePort, TargetType targetType,
            NodeCandidate targetNodeCandidate, String targetName, TargetProvider targetProvider, String targetOpenPorts,
            boolean isUsingHttps, boolean isPrivateIP, String id) {
        this.authorizationBearer = authorizationBearer;
        this.baguetteIp = baguetteIp;
        this.baguettePort = baguettePort;
        this.targetType = targetType;
        this.targetNodeCandidate = targetNodeCandidate;
        this.targetName = targetName;
        this.targetProvider = targetProvider;
        this.targetOpenPorts = targetOpenPorts;
        this.isUsingHttps = isUsingHttps;
        this.isPrivateIP = isPrivateIP;
        this.nodeId = id;
    }

    public EmsDeploymentRequest(String authorizationBearer, String baguetteIp, int baguettePort, String targetType,
            NodeCandidate targetNodeCandidate, String targetName, TargetProvider targetProvider, String targetOpenPorts,
            boolean isUsingHttps, boolean isPrivateIP, String id) {
        this.authorizationBearer = authorizationBearer;
        this.baguetteIp = baguetteIp;
        this.baguettePort = baguettePort;
        this.targetType = TargetType.fromValue(targetType);
        this.targetNodeCandidate = targetNodeCandidate;
        this.targetName = targetName;
        this.targetProvider = targetProvider;
        this.targetOpenPorts = targetOpenPorts;
        this.isUsingHttps = isUsingHttps;
        this.isPrivateIP = isPrivateIP;
        this.nodeId = id;
    }

    /**
     * Provide the variable Array to be used in the EMS deployment workflow, structured to be ysed with the submit PA API
     * @return The structured map.
     */
    @JsonIgnore
    public Map<String, TaskVariable> getWorkflowMap() {
        Map<String, TaskVariable> result = new HashMap<>();
        result.put("authorization_bearer",
                   new TaskVariable("authorization_bearer", this.authorizationBearer, "", false));
        result.put("baguette_ip", new TaskVariable("baguette_ip", baguetteIp, "", false));
        result.put("baguette_port", new TaskVariable("baguette_port", String.valueOf(baguettePort), "", false));
        result.put("target_os_name",
                   new TaskVariable("target_os_name",
                                    targetNodeCandidate.getImage()
                                                       .getOperatingSystem()
                                                       .getOperatingSystemFamily()
                                                       .name(),
                                    "",
                                    false));
        result.put("target_os_family",
                   new TaskVariable("target_os_family",
                                    targetNodeCandidate.getImage()
                                                       .getOperatingSystem()
                                                       .getOperatingSystemFamily()
                                                       .name(),
                                    "",
                                    false));
        result.put("target_os_version",
                   new TaskVariable("target_os_version",
                                    targetNodeCandidate.getImage()
                                                       .getOperatingSystem()
                                                       .getOperatingSystemVersion()
                                                       .toString(),
                                    "",
                                    false));
        result.put("target_os_arch",
                   new TaskVariable("target_os_arch",
                                    targetNodeCandidate.getImage()
                                                       .getOperatingSystem()
                                                       .getOperatingSystemArchitecture()
                                                       .toString(),
                                    "",
                                    false));
        result.put("target_type", new TaskVariable("target_type", targetType.name(), "", false));
        result.put("target_name", new TaskVariable("target_name", targetName, "", false));
        result.put("target_hdw_cores",
                   new TaskVariable("target_hdw_cores",
                                    targetNodeCandidate.getHardware().getCores().toString(),
                                    "",
                                    false));
        result.put("target_hdw_memory",
                   new TaskVariable("target_hdw_memory",
                                    targetNodeCandidate.getHardware().getRam().toString(),
                                    "",
                                    false));
        result.put("target_hdw_disk",
                   new TaskVariable("target_hdw_disk",
                                    targetNodeCandidate.getHardware().getDisk().toString(),
                                    "",
                                    false));
        result.put("target_hdw_fpga",
                   new TaskVariable("target_hdw_fpga",
                                    targetNodeCandidate.getHardware().getFpga().toString(),
                                    "",
                                    false));
        result.put("target_provider", new TaskVariable("target_provider", targetProvider.name(), "", false));
        result.put("target_open_ports", new TaskVariable("target_open_ports", targetOpenPorts, "", false));
        result.put("target_image_id",
                   new TaskVariable("target_image_id", targetNodeCandidate.getImage().getProviderId(), "", false));
        result.put("region", new TaskVariable("region", targetNodeCandidate.getLocation().getName(), "", false));
        if (targetNodeCandidate.getLocation().getGeoLocation() != null) {
            result.put("location_country",
                       new TaskVariable("location_country",
                                        targetNodeCandidate.getLocation().getGeoLocation().getCountry(),
                                        "",
                                        false));
            result.put("location_city",
                       new TaskVariable("location_city",
                                        targetNodeCandidate.getLocation().getGeoLocation().getCity(),
                                        "",
                                        false));
            result.put("location_longitude",
                       new TaskVariable("location_longitude",
                                        targetNodeCandidate.getLocation().getGeoLocation().getLongitude().toString(),
                                        "",
                                        false));
            result.put("location_latitude",
                       new TaskVariable("location_latitude",
                                        targetNodeCandidate.getLocation().getGeoLocation().getLatitude().toString(),
                                        "",
                                        false));
        }
        result.put("using_https", new TaskVariable("using_https", isUsingHttps + "", "PA:Boolean", false));
        result.put("id", new TaskVariable("id", nodeId, "", false));
        return result;
    }

    public EmsDeploymentRequest copy(String nodeId) {
        return new EmsDeploymentRequest(authorizationBearer,
                                        baguetteIp,
                                        baguettePort,
                                        targetType,
                                        targetNodeCandidate,
                                        targetName,
                                        targetProvider,
                                        targetOpenPorts,
                                        isUsingHttps,
                                        isPrivateIP,
                                        nodeId);
    }
}
