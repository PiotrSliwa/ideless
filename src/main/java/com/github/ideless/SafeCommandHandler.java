package com.github.ideless;

import java.util.List;

@FunctionalInterface
public interface SafeCommandHandler {
    public void handle(List<String> parameters);
}
