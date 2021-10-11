Feature: Patient Resource

  @test
  Scenario: Verify the creation of managing Organization
    Given I am Testing Case : "225371"
    When users send post request for creating the organization
    Then the server return a success status code for creation