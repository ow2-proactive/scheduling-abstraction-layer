/*
 * ProActive Parallel Suite(TM):
 * The Open Source library for parallel and distributed
 * Workflows & Scheduling, Orchestration, Cloud Automation
 * and Big Data Analysis on Enterprise Grids & Clouds.
 *
 * Copyright (c) 2007 - 2017 ActiveEon
 * Contact: contact@activeeon.com
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation: version 3 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 */
package org.ow2.proactive.sal.service.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Subtype of Requirement
 */
public class AttributeRequirement extends Requirement {
    @JsonProperty("requirementClass")
    private String requirementClass = null;

    @JsonProperty("requirementAttribute")
    private String requirementAttribute = null;

    @JsonProperty("requirementOperator")
    private RequirementOperator requirementOperator = null;

    @JsonProperty("value")
    private String value = null;

    public AttributeRequirement requirementClass(String requirementClass) {
        this.requirementClass = requirementClass;
        return this;
    }

    /**
     * Get requirementClass
     * @return requirementClass
     **/
    public String getRequirementClass() {
        return requirementClass;
    }

    public void setRequirementClass(String requirementClass) {
        this.requirementClass = requirementClass;
    }

    public AttributeRequirement requirementAttribute(String requirementAttribute) {
        this.requirementAttribute = requirementAttribute;
        return this;
    }

    /**
     * Get requirementAttribute
     * @return requirementAttribute
     **/
    public String getRequirementAttribute() {
        return requirementAttribute;
    }

    public void setRequirementAttribute(String requirementAttribute) {
        this.requirementAttribute = requirementAttribute;
    }

    public AttributeRequirement requirementOperator(RequirementOperator requirementOperator) {
        this.requirementOperator = requirementOperator;
        return this;
    }

    /**
     * Get requirementOperator
     * @return requirementOperator
     **/
    public RequirementOperator getRequirementOperator() {
        return requirementOperator;
    }

    public void setRequirementOperator(RequirementOperator requirementOperator) {
        this.requirementOperator = requirementOperator;
    }

    public AttributeRequirement value(String value) {
        this.value = value;
        return this;
    }

    /**
     * Get value
     * @return value
     **/
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
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
