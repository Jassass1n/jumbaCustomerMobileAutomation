@resetSession
Feature: Place Orders

  Background:
    Given the user is logged in

  @order @selfcollect @paynow
  Scenario: Proceed with self collect order placement
    Given the user is on fulfilment details page
    When user inputs plate number
    And user clicks Proceed to payment button
    Then payment page should be displayed
    When user selects a bank from the dropdown
    And user enters a unique reference number for bank transfer
    And user uploads proof of payment image
    Then confirm payment button should be enabled
    When user clicks confirm payment button
    And user clicks complete order button
    When user clicks view order
    Then the order details page should be displayed with correct payment info
#
#  @order @delivery @paynow
#  Scenario: Place delivery order
#    Given the user is on fulfilment details page
#    When the user switches to the Delivery method page
#    And user clicks Proceed to payment button
#    Then payment page should be displayed
#    When user selects a bank from the dropdown
#    And user enters a unique reference number for bank transfer
#    And user uploads proof of payment image
#    Then confirm payment button should be enabled
#    When user clicks confirm payment button
#    And user clicks complete order button
#    When user clicks view order
#    Then the order details page should be displayed with correct payment info