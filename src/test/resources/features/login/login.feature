Feature: Login Functionality

  Background:
  As a mobile app user
  I want to authenticate using different methods i.e Guest and Registered
  So I can access the application's features

  @notification @resetSession
  Scenario: Handle notification permission on app launch
    When the user allows notifications
    Then the login page should be displayed

  @guestSession @resetSession
  Scenario: Login using guest option
    Given the user is on the login page
    When the user selects "Continue as Guest"
    Then the home page should be displayed

  @otpSession @resetSession
  Scenario: Login using phone number and OTP
    Given the user is on the login page
    When the user enters valid phone number "717432687"
    Then the user enters valid OTP "0000" and is redirected to the home page
    Then the home page should be displayed