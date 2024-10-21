/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum SubmittedJobType {

    CREATED("CREATED"),

    FIRST_DEPLOYMENT("FIRST_DEPLOYMENT"),

    RECONFIGURATION("RECONFIGURATION"),

    SCALE_OUT("SCALE_OUT"),

    SCALE_IN("SCALE_IN"),

    STOP("STOP"),

    UNKNOWN("UNKNOWN");

    private final String value;

    SubmittedJobType(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static SubmittedJobType fromValue(String text) {
        for (SubmittedJobType b : SubmittedJobType.values()) {
            if (String.valueOf(b.value).equals(text.toUpperCase(Locale.ROOT))) {
                return b;
            }
        }
        return UNKNOWN;
    }
}
