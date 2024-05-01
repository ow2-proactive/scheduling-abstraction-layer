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

    @Column(name = "PORT")
    @JsonProperty("port")
    private String port = null;

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

    @Column(name = "SYSTEM_ARCH")
    @JsonProperty("systemArch")
    private String systemArch = null;

    @JsonProperty("scriptURL")
    private String scriptURL = null;

    @JsonProperty("jarURL")
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
