/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Embeddable;

import org.ow2.proactive.sal.util.ModelUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * Credentials for remote access to the virtual machine. Typically, one of password or privateKey is set.
 */
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Accessors(chain = true)
@EqualsAndHashCode
@Getter
@Setter
public class LoginCredential implements Serializable {
    public static final String JSON_USERNAME = "username";

    public static final String JSON_PASSWORD = "password";

    public static final String JSON_PRIVATE_KEY = "privateKey";

    @JsonProperty(JSON_USERNAME)
    private String username = null;

    @JsonProperty(JSON_PASSWORD)
    private String password = null;

    @JsonProperty(JSON_PRIVATE_KEY)
    private String privateKey = null;

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
        fields.put(JSON_USERNAME, username);
        fields.put(JSON_PASSWORD, password);
        fields.put(JSON_PRIVATE_KEY, privateKey);
        return ModelUtils.buildToString(getClass().getSimpleName(), fields);
    }
}
