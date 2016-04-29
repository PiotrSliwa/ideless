package com.github.ideless.init;

import com.github.ideless.FileIO;
import com.github.ideless.processors.ContentProcessor;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class FileInitializerTest {

    @Test
    public void shallCopyFileFromSourceToTargetPathProcessedWithContentProcessor() throws Exception {
        FileIO fileIO = mock(FileIO.class);
        ContentProcessor contentProcessor = mock(ContentProcessor.class);
        FileInitializer sut = new FileInitializer(fileIO, contentProcessor);

        String sourcePath = "source";
        String targetPath = "target";
        String data = "file contents";
        String processedData = "processed file contents";

        when(fileIO.read(sourcePath)).thenReturn(data);
        when(contentProcessor.process(data)).thenReturn(processedData);

        sut.initialize(sourcePath, targetPath);

        verify(fileIO).write(targetPath, processedData);
    }

}
