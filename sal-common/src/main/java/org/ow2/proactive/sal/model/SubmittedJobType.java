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
