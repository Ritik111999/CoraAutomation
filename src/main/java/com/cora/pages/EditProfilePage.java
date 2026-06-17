package com.cora.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Edit Profile screen — opened from My Profile → Account → Basic Info.
 * Covers profile photo upload and Personal Information only (not Account Security).
 */
public class EditProfilePage extends BasePage {

    private static final By HEADER_TITLE = By.xpath("//header//h1[normalize-space()='Edit Profile']");
    private static final By IMAGE_INPUT = By.id("imageInput");
    private static final By CHANGE_PHOTO_BUTTON = By.xpath("//button[normalize-space()='Change Photo']");
    private static final By EDIT_AVATAR_CAMERA_BUTTON = By.xpath(
            "//input[@id='imageInput']/following-sibling::div//button[contains(@class,'rounded-full')]");
    private static final By EDIT_AVATAR_IMAGE = By.xpath(
            "//input[@id='imageInput']/following-sibling::div//img[@alt='Profile']");
    private static final By PERSONAL_INFO_HEADING = By.xpath("//h2[normalize-space()='Personal Information']");

    private static final By FIRST_NAME_INPUT = By.xpath(
            "//label[normalize-space()='First Name']/following-sibling::input");
    private static final By LAST_NAME_INPUT = By.xpath(
            "//label[normalize-space()='Last Name']/following-sibling::input");
    private static final By EMAIL_INPUT = By.xpath(
            "//label[normalize-space()='Email Address']/following-sibling::input[@type='email']");
    private static final By PHONE_INPUT = By.xpath(
            "//label[normalize-space()='Phone Number']/following-sibling::input[@type='tel']");
    private static final By COMPANY_INPUT = By.xpath(
            "//label[contains(normalize-space(),'Company Name')]/following-sibling::input");

    private static final By SAVE_CHANGES_BUTTON = By.xpath("//button[normalize-space()='Save Changes']");
    private static final By GO_BACK_BUTTON = By.xpath("//button[normalize-space()='Go Back']");

    public EditProfilePage(WebDriver driver) {
        super(driver);
    }

    public void waitForPageReady() {
        utils.waitForVisibility(HEADER_TITLE);
        utils.waitForVisibility(PERSONAL_INFO_HEADING);
    }

    public boolean isEditProfilePageLoaded() {
        return utils.isDisplayed(HEADER_TITLE) && utils.isDisplayed(PERSONAL_INFO_HEADING);
    }

    public boolean isPersonalInformationSectionDisplayed() {
        return utils.isDisplayed(PERSONAL_INFO_HEADING);
    }

    public boolean isAccountSecuritySectionDisplayed() {
        return utils.isDisplayed(By.xpath("//h2[normalize-space()='Account Security']"));
    }

    public String getFirstName() {
        return utils.getAttribute(FIRST_NAME_INPUT, "value");
    }

    public String getLastName() {
        return utils.getAttribute(LAST_NAME_INPUT, "value");
    }

    public String getEmail() {
        return utils.getAttribute(EMAIL_INPUT, "value");
    }

    public String getPhone() {
        return utils.getAttribute(PHONE_INPUT, "value");
    }

    public String getCompany() {
        return utils.getAttribute(COMPANY_INPUT, "value");
    }

    public boolean isEmailDisabled() {
        return !utils.waitForPresence(EMAIL_INPUT).isEnabled();
    }

    public void enterFirstName(String firstName) {
        utils.sendKeys(FIRST_NAME_INPUT, firstName);
    }

    public void enterLastName(String lastName) {
        utils.sendKeys(LAST_NAME_INPUT, lastName);
    }

    public void enterPhone(String phone) {
        utils.sendKeys(PHONE_INPUT, phone);
    }

    public void enterCompany(String company) {
        utils.sendKeys(COMPANY_INPUT, company);
    }

    public void clearFirstName() {
        clearReactInput(FIRST_NAME_INPUT);
    }

    public void clearLastName() {
        clearReactInput(LAST_NAME_INPUT);
    }

    public void clearPhone() {
        clearReactInput(PHONE_INPUT);
    }

    private void clearReactInput(By locator) {
        WebElement element = utils.waitForVisibility(locator);
        element.click();
        element.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"));
        element.sendKeys(org.openqa.selenium.Keys.BACK_SPACE);
        utils.executeScript(
                "arguments[0].value = ''; arguments[0].dispatchEvent(new Event('input', { bubbles: true }));"
                        + "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                element);
    }

    public void clickChangePhoto() {
        utils.click(CHANGE_PHOTO_BUTTON);
    }

    public void uploadProfileImage(String absolutePath) {
        utils.waitForPresence(IMAGE_INPUT).sendKeys(absolutePath);
    }

    public String getEditAvatarSrc() {
        return utils.getAttribute(EDIT_AVATAR_IMAGE, "src");
    }

    public boolean isEditAvatarDisplayed() {
        return utils.isDisplayed(EDIT_AVATAR_IMAGE);
    }

    public void clickSaveChanges() {
        utils.scrollToBottom();
        utils.clickWithJs(SAVE_CHANGES_BUTTON);
    }

    public void clickGoBack() {
        utils.scrollToElement(GO_BACK_BUTTON);
        utils.click(GO_BACK_BUTTON);
    }

    public void waitForFirstNameValue(String expected) {
        WebDriverWait saveWait = new WebDriverWait(utils.getDriver(), Duration.ofSeconds(20));
        saveWait.until(driver -> expected.equals(
                driver.findElement(FIRST_NAME_INPUT).getAttribute("value")));
    }

    public void waitForSaveToComplete() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public boolean isFirstNameInvalid() {
        return hasInvalidFieldStyle(FIRST_NAME_INPUT);
    }

    public boolean isLastNameInvalid() {
        return hasInvalidFieldStyle(LAST_NAME_INPUT);
    }

    public boolean isPhoneInvalid() {
        return hasInvalidFieldStyle(PHONE_INPUT);
    }

    public boolean isSaveChangesEnabled() {
        return utils.isEnabled(SAVE_CHANGES_BUTTON);
    }

    private boolean hasInvalidFieldStyle(By inputLocator) {
        String className = utils.getAttribute(inputLocator, "class");
        return className != null && (className.contains("border-red") || className.contains("ring-red"));
    }
}
