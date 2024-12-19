package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.ow2.proactive.sal.util.ModelUtils; // Assuming this is the correct import for ModelUtils

/**
 * Represents the configuration of a cloud.
 */
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Getter
@Setter
@EqualsAndHashCode(of = {CloudConfiguration.JSON_NODE_GROUP, CloudConfiguration.JSON_PROPERTIES})
public class CloudConfiguration implements Serializable {

    // JSON field constants
    public static final String JSON_NODE_GROUP = "nodeGroup";
    public static final String JSON_PROPERTIES = "properties";

    @Column(name = "NODE_GROUP")
    @JsonProperty(JSON_NODE_GROUP)
    private String nodeGroup = null;

    @Column(name = "PROPERTIES")
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @JsonProperty(JSON_PROPERTIES)
    private Map<String, String> properties = null;

    /**
     * Custom toString() method for the CloudConfiguration class using ModelUtils.
     * This method uses {@link ModelUtils#buildToString} to generate the string representation of the object.
     * @return A formatted string representation of the CloudConfiguration object.
     */
    @Override
    public String toString() {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put(JSON_NODE_GROUP, nodeGroup);
        fields.put(JSON_PROPERTIES, properties);

        return ModelUtils.buildToString(CloudConfiguration.class.getSimpleName(), fields);
    }


    @PreRemove
    private void cleanMappedDataFirst() {
        this.properties.clear();
    }
}
