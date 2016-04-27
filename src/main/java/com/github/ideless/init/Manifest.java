package com.github.ideless.init;

import java.util.List;

public class Manifest {

    private List<String> initFiles;
    private List<Property> properties;

    public Manifest() { }

    public Manifest(List<String> initFiles) {
        this.initFiles = initFiles;
    }

    public Manifest(List<String> initFiles, List<Property> properties) {
        this.initFiles = initFiles;
        this.properties = properties;
    }

    public List<String> getInitFiles() {
        return initFiles;
    }

    public List<Property> getProperties() {
        return properties;
    }
}
