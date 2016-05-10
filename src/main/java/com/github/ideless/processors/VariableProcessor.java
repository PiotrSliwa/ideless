package com.github.ideless.processors;

import com.github.ideless.JsonIO;

public class VariableProcessor {

    private final VariableGetter variableGetter;
    private final JsonIO jsonIO;

    public VariableProcessor(VariableGetter variableGetter, JsonIO jsonIO) {
        this.variableGetter = variableGetter;
        this.jsonIO = jsonIO;
    }

    public String process(String variable) throws Exception {
        if (variable == null || variable.trim().isEmpty())
            throw new EmptyVariableNameException();
        Object object = variableGetter.get(variable);
        if (object == null)
            throw new UndefinedVariableException(variable);
        return jsonIO.toJson(object);
    }

}
