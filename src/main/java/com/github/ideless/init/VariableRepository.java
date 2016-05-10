package com.github.ideless.init;

import com.github.ideless.processors.VariableGetter;
import java.util.HashMap;
import java.util.Map;

public class VariableRepository implements VariableGetter {

    private static final String PROPERTY_NAMESPACE = "properties.";

    private final Map<String, Object> variables = new HashMap<>();

    public void setProperty(String name, String value) {
        variables.put(PROPERTY_NAMESPACE + name, value);
    }

    @Override
    public Object get(String variable) {
        return variables.get(variable);
    }

}
