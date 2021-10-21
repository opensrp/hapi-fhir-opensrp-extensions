/*
Define global variables to use re-use them
 */

package config;
import io.restassured.specification.RequestSpecification;

public class EnvGlobals {


    public static StringBuilder difference = new StringBuilder();

    public static RequestSpecification requestSpecification;
    public static String managingOrgId;
    public static String LocationOrgId;
    public static String PractitionerId;

 }
