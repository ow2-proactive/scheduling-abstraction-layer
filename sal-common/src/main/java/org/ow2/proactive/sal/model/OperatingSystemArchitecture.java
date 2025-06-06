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
 * Type of OS Architecture
 */
public enum OperatingSystemArchitecture {

    AMD64("AMD64"),

    UNKNOWN("UNKNOWN"),

    I386("I386"),

    ARM("ARM"),

    ARM64("ARM64");

    private String value;

    OperatingSystemArchitecture(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static OperatingSystemArchitecture fromValue(String text) {
        for (OperatingSystemArchitecture b : OperatingSystemArchitecture.values()) {
            if (String.valueOf(b.value).equals(text.toUpperCase(Locale.ROOT))) {
                return b;
            }
        }
        return UNKNOWN;
    }
}
