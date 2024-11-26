/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.util.List;
import java.util.Objects;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Representation of an Edge used by ProActive
 * This class is a clone of the Byon Node Class with some modifications
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "EDGE_NODE")
public class EdgeNode extends AbstractNode {
    public static final String ANY_JOB_ID = "any";

    // Constants for JSON properties
    public static final String JSON_NAME = "name";

    public static final String JSON_LOGIN_CREDENTIAL = "loginCredential";

    public static final String JSON_IP_ADDRESSES = "ipAddresses";

    public static final String JSON_NODE_PROPERTIES = "nodeProperties";

    public static final String JSON_PORT = "port";

    public static final String JSON_REASON = "reason";

    public static final String JSON_DIAGNOSTIC = "diagnostic";

    public static final String JSON_USER_ID = "userId";

    public static final String JSON_ALLOCATED = "allocated";

    // edge jobID corresponds to the ProActive job name
    public static final String JSON_JOB_ID = "jobId";

    public static final String JSON_SYSTEM_ARCH = "systemArch";

    public static final String JSON_SCRIPT_URL = "scriptURL";

    public static final String JSON_JAR_URL = "jarURL";

    @Column(name = "NAME")
    @JsonProperty(JSON_NAME)
    private String name = null;

    @Embedded
    @JsonProperty(JSON_LOGIN_CREDENTIAL)
    private LoginCredential loginCredential = null;

    @ElementCollection(targetClass = IpAddress.class)
    private List<IpAddress> ipAddresses = null;

    @Embedded
    @JsonProperty(JSON_NODE_PROPERTIES)
    private NodeProperties nodeProperties = null;

    @Column(name = "PORT")
    @JsonProperty(JSON_PORT)
    private String port = null;

    @Column(name = "REASON")
    @JsonProperty(JSON_REASON)
    private String reason = null;

    @Column(name = "DIAGNOSTIC")
    @JsonProperty(JSON_DIAGNOSTIC)
    private String diagnostic = null;

    @Column(name = "USER_ID")
    @JsonProperty(JSON_USER_ID)
    private String userId = null;

    @Column(name = "ALLOCATED")
    @JsonProperty(JSON_ALLOCATED)
    private Boolean allocated = null;

    @Column(name = "JOB_ID")
    @JsonProperty(JSON_JOB_ID)
    private String jobId;

    @Column(name = "SYSTEM_ARCH")
    @JsonProperty(JSON_SYSTEM_ARCH)
    private String systemArch = null;

    @JsonProperty(JSON_SCRIPT_URL)
    private String scriptURL = null;

    @JsonProperty(JSON_JAR_URL)
    private String jarURL = null;

    public EdgeNode name(String name) {
        this.setName(name);
        return this;
    }

    public EdgeNode loginCredential(LoginCredential loginCredential) {
        this.setLoginCredential(loginCredential);
        return this;
    }

    public EdgeNode ipAddresses(List<IpAddress> ipAddresses) {
        this.setIpAddresses(ipAddresses);
        return this;
    }

    public EdgeNode addIpAddressesItem(IpAddress ipAddressesItem) {
        this.addIpAddressesItem(ipAddressesItem);
        return this;
    }

    public EdgeNode nodeProperties(NodeProperties nodeProperties) {
        this.setNodeProperties(nodeProperties);
        return this;
    }

    public EdgeNode reason(String reason) {
        this.setReason(reason);
        return this;
    }

    public EdgeNode diagnostic(String diagnostic) {
        this.setDiagnostic(diagnostic);
        return this;
    }

    public EdgeNode id(String id) {
        this.setId(id);
        return this;
    }

    public EdgeNode userId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public EdgeNode allocated(Boolean allocated) {
        this.setAllocated(allocated);
        return this;
    }

    public String composeNodeSourceName() {
        return "EDGE_NS_" + this.systemArch + "_" + this.id;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EdgeNode edgeNode = (EdgeNode) o;
        return Objects.equals(this.name, edgeNode.getName()) &&
               Objects.equals(this.loginCredential, edgeNode.getLoginCredential()) &&
               Objects.equals(this.ipAddresses, edgeNode.getIpAddresses()) &&
               Objects.equals(this.nodeProperties, edgeNode.getNodeProperties()) &&
               Objects.equals(this.reason, edgeNode.getReason()) &&
               Objects.equals(this.diagnostic, edgeNode.getDiagnostic()) &&
               Objects.equals(this.nodeCandidate, edgeNode.getNodeCandidate()) &&
               Objects.equals(this.id, edgeNode.getId()) && Objects.equals(this.userId, edgeNode.getUserId()) &&
               Objects.equals(this.allocated, edgeNode.getAllocated()) &&
               Objects.equals(this.jobId, edgeNode.getJobId()) &&
               Objects.equals(this.systemArch, edgeNode.getSystemArch()) &&
               Objects.equals(this.scriptURL, edgeNode.getScriptURL()) && Objects.equals(jarURL, edgeNode.getJarURL());
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
                            this.nodeProperties,
                            this.userId,
                            this.allocated,
                            this.jobId,
                            this.systemArch,
                            this.scriptURL,
                            this.jarURL);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class EdgeNode {\n");

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
        sb.append("    systemArch: ").append(toIndentedString(this.systemArch)).append("\n");
        sb.append("    scriptURL: ").append(toIndentedString(this.scriptURL)).append("\n");
        sb.append("    jarURL: ").append(toIndentedString(this.jarURL)).append("\n");
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
