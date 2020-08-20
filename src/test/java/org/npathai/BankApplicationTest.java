package org.npathai;

import com.google.common.util.concurrent.MoreExecutors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.npathai.command.CommandExecutor;
import org.npathai.command.CommandResponse;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

class BankApplicationTest {
    private static final String QUIT = "q";
    private static final String COMMAND_1 = "open account Alice";
    private static final String COMMAND_2 = "open account Ram";

    @Mock
    Console mockConsole;
    @Mock
    CommandExecutor commandExecutor;
    Executor executorService;
    BankApplication bankApplication;
    @Captor
    ArgumentCaptor<Runnable> runnableArgumentCaptor;

    @BeforeEach
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        executorService = MoreExecutors.directExecutor();
        bankApplication = new BankApplication(mockConsole, executorService, commandExecutor);
    }

    @Test
    public void startsProcessingCommandsAsynchronously() {
        Executor mockExecutor = Mockito.mock(Executor.class);
        bankApplication = new BankApplication(mockConsole, mockExecutor, new CommandExecutor(null));
        given(mockConsole.readLine()).willReturn(QUIT);

        bankApplication.start();

        then(mockExecutor).should(times(1)).execute(runnableArgumentCaptor.capture());

        runnableArgumentCaptor.getValue().run();
        then(mockConsole).should().readLine();
    }

    @Test
    public void shouldReadFromConsoleTillQuitCommand() {
        given(mockConsole.readLine()).willReturn(COMMAND_1, COMMAND_2, QUIT);
        given(commandExecutor.executeCommand(anyString())).willReturn(new CommandResponse("Dummy response"));
        bankApplication.start();

        InOrder inOrder = Mockito.inOrder(commandExecutor);
        inOrder.verify(commandExecutor).executeCommand(COMMAND_1);
        inOrder.verify(commandExecutor).executeCommand(COMMAND_2);

        verify(commandExecutor, never()).executeCommand(QUIT);
    }

    @Test
    public void writesTheOutputProducedByCommandToConsoleAsASingleLineSeparatedByNewLine() {
        given(mockConsole.readLine()).willReturn(COMMAND_1, QUIT);
        given(commandExecutor.executeCommand(COMMAND_1)).willReturn(new CommandResponse(List.of("Output Value 1", "Output Value 2")));

        bankApplication.start();

        then(mockConsole).should().write("Output Value 1" + System.lineSeparator() + "Output Value 2");
    }

    @Test
    public void doesNotWriteAnythingToConsoleWhenCommandDoesntReturnAnyOutput() {
        given(mockConsole.readLine()).willReturn(COMMAND_1, QUIT);
        given(commandExecutor.executeCommand(COMMAND_1)).willReturn(new CommandResponse(Collections.emptyList()));

        bankApplication.start();

        verify(mockConsole, never()).write(anyString());
    }
}