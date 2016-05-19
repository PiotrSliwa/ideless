package com.github.ideless.init;

import com.github.ideless.FileIO;
import com.github.ideless.processors.ContentProcessor;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class FileInitializerTest {

    private final static Path SOURCE_PATH = Paths.get("source");
    private final static Path TARGET_PATH = Paths.get("target");
    private final static String DATA = "file contents";
    private final static String PROCESSED_DATA = "processed file contents";

    FileIO fileIO;
    ContentProcessor contentProcessor;
    FileInitializer sut;

    @Before
    public void beforeTest() {
        fileIO = mock(FileIO.class);
        contentProcessor = mock(ContentProcessor.class);
        sut = new FileInitializer(fileIO, contentProcessor);
    }

    @Test
    public void shallCopyFileFromSourceToTargetPathProcessedWithContentProcessor() throws Exception {
        when(fileIO.read(SOURCE_PATH)).thenReturn(DATA);
        when(contentProcessor.process(DATA)).thenReturn(PROCESSED_DATA);
        sut.initialize(SOURCE_PATH, TARGET_PATH);
        verify(fileIO).write(TARGET_PATH, PROCESSED_DATA);
    }

}
