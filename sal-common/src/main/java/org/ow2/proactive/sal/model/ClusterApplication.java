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
package org.ow2.proactive.sal.model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClusterApplication {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String applicationId = null;

    @JsonProperty("appName")
    private String appName = null;

    private String clusterName = null;

    //TODO    change this
    private PackageManagerEnum yamlManager = null;

    @JsonProperty("appFile")
    private String appFile = null;

    @JsonProperty("packageManager")
    private String packageManager = null;

    @JsonProperty("action")
    private String action = null;

    @JsonProperty("flags")
    private String flags = "";

    public enum PackageManagerEnum {
        HELM("helm", "helm upgrade --install"),
        KUBECTL("kubectl", "kubectl apply -f "),
        KUBEVELA("kubevela", "vela up -f ");

        @Getter
        private final String name;

        @Getter
        private final String command;

        PackageManagerEnum(String name, String command) {
            this.name = name;
            this.command = command;
        }

        public static PackageManagerEnum getPackageManagerEnumByName(String name) {
            Optional<PackageManagerEnum> packageManager = Arrays.stream(PackageManagerEnum.values())
                                                                .filter(pm -> pm.name.equals(name))
                                                                .findFirst();
            return packageManager.orElse(null);
        }

    }
}
