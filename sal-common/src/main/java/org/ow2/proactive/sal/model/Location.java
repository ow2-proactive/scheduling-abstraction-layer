/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.Locale;

import javax.persistence.*;

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
@Table(name = "LOCATION")
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
    @Column(name = "ID")
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

        private String value;

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Location {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    providerId: ").append(toIndentedString(providerId)).append("\n");
        sb.append("    locationScope: ").append(toIndentedString(locationScope)).append("\n");
        sb.append("    isAssignable: ").append(toIndentedString(isAssignable)).append("\n");
        sb.append("    geoLocation: ").append(toIndentedString(geoLocation)).append("\n");
        sb.append("    parent: ").append(toIndentedString(parent)).append("\n");
        sb.append("    state: ").append(toIndentedString(state)).append("\n");
        sb.append("    owner: ").append(toIndentedString(owner)).append("\n");
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
