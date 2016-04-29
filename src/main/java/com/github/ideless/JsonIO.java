package com.github.ideless;

import com.google.gson.Gson;

public class JsonIO {
    private final static Gson GSON = new Gson();

    public String toJson(Object object) {
        return GSON.toJson(object);
    }
    
    public <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }
}
