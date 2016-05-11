package com.github.ideless.init;

import java.util.List;

public class Manifest {

    private List<String> initFiles;
    private List<Property> properties;
    private List<String> expressionFormat;

    public Manifest() { }

    public Manifest(List<String> initFiles) {
        this.initFiles = initFiles;
    }

    public Manifest(List<String> initFiles, List<Property> properties) {
        this.initFiles = initFiles;
        this.properties = properties;
    }

    public Manifest(List<String> initFiles, List<Property> properties, List<String> expressionFormat) {
        this.initFiles = initFiles;
        this.properties = properties;
        this.expressionFormat = expressionFormat;
    }

    public List<String> getInitFiles() {
        return initFiles;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public List<String> getExpressionFormat() {
        return expressionFormat;
    }
}
