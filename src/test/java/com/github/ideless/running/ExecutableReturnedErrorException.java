package com.github.ideless.running;

/**
 *
 * @author psliwa
 */
public class ExecutableReturnedErrorException extends Exception {
    public ExecutableReturnedErrorException(String errorOuput) {
        super(errorOuput);
    }
}
