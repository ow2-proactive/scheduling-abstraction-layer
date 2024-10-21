/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.nc;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum AwsWhiteListedInstanceTypes {
    F1("F1");

    private String value;

    AwsWhiteListedInstanceTypes(String value) {
        this.value = value;
    }

    /**
     * Says if an AWS hardware type's family is white listed
     * @param instanceType The instance type to check
     * @return True if the instance type family is while listed, false otherwise
     */
    public static boolean isAwsWhiteListedHardwareInstanceType(String instanceType) {
        for (AwsWhiteListedInstanceTypes c : AwsWhiteListedInstanceTypes.values()) {
            if (instanceType.toLowerCase(Locale.ROOT).startsWith(c.value.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static AwsWhiteListedInstanceTypes fromValue(String text) {
        for (AwsWhiteListedInstanceTypes b : AwsWhiteListedInstanceTypes.values()) {
            if (String.valueOf(b.value).equals(text.toUpperCase(Locale.ROOT))) {
                return b;
            }
        }
        return null;
    }
}
