package com.cora.pages;

import com.cora.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Contact Us page (/contact) — message form with name, email, subject, and message.
 */
public class ContactUsPage extends BasePage {

    private static final By HEADER_TITLE = By.xpath("//header//h1[normalize-space()='Contact Us']");
    private static final By HERO_HEADING = By.xpath("//main//h1[normalize-space()=\"We're Here to Help\"]");
    private static final By RESPONSE_TIME_LABEL = By.xpath(
            "//main//p[contains(normalize-space(),'Typical response')]");
    private static final By FORM_HEADING = By.xpath("//main//h2[normalize-space()='Send Us a Message']");

    private static final By NAME_INPUT = By.id("name");
    private static final By EMAIL_INPUT = By.id("email");
    private static final By SUBJECT_SELECT = By.id("subject");
    private static final By MESSAGE_TEXTAREA = By.id("message");
    private static final By CHARACTER_COUNTER = By.xpath(
            "//textarea[@id='message']/following-sibling::p[contains(normalize-space(),'/ 1000')]");
    private static final By SUBMIT_BUTTON = By.xpath("//form//button[@type='submit' and normalize-space()='Submit']");

    private static final By SIDEBAR_CONTACT_LINK = By.xpath(
            "//aside//a[@href='/contact' and .//span[normalize-space()='Contact Us']]");

    private static final String TOAST_MESSAGE_XPATH = "//*[contains(normalize-space(),'%s')]";

    public ContactUsPage(WebDriver driver) {
        super(driver);
    }

    public void openAuthenticated() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.login(
                ConfigReader.get("cora.login.valid.username"),
                ConfigReader.get("cora.login.valid.password"));
        utils.waitForUrlContains(ConfigReader.get("cora.home.path"));
    }

    public void openFromSidebar() {
        utils.click(SIDEBAR_CONTACT_LINK);
        waitForPageReady();
    }

    public void openDirect() {
        navigateWithRetry(ConfigReader.get("cora.contact.path"));
        waitForPageReady();
    }

    public void waitForPageReady() {
        utils.waitForVisibility(FORM_HEADING);
    }

    public boolean isOnContactPage() {
        return driver.getCurrentUrl().contains(ConfigReader.get("cora.contact.path"));
    }

    public boolean isContactPageLoaded() {
        return utils.isDisplayed(HERO_HEADING)
                && utils.isDisplayed(FORM_HEADING)
                && utils.isDisplayed(RESPONSE_TIME_LABEL);
    }

    public boolean isHeaderTitleDisplayed() {
        return utils.isDisplayed(HEADER_TITLE);
    }

    public void enterName(String name) {
        utils.sendKeys(NAME_INPUT, name);
    }

    public void enterEmail(String email) {
        utils.sendKeys(EMAIL_INPUT, email);
    }

    public void selectSubjectByVisibleText(String subject) {
        utils.selectByVisibleText(SUBJECT_SELECT, subject);
    }

    public void enterMessage(String message) {
        utils.sendKeys(MESSAGE_TEXTAREA, message);
    }

    public void fillContactForm(String name, String email, String subject, String message) {
        enterName(name);
        enterEmail(email);
        selectSubjectByVisibleText(subject);
        enterMessage(message);
    }

    public boolean isSubmitReady() {
        String className = utils.getAttribute(SUBMIT_BUTTON, "class");
        return className == null || !className.contains("bg-[#A3A3A3]");
    }

    public boolean isFormValid() {
        Object valid = utils.executeScript("return document.querySelector('main form').checkValidity();");
        return Boolean.TRUE.equals(valid);
    }

    public boolean isSubmitEnabled() {
        return utils.waitForPresence(SUBMIT_BUTTON).isEnabled();
    }

    public void clickSubmit() {
        utils.scrollToElement(SUBMIT_BUTTON);
        utils.clickWithJs(SUBMIT_BUTTON);
    }

    public void submitContactForm(String name, String email, String subject, String message) {
        fillContactForm(name, email, subject, message);
        clickSubmit();
    }

    public String getSelectedSubject() {
        return utils.getSelectedOptionText(SUBJECT_SELECT);
    }

    public int getMessageCharacterCount() {
        String counterText = utils.getText(CHARACTER_COUNTER);
        String countPart = counterText.split("/")[0].trim();
        return Integer.parseInt(countPart);
    }

    public boolean isEmailFieldValid() {
        WebElement emailField = utils.waitForPresence(EMAIL_INPUT);
        Object valid = utils.executeScript("return arguments[0].checkValidity();", emailField);
        return Boolean.TRUE.equals(valid);
    }

    public boolean isNameEmpty() {
        return utils.getAttribute(NAME_INPUT, "value").isBlank();
    }

    public boolean isMessageEmpty() {
        return utils.getAttribute(MESSAGE_TEXTAREA, "value").isBlank();
    }

    public boolean isToastDisplayed(String message) {
        return utils.isDisplayed(By.xpath(String.format(TOAST_MESSAGE_XPATH, message)));
    }

    public void waitForToast(String message) {
        utils.waitForVisibility(By.xpath(String.format(TOAST_MESSAGE_XPATH, message)));
    }

    public boolean isSubmitSuccessDisplayed() {
        return isToastDisplayed(ConfigReader.get("cora.contact.toast.sent"));
    }

    public boolean isFormCleared() {
        return isNameEmpty()
                && isMessageEmpty()
                && utils.getAttribute(EMAIL_INPUT, "value").isBlank();
    }

    public void waitForSubmitSuccess() {
        try {
            waitForToast(ConfigReader.get("cora.contact.toast.sent"));
        } catch (Exception ignored) {
            utils.waitForSeconds(2);
        }
    }
}
