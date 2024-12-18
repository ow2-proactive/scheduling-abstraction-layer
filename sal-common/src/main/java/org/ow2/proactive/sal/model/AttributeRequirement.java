/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

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
@JsonTypeName(value = "AttributeRequirement")
public class AttributeRequirement extends Requirement {

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

    public AttributeRequirement(String requirementClass, String requirementAttribute,
            RequirementOperator requirementOperator, String value) {
        this.type = RequirementType.ATTRIBUTE;
        this.requirementClass = requirementClass;
        this.requirementAttribute = requirementAttribute;
        this.requirementOperator = requirementOperator;
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AttributeRequirement {\n");

        sb.append("    ").append(super.toString()).append("\n");
        sb.append("    ")
          .append(JSON_REQUIREMENT_CLASS)
          .append(": ")
          .append(toIndentedString(requirementClass))
          .append("\n");
        sb.append("    ")
          .append(JSON_REQUIREMENT_ATTRIBUTE)
          .append(": ")
          .append(toIndentedString(requirementAttribute))
          .append("\n");
        sb.append("    ")
          .append(JSON_REQUIREMENT_OPERATOR)
          .append(": ")
          .append(toIndentedString(requirementOperator))
          .append("\n");
        sb.append("    ").append(JSON_VALUE).append(": ").append(toIndentedString(value)).append("\n");
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
}
