package org.npathai;

import com.google.common.base.Joiner;

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
            String command;
            while (!(command = console.readLine()).equals("q")) {
                List<String> outputLines = commandExecutor.executeCommand(command);
                if (outputLines.isEmpty()) {
                    continue;
                }
                String output = Joiner.on(System.lineSeparator()).join(outputLines);
                console.write(output);
            }
        });
    }
}
