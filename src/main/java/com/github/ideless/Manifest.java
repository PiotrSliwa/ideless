package com.github.ideless;

import java.util.List;

public class Manifest {

    public static class Property {
        private String name;
        private String description;

        public Property() { }

        public Property(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

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
