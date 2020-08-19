Feature: Transfer Funds

  Background:
    Given "Alice" is an account holder with initial balance of Rs 2000
    And "Bob" is an account holder

  Scenario: As an account holder, I want to transfer funds, so that I can pay
    funds online to known payees

    When "Alice" transfers Rs 1000 to "Bob"'s account
    Then "Alice" account balance should be Rs 1000
    And "Bob" should see balance of Rs 1000

  Scenario: As an account holder, I want amount to be reversed back to my account
    if funds cannot be transferred to payee account and user should be shown helpful message that the amount
    will be reversed back to their account

    Given "Bob" has closed the account
    When "Alice" transfers Rs 1000 to "Bob"'s account
    Then "Alice" should fail to transfer due to payee account closure
    And "Alice" should see balance of Rs 2000

  Scenario: As a bank, I don't want account holders to be able to transfer more than
    current balance in account

    Given "Alice" has withdrawn Rs 100 from her account
    And "Alice" has deposited Rs 500 to her account
    When "Alice" transfers Rs 2401 to "Bob"'s account
    Then "Alice" should fail to transfer due to insufficient funds

  Scenario: As a bank, I don't want account holders to be able to transfer
    amount such that balance goes below minimum amount of 500

    And "Alice" has withdrawn Rs 100 from her account
    And "Alice" has deposited Rs 500 to her account
    When "Alice" transfers Rs 1901 to "Bob"'s account
    Then "Alice" should fail to transfer amount due to minimum balance requirement

