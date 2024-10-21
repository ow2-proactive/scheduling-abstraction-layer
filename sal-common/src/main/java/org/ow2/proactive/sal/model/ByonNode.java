/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Representation of a BYON used by Cloudiator
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "BYON_NODE")
public class ByonNode extends AbstractNode {

    @Column(name = "NAME")
    @JsonProperty("name")
    private String name = null;

    @Embedded
    @JsonProperty("loginCredential")
    private LoginCredential loginCredential = null;

    @ElementCollection(targetClass = IpAddress.class)
    private List<IpAddress> ipAddresses = null;

    @Embedded
    @JsonProperty("nodeProperties")
    private NodeProperties nodeProperties = null;

    @Column(name = "REASON")
    @JsonProperty("reason")
    private String reason = null;

    @Column(name = "DIAGNOSTIC")
    @JsonProperty("diagnostic")
    private String diagnostic = null;

    @Column(name = "USER_ID")
    @JsonProperty("userId")
    private String userId = null;

    @Column(name = "ALLOCATED")
    @JsonProperty("allocated")
    private Boolean allocated = null;

    @Column(name = "JOB_ID")
    @JsonProperty("jobId")
    private String jobId;

    public ByonNode name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Human-readable name for the node.
     * @return name
     **/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ByonNode loginCredential(LoginCredential loginCredential) {
        this.loginCredential = loginCredential;
        return this;
    }

    /**
     * Get loginCredential
     * @return loginCredential
     **/
    public LoginCredential getLoginCredential() {
        return loginCredential;
    }

    public void setLoginCredential(LoginCredential loginCredential) {
        this.loginCredential = loginCredential;
    }

    public ByonNode ipAddresses(List<IpAddress> ipAddresses) {
        this.ipAddresses = ipAddresses;
        return this;
    }

    public ByonNode addIpAddressesItem(IpAddress ipAddressesItem) {
        if (this.ipAddresses == null) {
            this.ipAddresses = new ArrayList<IpAddress>();
        }
        this.ipAddresses.add(ipAddressesItem);
        return this;
    }

    /**
     * The public/private ip addresses under which this node is reachable.
     * @return ipAddresses
     **/
    public List<IpAddress> getIpAddresses() {
        return ipAddresses;
    }

    public void setIpAddresses(List<IpAddress> ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    public ByonNode nodeProperties(NodeProperties nodeProperties) {
        this.nodeProperties = nodeProperties;
        return this;
    }

    /**
     * Further properties of this node.
     * @return nodeProperties
     **/
    public NodeProperties getNodeProperties() {
        return nodeProperties;
    }

    public void setNodeProperties(NodeProperties nodeProperties) {
        this.nodeProperties = nodeProperties;
    }

    public ByonNode reason(String reason) {
        this.reason = reason;
        return this;
    }

    public ByonNode diagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
        return this;
    }

    public ByonNode id(String id) {
        this.id = id;
        return this;
    }

    public ByonNode userId(String userId) {
        this.userId = userId;
        return this;
    }

    public ByonNode allocated(Boolean allocated) {
        this.allocated = allocated;
        return this;
    }

    /**
     * Signals if the node was allocated by cloudiator
     * @return allocated
     **/
    public Boolean isAllocated() {
        return this.allocated;
    }

    public String composeNodeSourceName() {
        return "BYON_NS_" + this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ByonNode byonNode = (ByonNode) o;
        return Objects.equals(this.name, byonNode.name) &&
               Objects.equals(this.loginCredential, byonNode.loginCredential) &&
               Objects.equals(this.ipAddresses, byonNode.ipAddresses) &&
               Objects.equals(this.nodeProperties, byonNode.nodeProperties) &&
               Objects.equals(this.reason, byonNode.reason) && Objects.equals(this.diagnostic, byonNode.diagnostic) &&
               Objects.equals(this.nodeCandidate, byonNode.nodeCandidate) && Objects.equals(this.id, byonNode.id) &&
               Objects.equals(this.userId, byonNode.userId) && Objects.equals(this.allocated, byonNode.allocated) &&
               Objects.equals(this.jobId, byonNode.jobId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name,
                            this.id,
                            this.loginCredential,
                            this.ipAddresses,
                            this.nodeProperties,
                            this.reason,
                            this.diagnostic,
                            this.nodeCandidate,
                            this.userId,
                            this.allocated);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ByonNode {\n");

        sb.append("    name: ").append(toIndentedString(this.name)).append("\n");
        sb.append("    id: ").append(toIndentedString(this.id)).append("\n");
        sb.append("    loginCredential: ").append(toIndentedString(this.loginCredential)).append("\n");
        sb.append("    ipAddresses: ").append(toIndentedString(this.ipAddresses)).append("\n");
        sb.append("    nodeProperties: ").append(toIndentedString(this.nodeProperties)).append("\n");
        sb.append("    reason: ").append(toIndentedString(this.reason)).append("\n");
        sb.append("    diagnostic: ").append(toIndentedString(this.diagnostic)).append("\n");
        sb.append("    nodeCandidate: ").append(toIndentedString(this.nodeCandidate)).append("\n");
        sb.append("    userId: ").append(toIndentedString(this.userId)).append("\n");
        sb.append("    allocated: ").append(toIndentedString(this.allocated)).append("\n");
        sb.append("    jobId: ").append(toIndentedString(this.jobId)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    @PreRemove
    private void cleanMappedDataFirst() {
        this.ipAddresses.clear();
    }
}
