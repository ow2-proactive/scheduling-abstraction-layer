/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.util.Locale;
import java.util.Objects;

import com.fasterxml.jackson.annotation.*;

import lombok.Getter;
import lombok.Setter;


/**
 * polymorphic Superclass, only subtypes are allowed
 */
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({ @JsonSubTypes.Type(value = AttributeRequirement.class, name = "AttributeRequirement"),
                @JsonSubTypes.Type(value = NodeTypeRequirement.class, name = "NodeTypeRequirement"), })

public abstract class Requirement {

    /**
     * Port type
     */
    enum RequirementType {
        ATTRIBUTE("AttributeRequirement"),

        NODE_TYPE("NodeTypeRequirement");

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

    @JsonProperty("type")
    protected RequirementType type;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Requirement requirement = (Requirement) o;
        return Objects.equals(this.type, requirement.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Requirement {\n");

        sb.append("    type: ").append(toIndentedString(type)).append("\n");
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
