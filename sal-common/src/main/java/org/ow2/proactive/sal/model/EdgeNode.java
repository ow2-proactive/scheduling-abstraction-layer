/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.*;

import org.ow2.proactive.sal.util.ModelUtils;

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
@EqualsAndHashCode(callSuper = false)
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

    /**
     * Custom toString() method for the class to format the output.
     * This method creates a formatted string representation of the class object.
     * It uses a map of field names (represented as JSON constants) and their corresponding values
     * to build a human-readable string. The method leverages the {@link ModelUtils#buildToString}
     * utility method to generate the string, ensuring that all fields are included with proper formatting.
     *
     * @return A formatted string representation of the Hardware object, with each field on a new line.
     */
    @Override
    public String toString() {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put(EdgeDefinition.JSON_NAME, name);
        fields.put(EdgeDefinition.JSON_LOGIN_CREDENTIAL, loginCredential);
        fields.put(EdgeDefinition.JSON_NODE_PROPERTIES, nodeProperties);
        fields.put(EdgeDefinition.JSON_PORT, port);
        fields.put(EdgeDefinition.JSON_REASON, reason);
        fields.put(EdgeDefinition.JSON_DIAGNOSTIC, diagnostic);
        fields.put(EdgeDefinition.JSON_USER_ID, userId);
        fields.put(EdgeDefinition.JSON_ALLOCATED, allocated);
        fields.put(EdgeDefinition.JSON_JOB_ID, jobId);
        fields.put(EdgeDefinition.JSON_SYSTEM_ARCH, systemArch);
        fields.put(EdgeDefinition.JSON_SCRIPT_URL, scriptURL);
        fields.put(EdgeDefinition.JSON_JAR_URL, jarURL);

        return ModelUtils.buildToString(EdgeNode.class.getSimpleName(), fields);
    }

    @PreRemove
    private void cleanMappedDataFirst() {
        this.ipAddresses.clear();
    }
}
