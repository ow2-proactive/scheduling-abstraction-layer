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

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Getter
@Setter
@Embeddable
public class CommandsInstallation {
    @Column(name = "PREINSTALL", columnDefinition = "TEXT")
    private String preInstall;

    @Column(name = "INSTALL", columnDefinition = "TEXT")
    private String install;

    @Column(name = "POSTINSTALL", columnDefinition = "TEXT")
    private String postInstall;

    @Column(name = "PRESTART", columnDefinition = "TEXT")
    private String preStart;

    @Column(name = "START", columnDefinition = "TEXT")
    private String start;

    @Column(name = "POSTSTART", columnDefinition = "TEXT")
    private String postStart;

    @Column(name = "PRESTOP", columnDefinition = "TEXT")
    private String preStop;

    @Column(name = "STOP", columnDefinition = "TEXT")
    private String stop;

    @Column(name = "POSTSTOP", columnDefinition = "TEXT")
    private String postStop;

    @Column(name = "UPDATE_CMD", columnDefinition = "TEXT")
    private String updateCmd;

    @Embedded
    private OperatingSystemType operatingSystemType;
}
