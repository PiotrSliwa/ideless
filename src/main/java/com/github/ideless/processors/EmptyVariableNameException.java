package com.github.ideless.processors;

public class EmptyVariableNameException extends Exception {

    public EmptyVariableNameException() {
        super("Empty variable name");
    }
}
