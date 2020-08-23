package stepdefs;

import intrastructure.AccountHolder;
import intrastructure.Fixture;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountStepDefs {

    private final Fixture application;
    private final AccountHolder accountHolder;

    public AccountStepDefs(Fixture application, AccountHolder accountHolder) {
        this.application = application;
        this.accountHolder = accountHolder;
    }

    @Given("{string} is an account holder")
    public void alice_is_an_account_holder(String accountHolderName) {
        application.willReceive("open account " + accountHolderName);
        accountHolder.saveAccount(accountHolderName, application.readOutput());
    }

    @Given("{string} is an account holder with initial balance of Rs {int}")
    public void alice_is_an_account_holder(String accountHolderName, int initialBalance) {
        application.willReceive("open account " + accountHolderName);
        accountHolder.saveAccount(accountHolderName, application.readOutput());
        userDepositsToAccount(accountHolderName, initialBalance);
    }

    @Given("{string} is an account holder with initial balance of Rs {int} on {string}")
    public void alice_is_an_account_holder_on(String accountHolderName, int initialBalance, String createdOn) {
        LocalDateTime createdOnTime = timeFrom(createdOn);
        application.willReceive("open account " + accountHolderName, createdOnTime);
        accountHolder.saveAccount(accountHolderName, application.readOutput());
        LocalDateTime localDateTime = timeFrom(createdOn);
        application.willReceive(accountHolder.getAccountNoByName(accountHolderName) + " deposit " + initialBalance,
                localDateTime);
        assertThat(application.readOutput()).isEqualTo("Successfully deposited Rs " + initialBalance);
    }

    private LocalDateTime timeFrom(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return LocalDateTime.of(date, LocalTime.of(0, 0, 0));
    }

    @When("{string} deposits Rs {int} to her account")
    public void userDepositsToAccount(String accountHolderName, int amount) {
        application.willReceive(accountHolder.getAccountNoByName(accountHolderName) + " deposit " + amount);
        assertThat(application.readOutput()).isEqualTo("Successfully deposited Rs " + amount);
    }

    @When("{string} deposits Rs {int} to her account on {string}")
    public void depositsRsToHerAccountOn(String accountHolderName, int amount, String depositedOn) {
        LocalDateTime localDateTime = timeFrom(depositedOn);
        application.willReceive(accountHolder.getAccountNoByName(accountHolderName) + " deposit " + amount,
                localDateTime);
        assertThat(application.readOutput()).isEqualTo("Successfully deposited Rs " + amount);
    }

    @When("{string} withdraws Rs {int} from her account")
    public void userWithdrawsFromAccount(String accountHolderName, int amount) {
        application.willReceive(accountHolder.getAccountNoByName(accountHolderName) + " withdraw " + amount);
        assertThat(application.readOutput()).isEqualTo("Successfully withdrawn Rs " + amount);
    }

    @When("{string} withdraws Rs {int} from her account on {string}")
    public void withdrawsRsFromHerAccountOn(String accountHolderName, int amount, String withdrawnOn) {
        LocalDateTime localDateTime = timeFrom(withdrawnOn);
        application.willReceive(accountHolder.getAccountNoByName(accountHolderName) + " withdraw " + amount,
                localDateTime);
        assertThat(application.readOutput()).isEqualTo("Successfully withdrawn Rs " + amount);
    }

    @When("{string} transfers Rs {int} to {string}'s account")
    public void transfersToSAccount(String fromAccountHolderName, int amount, String toAccountHolderName) {
        String fromAccountNo = accountHolder.getAccountNoByName(fromAccountHolderName);
        String toAccountNo = accountHolder.getAccountNoByName(toAccountHolderName);
        application.willReceive("transfer " + fromAccountNo + " " + toAccountNo + " " + amount);
    }

    @When("{string} closes her account")
    public void closesHerAccount(String accountHolderName) {
        application.willReceive("close " + accountHolder.getAccountNoByName(accountHolderName));
        assertThat(application.readOutput()).isEqualTo("Account closed");
    }

    @Then("{string} cannot make any further transactions from account")
    public void cannotMakeAnyFurtherTransactionsFromAccount(String accountHolderName) {
        application.willReceive(accountHolder.getAccountNoByName(accountHolderName) + " withdraw " + 1);
        assertThat(application.readOutput()).isEqualTo("Account is closed, cannot make any transaction");
        application.willReceive(accountHolder.getAccountNoByName(accountHolderName) + " deposit " + 1);
        assertThat(application.readOutput()).isEqualTo("Account is closed, cannot make any transaction");
    }

    @And("{string} has closed the account")
    public void hasClosedTheAccount(String accountHolderName) {
        application.willReceive("close " + accountHolder.getAccountNoByName(accountHolderName));
        application.readOutput();
    }

    @And("{string} has deposited Rs {int} to her account")
    public void hasDepositedRsToHerAccount(String accountHolderName, int amount) {
        userDepositsToAccount(accountHolderName, amount);
    }

    @And("{string} has withdrawn Rs {int} from her account")
    public void hasWithdrawnRsFromHerAccount(String accountHolderName, int amount) {
        userWithdrawsFromAccount(accountHolderName, amount);
    }

    @Then("{string} should fail to transfer due to insufficient funds")
    public void shouldFailToTransferDueToInsufficientFunds(String accountHolderName) {
        assertThat(application.readOutput()).isEqualTo("Insufficient funds in account");
    }

    @Then("{string} should fail to withdraw amount due to minimum balance requirement")
    public void shouldFailToWithdrawAmountToMinimumBalanceRequirement(String accountHolderName) {
        assertThat(application.readOutput()).isEqualTo("Must maintain minimum balance of 500");
    }

    @Then("{string} should fail to transfer amount due to minimum balance requirement")
    public void shouldFailToTransferAmountDueToMinimumBalanceRequirement(String accountHolderName) {
        assertThat(application.readOutput()).isEqualTo("Must maintain minimum balance of 500");
    }

    @Then("{string} should fail to transfer due to payee account closure")
    public void shouldFailToTransferDueToPayeeAccountClosure(String accountHolderName) {
        assertThat(application.readOutput()).isEqualTo("Payee account is closed. Amount will be reversed back to your account.");
    }

    @Given("{string} is a zero balance account holder")
    public void isAZeroBalanceAccountHolder(String accountHolderName) {
        application.willReceive("open zero balance account " + accountHolderName);
        accountHolder.saveAccount(accountHolderName, application.readOutput());
    }

    @Then("{string} is allowed to withdraw the amount")
    public void isAllowedToWithdrawTheAmount(String accountHolderName) {

    }

    @When("{string} enters incorrect command")
    public void entersIncorrectCommand(String accountHolderName) {
        application.willReceive("unknown command");
    }

    @Then("{string} should see unknown command failure message")
    public void shouldSeeUnknownCommandFailureMessage(String accountHolderName) {
        assertThat(application.readOutput()).isEqualTo("Unknown command");
    }

    @When("{string} tries to withdraw Rs {int} from her account")
    public void triesToWithdrawRsFromHerAccount(String accountHolderName, int amount) {
        application.willReceive(accountHolder.getAccountNoByName(accountHolderName) + " withdraw " + amount);
    }

    @Then("{string} should see balance of Rs {int}")
    public void shouldSeeBalanceOfRs(String accountHolderName, int balance) {
        application.willReceive(accountHolder.getAccountNoByName(accountHolderName) + " balance");
        assertThat(application.readOutput()).isEqualTo("Balance Rs " + balance);
    }

    @Then("{string} account balance should be Rs {int}")
    public void accountBalanceShouldBeRs(String accountHolderName, int expectedBalance) {
        application.willReceive(accountHolder.getAccountNoByName(accountHolderName) + " balance");
        assertThat(application.readOutput()).isEqualTo("Balance Rs " + expectedBalance);
    }
}
