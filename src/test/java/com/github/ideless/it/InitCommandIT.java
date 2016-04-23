package com.github.ideless.it;

import com.github.ideless.running.ExecutableReturnedErrorException;
import com.github.ideless.running.Runner;
import com.github.ideless.running.RunnerException;
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
    public void absentTemplate() throws IOException, ExecutableReturnedErrorException, RunnerException {
        String out = Runner.run("init strangeTemplate");
        assertThat(out, startsWith("Error: Invalid ideless template"));
    }

}
