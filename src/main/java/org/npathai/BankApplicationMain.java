package org.npathai;

import org.npathai.command.CommandExecutor;
import org.npathai.command.CommandFactory;
import org.npathai.domain.account.AccountService;
import org.npathai.domain.account.InMemoryAccounts;

import java.util.concurrent.Executors;

public class BankApplicationMain {

    public static void main(String[] args) throws InterruptedException {
        BankApplication bankApplication = new BankApplication(new Console(),
                Executors.newSingleThreadExecutor(),
                new CommandExecutor(new CommandFactory(new AccountService(new InMemoryAccounts()))));
        bankApplication.start();
        Thread.currentThread().join();
    }
}
