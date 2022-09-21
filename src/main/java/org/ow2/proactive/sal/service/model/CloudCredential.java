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

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


/**
 * Represents the credentials used to authenticate with a cloud
 */
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CloudCredential implements Serializable {
    @Column(name = "USER")
    @JsonProperty("user")
    private String user = null;

    @Column(name = "SECRET")
    @JsonProperty("secret")
    private String secret = null;

    public CloudCredential user(String user) {
        this.user = user;
        return this;
    }

    /**
     * Username for authentication at the cloud provider's API
     * @return user
     **/
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public CloudCredential secret(String secret) {
        this.secret = secret;
        return this;
    }

    /**
     * Secret (e.g. Password) for authentication at the cloud provider's API
     * @return secret
     **/
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CloudCredential cloudCredential = (CloudCredential) o;
        return Objects.equals(this.user, cloudCredential.user) && Objects.equals(this.secret, cloudCredential.secret);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, secret);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class CloudCredential {\n");

        sb.append("    user: ").append(toIndentedString(user)).append("\n");
        sb.append("    secret: ").append(toIndentedString(secret)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
