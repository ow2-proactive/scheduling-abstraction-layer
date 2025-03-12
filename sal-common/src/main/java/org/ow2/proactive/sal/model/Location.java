/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
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
import lombok.experimental.Accessors;


/**
 * Represents a (virtual) location offered by a cloud
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "LOCATION", indexes = @Index(name = "idx_location_id", columnList = "ID"))
@Accessors(chain = true)
@EqualsAndHashCode
@Getter
@Setter
public class Location implements Serializable {
    public static final String JSON_ID = "id";

    public static final String JSON_NAME = "name";

    public static final String JSON_PROVIDER_ID = "providerId";

    public static final String JSON_LOCATION_SCOPE = "locationScope";

    public static final String JSON_IS_ASSIGNABLE = "isAssignable";

    public static final String JSON_GEO_LOCATION = "geoLocation";

    public static final String JSON_PARENT = "parent";

    public static final String JSON_STATE = "state";

    public static final String JSON_OWNER = "owner";

    @Id
    @Column(name = "ID", nullable = false, unique = true)
    @JsonProperty(JSON_ID)
    private String id = null;

    @Column(name = "NAME")
    @JsonProperty(JSON_NAME)
    private String name = null;

    @Column(name = "PROVIDER_ID")
    @JsonProperty(JSON_PROVIDER_ID)
    private String providerId = null;

    /**
     * Scope of the location
     */
    public enum LocationScopeEnum {
        PROVIDER("PROVIDER"),
        REGION("REGION"),
        ZONE("ZONE"),
        HOST("HOST");

        private final String value;

        LocationScopeEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static LocationScopeEnum fromValue(String text) {
            for (LocationScopeEnum b : LocationScopeEnum.values()) {
                if (String.valueOf(b.value).equals(text.toUpperCase(Locale.ROOT))) {
                    return b;
                }
            }
            return null;
        }
    }

    @Column(name = "LOCATION_SCOPE")
    @Enumerated(EnumType.STRING)
    @JsonProperty(JSON_LOCATION_SCOPE)
    private LocationScopeEnum locationScope = null;

    @Column(name = "IS_ASSIGNABLE")
    @JsonProperty(JSON_IS_ASSIGNABLE)
    private Boolean isAssignable = null;

    @Embedded
    @JsonProperty(JSON_GEO_LOCATION)
    private GeoLocation geoLocation = null;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    @JsonProperty(JSON_PARENT)
    private Location parent = null;

    @Column(name = "STATE")
    @Enumerated(EnumType.STRING)
    @JsonProperty(JSON_STATE)
    private DiscoveryItemState state = null;

    @Column(name = "OWNER")
    @JsonProperty(JSON_OWNER)
    private String owner = null;

    /**
     * Custom toString() method for the class to format the output.
     * This method creates a formatted string representation of the class object.
     * It uses a map of field names (represented as JSON constants) and their corresponding values
     * to build a human-readable string. The method leverages the {@link ModelUtils#buildToString}
     * utility method to generate the string, ensuring that all fields are included with proper formatting.
     *
     * @return A formatted string representation of the Hardware object, with each field on a new line.
     */
    @Override
    public String toString() {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put(JSON_ID, id);
        fields.put(JSON_NAME, name);
        fields.put(JSON_PROVIDER_ID, providerId);
        fields.put(JSON_LOCATION_SCOPE, locationScope);
        fields.put(JSON_IS_ASSIGNABLE, isAssignable);
        fields.put(JSON_GEO_LOCATION, geoLocation);
        fields.put(JSON_PARENT, parent);
        fields.put(JSON_STATE, state);
        fields.put(JSON_OWNER, owner);
        return ModelUtils.buildToString(getClass().getSimpleName(), fields);
    }
}
