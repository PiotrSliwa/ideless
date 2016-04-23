package com.github.ideless.it;

import com.github.ideless.running.ExecutableReturnedErrorException;
import com.github.ideless.running.Runner;
import com.github.ideless.running.RunnerException;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.*;
import org.junit.Test;

public class RunIT {

    @Test
    public void noArguments() throws ExecutableReturnedErrorException, RunnerException {
        assertThat(Runner.run(""), startsWith("Usage: "));
    }

    @Test
    public void unknownCommand() throws ExecutableReturnedErrorException, RunnerException {
        assertThat(Runner.run("dummyUnknownCommand"), startsWith("Usage: "));
    }

}
