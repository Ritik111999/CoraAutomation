package com.cora.tests;

import com.cora.base.BaseTest;
import com.cora.config.ConfigReader;
import com.cora.pages.ContactUsPage;
import com.cora.utils.RandomDataUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Contact Us module — message form validation and submission.
 * Fully parallel-safe: each @Test uses an isolated Chrome browser (see testng-profile-contact-faq-parallel.xml).
 */
public class ContactUsTest extends BaseTest {

    private ContactUsPage openContactUsAuthenticated() {
        ContactUsPage contactUsPage = new ContactUsPage(getDriver());
        contactUsPage.openAuthenticated();
        contactUsPage.openFromSidebar();
        contactUsPage.waitForPageReady();
        return contactUsPage;
    }

    // -------------------------------------------------------------------------
    // POSITIVE — TC1: Contact Us page loads with form and response info
    // -------------------------------------------------------------------------

    @Test(groups = {"Positive"}, description = "Contact Us TC1 - Page loads with form fields")
    public void testPositive_contactUs_pageLoadsWithForm() {
        ContactUsPage contactUsPage = openContactUsAuthenticated();

        Assert.assertTrue(contactUsPage.isOnContactPage(), "URL should be Contact Us page");
        Assert.assertTrue(contactUsPage.isContactPageLoaded(), "Hero and form sections should display");
        Assert.assertTrue(contactUsPage.isHeaderTitleDisplayed(), "Header should say Contact Us");
        Assert.assertEquals(contactUsPage.getSelectedSubject(),
                ConfigReader.get("cora.contact.form.subject.general"),
                "Default subject should be General Inquiry");
        Assert.assertFalse(contactUsPage.isSubmitReady(),
                "Submit should appear inactive before required fields are filled");
        Assert.assertEquals(contactUsPage.getMessageCharacterCount(), 0,
                "Message counter should start at 0 / 1000");
    }

    // -------------------------------------------------------------------------
    // POSITIVE — TC2: Valid contact form submission succeeds
    // -------------------------------------------------------------------------

    @Test(groups = {"Positive"}, description = "Contact Us TC2 - Submit valid contact message")
    public void testPositive_contactUs_submitValidMessage() {
        ContactUsPage contactUsPage = openContactUsAuthenticated();

        String name = RandomDataUtils.withSuffix(ConfigReader.get("cora.contact.form.name.prefix"));
        String message = RandomDataUtils.withSuffix(ConfigReader.get("cora.contact.form.message.prefix"));

        contactUsPage.submitContactForm(
                name,
                ConfigReader.get("cora.contact.form.email"),
                ConfigReader.get("cora.contact.form.subject.support"),
                message);

        contactUsPage.waitForSubmitSuccess();

        Assert.assertTrue(contactUsPage.isOnContactPage(), "User should remain on Contact Us after submit");
        Assert.assertTrue(contactUsPage.isSubmitSuccessDisplayed()
                        || contactUsPage.isFormCleared(),
                "Success toast or cleared form should appear after valid submit");
    }

    // -------------------------------------------------------------------------
    // POSITIVE — TC3: Contact Us is accessible without login
    // -------------------------------------------------------------------------

    @Test(groups = {"Positive"}, description = "Contact Us TC3 - Page loads without authentication")
    public void testPositive_contactUs_accessibleWithoutLogin() {
        ContactUsPage contactUsPage = new ContactUsPage(getDriver());
        contactUsPage.openDirect();

        Assert.assertTrue(contactUsPage.isOnContactPage(), "Contact Us should load without login");
        Assert.assertTrue(contactUsPage.isContactPageLoaded(), "Form should be visible to guest users");
    }

    // -------------------------------------------------------------------------
    // NEGATIVE — TC4: Empty form submit does not succeed
    // -------------------------------------------------------------------------

    @Test(groups = {"Negative"}, description = "Contact Us TC4 - Empty form submit blocked")
    public void testNegative_contactUs_emptyFormSubmitBlocked() {
        ContactUsPage contactUsPage = openContactUsAuthenticated();

        Assert.assertFalse(contactUsPage.isSubmitReady(),
                "Submit should appear inactive when all fields are empty");
        Assert.assertTrue(contactUsPage.isNameEmpty(), "Name should be empty initially");
        Assert.assertTrue(contactUsPage.isMessageEmpty(), "Message should be empty initially");

        contactUsPage.clickSubmit();

        Assert.assertTrue(contactUsPage.isOnContactPage(),
                "User should remain on Contact Us after empty submit attempt");
        Assert.assertFalse(contactUsPage.isSubmitSuccessDisplayed(),
                "Empty form should not produce a success toast");
    }

    // -------------------------------------------------------------------------
    // NEGATIVE — TC5: Invalid email blocks submission
    // -------------------------------------------------------------------------

    @Test(groups = {"Negative"}, description = "Contact Us TC5 - Invalid email blocks submission")
    public void testNegative_contactUs_invalidEmailBlocksSubmit() {
        ContactUsPage contactUsPage = openContactUsAuthenticated();

        contactUsPage.fillContactForm(
                ConfigReader.get("cora.contact.form.name.prefix"),
                ConfigReader.get("cora.contact.form.email.invalid"),
                ConfigReader.get("cora.contact.form.subject.general"),
                ConfigReader.get("cora.contact.form.message.prefix"));

        Assert.assertFalse(contactUsPage.isEmailFieldValid(),
                "Browser validation should reject invalid email format");

        contactUsPage.clickSubmit();
        Assert.assertTrue(contactUsPage.isOnContactPage(),
                "Invalid email should not navigate away from Contact Us");
        Assert.assertFalse(contactUsPage.isSubmitSuccessDisplayed(),
                "Success toast should not appear for invalid email");
    }

    // -------------------------------------------------------------------------
    // NEGATIVE — TC6: Missing message submit does not succeed
    // -------------------------------------------------------------------------

    @Test(groups = {"Negative"}, description = "Contact Us TC6 - Missing message submit blocked")
    public void testNegative_contactUs_missingMessageSubmitBlocked() {
        ContactUsPage contactUsPage = openContactUsAuthenticated();

        contactUsPage.enterName(ConfigReader.get("cora.contact.form.name.prefix"));
        contactUsPage.enterEmail(ConfigReader.get("cora.contact.form.email"));
        contactUsPage.selectSubjectByVisibleText(ConfigReader.get("cora.contact.form.subject.billing"));

        Assert.assertTrue(contactUsPage.isMessageEmpty(), "Message should remain empty");
        contactUsPage.clickSubmit();

        Assert.assertTrue(contactUsPage.isOnContactPage(),
                "User should remain on Contact Us without a message");
        Assert.assertFalse(contactUsPage.isSubmitSuccessDisplayed(),
                "Submit without message should not show success toast");
    }
}
