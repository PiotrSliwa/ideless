package com.github.ideless;

public class InvalidJsonException extends Exception {

    public InvalidJsonException(String details) {
        super("Invalid JSON (" + details + ")");
    }
}
