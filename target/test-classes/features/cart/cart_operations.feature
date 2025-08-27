@guestSession
Feature: Cart Operations

  @cart
  Scenario: View Cart and edit items in the cart
    Given the user is on the cart page
    When user attempts to update the quantity
    Then the cart page should be displayed
    When user clicks Add More Items button
    When the user searches for a product
    And the user taps on the first product in the search results
    Then the Product Details page should be displayed
    When the user selects valid pickup location and clicks add to cart
    And user updates the quantity of the product
    When user clicks View Cart button
    Then the cart page should be displayed with the added item
    And the user clears the cart items
    When the user clicks start shopping button
    And the user clicks home button
    Then the home page should be displayed