@Condition
Feature: Condition

  @POST
  Scenario: Create Condition
    Given I am Testing Case : "492"
    And I Set POST Condition service api endpoint
    When I Set request HEADER and PAYLOAD
    And Send a POST HTTP request
    Then I receive valid Response for POST Condition service

  @GET
  Scenario: Read Condition
    Given I am Testing Case : "493"
    And I Set GET Condition api endpoint
    When I Set request HEADER
    And Send a GET HTTP request
    Then I receive valid Response for GET Condition service


  @GET
  Scenario: Read Condition for specific Patient
    Given I am Testing Case : "495"
    And I Set GET Condition api endpoint for specific Patient
    When I Set request HEADER
    And Send a GET HTTP request
    Then I receive valid Response for GET Condition service


  @PUT
  Scenario: Update Condition
    Given I am Testing Case : "494"
    And I Set PUT Condition api endpoint
    When I Set request HEADER and PAYLOAD
    And Send a PUT HTTP request
    Then I receive valid Response for PUT Condition service
