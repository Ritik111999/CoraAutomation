package com.cora.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Global property search dropdown in the app header (Home, Properties, etc.).
 */
public class PropertySearchOverlay extends BasePage {

    private static final By OPEN_SEARCH_BUTTON = By.xpath("//button[@aria-label='Open property search']");
    private static final By CLOSE_HEADER_SEARCH_BUTTON = By.xpath("//button[@aria-label='Close property search']");
    private static final By SEARCH_INPUT = By.xpath("//input[@placeholder='Search properties by address, city, ZIP']");
    private static final By CLEAR_SEARCH_BUTTON = By.xpath("//button[@aria-label='Close search']");
    private static final By RESULTS_PANEL = By.xpath(
            "//header//div[contains(@class,'max-h-80') and contains(@class,'overflow-y-auto')]");
    private static final By NO_RESULTS_MESSAGE = By.xpath(
            "//header//*[contains(translate(normalize-space(),"
                    + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'no results')"
                    + " or contains(translate(normalize-space(),"
                    + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'no properties')]");

    public PropertySearchOverlay(WebDriver driver) {
        super(driver);
    }

    public void open() {
        if (isInputDisplayed()) {
            return;
        }
        if (utils.isDisplayed(OPEN_SEARCH_BUTTON)) {
            utils.click(OPEN_SEARCH_BUTTON);
        }
        utils.waitForVisibility(SEARCH_INPUT);
    }

    public void close() {
        if (utils.isDisplayed(CLOSE_HEADER_SEARCH_BUTTON)) {
            utils.click(CLOSE_HEADER_SEARCH_BUTTON);
            utils.waitForInvisibility(SEARCH_INPUT);
        }
    }

    public boolean isInputDisplayed() {
        try {
            return utils.isDisplayed(SEARCH_INPUT);
        } catch (Exception e) {
            return false;
        }
    }

    public String getPlaceholder() {
        open();
        return utils.getAttribute(SEARCH_INPUT, "placeholder");
    }

    public void enterSearchTerm(String term) {
        open();
        WebElement input = utils.waitForVisibility(SEARCH_INPUT);
        input.clear();
        input.sendKeys(term);
    }

    public void clearSearchInput() {
        if (utils.isDisplayed(CLEAR_SEARCH_BUTTON)) {
            utils.click(CLEAR_SEARCH_BUTTON);
        }
    }

    public String getSearchValue() {
        if (!isInputDisplayed()) {
            return "";
        }
        return utils.getAttribute(SEARCH_INPUT, "value");
    }

    public void waitForSearchResultContaining(String fragment) {
        By result = searchResultContaining(fragment);
        utils.waitForVisibility(result);
    }

    public boolean isSearchResultDisplayed(String fragment) {
        return utils.isDisplayed(searchResultContaining(fragment));
    }

    public void waitForNoSearchResults() {
        org.openqa.selenium.support.ui.WebDriverWait resultWait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(8));
        resultWait.until(d -> getSearchResultItemCount() == 0 || isNoResultsMessageDisplayed());
    }

    public String getResultsPanelText() {
        if (!utils.isDisplayed(RESULTS_PANEL)) {
            return "";
        }
        return utils.getText(RESULTS_PANEL).trim();
    }

    public int getSearchResultItemCount() {
        if (!utils.isDisplayed(RESULTS_PANEL)) {
            return 0;
        }
        return driver.findElements(By.xpath("//header//div[contains(@class,'max-h-80')]//h4")).size();
    }

    public boolean isNoResultsMessageDisplayed() {
        return utils.isDisplayed(NO_RESULTS_MESSAGE)
                || getResultsPanelText().toLowerCase().contains("no results")
                || getResultsPanelText().toLowerCase().contains("no properties");
    }

    public boolean isNoResultsDisplayed() {
        if (!isInputDisplayed()) {
            return false;
        }
        String value = getSearchValue();
        if (value == null || value.isBlank()) {
            return false;
        }
        return getSearchResultItemCount() == 0 || isNoResultsMessageDisplayed();
    }

    public void clickFirstSearchResult() {
        utils.click(By.xpath(
                "(//header//div[contains(@class,'max-h-80')]//button[contains(@class,'cursor-pointer')]"
                        + " | //header//div[contains(@class,'max-h-80')]//*[self::button or self::div]"
                        + "[contains(@class,'cursor-pointer')])[1]"));
    }

    private By searchResultContaining(String fragment) {
        return By.xpath("//header//div[contains(@class,'max-h-80')]"
                + "//*[contains(normalize-space(),\"" + escapeXpathLiteral(fragment) + "\")]");
    }

    private static String escapeXpathLiteral(String value) {
        if (!value.contains("\"")) {
            return value;
        }
        return value.replace("\"", "'");
    }
}
