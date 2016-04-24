package com.github.ideless.it;

import com.github.ideless.running.ExecutableReturnedErrorException;
import com.github.ideless.running.RunnerException;
import com.github.ideless.running.SandboxManager;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.*;
import org.junit.Test;

public class RunIT {

    @Test
    public void noArguments() throws ExecutableReturnedErrorException, RunnerException {
        SandboxManager manager = new SandboxManager("RunIT_noArguments");
        assertThat(manager.getRunner().run(""), startsWith("Usage: "));
    }

    @Test
    public void unknownCommand() throws ExecutableReturnedErrorException, RunnerException {
        SandboxManager manager = new SandboxManager("RunIT_unknownCommand");
        assertThat(manager.getRunner().run("dummyUnknownCommand"), startsWith("Usage: "));
    }

}
