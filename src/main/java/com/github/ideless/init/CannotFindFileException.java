package com.github.ideless.init;

public class CannotFindFileException extends Exception {

    public CannotFindFileException(String filename) {
        super("Cannot find file '" + filename + "'");
    }

}
