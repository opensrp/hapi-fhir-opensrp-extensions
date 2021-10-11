package stepdefs;

import config.ConfigProperties;
import config.EndpointURLs;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import general.ReusableFunctions;
import payloads.ManigingOrgPayload;

public class ManagingOrg {

    @When("users send post request for creating the organization")
    public void users_send_post_request_for_creating_the_organization() {
        String RequestPayLoad = ManigingOrgPayload.createManagingOrg();
        ReusableFunctions.givenHeaderPayload(ReusableFunctions.headers(), RequestPayLoad);
        ReusableFunctions.whenFunction(Hooks.HTTP_METHOD_POST, ConfigProperties.baseUrl + EndpointURLs.MANAGING_ORGANIZATION_URL);
    }

    @Then("the server return a success status code for creation")
    public void the_server_return_a_success_status_code_for_creation() {
        ReusableFunctions.thenFunction(Hooks.HTTP_RESPONSE_CREATED);
        System.out.println(ReusableFunctions.getResponsePath("id"));
    }

}
