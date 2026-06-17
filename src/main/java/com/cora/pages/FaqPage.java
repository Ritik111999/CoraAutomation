package com.cora.pages;

import com.cora.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * FAQ page (/faq) — searchable accordion FAQ with category sections.
 */
public class FaqPage extends BasePage {

    private static final By HEADER_TITLE = By.xpath("//header//h1[normalize-space()='FAQ']");
    private static final By SEARCH_INPUT = By.xpath("//input[@placeholder='Search questions...']");
    private static final By STILL_NEED_HELP_HEADING = By.xpath("//h2[normalize-space()='Still Need Help?']");
    private static final By CONTACT_SUPPORT_BUTTON = By.xpath("//button[normalize-space()='Contact Support']");
    private static final By FAQ_DETAILS = By.xpath("//main//details");
    private static final By FIRST_FAQ_SUMMARY = By.xpath("//main//details/summary");
    private static final By NO_RESULTS_MESSAGE = By.xpath(
            "//main//*[contains(normalize-space(),'No results') or contains(normalize-space(),'no results')]");

    private static final By SIDEBAR_FAQ_LINK = By.xpath(
            "//aside//a[@href='/faq' and .//span[normalize-space()='FAQ']]");

    public FaqPage(WebDriver driver) {
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
        utils.click(SIDEBAR_FAQ_LINK);
        waitForPageReady();
    }

    public void openDirect() {
        navigateWithRetry(ConfigReader.get("cora.faq.path"));
        waitForPageReady();
    }

    public void waitForPageReady() {
        utils.waitForVisibility(SEARCH_INPUT);
        utils.waitForVisibility(FIRST_FAQ_SUMMARY);
    }

    public boolean isOnFaqPage() {
        return driver.getCurrentUrl().contains(ConfigReader.get("cora.faq.path"));
    }

    public boolean isFaqPageLoaded() {
        return utils.isDisplayed(SEARCH_INPUT)
                && utils.isDisplayed(STILL_NEED_HELP_HEADING)
                && utils.isDisplayed(CONTACT_SUPPORT_BUTTON)
                && getVisibleQuestionCount() > 0;
    }

    public boolean isHeaderTitleDisplayed() {
        return utils.isDisplayed(HEADER_TITLE);
    }

    public boolean isCategoryDisplayed(String categoryName) {
        return utils.isDisplayed(By.xpath("//main//h2[normalize-space()='" + categoryName + "']"));
    }

    public void enterSearchTerm(String term) {
        utils.sendKeys(SEARCH_INPUT, term);
    }

    public void clearSearch() {
        WebElement search = utils.waitForVisibility(SEARCH_INPUT);
        search.clear();
        search.sendKeys("");
    }

    public int getVisibleQuestionCount() {
        List<WebElement> questions = driver.findElements(FAQ_DETAILS);
        return (int) questions.stream().filter(WebElement::isDisplayed).count();
    }

    public boolean isContactSupportDisplayed() {
        return utils.isDisplayed(CONTACT_SUPPORT_BUTTON);
    }

    public boolean isQuestionDisplayed(String questionText) {
        return utils.isDisplayed(questionLocator(questionText));
    }

    public void expandQuestion(String questionText) {
        By summary = By.xpath(questionDetailsXpath(questionText) + "/summary");
        utils.scrollToElement(summary);
        utils.click(summary);
    }

    public boolean isQuestionExpanded(String questionText) {
        WebElement details = utils.waitForPresence(questionLocator(questionText));
        return details.getAttribute("open") != null;
    }

    public boolean isAnswerDisplayed(String questionText) {
        By answer = By.xpath(questionDetailsXpath(questionText) + "/div[contains(@class,'pb-6')]");
        return utils.isDisplayed(answer);
    }

    public String getAnswerText(String questionText) {
        By answer = By.xpath(questionDetailsXpath(questionText) + "/div[contains(@class,'pb-6')]");
        return utils.getText(answer);
    }

    public boolean isNoResultsDisplayed() {
        return utils.isDisplayed(NO_RESULTS_MESSAGE) || getVisibleQuestionCount() == 0;
    }

    public void clickContactSupport() {
        utils.scrollToElement(CONTACT_SUPPORT_BUTTON);
        utils.clickWithJs(CONTACT_SUPPORT_BUTTON);
    }

    private By questionLocator(String questionText) {
        return By.xpath(questionDetailsXpath(questionText));
    }

    private String questionDetailsXpath(String questionText) {
        return "//main//details[.//summary//span[normalize-space()=\"" + questionText + "\"]]";
    }
}
