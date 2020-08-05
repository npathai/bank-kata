package org.npathai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CommandFactoryTest {
    static final String ACCOUNT_NO = UUID.randomUUID().toString();
    static final String OPEN_ACCOUNT_COMMAND = "open account Alice";
    static final String WITHDRAW_COMMAND = ACCOUNT_NO + " withdraw 1000";
    static final String DEPOSIT_COMMAND = ACCOUNT_NO + " deposit 1000";
    static final String SHOW_STATEMENT_COMMAND = ACCOUNT_NO + " statement";
    private static final String TRANSFER_COMMAND = ACCOUNT_NO + " transfer " + UUID.randomUUID().toString() + " 1000";

    @Mock
    AccountService accountService;

    CommandFactory commandFactory;

    @BeforeEach
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        commandFactory = new CommandFactory(accountService);
    }
    
    @Test
    public void returnsOpenAccountCommand() {
        assertThat(commandFactory.createCommand(OPEN_ACCOUNT_COMMAND)).isInstanceOf(OpenAccountCommand.class);
    }

    @Test
    public void returnsWithdrawAccountCommand() {
        assertThat(commandFactory.createCommand(WITHDRAW_COMMAND)).isInstanceOf(WithdrawCommand.class);
    }

    @Test
    public void returnsDepositAccountCommand() {
        assertThat(commandFactory.createCommand(DEPOSIT_COMMAND)).isInstanceOf(DepositCommand.class);
    }

    @Test
    public void returnsShowStatementCommand() {
        assertThat(commandFactory.createCommand(SHOW_STATEMENT_COMMAND)).isInstanceOf(ShowStatementCommand.class);
    }

    @Test
    public void returnsTransferCommand() {
        assertThat(commandFactory.createCommand(TRANSFER_COMMAND)).isInstanceOf(TransferCommand.class);
    }
}