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

import java.io.*;

import org.apache.commons.io.IOUtils;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class TemporaryFilesHelper {

    private static final String TMP_SYS_PROPERTY = "java.io.tmpdir";

    private static final String TMP_DIRECTORY;

    private static final File TMP_DIRECTORY_FILE;

    static {
        TMP_DIRECTORY = ((System.getProperty(TMP_SYS_PROPERTY).endsWith(File.separator))
                                                                                         ? System.getProperty(TMP_SYS_PROPERTY) +
                                                                                           "proactive_tmp"
                                                                                         : System.getProperty(TMP_SYS_PROPERTY) +
                                                                                           File.separator +
                                                                                           "proactive_tmp");
        TMP_DIRECTORY_FILE = new File(TMP_DIRECTORY);
        boolean result = TMP_DIRECTORY_FILE.mkdirs();
        if (result) {
            LOGGER.info("Temporary directory created successfully");
        } else {
            LOGGER.warn("Temporary directory couldn't be created");
        }
    }

    public static File createTempFile(String prefix, String suffix) throws IOException {
        File newFile = File.createTempFile(prefix, suffix, TMP_DIRECTORY_FILE);
        newFile.deleteOnExit();
        LOGGER.info("Temporary file " + newFile.getAbsolutePath() + " created successfully");
        return newFile;
    }

    public static File createTempFile(String prefix, String suffix, InputStream inStream) throws IOException {
        File newFile = createTempFile(prefix, suffix);
        byte[] buffer = new byte[inStream.available()];
        inStream.read(buffer);
        try (OutputStream outStream = new FileOutputStream(newFile)) {
            outStream.write(buffer);
            IOUtils.closeQuietly(inStream);
            IOUtils.closeQuietly(outStream);
        }
        LOGGER.info("Temporary file " + newFile.getAbsolutePath() + " filled successfully with inputStream");
        return newFile;
    }

    public static File createTempFile(String filename, InputStream inStream) throws IOException {
        String prefix = filename.split("[.]")[0];
        String suffix = filename.substring(filename.indexOf("."));
        return createTempFile(prefix, suffix, inStream);
    }

    public static File createTempFileFromResource(String filename) throws IOException {
        return createTempFile(filename.trim().substring(filename.lastIndexOf(File.separator) + 1),
                              TemporaryFilesHelper.class.getResourceAsStream(filename));
    }

    public static void delete(File fileToDelete) {
        if (fileToDelete != null) {
            boolean result = fileToDelete.delete();
            if (result) {
                LOGGER.info("Temporary file deleted successfully");
            } else {
                LOGGER.warn("Temporary file couldn't be deleted");
            }
        }
    }
}
