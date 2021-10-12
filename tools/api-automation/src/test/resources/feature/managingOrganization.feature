Feature: Patient Resource

 @test @post
  Scenario: Verify the creation of managing Organization
    Given I am Testing Case : "115"
    When users send post request for creating the organization
    Then the server return a success status code for creation



  Scenario: Verify the Reading data of managing Organization
    Given I am Testing Case : "225371"
    When users send get request for reading the organization
    Then the server return a success status code for reading


  Scenario: Verify the update data of managing Organization
    Given I am Testing Case : "225371"
    When users send update request for existing managing organization
    Then the server return a success status code for update

