package com.github.ideless.processors;

import com.github.ideless.JsonIO;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class VariableProcessorTest {

    private VariableGetter variableGetter;
    private JsonIO jsonIO;
    private VariableProcessor sut;

    @Before
    public void beforeTest() {
        variableGetter = mock(VariableGetter.class);
        jsonIO = mock(JsonIO.class);
        sut = new VariableProcessor(variableGetter, jsonIO);
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
    public void shallReturnJsonFromWhateverVariableGetterReturns() throws Exception {
        final String variable = "dummyVariable";
        final int value = 42;
        final String json = "json value";
        when(variableGetter.get(variable)).thenReturn(value);
        when(jsonIO.toJson(value)).thenReturn(json);
        assertEquals(json, sut.process(variable));
    }

}
