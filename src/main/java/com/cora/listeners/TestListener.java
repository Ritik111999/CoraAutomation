package com.cora.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.cora.base.BaseTest;
import com.cora.config.ConfigReader;
import com.cora.reporting.ExtentReportManager;
import com.cora.utils.ScreenshotUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener that drives Extent Reports logging and screenshot capture
 * for both PASS and FAIL outcomes.
 */
public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        ExtentReportManager.initReport();
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportManager.flushReport();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        String displayName = description != null && !description.isBlank() ? description : testName;

        ExtentTest extentTest = ExtentReportManager.createTest(displayName);
        extentTest.assignCategory(result.getTestClass().getName());
        extentTest.info("Test started: " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest extentTest = ExtentReportManager.getTest();
        extentTest.log(Status.PASS, MarkupHelper.createLabel("PASSED", ExtentColor.GREEN));

        if (shouldCaptureOnPass()) {
            attachScreenshot(result, extentTest, "PASS");
        }

        ExtentReportManager.removeTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest extentTest = ExtentReportManager.getTest();
        extentTest.log(Status.FAIL, MarkupHelper.createLabel("FAILED", ExtentColor.RED));

        if (result.getThrowable() != null) {
            extentTest.fail(result.getThrowable());
        }

        if (shouldCaptureOnFail()) {
            attachScreenshot(result, extentTest, "FAIL");
        }

        ExtentReportManager.removeTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest extentTest = ExtentReportManager.getTest();
        extentTest.log(Status.SKIP, MarkupHelper.createLabel("SKIPPED", ExtentColor.ORANGE));

        if (result.getThrowable() != null) {
            extentTest.skip(result.getThrowable());
        }

        ExtentReportManager.removeTest();
    }

    private void attachScreenshot(ITestResult result, ExtentTest extentTest, String status) {
        WebDriver driver = BaseTest.getThreadLocalDriver();
        if (driver == null) {
            extentTest.warning("Screenshot not captured (" + status + "): WebDriver was null");
            return;
        }

        try {
            String screenshotPath = ScreenshotUtils.captureScreenshot(driver, result.getMethod().getMethodName());
            if (screenshotPath != null) {
                extentTest.addScreenCaptureFromPath(screenshotPath);
                extentTest.info("Screenshot captured on " + status + ": " + screenshotPath);
            }
        } catch (Exception e) {
            extentTest.warning("Screenshot capture failed: " + e.getMessage());
        }
    }

    private boolean shouldCaptureOnPass() {
        return ConfigReader.getBoolean("screenshot.on.pass", true);
    }

    private boolean shouldCaptureOnFail() {
        return ConfigReader.getBoolean("screenshot.on.fail", true);
    }
}
