package com.cora.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.cora.config.ConfigReader;
import tech.grasshopper.reporter.ExtentPDFReporter;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Central Extent Reports manager for parallel TestNG execution.
 * Produces the Carderosity-style Extend Module Wise QA Report as PDF.
 */
public final class ExtentTestManager {

    private static ExtentReports extentReports;
    private static ExtentSparkReporter sparkReporter;
    private static ExtentPDFReporter pdfReporter;
    private static ReportArtifactPaths artifactPaths;

    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();

    private ExtentTestManager() {
    }

    public static synchronized void initReport() {
        if (extentReports != null) {
            return;
        }

        ConfigReader.init();

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String reportDir = System.getProperty("user.dir") + File.separator + "test-output" + File.separator + "reports";
        new File(reportDir).mkdirs();

        String productName = ConfigReader.get("report.product.name", "Cora PWA");
        String htmlTempPath = reportDir + File.separator + ".extent-temp-" + timestamp + ".html";
        String pdfReportPath = reportDir + File.separator + productName + " — Extend Module Wise QA Report.pdf";
        artifactPaths = new ReportArtifactPaths(reportDir, htmlTempPath, pdfReportPath);

        sparkReporter = new ExtentSparkReporter(htmlTempPath);
        sparkReporter.config().setDocumentTitle(ConfigReader.get("report.title", "Cora Test Execution Report"));
        sparkReporter.config().setReportName(ConfigReader.get("report.name", "Cora Automation Report"));
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setTimelineEnabled(true);
        sparkReporter.config().setOfflineMode(true);
        loadSparkXmlConfig(sparkReporter);

        pdfReporter = PdfReportGenerator.createReporter(pdfReportPath);

        extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter, pdfReporter);
        applyEnvironmentSystemInfo();
    }

    private static void loadSparkXmlConfig(ExtentSparkReporter spark) {
        try (InputStream xml = ExtentTestManager.class.getClassLoader().getResourceAsStream("extent-config.xml")) {
            if (xml == null) {
                return;
            }
            File tempConfig = File.createTempFile("extent-config", ".xml");
            tempConfig.deleteOnExit();
            Files.copy(xml, tempConfig.toPath(), StandardCopyOption.REPLACE_EXISTING);
            spark.loadXMLConfig(tempConfig);
        } catch (Exception ignored) {
            // extent-config.xml is optional
        }
    }

    private static void applyEnvironmentSystemInfo() {
        ExecutionEnvironment environment = ExecutionEnvironment.fromConfig();
        for (Map.Entry<String, String> entry : environment.asSystemInfoMap().entrySet()) {
            extentReports.setSystemInfo(entry.getKey(), entry.getValue());
        }
    }

    public static synchronized void flushReport() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }

    public static synchronized ExtentTest createTest(String testName) {
        if (extentReports == null) {
            initReport();
        }
        ExtentTest test = extentReports.createTest(testName);
        extentTestThreadLocal.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        ExtentTest test = extentTestThreadLocal.get();
        if (test == null) {
            throw new IllegalStateException(
                    "ExtentTest not initialized for thread " + Thread.currentThread().getName()
                            + ". Ensure TestListener is registered.");
        }
        return test;
    }

    public static void removeTest() {
        extentTestThreadLocal.remove();
    }

    public static ReportArtifactPaths getArtifactPaths() {
        return artifactPaths;
    }
}
