@resetSession
Feature: Update Notification Settings
  Background:
    Given the user is logged in
  @notifications
  Scenario: Enable or disable notifications
    Given user is on Profile page
    When user clicks notification preferences button
    And notification preferences page should be displayed
    When user toggles all notification preferences
    Then user clicks update preferences button