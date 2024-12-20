/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.*;

import org.ow2.proactive.sal.util.ModelUtils;

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
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
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

        private final String value;

        StateEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return value;
        }

        @JsonCreator
        public static StateEnum fromValue(String text) {
            for (StateEnum b : StateEnum.values()) {
                if (b.value.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @Override
    public String toString() {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put(JSON_ID, id);
        fields.put(JSON_ENDPOINT, endpoint);
        fields.put(JSON_CLOUD_TYPE, cloudType);
        fields.put(JSON_API, api);
        fields.put(JSON_CREDENTIAL, credential);
        fields.put(JSON_CLOUD_CONFIGURATION, cloudConfiguration);
        fields.put(JSON_OWNER, owner);
        fields.put(JSON_STATE, state);
        fields.put(JSON_DIAGNOSTIC, diagnostic);

        return ModelUtils.buildToString(Cloud.class.getSimpleName(), fields);
    }

}
