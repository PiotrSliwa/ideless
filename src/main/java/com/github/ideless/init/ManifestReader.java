package com.github.ideless.init;

import com.github.ideless.FileIO;
import com.github.ideless.JsonIO;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.file.Path;

public class ManifestReader {

    private final FileIO fileIO;
    private final JsonIO jsonIO;

    public ManifestReader(FileIO fileIO, JsonIO jsonIO) {
        this.fileIO = fileIO;
        this.jsonIO = jsonIO;
    }

    public Manifest read(Path path) throws IOException, JsonSyntaxException {
        String data = fileIO.read(path);
        return jsonIO.fromJson(data, Manifest.class);
    }

}
