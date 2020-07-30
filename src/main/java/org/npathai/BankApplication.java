package org.npathai;

import java.util.*;
import java.util.concurrent.Executor;

public class BankApplication {
    private final Console console;
    private final Executor executorService;
    private final CommandExecutor commandExecutor;
    private Map<String, Account> accountByAccountNo = new HashMap<>();

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
                for (String outputLine : outputLines) {
                    console.write(outputLine);
                }
            }
        });
    }

    private void processCommand(String command) {
        if (command.startsWith("open account")) {
            System.out.println("Processing Account Open Command");
            openAccount(command);
        } else if (command.contains(" deposit ")) {
            System.out.println("Processing Account Deposit Command");
            depositAmount(command);
        } else if (command.contains(" withdraw ")) {
            System.out.println("Processing Account Withdrawal Command");
            withdrawAccount(command);
        } else if (command.endsWith("statement")) {
            showStatement(command);
        }
    }

    private void showStatement(String command) {
        String[] parts = command.split(" ");
        String accountNo = parts[0];
        StringBuilder output = new StringBuilder();
        console.write("type||amount");
        List<Account.AccountTransaction> transactions = accountByAccountNo.get(accountNo).transactionList;
        for (Account.AccountTransaction transaction : transactions) {
            console.write(transaction.type + "||" + transaction.amount);
        }
    }

    private void withdrawAccount(String command) {
        String[] parts = command.split(" ");
        String accountNo = parts[0];
        int amount = Integer.parseInt(parts[2]);
        accountByAccountNo.get(accountNo).withdraw(amount);
    }

    private void depositAmount(String command) {
        String[] parts = command.split(" ");
        String accountNo = parts[0];
        int amount = Integer.parseInt(parts[2]);
        accountByAccountNo.get(accountNo).deposit(amount);
    }

    private void openAccount(String command) {
        String[] parts = command.split(" ");
        String accountHolderName = parts[2];
        Account account = new Account(accountHolderName);
        accountByAccountNo.put(account.accountNo(), account);
        console.write(account.accountNo());
    }

    class Account {
        private String accountNo = UUID.randomUUID().toString();
        private long balance;
        private final String accountHolderName;
        private List<AccountTransaction> transactionList = new ArrayList<>();

        public Account(String accountHolderName) {
            this.accountHolderName = accountHolderName;
        }

        public void deposit(long amount) {
            balance += amount;
            transactionList.add(new AccountTransaction("C", amount));
        }

        public void withdraw(int amount) {
            balance -= amount;
            transactionList.add(new AccountTransaction("D", amount));
        }

        public String accountNo() {
            return accountNo;
        }

        class AccountTransaction {
            String type;
            long amount;

            public AccountTransaction(String type, long amount) {
                this.type = type;
                this.amount = amount;
            }
        }
    }
}
