package com.github.ideless.processors;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class ContentProcessorTest {

    private static final String EXPR = "some expression";
    private static final String DEFAULT_ESCAPE_SEQUENCE = "\\";
    private static final String[] DEFAULT_DELIMITERS = {"{{", "}}"};
    private static final String RESULT = "result";

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
        when(expressionProcessor.process(EXPR)).thenReturn(RESULT);
        assertEquals(createSurroundedText(RESULT), sut.process(createSurroundedText(createExpression(EXPR))));
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
    public void shallReturnUntouchedStringWhenItIsPrecededByEscapeSequence() throws Exception {
        final String input = createSurroundedText(DEFAULT_ESCAPE_SEQUENCE + createExpression(EXPR));
        assertEquals(input, sut.process(input));
    }

    @Test
    public void shallDelegateExpressionToExpressionProcessorAndReplaceOccurenceWithTheReturnedValue_WithChangedDelimiters() throws Exception {
        String[] delimiters = {"<", ">"};
        when(expressionProcessor.process(EXPR)).thenReturn(RESULT);

        sut.updateConfig(delimiters[0], delimiters[1], DEFAULT_ESCAPE_SEQUENCE);
        assertEquals(createSurroundedText(RESULT), sut.process(createSurroundedText(delimiters[0] + EXPR + delimiters[1])));
    }

    @Test
    public void shallReturnUntouchedStringWhenItIsPrecededByEscapeChar_WithChangedEscapeSequence() throws Exception {
        final String escapeSequence = "+=$*/";
        final String input = createSurroundedText(escapeSequence + createExpression(EXPR));

        sut.updateConfig(DEFAULT_DELIMITERS[0], DEFAULT_DELIMITERS[1], escapeSequence);
        assertEquals(input, sut.process(input));
    }

}
