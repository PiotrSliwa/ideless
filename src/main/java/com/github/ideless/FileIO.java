package com.github.ideless;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileIO {

    public String read(String path) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(path))) {
            return stream.collect(Collectors.joining("\n"));
        }
    }

    public void write(String filename, String data) throws IOException {
        try (FileWriter fw = new FileWriter(filename)) {
            fw.write(data);
        }
    }

}
