package com.github.ideless.processors;

import com.github.ideless.JsonIO;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class VariableProcessorTest {

    private VariableRepository variableRepository;
    private JsonIO jsonIO;
    private VariableProcessor sut;

    @Before
    public void beforeTest() {
        variableRepository = mock(VariableRepository.class);
        jsonIO = mock(JsonIO.class);
        sut = new VariableProcessor(variableRepository, jsonIO);
    }

    @Test(expected = EmptyVariableNameException.class)
    public void shallThrowErrorWhenNullProvided() throws Exception {
        sut.process(null);
    }

    @Test(expected = EmptyVariableNameException.class)
    public void shallThrowErrorWhenEmptyTrimmedProvided() throws Exception {
        sut.process(" ");
    }

    @Test(expected = UndefinedVariableException.class)
    public void shallThrowErrorWhenUndefinedVariableNameProvided() throws Exception {
        sut.process("undefinedVariable");
    }

    @Test
    public void shallReturnJsonFromWhateverVariableRepositoryReturns() throws Exception {
        final String variable = "dummyVariable";
        final int value = 42;
        final String json = "json value";
        when(variableRepository.get(variable)).thenReturn(value);
        when(jsonIO.toJson(value)).thenReturn(json);
        assertEquals(json, sut.process(variable));
    }

}
