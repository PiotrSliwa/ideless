package com.github.ideless.init;

import com.github.ideless.SafeCommandHandler;
import com.github.ideless.UserIO;
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

    private SafeCommandHandler defaultHandler;
    private ManifestReader manifestReader;
    private UserIO userIO;
    private FileInitializer fileInitializer;
    private InitCommandHandler sut;

    private static String getFileInitMessage(String file) {
        return "Initializing file: " + file;
    }

    private static String getPropertyQuestionMessage(Property property) {
        return property.getName() + " (" + property.getDescription() + "): ";
    }

    @Before
    public void beforeTest() {
        defaultHandler = mock(SafeCommandHandler.class);
        manifestReader = mock(ManifestReader.class);
        userIO = mock(UserIO.class);
        fileInitializer = mock(FileInitializer.class);
        sut = new InitCommandHandler(defaultHandler, manifestReader, userIO, fileInitializer);
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

        sut.handle(Arrays.asList(PATH));

        verify(fileInitializer).initialize(PATH + "/" + FILES.get(0), FILES.get(0));
        verify(fileInitializer).initialize(PATH + "/" + FILES.get(1), FILES.get(1));

        verify(userIO).println(getFileInitMessage(FILES.get(0)));
        verify(userIO).println(getFileInitMessage(FILES.get(1)));
    }

    @Test(expected = CannotFindFileException.class)
    public void shallThrowErrorWhenInitializerReturnsIOException() throws Exception {
        when(manifestReader.read(MANIFEST_PATH)).thenReturn(new Manifest(FILES));
        doThrow(new IOException()).when(fileInitializer).initialize(Matchers.any(), Matchers.any());
        sut.handle(Arrays.asList(PATH));
    }

    @Test
    public void shallAskUserForPropertyWhenManifestContainsOne() throws Exception {
        Property property = new Property("propertyName", "propertyDescription");
        when(manifestReader.read(MANIFEST_PATH)).thenReturn(new Manifest(Arrays.asList(FILE), Arrays.asList(property)));

        sut.handle(Arrays.asList(PATH));

        verify(userIO).print(getPropertyQuestionMessage(property));
    }

}
