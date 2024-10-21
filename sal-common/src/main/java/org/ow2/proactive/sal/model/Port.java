/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Getter
@Setter
@Entity
@Table(name = "PORT")
public class Port implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    @Column(name = "PORT_ID")
    private Integer portId;

    @JsonIgnore
    @Column(name = "NAME")
    private String name;

    @Column(name = "VALUE")
    private Integer value;

    @JsonIgnore
    @Column(name = "REQUESTED_NAME")
    private String requestedName;

    @JsonIgnore
    @Column(name = "REQUIRING_COMPONENT_NAME")
    private String requiringComponentName;

    public Port(String name, Integer value) {
        this.name = name;
        if ((value == -1) || (value >= 0 && value <= 65535)) {
            this.value = value;
        } else {
            throw new IllegalArgumentException(String.format("Invalid port value provided: %d", value));
        }
    }

    public String serializeToVariableMap() {
        return "{" + "\"requiringComponentName\":\"" + this.requiringComponentName + "\"" + "," +
               "\"requiringPortName\":\"" + this.requestedName + "\"" + "," + "\"portValue\":\"" + this.value + "\"" +
               "}";
    }
}
