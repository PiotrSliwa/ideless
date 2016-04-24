package com.github.ideless;

import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.List;

public class InitCommandHandler implements CommandHandler {

    private final SafeCommandHandler invalidParameterHandler;
    private final ManifestReader manifestReader;

    public InitCommandHandler(SafeCommandHandler invalidParameterHandler, ManifestReader manifestReader) {
        this.invalidParameterHandler = invalidParameterHandler;
        this.manifestReader = manifestReader;
    }

    @Override
    public void handle(List<String> parameters) throws Exception {
        if (parameters.isEmpty()) {
            invalidParameterHandler.handle(parameters);
            return;
        }
        try {
            Manifest manifest = manifestReader.read(parameters.get(0) + "/.ideless");
            if (manifest == null)
                throw new InvalidTemplateException("null manifest");
            if (manifest.getInitFiles() == null)
                throw new LackOfFieldException("initFiles");
        }
        catch (IOException ex) {
            throw new InvalidTemplateException(ex.getMessage());
        }
        catch (JsonSyntaxException ex) {
            throw new InvalidJsonException(ex.getMessage());
        }
    }

}
