/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jclouds.ec2.domain.InstanceType;

import lombok.extern.log4j.Log4j2;


@Log4j2
public class JCloudsInstancesUtils {

    private static final Set<String> handledAWSInstanceTypes;

    static {
        handledAWSInstanceTypes = new HashSet<>();
        Arrays.stream(InstanceType.class.getFields()).forEach(field -> {
            try {
                handledAWSInstanceTypes.add(field.get(InstanceType.class).toString());
            } catch (IllegalAccessException e) {
                LOGGER.warn(String.valueOf(e.getStackTrace()));
            }
        });
    }

    private JCloudsInstancesUtils() {
    }

    /**
     * Some hardware instance types (like t3a and t4g for AWS) are not YET, handled by jclouds.
     * This method is here to check if the instance type is handled or not.
     * @param providerName The Cloud provider id
     * @param instanceType The instance type to be checked
     * @return true if the instance type is handled by jclouds, false otherwise
     */
    public static boolean isHandledHardwareInstanceType(String providerName, String instanceType) {
        if ("aws-ec2".equals(providerName)) {
            return handledAWSInstanceTypes.contains(instanceType);
        }
        // TODO: To check if for other cloud providers all instance types are handled by JClouds
        // not all instance types are handled (they can be excluded from findNodeCandidate by hardware/name attribute
        return true;
    }
}
