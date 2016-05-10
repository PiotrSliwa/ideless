package com.github.ideless.running;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Runner {

    private static final String EXECUTABLE_RELATIVE_PATH = "target/ideless.jar";

    private final String workspacePath;
    private final String executablePath;
    private final List<String> inputs = new ArrayList<>();

    public Runner(String workspacePath) {
        this.workspacePath = workspacePath;
        executablePath = generatePrefixedPathFromMainDir(workspacePath, EXECUTABLE_RELATIVE_PATH);
    }

    private static String generatePrefixedPathFromMainDir(String fromPath, String relativePath) {
        String result = "";
        for (int i = fromPath.split("\\/").length; i > 0; --i)
            result += "../";
        return result + relativePath;
    }

    public void addInput(String input) {
        inputs.add(input + "\n");
    }

    public String run(String args) throws ExecutableReturnedErrorException, RunnerException {
        try {
            ArrayList<String> command = new ArrayList<>(Arrays.asList("java", "-jar", executablePath));
            command.addAll(Arrays.asList(args.split(" ")));
            Process proc = new ProcessBuilder(command).directory(new File(workspacePath)).start();
            writeInputsToStream(proc.getOutputStream());
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

    private void writeInputsToStream(OutputStream outputStream) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
        for (String input : inputs) {
            bw.write(input);
            bw.newLine();
        }
        bw.flush();
    }
}
