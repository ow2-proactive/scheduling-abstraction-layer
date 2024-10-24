/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ow2.proactive.sal.model.GeoLocationData;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;


@Getter
@Setter
@Log4j2
public class GeoLocationUtils {

    private final List<GeoLocationData> cloudsGeoLocationData;

    public GeoLocationUtils() {
        cloudsGeoLocationData = chargeCloudGLsDB();
        LOGGER.debug("Clouds GeoLocation Database loaded successfully.");
    }

    private List<GeoLocationData> chargeCloudGLsDB() {
        LOGGER.info("Charging Cloud GeoLocations DB ...");
        List<GeoLocationData> cloudsGLDB = new LinkedList<>();
        File input = null;
        try {
            input = TemporaryFilesHelper.createTempFileFromResource(File.separator + "db_cloud_regions.csv");
            CsvSchema csv = CsvSchema.emptySchema().withHeader().withColumnSeparator(' ').withNullValue("");
            CsvMapper csvMapper = new CsvMapper();
            ObjectReader objectReader = csvMapper.readerFor(Map.class).with(csv);
            LOGGER.debug("Reading GeoLocation values from objectReader: " + objectReader);
            MappingIterator<Map<?, ?>> mappingIterator = objectReader.readValues(input);
            List<Map<?, ?>> list = mappingIterator.readAll();
            LOGGER.debug("List of GeoLocations got: " + list);
            list.forEach(map -> cloudsGLDB.add(new GeoLocationData(map.get("CITY").toString(),
                                                                   map.get("COUNTRY").toString(),
                                                                   Double.valueOf(map.get("LATITUDE").toString()),
                                                                   Double.valueOf(map.get("LONGITUDE").toString()),
                                                                   map.get("REGION").toString(),
                                                                   map.get("CLOUD").toString())));
            LOGGER.info("Cloud GeoLocations DB loaded successfully: " + cloudsGLDB);
        } catch (IOException ioe) {
            LOGGER.error("Charging the Geolocation database failed due to: " + Arrays.toString(ioe.getStackTrace()));
        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred while processing GeoLocation data: " + e.getMessage(), e);
        }

        TemporaryFilesHelper.delete(input);
        return cloudsGLDB;
    }

    public GeoLocationData findGeoLocation(String cloud, String region) {
        return cloudsGeoLocationData.stream()
                                    .filter(cloudGL -> cloud.equals(cloudGL.getCloud()) &&
                                                       region.equals(cloudGL.getRegion()))
                                    .findAny()
                                    .orElse(new GeoLocationData());
    }
}
