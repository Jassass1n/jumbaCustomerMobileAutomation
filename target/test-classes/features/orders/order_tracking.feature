@resetSession
Feature: Track and View Orders
  Background:
    Given the user is logged in
  @trackOrder
  Scenario: Track order status
    Given the user is on the Home page
    When the user switches to the Delivery tab
    Then the Delivery page should be displayed
    When user clicks Track Order button
    And Selects any order from the list
    Then the order tracking page should be displayed

@orderTracking
  Scenario: Track order from order listing
    Given the user is on order listing page
    When user opens the first Delivery order with highest priority status
    Then the order details page should be displayed
    Then track order button should be visible