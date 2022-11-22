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
