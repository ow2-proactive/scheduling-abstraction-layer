/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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

    IN("IN"),

    INC("INC");

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
            case INC:
                return A.contains(B);
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
