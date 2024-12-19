/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.util;

import java.util.Map;


public class ModelUtils {

    /**
     * Builds a formatted string representation of an object, given its class name and fields.
     * This method takes the class name and a map of field names and their respective values,
     * and constructs a formatted string that represents the object in a human-readable way.
     *
     * @param className The name of the class to be included in the string representation.
     * @param fields A map of field names (as keys) and field values (as values) to be included.
     * @return A formatted string representing the class and its fields.
     */
    public static String buildToString(String className, Map<String, Object> fields) {
        StringBuilder sb = new StringBuilder();
        sb.append("class ").append(className).append(" {\n");
        fields.forEach((key, value) -> sb.append("    ")
                                         .append(key)
                                         .append(": ")
                                         .append(toIndentedString(value))
                                         .append("\n"));
        sb.append("}");
        return sb.toString();
    }

    /**
     * Indents multi-line strings and ensures proper formatting for null values.
     * This method takes an object, converts it to a string, and ensures that each line in
     * the string (if multi-line) is indented by 4 spaces. If the object is null, it returns "null".
     *
     * @param o The object to be converted into an indented string.
     * @return The indented string representation of the object, or "null" if the object is null.
     */
    public static String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
