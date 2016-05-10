package com.github.ideless.processors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentProcessor {

    private static final Pattern PATTERN = Pattern.compile("(?<!\\\\)\\{\\{(.+?)\\}\\}");

    private final ExpressionProcessor expressionProcessor;

    public ContentProcessor(ExpressionProcessor expressionProcessor) {
        this.expressionProcessor = expressionProcessor;
    }

    public String process(String input) throws Exception {
        if (input == null)
            return "";
        Matcher matcher = PATTERN.matcher(input);
        return replaceMatchedExpressions(matcher);
    }

    private String replaceMatchedExpressions(Matcher matcher) throws Exception {
        StringBuffer sb = new StringBuffer();
        while (matcher.find())
            matcher.appendReplacement(sb, process(matcher));
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String process(Matcher matcher) throws Exception {
        String expression = matcher.group(1);
        return expressionProcessor.process(expression);
    }

}
