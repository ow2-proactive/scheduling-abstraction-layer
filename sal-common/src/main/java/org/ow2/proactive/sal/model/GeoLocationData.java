/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GeoLocationData {
    private String city;

    private String country;

    private Double latitude;

    private Double longitude;

    private String region;

    private String cloud;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeoLocationData geoLocation = (GeoLocationData) o;
        return Objects.equals(this.cloud, geoLocation.cloud) && Objects.equals(this.region, geoLocation.region);
    }

    public String toString() {
        return "GeoLocationData{" + GeoLocation.JSON_CITY + "='" + city + '\'' + ", " + GeoLocation.JSON_COUNTRY +
               "='" + country + '\'' + ", " + GeoLocation.JSON_LATITUDE + "=" + latitude + ", " +
               GeoLocation.JSON_LONGITUDE + "=" + longitude + ", region='" + region + '\'' + ", cloud='" + cloud +
               '\'' + '}';
    }

}
