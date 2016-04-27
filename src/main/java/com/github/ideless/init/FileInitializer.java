package com.github.ideless.init;

import com.github.ideless.FileIO;
import java.io.IOException;

public class FileInitializer {

    private final FileIO fileIO;

    public FileInitializer(FileIO fileIO) {
        this.fileIO = fileIO;
    }

    public void initialize(String sourcePath, String targetPath) throws IOException {
        String data = fileIO.read(sourcePath);
        fileIO.write(targetPath, data);
    }

}
