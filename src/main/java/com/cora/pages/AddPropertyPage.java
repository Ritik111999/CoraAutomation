package com.cora.pages;

import com.cora.config.ConfigReader;
import com.cora.utils.RandomDataUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Add Property wizard — steps 1–6 (/add-listing … /add-listing-step6).
 * After uploading photos on step 1, Save Draft must be clicked or images are not stored.
 * Use step dots (under the step title) to navigate back to earlier steps.
 */
public class AddPropertyPage extends BasePage {

    private static final By PAGE_HEADING = By.xpath("//h1[normalize-space()='Add Property']");
    private static final By STEP_INDICATOR = By.xpath("//span[contains(normalize-space(),'of 6')]");
    private static final By STEP_DOTS = By.xpath(
            "//span[contains(normalize-space(),'of 6')]/../div[contains(@class,'space-x-3')]//button");

    // Step 1 — Basic Information
    private static final By STREET_ADDRESS_INPUT = By.xpath("//input[@name='street_address']");
    private static final By UNIT_NUMBER_INPUT = By.xpath("//input[@name='unit_number']");
    private static final By CITY_INPUT = By.xpath("//input[@name='city']");
    private static final By ZIP_CODE_INPUT = By.xpath("//input[@name='zip_code']");
    private static final By COUNTY_INPUT = By.xpath("//input[@name='county']");
    private static final By COUNTRY_SELECT = By.xpath("//select[@name='country']");
    private static final By STATE_SELECT = By.xpath("//select[@name='state']");
    private static final By PROPERTY_TYPE_SELECT = By.xpath("//select[@name='property_type']");
    private static final By LISTING_PRICE_INPUT = By.xpath("//input[@name='listing_price']");
    private static final By BEDROOMS_INPUT = By.xpath("//input[@name='bedrooms']");
    private static final By BATHROOMS_INPUT = By.xpath("//input[@name='bathrooms']");
    private static final By SQUARE_FOOTAGE_INPUT = By.xpath("//input[@name='square_footage']");
    private static final By YEAR_BUILT_INPUT = By.xpath("//input[@name='year_built']");
    private static final By LOT_SIZE_INPUT = By.xpath("//input[@name='lot_size']");
    private static final By FILE_INPUT = By.xpath("//input[@type='file']");
    private static final By PHOTO_REMOVE_BUTTON = By.xpath("//main//button[normalize-space()='×']");
    private static final By PROPERTY_ADDRESS_HEADING = By.xpath("//h2[normalize-space()='Property Address']");

    // Step 2 — Property Features
    private static final By TOTAL_ROOM_INPUT = By.xpath("//input[@name='total_room']");
    private static final By PARKING_SPACE_INPUT = By.xpath("//input[@name='parking_space']");
    private static final By HAS_DRIVEWAY_CHECKBOX = By.xpath("//input[@name='has_driveway']");
    private static final By STORY_LEVEL_SELECT = By.xpath("//select[@name='story_level']");
    private static final By GARAGE_TYPE_SELECT = By.xpath("//select[@name='garage_type']");
    private static final By ARCHITECTURAL_STYLE_SELECT = By.xpath("//select[@name='architectural_style']");
    private static final By CONSTRUCTION_TYPE_SELECT = By.xpath("//select[@name='construction_type']");
    private static final By ROOF_TYPE_SELECT = By.xpath("//select[@name='roof_type']");

    // Step 3 — Special Features
    private static final By KITCHEN_DINING_ACCORDION = By.xpath(
            "//button[.//span[normalize-space()='Kitchen & Dining']]");
    private static final By KITCHEN_DINING_CHECKBOX = By.xpath(
            "//button[.//span[normalize-space()='Kitchen & Dining']]/following-sibling::div//input[@type='checkbox'][1]");
    private static final By OTHER_SPECIAL_FEATURES_TEXTAREA = By.xpath(
            "//textarea[contains(@placeholder,'unique features not listed')]");

