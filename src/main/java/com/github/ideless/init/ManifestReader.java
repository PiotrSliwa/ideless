package com.github.ideless.init;

import com.github.ideless.FileIO;
import com.github.ideless.GsonWrapper;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.file.Path;

public class ManifestReader {

    private final FileIO fileIO;
    private final GsonWrapper gsonWrapper;

    public ManifestReader(FileIO fileIO, GsonWrapper gsonWrapper) {
        this.fileIO = fileIO;
        this.gsonWrapper = gsonWrapper;
    }

    public Manifest read(Path path) throws IOException, JsonSyntaxException {
        String data = fileIO.read(path);
        return gsonWrapper.fromJson(data, Manifest.class);
    }

}
