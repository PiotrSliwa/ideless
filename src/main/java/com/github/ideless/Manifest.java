package com.github.ideless;

import java.util.List;

public class Manifest {
    private List<String> initFiles;

    public Manifest() {
    }

    public Manifest(List<String> initFiles) {
        this.initFiles = initFiles;
    }

    public List<String> getInitFiles() {
        return initFiles;
    }
}
