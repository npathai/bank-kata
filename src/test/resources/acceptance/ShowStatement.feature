Feature: Account Statement

  Background:
    Given "Alice" is an account holder with initial balance of Rs 1000 on "01/01/2020"

  Scenario: As an account holder, I want to see the past transactions in order from latest to oldest,
    so that I can verify transactions I made

    When "Alice" deposits Rs 500 to her account on "02/01/2020"
    And "Alice" withdraws Rs 100 from her account on "03/01/2020"
    And "Alice" opens account statement
    Then "Alice" should see statement:
    |type|amount|date       |
    | D  | 100  |03/01/2020 |
    | C  | 500  |02/01/2020 |
    | C  | 1000 |01/01/2020 |


  Scenario: As an account holder, I want ability to filter transactions of type DEPOSIT,
    so that I can easily navigate and understand my earnings

    When "Alice" withdraws Rs 100 from her account on "02/01/2020"
    And "Alice" deposits Rs 500 to her account on "03/01/2020"
    And "Alice" withdraws Rs 200 from her account on "04/01/2020"
    When "Alice" opens account statement filtered by type "deposit"
    Then "Alice" should see statement:
      |type|amount|date       |
      | C  | 500 |03/01/2020 |
      | C  | 1000  |01/01/2020 |

  Scenario: As an account holder, I want ability to filter transactions of type WITHDRAWAL,
  so that I can easily navigate and understand my expenditures

    And "Alice" withdraws Rs 100 from her account on "02/01/2020"
    And "Alice" deposits Rs 500 to her account on "03/01/2020"
    And "Alice" withdraws Rs 200 from her account on "04/01/2020"
    When "Alice" opens account statement filtered by type "withdrawal"
    Then "Alice" should see statement:
      |type|amount|date       |
      | D  | 200  |04/01/2020 |
      | D  | 100  |02/01/2020 |

  Scenario: As an account holder, I want to filter transactions between given from and to dates,
    so that I can easily check past transactions

    Account holder should only be able to see the transactions that occurred on or within the date
    range.

    When "Alice" withdraws Rs 100 from her account on "02/01/2020"
    And "Alice" deposits Rs 500 to her account on "03/01/2020"
    And "Alice" withdraws Rs 200 from her account on "04/01/2020"
    When "Alice" opens account statement filtered by date from "02/01/2020" to "03/01/2020"
    Then "Alice" should see statement:
      |type|amount|date       |
      | C  | 500  |03/01/2020 |
      | D  | 100  |02/01/2020 |
