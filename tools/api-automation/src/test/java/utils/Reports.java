package utils;

import com.relevantcodes.extentreports.ExtentReports;
import config.ConfigProperties;

public class Reports {

    private static ExtentReports extent;

    public static void startReport() {
        extent = new ExtentReports(System.getProperty("user.dir") + ConfigProperties.htmlReportPath, true);
    }

    public static ExtentReports getExtentReport() {
        if (extent != null) {
            return extent;
        } else {
            throw new IllegalStateException("Extent Report object not initialized");
        }
    }
}
