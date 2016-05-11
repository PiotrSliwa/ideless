package com.github.ideless.it;

import com.github.ideless.running.SandboxManager;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.startsWith;
import org.junit.Assert;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

public class InitCommandIT {

    private static final Gson GSON = new Gson();

    private static final String FILE_NAME = "file1";
    private static final String FILE_DATA = "some data";
    private static final String BEFORE_EXPR = "something";
    private static final String AFTER_EXPR = "else";
    private static final String PROPERTY_NAME = "prop_name";
    private static final String PROPERTY_DESCRIPTION = "Some prop description.";
    private static final String USER_VALUE = "DUMMY";

    private Map<String, Object> manifestFile;
    private Map<String, Object> properties;

    private SandboxManager initValid(String manifestData) throws IOException {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
        String[] classElems = ste.getClassName().split("\\.");
        String suiteName = classElems[classElems.length - 1];
        String testName = ste.getMethodName();
        SandboxManager manager = new SandboxManager(suiteName + "_" + testName);
        manager.writeToTemplateDir(".ideless", manifestData);
        return manager;
    }

    private SandboxManager initValid(Map<String, Object> manifestData) throws IOException {
        return initValid(GSON.toJson(manifestData));
    }

    private static String runInitCommand(SandboxManager manager) throws Exception {
        return manager.getRunner().run("init " + manager.getTemplateDirName());
    }

    @Before
    public void beforeTest() {
        manifestFile = new HashMap<>();
        properties = new HashMap<>();
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
        manifestFile.put("someField", Arrays.asList("someValue"));
        SandboxManager manager = initValid(manifestFile);
        String out = runInitCommand(manager);
        assertThat(out, startsWith("Error: Lack of 'initFiles' field"));
    }

    @Test
    public void absentInitFile() throws Exception {
        manifestFile.put("initFiles", Arrays.asList("unknownFile"));
        SandboxManager manager = initValid(manifestFile);
        String out = runInitCommand(manager);
        assertThat(out, startsWith("Error: Cannot find file"));
    }

    @Test
    public void shallCopyInitFiles() throws Exception {
        manifestFile.put("initFiles", Arrays.asList(FILE_NAME));
        SandboxManager manager = initValid(manifestFile);
        manager.writeToTemplateDir(FILE_NAME, FILE_DATA);

        String out = runInitCommand(manager);
        assertThat(out, startsWith("Initializing file: " + FILE_NAME));
        Assert.assertEquals(FILE_DATA, manager.read(FILE_NAME));
    }

    @Test
    public void shallAskForPropertyIfOneDefined() throws Exception {
        properties.put("name", PROPERTY_NAME);
        properties.put("description", PROPERTY_DESCRIPTION);
        manifestFile.put("properties", Arrays.asList(properties));
        manifestFile.put("initFiles", Arrays.asList(FILE_NAME));
        SandboxManager manager = initValid(manifestFile);

        manager.writeToTemplateDir(FILE_NAME, FILE_DATA);
        manager.getRunner().addInput("dummy");

        String out = runInitCommand(manager);
        assertThat(out, startsWith(PROPERTY_NAME + " (" + PROPERTY_DESCRIPTION + ")"));
    }

    @Test
    public void shallReturnErrorWhenEmptyExpressionFoundInTemplate() throws Exception {
        manifestFile.put("initFiles", Arrays.asList(FILE_NAME));
        SandboxManager manager = initValid(manifestFile);

        manager.writeToTemplateDir(FILE_NAME, BEFORE_EXPR + "{{ }}" + AFTER_EXPR);

        String out = runInitCommand(manager);
        assertThat(out, startsWith("Error: Empty expression found in template file"));
    }

    @Test
    public void shallReturnErrorWhenErrorousExpressionFoundInTemplate() throws Exception {
        manifestFile.put("initFiles", Arrays.asList(FILE_NAME));
        SandboxManager manager = initValid(manifestFile);

        String unreadableExpression = "unreadable expression";
        manager.writeToTemplateDir(FILE_NAME, BEFORE_EXPR + "{{ " + unreadableExpression + " }}" + AFTER_EXPR);

        String out = runInitCommand(manager);
        assertThat(out, startsWith("Error: Unreadable expression: '" + unreadableExpression + "'"));
    }

