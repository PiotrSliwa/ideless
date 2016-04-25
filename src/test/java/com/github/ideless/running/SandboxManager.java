package com.github.ideless.running;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SandboxManager {

    private static final String SANDBOX_PATH = "target/it-sandbox";
    private static final String TEMPLATE_DIR_NAME = "template";

    private final String dir;
    private final Runner runner;

    public SandboxManager(String testName) {
        dir = SANDBOX_PATH + "/" + testName;
        resetDir(dir);
        resetDir(getDirPath(TEMPLATE_DIR_NAME));
        runner = new Runner(dir);
    }

    private static void resetDir(String path) {
        File directory = new File(path);
        if (directory.exists())
            directory.delete();
        directory.mkdir();
    }

    public void write(String filename, String data) throws IOException {
        try (FileWriter fw = new FileWriter(getDirPath(filename))) {
            fw.write(data);
        }
    }

    public String read(String filename) throws FileNotFoundException, IOException {
        File file = new File(getDirPath(filename));
        try (Stream<String> stream = Files.lines(file.toPath())) {
            return stream.collect(Collectors.joining("\n"));
        }
    }

    private String getDirPath(String filename) {
        return dir + "/" + filename;
    }

    public String getTemplateDirName() {
        return TEMPLATE_DIR_NAME;
    }

    public Runner getRunner() {
        return runner;
    }

    public void writeToTemplateDir(String filename, String data) throws IOException {
        write(TEMPLATE_DIR_NAME + "/" + filename, data);
    }

    public String readFromTemplateDir(String filename) throws IOException {
        return read(TEMPLATE_DIR_NAME + "/" + filename);
    }

}
