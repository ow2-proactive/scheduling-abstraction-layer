/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;

import javax.persistence.*;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Getter
@Setter
@Entity
@Table(name = "CREDENTIALS")
public class Credentials implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "CREDENTIALS_ID")
    private Integer credentialsId;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "PRIVATE_KEY", columnDefinition = "TEXT")
    private String privateKey;

    @Column(name = "PUBLIC_KEY", columnDefinition = "TEXT")
    private String publicKey;

    @Column(name = "DOMAIN")
    private String domain;

}
