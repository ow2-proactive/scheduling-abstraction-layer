/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;


/**
 * Represents SSH credentials for cloud configuration
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Embeddable
public class SSHCredentials implements Serializable {

    // JSON field constants
    public static final String JSON_USERNAME = "username";

    public static final String JSON_KEY_PAIR_NAME = "keyPairName";

    public static final String JSON_PUBLIC_KEY = "publicKey";

    public static final String JSON_PRIVATE_KEY = "privateKey";

    @Column(name = "USERNAME")
    @JsonProperty(JSON_USERNAME)
    private String username = null;

    @Column(name = "KEY_PAIR_NAME")
    @JsonProperty(JSON_KEY_PAIR_NAME)
    private String keyPairName = null;

    @Lob
    @Column(name = "PUBLIC_KEY")
    @JsonProperty(JSON_PUBLIC_KEY)
    private String publicKey = null;

    @Lob
    @Column(name = "PRIVATE_KEY")
    @JsonProperty(JSON_PRIVATE_KEY)
    private String privateKey = null;
}
