package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.Map;
import java.util.LinkedHashMap;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ow2.proactive.sal.util.ModelUtils;

/**
 * Node candidate environment
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
@EqualsAndHashCode
public class Environment implements Serializable {

    // JSON Constants
    public static final String JSON_RUNTIME = "runtime";

    @Column(name = "RUNTIME")
    @Enumerated(EnumType.STRING)
    @JsonProperty(JSON_RUNTIME)
    private Runtime runtime = null;

    @Override
    public String toString() {
        // Using LinkedHashMap to preserve field order
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put(JSON_RUNTIME, runtime);

        return ModelUtils.buildToString(Environment.class.getSimpleName(), fields);
    }
}
