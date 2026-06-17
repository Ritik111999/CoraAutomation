package com.cora.tests;

import com.cora.base.BaseTest;
import com.cora.config.ConfigReader;
import com.cora.pages.AddPropertyPage;
import com.cora.pages.LoginPage;
import com.cora.pages.PropertiesPage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Properties module — listing page, Add Property wizard (steps 1–6), unauth negatives.
 */
public class PropertiesTest extends BaseTest {

    private AddPropertyPage openAddPropertyWizard() {
        PropertiesPage propertiesPage = new PropertiesPage(getDriver());
        AddPropertyPage form = new AddPropertyPage(getDriver());
        propertiesPage.openAuthenticated();
        propertiesPage.openFromSidebar();
        propertiesPage.clickAddProperty();
        form.waitForStep(1);
        return form;
    }

    // -------------------------------------------------------------------------
    // POSITIVE — TC1: Properties list → Add Property form opens
    // -------------------------------------------------------------------------

    @Test(groups = {"Positive"}, description = "Properties TC1 - Open Add Property form")
    public void testPositive_properties_openAddPropertyForm() {
        AddPropertyPage form = openAddPropertyWizard();

        Assert.assertTrue(form.isOnStep(1), "Should be on wizard step 1");
        Assert.assertTrue(form.isStepIndicatorShowing(1), "Step 1 indicator should display");
        Assert.assertTrue(form.isPropertyAddressSectionDisplayed(), "Property Address section should show");
        Assert.assertTrue(form.isNextButtonEnabled(), "Next should be enabled on step 1");
    }

    // -------------------------------------------------------------------------
    // POSITIVE — TC2: Complete Add Property wizard steps 1–6
    // -------------------------------------------------------------------------

    @Test(groups = {"Positive"}, description = "Properties TC2 - Add Property wizard steps 1 to 6 with photos")
    public void testPositive_properties_addPropertyWizardSteps1To6() {
        AddPropertyPage form = openAddPropertyWizard();

        form.completeWizardThroughStep(6);
        form.fillStep6WithRandomData();

        Assert.assertTrue(form.isReviewSectionDisplayed(), "Review section should show on step 6");
        Assert.assertTrue(form.isPublishListingDisplayed(), "Publish Listing button should show");

        if (ConfigReader.getBoolean("cora.properties.form.publish.on.complete", false)) {
            form.clickPublishListing();
        } else {
            form.clickSaveDraft();
            form.waitForDraftSaved();
        }
    }

    // -------------------------------------------------------------------------
    // POSITIVE — TC3: Navigate back via step dots
    // -------------------------------------------------------------------------

    @Test(groups = {"Positive"}, description = "Properties TC3 - Navigate back in wizard using step dots")
    public void testPositive_properties_navigateBackViaStepDots() {
        AddPropertyPage form = openAddPropertyWizard();

        form.completeWizardThroughStep(4);
        Assert.assertTrue(form.isOnStep(4), "Should be on step 4 before navigating back");
        Assert.assertTrue(form.isStepIndicatorShowing(4), "Step 4 indicator should display");

        form.navigateToStepViaDots(2);
        Assert.assertTrue(form.isOnStep(2), "Should navigate back to step 2 via dots");
        Assert.assertTrue(form.isStepIndicatorShowing(2), "Step 2 indicator should display after dot click");

        form.navigateToStepViaDots(1);
        Assert.assertTrue(form.isOnStep(1), "Should navigate back to step 1 via dots");
        Assert.assertTrue(form.isPropertyAddressSectionDisplayed(), "Step 1 address fields should show");
    }

    // -------------------------------------------------------------------------
    // NEGATIVE — TC4: Publish without required description on step 6
    // -------------------------------------------------------------------------

    @Test(groups = {"Negative"}, description = "Properties TC4 - Publish blocked without property description")
    public void testNegative_properties_publishWithoutDescription() {
        AddPropertyPage form = openAddPropertyWizard();

        form.completeWizardThroughStep(6);
        Assert.assertTrue(form.isPublishListingDisplayed(), "Publish Listing should be visible on step 6");

        form.clickPublishListing();

        Assert.assertTrue(form.isDescriptionRequiredErrorDisplayed(),
                "Validation error should show when publishing without description");
        Assert.assertTrue(form.isOnStep(6), "Should remain on step 6 after failed publish");
    }

    // -------------------------------------------------------------------------
    // NEGATIVE — Unauthenticated access (2 parallel rows)
    // -------------------------------------------------------------------------

    @DataProvider(name = "propertiesUnauthenticatedData", parallel = true)
    public Object[][] propertiesUnauthenticatedData() {
        return ConfigReader.getIndexedDataSet(
                "cora.properties.negative.unauth",
                "scenario",
                "path",
                "expected"
        );
    }

    @Test(groups = {"Negative"},
            dataProvider = "propertiesUnauthenticatedData",
            description = "Properties | Negative | Protected pages redirect to login without session")
    public void testNegative_properties_unauthenticatedRedirect(String scenario, String path, String expectedLoginPath) {
        getDriver().get(ConfigReader.get("base.url") + path);
        getUtils().waitForUrlContains(expectedLoginPath);

        LoginPage loginPage = new LoginPage(getDriver());
        Assert.assertTrue(loginPage.isOnLoginPage(),
                "Should redirect to login for scenario: " + scenario);
        Assert.assertTrue(loginPage.isWelcomeHeadingDisplayed(),
                "Login page should load for scenario: " + scenario);
    }
}
