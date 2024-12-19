/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.ow2.proactive.sal.util.ModelUtils;

import com.fasterxml.jackson.annotation.*;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


/**
 * polymorphic Superclass, only subtypes are allowed
 */
@Getter
@Setter
@EqualsAndHashCode
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = Requirement.JSON_TYPE, visible = true)
@JsonSubTypes({ @JsonSubTypes.Type(value = AttributeRequirement.class, name = AttributeRequirement.CLASS_NAME),
                @JsonSubTypes.Type(value = NodeTypeRequirement.class, name = NodeTypeRequirement.CLASS_NAME) })
public abstract class Requirement {

    // JSON property constants
    public static final String JSON_TYPE = "type";

    /**
     * Requirement type (can be ATTRIBUTE or NODE_TYPE)
     */
    public enum RequirementType {
        ATTRIBUTE(AttributeRequirement.CLASS_NAME),
        NODE_TYPE(NodeTypeRequirement.CLASS_NAME);

        private final String value;

        RequirementType(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static RequirementType fromValue(String text) {
            for (RequirementType b : RequirementType.values()) {
                if (String.valueOf(b.value).equals(text.toUpperCase(Locale.ROOT))) {
                    return b;
                }
            }
            return null;
        }
    }

    @JsonProperty(JSON_TYPE)
    protected RequirementType type;

    /**
     * Custom toString() method for the Requirement class to format the output.
     * This method creates a formatted string representation of the Requirement object.
     * It uses the type and other fields to build a human-readable string.
     * The method leverages the {@link ModelUtils#buildToString} utility method to generate the string.
     *
     * @return A formatted string representation of the Requirement object, with each field on a new line.
     */
    @Override
    public String toString() {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put(JSON_TYPE, type);

        return ModelUtils.buildToString(Requirement.class.getSimpleName(), fields);
    }
}
