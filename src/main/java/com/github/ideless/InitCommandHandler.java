package com.github.ideless;

import java.util.List;

public class InitCommandHandler implements CommandHandler {

    private final SafeCommandHandler invalidParameterHandler;
    private final TemplateReader templateReader;

    public InitCommandHandler(SafeCommandHandler invalidParameterHandler, TemplateReader templateReader) {
        this.invalidParameterHandler = invalidParameterHandler;
        this.templateReader = templateReader;
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
