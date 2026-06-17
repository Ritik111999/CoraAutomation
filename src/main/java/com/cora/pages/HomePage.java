package com.cora.pages;

import com.cora.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Home dashboard page (/home).
 * Locators built from live page HTML. Microphone / voice assistant is excluded.
 */
public class HomePage extends BasePage {

    // --- Page header ---
    private static final By PAGE_HEADER_TITLE = By.xpath("//header//h1[normalize-space()='Home']");
    private static final By WELCOME_MESSAGE = By.xpath("//h1[contains(@class,'text-2xl') and contains(normalize-space(),'Welcome back')]");

    // --- Quick stats (Tasks Due / Missed Calls / Today) ---
    private static final By TASKS_DUE_STAT = By.xpath(
            "//div[contains(@class,'uppercase') and normalize-space()='Tasks Due']/preceding-sibling::div[contains(@class,'font-bold')][1]");
    private static final By MISSED_CALLS_STAT = By.xpath(
            "//div[contains(@class,'uppercase') and normalize-space()='Missed Calls']/preceding-sibling::div[contains(@class,'font-bold')][1]");
    private static final By TODAY_STAT = By.xpath(
            "//div[contains(@class,'uppercase') and normalize-space()='Today']/preceding-sibling::div[contains(@class,'font-bold')][1]");

    // --- TEST CASE 1: My Cora Queue ---
    private static final By QUEUE_SECTION_HEADING = By.xpath("//h2[.//span[normalize-space()='My Cora Queue']]");
    private static final By QUEUE_VIEW_ALL_BUTTON = By.xpath(
            "//h2[.//span[normalize-space()='My Cora Queue']]/following-sibling::button[normalize-space()='View All']");
    private static final By QUEUE_EMPTY_MESSAGE = By.xpath(
            "//div[contains(normalize-space(),'No pending tasks')]");

    // --- TEST CASE 2: Today's Live Feed ---
    private static final By LIVE_FEED_SECTION_HEADING = By.xpath("//h2[normalize-space()=\"Today's Live Feed\"]");
    private static final By LIVE_FEED_DATE_LABEL = By.xpath(
            "//h2[normalize-space()=\"Today's Live Feed\"]/following-sibling::p[contains(@class,'text-xs')]");
    private static final By LIVE_FEED_CALL_HISTORY_BUTTON = By.xpath(
            "//h2[normalize-space()=\"Today's Live Feed\"]/ancestor::div[contains(@class,'justify-between')][1]//button[normalize-space()='Call History']");
    private static final By LIVE_FEED_VIEW_ALL_BUTTON = By.xpath(
            "//h2[normalize-space()=\"Today's Live Feed\"]/ancestor::div[contains(@class,'justify-between')][1]//button[normalize-space()='View All']");
    private static final By LIVE_FEED_FIRST_ITEM = By.xpath(
            "//h2[normalize-space()=\"Today's Live Feed\"]/ancestor::div[contains(@class,'px-4')][1]//h4[contains(normalize-space(),'Appointment scheduled')]");

    // --- TEST CASE 3: Appointment Views ---
    private static final By APPOINTMENT_VIEWS_HEADING = By.xpath("//h2[normalize-space()='Appointment Views']");
    private static final By APPOINTMENT_VIEWS_VIEW_ALL_BUTTON = By.xpath(
            "//h2[normalize-space()='Appointment Views']/following-sibling::button[normalize-space()='View All']");
    private static final By APPOINTMENT_UPCOMING_CARD = By.xpath("//button[.//p[normalize-space()='Upcoming']]");
    private static final By APPOINTMENT_PAST_CARD = By.xpath("//button[.//p[normalize-space()='Past']]");
    private static final By APPOINTMENT_CANCELLED_CARD = By.xpath("//button[.//p[normalize-space()='Cancelled']]");
    private static final By APPOINTMENT_HISTORY_CARD = By.xpath("//button[.//p[normalize-space()='History']]");

    // --- Today's Schedule ---
    private static final By TODAY_SCHEDULE_HEADING = By.xpath("//h2[normalize-space()=\"Today's Schedule\"]");

