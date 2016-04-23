package com.github.ideless;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class InitCommandHandlerTest {

    private SafeCommandHandler defaultHandler;
    private TemplateReader reader;
    private InitCommandHandler sut;

    @Before
    public void beforeTest() {
        defaultHandler = mock(SafeCommandHandler.class);
        reader = mock(TemplateReader.class);
        sut = new InitCommandHandler(defaultHandler, reader);
    }

    @Test
    public void shallCallDefaultHandlerWhenNoParametersGiven() throws Exception {
        sut.handle(Arrays.asList());
        verify(defaultHandler).handle(Arrays.asList());
    }

    @Test(expected = InvalidTemplateException.class)
    public void shallThrowErrorWhenTemplateIsInvalid() throws Exception {
        String invalidPath = "unknownTemplate";
        when(reader.read(invalidPath)).thenReturn(null);
        sut.handle(Arrays.asList(invalidPath));
    }

}
