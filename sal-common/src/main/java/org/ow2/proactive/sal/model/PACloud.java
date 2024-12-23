/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "PA_CLOUD")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = CloudDefinition.JSON_CLOUD_ID, scope = PACloud.class)
public class PACloud implements Serializable {

    public static final String WHITE_LISTED_NAME_PREFIX = "WLH";

    @Id
    @Column(name = "CLOUD_ID")
    private String cloudId;

    @Column(name = "NODE_SOURCE_NAME_PREFIX")
    private String nodeSourceNamePrefix;

    @Column(name = "CLOUD_PROVIDER")
    @Enumerated(EnumType.STRING)
    private CloudProviderType cloudProvider;

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

    @ElementCollection
    @CollectionTable(name = "PACLOUD_DEPLOYED_REGIONS_MAPPING", joinColumns = { @JoinColumn(name = "CLOUD_ID", referencedColumnName = "CLOUD_ID") })
    @MapKeyColumn(name = "REGION")
    @Column(name = "AMI")
    private Map<String, String> deployedRegions;

    @ElementCollection
    @CollectionTable(name = "PACLOUD_DEPLOYED_WL_REGIONS_MAPPING", joinColumns = { @JoinColumn(name = "CLOUD_ID", referencedColumnName = "CLOUD_ID") })
    @MapKeyColumn(name = "REGION")
    @Column(name = "AMI")
    private Map<String, String> deployedWhiteListedRegions;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = { CascadeType.MERGE, CascadeType.REFRESH,
                                                                         CascadeType.REMOVE })
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("deploymentNodeNames")
    private List<Deployment> deployments;

    @OneToOne
    private Credentials credentials;

    //    This is added for deserialization testing purpose
    public PACloud(String cloudId) {
        this.cloudId = cloudId;
    }

    //    This is added for deserialization testing purpose
    @JsonSetter("deploymentNodeNames")
    private void setDeploymentsByIds(List<String> deployments) {
        this.deployments = deployments.stream().map(Deployment::new).collect(Collectors.toList());
        this.deployments.forEach(deployment -> deployment.setPaCloud(this));
    }

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
        return getClass().getSimpleName() + "{" + CloudDefinition.JSON_CLOUD_ID + "='" + cloudId + '\'' + ", " +
               CloudDefinition.JSON_CLOUD_PROVIDER_NAME + "='" + cloudProvider + '\'' + ", " +
               CloudDefinition.JSON_CLOUD_TYPE + "='" + cloudType.toString() + '\'' + ", " +
               CloudDefinition.JSON_SECURITY_GROUP + "='" + securityGroup + '\'' + ", " + CloudDefinition.JSON_SUBNET +
               "='" + subnet + '\'' + ", " + CloudDefinition.JSON_SSH_CREDENTIALS + "='" +
               Optional.ofNullable(sshCredentials).map(SSHCredentials::toString).orElse(null) + '\'' + ", " +
               CloudDefinition.JSON_ENDPOINT + "='" + endpoint + '\'' + ", scopePrefix='" + scopePrefix + '\'' +
               ", scopeValue='" + scopeValue + '\'' + ", " + CloudDefinition.JSON_IDENTITY_VERSION + "='" +
               identityVersion + '\'' + ", dummyInfrastructureName='" + dummyInfrastructureName + '\'' + ", " +
               CloudDefinition.JSON_DEFAULT_NETWORK + "='" + defaultNetwork + '\'' + ", " +
               CloudDefinition.JSON_BLACKLIST + "='" + blacklist + '\'' + ", deployedRegions=" + deployedRegions +
               ", deployedWhiteListedRegions=" + deployedWhiteListedRegions + ", deployments='" + deploymentsPrint +
               '\'' + '}';
    }

    @PreRemove
    private void cleanMappedDataFirst() {
        this.deployedRegions.clear();
        this.deployedWhiteListedRegions.clear();
    }
}
