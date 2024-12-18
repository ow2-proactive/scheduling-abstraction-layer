/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Representation of a cloud used by Cloudiator
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "CLOUD")
public class Cloud implements Serializable {
    public static final String JSON_ID = "id";

    public static final String JSON_ENDPOINT = "endpoint";

    public static final String JSON_CLOUD_TYPE = "cloudType";

    public static final String JSON_API = "api";

    public static final String JSON_CREDENTIAL = "credential";

    public static final String JSON_CLOUD_CONFIGURATION = "cloudConfiguration";

    public static final String JSON_OWNER = "owner";

    public static final String JSON_STATE = "state";

    public static final String JSON_DIAGNOSTIC = "diagnostic";

    @Id
    @Column(name = "ID")
    @JsonProperty(JSON_ID)
    private String id = null;

    @Column(name = "ENDPOINT")
    @JsonProperty(JSON_ENDPOINT)
    @EqualsAndHashCode.Include
    private String endpoint = null;

    @Column(name = "CLOUD_TYPE")
    @Enumerated(EnumType.STRING)
    @JsonProperty(JSON_CLOUD_TYPE)
    private CloudType cloudType = null;

    @Embedded
    @JsonProperty(JSON_API)
    private Api api = null;

    @Embedded
    @JsonProperty(JSON_CREDENTIAL)
    private CloudCredential credential = null;

    @Embedded
    @JsonProperty(JSON_CLOUD_CONFIGURATION)
    private CloudConfiguration cloudConfiguration = null;

    @Column(name = "OWNER")
    @JsonProperty(JSON_OWNER)
    private String owner = null;

    @Column(name = "STATE")
    @Enumerated(EnumType.STRING)
    @JsonProperty(JSON_STATE)
    private StateEnum state = null;

    @Column(name = "DIAGNOSTIC")
    @JsonProperty(JSON_DIAGNOSTIC)
    private String diagnostic = null;

    /**
     * State of the cloud
     */
    public enum StateEnum {
        OK("OK"),
        ERROR("ERROR");

        private String value;

        StateEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static StateEnum fromValue(String text) {
            for (StateEnum b : StateEnum.values()) {
                if (String.valueOf(b.value).equals(text.toUpperCase(Locale.ROOT))) {
                    return b;
                }
            }
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Cloud cloud = (Cloud) o;
        return Objects.equals(this.endpoint, cloud.endpoint) && Objects.equals(this.cloudType, cloud.cloudType) &&
               Objects.equals(this.api, cloud.api) && Objects.equals(this.credential, cloud.credential) &&
               Objects.equals(this.cloudConfiguration, cloud.cloudConfiguration) && Objects.equals(this.id, cloud.id) &&
               Objects.equals(this.owner, cloud.owner) && Objects.equals(this.state, cloud.state) &&
               Objects.equals(this.diagnostic, cloud.diagnostic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(endpoint, cloudType, api, credential, cloudConfiguration, id, owner, state, diagnostic);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Cloud {\n");

        sb.append("    endpoint: ").append(toIndentedString(endpoint)).append("\n");
        sb.append("    cloudType: ").append(toIndentedString(cloudType)).append("\n");
        sb.append("    api: ").append(toIndentedString(api)).append("\n");
        sb.append("    credential: ").append(toIndentedString(credential)).append("\n");
        sb.append("    cloudConfiguration: ").append(toIndentedString(cloudConfiguration)).append("\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    owner: ").append(toIndentedString(owner)).append("\n");
        sb.append("    state: ").append(toIndentedString(state)).append("\n");
        sb.append("    diagnostic: ").append(toIndentedString(diagnostic)).append("\n");
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
