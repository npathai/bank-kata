Feature: Zero balance account
  As a bank, I want account holders to be able to create zero balance accounts, as
  per the mandate of Prime Minister of India.
  The account can be created with zero balance and the account holders don't need to
  maintain minimum balance like regular accounts.

  Scenario: Withdraw all amount
    As a account holder, I can withdraw the whole amount from bank, so that I can use
    all money I have, due to low income

    Given "Alice" is a zero balance account holder
    And "Alice" has deposited Rs 100 to her account
    When "Alice" withdraws Rs 100 from her account
    Then "Alice" is allowed to withdraw the amount
    And "Alice" should see statement:
    | type | amount |
    | C    | 100    |
    | D    | 100    |
