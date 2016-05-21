package com.github.ideless.init;

import com.github.ideless.FileIO;
import com.github.ideless.JsonIO;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.file.Path;

public class ManifestReader {

    private static final int EXPRESSION_FORMAT_SIZE = 3;

    private final FileIO fileIO;
    private final JsonIO jsonIO;

    public ManifestReader(FileIO fileIO, JsonIO jsonIO) {
        this.fileIO = fileIO;
        this.jsonIO = jsonIO;
    }

    public Manifest read(Path path) throws Exception {
        String data;
        try {
            data = fileIO.read(path);
        } catch (IOException ex) {
            throw new InvalidTemplateException(ex.getMessage());
        }
        try {
            Manifest manifest = jsonIO.fromJson(data, Manifest.class);
            validate(manifest);
            return manifest;
        } catch (JsonSyntaxException ex) {
            throw new InvalidJsonException(ex.getMessage());
        }
    }

    private void validate(Manifest manifest) throws InvalidTemplateException, LackOfFieldException, InvalidNumberOfElementsInArrayException {
        if (manifest == null)
            throw new InvalidTemplateException("null manifest");
        if (manifest.getInitFiles() == null)
            throw new LackOfFieldException("initFiles");
        if (manifest.getExpressionFormat() != null && manifest.getExpressionFormat().size() != EXPRESSION_FORMAT_SIZE)
            throw new InvalidNumberOfElementsInArrayException("expressionFormat", EXPRESSION_FORMAT_SIZE);
    }

}
