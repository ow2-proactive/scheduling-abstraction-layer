/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Types of cloud providers
 */
public enum CloudProviderType {

    AWS_EC2("aws-ec2"),
    AZURE("azure"),
    GCE("gce"),
    OPENSTACK("openstack"), //openstack-nova

    EDGE("EDGE"),
    BYON("BYON"),

    OTHER("other"); // For any unknown or future providers

    private final String value;

    CloudProviderType(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }

    @JsonCreator
    public static CloudProviderType fromValue(String text) {
        for (CloudProviderType type : CloudProviderType.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return OTHER; // Return a default type if the input is invalid
    }
}
