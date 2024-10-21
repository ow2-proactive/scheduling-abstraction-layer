/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ow2.proactive.sal.service.service.application.PAFactory;

import lombok.extern.log4j.Log4j2;


@Log4j2
public class Utils {

    private Utils() {
    }

    public static String getContentWithFileName(String fileName) {
        String script;
        String newLine = System.getProperty("line.separator");
        String scriptFileNameWithSeparator = (fileName.startsWith(File.separator)) ? fileName
                                                                                   : File.separator + fileName;
        LOGGER.debug("Creating a simple script from the file : " + scriptFileNameWithSeparator);
        try (Stream<String> lines = new BufferedReader(new InputStreamReader(PAFactory.class.getResourceAsStream(scriptFileNameWithSeparator))).lines()) {
            script = lines.collect(Collectors.joining(newLine));
        }
        return script;
    }
}
