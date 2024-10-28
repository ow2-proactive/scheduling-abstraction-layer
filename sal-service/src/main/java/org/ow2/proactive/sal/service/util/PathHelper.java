/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.util;

public class PathHelper {

    private final static String propertiesFileName = "eu.morphemic.schedulingAbstractionLayer.properties";

    // Environment Variable Name = MELODIC_CONFIG_DIR
    private final static String propertiesFileEnvironmentVariableName = "MELODIC_CONFIG_DIR";

    public static String getPersistencePropertiesFilePath() {
        String path = System.getenv(propertiesFileEnvironmentVariableName);
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path + "/" + propertiesFileName;
    }
}
