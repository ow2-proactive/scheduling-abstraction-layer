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
package org.ow2.proactive.sal.service.nc;

import lombok.extern.log4j.Log4j2;


@Log4j2
public class WhiteListedInstanceTypesUtils {

    private WhiteListedInstanceTypesUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Says if a hardware type's family is white listed for a specific cloud provider
     * @param instanceType The instance type to check
     * @return True if the instance type family is while listed, false otherwise
     */
    public static boolean isHandledHardwareInstanceType(String instanceType) {
        return AwsWhiteListedInstanceTypes.isAwsWhiteListedHardwareInstanceType(instanceType);
    }
}
