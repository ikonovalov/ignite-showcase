package ru.codeunited.ignite.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class IgniteNodeAttributes {

    private Map<String, String> attributes = new HashMap<>();

    private IgniteNodeAttributes() {
        super();
    }

    public static IgniteNodeAttributes newInstance() {
        return new IgniteNodeAttributes();
    }

    public IgniteNodeAttributes attr(String key, String value) {
        attributes.put(key, value);
        return this;
    }

    public Map<String, String> toMap() {
        return Collections.unmodifiableMap(attributes);
    }
}