    // Step 4 — Financial & Legal
    private static final By ANNUAL_PROPERTY_TAX_INPUT = By.xpath("//input[@name='annual_property_tax']");
    private static final By TAX_ASSESSMENT_INPUT = By.xpath("//input[@name='tax_assessment']");
    private static final By PARCEL_APN_INPUT = By.xpath("//input[@name='parcel_apn_number']");
    private static final By ZONING_INPUT = By.xpath("//input[@name='zoning']");
    private static final By ELEMENTARY_SCHOOL_INPUT = By.xpath("//input[@name='elementary_school']");
    private static final By MIDDLE_SCHOOL_INPUT = By.xpath("//input[@name='middle_school']");
    private static final By HIGH_SCHOOL_INPUT = By.xpath("//input[@name='high_school']");
    private static final By SCHOOL_DISTRICT_INPUT = By.xpath("//input[@name='school_district_name']");
    private static final By SPECIAL_ASSESSMENT_TEXTAREA = By.xpath("//textarea[@name='special_assessment']");
    private static final By LEGAL_DESCRIPTION_TEXTAREA = By.xpath("//textarea[@name='legal_description']");
    private static final By TAX_YEAR_SELECT = By.xpath("//select[@name='tax_year']");

    // Step 5 — Listing & Showing
    private static final By DAYS_ON_MARKET_INPUT = By.xpath("//input[@name='days_on_market']");
    private static final By LISTING_DATE_INPUT = By.xpath(
            "//label[normalize-space()='Listing Date']/following::input[@placeholder='mm/dd/yyyy'][1]");
    private static final By ADD_OPEN_HOUSE_BUTTON = By.xpath(
            "//button[contains(normalize-space(),'Add Open House')]");
    private static final By OPEN_HOUSE_MODAL_TITLE = By.xpath("//h3[normalize-space()='Add Open House']");
    private static final By OPEN_HOUSE_MODAL = By.xpath(
            "//h3[normalize-space()='Add Open House']/ancestor::div[contains(@class,'rounded-3xl')][1]");
    private static final By OPEN_HOUSE_SAVE_BUTTON = By.xpath(
            "//button[contains(normalize-space(),'Save Open House')]");
    private static final By DATE_PICKER_POPPER = By.cssSelector(".react-datepicker-popper");
    private static final By DATE_PICKER_CURRENT_MONTH = By.cssSelector(".react-datepicker__current-month");
    private static final By DATE_PICKER_NEXT_MONTH = By.cssSelector(".react-datepicker__navigation--next");
    private static final By DATE_PICKER_PREV_MONTH = By.cssSelector(".react-datepicker__navigation--previous");
    private static final By SHOWING_CONTACT_INPUT = By.xpath("//input[@name='showing_contact']");
    private static final By ACCESS_INSTRUCTION_TEXTAREA = By.xpath("//textarea[@name='access_instruction']");
    private static final By BEST_SHOWING_TIME_TEXTAREA = By.xpath("//textarea[@name='best_showing_time']");
    private static final By NOTICE_REQUIRED_SELECT = By.xpath("//select[@name='notice_required']");

    // Step 6 — Description & Review
    private static final By PROPERTY_DESCRIPTION_TEXTAREA = By.xpath(
            "//textarea[contains(@placeholder,'Describe the property, highlight')]");
    private static final By DESCRIPTION_REQUIRED_ERROR = By.xpath(
            "//p[contains(normalize-space(),'Add a property description before publishing')]");
    private static final By REVIEW_BASIC_INFO_HEADING = By.xpath("//h2[normalize-space()='Basic Info']");
    private static final By REVIEW_YOUR_LISTING_HEADING = By.xpath("//h2[normalize-space()='Review Your Listing']");
    private static final By PUBLISH_LISTING_BUTTON = By.xpath("//button[normalize-space()='Publish Listing']");

    // Step 6 — Associated Contacts (optional)
    private static final By ASSOCIATED_CONTACTS_HEADING = By.xpath("//h2[normalize-space()='Associated Contacts']");
    private static final By ADD_ASSOCIATED_CONTACT_BUTTON = By.xpath(
            "//h2[normalize-space()='Associated Contacts']/ancestor::div[contains(@class,'bg-white')]"
                    + "//button[.//span[normalize-space()='+ Add Contact']]");
    private static final By ASSOCIATED_CONTACT_CARDS = By.xpath(
            "//h2[normalize-space()='Associated Contacts']/following::span[starts-with(normalize-space(),'Contact ')]");

    private static final By SAVE_DRAFT_BUTTON = By.xpath("//button[normalize-space()='Save Draft']");
    private static final By NEXT_BUTTON = By.xpath("//button[normalize-space()='Next']");

    public AddPropertyPage(WebDriver driver) {
        super(driver);
    }

    public void waitForPageReady() {
        utils.waitForVisibility(PAGE_HEADING);
    }

