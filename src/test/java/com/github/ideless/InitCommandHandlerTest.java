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
    private static final String MANIFEST_PATH = PATH + "/.ideless";
    private static final String FILE = "file1";
    private static final List<String> FILES = Arrays.asList("file1", "file2");
    private static final List<String> DATA = Arrays.asList("data one", "data two");

    private SafeCommandHandler defaultHandler;
    private ManifestReader manifestReader;
    private FileIO fileIO;
    private UserIO userIO;
    private InitCommandHandler sut;

    @Before
    public void beforeTest() {
        defaultHandler = mock(SafeCommandHandler.class);
        manifestReader = mock(ManifestReader.class);
        fileIO = mock(FileIO.class);
        userIO = mock(UserIO.class);
        sut = new InitCommandHandler(defaultHandler, manifestReader, fileIO, userIO);
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
        when(manifestReader.read(MANIFEST_PATH)).thenReturn(new Manifest(FILES));
        when(fileIO.read(PATH + "/" + FILES.get(0))).thenReturn(DATA.get(0));
        when(fileIO.read(PATH + "/" + FILES.get(1))).thenReturn(DATA.get(1));

        sut.handle(Arrays.asList(PATH));

        verify(fileIO).read(PATH + "/" + FILES.get(0));
        verify(fileIO).read(PATH + "/" + FILES.get(1));
        verify(fileIO).write(FILES.get(0), DATA.get(0));
        verify(fileIO).write(FILES.get(1), DATA.get(1));
    }

    @Test(expected = CannotFindFileException.class)
    public void shallThrowErrorWhenCannotReadFile() throws Exception {
        when(manifestReader.read(MANIFEST_PATH)).thenReturn(new Manifest(FILES));
        when(fileIO.read(Matchers.any())).thenThrow(new IOException());
        sut.handle(Arrays.asList(PATH));
    }

    @Test
    public void shallAskUserForPropertyWhenManifestContainsOne() throws Exception {
        Manifest.Property property = new Manifest.Property("propertyName", "propertyDescription");
        when(manifestReader.read(MANIFEST_PATH)).thenReturn(new Manifest(Arrays.asList(FILE), Arrays.asList(property)));
        when(fileIO.read(PATH + "/" + FILE)).thenReturn(DATA.get(0));

        sut.handle(Arrays.asList(PATH));

        verify(userIO).print(property.getName() + " (" + property.getDescription() + "): ");
    }

}
