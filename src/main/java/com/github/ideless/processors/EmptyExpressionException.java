package com.github.ideless.processors;

public class EmptyExpressionException extends Exception {

    public EmptyExpressionException() {
        super("Empty expression found in template file");
    }

}
