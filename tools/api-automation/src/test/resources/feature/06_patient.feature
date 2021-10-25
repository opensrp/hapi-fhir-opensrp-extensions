@Patient
Feature: Patient

  @POST
  Scenario: Create Patient
    Given I am Testing Case : "480"
    And I Set POST Patient service api endpoint
    When I Set request HEADER and PAYLOAD
    And Send a POST HTTP request
    Then I receive valid Response for POST Patient service

  @GET
  Scenario: Read Patient
    Given I am Testing Case : "481"
    And I Set GET Patient api endpoint
    When I Set request HEADER
    And Send a GET HTTP request
    Then I receive valid Response for GET Patient service


  @GET
  Scenario: Read Patient for specific Organization
    Given I am Testing Case : "482"
    And I Set GET Patient api endpoint for specific Organization
    When I Set request HEADER
    And Send a GET HTTP request
    Then I receive valid Response for GET Patient service


  @PUT
  Scenario: Update Patient
    Given I am Testing Case : "483"
    And I Set PUT Facility Patient api endpoint
    When I Set request HEADER and PAYLOAD
    And Send a PUT HTTP request
    Then I receive valid Response for PUT Patient service
