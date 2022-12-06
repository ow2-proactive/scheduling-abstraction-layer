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
package org.ow2.proactive.sal.model;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Part of AttributeRequirement
 */
public enum RequirementOperator {

    EQ("EQ"),

    LEQ("LEQ"),

    GEQ("GEQ"),

    GT("GT"),

    LT("LT"),

    NEQ("NEQ"),

    IN("IN");

    private String value;

    RequirementOperator(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    public boolean compare(String A, String B) {
        switch (this) {
            case EQ:
                return A.equals(B);
            case NEQ:
                return !A.equals(B);
            case IN:
                return B.contains(A);
        }
        throw new RuntimeException("Illegal operation between Strings: " + this.value);
    }

    public boolean compare(Integer A, Integer B) {
        switch (this) {
            case EQ:
                return A.equals(B);
            case LEQ:
                return A <= B;
            case GEQ:
                return A >= B;
            case LT:
                return A < B;
            case GT:
                return A > B;
            case NEQ:
                return !A.equals(B);
        }
        throw new RuntimeException("Illegal operation between Integers: " + this.value);
    }

    public boolean compare(Long A, Long B) {
        switch (this) {
            case EQ:
                return A.equals(B);
            case LEQ:
                return A <= B;
            case GEQ:
                return A >= B;
            case LT:
                return A < B;
            case GT:
                return A > B;
            case NEQ:
                return !A.equals(B);
        }
        throw new RuntimeException("Illegal operation between Longs: " + this.value);
    }

    public boolean compare(Float A, Float B) {
        switch (this) {
            case EQ:
                return A.equals(B);
            case LEQ:
                return A <= B;
            case GEQ:
                return A >= B;
            case LT:
                return A < B;
            case GT:
                return A > B;
            case NEQ:
                return !A.equals(B);
        }
        throw new RuntimeException("Illegal operation between Floats: " + this.value);
    }

    public boolean compare(Double A, Double B) {
        switch (this) {
            case EQ:
                return A.equals(B);
            case LEQ:
                return A <= B;
            case GEQ:
                return A >= B;
            case LT:
                return A < B;
            case GT:
                return A > B;
            case NEQ:
                return !A.equals(B);
        }
        throw new RuntimeException("Illegal operation between Doubles: " + this.value);
    }

    @JsonCreator
    public static RequirementOperator fromValue(String text) {
        for (RequirementOperator b : RequirementOperator.values()) {
            if (String.valueOf(b.value).equals(text.toUpperCase(Locale.ROOT))) {
                return b;
            }
        }
        return null;
    }
}
