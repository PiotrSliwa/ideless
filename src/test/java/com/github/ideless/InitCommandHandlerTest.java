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
    private FileIO fileIO;
    private InitCommandHandler sut;

    @Before
    public void beforeTest() {
        defaultHandler = mock(SafeCommandHandler.class);
        fileIO = mock(FileIO.class);
        sut = new InitCommandHandler(defaultHandler, fileIO);
    }

    @Test
    public void shallCallDefaultHandlerWhenNoParametersGiven() throws Exception {
        sut.handle(Arrays.asList());
        verify(defaultHandler).handle(Arrays.asList());
    }

    @Test(expected = InvalidTemplateException.class)
    public void shallThrowErrorWhenManifestCannotBeOpen() throws Exception {
        when(fileIO.read(MANIFEST_PATH)).thenThrow(new IOException());
        sut.handle(Arrays.asList(PATH));
    }

}
