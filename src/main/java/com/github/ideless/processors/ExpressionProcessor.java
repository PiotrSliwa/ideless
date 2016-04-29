package com.github.ideless.processors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionProcessor {

    private static final Pattern VARIABLE_EXPRESSION_PATTERN = Pattern.compile("\\$([^\\s]+)");
    private static final Pattern STRING_EXPRESSION_PATTERN = Pattern.compile("\"(.*?)\"");

    private final VariableProcessor variableProcessor;

    public ExpressionProcessor(VariableProcessor variableProcessor) {
        this.variableProcessor = variableProcessor;
    }

    public String process(String expression) throws Exception {
        if (expression == null)
            throw new EmptyExpressionException();
        String trimmedExpression = expression.trim();
        if (trimmedExpression.isEmpty())
            throw new EmptyExpressionException();

        Matcher variableExprMatcher = VARIABLE_EXPRESSION_PATTERN.matcher(trimmedExpression);
        if (variableExprMatcher.matches()) {
            String variableExpression = variableExprMatcher.group(1);
            trimmedExpression = variableExprMatcher.replaceAll(variableProcessor.process(variableExpression));
        }

        Matcher stringExprMatcher = STRING_EXPRESSION_PATTERN.matcher(trimmedExpression);
        if (stringExprMatcher.matches())
            return stringExprMatcher.group(1);
        throw new UnreadableExpressionException(trimmedExpression);
    }

}
