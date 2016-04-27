package com.github.ideless.init;

public class LackOfFieldException extends Exception {

    public LackOfFieldException(String fieldName) {
        super("Lack of '" + fieldName + "' field");
    }
}
