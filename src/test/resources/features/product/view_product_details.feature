@guestSession
Feature: Search Product Without Login

  Background:
    Given the user continues as guest
@productSearch
  Scenario: Search and view product details without logging in
    Given the user is on the Home page
    When the user taps on the Search field
    And the user enters a valid product name
    And the user taps on the first product in the search results
    Then the Product Details page should be displayed