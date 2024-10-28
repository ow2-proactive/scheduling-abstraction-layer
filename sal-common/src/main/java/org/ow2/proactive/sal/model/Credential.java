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


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Embeddable
public class Credential implements Serializable {

    @JsonProperty("user")
    private String user = null;

    @JsonProperty("secret")
    private String secret = null;

    @JsonProperty("domain")
    private String domain = null;

    @JsonProperty("subscriptionId")
    private String subscriptionId = null;
}
