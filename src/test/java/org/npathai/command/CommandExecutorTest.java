package org.npathai.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class CommandExecutorTest {

    static final String COMMAND = "open account Alice";

    CommandExecutor commandExecutor;
    @Mock
    CommandFactory commandFactory;

    @BeforeEach
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        commandExecutor = new CommandExecutor(commandFactory);
    }

    @Test
    public void executesAndReturnsResponseOfCommandWhenThereIsAMatchingOne() {
        Command command = Mockito.mock(Command.class);
        when(commandFactory.createCommand(COMMAND)).thenReturn(command);
        when(command.execute()).thenReturn(List.of("Account created"));

        CommandResponse expectedCommandResponse = new CommandResponse("Account created");
        assertThat(commandExecutor.executeCommand(COMMAND)).isEqualTo(expectedCommandResponse);
    }
    
    @Test
    public void gracefullyHandlesWhenCommandIsUnknown() {
        doThrow(UnknownCommandException.class).when(commandFactory).createCommand(COMMAND);
        CommandResponse commandResponse = new CommandResponse("Unknown command");
        assertThat(commandExecutor.executeCommand(COMMAND)).isEqualTo(commandResponse);
    }
}