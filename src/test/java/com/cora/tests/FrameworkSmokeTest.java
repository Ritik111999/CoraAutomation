package com.cora.tests;

import com.cora.base.BaseTest;
import com.cora.config.ConfigReader;
import com.cora.reporting.ExtentReportManager;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Lightweight smoke test to validate framework wiring (ConfigReader, BaseTest, reporting).
 * Remove or replace once real page tests are added.
 */
public class FrameworkSmokeTest extends BaseTest {

    @Test(description = "Framework Smoke - Config and WebDriver initialization")
    public void verifyFrameworkInitialization() {
        ExtentReportManager.getTest().info("Validating config.properties is loaded");

        String baseUrl = ConfigReader.get("base.url");
        Assert.assertNotNull(baseUrl, "base.url should be configured");
        Assert.assertFalse(baseUrl.isBlank(), "base.url should not be blank");

        ExtentReportManager.getTest().info("Verifying WebDriver and utilities are initialized");
        Assert.assertNotNull(getDriver(), "WebDriver should be initialized");
        Assert.assertNotNull(getUtils(), "WebElementUtils should be initialized");
    }
}
