package com.cora.pages;

import com.cora.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Draft Properties listing (/properties/drafts or similar).
 */
public class DraftPropertiesPage extends BasePage {

    private static final By PAGE_HEADING = By.xpath("//header//h1[normalize-space()='Draft Properties']");
    private static final By DRAFT_CARD = By.xpath(
            "//main//div[contains(@class,'rounded-3xl') and contains(@class,'cursor-pointer')]");
    private static final By DELETE_DRAFT_BUTTON = By.xpath("//button[normalize-space()='Delete Draft']");
    private static final By IN_APP_CONFIRM_DELETE = By.xpath(
            "//div[contains(@class,'fixed') and contains(@class,'inset-0')]"
                    + "//button[normalize-space()='Delete' or normalize-space()='Confirm' or normalize-space()='Yes']");

    public DraftPropertiesPage(WebDriver driver) {
        super(driver);
    }

    public void waitForPageReady() {
        utils.waitForVisibility(PAGE_HEADING);
    }

    public boolean isDraftPropertiesPageLoaded() {
        return utils.isDisplayed(PAGE_HEADING);
    }

    public int getDraftCardCount() {
        return utils.waitForAllVisible(DRAFT_CARD).size();
    }

    public boolean isDraftDisplayedByAddressFragment(String addressFragment) {
        return utils.isDisplayed(draftCardByAddress(addressFragment));
    }

    /** Opens draft in edit mode — clicks the card title area, not Delete Draft. */
    public void openDraftForEditByAddress(String addressFragment) {
        utils.click(draftTitleByAddress(addressFragment));
        utils.waitForUrlContains(ConfigReader.get("cora.properties.add.path"));
    }

    public void deleteDraftByAddress(String addressFragment) {
        utils.click(deleteDraftButtonByAddress(addressFragment));
        utils.acceptAlertIfPresent();
        utils.clickIfDisplayedWithin(IN_APP_CONFIRM_DELETE, 3);
        utils.waitForSeconds(2);
    }

    public void waitForDraftRemoved(String addressFragment) {
        utils.waitForInvisibility(draftCardByAddress(addressFragment));
    }

    private By draftCardByAddress(String addressFragment) {
        return By.xpath("//main//p[contains(normalize-space(),'"
                + escapeXpathLiteral(addressFragment)
                + "')]/ancestor::div[contains(@class,'rounded-3xl')][1]");
    }

    private By draftTitleByAddress(String addressFragment) {
        return By.xpath("//main//p[contains(normalize-space(),'"
                + escapeXpathLiteral(addressFragment)
                + "')]/ancestor::div[contains(@class,'rounded-3xl')][1]//h4");
    }

    private By deleteDraftButtonByAddress(String addressFragment) {
        return By.xpath("//main//p[contains(normalize-space(),'"
                + escapeXpathLiteral(addressFragment)
                + "')]/ancestor::div[contains(@class,'rounded-3xl')][1]//button[normalize-space()='Delete Draft']");
    }

    private static String escapeXpathLiteral(String value) {
        if (!value.contains("'")) {
            return value;
        }
        return "concat('" + value.replace("'", "', \"'\", '") + "')";
    }
}
