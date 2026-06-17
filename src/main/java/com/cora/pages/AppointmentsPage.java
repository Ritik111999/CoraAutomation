package com.cora.pages;

import com.cora.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Appointments page (/appointments) — opens from Home → Appointment Views → View All.
 * Main page locators from live HTML. Modal locators use placeholders shown in the app forms.
 */
public class AppointmentsPage extends BasePage {

    private static final By HEADER_TITLE = By.xpath("//header//h1[normalize-space()='Appointments']");
    private static final By PAGE_HEADING = By.xpath("//main//h1[normalize-space()='Appointments']");
    private static final By PAGE_DESCRIPTION = By.xpath(
            "//main//h1[normalize-space()='Appointments']/following-sibling::p[contains(@class,'text-slate-500')]");

    private static final By BLOCK_TIME_BUTTON = By.xpath(
            "//main//div[contains(@class,'flex-wrap')]//button[normalize-space()='Block time']");
    private static final By ADD_APPOINTMENT_BUTTON = By.xpath(
            "//main//div[contains(@class,'flex-wrap')]//button[normalize-space()='Add appointment']");
    private static final By BACK_BUTTON = By.xpath(
            "//main//div[contains(@class,'flex-wrap')]//button[normalize-space()='Back']");

    private static final By ALL_TAB = By.xpath(
            "//main//div[contains(@class,'overflow-x-auto')]//button[normalize-space()='All']");
    private static final By UPCOMING_TAB = By.xpath(
            "//main//div[contains(@class,'overflow-x-auto')]//button[normalize-space()='Upcoming']");
    private static final By PAST_TAB = By.xpath(
            "//main//div[contains(@class,'overflow-x-auto')]//button[normalize-space()='Past']");
    private static final By CANCELLED_TAB = By.xpath(
            "//main//div[contains(@class,'overflow-x-auto')]//button[normalize-space()='Cancelled']");

    private static final By PREVIOUS_MONTH_BUTTON = By.xpath("//button[@aria-label='Previous month']");
    private static final By NEXT_MONTH_BUTTON = By.xpath("//button[@aria-label='Next month']");
    private static final By CALENDAR_TODAY_BUTTON = By.xpath(
            "//main//section[contains(@class,'rounded-lg')]//button[normalize-space()='Today']");
    private static final By CALENDAR_MONTH_HEADING = By.xpath(
            "//main//section[contains(@class,'rounded-lg')]//h2[contains(@class,'font-bold')]");

    private static final By SELECTED_DATE_HEADING = By.xpath("//aside//p[normalize-space()='Selected date']/following-sibling::h2");
    private static final By SIDEBAR_ADD_ON_DATE_BUTTON = By.xpath(
            "//button[@aria-label='Add appointment on selected date']");
    private static final By THIS_MONTH_HEADING = By.xpath("//aside//h2[normalize-space()='This month']");
    private static final By THIS_MONTH_COUNT_BADGE = By.xpath(
            "//aside//h2[normalize-space()='This month']/following-sibling::span");

    // Add appointment modal (opens after clicking Add appointment)
    private static final By CLIENT_NAME_INPUT = By.xpath("//input[@placeholder='Name']");
    private static final By PHONE_NUMBER_INPUT = By.xpath("//input[@placeholder='Phone number']");
    private static final By NOTES_TEXTAREA = By.xpath(
            "//textarea[@placeholder='Showing notes, qualification details, or client preferences']");
    private static final By SAVE_APPOINTMENT_BUTTON = By.xpath("//button[normalize-space()='Save appointment']");

    // Block time modal (opens after clicking Block time)
    private static final By BLOCK_TIME_CONFIRM_BUTTON = By.xpath(
            "(//button[normalize-space()='Block time'])[last()]");

    private static final String TOAST_MESSAGE_XPATH = "//*[contains(normalize-space(),'%s')]";
    private static final String SIDEBAR_ENTRY_XPATH = "//aside//*[contains(normalize-space(),'%s')]";

    public AppointmentsPage(WebDriver driver) {
        super(driver);
    }

    public void waitForPageReady() {
        utils.waitForVisibility(PAGE_HEADING);
    }

    public boolean isAppointmentsPageLoaded() {
        return utils.isDisplayed(PAGE_HEADING);
    }

    public boolean isHeaderTitleDisplayed() {
        return utils.isDisplayed(HEADER_TITLE);
    }

    public boolean isOnAppointmentsPage() {
        return driver.getCurrentUrl().contains(ConfigReader.get("cora.appointments.path"));
    }

    public String getPageDescription() {
        return utils.getText(PAGE_DESCRIPTION);
    }

    public boolean isCalendarMonthDisplayed() {
        return utils.isDisplayed(CALENDAR_MONTH_HEADING);
    }

    public boolean isThisMonthSectionDisplayed() {
        return utils.isDisplayed(THIS_MONTH_HEADING);
    }

    public void clickAllTab() {
        utils.click(ALL_TAB);
    }

    public void clickUpcomingTab() {
        utils.click(UPCOMING_TAB);
    }

    public void clickPastTab() {
        utils.click(PAST_TAB);
    }

    public void clickCancelledTab() {
        utils.click(CANCELLED_TAB);
    }

    public void clickPreviousMonth() {
        utils.click(PREVIOUS_MONTH_BUTTON);
    }

    public void clickNextMonth() {
        utils.click(NEXT_MONTH_BUTTON);
    }

    public void clickCalendarToday() {
        utils.click(CALENDAR_TODAY_BUTTON);
    }

    public void clickAddAppointment() {
        utils.click(ADD_APPOINTMENT_BUTTON);
    }

    public void clickSidebarAddOnSelectedDate() {
        utils.click(SIDEBAR_ADD_ON_DATE_BUTTON);
    }

    public void enterClientName(String name) {
        utils.sendKeys(CLIENT_NAME_INPUT, name);
    }

    public void enterPhoneNumber(String phone) {
        utils.sendKeys(PHONE_NUMBER_INPUT, phone);
    }

    public void enterNotes(String notes) {
        utils.sendKeys(NOTES_TEXTAREA, notes);
    }

    public void clickSaveAppointment() {
        utils.click(SAVE_APPOINTMENT_BUTTON);
    }

    public boolean isSaveAppointmentButtonEnabled() {
        return utils.isEnabled(SAVE_APPOINTMENT_BUTTON);
    }

    public boolean isAddAppointmentModalDisplayed() {
        return utils.isDisplayed(CLIENT_NAME_INPUT);
    }

    public void waitForAddAppointmentModalClosed() {
        utils.waitForInvisibility(CLIENT_NAME_INPUT);
    }

    public void clickBlockTime() {
        utils.click(BLOCK_TIME_BUTTON);
    }

    public void clickBlockTimeConfirm() {
        utils.click(BLOCK_TIME_CONFIRM_BUTTON);
    }

    public boolean isToastDisplayed(String message) {
        return utils.isDisplayed(By.xpath(String.format(TOAST_MESSAGE_XPATH, message)));
    }

    public void waitForToast(String message) {
        utils.waitForVisibility(By.xpath(String.format(TOAST_MESSAGE_XPATH, message)));
    }

    public boolean isSidebarEntryDisplayed(String text) {
        return utils.isDisplayed(By.xpath(String.format(SIDEBAR_ENTRY_XPATH, text)));
    }

    public boolean isSidebarStatusDisplayed(String status) {
        return utils.isDisplayed(By.xpath("//aside//span[normalize-space()='" + status + "']"));
    }

    public void clickBack() {
        utils.click(BACK_BUTTON);
    }
}
