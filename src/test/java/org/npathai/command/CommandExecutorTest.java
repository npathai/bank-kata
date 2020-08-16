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
    public void executesAndReturnsOutputOfCommandWhenThereIsAMatchingOne() {
        Command command = Mockito.mock(Command.class);
        when(commandFactory.createCommand(COMMAND)).thenReturn(command);
        when(command.execute()).thenReturn(List.of("Account created"));

        assertThat(commandExecutor.executeCommand(COMMAND)).isEqualTo(List.of("Account created"));
    }
    
    @Test
    public void gracefullyHandlesWhenCommandIsUnknown() {
        doThrow(UnknownCommandException.class).when(commandFactory).createCommand(COMMAND);
        assertThat(commandExecutor.executeCommand(COMMAND)).containsExactly("Unknown command");
    }
}