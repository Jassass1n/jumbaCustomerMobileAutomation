@resetSession
Feature: Making Payments
  Background:
    Given the user is logged in
  @bankTransferViaOrderListing
  Scenario: Making bank transfer payment via order listing
    Given the user is on order listing page
    When user opens the first Self-Collect order with highest priority status
    Then the order details page should be displayed
    When user clicks pay now link
    Then payment page should be displayed
    When user selects a bank from the dropdown
    And user enters a unique reference number for bank transfer
    And user uploads proof of payment image
    Then confirm payment button should be enabled
    When user clicks confirm payment button
    And user clicks complete order button
    When user clicks view order
    Then the order details page should be displayed with correct payment info
  @wip
  Scenario: Complete payment using Jengwa
  @wip
  Scenario: Complete payment using Mpesa