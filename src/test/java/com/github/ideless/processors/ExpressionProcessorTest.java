package com.github.ideless.processors;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class ExpressionProcessorTest {

    private VariableProcessor variableProcessor;
    private ExpressionProcessor sut;

    @Before
    public void beforeTest() {
        variableProcessor = mock(VariableProcessor.class);
        sut = new ExpressionProcessor(variableProcessor);
    }

    @Test(expected = EmptyExpressionException.class)
    public void shallThrowErrorWhenNullPassed() throws Exception {
        sut.process(null);
    }

    @Test(expected = EmptyExpressionException.class)
    public void shallThrowErrorWhenEmptyExpressionPassed() throws Exception {
        sut.process(" ");
    }

    @Test(expected = UnreadableExpressionException.class)
    public void shallThrowErrorWhenUnparsableExpressionPassed() throws Exception {
        sut.process("unreadable expression");
    }

    @Test
    public void shallReturnStringFromStringExpression() throws Exception {
        final String dummy = "dummy";
        assertEquals(dummy, sut.process('"' + dummy + '"'));
    }

    @Test
    public void shallDelegateToVariableProcessorAndReplaceVariableWithWhateverItReturns() throws Exception {
        final String variable = "dummyObject.dummyVariable";
        final String value = "dummy variable's value";
        when(variableProcessor.process(variable)).thenReturn('"' + value + '"');
        assertEquals(value, sut.process(" $" + variable + " "));
    }

}
