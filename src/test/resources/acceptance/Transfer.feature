Feature: Transfer Funds
  Scenario: As an account holder, I want to transfer funds, so that I can pay
    funds online to known payees

    Given "Alice" is an account holder with initial balance of Rs 1000
    And "Bob" is an account holder
    When "Alice" transfers Rs 1000 to "Bob"'s account
    Then "Alice" should see a withdrawal of Rs 1000 in account
    And "Bob" should see a credit of Rs 1000 in account

  Scenario: As an account holder, I want amount to be reversed back to my account
    if funds cannot be transferred to payee account

    Given "Alice" is an account holder with initial balance of Rs 1000
    And "Bob" is an account holder
    And "Bob" has closed the account
    When "Alice" transfers Rs 1000 to "Bob"'s account
    Then "Alice" should see a withdrawal of Rs 1000 in account
    And "Alice" should see a credit of Rs 1000 in account

  Scenario: As a bank, I don't want account holders to be able to transfer more than
    current balance in account

    Given "Alice" is an account holder with initial balance of Rs 1000
    And "Bob" is an account holder
    And "Alice" has withdrawn Rs 100 from her account
    And "Alice" has deposited Rs 500 to her account
    When "Alice" transfers Rs 1401 to "Bob"'s account
    Then "Alice" should fail to transfer due to insufficient funds

  Scenario: As a bank, I don't want account holders to be able to transfer
    amount such that balance goes below minimum amount of 500

    Given "Alice" is an account holder with initial balance of Rs 1000
    And "Bob" is an account holder
    And "Alice" has withdrawn Rs 100 from her account
    And "Alice" has deposited Rs 500 to her account
    When "Alice" transfers Rs 901 to "Bob"'s account
    Then "Alice" should fail to transfer amount due to minimum balance requirement

