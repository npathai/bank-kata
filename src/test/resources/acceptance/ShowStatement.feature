Feature: Account Statement

  Background:
    Given "Alice" is an account holder with initial balance of Rs 1000

  Scenario: As an account holder, I want to see the past transactions, so that I can verify
    transactions I made
    When "Alice" deposits Rs 500 to her account
    And "Alice" withdraws Rs 100 from her account
    And "Alice" opens account statement
    Then "Alice" should see statement:
    |type|amount|
    | C  | 1000 |
    | C  | 500  |
    | D  | 100  |


  Scenario: As an account holder, I want ability to filter transactions of type DEPOSIT,
    so that I can easily navigate and understand my earnings

    When "Alice" has withdrawn Rs 100 from her account
    And "Alice" has deposited Rs 500 to her account
    And "Alice" has withdrawn Rs 200 from her account
    When "Alice" opens account statement filtered by type "deposit"
    Then "Alice" should see statement:
      |type|amount|
      | C  | 1000 |
      | C  | 500  |

  Scenario: As an account holder, I want ability to filter transactions of type WITHDRAWAL,
  so that I can easily navigate and understand my expenditures

    And "Alice" has withdrawn Rs 100 from her account
    And "Alice" has deposited Rs 500 to her account
    And "Alice" has withdrawn Rs 200 from her account
    When "Alice" opens account statement filtered by type "withdrawal"
    Then "Alice" should see statement:
      |type|amount|
      | D  | 100  |
      | D  | 200  |