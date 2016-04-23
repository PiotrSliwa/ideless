package com.github.ideless;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandDispatcher {

    private final SafeCommandHandler defaultHandler;
    private final SafeCommandHandler errorHandler;
    private final Map<String, CommandHandler> handlers = new HashMap<>();

    public CommandDispatcher(SafeCommandHandler defaultHandler, SafeCommandHandler errorHandler) {
        this.defaultHandler = defaultHandler;
        this.errorHandler = errorHandler;
    }

    void addHandler(String commandName, CommandHandler commandHandler) {
        handlers.put(commandName, commandHandler);
    }

    public void dispatch(String[] args) {
        if (args == null || args.length == 0) {
            handleDefault(Arrays.asList());
            return;
        }
        CommandHandler handler = handlers.get(args[0]);
        if (handler == null) {
            handleDefault(parseParameters(args));
            return;
        }
        try {
            handler.handle(parseParameters(args));
        } catch (Exception ex) {
            errorHandler.handle(Arrays.asList(ex.getMessage()));
        }
    }

    private static List<String> parseParameters(String[] args) {
        return Arrays.asList(args).subList(1, args.length);
    }

    private void handleDefault(List<String> parameters) {
        defaultHandler.handle(parameters);
    }

}
