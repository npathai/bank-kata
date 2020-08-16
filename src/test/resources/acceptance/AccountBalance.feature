Feature: Account Balance

  Background:
    Given "Alice" is an account holder

  Scenario: As a account holder, I want to check current balance in my account,
    so that I can see my savings

    When "Alice" deposits Rs 1000 to her account
    And "Alice" withdraws Rs 500 from her account
    And "Alice" deposits Rs 10 to her account
    Then "Alice" should see balance of Rs 510
