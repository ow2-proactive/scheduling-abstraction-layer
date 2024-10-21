/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


/**
 * Repesents the configuration of a cloud.
 */
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CloudConfiguration implements Serializable {
    @Column(name = "NODE_GROUP")
    @JsonProperty("nodeGroup")
    private String nodeGroup = null;

    @Column(name = "PROPERTIES")
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @JsonProperty("properties")
    private Map<String, String> properties = null;

    public CloudConfiguration nodeGroup(String nodeGroup) {
        this.nodeGroup = nodeGroup;
        return this;
    }

    /**
     * A prefix all Cloudiator managed entities will belong to.
     * @return nodeGroup
     **/
    public String getNodeGroup() {
        return nodeGroup;
    }

    public void setNodeGroup(String nodeGroup) {
        this.nodeGroup = nodeGroup;
    }

    public CloudConfiguration properties(Map<String, String> properties) {
        this.properties = properties;
        return this;
    }

    /**
     * Configuration as key-value map.
     * @return properties
     **/
    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CloudConfiguration cloudConfiguration = (CloudConfiguration) o;
        return Objects.equals(this.nodeGroup, cloudConfiguration.nodeGroup) &&
               Objects.equals(this.properties, cloudConfiguration.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeGroup, properties);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class CloudConfiguration {\n");

        sb.append("    nodeGroup: ").append(toIndentedString(nodeGroup)).append("\n");
        sb.append("    properties: ").append(toIndentedString(properties)).append("\n");
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
        this.properties.clear();
    }
}
