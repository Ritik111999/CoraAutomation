package com.cora.pages;

import com.cora.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Properties listing page (/properties).
 * Locators from live page HTML. Microphone / voice assistant excluded.
 */
public class PropertiesPage extends BasePage {

    private static final By HEADER_TITLE = By.xpath("//header//h1[normalize-space()='Properties']");
    private static final By PAGE_HEADING = By.xpath("//main//h1[contains(normalize-space(),'Properties')]");
    private static final By ADD_PROPERTY_BUTTON = By.xpath("//main//button[normalize-space()='Add Property']");
    private static final By DRAFT_PROPERTIES_BUTTON = By.xpath("//button[normalize-space()='Draft Properties']");
    private static final By PROPERTY_GRID = By.xpath("//main//div[contains(@class,'grid') and contains(@class,'md:grid-cols-2')]");
    private static final By PROPERTY_CARD = By.xpath(
            "//main//div[contains(@class,'rounded-3xl') and contains(@class,'cursor-pointer')]");
    private static final By SIDEBAR_PROPERTIES_LINK = By.xpath(
            "//aside//a[@href='/properties' and .//span[normalize-space()='Properties']]");
    private static final By PROPERTY_OPTIONS_BUTTON = By.xpath("//button[@aria-label='Property options']");
    private static final By MENU_EDIT_BUTTON = By.xpath(
            "//div[contains(@class,'absolute') and contains(@class,'bottom-full')]//button[normalize-space()='Edit']");
    private static final By MENU_DELETE_BUTTON = By.xpath(
            "//div[contains(@class,'absolute') and contains(@class,'bottom-full')]//button[normalize-space()='Delete']");

    public PropertiesPage(WebDriver driver) {
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
        utils.click(SIDEBAR_PROPERTIES_LINK);
        waitForPageReady();
    }

    public void openDirect() {
        driver.get(ConfigReader.get("base.url") + ConfigReader.get("cora.properties.path"));
        waitForPageReady();
    }

    public void waitForPageReady() {
        utils.waitForVisibility(PAGE_HEADING);
    }

    public boolean isOnPropertiesPage() {
        return driver.getCurrentUrl().contains(ConfigReader.get("cora.properties.path"));
    }

    public boolean isHeaderTitleDisplayed() {
        return utils.isDisplayed(HEADER_TITLE);
    }

    public boolean isPropertiesPageLoaded() {
        return utils.isDisplayed(PAGE_HEADING);
    }

    public String getPageHeadingText() {
        return utils.getText(PAGE_HEADING);
    }

    public boolean isPropertyGridDisplayed() {
        return utils.isDisplayed(PROPERTY_GRID);
    }

    public int getVisiblePropertyCardCount() {
        return utils.waitForAllVisible(PROPERTY_CARD).size();
    }

    public void clickAddProperty() {
        utils.click(ADD_PROPERTY_BUTTON);
        utils.waitForUrlContains(ConfigReader.get("cora.properties.add.path"));
    }

    public void clickDraftProperties() {
        utils.scrollToTop();
        utils.clickWithJs(DRAFT_PROPERTIES_BUTTON);
        utils.waitForUrlContains("draft");
    }

    public void clickPropertySearch() {
        propertySearch().open();
    }

    private PropertySearchOverlay propertySearch() {
        return new PropertySearchOverlay(driver);
    }

    public boolean isPropertyCardDisplayedByAddress(String addressFragment) {
        return utils.isDisplayed(By.xpath(
                "//main//h4[contains(normalize-space(),'" + addressFragment + "')]"));
    }

    public boolean isStatusBadgeDisplayed(String status) {
        return utils.isDisplayed(By.xpath(
                "//main//span[normalize-space()='" + status + "']"));
    }

    public boolean isStatusBadgeDisplayedForAddress(String addressFragment, String status) {
        return utils.isDisplayed(By.xpath(
                "//main//h4[contains(normalize-space(),'" + addressFragment + "')]"
                        + "/ancestor::div[contains(@class,'rounded-3xl')][1]"
                        + "//span[normalize-space()='" + status + "']"));
    }

    public void openPropertyOptionsMenuForAddress(String addressFragment) {
        utils.click(propertyOptionsButtonByAddress(addressFragment));
    }

    public void clickEditFromPropertyMenu() {
        utils.clickWithJs(MENU_EDIT_BUTTON);
        utils.waitForUrlContains(ConfigReader.get("cora.properties.add.path"));
    }

    public void clickDeleteFromPropertyMenu() {
        utils.clickWithJs(MENU_DELETE_BUTTON);
        utils.acceptAlertIfPresent();
        utils.clickIfDisplayedWithin(By.xpath(
                "//div[contains(@class,'fixed') and contains(@class,'inset-0')]"
                        + "//button[normalize-space()='Delete' or normalize-space()='Confirm']"), 3);
        utils.waitForSeconds(2);
    }

    public void waitForPropertyRemoved(String addressFragment) {
        utils.waitForInvisibility(propertyCardHeadingByAddress(addressFragment));
    }

    private By propertyOptionsButtonByAddress(String addressFragment) {
        return By.xpath("//main//h4[contains(normalize-space(),'"
                + escapeXpathLiteral(addressFragment)
                + "')]/ancestor::div[contains(@class,'rounded-3xl')][1]//button[@aria-label='Property options']");
    }

    private By propertyCardHeadingByAddress(String addressFragment) {
        return By.xpath("//main//h4[contains(normalize-space(),'" + escapeXpathLiteral(addressFragment) + "')]");
    }

    private static String escapeXpathLiteral(String value) {
        if (!value.contains("'")) {
            return value;
        }
        return "concat('" + value.replace("'", "', \"'\", '") + "')";
    }
}
