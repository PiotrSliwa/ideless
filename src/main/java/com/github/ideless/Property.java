package com.github.ideless;

public class Property {
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
