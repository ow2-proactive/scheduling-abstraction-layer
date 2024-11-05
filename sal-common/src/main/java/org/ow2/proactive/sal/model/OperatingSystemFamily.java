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

    WINDOWS("WINDOWS"),

    LINUX("LINUX");

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
