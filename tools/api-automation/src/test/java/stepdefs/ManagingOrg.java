package stepdefs;

import config.ConfigProperties;
import config.EndpointURLs;
import config.EnvGlobals;
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
        EnvGlobals.managingOrgId = ReusableFunctions.getResponsePath("id");
        System.out.println(EnvGlobals.managingOrgId);
    }

    @When("users send get request for reading the organization")
    public void users_send_get_request_for_reading_the_organization() {
        ReusableFunctions.headers();
        ReusableFunctions.whenFunction(Hooks.HTTP_METHOD_GET, ConfigProperties.baseUrl + String.format(EndpointURLs.GET_MANAGING_ORGANIZATION_URL, EnvGlobals.managingOrgId));

    }

    @Then("the server return a success status code for reading")
    public void the_server_return_a_success_status_code_for_reading() {
        ReusableFunctions.thenFunction(Hooks.HTTP_RESPONSE_SUCCESS);
        System.out.println(ReusableFunctions.getResponse());
    }

    @When("users send update request for existing managing organization")
    public void users_send_update_request_for_existing_managing_organization() {
        String RequestPayLoad = ManigingOrgPayload.updateManagingOrg(EnvGlobals.managingOrgId);
        System.out.println(RequestPayLoad);
        ReusableFunctions.givenHeaderPayload(ReusableFunctions.headers(), RequestPayLoad);
        ReusableFunctions.whenFunction(Hooks.HTTP_METHOD_PUT, ConfigProperties.baseUrl + String.format(EndpointURLs.GET_MANAGING_ORGANIZATION_URL, EnvGlobals.managingOrgId));
    }

    @Then("the server return a success status code for update")
    public void the_server_return_a_success_status_code_for_update() {
        ReusableFunctions.thenFunction(Hooks.HTTP_RESPONSE_SUCCESS);
        System.out.println(ReusableFunctions.getResponse());
    }
}
