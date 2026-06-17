package com.cora.pages;

import com.cora.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for Cora login screen (/login).
 */
public class LoginPage extends BasePage {

    // Locators — prefer type/placeholder over brittle Tailwind classes
    private static final By WELCOME_HEADING = By.xpath("//h2[contains(normalize-space(), 'Welcome to Cora')]");
    private static final By EMAIL_INPUT = By.cssSelector("input[type='email']");
    private static final By PASSWORD_INPUT = By.cssSelector("input[type='password']");
    private static final By REMEMBER_ME_CHECKBOX = By.cssSelector("input[type='checkbox']");
    private static final By SIGN_IN_BUTTON = By.cssSelector("button[type='submit']");
    private static final By RESET_PASSWORD_LINK = By.cssSelector("a[href='/forgot']");
    private static final By SIGN_UP_LINK = By.cssSelector("a[href='/signup']");
    private static final By ERROR_MESSAGE = By.xpath(
            "//form//*[contains(normalize-space(), 'incorrect') or contains(normalize-space(), 'required')]");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        String loginUrl = ConfigReader.get("base.url") + ConfigReader.get("cora.login.path");
        int maxAttempts = ConfigReader.getInt("navigation.retry.count", 2);

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                driver.get(loginUrl);
                waitForLoginPageReady();
                return;
            } catch (Exception e) {
                if (attempt == maxAttempts) {
                    throw e;
                }
            }
        }
    }

    /** Waits for React SPA to render the login form (important under parallel load). */
    public void waitForLoginPageReady() {
        utils.waitForVisibility(EMAIL_INPUT);
        utils.waitForVisibility(SIGN_IN_BUTTON);
    }

    public void enterEmail(String email) {
        utils.sendKeys(EMAIL_INPUT, email);
    }

    public void enterPassword(String password) {
        utils.sendKeys(PASSWORD_INPUT, password);
    }

    public void clickRememberMe() {
        utils.click(REMEMBER_ME_CHECKBOX);
    }

    public void clickSignIn() {
        utils.click(SIGN_IN_BUTTON);
    }

    public void clickResetPassword() {
        utils.click(RESET_PASSWORD_LINK);
    }

    public void clickSignUp() {
        utils.click(SIGN_UP_LINK);
    }

    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickSignIn();
    }

    public boolean isWelcomeHeadingDisplayed() {
        return utils.isDisplayed(WELCOME_HEADING);
    }

    public String getWelcomeHeadingText() {
        return utils.getText(WELCOME_HEADING);
    }

    public boolean isSignInButtonEnabled() {
        return utils.waitForVisibility(SIGN_IN_BUTTON).isEnabled();
    }

    public boolean isErrorMessageDisplayed() {
        return utils.isDisplayed(ERROR_MESSAGE);
    }

    public String getErrorMessage() {
        return utils.getText(ERROR_MESSAGE);
    }

    public boolean isOnLoginPage() {
        return driver.getCurrentUrl().contains(ConfigReader.get("cora.login.path"));
    }
}
