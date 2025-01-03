/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.*;


/**
 * Represents installation details for commands, extending the AbstractInstallation class.
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Getter
@Setter
@Embeddable
@JsonTypeName(value = "commands")
public class CommandsInstallation extends AbstractInstallation {

    // JSON field constants
    public static final String JSON_PRE_INSTALL = "preInstall";

    public static final String JSON_INSTALL = "install";

    public static final String JSON_POST_INSTALL = "postInstall";

    public static final String JSON_PRE_START = "preStart";

    public static final String JSON_START = "start";

    public static final String JSON_POST_START = "postStart";

    public static final String JSON_PRE_STOP = "preStop";

    public static final String JSON_STOP = "stop";

    public static final String JSON_POST_STOP = "postStop";

    public static final String JSON_UPDATE_CMD = "update";

    public static final String JSON_START_DETECTION = "startDetection";

    public static final String JSON_OPERATING_SYSTEM = "operatingSystem";

    @Column(name = "PREINSTALL", columnDefinition = "TEXT")
    @JsonProperty(JSON_PRE_INSTALL)
    private String preInstall;

    @Column(name = "INSTALL", columnDefinition = "TEXT")
    @JsonProperty(JSON_INSTALL)
    private String install;

    @Column(name = "POSTINSTALL", columnDefinition = "TEXT")
    @JsonProperty(JSON_POST_INSTALL)
    private String postInstall;

    @Column(name = "PRESTART", columnDefinition = "TEXT")
    @JsonProperty(JSON_PRE_START)
    private String preStart;

    @Column(name = "START", columnDefinition = "TEXT")
    @JsonProperty(JSON_START)
    private String start;

    @Column(name = "POSTSTART", columnDefinition = "TEXT")
    @JsonProperty(JSON_POST_START)
    private String postStart;

    @Column(name = "PRESTOP", columnDefinition = "TEXT")
    @JsonProperty(JSON_PRE_STOP)
    private String preStop;

    @Column(name = "STOP", columnDefinition = "TEXT")
    @JsonProperty(JSON_STOP)
    private String stop;

    @Column(name = "POSTSTOP", columnDefinition = "TEXT")
    @JsonProperty(JSON_POST_STOP)
    private String postStop;

    @Column(name = "UPDATE_CMD", columnDefinition = "TEXT")
    @JsonProperty(JSON_UPDATE_CMD)
    private String updateCmd;

    @Column(name = "START_DETECTION", columnDefinition = "TEXT")
    @JsonProperty(JSON_START_DETECTION)
    private String startDetection;

    @Embedded
    @JsonProperty(JSON_OPERATING_SYSTEM)
    private OperatingSystemType operatingSystemType;

    @Override
    public InstallationType getType() {
        return InstallationType.COMMANDS;
    }
}
