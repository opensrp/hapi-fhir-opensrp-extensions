
package config;


import ru.qatools.properties.Property;
import ru.qatools.properties.PropertyLoader;
import ru.qatools.properties.Resource.Classpath;


@Classpath({"application.properties"})

public class ApplicationConfigReader {



    @Property("baseUrl")
    private String baseUrl;
    @Property("htmlReportPath")
    private String htmlReportPath;



    public ApplicationConfigReader() {
        PropertyLoader.newInstance().populate(this);
    }


    public String getBaseUrl() {
        return this.baseUrl;
    }
    public String getHtmlReportPath() {
        return this.htmlReportPath;
    }



}