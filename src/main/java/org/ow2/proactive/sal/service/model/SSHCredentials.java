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

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Embeddable
public class SSHCredentials {

    @Column(name = "USERNAME")
    private String username = null;

    @Column(name = "KEY_PAIR_NAME")
    private String keyPairName = null;

    @Lob
    @Column(name = "PRIVATE_KEY")
    private String privateKey = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SSHCredentials sshCredentials = (SSHCredentials) o;
        return Objects.equals(this.username, sshCredentials.username) &&
               Objects.equals(this.keyPairName, sshCredentials.keyPairName) &&
               Objects.equals(this.privateKey, sshCredentials.privateKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, keyPairName, privateKey);
    }
}
