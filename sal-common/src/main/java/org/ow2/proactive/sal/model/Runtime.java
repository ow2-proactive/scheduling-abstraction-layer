/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Represents runtime provided by a FaaS platform.
 */
public enum Runtime {

    NODEJS("nodejs"),

    PYTHON("python"),

    JAVA("java"),

    DOTNET("dotnet"),

    GO("go");

    private String value;

    Runtime(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static Runtime fromValue(String text) {
        for (Runtime b : Runtime.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
