package com.github.ideless.processors;

import com.github.ideless.JsonIO;

public class VariableProcessor {

    private final VariableRepository variableRepository;
    private final JsonIO jsonIO;

    public VariableProcessor(VariableRepository variableRepository, JsonIO jsonIO) {
        this.variableRepository = variableRepository;
        this.jsonIO = jsonIO;
    }

    public String process(String variable) throws Exception {
        if (variable == null || variable.trim().isEmpty())
            throw new EmptyVariableNameException();
        Object object = variableRepository.get(variable);
        if (object == null)
            throw new UndefinedVariableException(variable);
        return jsonIO.toJson(object);
    }

}
