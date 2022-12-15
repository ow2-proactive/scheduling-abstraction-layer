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
