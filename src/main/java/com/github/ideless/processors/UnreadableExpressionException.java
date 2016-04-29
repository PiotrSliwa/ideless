package com.github.ideless.processors;

public class UnreadableExpressionException extends Exception {

    UnreadableExpressionException(String expression) {
        super("Unreadable expression: '" + expression + "'");
    }

}
