@resetSession
Feature: Re-ordering selfcollect

  Background:
    Given the user is logged in

  @reorder
  Scenario: Make a selfcollect reorder
    Given the user is on order listing page
    When user opens the first Self-Collect order with highest priority status
    Then the order details page should be displayed
    When user clicks re-order button
    And cart page should be displayed with the same items as the original order
    Then user should be able to complete the selfcollect order

