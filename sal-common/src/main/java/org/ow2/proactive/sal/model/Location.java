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
import lombok.NoArgsConstructor;


/**
 * Represents a (virtual) location offers by a cloud
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "LOCATION")
public class Location implements Serializable {
    @Id
    @Column(name = "ID")
    @JsonProperty("id")
    private String id = null;

    @Column(name = "NAME")
    @JsonProperty("name")
    private String name = null;

    @Column(name = "PROVIDER_ID")
    @JsonProperty("providerId")
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
    @JsonProperty("locationScope")
    private LocationScopeEnum locationScope = null;

    @Column(name = "IS_ASSIGNABLE")
    @JsonProperty("isAssignable")
    private Boolean isAssignable = null;

    @Embedded
    @JsonProperty("geoLocation")
    private GeoLocation geoLocation = null;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    @JsonProperty("parent")
    private Location parent = null;

    @Column(name = "STATE")
    @Enumerated(EnumType.STRING)
    @JsonProperty("state")
    private DiscoveryItemState state = null;

    @Column(name = "OWNER")
    @JsonProperty("owner")
    private String owner = null;

    public Location id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Unique identifier
     * @return id
     **/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Location name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Human-readable name
     * @return name
     **/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location providerId(String providerId) {
        this.providerId = providerId;
        return this;
    }

    /**
     * Original id issued by the provider
     * @return providerId
     **/
    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public Location locationScope(LocationScopeEnum locationScope) {
        this.locationScope = locationScope;
        return this;
    }

    /**
     * Scope of the location
     * @return locationScope
     **/
    public LocationScopeEnum getLocationScope() {
        return locationScope;
    }

    public void setLocationScope(LocationScopeEnum locationScope) {
        this.locationScope = locationScope;
    }

    public Location isAssignable(Boolean isAssignable) {
        this.isAssignable = isAssignable;
        return this;
    }

    /**
     * True of the location can be used to start virtual machines, false if not
     * @return isAssignable
     **/
    public Boolean isIsAssignable() {
        return isAssignable;
    }

    public void setIsAssignable(Boolean isAssignable) {
        this.isAssignable = isAssignable;
    }

    public Location geoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
        return this;
    }

    /**
     * Get geoLocation
     * @return geoLocation
     **/
    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    public Location parent(Location parent) {
        this.parent = parent;
        return this;
    }

    /**
     * Get parent
     * @return parent
     **/
    public Location getParent() {
        return parent;
    }

    public void setParent(Location parent) {
        this.parent = parent;
    }

    public Location state(DiscoveryItemState state) {
        this.state = state;
        return this;
    }

    /**
     * Get state
     * @return state
     **/
    public DiscoveryItemState getState() {
        return state;
    }

    public void setState(DiscoveryItemState state) {
        this.state = state;
    }

    public Location owner(String owner) {
        this.owner = owner;
        return this;
    }

    /**
     * Get owner
     * @return owner
     **/
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Location location = (Location) o;
        return Objects.equals(this.id, location.id) && Objects.equals(this.name, location.name) &&
               Objects.equals(this.providerId, location.providerId) &&
               Objects.equals(this.locationScope, location.locationScope) &&
               Objects.equals(this.isAssignable, location.isAssignable) &&
               Objects.equals(this.geoLocation, location.geoLocation) && Objects.equals(this.parent, location.parent) &&
               Objects.equals(this.state, location.state) && Objects.equals(this.owner, location.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, providerId, locationScope, isAssignable, geoLocation, parent, state, owner);
    }

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
