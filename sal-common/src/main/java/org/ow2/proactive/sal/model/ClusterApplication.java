/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.util.Arrays;
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

    // JSON field constants
    public static final String JSON_APP_NAME = "appName";

    public static final String JSON_CLUSTER_NAME = "clusterName";

    public static final String JSON_APP_FILE = "appFile";

    public static final String JSON_PACKAGE_MANAGER = "packageManager";

    public static final String JSON_ACTION = "action";

    public static final String JSON_FLAGS = "flags";

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String applicationId = null;

    @JsonProperty(JSON_APP_NAME)
    private String appName = null;

    private String clusterName = null;

    // TODO: change this
    private PackageManagerEnum yamlManager = null;

    @JsonProperty(JSON_APP_FILE)
    private String appFile = null;

    @JsonProperty(JSON_PACKAGE_MANAGER)
    private String packageManager = null;

    @JsonProperty(JSON_ACTION)
    private String action = null;

    @JsonProperty(JSON_FLAGS)
    private String flags = "";

    @Getter
    public enum PackageManagerEnum {
        HELM("helm", "helm upgrade --install"),
        KUBECTL("kubectl", "kubectl apply -f "),
        KUBEVELA("kubevela", "vela up -f ");

        private final String name;

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
