package com.github.ideless.running;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Runner {

    public static String EXECUTABLE_PATH = "target/ideless.jar";

    public static String run(String args) throws ExecutableReturnedErrorException, RunnerException {
        try {
            Process proc = Runtime.getRuntime().exec("java -jar " + EXECUTABLE_PATH + " " + args);
            proc.waitFor();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(proc.getErrorStream()))) {
                String errorMsgs = br.lines().collect(Collectors.joining("\n"));
                if (!errorMsgs.isEmpty())
                    throw new ExecutableReturnedErrorException(errorMsgs);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                return br.lines().collect(Collectors.joining("\n"));
            }

        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
            throw new RunnerException();
        }
    }
}
