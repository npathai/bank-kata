package stepdefs;

import intrastructure.Fixture;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
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
        assertThat(application.readOutput()).isEqualTo("type||amount");
        assertThat(application.readOutput()).isEqualTo("C||1000");
        assertThat(application.readOutput()).isEqualTo("C||500");
        assertThat(application.readOutput()).isEqualTo("D||100");
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
        assertThat(application.readOutput()).isEqualTo("type||amount");
        assertThat(application.readOutput()).isEqualTo("D||" + amount);
    }

    @And("{string} should see a credit of Rs {int} in account")
    public void shouldSeeACreditOfInAccount(String accountHolderName, int amount) {
        application.willReceive(accountNoByAccountHolderName.get(accountHolderName) + " statement");
        assertThat(application.readOutput()).isEqualTo("type||amount");
        assertThat(application.readOutput()).isEqualTo("C||" + amount);
    }
}
