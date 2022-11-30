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
package org.ow2.proactive.sal.common.model;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "PA_CLOUD")
public class PACloud implements Serializable {

    public static final String WHITE_LISTED_NAME_PREFIX = "WLH";

    @Id
    @Column(name = "CLOUD_ID")
    private String cloudID;

    @Column(name = "NODE_SOURCE_NAME_PREFIX")
    private String nodeSourceNamePrefix;

    @Column(name = "CLOUD_PROVIDER_NAME")
    private String cloudProviderName;

    @Column(name = "CLOUD_TYPE")
    @Enumerated(EnumType.STRING)
    private CloudType cloudType;

    @Column(name = "SUBNET")
    private String subnet;

    @Column(name = "SECURITY_GROUP")
    private String securityGroup;

    @Embedded
    @Column(name = "SSH_CREDENTIALS")
    private SSHCredentials sshCredentials;

    @Column(name = "ENDPOINT")
    private String endpoint;

    @Column(name = "SCOPE_PREFIX")
    private String scopePrefix;

    @Column(name = "SCOPE_VALUE")
    private String scopeValue;

    @Column(name = "IDENTITY_VERSION")
    private String identityVersion;

    @Column(name = "DUMMY_INFRASTRUCTURE_NAME")
    private String dummyInfrastructureName;

    @Column(name = "DEFAULT_NETWORK")
    private String defaultNetwork;

    @Column(name = "BLACKLIST")
    private String blacklist;

    @Column(name = "DEPLOYED_REGIONS")
    @ElementCollection(targetClass = String.class)
    private Map<String, String> deployedRegions;

    @Column(name = "DEPLOYED_WHITE_LISTED_REGIONS")
    @ElementCollection(targetClass = String.class)
    private Map<String, String> deployedWhiteListedRegions;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Deployment> deployments;

    @OneToOne
    private Credentials credentials;

    public void addDeployment(Deployment deployment) {
        if (deployments == null) {
            deployments = new LinkedList<>();
        }
        deployments.add(deployment);
    }

    public void removeDeployment(Deployment deployment) {
        deployments.remove(deployment);
    }

    public void clearDeployments() {
        deployments.clear();
    }

    public void addDeployedRegion(String region, String imageProviderId) {
        if (deployedRegions == null) {
            deployedRegions = new HashMap<>();
        }
        deployedRegions.put(region, imageProviderId);
    }

    public Boolean isRegionDeployed(String region) {
        return deployedRegions.containsKey(region);
    }

    public void addWhiteListedDeployedRegion(String region, String imageProviderId) {
        if (deployedWhiteListedRegions == null) {
            deployedWhiteListedRegions = new HashMap<>();
        }
        deployedWhiteListedRegions.put(region, imageProviderId);
    }

    public Boolean isWhiteListedRegionDeployed(String region) {
        return deployedWhiteListedRegions.containsKey(region);
    }

    @Override
    public String toString() {
        String deploymentsPrint = deployments == null ? "[]"
                                                      : deployments.stream()
                                                                   .map(Deployment::getNodeName)
                                                                   .collect(Collectors.toList())
                                                                   .toString();
        return "PACloud{" + "cloudID='" + cloudID + '\'' + ", nodeSourceNamePrefix='" + nodeSourceNamePrefix + '\'' +
               ", cloudProviderName='" + cloudProviderName + '\'' + ", cloudType='" + cloudType.toString() + '\'' +
               ", subnet='" + subnet + '\'' + ", securityGroup='" + securityGroup + '\'' + ", sshCredentials='" +
               Optional.ofNullable(sshCredentials).map(SSHCredentials::toString).orElse(null) + '\'' + ", endpoint='" +
               endpoint + '\'' + ", scopePrefix='" + scopePrefix + '\'' + ", scopeValue='" + scopeValue + '\'' +
               ", identityVersion='" + identityVersion + '\'' + ", dummyInfrastructureName='" +
               dummyInfrastructureName + '\'' + ", defaultNetwork='" + defaultNetwork + '\'' + ", blacklist='" +
               blacklist + '\'' + ", deployedRegions=" + deployedRegions + ", deployedWhiteListedRegions=" +
               deployedWhiteListedRegions + ", deployments='" + deploymentsPrint + '\'' + '}';
    }
}
