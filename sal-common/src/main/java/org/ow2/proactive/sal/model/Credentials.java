/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;


/**
 * Represents a set of credentials for authentication and access.
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Getter
@Setter
@Entity
@Table(name = "CREDENTIALS")
public class Credentials implements Serializable {

    // JSON field constants
    public static final String JSON_CREDENTIALS_ID = "credentialsId";

    public static final String JSON_USER_NAME = "userName";

    public static final String JSON_PROJECT_ID = "projectId";

    public static final String JSON_PASSWORD = "password";

    public static final String JSON_PRIVATE_KEY = "privateKey";

    public static final String JSON_PUBLIC_KEY = "publicKey";

    public static final String JSON_DOMAIN = "domain";

    public static final String JSON_SUBSCRIPTION_ID = "subscriptionId";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "CREDENTIALS_ID")
    @JsonProperty(JSON_CREDENTIALS_ID)
    private Integer credentialsId;

    @Column(name = "USER_NAME")
    @JsonProperty(JSON_USER_NAME)
    private String userName;

    @Column(name = "PROJECT_ID")
    @JsonProperty(JSON_PROJECT_ID)
    private String projectId;

    @Column(name = "PASSWORD")
    @JsonProperty(JSON_PASSWORD)
    private String password;

    @Column(name = "PRIVATE_KEY", columnDefinition = "TEXT")
    @JsonProperty(JSON_PRIVATE_KEY)
    private String privateKey;

    @Column(name = "PUBLIC_KEY", columnDefinition = "TEXT")
    @JsonProperty(JSON_PUBLIC_KEY)
    private String publicKey;

    @Column(name = "DOMAIN")
    @JsonProperty(JSON_DOMAIN)
    private String domain;

    @Column(name = "SUBSCRIPTION_ID")
    @JsonProperty(JSON_SUBSCRIPTION_ID)
    private String subscriptionId;
}
