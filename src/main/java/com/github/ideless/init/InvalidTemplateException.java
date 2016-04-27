package com.github.ideless.init;

public class InvalidTemplateException extends Exception {

    public InvalidTemplateException(String details) {
        super("Invalid ideless template directory (" + details + ")");
    }

}
