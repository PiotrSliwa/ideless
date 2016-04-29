package com.github.ideless.it;

import com.github.ideless.running.SandboxManager;
import java.io.IOException;
import static org.hamcrest.CoreMatchers.startsWith;
import org.junit.Assert;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class InitCommandIT {

    private static final String FILE_NAME = "file1";
    private static final String FILE_DATA = "some data";
    private static final String BEFORE_EXPR = "something";
    private static final String AFTER_EXPR = "else";

    private SandboxManager initValid(String manifestData) throws IOException {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
        String[] classElems = ste.getClassName().split("\\.");
        String suiteName = classElems[classElems.length - 1];
        String testName = ste.getMethodName();
        SandboxManager manager = new SandboxManager(suiteName + "_" + testName);
        manager.writeToTemplateDir(".ideless", manifestData);
        return manager;
    }

    private static String runInitCommand(SandboxManager manager) throws Exception {
        return manager.getRunner().run("init " + manager.getTemplateDirName());
    }

    @Test
    public void lackOfTemplateParameter() throws Exception {
        SandboxManager manager = new SandboxManager("InitCommandIT_lackOfTemplateParameter");
        String out = manager.getRunner().run("init");
        assertThat(out, startsWith("Usage: "));
    }

    @Test
    public void absentTemplateDirectory() throws Exception {
        SandboxManager manager = new SandboxManager("InitCommandIT_absentTemplateDirectory");
        String out = manager.getRunner().run("init strangeTemplate");
        assertThat(out, startsWith("Error: Invalid ideless template directory ("));
    }

    @Test
    public void absentManifestFile() throws Exception {
        SandboxManager manager = new SandboxManager("InitCommandIT_absentManifestFile");
        String out = runInitCommand(manager);
        assertThat(out, startsWith("Error: Invalid ideless template directory ("));
    }

    @Test
    public void invalidJsonAsManifestFile() throws Exception{
        SandboxManager manager = initValid("invalid json");
        String out = runInitCommand(manager);
        assertThat(out, startsWith("Error: Invalid JSON ("));
    }

    @Test
    public void noInitFilesInManifestFile() throws Exception {
        SandboxManager manager = initValid("{\"someField\":\"someValue\"}");
        String out = runInitCommand(manager);
        assertThat(out, startsWith("Error: Lack of 'initFiles' field"));
    }

    @Test
    public void absentInitFile() throws Exception {
        SandboxManager manager = initValid("{\"initFiles\":[\"unknownFile\"]}");
        String out = runInitCommand(manager);
        assertThat(out, startsWith("Error: Cannot find file"));
    }

    @Test
    public void shallCopyInitFiles() throws Exception {
        SandboxManager manager = initValid("{\"initFiles\":[\"" + FILE_NAME + "\"]}");
        manager.writeToTemplateDir(FILE_NAME, FILE_DATA);

        String out = runInitCommand(manager);
        assertThat(out, startsWith("Initializing file: " + FILE_NAME));
        Assert.assertEquals(FILE_DATA, manager.read(FILE_NAME));
    }

    @Test
    public void shallAskForPropertyIfOneDefined() throws Exception {
        String propertyName = "prop_name";
        String propertyDescription = "Some prop description.";

        SandboxManager manager = initValid(
                "{\"initFiles\":[\"" + FILE_NAME + "\"],\"properties\":[{\"name\":\"" +
                propertyName + "\",\"description\":\"" + propertyDescription + "\"}]}");
        manager.writeToTemplateDir(FILE_NAME, FILE_DATA);

        String out = runInitCommand(manager);
        assertThat(out, startsWith(propertyName + " (" + propertyDescription + ")"));
    }

    @Test
    public void shallReturnErrorWhenEmptyExpressionFoundInTemplate() throws Exception {
        SandboxManager manager = initValid(
                "{\"initFiles\":[\"" + FILE_NAME + "\"]}");
        manager.writeToTemplateDir(FILE_NAME, BEFORE_EXPR + "{{ }}" + AFTER_EXPR);

        String out = runInitCommand(manager);
        assertThat(out, startsWith("Error: Empty expression found in template file"));
    }

    @Test
    public void shallReturnErrorWhenErrorousExpressionFoundInTemplate() throws Exception {
        String unreadableExpression = "unreadable expression";

        SandboxManager manager = initValid(
                "{\"initFiles\":[\"" + FILE_NAME + "\"]}");
        manager.writeToTemplateDir(FILE_NAME, BEFORE_EXPR + "{{ " + unreadableExpression + " }}" + AFTER_EXPR);

        String out = runInitCommand(manager);
        assertThat(out, startsWith("Error: Unreadable expression: '" + unreadableExpression + "'"));
    }

    @Test
    public void shallReplaceStringExpressionWithTheStringInTemplate() throws Exception {
        String dummyString = "dummy string";

        SandboxManager manager = initValid(
                "{\"initFiles\":[\"" + FILE_NAME + "\"]}");
        manager.writeToTemplateDir(FILE_NAME, BEFORE_EXPR + "{{ \"" + dummyString + "\" }}" + AFTER_EXPR);

        String out = runInitCommand(manager);
        assertThat(out, startsWith("Initializing file: " + FILE_NAME));
        Assert.assertEquals(BEFORE_EXPR + dummyString + AFTER_EXPR, manager.read(FILE_NAME));
    }

    @Test
    public void shallReturnErrorWhenUndefinedVariableUsedInExpression() throws Exception {
        String unknownVariable = "unknownVariable";

        SandboxManager manager = initValid(
                "{\"initFiles\":[\"" + FILE_NAME + "\"]}");
        manager.writeToTemplateDir(FILE_NAME, BEFORE_EXPR + "{{ $" + unknownVariable + " }}" + AFTER_EXPR);

        String out = runInitCommand(manager);
        assertThat(out, startsWith("Error: Undefined variable: '" + unknownVariable + "'"));
    }

}
