package stepdefs;

import com.relevantcodes.extentreports.ExtentTest;


import com.relevantcodes.extentreports.LogStatus;
import config.ConfigProperties;
import cucumber.api.Result;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.runtime.ScenarioImpl;
import general.GeneralFunctions;
import org.apache.commons.lang3.reflect.FieldUtils;
import tests.RunCukesTest;
import utils.Reports;

import java.lang.reflect.Field;
import java.util.ArrayList;


public class Hooks extends RunCukesTest {
    public static final String HTTP_METHOD_POST= "post";
    public static final String HTTP_METHOD_GET= "get";
    public static final String HTTP_METHOD_DELETE= "delete";
    public static final String HTTP_METHOD_PUT= "put";
    public static final int HTTP_RESPONSE_SUCCESS= 200;
    public static final int HTTP_RESPONSE_CREATED= 201;
    public static final int HTTP_RESPONSE_NO_CONTENT= 204;
    public static final int HTTP_RESPONSE_NOT_FOUND = 404;

    static ExtentTest logger;
    @Before
    public static void testStart(Scenario scenario) throws Throwable {
        logger = Reports.getExtentReport().startTest(scenario.getName(),"");
        logger.setStartedTime(GeneralFunctions.getTime());

        }

    @After
    public static void testEnd(Scenario scenario) throws Throwable
    {
        if ( scenario.getStatus() == Result.Type.FAILED ) {
            Field field = FieldUtils.getField(((ScenarioImpl) scenario).getClass(), "stepResults", true);
            field.setAccessible(true);
            ArrayList<Result> results = (ArrayList<Result>) field.get(scenario);
            for (Result result : results) {
                if (result.getError() != null)
                    logger.log(LogStatus.FAIL, "Test Case Failed reason is: " + result.getError());
            }

        }
        else if (scenario.getStatus() == Result.Type.SKIPPED ) {
            logger.log(LogStatus.SKIP, "Test Case Skipped is: ");

        }
        else {
            logger.log(LogStatus.PASS, scenario.getName() + " is Passed");
        }

        Reports.getExtentReport().endTest(logger);

         }


}