/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.ow2.proactive.sal.util.ModelUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Part of Location Represents a geographical location
 */
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class GeoLocation implements Serializable {

    // JSON field names as constants
    public static final String JSON_CITY = "city";

    public static final String JSON_COUNTRY = "country";

    public static final String JSON_LATITUDE = "latitude";

    public static final String JSON_LONGITUDE = "longitude";

    @Column(name = "CITY")
    @JsonProperty(JSON_CITY)
    private String city = null;

    @Column(name = "COUNTRY")
    @JsonProperty(JSON_COUNTRY)
    private String country = null;

    @Column(name = "LATITUDE")
    @JsonProperty(JSON_LATITUDE)
    private Double latitude = null;

    @Column(name = "LONGITUDE")
    @JsonProperty(JSON_LONGITUDE)
    private Double longitude = null;

    public GeoLocation(GeoLocationData geoLocationData) {
        this.city = geoLocationData.getCity();
        this.country = geoLocationData.getCountry();
        this.latitude = geoLocationData.getLatitude();
        this.longitude = geoLocationData.getLongitude();
    }

    @Override
    public String toString() {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put(JSON_CITY, city);
        fields.put(JSON_COUNTRY, country);
        fields.put(JSON_LATITUDE, latitude);
        fields.put(JSON_LONGITUDE, longitude);

        return ModelUtils.buildToString(GeoLocation.class.getSimpleName(), fields);
    }
}
