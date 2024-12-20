/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;


/**
 * Represents credentials for authentication and access.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Embeddable
public class Credential implements Serializable {

    // JSON field constants
    public static final String JSON_USER = "user";

    public static final String JSON_SECRET = "secret";

    public static final String JSON_DOMAIN = "domain";

    public static final String JSON_SUBSCRIPTION_ID = "subscriptionId";

    @JsonProperty(JSON_USER)
    private String user = null;

    @JsonProperty(JSON_SECRET)
    private String secret = null;

    @JsonProperty(JSON_DOMAIN)
    private String domain = null;

    @JsonProperty(JSON_SUBSCRIPTION_ID)
    private String subscriptionId = null;
}
