package com.github.ideless;

import com.github.ideless.init.ManifestReader;
import com.github.ideless.init.InitCommandHandler;
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
        ManifestReader manifestReader = new ManifestReader(fileIO);
        CommandDispatcher dispatcher = new CommandDispatcher(defaultHandler, errorHandler);
        dispatcher.addHandler("init", new InitCommandHandler(defaultHandler, manifestReader, fileIO, userIO));
        dispatcher.dispatch(args);

    }

}
