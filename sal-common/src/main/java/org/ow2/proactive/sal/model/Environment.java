/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
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
 * Node candidate environment
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
@EqualsAndHashCode
public class Environment implements Serializable {

    // JSON Constants
    public static final String JSON_RUNTIME = "runtime";

    @Column(name = "RUNTIME")
    @Enumerated(EnumType.STRING)
    @JsonProperty(JSON_RUNTIME)
    private Runtime runtime = null;

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
        // Using LinkedHashMap to preserve field order
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put(JSON_RUNTIME, runtime);

        return ModelUtils.buildToString(Environment.class.getSimpleName(), fields);
    }
}
