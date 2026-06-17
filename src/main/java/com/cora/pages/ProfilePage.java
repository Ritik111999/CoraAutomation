package com.cora.pages;

import com.cora.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.io.File;

/**
 * My Profile page (/profile) — profile photo upload and Account → Basic Info entry.
 */
public class ProfilePage extends BasePage {

    private static final By HEADER_TITLE = By.xpath("//header//h1[normalize-space()='Profile']");
    private static final By PROFILE_IMAGE_INPUT = By.id("profileImageInput");
    private static final By EDIT_AVATAR_BUTTON = By.xpath(
            "//input[@id='profileImageInput']/following-sibling::div//button[contains(@class,'rounded-full')]");
    private static final By PROFILE_AVATAR_IMAGE = By.xpath(
            "//input[@id='profileImageInput']/following-sibling::div//img");
    private static final By DISPLAY_NAME = By.xpath(
            "//main//div[contains(@class,'rounded-2xl') and contains(@class,'text-center')]//h1");
    private static final By DISPLAY_EMAIL = By.xpath(
            "//main//div[contains(@class,'rounded-2xl') and contains(@class,'text-center')]//p[contains(@class,'text-slate-400')]");
    private static final By BASIC_INFO_ROW = By.xpath(
            "//p[normalize-space()='Basic Info']/ancestor::div[contains(@class,'cursor-pointer')][1]");
    private static final By ACCOUNT_SECTION_HEADING = By.xpath("//h2[normalize-space()='Account']");
    private static final By SIDEBAR_PROFILE_LINK = By.xpath(
            "//aside//a[@href='/profile' and .//span[normalize-space()='My Profile']]");

    public ProfilePage(WebDriver driver) {
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
        utils.click(SIDEBAR_PROFILE_LINK);
        waitForPageReady();
    }

    public void openDirect() {
        driver.get(ConfigReader.get("base.url") + ConfigReader.get("cora.profile.path"));
        waitForPageReady();
    }

    public void waitForPageReady() {
        utils.waitForVisibility(HEADER_TITLE);
    }

    public boolean isOnProfilePage() {
        return driver.getCurrentUrl().contains(ConfigReader.get("cora.profile.path"))
                && !driver.getCurrentUrl().contains("edit");
    }

    public boolean isProfilePageLoaded() {
        return utils.isDisplayed(HEADER_TITLE) && utils.isDisplayed(ACCOUNT_SECTION_HEADING);
    }

    public String getDisplayName() {
        return utils.getText(DISPLAY_NAME);
    }

    public String getDisplayEmail() {
        return utils.getText(DISPLAY_EMAIL);
    }

    public String getProfileAvatarSrc() {
        return utils.getAttribute(PROFILE_AVATAR_IMAGE, "src");
    }

    public boolean isProfileAvatarDisplayed() {
        return utils.isDisplayed(PROFILE_AVATAR_IMAGE);
    }

    public void clickEditAvatar() {
        utils.click(EDIT_AVATAR_BUTTON);
    }

    public void uploadProfileImage(String absolutePath) {
        utils.waitForPresence(PROFILE_IMAGE_INPUT).sendKeys(absolutePath);
    }

    public void uploadProfileImageFromConfig() {
        uploadProfileImage(resolveProfilePhotoPath());
    }

    public void clickBasicInfo() {
        utils.scrollToElement(BASIC_INFO_ROW);
        utils.click(BASIC_INFO_ROW);
    }

    public String resolveProfilePhotoPath() {
        String configured = ConfigReader.get("cora.profile.photo.path");
        File file = new File(configured);
        if (!file.isAbsolute()) {
            file = new File(System.getProperty("user.dir"), configured);
        }
        if (!file.exists()) {
            throw new IllegalStateException("Profile photo not found: " + file.getAbsolutePath());
        }
        return file.getAbsolutePath();
    }
}
