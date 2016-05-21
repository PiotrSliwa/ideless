package com.github.ideless;

import java.nio.file.Path;

public class Template {

    public static enum Type {
        Local,
        UserHome
    }

    private final Path path;
    private final Type type;

    public Template(Path path, Type type) {
        this.path = path;
        this.type = type;
    }

    public Path getPath() {
        return path;
    }

    public Type getType() {
        return type;
    }

}
