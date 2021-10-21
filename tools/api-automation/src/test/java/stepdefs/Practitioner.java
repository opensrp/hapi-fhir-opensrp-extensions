package stepdefs;

import config.EndpointURLs;
import config.EnvGlobals;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import general.ReusableFunctions;
import payloads.PractitionerPayload;

import static stepdefs.Hooks.RequestPayLoad;
import static stepdefs.Hooks.endPoint;

public class Practitioner {


    @Given("I Set POST Practitioner service api endpoint")
    public void i_Set_POST_Practitioner_service_api_endpoint() {
        endPoint = EndpointURLs.PRACTITIONER_URL;
        RequestPayLoad = PractitionerPayload.createPractitioner();
    }

    @Then("I receive valid Response for POST Practitioner service")
    public void i_receive_valid_Response_for_POST_Practitioner_service() {
        ReusableFunctions.thenFunction(Hooks.HTTP_RESPONSE_CREATED);
        EnvGlobals.PractitionerId = ReusableFunctions.getResponsePath("id");
    }

    @Given("I Set GET Practitioner api endpoint")
    public void i_Set_GET_Practitioner_api_endpoint() {
        endPoint = EndpointURLs.GET_PRACTITIONER_URL;
        endPoint= String.format(endPoint, EnvGlobals.PractitionerId);
    }

    @Then("I receive valid Response for GET Practitioner service")
    public void i_receive_valid_Response_for_GET_Practitioner_service() {
        ReusableFunctions.thenFunction(Hooks.HTTP_RESPONSE_SUCCESS);
    }

    @Given("I Set PUT Facility Practitioner api endpoint")
    public void i_Set_PUT_Facility_Practitioner_api_endpoint() {
        endPoint = EndpointURLs.GET_PRACTITIONER_URL;
        endPoint= String.format(endPoint, EnvGlobals.PractitionerId);
        RequestPayLoad = PractitionerPayload.updatePractitioner(EnvGlobals.PractitionerId);
    }

    @Then("I receive valid Response for PUT Practitioner service")
    public void i_receive_valid_Response_for_PUT_Practitioner_service() {
        ReusableFunctions.thenFunction(Hooks.HTTP_RESPONSE_SUCCESS);
    }

}
