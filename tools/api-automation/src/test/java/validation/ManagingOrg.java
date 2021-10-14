package validation;

import com.jayway.jsonpath.JsonPath;
import config.EnvGlobals;
import general.ReusableFunctions;
import org.junit.Assert;

public class ManagingOrg {

    public static final String RESPONSE_ID = "id";
    public static final String RESPONSE_NAME = "name";
    public static final String RESPONSE_RESOURCE_TYPE = "resourceType";


    public static void validatePostResponse(String name) {
        Assert.assertNotNull(ReusableFunctions.getResponsePath(RESPONSE_ID));
        Assert.assertEquals(ReusableFunctions.getResponsePath(RESPONSE_NAME), name);
        Assert.assertEquals(ReusableFunctions.getResponsePath(RESPONSE_RESOURCE_TYPE), "Organization");
    }



}
