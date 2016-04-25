package com.github.ideless;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import static org.mockito.Mockito.*;

public class InitCommandHandlerTest {

    private static final String PATH = "dummy";
    private static final String MANIFEST_PATH = "dummy/.ideless";
    private static final List<String> FILES = Arrays.asList("file1", "file2");

    private SafeCommandHandler defaultHandler;
    private ManifestReader manifestReader;
    private FileIO fileIO;
    private InitCommandHandler sut;

    @Before
    public void beforeTest() {
        defaultHandler = mock(SafeCommandHandler.class);
        manifestReader = mock(ManifestReader.class);
        fileIO = mock(FileIO.class);
        sut = new InitCommandHandler(defaultHandler, manifestReader, fileIO);
    }

    @Test
    public void shallCallDefaultHandlerWhenNoParametersGiven() throws Exception {
        sut.handle(Arrays.asList());
        verify(defaultHandler).handle(Arrays.asList());
    }

    @Test(expected = InvalidTemplateException.class)
    public void shallThrowErrorWhenManifestCannotBeRead() throws Exception {
        when(manifestReader.read(MANIFEST_PATH)).thenThrow(new IOException());
        sut.handle(Arrays.asList(PATH));
    }

    @Test(expected = InvalidTemplateException.class)
    public void shallThrowErrorWhenNullManifestReturned() throws Exception {
        when(manifestReader.read(MANIFEST_PATH)).thenReturn(null);
        sut.handle(Arrays.asList(PATH));
    }

    @Test(expected = LackOfFieldException.class)
    public void shallThrowErrorWhenEmptyManifestReturned() throws Exception {
        when(manifestReader.read(MANIFEST_PATH)).thenReturn(new Manifest());
        sut.handle(Arrays.asList(PATH));
    }

    @Test
    public void shallReadInitFilesAndSaveThemToTarget() throws Exception {
        String data1 = "siple data one";
        String data2 = "siple data two";
        when(manifestReader.read(MANIFEST_PATH)).thenReturn(new Manifest(FILES));
        when(fileIO.read(PATH + "/" + FILES.get(0))).thenReturn(data1);
        when(fileIO.read(PATH + "/" + FILES.get(1))).thenReturn(data2);
        sut.handle(Arrays.asList(PATH));
        verify(fileIO).read(PATH + "/" + FILES.get(0));
        verify(fileIO).read(PATH + "/" + FILES.get(1));
        verify(fileIO).write(FILES.get(0), data1);
        verify(fileIO).write(FILES.get(1), data2);
    }

    @Test(expected = CannotFindFileException.class)
    public void shallThrowErrorWhenCannotReadFile() throws Exception {
        when(manifestReader.read(MANIFEST_PATH)).thenReturn(new Manifest(FILES));
        when(fileIO.read(Matchers.any())).thenThrow(new IOException());
        sut.handle(Arrays.asList(PATH));
    }

}