    public void waitForStep(int step) {
        waitForPageReady();
        utils.waitForVisibility(STEP_INDICATOR);
        if (step == 1) {
            utils.waitForUrlContains(ConfigReader.get("cora.properties.add.path"));
        } else {
            utils.waitForUrlContains("add-listing-step" + step);
        }
    }

    public boolean isOnStep(int step) {
        String url = driver.getCurrentUrl();
        if (step == 1) {
            return url.contains(ConfigReader.get("cora.properties.add.path"))
                    && !url.contains("-step");
        }
        return url.contains("add-listing-step" + step);
    }

    public boolean isStepIndicatorShowing(int step) {
        return utils.isDisplayed(By.xpath("//span[normalize-space()='Step " + step + " of 6']"));
    }

    /** Navigate back (or forward) using the dots under the step title. */
    public void navigateToStepViaDots(int targetStep) {
        utils.scrollToTop();
        List<WebElement> dots = utils.waitForAllVisible(STEP_DOTS);
        if (targetStep < 1 || targetStep > dots.size()) {
            throw new IllegalArgumentException("Invalid wizard step: " + targetStep);
        }
        dots.get(targetStep - 1).click();
        waitForStep(targetStep);
    }

    // -------------------------------------------------------------------------
    // Step 1
    // -------------------------------------------------------------------------

    public void fillStep1WithLocation(
            String country, String state, String propertyType, String streetAddress) {
        enterStreetAddress(streetAddress);
        enterUnitNumber(RandomDataUtils.unitNumber());
        enterCity(RandomDataUtils.city());
        enterZipCode(RandomDataUtils.zipCode());
        enterCounty(RandomDataUtils.county());
        selectCountryByVisibleText(country);
        if (state != null && !state.isBlank()) {
            selectStateByVisibleText(state);
        } else {
            selectFirstNonEmptyOption(STATE_SELECT);
        }
        selectPropertyTypeByVisibleText(propertyType);
        enterListingPrice(RandomDataUtils.listingPrice());
        enterBedrooms(RandomDataUtils.bedrooms());
        enterBathrooms(RandomDataUtils.bathrooms());
        enterSquareFootage(RandomDataUtils.squareFootage());
        enterYearBuilt(RandomDataUtils.yearBuilt());
        enterLotSize(RandomDataUtils.lotSize());
    }

    /**
     * Step 1 helper for negative scenarios.
     * Allows intentionally NOT selecting required dropdowns.
     */
    public void fillStep1WithLocationNegative(
            String country,
            String state,
            String propertyType,
            String streetAddress,
            boolean selectState,
            boolean selectPropertyType) {
        enterStreetAddress(streetAddress);
        enterUnitNumber(RandomDataUtils.unitNumber());
        enterCity(RandomDataUtils.city());
        enterZipCode(RandomDataUtils.zipCode());
        enterCounty(RandomDataUtils.county());

        selectCountryByVisibleText(country);
        if (selectState) {
            if (state == null || state.isBlank()) {
                throw new IllegalArgumentException("state must be provided when selectState=true");
            }
            selectStateByVisibleText(state);
        }

        if (selectPropertyType) {
            if (propertyType == null || propertyType.isBlank()) {
                throw new IllegalArgumentException("propertyType must be provided when selectPropertyType=true");
            }
            selectPropertyTypeByVisibleText(propertyType);
        }

        enterListingPrice(RandomDataUtils.listingPrice());
        enterBedrooms(RandomDataUtils.bedrooms());
        enterBathrooms(RandomDataUtils.bathrooms());
        enterSquareFootage(RandomDataUtils.squareFootage());
        enterYearBuilt(RandomDataUtils.yearBuilt());
        enterLotSize(RandomDataUtils.lotSize());
    }

    public void fillStep1WithRandomData() {
        enterStreetAddress(RandomDataUtils.streetAddress());
        enterUnitNumber(RandomDataUtils.unitNumber());
        enterCity(RandomDataUtils.city());
        enterZipCode(RandomDataUtils.zipCode());
        enterCounty(RandomDataUtils.county());
        selectCountryByVisibleText(ConfigReader.get("cora.properties.form.country"));
        selectStateByVisibleText(ConfigReader.get("cora.properties.form.state"));
        selectPropertyTypeByVisibleText(ConfigReader.get("cora.properties.form.property.type"));
        enterListingPrice(RandomDataUtils.listingPrice());
        enterBedrooms(RandomDataUtils.bedrooms());
        enterBathrooms(RandomDataUtils.bathrooms());
        enterSquareFootage(RandomDataUtils.squareFootage());
        enterYearBuilt(RandomDataUtils.yearBuilt());
        enterLotSize(RandomDataUtils.lotSize());
    }

