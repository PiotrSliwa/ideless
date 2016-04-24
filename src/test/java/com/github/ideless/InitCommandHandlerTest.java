package com.github.ideless;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class InitCommandHandlerTest {

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
    public void shallThrowErrorWhenTemplateIsInvalid() throws Exception {
        String invalidPath = "unknownTemplate";
        when(fileIO.read(invalidPath)).thenReturn(null);
        sut.handle(Arrays.asList(invalidPath));
    }

}
