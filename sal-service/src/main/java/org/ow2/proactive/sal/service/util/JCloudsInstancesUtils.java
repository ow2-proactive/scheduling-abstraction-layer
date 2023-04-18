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
        return true;
    }
}