    public void uploadPhotosFromScreenshotsFolder(int count) {
        List<String> paths = resolveScreenshotPaths(count);
        for (String path : paths) {
            WebElement fileInput = utils.waitForPresence(FILE_INPUT);
            fileInput.sendKeys(path);
            utils.waitForVisibility(PHOTO_REMOVE_BUTTON);
        }
    }

    /** Required after photo upload — images are not persisted without Save Draft. */
    public void uploadPhotosAndSaveDraft(int photoCount) {
        uploadPhotosFromScreenshotsFolder(photoCount);
        clickSaveDraft();
        waitForDraftSaved();
    }

    public void clickNextToStep(int expectedStep) {
        utils.scrollToBottom();
        utils.click(NEXT_BUTTON);
        waitForStep(expectedStep);
    }

    /** Clicks Next without waiting for step navigation (useful for negative validations). */
    public void clickNextButton() {
        utils.scrollToBottom();
        utils.click(NEXT_BUTTON);
    }

    public boolean isPropertyAddressSectionDisplayed() {
        return utils.isDisplayed(PROPERTY_ADDRESS_HEADING);
    }

    public String getStreetAddressValue() {
        return utils.getAttribute(STREET_ADDRESS_INPUT, "value");
    }

    public String getCity() {
        return utils.getAttribute(CITY_INPUT, "value");
    }

    public String getZipCode() {
        return utils.getAttribute(ZIP_CODE_INPUT, "value");
    }

    public String getSelectedCountry() {
        return utils.getSelectedOptionText(COUNTRY_SELECT);
    }

    public String getSelectedState() {
        return utils.getSelectedOptionText(STATE_SELECT);
    }

    public String getSelectedPropertyType() {
        return utils.getSelectedOptionText(PROPERTY_TYPE_SELECT);
    }

    public void waitForStreetAddressContains(String addressFragment) {
        WebDriverWait addressWait = new WebDriverWait(driver, Duration.ofSeconds(15));
        addressWait.until(d -> {
            String value = getStreetAddressValue();
            return value != null && value.contains(addressFragment);
        });
    }

    // -------------------------------------------------------------------------
    // Step 2
    // -------------------------------------------------------------------------

    public void fillStep2WithRandomData() {
        selectFirstNonEmptyOption(STORY_LEVEL_SELECT);
        utils.sendKeys(TOTAL_ROOM_INPUT, RandomDataUtils.totalRooms());
        clickFirstCheckboxByName("heating_type[]");
        clickFirstCheckboxByName("cooling_type[]");
        clickFirstCheckboxByName("features[]");
        clickFirstCheckboxByName("features[]", 2);
        utils.sendKeys(PARKING_SPACE_INPUT, RandomDataUtils.parkingSpaces());
        utils.clickIfDisplayed(HAS_DRIVEWAY_CHECKBOX);
        selectFirstNonEmptyOption(GARAGE_TYPE_SELECT);
        selectFirstNonEmptyOption(ARCHITECTURAL_STYLE_SELECT);
        selectFirstNonEmptyOption(CONSTRUCTION_TYPE_SELECT);
        selectFirstNonEmptyOption(ROOF_TYPE_SELECT);
    }

    // -------------------------------------------------------------------------
    // Step 3
    // -------------------------------------------------------------------------

    public void fillStep3WithRandomData() {
        utils.click(KITCHEN_DINING_ACCORDION);
        utils.clickIfDisplayed(KITCHEN_DINING_CHECKBOX);
        utils.sendKeys(OTHER_SPECIAL_FEATURES_TEXTAREA, RandomDataUtils.propertyDescription());
    }

    // -------------------------------------------------------------------------
    // Step 4
    // -------------------------------------------------------------------------