    // --- Sidebar (desktop) ---
    private static final By SIDEBAR_HOME_LINK = By.xpath("//aside//a[@href='/home' and .//span[normalize-space()='Home']]");
    private static final By SIDEBAR_LOGOUT_BUTTON = By.xpath("//aside//button[normalize-space()='Logout']");

    private final PropertySearchOverlay propertySearch;

    public HomePage(WebDriver driver) {
        super(driver);
        propertySearch = new PropertySearchOverlay(driver);
    }

    public PropertySearchOverlay propertySearch() {
        return propertySearch;
    }

    public void openAuthenticated() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.login(
                ConfigReader.get("cora.login.valid.username"),
                ConfigReader.get("cora.login.valid.password"));
        utils.waitForUrlContains(ConfigReader.get("cora.home.path"));
        waitForPageReady();
    }

    public void waitForPageReady() {
        utils.waitForVisibility(WELCOME_MESSAGE);
    }

    public boolean isOnHomePage() {
        return driver.getCurrentUrl().contains(ConfigReader.get("cora.home.path"));
    }

    public boolean isPageHeaderDisplayed() {
        return utils.isDisplayed(PAGE_HEADER_TITLE);
    }

    public boolean isWelcomeMessageDisplayed() {
        return utils.isDisplayed(WELCOME_MESSAGE);
    }

    public String getWelcomeMessage() {
        return utils.getText(WELCOME_MESSAGE);
    }

    public String getTasksDueCount() {
        return utils.getText(TASKS_DUE_STAT);
    }

    public String getMissedCallsCount() {
        return utils.getText(MISSED_CALLS_STAT);
    }

    public String getTodayCount() {
        return utils.getText(TODAY_STAT);
    }

    public boolean isQueueSectionDisplayed() {
        return utils.isDisplayed(QUEUE_SECTION_HEADING);
    }

    public boolean isQueueEmptyMessageDisplayed() {
        return utils.isDisplayed(QUEUE_EMPTY_MESSAGE);
    }

    public void clickQueueViewAll() {
        utils.click(QUEUE_VIEW_ALL_BUTTON);
    }

    public boolean isLiveFeedSectionDisplayed() {
        return utils.isDisplayed(LIVE_FEED_SECTION_HEADING);
    }

    public String getLiveFeedDateLabel() {
        return utils.getText(LIVE_FEED_DATE_LABEL);
    }

    public void clickLiveFeedCallHistory() {
        utils.click(LIVE_FEED_CALL_HISTORY_BUTTON);
    }

    public void clickLiveFeedViewAll() {
        utils.click(LIVE_FEED_VIEW_ALL_BUTTON);
    }

    public boolean isLiveFeedFirstItemDisplayed() {
        return utils.isDisplayed(LIVE_FEED_FIRST_ITEM);
    }

    public boolean isAppointmentViewsSectionDisplayed() {
        return utils.isDisplayed(APPOINTMENT_VIEWS_HEADING);
    }

    public void clickAppointmentViewsViewAll() {
        utils.click(APPOINTMENT_VIEWS_VIEW_ALL_BUTTON);
    }

    public void clickAppointmentUpcomingCard() {
        utils.click(APPOINTMENT_UPCOMING_CARD);
    }

    public void clickAppointmentPastCard() {
        utils.click(APPOINTMENT_PAST_CARD);
    }

    public void clickAppointmentCancelledCard() {
        utils.click(APPOINTMENT_CANCELLED_CARD);
    }

    public void clickAppointmentHistoryCard() {
        utils.click(APPOINTMENT_HISTORY_CARD);
    }

    public boolean isTodayScheduleDisplayed() {
        return utils.isDisplayed(TODAY_SCHEDULE_HEADING);
    }

    public void clickSidebarLogout() {
        utils.click(SIDEBAR_LOGOUT_BUTTON);
    }

    public void clickSidebarHome() {
        utils.click(SIDEBAR_HOME_LINK);
    }
}
