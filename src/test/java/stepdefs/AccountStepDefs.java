package stepdefs;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import intrastructure.Fixture;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountStepDefs {

    Fixture application = new Fixture();
    private final Map<String, String> accountNoByAccountHolderName = new HashMap<>();
    private List<String> statement;

    public AccountStepDefs() {
        System.out.println("Instance of AccountStepDefs created");
    }

    @Given("{string} is an account holder")
    public void alice_is_an_account_holder(String accountHolderName) {
        application.willReceive("open account " + accountHolderName);
        accountNoByAccountHolderName.put(accountHolderName, application.readOutput());
    }

    @Given("{string} is an account holder with initial balance of Rs {int}")
    public void alice_is_an_account_holder(String accountHolderName, int initialBalance) {
        application.willReceive("open account " + accountHolderName);
        accountNoByAccountHolderName.put(accountHolderName, application.readOutput());
        userDepositsToAccount(accountHolderName, initialBalance);
    }

    @Then("{string} can see account statement containing all the transactions \\(in order they occurred)")
    public void verify_can_see_account_statement(String accountHolderName) {
        application.willReceive(accountNoByAccountHolderName.get(accountHolderName) + " statement");
        String statement = Joiner.on(System.lineSeparator()).join(List.of(
                "type||amount",
                "C||1000",
                "C||500",
                "D||100"
        ));
        assertThat(application.readOutput()).isEqualTo(statement);
    }

    @When("{string} deposits Rs {int} to her account")
    public void userDepositsToAccount(String accountHolderName, int amount) {
        application.willReceive(accountNoByAccountHolderName.get(accountHolderName) + " deposit " + amount);
    }

    @And("{string} withdraws Rs {int} from her account")
    public void userWithdrawsFromAccount(String accountHolderName, int amount) {
        application.willReceive(accountNoByAccountHolderName.get(accountHolderName) + " withdraw " + amount);
    }

    @When("{string} transfers Rs {int} to {string}'s account")
    public void transfersToSAccount(String fromAccountHolderName, int amount, String toAccountHolderName) {
        String fromAccountNo = accountNoByAccountHolderName.get(fromAccountHolderName);
        String toAccountNo = accountNoByAccountHolderName.get(toAccountHolderName);
        application.willReceive("transfer " + fromAccountNo + " " + toAccountNo + " " + amount);
    }

    @Then("{string} should see a withdrawal of Rs {int} in account")
    public void shouldSeeAWithdrawalOfInAccount(String accountHolderName, int amount) {
        applictionWillReceiveShowStatementCommand(accountNoByAccountHolderName.get(accountHolderName), " statement");
        assertThat(allTransactions(application.readOutput())).contains("D||" + amount);
    }

    private void applictionWillReceiveShowStatementCommand(String s, String s2) {
        application.willReceive(s + s2);
    }

    private List<String> allTransactions(String output) {
        return ImmutableList.copyOf(Splitter.on(System.lineSeparator()).split(output));
    }

    @And("{string} should see a credit of Rs {int} in account")
    public void shouldSeeACreditOfInAccount(String accountHolderName, int amount) {
        application.willReceive(accountNoByAccountHolderName.get(accountHolderName) + " statement");
        assertThat(allTransactions(application.readOutput())).contains("C||" + amount);
    }

    @When("{string} closes her account")
    public void closesHerAccount(String accountHolderName) {
        application.willReceive("close " + accountNoByAccountHolderName.get(accountHolderName));
        assertThat(application.readOutput()).isEqualTo("Account closed");
    }

    @Then("{string} cannot make any further transactions from account")
    public void cannotMakeAnyFurtherTransactionsFromAccount(String accountHolderName) {
        userWithdrawsFromAccount(accountHolderName, 1);
        assertThat(application.readOutput()).isEqualTo("Account is closed, cannot make any transaction");
        userDepositsToAccount(accountHolderName, 1);
        assertThat(application.readOutput()).isEqualTo("Account is closed, cannot make any transaction");
    }

    @And("{string} has closed the account")
    public void hasClosedTheAccount(String accountHolderName) {
        application.willReceive("close " + accountNoByAccountHolderName.get(accountHolderName));
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

    @When("{string} sees account statement filtered by type {string}")
    public void seesAccountStatementFilteredByType(String accountHolderName, String transactionType) {
        application.willReceive(accountNoByAccountHolderName.get(accountHolderName) + " statement --type "
                + toStatementType(transactionType));
        statement = allTransactions(application.readOutput());
    }

    @Then("{string} can see account statement containing {int} transactions of type {string}")
    public void canSeeAccountStatementContainingTransactionsOfType(String accountHolderName, int count,
                                                                   String transactionType) {
        // Doing size - 1 to remove the statement header
        assertThat(statement.size() - 1).isEqualTo(count);
        String statementType = toStatementType(transactionType);
        for (int i = 1; i < count; i++) {
            assertThat(statement.get(i)).contains(statementType);
        }
    }

    private String toStatementType(String transactionType) {
        return "deposit".equals(transactionType) ? "C" : "D";
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
}
