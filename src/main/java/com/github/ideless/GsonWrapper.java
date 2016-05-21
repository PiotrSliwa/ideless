package com.github.ideless;

import com.google.gson.Gson;

public class GsonWrapper {

    private static final Gson gson = new Gson();

    public <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

}
