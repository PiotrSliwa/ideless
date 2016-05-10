package com.github.ideless.processors;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class ContentProcessorTest {

    private static final String EXPR = "some expression";

    private static String createExpression(String expressionContent) {
        return "{{" + expressionContent + "}}";
    }

    private static String createSurroundedText(String text) {
        return "some text before " + text + " some text after";
    }

    private ExpressionProcessor expressionProcessor;
    private ContentProcessor sut;

    @Before
    public void beforeTest() {
        expressionProcessor = mock(ExpressionProcessor.class);
        sut = new ContentProcessor(expressionProcessor);
    }

    @Test
    public void shallReturnEmptyStringWhenNullPassed() throws Exception {
        assertEquals("", sut.process(null));
    }

    @Test
    public void shallReturnUntouchedStringWhenItDoesNotContainAnyExpression() throws Exception {
        assertEquals(EXPR, sut.process(EXPR));
    }

    @Test
    public void shallDelegateExpressionToExpressionProcessorAndReplaceOccurenceWithTheReturnedValue() throws Exception {
        String result = "result";
        when(expressionProcessor.process(EXPR)).thenReturn(result);

        assertEquals(createSurroundedText(result), sut.process(createSurroundedText(createExpression(EXPR))));
    }

    @Test
    public void shallDelegateExpressionToExpressionProcessorAndReplaceOccurenceWithTheReturnedValue_ForMultipleExpressions() throws Exception {
        String[] expresssions = {"expr", "anotherExpr"};
        String[] results = {"result", "anotherResult"};
        when(expressionProcessor.process(expresssions[0])).thenReturn(results[0]);
        when(expressionProcessor.process(expresssions[1])).thenReturn(results[1]);
        String input = createSurroundedText(createExpression(expresssions[0])) + createSurroundedText(createExpression(expresssions[1]));
        String expectedOutput = createSurroundedText(results[0]) + createSurroundedText(results[1]);

        assertEquals(expectedOutput, sut.process(input));
    }

    @Test
    public void shallReturnUntouchedStringWhenItIsPrecededByEscapeChar() throws Exception {
        final String escapeChar = "\\";
        final String input = createSurroundedText(escapeChar + createExpression(EXPR));
        assertEquals(input, sut.process(input));
    }

}
