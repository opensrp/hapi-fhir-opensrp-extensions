/*
# set & get environment/globals variables
*/

package config;


public class ConfigProperties {
    public static config.ApplicationConfigReader appConfig = new config.ApplicationConfigReader();
    public static String htmlReportPath = appConfig.getHtmlReportPath();
    public static String baseUrl = appConfig.getBaseUrl();


}
