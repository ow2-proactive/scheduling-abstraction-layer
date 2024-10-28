/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "VAULT_KEY")
public class VaultKey implements Serializable {
    @Id
    @Column(name = "KEY_NAME")
    private String keyName;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        VaultKey vaultKey = (VaultKey) o;
        return Objects.equals(keyName, vaultKey.keyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyName);
    }
}
