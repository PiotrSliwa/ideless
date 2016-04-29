package com.github.ideless.init;

import com.github.ideless.CommandHandler;
import com.github.ideless.SafeCommandHandler;
import com.github.ideless.UserIO;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.List;

public class InitCommandHandler implements CommandHandler {

    private final SafeCommandHandler invalidParameterHandler;
    private final ManifestReader manifestReader;
    private final UserIO userIO;
    private final FileInitializer fileInitializer;

    public InitCommandHandler(SafeCommandHandler invalidParameterHandler, ManifestReader manifestReader, UserIO userIO, FileInitializer fileInitializer) {
        this.invalidParameterHandler = invalidParameterHandler;
        this.manifestReader = manifestReader;
        this.userIO = userIO;
        this.fileInitializer = fileInitializer;
    }

    @Override
    public void handle(List<String> parameters) throws Exception {
        if (parameters.isEmpty()) {
            invalidParameterHandler.handle(parameters);
            return;
        }
        String templateDir = getTemplateDir(parameters);
        Manifest manifest = readManifest(templateDir);
        initProperties(manifest);
        initFiles(manifest, templateDir);
    }

    private static String getTemplateDir(List<String> parameters) {
        return parameters.get(0);
    }

    private void initProperties(Manifest manifest) {
        if (manifest.getProperties() == null)
            return;
        manifest.getProperties().stream().forEach((property) -> {
            askUserForProperty(property);
        });
    }

    private void askUserForProperty(Property property) {
        userIO.print(property.getName() + " (" + property.getDescription() + "): ");
    }

    private void initFiles(Manifest manifest, String templateDir) throws Exception {
        for (String path : manifest.getInitFiles()) {
            try {
                fileInitializer.initialize(templateDir + "/" + path, path);
                userIO.println("Initializing file: " + path);
            }
            catch (IOException ex) {
                throw new CannotFindFileException(path);
            }
        }
    }

    private Manifest readManifest(String templateDir) throws Exception {
        try {
            Manifest manifest = manifestReader.read(templateDir + "/.ideless");
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
