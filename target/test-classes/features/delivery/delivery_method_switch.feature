@resetSession
Feature: Switch Delivery Method

  Background:
    Given the user is logged in
  @switchDelivery
  Scenario: Switch delivery method on Home and Products Pages
    Given the user is on the Home page
    When the user switches to the Delivery tab
    Then the Delivery page should be displayed
    When the user switches to the Self Collect tab
    Then the Self Collect page should be displayed
    When user clicks products button
    When the user switches to the products Delivery tab
    Then the user switches back to Self Collect tab