package com.github.ideless;

import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import static org.mockito.Mockito.*;

public class CommandDispatcherTest {

    private static final List<String> ARGS = Arrays.asList("arg1", "arg2");
    private static final String COMMAND_NAME = "command";

    private SafeCommandHandler defaultHandler;
    private SafeCommandHandler errorHandler;
    private CommandHandler commandHandler;
    private CommandDispatcher sut;

    @Before
    public void beforeTest() {
        defaultHandler = mock(SafeCommandHandler.class);
        errorHandler = mock(SafeCommandHandler.class);
        commandHandler = mock(CommandHandler.class);
        sut = new CommandDispatcher(defaultHandler, errorHandler);
    }

    private String[] createArgs(String command) {
        String[] result = { command, ARGS.get(0), ARGS.get(1)};
        return result;
    }

    @Test
    public void shallDispatchToDefaultWhenNothingsProvided() throws Exception {
        sut.dispatch(null);
        verify(defaultHandler).handle(Arrays.asList());
    }

    @Test
    public void shallDispatchToDefaultWhenUnknownCommandProvided() throws Exception {
        sut.addHandler(COMMAND_NAME, commandHandler);
        sut.dispatch(createArgs("unknown"));
        verify(defaultHandler).handle(ARGS);
        verify(errorHandler, never()).handle(Matchers.any());
        verify(commandHandler, never()).handle(Matchers.any());
    }

    @Test
    public void shallDispatchToCommandHandler() throws Exception {
        sut.addHandler(COMMAND_NAME, commandHandler);
        sut.dispatch(createArgs(COMMAND_NAME));
        verify(defaultHandler, never()).handle(Matchers.any());
        verify(errorHandler, never()).handle(Matchers.any());
        verify(commandHandler).handle(ARGS);
    }

    @Test
    public void shallDispatchToCommandHandlerWithEmptyList() throws Exception {
        sut.addHandler(COMMAND_NAME, commandHandler);
        String[] args = { COMMAND_NAME };
        sut.dispatch(args);
        verify(commandHandler).handle(Arrays.asList());
    }

    @Test
    public void shallCallErrorHandlerWithMessageWhenExceptionThrownByHandler() throws Exception {
        final String message = "dummy message";
        CommandHandler throwingHandler = (List<String> t) -> {
            throw new Exception(message);
        };
        sut.addHandler(COMMAND_NAME, throwingHandler);
        sut.dispatch(createArgs(COMMAND_NAME));
        verify(defaultHandler, never()).handle(Matchers.any());
        verify(errorHandler).handle(Arrays.asList(message));
    }

}
