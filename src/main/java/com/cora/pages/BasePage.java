package com.cora.pages;

import com.cora.config.ConfigReader;
import com.cora.utils.WebElementUtils;
import org.openqa.selenium.WebDriver;

/**
 * Abstract base for all Page Object classes.
 * Uses By locators (not @FindBy) to reduce StaleElementReferenceException risk.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebElementUtils utils;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.utils = new WebElementUtils(driver);
    }

    /** Retries driver.get() when parallel Chrome instances stress page-load under load. */
    protected void navigateWithRetry(String path) {
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        String url = ConfigReader.get("base.url") + normalizedPath;
        int maxAttempts = ConfigReader.getInt("navigation.retry.count", 2);

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                driver.get(url);
                return;
            } catch (Exception e) {
                if (attempt == maxAttempts) {
                    throw e;
                }
            }
        }
    }
}
