package stepdefs;

import config.EndpointURLs;
import config.EnvGlobals;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import general.ReusableFunctions;
import payloads.EncounterPayload;

import static stepdefs.Hooks.RequestPayLoad;
import static stepdefs.Hooks.endPoint;

public class Encounter {
    @Given("I Set POST Encounter service api endpoint")
    public void i_Set_POST_Encounter_service_api_endpoint() {
        endPoint = EndpointURLs.ENCOUNTER_URL;
        RequestPayLoad = EncounterPayload.createEncounter(EnvGlobals.patientId);
    }

    @Then("I receive valid Response for POST Encounter service")
    public void i_receive_valid_Response_for_POST_Encounter_service() {
        ReusableFunctions.thenFunction(Hooks.HTTP_RESPONSE_CREATED);
        EnvGlobals.encounterId = ReusableFunctions.getResponsePath("id");
    }

    @Given("I Set GET Encounter api endpoint")
    public void i_Set_GET_Encounter_api_endpoint() {

        endPoint = EndpointURLs.GET_ENCOUNTER_URL;
        endPoint = String.format(endPoint, EnvGlobals.encounterId);
    }
    @Then("I receive valid Response for GET Encounter service")
    public void i_receive_valid_Response_for_GET_Encounter_service() {
        ReusableFunctions.thenFunction(Hooks.HTTP_RESPONSE_SUCCESS);
    }

    @Given("I Set GET Encounter api endpoint for specific Patient")
    public void i_Set_GET_Encounter_api_endpoint_for_specific_Patient() {
        endPoint = EndpointURLs.GET_ENCOUNTER_BY_PATIENT_URL;
        endPoint= String.format(endPoint, EnvGlobals.patientId);
    }

    @Given("I Set PUT Encounter api endpoint")
    public void i_Set_PUT_Facility_Encounter_api_endpoint() {
        endPoint = EndpointURLs.GET_ENCOUNTER_URL;
        endPoint= String.format(endPoint, EnvGlobals.encounterId);
        RequestPayLoad = EncounterPayload.updateEncounter(EnvGlobals.patientId,EnvGlobals.encounterId);

    }

    @Then("I receive valid Response for PUT Encounter service")
    public void i_receive_valid_Response_for_PUT_Encounter_service() {
        ReusableFunctions.thenFunction(Hooks.HTTP_RESPONSE_SUCCESS);
    }
}
