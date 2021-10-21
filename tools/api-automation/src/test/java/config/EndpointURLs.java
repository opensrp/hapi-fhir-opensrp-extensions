
package config;


public class EndpointURLs {


    public static  String MANAGING_ORGANIZATION_URL = "/Organization";
    public static  String GET_MANAGING_ORGANIZATION_URL = "/Organization/%s";
    public static  String GET_ORGANIZATION_BY_LOCATION = "/Organization?address-state=MI";

    public static  String LOCATION_ORGANIZATION_URL = "/Location";
    public static  String GET_LOCATION_ORGANIZATION_URL = "/Location/%s";
    public static  String GET_LOCATION_BY_STATE = "/Location?address-state=MI";

    public static  String PRACTITIONER_URL = "/Practitioner";
    public static  String GET_PRACTITIONER_URL = "/Practitioner/%s";

    public static  String HEALTHCARESERVICES_URL = "/HealthcareService";
    public static  String GET_HEALTHCARESERVICES_URL = "/HealthcareService/%s";
    public static  String GET_HEALTHCARESERVICES_BY_ORGANIZATION_URL = "/HealthcareService?organization=%s";
    public static  String GET_HEALTHCARESERVICES_BY_LOCATION_URL = "/HealthcareService?location=%s";


    public static String PRACTITIONER_ROLE_URL = "/PractitionerRole";
    public static String GET_PRACTITIONER_ROLE_URL = "/PractitionerRole/%s";
    public static String GET_PRACTITIONER_ROLE_BY_ORGANIZATION_URL = "/PractitionerRole?organization=%s";
    public static String GET_PRACTITIONER_ROLE_BY_LOCATION_URL = "/PractitionerRole?location=%s";
    public static String GET_PRACTITIONER_ROLE_BY_HEALTHCARESERVICE_URL = "/PractitionerRole?healthcareService=%s";
    public static String GET_PRACTITIONER_ROLE_BY_PRACTITIONER_URL = "/PractitionerRole?organization=%s";

}
