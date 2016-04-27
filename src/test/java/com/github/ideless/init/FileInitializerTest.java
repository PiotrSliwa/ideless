package com.github.ideless.init;

import com.github.ideless.FileIO;
import java.io.IOException;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class FileInitializerTest {

    @Test
    public void shallCopyFileFromSourceToTargetPath() throws IOException {
        FileIO fileIO = mock(FileIO.class);
        FileInitializer sut = new FileInitializer(fileIO);

        String sourcePath = "source";
        String targetPath = "target";
        String data = "file contents";
        when(fileIO.read(sourcePath)).thenReturn(data);

        sut.initialize(sourcePath, targetPath);

        verify(fileIO).write(targetPath, data);
    }

}
