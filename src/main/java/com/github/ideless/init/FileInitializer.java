package com.github.ideless.init;

import com.github.ideless.FileIO;
import com.github.ideless.processors.ContentProcessor;

public class FileInitializer {

    private final FileIO fileIO;
    private final ContentProcessor contentProcessor;

    public FileInitializer(FileIO fileIO, ContentProcessor contentProcessor) {
        this.fileIO = fileIO;
        this.contentProcessor = contentProcessor;
    }

    public void initialize(String sourcePath, String targetPath) throws Exception {
        String data = fileIO.read(sourcePath);
        fileIO.write(targetPath, contentProcessor.process(data));
    }

}
