package com.github.ideless;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathsCreator {

    public Path createUserHome() {
        return Paths.get(System.getProperty("user.home")).resolve(".ideless");
    }

}
