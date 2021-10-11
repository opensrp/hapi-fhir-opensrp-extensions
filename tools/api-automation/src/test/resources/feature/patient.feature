Feature: Patient Resource


  Scenario: Verify the creation of patient
    Given I am Testing Case : "225371"
    When users send post request for patient
    Then the server return a success status