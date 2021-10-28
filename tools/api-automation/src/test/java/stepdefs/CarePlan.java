package stepdefs;

import config.EndpointURLs;
import config.EnvGlobals;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import general.ReusableFunctions;
import payloads.CarePlanPayload;


import static stepdefs.Hooks.RequestPayLoad;
import static stepdefs.Hooks.endPoint;

public class CarePlan {
    @Given("I Set POST Care Plan service api endpoint")
    public void i_Set_POST_Care_Plan_service_api_endpoint() {
        endPoint = EndpointURLs.CARE_PLAN_URL;
        RequestPayLoad = CarePlanPayload.createCarePlan(EnvGlobals.patientId,EnvGlobals.careTeamId,EnvGlobals.conditionId,EnvGlobals.goalId,EnvGlobals.encounterId,EnvGlobals.PractitionerId);
    }

    @Then("I receive valid Response for POST Care Plan service")
    public void i_receive_valid_Response_for_POST_Care_Plan_service() {
        ReusableFunctions.thenFunction(Hooks.HTTP_RESPONSE_CREATED);
        EnvGlobals.carePlan = ReusableFunctions.getResponsePath("id");
    }

    @Given("I Set GET Care Plan api endpoint")
    public void i_Set_GET_Care_Plan_api_endpoint() {
        endPoint = EndpointURLs.GET_CARE_PLAN_URL;
        endPoint = String.format(endPoint, EnvGlobals.carePlan);
    }

    @Then("I receive valid Response for GET Care Plan service")
    public void i_receive_valid_Response_for_GET_Care_Plan_service() {
        ReusableFunctions.thenFunction(Hooks.HTTP_RESPONSE_SUCCESS);
    }

    @Given("I Set GET Care Plan api endpoint for specific Patient")
    public void i_Set_GET_Care_Plan_api_endpoint_for_specific_Patient() {
        endPoint = EndpointURLs.GET_CARE_PLAN_BY_PATIENT_URL;
        endPoint= String.format(endPoint, EnvGlobals.patientId);
    }

    @Given("I Set GET Care Plan api endpoint for specific care Team")
    public void i_Set_GET_Care_Plan_api_endpoint_for_specific_care_Team() {
        endPoint = EndpointURLs.GET_CARE_PLAN_BY_CARE_TEAM_URL;
        endPoint= String.format(endPoint, EnvGlobals.careTeamId);
    }

    @Given("I Set GET Care Plan api endpoint for specific condition")
    public void i_Set_GET_Care_Plan_api_endpoint_for_specific_condition() {
        endPoint = EndpointURLs.GET_CARE_PLAN_BY_CONDITION_URL;
        endPoint= String.format(endPoint, EnvGlobals.conditionId);
    }

    @Given("I Set GET Care Plan api endpoint for specific encounter")
    public void i_Set_GET_Care_Plan_api_endpoint_for_specific_encounter() {
        endPoint = EndpointURLs.GET_CARE_PLAN_BY_ENCOUNTER_URL;
        endPoint= String.format(endPoint, EnvGlobals.encounterId);
    }

    @Given("I Set PUT Care Plan api endpoint")
    public void i_Set_PUT_Care_Plan_api_endpoint() {
        endPoint = EndpointURLs.GET_CARE_PLAN_URL;
        endPoint= String.format(endPoint, EnvGlobals.carePlan);
        RequestPayLoad = CarePlanPayload.updateCarePlan(EnvGlobals.patientId,EnvGlobals.careTeamId,EnvGlobals.conditionId,EnvGlobals.goalId,EnvGlobals.encounterId,EnvGlobals.PractitionerId,EnvGlobals.carePlan);

    }

    @Then("I receive valid Response for PUT Care Plan service")
    public void i_receive_valid_Response_for_PUT_Care_Plan_service() {
        ReusableFunctions.thenFunction(Hooks.HTTP_RESPONSE_SUCCESS);
    }


}
