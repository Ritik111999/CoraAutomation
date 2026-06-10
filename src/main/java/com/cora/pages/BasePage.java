package com.cora.pages;

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
}
