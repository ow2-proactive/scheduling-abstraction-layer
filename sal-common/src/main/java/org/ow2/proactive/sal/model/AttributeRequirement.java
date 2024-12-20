/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.ow2.proactive.sal.util.ModelUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


/**
 * Subtype of Requirement
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@JsonTypeName(value = AttributeRequirement.CLASS_NAME)
public class AttributeRequirement extends Requirement {
    // Define class name constant for reuse
    public static final String CLASS_NAME = "AttributeRequirement";

    // JSON property constants
    public static final String JSON_REQUIREMENT_CLASS = "requirementClass";

    public static final String JSON_REQUIREMENT_ATTRIBUTE = "requirementAttribute";

    public static final String JSON_REQUIREMENT_OPERATOR = "requirementOperator";

    public static final String JSON_VALUE = "value";

    @JsonProperty(JSON_REQUIREMENT_CLASS)
    private String requirementClass;

    @JsonProperty(JSON_REQUIREMENT_ATTRIBUTE)
    private String requirementAttribute;

    @JsonProperty(JSON_REQUIREMENT_OPERATOR)
    private RequirementOperator requirementOperator;

    @JsonProperty(JSON_VALUE)
    private String value;

    public AttributeRequirement() {
        this.type = RequirementType.ATTRIBUTE;
    }

    public AttributeRequirement(String requirementClass, String requirementAttribute,
            RequirementOperator requirementOperator, String value) {
        this.type = RequirementType.ATTRIBUTE;
        this.requirementClass = requirementClass;
        this.requirementAttribute = requirementAttribute;
        this.requirementOperator = requirementOperator;
        this.value = value;
    }

    /**
     * Custom toString() method for the AttributeRequirement class to format the output
     * This method creates a formatted string representation of the AttributeRequirement object.
     * It uses a map of field names (represented as JSON constants) and their corresponding values
     * to build a human-readable string. The method leverages the {@link ModelUtils#buildToString}
     * utility method to generate the string, ensuring that all fields are included with proper formatting.
     *
     * @return A formatted string representation of the AttributeRequirement object, with each field on a new line.
     */
    @Override
    public String toString() {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put(JSON_REQUIREMENT_CLASS, requirementClass);
        fields.put(JSON_REQUIREMENT_ATTRIBUTE, requirementAttribute);
        fields.put(JSON_REQUIREMENT_OPERATOR, requirementOperator);
        fields.put(JSON_VALUE, value);

        // Include the parent class fields as well
        String parentString = super.toString();
        Map<String, Object> parentFields = new LinkedHashMap<>();
        parentFields.put("type", type);
        fields.putAll(parentFields);

        return ModelUtils.buildToString(CLASS_NAME, fields);
    }
}
