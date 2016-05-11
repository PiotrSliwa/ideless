package com.github.ideless;

import com.github.ideless.init.FileInitializer;
import com.github.ideless.init.InitCommandHandler;
import com.github.ideless.init.ManifestReader;
import com.github.ideless.init.VariableRepository;
import com.github.ideless.processors.ContentProcessor;
import com.github.ideless.processors.ExpressionProcessor;
import com.github.ideless.processors.VariableProcessor;
import java.util.List;

public class Ideless {

    public static void main(String[] args) {

        SafeCommandHandler defaultHandler = (List<String> arguments) -> {
            System.out.println("Usage: ");
            System.out.println("    ideless [command [parameters]]");
            System.out.println("    Commands:");
            System.out.println("        init - initializes new project");
            System.out.println("               Parameters: [TEMPLATE_DIRECTORY]");
        };

        SafeCommandHandler errorHandler = (List<String> arguments) -> {
            System.out.println("Error: " + arguments.get(0));
        };

        FileIO fileIO = new FileIO();
        UserIO userIO = new UserIO();
        JsonIO jsonIO = new JsonIO();
        VariableRepository variableRepository = new VariableRepository();
        VariableProcessor variableProcessor = new VariableProcessor(variableRepository, jsonIO);
        ExpressionProcessor expressionProcessor = new ExpressionProcessor(variableProcessor);
        ContentProcessor contentProcessor = new ContentProcessor(expressionProcessor);
        ManifestReader manifestReader = new ManifestReader(fileIO);
        FileInitializer fileInitializer = new FileInitializer(fileIO, contentProcessor);
        CommandDispatcher dispatcher = new CommandDispatcher(defaultHandler, errorHandler);
        dispatcher.addHandler("init", new InitCommandHandler(defaultHandler, manifestReader, userIO, fileInitializer, variableRepository, contentProcessor));
        dispatcher.dispatch(args);

    }

}
