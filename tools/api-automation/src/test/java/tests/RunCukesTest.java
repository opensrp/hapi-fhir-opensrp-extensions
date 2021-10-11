package tests;


import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import utils.Reports;


import java.sql.SQLException;


@RunWith(Cucumber.class)
@CucumberOptions(features = { "src/test/resources/feature" },
        glue = {"stepdefs"},
        plugin = { "pretty", "html:target/cucumber" },
        tags={"@test"})

public  class RunCukesTest
{
    @BeforeClass
    public static void  beforeClass() throws SQLException {
        Reports.startReport();
    }

    @AfterClass
    public static void AfterClass() {
        Reports.getExtentReport().flush();
        Reports.getExtentReport().close();
    }
}
