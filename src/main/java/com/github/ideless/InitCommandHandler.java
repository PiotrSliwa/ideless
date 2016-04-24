package com.github.ideless;

import java.io.IOException;
import java.util.List;

public class InitCommandHandler implements CommandHandler {

    private final SafeCommandHandler invalidParameterHandler;
    private final FileIO fileIO;

    public InitCommandHandler(SafeCommandHandler invalidParameterHandler, FileIO templateReader) {
        this.invalidParameterHandler = invalidParameterHandler;
        this.fileIO = templateReader;
    }

    @Override
    public void handle(List<String> parameters) throws Exception {
        if (parameters.isEmpty()) {
            invalidParameterHandler.handle(parameters);
            return;
        }
        String manifest = readManifest(parameters);
        throw new InvalidJsonException("");
    }

    private String readManifest(List<String> parameters) throws InvalidTemplateException {
        try {
            return fileIO.read(parameters.get(0) + "/.ideless");
        } catch (IOException ex) {
            throw new InvalidTemplateException(ex.getMessage());
        }
    }

}
