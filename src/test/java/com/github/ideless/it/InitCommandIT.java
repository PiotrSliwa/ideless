package com.github.ideless.it;

import com.github.ideless.running.ExecutableReturnedErrorException;
import com.github.ideless.running.RunnerException;
import com.github.ideless.running.SandboxManager;
import java.io.IOException;
import static org.hamcrest.CoreMatchers.startsWith;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertThat;

public class InitCommandIT {

    private SandboxManager initValid(String manifestData) throws IOException {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
        String[] classElems = ste.getClassName().split("\\.");
        String suiteName = classElems[classElems.length - 1];
        String testName = ste.getMethodName();
        SandboxManager manager = new SandboxManager(suiteName + "_" + testName);
        manager.writeToTemplateDir(".ideless", manifestData);
        return manager;
    }

    @Test
    public void lackOfTemplateParameter() throws ExecutableReturnedErrorException, RunnerException {
        SandboxManager manager = new SandboxManager("InitCommandIT_lackOfTemplateParameter");
        String out = manager.getRunner().run("init");
        assertThat(out, startsWith("Usage: "));
    }

    @Test
    public void absentTemplateDirectory() throws IOException, ExecutableReturnedErrorException, RunnerException {
        SandboxManager manager = new SandboxManager("InitCommandIT_absentTemplateDirectory");
        String out = manager.getRunner().run("init strangeTemplate");
        assertThat(out, startsWith("Error: Invalid ideless template directory ("));
    }

    @Test
    public void absentManifestFile() throws ExecutableReturnedErrorException, RunnerException {
        SandboxManager manager = new SandboxManager("InitCommandIT_absentManifestFile");
        String out = manager.getRunner().run("init " + manager.getTemplateDirName());
        assertThat(out, startsWith("Error: Invalid ideless template directory ("));
    }

    @Test
    public void invalidJsonAsManifestFile() throws ExecutableReturnedErrorException, RunnerException, IOException {
        SandboxManager manager = initValid("invalid json");
        String out = manager.getRunner().run("init " + manager.getTemplateDirName());
        assertThat(out, startsWith("Error: Invalid JSON ("));
    }

    @Test
    public void noInitFilesInManifestFile() throws IOException, ExecutableReturnedErrorException, RunnerException {
        SandboxManager manager = initValid("{\"someField\":\"someValue\"}");
        String out = manager.getRunner().run("init " + manager.getTemplateDirName());
        assertThat(out, startsWith("Error: Lack of 'initFiles' field"));
    }

    @Test
    public void absentInitFile() throws IOException, ExecutableReturnedErrorException, RunnerException {
        SandboxManager manager = initValid("{\"initFiles\":[\"unknownFile\"]}");
        String out = manager.getRunner().run("init " + manager.getTemplateDirName());
        assertThat(out, startsWith("Error: Cannot find file"));
    }

    @Test
    public void shallCopyInitFiles() throws IOException, ExecutableReturnedErrorException, RunnerException {
        String fileName = "file1";
        String fileData = "some data";
        SandboxManager manager = initValid("{\"initFiles\":[\"" + fileName + "\"]}");
        manager.writeToTemplateDir(fileName, fileData);
        String out = manager.getRunner().run("init " + manager.getTemplateDirName());
        assertThat(out, startsWith("Initializing file: " + fileName));
        Assert.assertEquals(fileData, manager.read(fileName));
    }

}
