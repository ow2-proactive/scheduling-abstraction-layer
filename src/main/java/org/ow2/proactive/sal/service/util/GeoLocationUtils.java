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
