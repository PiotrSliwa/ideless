package com.github.ideless;

import java.io.IOException;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class InitCommandHandlerTest {

    private static final String PATH = "dummy";
    private static final String MANIFEST_PATH = "dummy/.ideless";

    private SafeCommandHandler defaultHandler;
    private ManifestReader manifestReader;
    private InitCommandHandler sut;

    @Before
    public void beforeTest() {
        defaultHandler = mock(SafeCommandHandler.class);
        manifestReader = mock(ManifestReader.class);
        sut = new InitCommandHandler(defaultHandler, manifestReader);
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

}
