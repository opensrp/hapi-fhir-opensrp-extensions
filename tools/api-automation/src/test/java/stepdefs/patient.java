package stepdefs;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class patient {

    public patient() {

    }

    @Given("I am Testing Case : {string}")
    public void i_am_Testing_Case(String caseId) {
         Hooks.caseID = caseId;
    }
    @When("users send post request for patient")
    public void users_send_post_request_for_patient() {
        // Write code here that turns the phrase above into concrete actions
       System.out.println("abc");
    }

    @Then("the server return a success status")
    public void the_server_return_a_success_status() {
        System.out.println("abc");
    }
}
