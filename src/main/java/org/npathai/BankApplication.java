package org.npathai;

public class BankApplication {
    private final Console console;

    public BankApplication(Console  console) {
        this.console = console;
    }

    public void start() {
        String command;
        while (!(command = console.readLine()).equals("q")) {
            processCommand(command);
        }
    }

    private void processCommand(String command) {

    }
}
