package com.cora.utils;

import com.cora.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Reusable, thread-safe helper methods for interacting with web elements.
 * Each instance is bound to a single WebDriver (one per test thread).
 */
public class WebElementUtils {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor jsExecutor;
    private final Actions actions;

    public WebElementUtils(WebDriver driver) {
        this.driver = driver;
        int explicitWaitSeconds = ConfigReader.getInt("explicit.wait.seconds", 15);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWaitSeconds));
        this.jsExecutor = (JavascriptExecutor) driver;
        this.actions = new Actions(driver);
    }

    public WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForClickability(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public boolean waitForInvisibility(By locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public void waitForUrlContains(String partialUrl) {
        wait.until(ExpectedConditions.urlContains(partialUrl));
    }

    public List<WebElement> waitForAllVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public void click(By locator) {
        VoiceAssistantShield.neutralize(driver);
        waitForClickability(locator).click();
    }

    public void clickWithJs(By locator) {
        VoiceAssistantShield.neutralize(driver);
        WebElement element = waitForVisibility(locator);
        jsExecutor.executeScript("arguments[0].click();", element);
    }

    public void sendKeys(By locator, String text) {
        WebElement element = waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
    }

    public String getText(By locator) {
        return waitForVisibility(locator).getText().trim();
    }

    public String getAttribute(By locator, String attributeName) {
        return waitForVisibility(locator).getAttribute(attributeName);
    }

    public boolean isDisplayed(By locator) {
        try {
            return waitForVisibility(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEnabled(By locator) {
        try {
            return waitForVisibility(locator).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public void selectByVisibleText(By locator, String visibleText) {
        WebElement element = waitForVisibility(locator);
        new Select(element).selectByVisibleText(visibleText);
    }

    public void selectByValue(By locator, String value) {
        WebElement element = waitForVisibility(locator);
        new Select(element).selectByValue(value);
    }

    public void selectByIndex(By locator, int index) {
        WebElement element = waitForVisibility(locator);
        new Select(element).selectByIndex(index);
    }

    public String getSelectedOptionText(By locator) {
        WebElement element = waitForVisibility(locator);
        return new Select(element).getFirstSelectedOption().getText().trim();
    }

    public void clickIfDisplayed(By locator) {
        if (isDisplayed(locator)) {
            VoiceAssistantShield.neutralize(driver);
            click(locator);
        }
    }

    public void scrollToElement(By locator) {
        WebElement element = waitForVisibility(locator);
        jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    public void scrollToBottom() {
        jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    public void scrollToTop() {
        jsExecutor.executeScript("window.scrollTo(0, 0);");
    }

    public void hover(By locator) {
        WebElement element = waitForVisibility(locator);
        actions.moveToElement(element).perform();
    }

    public Object executeScript(String script, Object... args) {
        return jsExecutor.executeScript(script, args);
    }

    public WebElement waitForPresence(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    /** Accepts a native browser confirm/alert if one appears (e.g. Delete Draft). */
    public void acceptAlertIfPresent() {
        try {
            WebDriverWait alertWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            Alert alert = alertWait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
        } catch (Exception ignored) {
            // No native dialog — in-app modal may handle confirmation instead
        }
    }

    public void clickIfDisplayedWithin(By locator, int timeoutSeconds) {
        try {
            VoiceAssistantShield.neutralize(driver);
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            WebElement element = shortWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            element.click();
        } catch (Exception ignored) {
            // Optional confirmation control not shown
        }
    }

    public WebDriver getDriver() {
        return driver;
    }
}
