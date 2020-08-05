package stepdefs;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
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
        application.willReceive(accountNoByAccountHolderName.get(accountHolderName) + " statement");
        String lastTransaction = getLastTransactionFromStatement(application.readOutput());
        assertThat(lastTransaction).isEqualTo("D||" + amount);
    }

    private String getLastTransactionFromStatement(String output) {
        Iterable<String> statementParts = Splitter.on(System.lineSeparator()).split(output);
        return Iterables.getLast(statementParts);
    }

    @And("{string} should see a credit of Rs {int} in account")
    public void shouldSeeACreditOfInAccount(String accountHolderName, int amount) {
        application.willReceive(accountNoByAccountHolderName.get(accountHolderName) + " statement");
        String lastTransaction = getLastTransactionFromStatement(application.readOutput());
        assertThat(lastTransaction).isEqualTo("C||" + amount);
    }

    @When("{string} closes her account")
    public void closesHerAccount(String accountHolderName) {
        application.willReceive(accountNoByAccountHolderName.get(accountHolderName) + " close");
        assertThat(application.readOutput()).isEqualTo("Account closed");
    }

    @Then("{string} cannot make any further transactions from account")
    public void cannotMakeAnyFurtherTransactionsFromAccount(String accountHolderName) {
        userWithdrawsFromAccount(accountHolderName, 1);
        assertThat(application.readOutput()).isEqualTo("Account is closed, cannot make any transaction");
        userDepositsToAccount(accountHolderName, 1);
        assertThat(application.readOutput()).isEqualTo("Account is closed, cannot make any transaction");
    }
}
