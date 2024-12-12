/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Embeddable
public class SSHCredentials implements Serializable {

    @Column(name = "USERNAME")
    @JsonProperty("username")
    private String username = null;

    @Column(name = "KEY_PAIR_NAME")
    @JsonProperty("keyPairName")
    private String keyPairName = null;

    @Lob
    @Column(name = "PUBLIC_KEY")
    @JsonProperty("publicKey")
    private String publicKey = null;

    @Lob
    @Column(name = "PRIVATE_KEY")
    @JsonProperty("privateKey")
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
               Objects.equals(this.publicKey, sshCredentials.publicKey) &&
               Objects.equals(this.privateKey, sshCredentials.privateKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, keyPairName, publicKey, privateKey);
    }
}
