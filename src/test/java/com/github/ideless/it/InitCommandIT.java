package com.github.ideless.it;

import com.github.ideless.running.SandboxManager;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

public class InitCommandIT {

    private static final Gson GSON = new Gson();

    private static final Path IDELESS_HOME_DIR = Paths.get(System.getProperty("user.home")).resolve(".ideless");
    private static final String FILE_NAME = "file1";
    private static final String FILE_DATA = "some data";
    private static final String BEFORE_EXPR = "something";
    private static final String AFTER_EXPR = "else";
    private static final String PROPERTY_NAME = "prop_name";
    private static final String PROPERTY_DESCRIPTION = "Some prop description.";
    private static final String USER_VALUE = "DUMMY";

    private Map<String, Object> manifestFile;
    private Map<String, Object> properties;

    private SandboxManager initValid(String manifestData, int stackElem) throws IOException {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[stackElem];
        String[] classElems = ste.getClassName().split("\\.");
        String suiteName = classElems[classElems.length - 1];
        String testName = ste.getMethodName();
        SandboxManager manager = new SandboxManager(suiteName + "_" + testName);
        manager.writeToTemplateDir(".ideless", manifestData);
        return manager;
    }

    private SandboxManager initValid(String manifestData) throws IOException {
        return initValid(GSON.toJson(manifestData), 3);
    }

    private SandboxManager initValid(Map<String, Object> manifestData) throws IOException {
        return initValid(GSON.toJson(manifestData), 3);
    }

    private static String runInitCommand(SandboxManager manager) throws Exception {
        return runInitCommand(manager, manager.getTemplateDirName());
    }

    private static String runInitCommand(SandboxManager manager, String parameter) throws Exception {
        String output = manager.getRunner().run("init " + parameter);
        manager.write("output.log", output);
        return output;
    }

    private void assertFileInUserHomeMatches(Path path, String expectedContent) throws IOException {
        try (Stream<String> stream = Files.lines(IDELESS_HOME_DIR.resolve(path))) {
            Assert.assertEquals(expectedContent, stream.collect(Collectors.joining("\n")));
        }
    }

    private static String runInitCommandWithoutSavingTemplate(SandboxManager manager) throws Exception {
        manager.getRunner().addInput("\n");
        return runInitCommand(manager);
    }

    @Before
    public void beforeTest() {
        manifestFile = new HashMap<>();
        properties = new HashMap<>();
    }

    @After
    public void afterTest() throws IOException {
        cleanupUserHome();
    }

    private void cleanupUserHome() throws IOException {
        File dir = IDELESS_HOME_DIR.toFile();
        if (dir.exists()) {
            FileUtils.cleanDirectory(dir);
            dir.delete();
        }
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
        assertThat(out, startsWith("Error: Invalid ideless template ("));
    }

    @Test
    public void absentManifestFile() throws Exception {
        SandboxManager manager = new SandboxManager("InitCommandIT_absentManifestFile");
        String out = runInitCommand(manager);
        assertThat(out, startsWith("Error: Invalid ideless template ("));
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

        String out = runInitCommandWithoutSavingTemplate(manager);
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

        String out = runInitCommandWithoutSavingTemplate(manager);
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

        String out = runInitCommandWithoutSavingTemplate(manager);
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

        String out = runInitCommandWithoutSavingTemplate(manager);
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

        String out = runInitCommandWithoutSavingTemplate(manager);
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

        String out = runInitCommandWithoutSavingTemplate(manager);
        assertThat(out, containsString("Initializing file: " + FILE_NAME));
        Assert.assertEquals(BEFORE_EXPR + USER_VALUE + AFTER_EXPR, manager.read(FILE_NAME));
    }

    @Test
    public void shallPutContentInNewDirectory() throws Exception {
        String directory = "directory";
        manifestFile.put("initFiles", Arrays.asList(FILE_NAME));
        manifestFile.put("directory", directory);
        SandboxManager manager = initValid(manifestFile);
        manager.writeToTemplateDir(FILE_NAME, FILE_DATA);

        String expectedTargetFile = directory + "/" + FILE_NAME;
        String out = runInitCommandWithoutSavingTemplate(manager);
        assertThat(out, containsString("Initializing file: " + expectedTargetFile));
        Assert.assertEquals(FILE_DATA, manager.read(expectedTargetFile));
    }

    @Test
    public void shallReturnErrorWhenUndefinedPropertyUsedInDirectoryDefinition() throws Exception {
        final String undefined = "undefined";
        final String undefinedVariable = "$" + undefined;
        manifestFile.put("initFiles", Arrays.asList(FILE_NAME));
        manifestFile.put("directory", undefinedVariable);
        SandboxManager manager = initValid(manifestFile);

        String out = runInitCommand(manager);
        assertThat(out, startsWith("Error: Undefined variable: '" + undefined + "'"));
    }

    @Test
    public void shallPutContentInNewDirectoryNamedByProperty() throws Exception {
        properties.put("name", PROPERTY_NAME);
        properties.put("description", PROPERTY_DESCRIPTION);
        manifestFile.put("properties", Arrays.asList(properties));
        manifestFile.put("initFiles", Arrays.asList(FILE_NAME));
        manifestFile.put("directory", "$properties." + PROPERTY_NAME);
        SandboxManager manager = initValid(manifestFile);

        String directory = "directory";
        manager.getRunner().addInput(directory);
        manager.writeToTemplateDir(FILE_NAME, FILE_DATA);

        String expectedTargetFile = directory + "/" + FILE_NAME;
        String out = runInitCommandWithoutSavingTemplate(manager);
        assertThat(out, containsString("Initializing file: " + expectedTargetFile));
        Assert.assertEquals(FILE_DATA, manager.read(expectedTargetFile));
    }

    @Test
    public void shallAskForSaveasNameAndSaveItInSuchDirectoryInUserHome() throws Exception {
        final String saveasName = "someName";

        manifestFile.put("initFiles", Arrays.asList(FILE_NAME));
        SandboxManager manager = initValid(manifestFile);
        manager.writeToTemplateDir(FILE_NAME, FILE_DATA);

        manager.getRunner().addInput(saveasName);

        String out = runInitCommand(manager);
        assertThat(out, containsString("Save template as (leave empty if you don't want to save it): "));
        assertFileInUserHomeMatches(Paths.get(saveasName, FILE_NAME), FILE_DATA);
    }

    @Test
    public void shallUseSavedTemplate() throws Exception {
        final String saveasName = "someName";

        manifestFile.put("initFiles", Arrays.asList(FILE_NAME));
        SandboxManager manager = initValid(manifestFile);
        manager.writeToTemplateDir(FILE_NAME, FILE_DATA);

        manager.getRunner().addInput(saveasName);

        runInitCommand(manager);
        String out = runInitCommand(manager, saveasName);
        assertThat(out, containsString("Initializing file: " + FILE_NAME));
        assertThat(out, not(containsString("Save template as (leave empty if you don't want to save it): ")));
        Assert.assertEquals(FILE_DATA, manager.read(FILE_NAME));
    }

}
