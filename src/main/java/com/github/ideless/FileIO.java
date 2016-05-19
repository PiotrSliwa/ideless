package com.github.ideless;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileIO {

    public String read(Path path) throws IOException {
        try (Stream<String> stream = Files.lines(path)) {
            return stream.collect(Collectors.joining("\n"));
        }
    }

    public void write(Path path, String data) throws IOException {
        makeParentDirs(path);
        File file = path.toFile();
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(data);
        }
    }

    private void makeParentDirs(Path path) {
        File parent = path.resolveSibling(".").toFile();
        if (!parent.isDirectory())
            parent.mkdirs();
    }

}
