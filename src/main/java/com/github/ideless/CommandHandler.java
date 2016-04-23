package com.github.ideless;

import java.util.List;

@FunctionalInterface
public interface CommandHandler {
    public void handle(List<String> parameters) throws Exception;
}
