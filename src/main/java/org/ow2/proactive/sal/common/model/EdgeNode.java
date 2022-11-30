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

import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
public class EdgeNode extends ByonNode {

    @Column(name = "SYSTEM_ARCH")
    @JsonProperty("systemArch")
    private String systemArch = null;

    @JsonProperty("scriptURL")
    private String scriptURL = null;

    @JsonProperty("jarURL")
    private String jarURL = null;

    @Override
    public EdgeNode name(String name) {
        super.setName(name);
        return this;
    }

    @Override
    public EdgeNode loginCredential(LoginCredential loginCredential) {
        super.setLoginCredential(loginCredential);
        return this;
    }

    @Override
    public EdgeNode ipAddresses(List<IpAddress> ipAddresses) {
        super.setIpAddresses(ipAddresses);
        return this;
    }

    @Override
    public EdgeNode addIpAddressesItem(IpAddress ipAddressesItem) {
        super.addIpAddressesItem(ipAddressesItem);
        return this;
    }

    @Override
    public EdgeNode nodeProperties(NodeProperties nodeProperties) {
        super.setNodeProperties(nodeProperties);
        return this;
    }

    @Override
    public EdgeNode reason(String reason) {
        super.setReason(reason);
        return this;
    }

    @Override
    public EdgeNode diagnostic(String diagnostic) {
        super.setDiagnostic(diagnostic);
        return this;
    }

    @Override
    public EdgeNode id(String id) {
        super.setId(id);
        return this;
    }

    @Override
    public EdgeNode userId(String userId) {
        super.setUserId(userId);
        return this;
    }

    @Override
    public EdgeNode allocated(Boolean allocated) {
        super.setAllocated(allocated);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EdgeNode edgeNode = (EdgeNode) o;
        return Objects.equals(super.getName(), edgeNode.getName()) &&
               Objects.equals(super.getLoginCredential(), edgeNode.getLoginCredential()) &&
               Objects.equals(super.getIpAddresses(), edgeNode.getIpAddresses()) &&
               Objects.equals(super.getNodeProperties(), edgeNode.getNodeProperties()) &&
               Objects.equals(super.getReason(), edgeNode.getReason()) &&
               Objects.equals(super.getDiagnostic(), edgeNode.getDiagnostic()) &&
               Objects.equals(super.getNodeCandidate(), edgeNode.getNodeCandidate()) &&
               Objects.equals(super.getId(), edgeNode.getId()) &&
               Objects.equals(super.getUserId(), edgeNode.getUserId()) &&
               Objects.equals(super.getAllocated(), edgeNode.getAllocated()) &&
               Objects.equals(super.getJobId(), edgeNode.getJobId()) &&
               Objects.equals(systemArch, edgeNode.getSystemArch()) &&
               Objects.equals(scriptURL, edgeNode.getScriptURL()) && Objects.equals(jarURL, edgeNode.getJarURL());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.getName(),
                            super.getLoginCredential(),
                            super.getIpAddresses(),
                            super.getNodeProperties(),
                            super.getReason(),
                            super.getDiagnostic(),
                            super.getNodeCandidate(),
                            super.getId(),
                            super.getUserId(),
                            super.getAllocated(),
                            super.getJobId(),
                            systemArch,
                            scriptURL,
                            jarURL);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class EdgeNode {\n");

        sb.append("    name: ").append(toIndentedString(super.getName())).append("\n");
        sb.append("    loginCredential: ").append(toIndentedString(super.getLoginCredential())).append("\n");
        sb.append("    ipAddresses: ").append(toIndentedString(super.getIpAddresses())).append("\n");
        sb.append("    nodeProperties: ").append(toIndentedString(super.getNodeProperties())).append("\n");
        sb.append("    reason: ").append(toIndentedString(super.getReason())).append("\n");
        sb.append("    diagnostic: ").append(toIndentedString(super.getDiagnostic())).append("\n");
        sb.append("    nodeCandidate: ").append(toIndentedString(super.getNodeCandidate())).append("\n");
        sb.append("    id: ").append(toIndentedString(super.getId())).append("\n");
        sb.append("    userId: ").append(toIndentedString(super.getUserId())).append("\n");
        sb.append("    allocated: ").append(toIndentedString(super.getAllocated())).append("\n");
        sb.append("    jobId: ").append(toIndentedString(super.getJobId())).append("\n");
        sb.append("    systemArch: ").append(toIndentedString(systemArch)).append("\n");
        sb.append("    scriptURL: ").append(toIndentedString(scriptURL)).append("\n");
        sb.append("    jarURL: ").append(toIndentedString(jarURL)).append("\n");
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
}
