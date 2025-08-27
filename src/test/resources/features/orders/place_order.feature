@resetSession
Feature: Place Orders

  Background:
    Given the user is logged in

  @order @selfcollect
  Scenario: Proceed with self collect order placement
    Given the user is on fulfilment details page
    When user inputs plate number
    And user clicks Proceed to payment button
    Then payment page should be displayed
    When user clicks pay balance later button
    When user clicks view order
    Then the order details page should be displayed with correct payment info

  @order @delivery
  Scenario: Place delivery order
    Given the user is on fulfilment details page
    When the user switches to the Delivery method page
    And user clicks Proceed to payment button
    Then payment page should be displayed
    When user clicks pay balance later button
    When user clicks view order
    Then the order details page should be displayed with correct payment info

  @wip
  Scenario: Reorder a past purchase
    # Steps to be implemented

  @wip
  Scenario: Place multiple products in one order (Self-collect & Delivery)
    # Steps to be implemented
