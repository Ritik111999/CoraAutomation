package com.cora.tests;

import com.cora.base.BaseTest;
import com.cora.config.ConfigReader;
import com.cora.pages.AddPropertyPage;
import com.cora.pages.PropertiesPage;
import com.cora.utils.RandomDataUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;

/**
 * Published property lifecycle on the main Properties list — create, edit, delete
 * (runs in order via dependsOnMethods).
 */
public class PropertiesPublishedLifecycleTest extends BaseTest {

    private String publishedStreetAddress;

    private PropertiesPage openPropertiesAuthenticated() {
        PropertiesPage propertiesPage = new PropertiesPage(getDriver());
        propertiesPage.openAuthenticated();
        propertiesPage.openFromSidebar();
        propertiesPage.waitForPageReady();
        return propertiesPage;
    }

    @Test(groups = {"Positive"}, priority = 1,
            description = "Published TC1 - Create and publish property from wizard")
    public void testPositive_published_createAndPublishProperty() {
        PropertiesPage propertiesPage = openPropertiesAuthenticated();
        AddPropertyPage form = new AddPropertyPage(getDriver());

        publishedStreetAddress = RandomDataUtils.streetAddress();
        propertiesPage.clickAddProperty();
        form.waitForStep(1);

        form.fillStep1WithLocation(
                ConfigReader.get("cora.properties.form.country"),
                ConfigReader.get("cora.properties.form.state"),
                ConfigReader.get("cora.properties.form.property.type"),
                publishedStreetAddress);
        form.uploadPhotosAndSaveDraft(ConfigReader.getInt("cora.properties.form.photo.count", 1));

        form.clickNextToStep(2);
        form.fillStep2WithRandomData();
        form.clickNextToStep(3);
        form.fillStep3WithRandomData();
        form.clickNextToStep(4);
        form.fillStep4WithRandomData();
        form.clickNextToStep(5);
        form.fillStep5WithRandomData();
        LocalDate openHouseDate = LocalDate.now().plusDays(1);
        form.addSingleOpenHouse(openHouseDate, "1:00 PM", "2:00 PM", "Refreshments provided");
        form.clickNextToStep(6);
        form.fillStep6WithRandomData();
        form.clickPublishListing();
        getUtils().waitForSeconds(3);

        propertiesPage.openFromSidebar();
        propertiesPage.waitForPageReady();
        Assert.assertTrue(propertiesPage.isPropertyCardDisplayedByAddress(publishedStreetAddress),
                "Published property should appear on Properties list");
    }

    @Test(groups = {"Positive"}, priority = 2,
            dependsOnMethods = "testPositive_published_createAndPublishProperty",
            description = "Published TC2 - Edit published property via options menu")
    public void testPositive_published_editPropertyFromList() {
        Assert.assertNotNull(publishedStreetAddress, "Create/publish test must run first");

        PropertiesPage propertiesPage = openPropertiesAuthenticated();
        Assert.assertTrue(propertiesPage.isPropertyCardDisplayedByAddress(publishedStreetAddress),
                "Property should exist before edit");

        propertiesPage.openPropertyOptionsMenuForAddress(publishedStreetAddress);
        propertiesPage.clickEditFromPropertyMenu();

        AddPropertyPage form = new AddPropertyPage(getDriver());
        form.waitForStep(1);
        Assert.assertTrue(form.getStreetAddressValue().contains(
                        publishedStreetAddress.substring(0, Math.min(20, publishedStreetAddress.length()))),
                "Street address should load when editing published property");
    }

    @Test(groups = {"Positive"}, priority = 3,
            dependsOnMethods = "testPositive_published_editPropertyFromList",
            description = "Published TC3 - Delete published property via options menu")
    public void testPositive_published_deletePropertyFromList() {
        Assert.assertNotNull(publishedStreetAddress, "Create/publish test must run first");

        PropertiesPage propertiesPage = openPropertiesAuthenticated();
        Assert.assertTrue(propertiesPage.isPropertyCardDisplayedByAddress(publishedStreetAddress),
                "Property should exist before delete");

        propertiesPage.openPropertyOptionsMenuForAddress(publishedStreetAddress);
        propertiesPage.clickDeleteFromPropertyMenu();
        propertiesPage.waitForPropertyRemoved(publishedStreetAddress);

        Assert.assertFalse(propertiesPage.isPropertyCardDisplayedByAddress(publishedStreetAddress),
                "Property should be removed after delete");
    }

    @DataProvider(name = "listingStatuses", parallel = true)
    public Object[][] listingStatuses() {
        return new Object[][]{
                {"ACTIVE"},
                {"PENDING"},
                {"OFF-MARKET"},
                {"COMING-SOON"}
        };
    }

    @Test(groups = {"Positive"}, priority = 4, dataProvider = "listingStatuses",
            description = "Published TC4 - Publish listing for each step-5 status and verify badge")
    public void testPositive_published_listingStatusBadges(String listingStatus) {
        PropertiesPage propertiesPage = openPropertiesAuthenticated();
        AddPropertyPage form = new AddPropertyPage(getDriver());
        String statusAddress = RandomDataUtils.streetAddress();

        propertiesPage.clickAddProperty();
        form.waitForStep(1);
        form.fillStep1WithLocation(
                ConfigReader.get("cora.properties.form.country"),
                ConfigReader.get("cora.properties.form.state"),
                ConfigReader.get("cora.properties.form.property.type"),
                statusAddress);
        form.uploadPhotosAndSaveDraft(ConfigReader.getInt("cora.properties.form.photo.count", 1));

        form.clickNextToStep(2);
        form.fillStep2WithRandomData();
        form.clickNextToStep(3);
        form.fillStep3WithRandomData();
        form.clickNextToStep(4);
        form.fillStep4WithRandomData();
        form.clickNextToStep(5);
        form.fillStep5WithRandomData();
        form.selectListingStatus(listingStatus);
        LocalDate openHouseDate = LocalDate.now().plusDays(1);
        form.addSingleOpenHouse(openHouseDate, "1:00 PM", "2:00 PM", "Refreshments provided");
        form.clickNextToStep(6);
        form.fillStep6WithRandomData();
        form.clickPublishListing();
        getUtils().waitForSeconds(3);

        propertiesPage.openFromSidebar();
        propertiesPage.waitForPageReady();
        Assert.assertTrue(propertiesPage.isPropertyCardDisplayedByAddress(statusAddress),
                "Published property should appear for status: " + listingStatus);
        Assert.assertTrue(propertiesPage.isStatusBadgeDisplayedForAddress(statusAddress, listingStatus),
                "Status badge should match listing status: " + listingStatus);

        propertiesPage.openPropertyOptionsMenuForAddress(statusAddress);
        propertiesPage.clickDeleteFromPropertyMenu();
        propertiesPage.waitForPropertyRemoved(statusAddress);
        Assert.assertFalse(propertiesPage.isPropertyCardDisplayedByAddress(statusAddress),
                "Published status test property should be deleted for: " + listingStatus);
    }
}
