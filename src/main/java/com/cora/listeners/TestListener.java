package com.cora.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.cora.base.BaseTest;
import com.cora.config.ConfigReader;
import com.cora.reporting.ExecutionEnvironment;
import com.cora.reporting.ExecutionMetrics;
import com.cora.reporting.ExecutionMetricsCollector;
import com.cora.reporting.ExtentTestManager;
import com.cora.reporting.ModuleWiseReportGenerator;
import com.cora.reporting.ReportArtifactPaths;
import com.cora.reporting.ReportCleanup;
import com.cora.reporting.ScenarioCategoryMapper;
import com.cora.reporting.ScenarioReportGenerator;
import com.cora.reporting.TestScenarioCollector;
import com.cora.reporting.TestScenarioDefinition;
import com.cora.reporting.TestScenarioResolver;
import com.cora.utils.ScreenshotUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.time.Instant;
import java.util.List;

/**
 * Suite + test listener that drives PDF-only QA reports (Carderosity format).
 */
public class TestListener implements ITestListener, ISuiteListener {

    private Instant suiteStartedAt;
    private static volatile boolean suiteFinalized;

    @Override
    public void onStart(ISuite suite) {
        suiteStartedAt = Instant.now();
        suiteFinalized = false;
        TestScenarioCollector.reset();
        ExtentTestManager.initReport();
    }

    @Override
    public void onFinish(ISuite suite) {
        if (suiteFinalized) {
            return;
        }
        suiteFinalized = true;
        Instant finishedAt = Instant.now();
        ExecutionMetrics metrics = ExecutionMetricsCollector.collect(suite, suiteStartedAt, finishedAt);

        ExtentTest extentTest = ExtentTestManager.createTest("Execution Summary");
        extentTest.assignCategory("Dashboard");
        extentTest.info("Total Tests: " + metrics.getTotal());
        extentTest.info("Passed: " + metrics.getPassed());
        extentTest.info("Failed: " + metrics.getFailed());
        extentTest.info("Skipped: " + metrics.getSkipped());
        extentTest.info("Pass Rate: " + String.format("%.1f%%", metrics.getPassRate()));
        extentTest.info("Execution Time: " + metrics.getFormattedDuration());
        extentTest.pass(MarkupHelper.createLabel("SUITE COMPLETED", ExtentColor.BLUE));
        ExtentTestManager.removeTest();

        ReportArtifactPaths paths = ExtentTestManager.getArtifactPaths();
        if (paths != null) {
            var results = TestScenarioCollector.getResults();
            if (!results.isEmpty()) {
                ModuleWiseReportGenerator.generate(results, paths, finishedAt);
                ScenarioReportGenerator.generate(results, paths, finishedAt);
            }
        }

        ExtentTestManager.flushReport();
        if (paths != null) {
            ReportCleanup.cleanupAfterRun(paths);
        }
    }

    @Override
    public void onStart(ITestContext context) {
        // Suite-level init handles report bootstrap.
    }

    @Override
    public void onFinish(ITestContext context) {
        // Suite-level finish handles flush + email.
    }

    @Override
    public void onTestStart(ITestResult result) {
        TestScenarioDefinition scenario = TestScenarioResolver.resolve(result);
        TestScenarioCollector.markStart(result);

        ExtentTest extentTest = ExtentTestManager.createTest(scenario.extentDisplayName());

        assignScenarioCategories(extentTest, result, scenario);
        assignBrowserMetadata(extentTest);
        logScenarioMetadata(extentTest, scenario);

        String threadLabel = BaseTest.getThreadLabel();
        if (threadLabel != null) {
            extentTest.info("Thread: " + threadLabel);
        }
        extentTest.info("Execution started");
    }

    private void assignScenarioCategories(ExtentTest extentTest, ITestResult result,
                                          TestScenarioDefinition scenario) {
        List<String> categories = ScenarioCategoryMapper.resolveCategories(result, scenario);
        for (String category : categories) {
            extentTest.assignCategory(category);
        }
    }

    private void logScenarioMetadata(ExtentTest extentTest, TestScenarioDefinition scenario) {
        extentTest.info("Scenario ID: " + scenario.getScenarioId());
        extentTest.info("Title: " + scenario.getTitle());
        extentTest.info("Type: " + scenario.getType().toUpperCase());
        extentTest.info("Preconditions: " + scenario.getPreconditions());
        StringBuilder steps = new StringBuilder("Steps:\n");
        int index = 1;
        for (String step : scenario.getSteps()) {
            steps.append(index++).append(". ").append(step).append('\n');
        }
        extentTest.info(steps.toString().trim());
        extentTest.info("Expected Result: " + scenario.getExpectedResult());
    }

    private void assignBrowserMetadata(ExtentTest extentTest) {
        ExecutionEnvironment env = ExecutionEnvironment.fromConfig();
        extentTest.assignDevice(env.getTargetDeviceName() + " | " + env.getOsVersion());
        extentTest.info("Automation Engine: " + env.getAutomationEngine());
        extentTest.info("Platform: " + env.getPlatformName());
        extentTest.info("Browser: " + env.getTargetDeviceName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        TestScenarioCollector.record(result, "PASS");
        logResult(result, Status.PASS, ExtentColor.GREEN, "PASS", true);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        TestScenarioCollector.record(result, "FAIL");
        logResult(result, Status.FAIL, ExtentColor.RED, "FAIL", true);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        TestScenarioCollector.record(result, "SKIP");
        logResult(result, Status.SKIP, ExtentColor.ORANGE, "SKIP", false);
    }

    private void logResult(ITestResult result, Status status, ExtentColor color,
                           String label, boolean captureScreenshot) {
        ExtentTest extentTest = ExtentTestManager.getTest();
        TestScenarioDefinition scenario = TestScenarioResolver.resolve(result);
        String outcome = label + " — " + scenario.getExpectedResult();
        extentTest.log(status, MarkupHelper.createLabel(outcome, color));

        if (result.getThrowable() != null) {
            if (status == Status.SKIP) {
                extentTest.skip(result.getThrowable());
            } else {
                extentTest.fail(result.getThrowable());
            }
        }

        if (captureScreenshot) {
            boolean shouldCapture = status == Status.PASS
                    ? ConfigReader.getBoolean("screenshot.on.pass", true)
                    : ConfigReader.getBoolean("screenshot.on.fail", true);
            if (shouldCapture) {
                attachScreenshot(result, extentTest, label);
            }
        }

        ExtentTestManager.removeTest();
    }

    private void attachScreenshot(ITestResult result, ExtentTest extentTest, String status) {
        WebDriver driver = BaseTest.getThreadLocalDriver();
        if (driver == null) {
            extentTest.warning("Screenshot not captured (" + status + "): WebDriver was null");
            return;
        }

        try {
            String screenshotPath = ScreenshotUtils.captureScreenshot(
                    driver,
                    result.getMethod().getMethodName() + "_T" + Thread.currentThread().getId()
            );
            if (screenshotPath != null) {
                extentTest.addScreenCaptureFromPath(screenshotPath);
                extentTest.info("Screenshot (" + status + "): " + screenshotPath);
            }
        } catch (Exception e) {
            extentTest.warning("Screenshot capture failed: " + e.getMessage());
        }
    }
}
