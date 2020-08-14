Feature: User Interface Friendliness

  Scenario: Graceful handling of unknown commands
    As a bank, I want user interface to provide user with helpful message, when
    the entered command is unknown

    Given "Alice" is an account holder
    When "Alice" enters incorrect command
    Then "Alice" should see unknown command failure message

