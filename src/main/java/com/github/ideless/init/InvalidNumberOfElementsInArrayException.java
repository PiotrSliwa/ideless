package com.github.ideless.init;

public class InvalidNumberOfElementsInArrayException extends Exception {

    public InvalidNumberOfElementsInArrayException(String name, int required) {
        super("Invalid number of elements in array '" + name + "' (required: " + Integer.toString(required) + ")");
    }
}
