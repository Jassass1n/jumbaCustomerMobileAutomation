@resetSession
Feature: Login and Logout
  Background:
    Given the user is logged in
    @logout
  Scenario: Logout from the application
      Given user is on Profile page
      Then the user logs out and returns to login page