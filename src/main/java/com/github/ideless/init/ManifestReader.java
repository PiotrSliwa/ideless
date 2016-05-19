package com.github.ideless.init;

import com.github.ideless.FileIO;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.file.Path;

public class ManifestReader {

    private final FileIO fileIO;

    public ManifestReader(FileIO fileIO) {
        this.fileIO = fileIO;
    }

    public Manifest read(Path path) throws IOException, JsonSyntaxException {
        String data = fileIO.read(path);
        return new Gson().fromJson(data, Manifest.class);
    }

}
