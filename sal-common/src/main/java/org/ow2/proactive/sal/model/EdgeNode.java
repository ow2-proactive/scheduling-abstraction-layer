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
import lombok.EqualsAndHashCode;
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
@Table(name = "EDGE_NODE")
@Getter
@Setter
@EqualsAndHashCode
public class EdgeNode extends AbstractNode {

    @Column(name = "NAME")
    @JsonProperty(EdgeDefinition.JSON_NAME)
    private String name = null;

    @Embedded
    @JsonProperty(EdgeDefinition.JSON_LOGIN_CREDENTIAL)
    private LoginCredential loginCredential = null;

    @ElementCollection(targetClass = IpAddress.class)
    private List<IpAddress> ipAddresses = null;

    @Embedded
    @JsonProperty(EdgeDefinition.JSON_NODE_PROPERTIES)
    private NodeProperties nodeProperties = null;

    @Column(name = "PORT")
    @JsonProperty(EdgeDefinition.JSON_PORT)
    private String port = null;

    @Column(name = "REASON")
    @JsonProperty(EdgeDefinition.JSON_REASON)
    private String reason = null;

    @Column(name = "DIAGNOSTIC")
    @JsonProperty(EdgeDefinition.JSON_DIAGNOSTIC)
    private String diagnostic = null;

    @Column(name = "USER_ID")
    @JsonProperty(EdgeDefinition.JSON_USER_ID)
    private String userId = null;

    @Column(name = "ALLOCATED")
    @JsonProperty(EdgeDefinition.JSON_ALLOCATED)
    private Boolean allocated = null;

    @Column(name = "JOB_ID")
    @JsonProperty(EdgeDefinition.JSON_JOB_ID)
    private String jobId;

    @Column(name = "SYSTEM_ARCH")
    @JsonProperty(EdgeDefinition.JSON_SYSTEM_ARCH)
    private String systemArch = null;

    @JsonProperty(EdgeDefinition.JSON_SCRIPT_URL)
    private String scriptURL = null;

    @JsonProperty(EdgeDefinition.JSON_JAR_URL)
    private String jarURL = null;

    public String composeNodeSourceName() {
        return "EDGE_NS_" + this.systemArch + "_" + this.id;
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
