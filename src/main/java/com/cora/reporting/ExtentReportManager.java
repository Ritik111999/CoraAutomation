package com.cora.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.cora.config.ConfigReader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Thread-safe Extent Reports manager for parallel TestNG execution.
 */
public final class ExtentReportManager {

    private static ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();

    private ExtentReportManager() {
        // utility class
    }

    public static synchronized void initReport() {
        if (extentReports != null) {
            return;
        }

        ConfigReader.init();

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String reportDir = System.getProperty("user.dir") + File.separator + "test-output" + File.separator + "reports";
        new File(reportDir).mkdirs();

        String reportPath = reportDir + File.separator + "ExtentReport_" + timestamp + ".html";

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setDocumentTitle(ConfigReader.get("report.title", "Cora Test Report"));
        sparkReporter.config().setReportName(ConfigReader.get("report.name", "Cora Automation Report"));
        sparkReporter.config().setTheme(Theme.STANDARD);

        extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter);
        extentReports.setSystemInfo("OS", System.getProperty("os.name"));
        extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
        extentReports.setSystemInfo("Browser", ConfigReader.get("browser", "chrome"));
        extentReports.setSystemInfo("Base URL", ConfigReader.get("base.url"));
    }

    public static synchronized void flushReport() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }

    public static ExtentTest createTest(String testName) {
        ExtentTest test = extentReports.createTest(testName);
        extentTestThreadLocal.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return extentTestThreadLocal.get();
    }

    public static void removeTest() {
        extentTestThreadLocal.remove();
    }
}
