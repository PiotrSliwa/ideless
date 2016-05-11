package com.github.ideless.processors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentProcessor implements ExpressionConfigUpdater {

    private final ExpressionProcessor expressionProcessor;
    private Pattern pattern = createPattern("{{", "}}", "\\");

    public ContentProcessor(ExpressionProcessor expressionProcessor) {
        this.expressionProcessor = expressionProcessor;
    }

    public String process(String input) throws Exception {
        if (input == null)
            return "";
        Matcher matcher = pattern.matcher(input);
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

    @Override
    public void updateConfig(String leftDelimiter, String rightDelimiter, String escapeSequence) {
        pattern = createPattern(leftDelimiter, rightDelimiter, escapeSequence);
    }

    private static Pattern createPattern(String leftDelimiter, String rightDelimiter, String escapeSequence) {
        return Pattern.compile(
                "(?<!" + Pattern.quote(escapeSequence) +
                ")" + Pattern.quote(leftDelimiter) +
                "(.+?)" + Pattern.quote(rightDelimiter));
    }

}
