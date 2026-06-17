package com.cora.pages;

import com.cora.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Follow-ups page (/follow-ups) — opens from Home → My Cora Queue → View All.
 * Locators built from live page HTML.
 */
public class FollowUpsPage extends BasePage {

    private static final By HEADER_TITLE = By.xpath("//header//h1[normalize-space()='Follow Ups']");
    private static final By PAGE_HEADING = By.xpath("//main//h1[normalize-space()='Follow-ups']");
    private static final By FOLLOW_UP_COUNT_LABEL = By.xpath(
            "//main//h1[normalize-space()='Follow-ups']/following-sibling::p[contains(@class,'text-slate-400')]");
    private static final By BACK_BUTTON = By.xpath(
            "//main//button[contains(@class,'rounded-xl') and normalize-space()='Back']");

    private static final By CURRENT_TAB = By.xpath(
            "//main//div[contains(@class,'overflow-x-auto')]//button[normalize-space()='Current']");
    private static final By EXPIRED_TAB = By.xpath(
            "//main//div[contains(@class,'overflow-x-auto')]//button[normalize-space()='Expired']");
    private static final By COMPLETED_TAB = By.xpath(
            "//main//div[contains(@class,'overflow-x-auto')]//button[normalize-space()='Completed']");
    private static final By DISMISSED_TAB = By.xpath(
            "//main//div[contains(@class,'overflow-x-auto')]//button[normalize-space()='Dismissed']");
    private static final By ALL_TAB = By.xpath(
            "//main//div[contains(@class,'overflow-x-auto')]//button[normalize-space()='All']");

    private static final By EMPTY_STATE_HEADING = By.xpath("//h3[normalize-space()='No follow-ups in this tab']");
    private static final By EMPTY_STATE_MESSAGE = By.xpath(
            "//p[contains(normalize-space(),'Switch tabs to see current, expired, completed, or dismissed follow-ups')]");

    private static final String FOLLOW_UP_ITEM_XPATH =
            "//main//div[contains(@class,'space-y-3')]//button[contains(normalize-space(),'%s')]"
                    + "|//main//div[contains(@class,'space-y-3')]//div[contains(@class,'cursor-pointer') and contains(normalize-space(),'%s')]";
    private static final By VIEW_DETAILS_BUTTON = By.xpath("//main//button[contains(normalize-space(),'View details')]");
    private static final By ANY_FOLLOW_UP_ROW = By.xpath(
            "//main//div[contains(@class,'space-y-3')]//*[self::button or contains(@class,'cursor-pointer')]"
                    + "[contains(@class,'rounded') or contains(@class,'border')]");

    public FollowUpsPage(WebDriver driver) {
        super(driver);
    }

    public void waitForPageReady() {
        utils.waitForVisibility(PAGE_HEADING);
    }

    public boolean isFollowUpsPageLoaded() {
        return utils.isDisplayed(PAGE_HEADING);
    }

    public boolean isHeaderTitleDisplayed() {
        return utils.isDisplayed(HEADER_TITLE);
    }

    public boolean isOnFollowUpsPage() {
        return driver.getCurrentUrl().contains(ConfigReader.get("cora.followups.path"));
    }

    public String getFollowUpCountLabel() {
        return utils.getText(FOLLOW_UP_COUNT_LABEL);
    }

    public void clickCurrentTab() {
        utils.click(CURRENT_TAB);
    }

    public void clickExpiredTab() {
        utils.click(EXPIRED_TAB);
    }

    public void clickCompletedTab() {
        utils.click(COMPLETED_TAB);
    }

    public void clickDismissedTab() {
        utils.click(DISMISSED_TAB);
    }

    public void clickAllTab() {
        utils.click(ALL_TAB);
    }

    public boolean isEmptyStateDisplayed() {
        return utils.isDisplayed(EMPTY_STATE_HEADING);
    }

    public boolean isEmptyStateMessageDisplayed() {
        return utils.isDisplayed(EMPTY_STATE_MESSAGE);
    }

    public boolean isFollowUpItemDisplayed(String itemName) {
        return utils.isDisplayed(By.xpath(String.format(FOLLOW_UP_ITEM_XPATH, itemName, itemName)));
    }

    public boolean hasFollowUpItems() {
        return utils.isDisplayed(ANY_FOLLOW_UP_ROW);
    }

    public void clickFollowUpItemByName(String itemName) {
        utils.click(By.xpath(String.format(FOLLOW_UP_ITEM_XPATH, itemName, itemName)));
    }

    public void clickViewDetails() {
        utils.click(VIEW_DETAILS_BUTTON);
    }

    public void clickBack() {
        utils.click(BACK_BUTTON);
    }
}
