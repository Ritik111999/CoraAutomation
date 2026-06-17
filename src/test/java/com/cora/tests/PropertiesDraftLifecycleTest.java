package com.cora.tests;

import com.cora.base.BaseTest;
import com.cora.config.ConfigReader;
import com.cora.pages.AddPropertyPage;
import com.cora.pages.DraftPropertiesPage;
import com.cora.pages.PropertiesPage;
import com.cora.utils.RandomDataUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Draft property lifecycle — create, edit, delete (runs in order via dependsOnMethods).
 * Also validates save-draft with multiple countries / property types.
 */
public class PropertiesDraftLifecycleTest extends BaseTest {

    private String createdDraftAddress;

    private PropertiesPage openPropertiesAuthenticated() {
        PropertiesPage propertiesPage = new PropertiesPage(getDriver());
        propertiesPage.openAuthenticated();
        propertiesPage.openFromSidebar();
        propertiesPage.waitForPageReady();
        return propertiesPage;
    }

    // -------------------------------------------------------------------------
    // Chain: Create → Edit → Delete (depends on prior step)
    // -------------------------------------------------------------------------

    @Test(groups = {"Positive"}, priority = 1,
            description = "Draft TC1 - Create property draft with photos and Save Draft")
    public void testPositive_draft_createPropertyDraft() {
        PropertiesPage propertiesPage = openPropertiesAuthenticated();
        AddPropertyPage form = new AddPropertyPage(getDriver());

        createdDraftAddress = RandomDataUtils.streetAddress();
        propertiesPage.clickAddProperty();
        form.waitForStep(1);

        form.fillStep1WithLocation(
                ConfigReader.get("cora.properties.form.country"),
                ConfigReader.get("cora.properties.form.state"),
                ConfigReader.get("cora.properties.form.property.type"),
                createdDraftAddress);
        int photoCount = ConfigReader.getInt("cora.properties.form.photo.count", 1);
        form.uploadPhotosAndSaveDraft(photoCount);

        propertiesPage.openFromSidebar();
        propertiesPage.waitForPageReady();
        propertiesPage.clickDraftProperties();

        DraftPropertiesPage drafts = new DraftPropertiesPage(getDriver());
        drafts.waitForPageReady();
        Assert.assertTrue(drafts.isDraftDisplayedByAddressFragment(createdDraftAddress),
                "New draft should appear on Draft Properties with address: " + createdDraftAddress);
    }

    @Test(groups = {"Positive"}, priority = 2, dependsOnMethods = "testPositive_draft_createPropertyDraft",
            description = "Draft TC2 - Edit saved draft from Draft Properties")
    public void testPositive_draft_editPropertyDraft() {
        Assert.assertNotNull(createdDraftAddress, "Create draft test must run first");

        PropertiesPage propertiesPage = openPropertiesAuthenticated();
        propertiesPage.clickDraftProperties();

        DraftPropertiesPage drafts = new DraftPropertiesPage(getDriver());
        drafts.waitForPageReady();
        Assert.assertTrue(drafts.isDraftDisplayedByAddressFragment(createdDraftAddress),
                "Draft should exist before edit");

        drafts.openDraftForEditByAddress(createdDraftAddress);

        AddPropertyPage form = new AddPropertyPage(getDriver());
        form.waitForStep(1);
        Assert.assertTrue(form.isPropertyAddressSectionDisplayed(), "Edit should open wizard step 1");
        Assert.assertTrue(form.getStreetAddressValue().contains(
                        createdDraftAddress.substring(0, Math.min(20, createdDraftAddress.length()))),
                "Street address should be pre-filled when editing draft");
    }

    @Test(groups = {"Positive"}, priority = 3,
            dependsOnMethods = "testPositive_draft_editPropertyDraft",
            description = "Draft TC3 - Delete draft from Draft Properties")
    public void testPositive_draft_deletePropertyDraft() {
        Assert.assertNotNull(createdDraftAddress, "Create draft test must run first");

        PropertiesPage propertiesPage = openPropertiesAuthenticated();
        propertiesPage.clickDraftProperties();

        DraftPropertiesPage drafts = new DraftPropertiesPage(getDriver());
        drafts.waitForPageReady();
        Assert.assertTrue(drafts.isDraftDisplayedByAddressFragment(createdDraftAddress),
                "Draft should exist before delete");

        int countBefore = drafts.getDraftCardCount();
        drafts.deleteDraftByAddress(createdDraftAddress);
        drafts.waitForDraftRemoved(createdDraftAddress);

        Assert.assertFalse(drafts.isDraftDisplayedByAddressFragment(createdDraftAddress),
                "Draft should be removed after Delete Draft");
        Assert.assertTrue(drafts.getDraftCardCount() < countBefore,
                "Draft card count should decrease after delete");
    }

    // -------------------------------------------------------------------------
    // Country / property-type variants — create, verify, edit, delete per row
    // -------------------------------------------------------------------------

    @DataProvider(name = "propertyLocationVariants", parallel = true)
    public Object[][] propertyLocationVariants() {
        return new Object[][]{
                {"United States", "Florida", "Single Family Home"},
                {"Canada", "", "Condo"},
                {"United Kingdom", "", "Townhouse"},
                {"Australia", "", "Single Family Home"},
                {"India", "", "Condo"},
        };
    }

    @Test(groups = {"Positive"}, priority = 4, dataProvider = "propertyLocationVariants",
            description = "Draft TC4 - Save draft, edit, and delete for each country/property type")
    public void testPositive_draft_lifecycleByCountryAndType(
            String country, String state, String propertyType) {
        PropertiesPage propertiesPage = openPropertiesAuthenticated();
        AddPropertyPage form = new AddPropertyPage(getDriver());
        String uniqueAddress = RandomDataUtils.streetAddress();

        propertiesPage.clickAddProperty();
        form.waitForStep(1);
        form.fillStep1WithLocation(country, state, propertyType, uniqueAddress);
        form.uploadPhotosAndSaveDraft(ConfigReader.getInt("cora.properties.form.photo.count", 1));

        propertiesPage.openFromSidebar();
        propertiesPage.waitForPageReady();
        propertiesPage.clickDraftProperties();

        DraftPropertiesPage drafts = new DraftPropertiesPage(getDriver());
        drafts.waitForPageReady();
        Assert.assertTrue(drafts.isDraftDisplayedByAddressFragment(uniqueAddress),
                "Draft should save for " + country + " / " + propertyType);

        drafts.openDraftForEditByAddress(uniqueAddress);
        form.waitForStep(1);
        form.waitForStreetAddressContains(uniqueAddress);
        Assert.assertTrue(form.getStreetAddressValue().contains(uniqueAddress),
                "Street address should persist when editing draft for " + country);
        if ("United States".equals(country)) {
            Assert.assertEquals(form.getSelectedCountry(), country,
                    "Country should persist for " + country);
        }
        Assert.assertEquals(form.getSelectedPropertyType(), propertyType,
                "Property type should persist for " + propertyType);

        propertiesPage.openFromSidebar();
        propertiesPage.clickDraftProperties();
        drafts.waitForPageReady();
        drafts.deleteDraftByAddress(uniqueAddress);
        drafts.waitForDraftRemoved(uniqueAddress);
    }
}
