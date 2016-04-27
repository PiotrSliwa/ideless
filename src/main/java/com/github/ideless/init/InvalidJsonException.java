package com.github.ideless.init;

public class InvalidJsonException extends Exception {

    public InvalidJsonException(String details) {
        super("Invalid JSON (" + details + ")");
    }
}
