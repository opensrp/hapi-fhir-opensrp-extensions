@PractitionerRole
Feature: PractitionerRole

  @POST
  Scenario: Create Practitioner Role
    Given I am Testing Case : "461"
    And I Set POST Practitioner Role service api endpoint
    When I Set request HEADER and PAYLOAD
    And Send a POST HTTP request
    Then I receive valid Response for POST Practitioner Role service

  @GET
  Scenario: Read Practitioner Role
    Given I am Testing Case : "462"
    And I Set GET Practitioner Role api endpoint
    When I Set request HEADER
    And Send a GET HTTP request
    Then I receive valid Response for GET Practitioner Role service

  @GET
  Scenario: Read Practitioner Role for specific location
    Given I am Testing Case : "463"
    And I Set GET Practitioner Role api endpoint for specific location
    When I Set request HEADER
    And Send a GET HTTP request
    Then I receive valid Response for GET Practitioner Role service

  @GET
  Scenario: Read Practitioner Role for specific Organization
    Given I am Testing Case : "464"
    And I Set GET Practitioner Role api endpoint for specific Organization
    When I Set request HEADER
    And Send a GET HTTP request
    Then I receive valid Response for GET Practitioner Role service

  @GET
  Scenario: Read Practitioner Role for specific practitioner
    Given I am Testing Case : "465"
    And I Set GET Practitioner Role api endpoint for specific Organization
    When I Set request HEADER
    And Send a GET HTTP request
    Then I receive valid Response for GET Practitioner Role service

  @PUT
  Scenario: Update Practitioner Role
    Given I am Testing Case : "466"
    And I Set PUT Facility Practitioner Role api endpoint
    When I Set request HEADER and PAYLOAD
    And Send a PUT HTTP request
    Then I receive valid Response for PUT Practitioner Role service
