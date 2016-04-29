package com.github.ideless.processors;

public class UndefinedVariableException extends Exception {

    public UndefinedVariableException(String variable) {
        super("Undefined variable: '" + variable + "'");
    }

}
