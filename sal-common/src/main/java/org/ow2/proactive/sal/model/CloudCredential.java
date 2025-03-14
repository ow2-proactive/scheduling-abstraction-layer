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

import org.ow2.proactive.sal.util.ModelUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Represents the credentials used to authenticate with a cloud
 */
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
@Getter
@Setter
public class CloudCredential implements Serializable {
    public static final String JSON_USER = "user";

    public static final String JSON_SECRET = "secret";

    @Column(name = "USER")
    @JsonProperty(JSON_USER)
    private String user = null;

    @Column(name = "SECRET")
    @JsonProperty(JSON_SECRET)
    private String secret = null;

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
        fields.put(JSON_USER, user);
        fields.put(JSON_SECRET, secret);
        return ModelUtils.buildToString(getClass().getSimpleName(), fields);
    }
}
