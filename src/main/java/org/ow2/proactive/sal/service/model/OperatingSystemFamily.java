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

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Type of OS Family
 */
public enum OperatingSystemFamily {

    UBUNTU("UBUNTU"),

    UNKNOWN_OS_FAMILY("UNKNOWN_OS_FAMILY"),

    AIX("AIX"),

    ARCH("ARCH"),

    CENTOS("CENTOS"),

    DARWIN("DARWIN"),

    DEBIAN("DEBIAN"),

    ESX("ESX"),

    FEDORA("FEDORA"),

    FREEBSD("FREEBSD"),

    GENTOO("GENTOO"),

    HPUX("HPUX"),

    COREOS("COREOS"),

    AMZN_LINUX("AMZN_LINUX"),

    MANDRIVA("MANDRIVA"),

    NETBSD("NETBSD"),

    OEL("OEL"),

    OPENBSD("OPENBSD"),

    RHEL("RHEL"),

    SCIENTIFIC("SCIENTIFIC"),

    CEL("CEL"),

    SLACKWARE("SLACKWARE"),

    SOLARIS("SOLARIS"),

    SUSE("SUSE"),

    TURBOLINUX("TURBOLINUX"),

    CLOUD_LINUX("CLOUD_LINUX"),

    WINDOWS("WINDOWS");

    private String value;

    OperatingSystemFamily(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static OperatingSystemFamily fromValue(String text) {
        for (OperatingSystemFamily b : OperatingSystemFamily.values()) {
            if (String.valueOf(b.value).equals(text.toUpperCase(Locale.ROOT))) {
                return b;
            }
        }
        return UNKNOWN_OS_FAMILY;
    }
}
