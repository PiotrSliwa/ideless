package com.github.ideless;

import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.List;

public class InitCommandHandler implements CommandHandler {

    private final SafeCommandHandler invalidParameterHandler;
    private final ManifestReader manifestReader;
    private final FileIO fileIO;

    public InitCommandHandler(SafeCommandHandler invalidParameterHandler, ManifestReader manifestReader, FileIO fileIO) {
        this.invalidParameterHandler = invalidParameterHandler;
        this.manifestReader = manifestReader;
        this.fileIO = fileIO;
    }

    @Override
    public void handle(List<String> parameters) throws Exception {
        if (parameters.isEmpty()) {
            invalidParameterHandler.handle(parameters);
            return;
        }
        Manifest manifest = readManifest(parameters);
        for (String path : manifest.getInitFiles()) {
            try {
                String data = fileIO.read(parameters.get(0) + "/" + path);
                System.out.println("Initializing file: " + path);
                fileIO.write(path, data);
            }
            catch (IOException ex) {
                throw new CannotFindFileException(path);
            }
        }
    }

    private Manifest readManifest(List<String> parameters) throws Exception {
        try {
            Manifest manifest = manifestReader.read(parameters.get(0) + "/.ideless");
            validate(manifest);
            return manifest;
        }
        catch (IOException ex) {
            throw new InvalidTemplateException(ex.getMessage());
        }
        catch (JsonSyntaxException ex) {
            throw new InvalidJsonException(ex.getMessage());
        }
    }

    private void validate(Manifest manifest) throws Exception {
        if (manifest == null)
            throw new InvalidTemplateException("null manifest");
        if (manifest.getInitFiles() == null)
            throw new LackOfFieldException("initFiles");
    }

}