    public void fillStep4WithRandomData() {
        utils.sendKeys(ANNUAL_PROPERTY_TAX_INPUT, RandomDataUtils.annualPropertyTax());
        selectFirstNonEmptyOption(TAX_YEAR_SELECT);
        utils.sendKeys(TAX_ASSESSMENT_INPUT, RandomDataUtils.taxAssessment());
        utils.sendKeys(SPECIAL_ASSESSMENT_TEXTAREA, RandomDataUtils.notes());
        utils.sendKeys(SCHOOL_DISTRICT_INPUT, RandomDataUtils.schoolDistrict());
        utils.sendKeys(ELEMENTARY_SCHOOL_INPUT, RandomDataUtils.schoolName());
        utils.sendKeys(MIDDLE_SCHOOL_INPUT, RandomDataUtils.schoolName());
        utils.sendKeys(HIGH_SCHOOL_INPUT, RandomDataUtils.schoolName());
        clickFirstCheckboxByName("utilities[]");
        clickFirstCheckboxByName("utilities[]", 2);
        utils.sendKeys(ZONING_INPUT, RandomDataUtils.zoning());
        utils.sendKeys(PARCEL_APN_INPUT, RandomDataUtils.parcelApn());
        utils.sendKeys(LEGAL_DESCRIPTION_TEXTAREA, RandomDataUtils.legalDescription());
    }

    // -------------------------------------------------------------------------
    // Step 5
    // -------------------------------------------------------------------------

    public void fillStep5WithRandomData() {
        utils.sendKeys(DAYS_ON_MARKET_INPUT, RandomDataUtils.daysOnMarket());
        enterListingDate(LocalDate.now().plusDays(1));
        utils.sendKeys(SHOWING_CONTACT_INPUT, RandomDataUtils.showingContact());
        utils.sendKeys(ACCESS_INSTRUCTION_TEXTAREA, RandomDataUtils.accessInstructions());
        utils.sendKeys(BEST_SHOWING_TIME_TEXTAREA, RandomDataUtils.bestShowingTime());
        selectFirstNonEmptyOption(NOTICE_REQUIRED_SELECT);
    }

    /**
     * Sets listing status on step 5.
     * Form values: Active, Pending, Off-Market, Coming-Soon (badges display uppercase).
     */
    public void selectListingStatus(String statusLabel) {
        String formValue = toFormListingStatusValue(statusLabel);
        By statusLabelLocator = By.xpath(
                "//label[.//input[@name='listing_status' and @value='" + formValue + "']]");
        utils.scrollToElement(statusLabelLocator);
        utils.click(statusLabelLocator);
    }

    public void enterListingDate(LocalDate date) {
        selectDateFromCalendar(LISTING_DATE_INPUT, date);
    }

    /**
     * Adds a single open house from the Step 5 modal (Date, Start Time, End Time are required).
     */
    public void addSingleOpenHouse(LocalDate openHouseDate, String startTime, String endTime, String notes) {
        utils.scrollToElement(ADD_OPEN_HOUSE_BUTTON);
        utils.click(ADD_OPEN_HOUSE_BUTTON);
        utils.waitForVisibility(OPEN_HOUSE_MODAL_TITLE);

        By openHouseDateInput = By.xpath(
                ".//label[normalize-space()='Date']/following::input[@placeholder='mm/dd/yyyy'][1]");
        By openHouseStartTimeInput = By.xpath(
                ".//label[normalize-space()='Start Time']/following::input[@placeholder='--:-- --'][1]");
        By openHouseEndTimeInput = By.xpath(
                ".//label[normalize-space()='End Time']/following::input[@placeholder='--:-- --'][1]");
        By openHouseNotes = By.xpath(
                ".//textarea[contains(@placeholder,'Refreshments provided')]");

        WebElement modal = utils.waitForVisibility(OPEN_HOUSE_MODAL);
        selectDateFromCalendar(modal, openHouseDateInput, openHouseDate);
        selectTimeFromCalendar(modal, openHouseStartTimeInput, startTime);
        selectTimeFromCalendar(modal, openHouseEndTimeInput, endTime);

        if (notes != null && !notes.isBlank()) {
            WebElement notesField = modal.findElement(openHouseNotes);
            notesField.clear();
            notesField.sendKeys(notes);
        }

        modal.findElement(By.xpath(".//button[contains(normalize-space(),'Save Open House')]")).click();
        utils.waitForInvisibility(OPEN_HOUSE_MODAL);
    }

    /** Opens react-datepicker, navigates month, and clicks the target day. */
    private void selectDateFromCalendar(By dateInputLocator, LocalDate date) {
        selectDateFromCalendar(driver, dateInputLocator, date);
    }

