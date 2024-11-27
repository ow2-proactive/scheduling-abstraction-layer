/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


/**
 * Credentials for remote access to the virtual machine. Typically, one of password or privateKey is set.
 */
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class LoginCredential implements Serializable {
    public static final String JSON_USERNAME = "username";

    public static final String JSON_PASSWORD = "password";

    public static final String JSON_PRIVATE_KEY = "privateKey";

    @JsonProperty(JSON_USERNAME)
    private String username = null;

    @JsonProperty(JSON_PASSWORD)
    private String password = null;

    @JsonProperty(JSON_PRIVATE_KEY)
    private String privateKey = null;

    public LoginCredential username(String username) {
        this.username = username;
        return this;
    }

    /**
     * The username for login
     * @return username
     **/
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LoginCredential password(String password) {
        this.password = password;
        return this;
    }

    /**
     * The password for login
     * @return password
     **/
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LoginCredential privateKey(String privateKey) {
        this.privateKey = privateKey;
        return this;
    }

    /**
     * The private key for login
     * @return privateKey
     **/
    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LoginCredential loginCredential = (LoginCredential) o;
        return Objects.equals(this.username, loginCredential.username) &&
               Objects.equals(this.password, loginCredential.password) &&
               Objects.equals(this.privateKey, loginCredential.privateKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, privateKey);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class LoginCredential {\n");

        sb.append("    ")
          .append(LoginCredential.JSON_USERNAME)
          .append(": ")
          .append(toIndentedString(username))
          .append("\n");
        sb.append("    ")
          .append(LoginCredential.JSON_PASSWORD)
          .append(": ")
          .append(toIndentedString(password))
          .append("\n");
        sb.append("    ")
          .append(LoginCredential.JSON_PRIVATE_KEY)
          .append(": ")
          .append(toIndentedString(privateKey))
          .append("\n");
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