    @Test
    public void shallReplaceStringExpressionWithTheStringInTemplate() throws Exception {
        manifestFile.put("initFiles", Arrays.asList(FILE_NAME));
        SandboxManager manager = initValid(manifestFile);

        String dummyString = "dummy string";
        manager.writeToTemplateDir(FILE_NAME, BEFORE_EXPR + "{{ \"" + dummyString + "\" }}" + AFTER_EXPR);

        String out = runInitCommand(manager);
        assertThat(out, startsWith("Initializing file: " + FILE_NAME));
        Assert.assertEquals(BEFORE_EXPR + dummyString + AFTER_EXPR, manager.read(FILE_NAME));
    }

    @Test
    public void shallReturnErrorWhenUndefinedVariableUsedInExpression() throws Exception {
        manifestFile.put("initFiles", Arrays.asList(FILE_NAME));
        SandboxManager manager = initValid(manifestFile);

        String unknownVariable = "unknownVariable";
        manager.writeToTemplateDir(FILE_NAME, BEFORE_EXPR + "{{ $" + unknownVariable + " }}" + AFTER_EXPR);

        String out = runInitCommand(manager);
        assertThat(out, startsWith("Error: Undefined variable: '" + unknownVariable + "'"));
    }

    @Test
    public void shallReplaceExpressionWithPropertyWithDefaultExpressionConfiguration() throws Exception {
        properties.put("name", PROPERTY_NAME);
        properties.put("description", PROPERTY_DESCRIPTION);
        manifestFile.put("properties", Arrays.asList(properties));
        manifestFile.put("initFiles", Arrays.asList(FILE_NAME));
        SandboxManager manager = initValid(manifestFile);

        manager.writeToTemplateDir(FILE_NAME, BEFORE_EXPR + "{{ $properties." + PROPERTY_NAME + " }}" + AFTER_EXPR);
        manager.getRunner().addInput(USER_VALUE);

        String out = runInitCommand(manager);
        assertThat(out, containsString("Initializing file: " + FILE_NAME));
        Assert.assertEquals(BEFORE_EXPR + USER_VALUE + AFTER_EXPR, manager.read(FILE_NAME));
    }

    @Test
    public void shallNotReplaceEscapedExpressionWithDefaultExpressionConfiguration() throws Exception {
        final String template = BEFORE_EXPR + "\\{{ $properties." + PROPERTY_NAME + " }}" + AFTER_EXPR;

        properties.put("name", PROPERTY_NAME);
        properties.put("description", PROPERTY_DESCRIPTION);
        manifestFile.put("properties", Arrays.asList(properties));
        manifestFile.put("initFiles", Arrays.asList(FILE_NAME));
        SandboxManager manager = initValid(manifestFile);

        manager.writeToTemplateDir(FILE_NAME, template);
        manager.getRunner().addInput(USER_VALUE);

        String out = runInitCommand(manager);
        assertThat(out, containsString("Initializing file: " + FILE_NAME));
        Assert.assertEquals(template, manager.read(FILE_NAME));
    }

    @Test
    public void shallReturnErrorWhenInsufficientElementsGivenInExpressionFormat() throws Exception {
        manifestFile.put("initFiles", Arrays.asList(FILE_NAME));
        manifestFile.put("expressionFormat", Arrays.asList("1"));
        SandboxManager manager = initValid(manifestFile);

        manager.writeToTemplateDir(FILE_NAME, FILE_DATA);
        manager.getRunner().addInput(USER_VALUE);

        String out = runInitCommand(manager);
        assertThat(out, startsWith("Error: Invalid number of elements in array 'expressionFormat'"));
    }

    @Test
    public void shallReplaceExpressionWithPropertyWithUserDefinedExpressionFormat() throws Exception {
        properties.put("name", PROPERTY_NAME);
        properties.put("description", PROPERTY_DESCRIPTION);
        manifestFile.put("properties", Arrays.asList(properties));
        manifestFile.put("initFiles", Arrays.asList(FILE_NAME));
        manifestFile.put("expressionFormat", Arrays.asList("<", ">", "\\"));
        SandboxManager manager = initValid(manifestFile);

        manager.writeToTemplateDir(FILE_NAME, BEFORE_EXPR + "< $properties." + PROPERTY_NAME + " >" + AFTER_EXPR);
        manager.getRunner().addInput(USER_VALUE);

        String out = runInitCommand(manager);
        assertThat(out, containsString("Initializing file: " + FILE_NAME));
        Assert.assertEquals(BEFORE_EXPR + USER_VALUE + AFTER_EXPR, manager.read(FILE_NAME));
    }

}