    private void selectDateFromCalendar(SearchContext scope, By dateInputLocator, LocalDate date) {
        WebElement dateInput = scope.findElement(dateInputLocator);
        scrollIntoView(dateInput);
        dateInput.click();
        utils.waitForVisibility(DATE_PICKER_POPPER);

        String targetMonthYear = date.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH));
        navigateCalendarToMonthYear(targetMonthYear);

        By dayCell = By.xpath("//div[contains(@class,'react-datepicker-popper')]"
                + "//div[contains(@class,'react-datepicker__day') and not(contains(@class,'disabled'))"
                + " and not(contains(@class,'outside-month')) and normalize-space(text())='"
                + date.getDayOfMonth() + "']");
        utils.click(dayCell);
        utils.waitForInvisibility(DATE_PICKER_POPPER);
    }

    /** Opens react-datepicker time list and selects a slot (e.g. "1:00 PM"). */
    private void selectTimeFromCalendar(SearchContext scope, By timeInputLocator, String timeLabel) {
        WebElement timeInput = scope.findElement(timeInputLocator);
        scrollIntoView(timeInput);
        timeInput.click();
        utils.waitForVisibility(DATE_PICKER_POPPER);

        By timeOption = By.xpath("//div[contains(@class,'react-datepicker-popper')]"
                + "//li[contains(@class,'react-datepicker__time-list-item')"
                + " and (normalize-space()='" + timeLabel + "'"
                + " or normalize-space()='" + stripLeadingZeroFromHour(timeLabel) + "')]");
        utils.click(timeOption);
        utils.waitForInvisibility(DATE_PICKER_POPPER);
    }

    private void scrollIntoView(WebElement element) {
        utils.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    private String stripLeadingZeroFromHour(String timeLabel) {
        if (timeLabel.length() > 1 && timeLabel.charAt(0) == '0' && Character.isDigit(timeLabel.charAt(1))) {
            return timeLabel.substring(1);
        }
        return timeLabel;
    }

    private void navigateCalendarToMonthYear(String targetMonthYear) {
        for (int attempt = 0; attempt < 24; attempt++) {
            String visibleMonth = utils.getText(DATE_PICKER_CURRENT_MONTH);
            if (visibleMonth.equalsIgnoreCase(targetMonthYear)) {
                return;
            }
            if (isMonthYearBefore(visibleMonth, targetMonthYear)) {
                utils.click(DATE_PICKER_NEXT_MONTH);
            } else {
                utils.click(DATE_PICKER_PREV_MONTH);
            }
        }
        throw new IllegalStateException("Unable to navigate calendar to: " + targetMonthYear);
    }

    private boolean isMonthYearBefore(String visible, String target) {
        try {
            LocalDate visibleDate = LocalDate.parse("01 " + visible, DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH));
            LocalDate targetDate = LocalDate.parse("01 " + target, DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH));
            return visibleDate.isBefore(targetDate);
        } catch (Exception e) {
            return true;
        }
    }

    private String toFormListingStatusValue(String statusLabel) {
        switch (statusLabel.trim().toUpperCase()) {
            case "ACTIVE":
                return "Active";
            case "PENDING":
                return "Pending";
            case "OFF-MARKET":
            case "OFF_MARKET":
                return "Off-Market";
            case "COMING-SOON":
            case "COMING_SOON":
                return "Coming-Soon";
            case "SOLD":
                return "Sold";
            default:
                return statusLabel.trim();
        }
    }

    // -------------------------------------------------------------------------
    // Step 6
    // -------------------------------------------------------------------------

    public void fillStep6WithRandomData() {
        fillStep6Description(RandomDataUtils.listingDescription());
    }

    public void fillStep6Description(String description) {
        utils.sendKeys(PROPERTY_DESCRIPTION_TEXTAREA, description);
    }

    public boolean isAssociatedContactsSectionDisplayed() {
        return utils.isDisplayed(ASSOCIATED_CONTACTS_HEADING)
                && utils.isDisplayed(ADD_ASSOCIATED_CONTACT_BUTTON);
    }

    public void clickAddAssociatedContact() {
        utils.scrollToElement(ADD_ASSOCIATED_CONTACT_BUTTON);
        utils.click(ADD_ASSOCIATED_CONTACT_BUTTON);
    }

    public int getAssociatedContactCount() {
        return driver.findElements(ASSOCIATED_CONTACT_CARDS).size();
    }

    /**
     * Adds one associated contact on step 6 using config defaults for the given contact index (1-based).
     */
    public void addAssociatedContactWithDefaults(int contactNumber) {
        String roleKey = "cora.properties.form.contact.role." + contactNumber;
        String role = ConfigReader.get(roleKey, "Buyer");
        addAssociatedContact(
                contactNumber,
                RandomDataUtils.contactName(),
                role,
                RandomDataUtils.phoneNumber(),
                RandomDataUtils.contactEmail());
    }

    public void addAssociatedContact(int contactNumber, String name, String role, String phone, String email) {
        while (getAssociatedContactCount() < contactNumber) {
            clickAddAssociatedContact();
        }
        WebElement card = waitForAssociatedContactCard(contactNumber);
        fillAssociatedContactCard(card, name, role, phone, email);
    }

    public void addAssociatedContactWithInvalidEmail(int contactNumber) {
        String invalidEmail = ConfigReader.get("cora.properties.form.contact.invalid.email", "not-a-valid-email");
        addAssociatedContact(
                contactNumber,
                RandomDataUtils.contactName(),
                ConfigReader.get("cora.properties.form.contact.role.1", "Buyer"),
                RandomDataUtils.phoneNumber(),
                invalidEmail);
    }

    public boolean isAssociatedContactEmailInvalid(int contactNumber) {
        try {
            WebElement card = waitForAssociatedContactCard(contactNumber);
            WebElement emailInput = card.findElement(By.xpath(".//input[@placeholder='Email']"));
            String ariaInvalid = emailInput.getAttribute("aria-invalid");
            if ("true".equalsIgnoreCase(ariaInvalid)) {
                return true;
            }
            Object valid = utils.executeScript("return arguments[0].validity.valid;", emailInput);
            return Boolean.FALSE.equals(valid);
        } catch (Exception e) {
            return false;
        }
    }

    public void removeAssociatedContact(int contactNumber) {
        WebElement card = waitForAssociatedContactCard(contactNumber);
        WebElement removeButton = card.findElement(By.xpath(".//button[normalize-space()='Remove']"));
        scrollIntoView(removeButton);
        removeButton.click();
    }

    public boolean isAssociatedContactDisplayed(int contactNumber, String nameFragment) {
        try {
            WebElement card = waitForAssociatedContactCard(contactNumber);
            WebElement nameInput = card.findElement(By.xpath(".//input[@placeholder='Name']"));
            String value = nameInput.getAttribute("value");
            if (value != null && value.contains(nameFragment)) {
                return true;
            }
            Object jsValue = utils.executeScript("return arguments[0].value;", nameInput);
            return jsValue != null && jsValue.toString().contains(nameFragment);
        } catch (Exception e) {
            return false;
        }
    }

    private WebElement waitForAssociatedContactCard(int contactNumber) {
        By cardLocator = By.xpath(
                "//span[normalize-space()='Contact " + contactNumber + "']"
                        + "/ancestor::div[contains(@class,'rounded-2xl') and contains(@class,'border')][1]");
        return utils.waitForVisibility(cardLocator);
    }

    private void fillAssociatedContactCard(WebElement card, String name, String role, String phone, String email) {
        WebElement nameInput = card.findElement(By.xpath(".//input[@placeholder='Name']"));
        WebElement roleSelect = card.findElement(By.xpath(".//select[@aria-label='Contact role']"));
        WebElement phoneInput = card.findElement(By.xpath(".//input[@placeholder='Phone']"));
        WebElement emailInput = card.findElement(By.xpath(".//input[@placeholder='Email']"));

        scrollIntoView(nameInput);
        nameInput.click();
        nameInput.clear();
        nameInput.sendKeys(name);

        scrollIntoView(roleSelect);
        new Select(roleSelect).selectByVisibleText(role);

        phoneInput.click();
        phoneInput.clear();
        phoneInput.sendKeys(phone);

        emailInput.click();
        emailInput.clear();
        emailInput.sendKeys(email);
    }

    public void clearPropertyDescription() {
        WebElement textarea = utils.waitForVisibility(PROPERTY_DESCRIPTION_TEXTAREA);
        textarea.clear();
    }

    public boolean isDescriptionRequiredErrorDisplayed() {
        return utils.isDisplayed(DESCRIPTION_REQUIRED_ERROR);
    }

    public boolean isReviewSectionDisplayed() {
        return utils.isDisplayed(REVIEW_BASIC_INFO_HEADING)
                && utils.isDisplayed(REVIEW_YOUR_LISTING_HEADING);
    }

    public boolean isPublishListingDisplayed() {
        return utils.isDisplayed(PUBLISH_LISTING_BUTTON);
    }

    /**
     * Fills step 1 (with photo upload + Save Draft) and advances through {@code targetStep}.
     * Caller must already be on step 1.
     */
    public void completeWizardThroughStep(int targetStep) {
        int photoCount = ConfigReader.getInt("cora.properties.form.photo.count", 1);
        fillStep1WithRandomData();
        uploadPhotosAndSaveDraft(photoCount);

        if (targetStep <= 1) {
            return;
        }
        clickNextToStep(2);
        fillStep2WithRandomData();

        if (targetStep <= 2) {
            return;
        }
        clickNextToStep(3);
        fillStep3WithRandomData();

        if (targetStep <= 3) {
            return;
        }
        clickNextToStep(4);
        fillStep4WithRandomData();

        if (targetStep <= 4) {
            return;
        }
        clickNextToStep(5);
        fillStep5WithRandomData();

        if (targetStep <= 5) {
            return;
        }
        clickNextToStep(6);
    }

    // -------------------------------------------------------------------------
    // Shared field accessors (step 1)
    // -------------------------------------------------------------------------

    public void enterStreetAddress(String address) {
        utils.sendKeys(STREET_ADDRESS_INPUT, address);
    }

    public void enterUnitNumber(String unit) {
        utils.sendKeys(UNIT_NUMBER_INPUT, unit);
    }

    public void enterCity(String city) {
        utils.sendKeys(CITY_INPUT, city);
    }

    public void enterZipCode(String zip) {
        utils.sendKeys(ZIP_CODE_INPUT, zip);
    }

    public void enterCounty(String county) {
        utils.sendKeys(COUNTY_INPUT, county);
    }

    public void selectCountryByVisibleText(String country) {
        utils.selectByVisibleText(COUNTRY_SELECT, country);
    }

    public void selectStateByVisibleText(String state) {
        utils.selectByVisibleText(STATE_SELECT, state);
    }

    public void selectPropertyTypeByVisibleText(String propertyType) {
        utils.selectByVisibleText(PROPERTY_TYPE_SELECT, propertyType);
    }

    public void enterListingPrice(String price) {
        utils.sendKeys(LISTING_PRICE_INPUT, price);
    }

    public void enterBedrooms(String bedrooms) {
        utils.sendKeys(BEDROOMS_INPUT, bedrooms);
    }

    public void enterBathrooms(String bathrooms) {
        utils.sendKeys(BATHROOMS_INPUT, bathrooms);
    }

    public void enterSquareFootage(String sqft) {
        utils.sendKeys(SQUARE_FOOTAGE_INPUT, sqft);
    }

    public void enterYearBuilt(String year) {
        utils.sendKeys(YEAR_BUILT_INPUT, year);
    }

    public void enterLotSize(String lotSize) {
        utils.sendKeys(LOT_SIZE_INPUT, lotSize);
    }

    public boolean isNextButtonEnabled() {
        return utils.isEnabled(NEXT_BUTTON);
    }

    public void clickSaveDraft() {
        utils.scrollToTop();
        utils.click(SAVE_DRAFT_BUTTON);
    }

    public void clickPublishListing() {
        utils.scrollToBottom();
        utils.click(PUBLISH_LISTING_BUTTON);
    }

    public void waitForDraftSaved() {
        utils.waitForSeconds(2);
    }

    private void selectFirstNonEmptyOption(By selectLocator) {
        if (utils.isDisplayed(selectLocator)) {
            utils.selectByIndex(selectLocator, 1);
        }
    }

    private void clickFirstCheckboxByName(String name) {
        clickFirstCheckboxByName(name, 1);
    }

    private void clickFirstCheckboxByName(String name, int index) {
        By locator = By.xpath("(//input[@name='" + name + "'])[" + index + "]");
        if (utils.isDisplayed(locator)) {
            utils.scrollToElement(locator);
            utils.clickWithJs(locator);
        }
    }

    private List<String> resolveScreenshotPaths(int count) {
        String screenshotDir = System.getProperty("user.dir") + File.separator + "test-output" + File.separator + "screenshots";
        File dir = new File(screenshotDir);
        File[] pngs = dir.listFiles((d, fileName) -> fileName.toLowerCase().endsWith(".png"));
        if (pngs == null || pngs.length == 0) {
            throw new IllegalStateException("No PNG files found in: " + screenshotDir);
        }
        Arrays.sort(pngs, Comparator.comparingLong(File::lastModified).reversed());
        return Arrays.stream(pngs).limit(count).map(File::getAbsolutePath).toList();
    }
}
