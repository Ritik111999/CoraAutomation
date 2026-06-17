package com.cora.pages;

import com.cora.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Live Feed page (/live-feed) — opens from Home → Today's Live Feed → View All.
 * Locators built from live page HTML.
 */
public class LiveFeedPage extends BasePage {

    private static final By HEADER_TITLE = By.xpath("//header//h1[normalize-space()='Live Feed']");
    private static final By PAGE_HEADING = By.xpath("//main//h1[normalize-space()='Live Feed']");
    private static final By ACTIVITY_SUMMARY_LABEL = By.xpath(
            "//main//h1[normalize-space()='Live Feed']/following-sibling::p[contains(@class,'text-slate-400')]");
    private static final By BACK_BUTTON = By.xpath(
            "//main//button[contains(@class,'rounded-xl') and normalize-space()='Back']");

    private static final By DATE_RANGE_DROPDOWN = By.xpath(
            "//main//label[contains(normalize-space(.), 'Date range')]/select");
    private static final By ACTIVITY_TYPE_DROPDOWN = By.xpath(
            "//main//label[contains(normalize-space(.), 'Activity type')]/select");
    private static final By EXACT_DATE_INPUT = By.xpath(
            "//main//label[contains(normalize-space(.), 'Exact date')]/input[@type='date']");

    private static final By TODAY_SECTION_HEADING = By.xpath("//main//h2[normalize-space()='Today']");
    private static final By APPOINTMENT_SCHEDULED_ACTIVITY = By.xpath(
            "//main//h3[contains(normalize-space(),'Appointment scheduled')]");

    public LiveFeedPage(WebDriver driver) {
        super(driver);
    }

    public void waitForPageReady() {
        utils.waitForVisibility(PAGE_HEADING);
    }

    public boolean isLiveFeedPageLoaded() {
        return utils.isDisplayed(PAGE_HEADING);
    }

    public boolean isHeaderTitleDisplayed() {
        return utils.isDisplayed(HEADER_TITLE);
    }

    public boolean isOnLiveFeedPage() {
        return driver.getCurrentUrl().contains(ConfigReader.get("cora.livefeed.path"));
    }

    public String getActivitySummaryLabel() {
        return utils.getText(ACTIVITY_SUMMARY_LABEL);
    }

    public void selectDateRangeByVisibleText(String visibleText) {
        utils.selectByVisibleText(DATE_RANGE_DROPDOWN, visibleText);
    }

    public void selectDateRangeByValue(String value) {
        utils.selectByValue(DATE_RANGE_DROPDOWN, value);
    }

    public void selectActivityTypeByVisibleText(String visibleText) {
        utils.selectByVisibleText(ACTIVITY_TYPE_DROPDOWN, visibleText);
    }

    public void selectActivityTypeByValue(String value) {
        utils.selectByValue(ACTIVITY_TYPE_DROPDOWN, value);
    }

    public void clickExactDateInput() {
        utils.click(EXACT_DATE_INPUT);
    }

    public void setExactDate(String isoDate) {
        utils.sendKeys(EXACT_DATE_INPUT, isoDate);
    }

    public void clearExactDate() {
        utils.sendKeys(EXACT_DATE_INPUT, "");
    }

    public boolean isTodaySectionDisplayed() {
        return utils.isDisplayed(TODAY_SECTION_HEADING);
    }

    public boolean isAppointmentScheduledActivityDisplayed() {
        return utils.isDisplayed(APPOINTMENT_SCHEDULED_ACTIVITY);
    }

    public void clickBack() {
        utils.click(BACK_BUTTON);
    }
}
