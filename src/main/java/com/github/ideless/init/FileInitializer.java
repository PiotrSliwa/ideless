package com.github.ideless.init;

import com.github.ideless.FileIO;
import com.github.ideless.processors.ContentProcessor;
import java.nio.file.Path;

public class FileInitializer {

    private final FileIO fileIO;
    private final ContentProcessor contentProcessor;

    public FileInitializer(FileIO fileIO, ContentProcessor contentProcessor) {
        this.fileIO = fileIO;
        this.contentProcessor = contentProcessor;
    }

    public void initialize(Path source, Path target) throws Exception {
        String data = fileIO.read(source);
        fileIO.write(target, contentProcessor.process(data));
    }

}
