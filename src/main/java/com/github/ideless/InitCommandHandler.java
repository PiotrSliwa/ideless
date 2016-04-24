package com.github.ideless;

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
        throw new InvalidTemplateException();
    }

}
