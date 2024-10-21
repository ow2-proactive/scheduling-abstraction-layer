/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Getter;
import lombok.Setter;


/**
 * Subtype of Requirement
 */
@Getter
@Setter
@JsonTypeName(value = "AttributeRequirement")
public class AttributeRequirement extends Requirement {
    @JsonProperty("requirementClass")
    private String requirementClass;

    @JsonProperty("requirementAttribute")
    private String requirementAttribute;

    @JsonProperty("requirementOperator")
    private RequirementOperator requirementOperator;

    @JsonProperty("value")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AttributeRequirement attributeRequirement = (AttributeRequirement) o;
        return Objects.equals(this.requirementClass, attributeRequirement.requirementClass) &&
               Objects.equals(this.requirementAttribute, attributeRequirement.requirementAttribute) &&
               Objects.equals(this.requirementOperator, attributeRequirement.requirementOperator) &&
               Objects.equals(this.value, attributeRequirement.value) && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requirementClass, requirementAttribute, requirementOperator, value, super.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AttributeRequirement {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    requirementClass: ").append(toIndentedString(requirementClass)).append("\n");
        sb.append("    requirementAttribute: ").append(toIndentedString(requirementAttribute)).append("\n");
        sb.append("    requirementOperator: ").append(toIndentedString(requirementOperator)).append("\n");
        sb.append("    value: ").append(toIndentedString(value)).append("\n");
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
