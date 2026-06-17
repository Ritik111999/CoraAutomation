package com.cora.tests;

import com.cora.base.BaseTest;
import com.cora.config.ConfigReader;
import com.cora.pages.AddPropertyPage;
import com.cora.pages.PropertiesPage;
import com.cora.utils.RandomDataUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.openqa.selenium.TimeoutException;

import java.time.LocalDate;

/**
 * Negative coverage for:
 * - Draft wizard: missing required Step-1 dropdowns and country/state mismatch reset
 * - Published wizard: invalid listing status (e.g. SOLD / UNKNOWN)
 *
 * Note: No assertions or interactions for voice assistant / AI features.
 */
public class PropertiesNegativeCountriesAndStatusTest extends BaseTest {

    private PropertiesPage openPropertiesAuthenticated() {
        PropertiesPage propertiesPage = new PropertiesPage(getDriver());
        propertiesPage.openAuthenticated();
        propertiesPage.openFromSidebar();
        propertiesPage.waitForPageReady();
        return propertiesPage;
    }

    @DataProvider(name = "negativeDraftEmptyRequiredFields", parallel = true)
    public Object[][] negativeDraftEmptyRequiredFields() {
        return new Object[][]{
                // Empty Street Address (should prevent proceeding to step 2)
                {"United States", "Florida", "Single Family Home", true, true},
                {"Canada", "", "Condo", false, true}
        };
    }

    @Test(
            groups = {"Negative"},
            dataProvider = "negativeDraftEmptyRequiredFields",
            description = "Negative Draft - Empty required fields should block proceeding"
    )
    public void testNegative_draft_emptyRequiredFieldsShouldBlockProceeding(
            String country,
            String state,
            String propertyType,
            boolean selectState,
            boolean selectPropertyType) {
        PropertiesPage propertiesPage = openPropertiesAuthenticated();
        AddPropertyPage form = new AddPropertyPage(getDriver());
        String address = RandomDataUtils.streetAddress();

        propertiesPage.clickAddProperty();
        form.waitForStep(1);

        form.fillStep1WithLocationNegative(
                country,
                state,
                propertyType,
                "", // Intentionally empty Street Address
                selectState,
                selectPropertyType
        );

        form.clickNextButton();
        getUtils().waitForSeconds(2);

        Assert.assertTrue(
                form.isOnStep(1),
                "Should remain on Step 1 when required field(s) are empty. country=" + country + " (test address="
                        + address + ")"
        );
    }

    @Test(groups = {"Negative"}, description = "Negative Draft - Changing country should reset state")
    public void testNegative_draft_countryStateMismatchResetsState() {
        PropertiesPage propertiesPage = openPropertiesAuthenticated();
        AddPropertyPage form = new AddPropertyPage(getDriver());
        String address = RandomDataUtils.streetAddress();

        propertiesPage.clickAddProperty();
        form.waitForStep(1);

        // Start with US + a concrete state, then switch country.
        form.fillStep1WithLocation(
                "United States",
                "Florida",
                "Single Family Home",
                address
        );

        String selectedStateBefore = form.getSelectedState();
        Assert.assertEquals(selectedStateBefore, "Florida", "Precondition: Florida should be selected");

        form.selectCountryByVisibleText("Canada");
        String selectedStateAfter = form.getSelectedState();

        Assert.assertNotEquals(
                selectedStateAfter,
                "Florida",
                "After switching country, state should not remain as the previous selection"
        );
    }

    @DataProvider(name = "negativeListingStatuses", parallel = true)
    public Object[][] negativeListingStatuses() {
        return new Object[][]{
                {"SOLD"},
                {"UNKNOWN"}
        };
    }

    @Test(
            groups = {"Negative"},
            dataProvider = "negativeListingStatuses",
            description = "Negative Published - Invalid listing status should not show a matching badge"
    )
    public void testNegative_published_invalidListingStatus(String listingStatus) {
        PropertiesPage propertiesPage = openPropertiesAuthenticated();
        AddPropertyPage form = new AddPropertyPage(getDriver());
        String statusAddress = RandomDataUtils.streetAddress();

        propertiesPage.clickAddProperty();
        form.waitForStep(1);
        form.fillStep1WithLocation(
                ConfigReader.get("cora.properties.form.country"),
                ConfigReader.get("cora.properties.form.state"),
                ConfigReader.get("cora.properties.form.property.type"),
                statusAddress
        );
        form.uploadPhotosAndSaveDraft(ConfigReader.getInt("cora.properties.form.photo.count", 1));

        form.clickNextToStep(2);
        form.fillStep2WithRandomData();
        form.clickNextToStep(3);
        form.fillStep3WithRandomData();
        form.clickNextToStep(4);
        form.fillStep4WithRandomData();
        form.clickNextToStep(5);
        form.fillStep5WithRandomData();

        // selectListingStatus is defensive and may throw for unsupported values.
        if ("UNKNOWN".equalsIgnoreCase(listingStatus)) {
            Assert.assertThrows(TimeoutException.class, () -> form.selectListingStatus(listingStatus));
            return;
        }

        form.selectListingStatus(listingStatus);
        LocalDate openHouseDate = LocalDate.now().plusDays(1);
        form.addSingleOpenHouse(openHouseDate, "1:00 PM", "2:00 PM", "Refreshments provided");
        form.clickNextToStep(6);
        form.fillStep6WithRandomData();

        form.clickPublishListing();
        getUtils().waitForSeconds(3);

        propertiesPage.openFromSidebar();
        propertiesPage.waitForPageReady();

        boolean cardDisplayed = propertiesPage.isPropertyCardDisplayedByAddress(statusAddress);
        Assert.assertTrue(cardDisplayed, "Property should be present after publishing. address=" + statusAddress);

        // Negative assertion: when status=SOLD, ACTIVE badge should not be shown.
        Assert.assertFalse(
                propertiesPage.isStatusBadgeDisplayedForAddress(statusAddress, "ACTIVE"),
                "ACTIVE badge should not be displayed when listingStatus=" + listingStatus + ". address=" + statusAddress
        );

        propertiesPage.openPropertyOptionsMenuForAddress(statusAddress);
        propertiesPage.clickDeleteFromPropertyMenu();
        propertiesPage.waitForPropertyRemoved(statusAddress);
        Assert.assertFalse(propertiesPage.isPropertyCardDisplayedByAddress(statusAddress),
                "Property should be deleted. address=" + statusAddress);
    }
}

