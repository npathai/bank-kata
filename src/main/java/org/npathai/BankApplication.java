package org.npathai;

import com.google.common.base.Joiner;
import org.npathai.command.CommandExecutor;
import org.npathai.command.CommandResponse;

import java.util.*;
import java.util.concurrent.Executor;

public class BankApplication {
    private final Console console;
    private final Executor executorService;
    private final CommandExecutor commandExecutor;

    public BankApplication(Console console, Executor executorService, CommandExecutor commandExecutor) {
        this.console = console;
        this.executorService = executorService;
        this.commandExecutor = commandExecutor;
    }

    public void start() {
        executorService.execute(() -> {
            System.out.println("Started reading commands from command line");
            String command;
            while (!(command = console.readLine()).equals("q")) {
                CommandResponse commandResponse = commandExecutor.executeCommand(command);
                if (commandResponse.isEmpty()) {
                    continue;
                }
                String output = Joiner.on(System.lineSeparator()).join(commandResponse.lines());
                console.write(output);
            }
        });
    }
}