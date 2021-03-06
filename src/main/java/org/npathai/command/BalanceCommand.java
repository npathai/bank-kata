package org.npathai.command;

import org.npathai.domain.account.AccountService;

public class BalanceCommand implements Command {

    private final String balanceCommand;
    private final AccountService accountService;

    public BalanceCommand(String balanceCommand, AccountService accountService) {
        this.balanceCommand = balanceCommand;
        this.accountService = accountService;
    }

    @Override
    public CommandResponse execute() {
        String accountNo = balanceCommand.split(" ")[0];
        BalanceRequest balanceRequest = new BalanceRequest(accountNo);
        Long balance = accountService.getBalance(balanceRequest);
        return CommandResponse.of("Balance Rs " + balance);
    }
}
