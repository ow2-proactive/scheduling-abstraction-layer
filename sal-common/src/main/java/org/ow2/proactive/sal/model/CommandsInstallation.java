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


@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Getter
@Setter
@Embeddable
@JsonTypeName(value = "commands")
public class CommandsInstallation extends AbstractInstallation {

    @Column(name = "PREINSTALL", columnDefinition = "TEXT")
    @JsonProperty("preInstall")
    private String preInstall;

    @Column(name = "INSTALL", columnDefinition = "TEXT")
    @JsonProperty("install")
    private String install;

    @Column(name = "POSTINSTALL", columnDefinition = "TEXT")
    @JsonProperty("postInstall")
    private String postInstall;

    @Column(name = "PRESTART", columnDefinition = "TEXT")
    @JsonProperty("preStart")
    private String preStart;

    @Column(name = "START", columnDefinition = "TEXT")
    @JsonProperty("start")
    private String start;

    @Column(name = "POSTSTART", columnDefinition = "TEXT")
    @JsonProperty("postStart")
    private String postStart;

    @Column(name = "PRESTOP", columnDefinition = "TEXT")
    @JsonProperty("preStop")
    private String preStop;

    @Column(name = "STOP", columnDefinition = "TEXT")
    @JsonProperty("stop")
    private String stop;

    @Column(name = "POSTSTOP", columnDefinition = "TEXT")
    @JsonProperty("postStop")
    private String postStop;

    @Column(name = "UPDATE_CMD", columnDefinition = "TEXT")
    @JsonProperty("update")
    private String updateCmd;

    @Column(name = "START_DETECTION", columnDefinition = "TEXT")
    @JsonProperty("startDetection")
    private String startDetection;

    @Embedded
    @JsonProperty("operatingSystem")
    private OperatingSystemType operatingSystemType;

    @Override
    public InstallationType getType() {
        return InstallationType.COMMANDS;
    }
}
