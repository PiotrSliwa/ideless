package com.github.ideless.running;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SandboxManager {

    private static final String SANDBOX_PATH = "target/it-sandbox";

    private final String dir;

    public SandboxManager(String testName) {
        dir = SANDBOX_PATH + "/" + testName;
        File sandboxDir = new File(dir);
        if (sandboxDir.exists())
            sandboxDir.delete();
        sandboxDir.mkdir();
    }

    public void write(String filename, String data) throws FileNotFoundException, IOException {
        File file = new File(dir + "/" + filename);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data.getBytes());
    }

    public String read(String filename) throws FileNotFoundException, IOException {
        File file = new File(dir + "/" + filename);
        try (Stream<String> stream = Files.lines(file.toPath())) {
            return stream.collect(Collectors.joining("\n"));
        }
    }

}
