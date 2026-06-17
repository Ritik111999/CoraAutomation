package com.cora.tests;

import com.cora.base.BaseTest;
import com.cora.config.ConfigReader;
import com.cora.pages.FaqPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * FAQ module — search, accordion expansion, and support section.
 * Fully parallel-safe: each @Test uses an isolated Chrome browser (see testng-profile-contact-faq-parallel.xml).
 */
public class FaqTest extends BaseTest {

    private FaqPage openFaqAuthenticated() {
        FaqPage faqPage = new FaqPage(getDriver());
        faqPage.openAuthenticated();
        faqPage.openFromSidebar();
        faqPage.waitForPageReady();
        return faqPage;
    }

    // -------------------------------------------------------------------------
    // POSITIVE — TC1: FAQ page loads with categories and questions
    // -------------------------------------------------------------------------

    @Test(groups = {"Positive"}, description = "FAQ TC1 - Page loads with search and FAQ sections")
    public void testPositive_faq_pageLoadsWithSections() {
        FaqPage faqPage = openFaqAuthenticated();

        Assert.assertTrue(faqPage.isOnFaqPage(), "URL should be FAQ page");
        Assert.assertTrue(faqPage.isFaqPageLoaded(), "FAQ search and questions should display");
        Assert.assertTrue(faqPage.isHeaderTitleDisplayed(), "Header should say FAQ");
        Assert.assertTrue(faqPage.isCategoryDisplayed(ConfigReader.get("cora.faq.category.getting.started")),
                "Getting Started category should show");
        Assert.assertTrue(faqPage.isCategoryDisplayed(ConfigReader.get("cora.faq.category.troubleshooting")),
                "Troubleshooting category should show");
        Assert.assertTrue(faqPage.isQuestionDisplayed(ConfigReader.get("cora.faq.question.setup")),
                "Setup question should be visible");
    }

    // -------------------------------------------------------------------------
    // POSITIVE — TC2: Search filters FAQ questions
    // -------------------------------------------------------------------------

    @Test(groups = {"Positive"}, description = "FAQ TC2 - Search filters matching questions")
    public void testPositive_faq_searchFiltersQuestions() {
        FaqPage faqPage = openFaqAuthenticated();

        Assert.assertTrue(faqPage.isQuestionDisplayed(ConfigReader.get("cora.faq.question.setup")),
                "Setup question should be visible before search");

        faqPage.enterSearchTerm(ConfigReader.get("cora.faq.search.match"));
        getUtils().waitForSeconds(1);

        Assert.assertTrue(faqPage.isQuestionDisplayed(ConfigReader.get("cora.faq.question.property")),
                "Property-related question should remain visible after search");
        Assert.assertFalse(faqPage.isQuestionDisplayed(ConfigReader.get("cora.faq.question.setup")),
                "Unrelated question should be hidden after search");
    }

    // -------------------------------------------------------------------------
    // POSITIVE — TC3: Expanding a question shows its answer
    // -------------------------------------------------------------------------

    @Test(groups = {"Positive"}, description = "FAQ TC3 - Expand question shows answer")
    public void testPositive_faq_expandQuestionShowsAnswer() {
        FaqPage faqPage = openFaqAuthenticated();
        String question = ConfigReader.get("cora.faq.question.setup");

        faqPage.expandQuestion(question);

        Assert.assertTrue(faqPage.isQuestionExpanded(question), "Question accordion should open");
        Assert.assertTrue(faqPage.isAnswerDisplayed(question), "Answer content should be visible");
        Assert.assertTrue(faqPage.getAnswerText(question).toLowerCase()
                        .contains(ConfigReader.get("cora.faq.question.setup.answer.fragment").toLowerCase()),
                "Answer should contain expected guidance text");
    }

    // -------------------------------------------------------------------------
    // POSITIVE — TC4: Still Need Help section shows Contact Support
    // -------------------------------------------------------------------------

    @Test(groups = {"Positive"}, description = "FAQ TC4 - Still Need Help shows Contact Support")
    public void testPositive_faq_stillNeedHelpShowsContactSupport() {
        FaqPage faqPage = openFaqAuthenticated();

        Assert.assertTrue(faqPage.isContactSupportDisplayed(),
                "Contact Support button should be visible");
        Assert.assertTrue(faqPage.isOnFaqPage(), "User should remain on FAQ page");
    }

    // -------------------------------------------------------------------------
    // NEGATIVE — TC5: Search with no match shows empty results
    // -------------------------------------------------------------------------

    @Test(groups = {"Negative"}, description = "FAQ TC5 - Search with no match shows no results")
    public void testNegative_faq_searchWithNoMatches() {
        FaqPage faqPage = openFaqAuthenticated();

        faqPage.enterSearchTerm(ConfigReader.get("cora.faq.search.nomatch"));
        getUtils().waitForSeconds(1);

        Assert.assertTrue(faqPage.isNoResultsDisplayed(),
                "No matching FAQ items should be shown for unmatched search");
        Assert.assertTrue(faqPage.isOnFaqPage(), "User should remain on FAQ after empty search");
    }

    // -------------------------------------------------------------------------
    // POSITIVE — TC5: FAQ accessible without login
    // -------------------------------------------------------------------------

    @Test(groups = {"Positive"}, description = "FAQ TC5 - Page loads without authentication")
    public void testPositive_faq_accessibleWithoutLogin() {
        FaqPage faqPage = new FaqPage(getDriver());
        faqPage.openDirect();

        Assert.assertTrue(faqPage.isOnFaqPage(), "FAQ should load without login");
        Assert.assertTrue(faqPage.isFaqPageLoaded(), "FAQ content should be visible to guest users");
    }

    // -------------------------------------------------------------------------
    // NEGATIVE — TC6: Special-character search shows no results
    // -------------------------------------------------------------------------

    @Test(groups = {"Negative"}, description = "FAQ TC6 - Special-character search shows no results")
    public void testNegative_faq_searchWithSpecialCharactersShowsNoResults() {
        FaqPage faqPage = openFaqAuthenticated();

        faqPage.enterSearchTerm(ConfigReader.get("cora.faq.search.specialchars"));
        getUtils().waitForSeconds(1);

        Assert.assertTrue(faqPage.isNoResultsDisplayed(),
                "Special-character search should not match FAQ items");
        Assert.assertTrue(faqPage.isOnFaqPage(), "User should remain on FAQ page");
    }
}
