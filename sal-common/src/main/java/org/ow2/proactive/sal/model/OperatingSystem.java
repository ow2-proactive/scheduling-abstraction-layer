/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.ow2.proactive.sal.util.ModelUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Represents the operating system of an image
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
@EqualsAndHashCode
public class OperatingSystem implements Serializable {

    // JSON property names as constants
    public static final String JSON_OPERATING_SYSTEM_FAMILY = "operatingSystemFamily";

    public static final String JSON_OPERATING_SYSTEM_ARCHITECTURE = "operatingSystemArchitecture";

    public static final String JSON_OPERATING_SYSTEM_VERSION = "operatingSystemVersion";

    @Column(name = "OPERATING_SYSTEM_FAMILY")
    @Enumerated(EnumType.STRING)
    @JsonProperty(JSON_OPERATING_SYSTEM_FAMILY)
    private OperatingSystemFamily operatingSystemFamily = null;

    @Column(name = "OPERATING_SYSTEM_ARCHITECTURE")
    @Enumerated(EnumType.STRING)
    @JsonProperty(JSON_OPERATING_SYSTEM_ARCHITECTURE)
    private OperatingSystemArchitecture operatingSystemArchitecture = null;

    @Column(name = "OPERATING_SYSTEM_VERSION")
    @JsonProperty(JSON_OPERATING_SYSTEM_VERSION)
    private BigDecimal operatingSystemVersion = null;

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
        fields.put(JSON_OPERATING_SYSTEM_FAMILY, operatingSystemFamily);
        fields.put(JSON_OPERATING_SYSTEM_ARCHITECTURE, operatingSystemArchitecture);
        fields.put(JSON_OPERATING_SYSTEM_VERSION, operatingSystemVersion);

        return ModelUtils.buildToString(getClass().getSimpleName(), fields);
    }
}
