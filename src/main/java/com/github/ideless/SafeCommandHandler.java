package com.github.ideless;

import java.util.List;

@FunctionalInterface
public interface SafeCommandHandler {
    void handle(List<String> parameters);
}
