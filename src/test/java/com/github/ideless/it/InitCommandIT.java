package com.github.ideless.it;

import com.github.ideless.running.ExecutableReturnedErrorException;
import com.github.ideless.running.Runner;
import com.github.ideless.running.RunnerException;
import com.github.ideless.running.SandboxManager;
import java.io.IOException;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class InitCommandIT {

    @Test
    public void lackOfTemplateParameter() throws ExecutableReturnedErrorException, RunnerException {
        String out = Runner.run("init");
        assertThat(out, startsWith("Usage: "));
    }

    @Test
    public void absentTemplateDirectory() throws IOException, ExecutableReturnedErrorException, RunnerException {
        String out = Runner.run("init strangeTemplate");
        assertThat(out, startsWith("Error: Invalid ideless template directory ("));
    }

    @Test
    public void absentManifestFile() throws ExecutableReturnedErrorException, RunnerException {
        SandboxManager manager = new SandboxManager("absentManifestFile");
        String out = Runner.run("init " + manager.getPath("."));
        assertThat(out, startsWith("Error: Invalid ideless template directory ("));
    }

    @Test
    public void invalidJsonAsManifestFile() throws ExecutableReturnedErrorException, RunnerException, IOException {
        SandboxManager manager = new SandboxManager("invalidJsonAsManifestFile");
        manager.write(".ideless", "invalid json");
        String out = Runner.run("init " + manager.getPath("."));
        assertThat(out, startsWith("Error: Invalid JSON ("));
    }

}
