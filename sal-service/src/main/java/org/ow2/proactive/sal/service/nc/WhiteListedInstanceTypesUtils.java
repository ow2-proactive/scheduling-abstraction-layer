/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
