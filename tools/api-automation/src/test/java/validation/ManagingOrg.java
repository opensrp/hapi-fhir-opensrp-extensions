package validation;

import com.jayway.jsonpath.JsonPath;
import config.EnvGlobals;
import general.ReusableFunctions;
import org.junit.Assert;

public class ManagingOrg {

    public static final String RESPONSE_ID = "id";
    public static final String RESPONSE_NAME = "name";


    public static void validatePostResponse() {
        Assert.assertNotNull(ReusableFunctions.getResponsePath(RESPONSE_ID));
        Assert.assertEquals(JsonPath.read(EnvGlobals.managingOrgId, "id"), ReusableFunctions.getResponsePath(RESPONSE_ID));
        Assert.assertEquals(ReusableFunctions.getResponsePath(RESPONSE_NAME), "Ministry of Health");
    }

}
