package stepdefs;

import config.EndpointURLs;
import config.EnvGlobals;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import general.ReusableFunctions;
import payloads.OrganizationPayload;
import payloads.PatientPayload;

import static stepdefs.Hooks.RequestPayLoad;
import static stepdefs.Hooks.endPoint;

public class Patient {
    @Given("I Set POST Patient service api endpoint")
    public void i_Set_POST_Patient_service_api_endpoint() {
        endPoint = EndpointURLs.PATIENT_ROLE_URL;
        RequestPayLoad = PatientPayload.createPatient(EnvGlobals.managingOrgId);
    }

    @Then("I receive valid Response for POST Patient service")
    public void i_receive_valid_Response_for_POST_Patient_service() {
        ReusableFunctions.thenFunction(Hooks.HTTP_RESPONSE_CREATED);
        EnvGlobals.patientId = ReusableFunctions.getResponsePath("id");
    }

    @Given("I Set GET Patient api endpoint")
    public void i_Set_GET_Patient_api_endpoint() {
        endPoint = EndpointURLs.GET_PATIENT_ROLE_URL;
        endPoint= String.format(endPoint, EnvGlobals.patientId);
    }

    @Then("I receive valid Response for GET Patient service")
    public void i_receive_valid_Response_for_GET_Patient_service() {
        ReusableFunctions.thenFunction(Hooks.HTTP_RESPONSE_SUCCESS);
    }

    @Given("I Set GET Patient api endpoint for specific Organization")
    public void i_Set_GET_Patient_api_endpoint_for_specific_Organization() {
        endPoint = EndpointURLs.GET_PATIENT_BY_ORGANIZATION_URL;
        endPoint= String.format(endPoint, EnvGlobals.managingOrgId);
    }

    @Given("I Set PUT Facility Patient api endpoint")
    public void i_Set_PUT_Facility_Patient_api_endpoint() {
        endPoint = EndpointURLs.GET_PATIENT_ROLE_URL;
        endPoint= String.format(endPoint, EnvGlobals.patientId);
        RequestPayLoad = PatientPayload.upadtePatient(EnvGlobals.managingOrgId,EnvGlobals.patientId);

    }
    @Then("I receive valid Response for PUT Patient service")
    public void i_receive_valid_Response_for_PUT_Patient_service() {
        ReusableFunctions.thenFunction(Hooks.HTTP_RESPONSE_SUCCESS);
    }


}
