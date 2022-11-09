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
import java.util.Locale;
import java.util.Objects;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


/**
 * Representation of a cloud used by Cloudiator
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CLOUD")
public class Cloud implements Serializable {
    @Id
    @Column(name = "ID")
    @JsonProperty("id")
    private String id = null;

    @Column(name = "ENDPOINT")
    @JsonProperty("endpoint")
    private String endpoint = null;

    @Column(name = "CLOUD_TYPE")
    @Enumerated(EnumType.STRING)
    @JsonProperty("cloudType")
    private CloudType cloudType = null;

    @Embedded
    @JsonProperty("api")
    private Api api = null;

    @Embedded
    @JsonProperty("credential")
    private CloudCredential credential = null;

    @Embedded
    @JsonProperty("cloudConfiguration")
    private CloudConfiguration cloudConfiguration = null;

    @Column(name = "OWNER")
    @JsonProperty("owner")
    private String owner = null;

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

    @Column(name = "STATE")
    @Enumerated(EnumType.STRING)
    @JsonProperty("state")
    private StateEnum state = null;

    @Column(name = "DIAGNOSTIC")
    @JsonProperty("diagnostic")
    private String diagnostic = null;

    public Cloud endpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    /**
     * URI where the api of this cloud provider can be accessed.
     * @return endpoint
     **/
    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Cloud cloudType(CloudType cloudType) {
        this.cloudType = cloudType;
        return this;
    }

    /**
     * Get cloudType
     * @return cloudType
     **/
    public CloudType getCloudType() {
        return cloudType;
    }

    public void setCloudType(CloudType cloudType) {
        this.cloudType = cloudType;
    }

    public Cloud api(Api api) {
        this.api = api;
        return this;
    }

    /**
     * Get api
     * @return api
     **/
    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
    }

    public Cloud credential(CloudCredential credential) {
        this.credential = credential;
        return this;
    }

    /**
     * Get credential
     * @return credential
     **/
    public CloudCredential getCredential() {
        return credential;
    }

    public void setCredential(CloudCredential credential) {
        this.credential = credential;
    }

    public Cloud cloudConfiguration(CloudConfiguration cloudConfiguration) {
        this.cloudConfiguration = cloudConfiguration;
        return this;
    }

    /**
     * Get cloudConfiguration
     * @return cloudConfiguration
     **/
    public CloudConfiguration getCloudConfiguration() {
        return cloudConfiguration;
    }

    public void setCloudConfiguration(CloudConfiguration cloudConfiguration) {
        this.cloudConfiguration = cloudConfiguration;
    }

    public Cloud id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Unique identifier for the cloud
     * @return id
     **/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Cloud owner(String owner) {
        this.owner = owner;
        return this;
    }

    /**
     * Id of the user owning this cloud.
     * @return owner
     **/
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Cloud state(StateEnum state) {
        this.state = state;
        return this;
    }

    /**
     * State of the cloud
     * @return state
     **/
    public StateEnum getState() {
        return state;
    }

    public void setState(StateEnum state) {
        this.state = state;
    }

    public Cloud diagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
        return this;
    }

    /**
     * Diagnostic information for the cloud
     * @return diagnostic
     **/
    public String getDiagnostic() {
        return diagnostic;
    }

    public void setDiagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
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
