/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


/**
 * Part of Location Represents a geographical location
 */
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class GeoLocation implements Serializable {
    @Column(name = "CITY")
    @JsonProperty("city")
    private String city = null;

    @Column(name = "COUNTRY")
    @JsonProperty("country")
    private String country = null;

    @Column(name = "LATITUDE")
    @JsonProperty("latitude")
    private Double latitude = null;

    @Column(name = "LONGITUDE")
    @JsonProperty("longitude")
    private Double longitude = null;

    public GeoLocation(GeoLocationData geoLocationData) {
        this.city = geoLocationData.getCity();
        this.country = geoLocationData.getCountry();
        this.latitude = geoLocationData.getLatitude();
        this.longitude = geoLocationData.getLongitude();
    }

    public GeoLocation city(String city) {
        this.city = city;
        return this;
    }

    /**
     * City of the location
     * @return city
     **/
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public GeoLocation country(String country) {
        this.country = country;
        return this;
    }

    /**
     * An ISO 3166-1 alpha-2 country code
     * @return country
     **/
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public GeoLocation latitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    /**
     * Latitude of the location in decimal degrees
     * @return latitude
     **/
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public GeoLocation longitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    /**
     * Longitude of the location in decimal degrees
     * @return longitude
     **/
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeoLocation geoLocation = (GeoLocation) o;
        return Objects.equals(this.city, geoLocation.city) && Objects.equals(this.country, geoLocation.country) &&
               Objects.equals(this.latitude, geoLocation.latitude) &&
               Objects.equals(this.longitude, geoLocation.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, country, latitude, longitude);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class GeoLocation {\n");

        sb.append("    city: ").append(toIndentedString(city)).append("\n");
        sb.append("    country: ").append(toIndentedString(country)).append("\n");
        sb.append("    latitude: ").append(toIndentedString(latitude)).append("\n");
        sb.append("    longitude: ").append(toIndentedString(longitude)).append("\n");
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
