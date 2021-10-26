package stepdefs;

import config.EndpointURLs;
import config.EnvGlobals;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import general.ReusableFunctions;
import payloads.PractitionerRolePayload;

import static stepdefs.Hooks.RequestPayLoad;
import static stepdefs.Hooks.endPoint;

public class PractitionerRole {

    @Given("I Set POST Practitioner Role service api endpoint")
    public void i_Set_POST_Practitioner_Role_service_api_endpoint() {
        endPoint = EndpointURLs.PRACTITIONER_ROLE_URL;
        RequestPayLoad = PractitionerRolePayload.createPractitionerRole(EnvGlobals.managingOrgId,EnvGlobals.LocationOrgId,EnvGlobals.healthCaeServiceId,EnvGlobals.PractitionerId);
    }

    @Then("I receive valid Response for POST Practitioner Role service")
    public void i_receive_valid_Response_for_POST_Practitioner_Role_service() {
        ReusableFunctions.thenFunction(Hooks.HTTP_RESPONSE_CREATED);
        EnvGlobals.practitionerRoleId = ReusableFunctions.getResponsePath("id");
    }

    @Given("I Set GET Practitioner Role api endpoint")
    public void i_Set_GET_Practitioner_Role_api_endpoint() {
        endPoint = EndpointURLs.GET_PRACTITIONER_ROLE_URL;
        endPoint= String.format(endPoint, EnvGlobals.practitionerRoleId);
    }

    @Then("I receive valid Response for GET Practitioner Role service")
    public void i_receive_valid_Response_for_GET_Practitioner_Role_service() {
        ReusableFunctions.thenFunction(Hooks.HTTP_RESPONSE_SUCCESS);
    }

    @Given("I Set GET Practitioner Role api endpoint for specific location")
    public void i_Set_GET_Practitioner_Role_api_endpoint_for_specific_location() {
        endPoint = EndpointURLs.GET_PRACTITIONER_ROLE_BY_LOCATION_URL;
        endPoint= String.format(endPoint, EnvGlobals.LocationOrgId);
    }

    @Given("I Set GET Practitioner Role api endpoint for specific Organization")
    public void i_Set_GET_Practitioner_Role_api_endpoint_for_specific_Organization() {
        endPoint = EndpointURLs.GET_PRACTITIONER_ROLE_BY_ORGANIZATION_URL;
        endPoint= String.format(endPoint, EnvGlobals.managingOrgId);
    }

    @Given("I Set GET Practitioner Role api endpoint for specific practitioner")
    public void i_Set_GET_Practitioner_Role_api_endpoint_for_specific_practitioner() {
        endPoint = EndpointURLs.GET_PRACTITIONER_ROLE_BY_PRACTITIONER_URL;
        endPoint= String.format(endPoint, EnvGlobals.PractitionerId);
    }

    @Given("I Set PUT Practitioner Role api endpoint")
    public void i_Set_PUT_Facility_Practitioner_Role_api_endpoint() {
        endPoint = EndpointURLs.GET_PRACTITIONER_ROLE_URL;
        endPoint= String.format(endPoint, EnvGlobals.practitionerRoleId);
        RequestPayLoad = PractitionerRolePayload.updatePractitionerRole(EnvGlobals.managingOrgId,EnvGlobals.LocationOrgId,EnvGlobals.healthCaeServiceId,EnvGlobals.PractitionerId,EnvGlobals.practitionerRoleId);
    }

    @Then("I receive valid Response for PUT Practitioner Role service")
    public void i_receive_valid_Response_for_PUT_Practitioner_Role_service() {
        ReusableFunctions.thenFunction(Hooks.HTTP_RESPONSE_SUCCESS);
    }

}
