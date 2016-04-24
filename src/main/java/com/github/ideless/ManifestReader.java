package com.github.ideless;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;

public class ManifestReader {

    private final FileIO fileIO;

    public ManifestReader(FileIO fileIO) {
        this.fileIO = fileIO;
    }

    public Manifest read(String path) throws IOException, JsonSyntaxException {
        String data = fileIO.read(path);
        return new Gson().fromJson(data, Manifest.class);
    }

}
