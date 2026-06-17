package com.cora.tests;

import com.cora.base.BaseTest;
import com.cora.config.ConfigReader;
import com.cora.pages.AddPropertyPage;
import com.cora.pages.HomePage;
import com.cora.pages.PropertiesPage;
import com.cora.pages.PropertySearchOverlay;
import com.cora.utils.RandomDataUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Home header property search — exercised after publishing a listing.
 */
public class HomePropertySearchTest extends BaseTest {

    private PublishedPropertyContext publishPropertyListing() {
        PropertiesPage propertiesPage = new PropertiesPage(getDriver());
        AddPropertyPage form = new AddPropertyPage(getDriver());

        propertiesPage.openAuthenticated();
        propertiesPage.openFromSidebar();
        propertiesPage.waitForPageReady();

        String streetAddress = RandomDataUtils.streetAddress();
        propertiesPage.clickAddProperty();
        form.waitForStep(1);
        form.fillStep1WithLocation(
                ConfigReader.get("cora.properties.form.country"),
                ConfigReader.get("cora.properties.form.state"),
                ConfigReader.get("cora.properties.form.property.type"),
                streetAddress);
        String city = form.getCity();
        String zipCode = form.getZipCode();

        form.uploadPhotosAndSaveDraft(ConfigReader.getInt("cora.properties.form.photo.count", 1));
        form.clickNextToStep(2);
        form.fillStep2WithRandomData();
        form.clickNextToStep(3);
        form.fillStep3WithRandomData();
        form.clickNextToStep(4);
        form.fillStep4WithRandomData();
        form.clickNextToStep(5);
        form.fillStep5WithRandomData();
        form.clickNextToStep(6);
        form.fillStep6WithRandomData();
        form.clickPublishListing();
        getUtils().waitForSeconds(3);

        propertiesPage.openFromSidebar();
        propertiesPage.waitForPageReady();
        Assert.assertTrue(propertiesPage.isPropertyCardDisplayedByAddress(streetAddress),
                "Published property should appear before search tests");

        return new PublishedPropertyContext(propertiesPage, streetAddress, city, zipCode);
    }

    private void deletePublishedProperty(PropertiesPage propertiesPage, String streetAddress) {
        propertiesPage.openFromSidebar();
        propertiesPage.waitForPageReady();
        if (propertiesPage.isPropertyCardDisplayedByAddress(streetAddress)) {
            propertiesPage.openPropertyOptionsMenuForAddress(streetAddress);
            propertiesPage.clickDeleteFromPropertyMenu();
            propertiesPage.waitForPropertyRemoved(streetAddress);
        }
    }

    // -------------------------------------------------------------------------
    // POSITIVE — TC8: Search published listing by street address
    // -------------------------------------------------------------------------

    @Test(groups = {"Positive"}, description = "Home TC8 - Search finds published property by address")
    public void testPositive_homePropertySearch_findPublishedByAddress() {
        PublishedPropertyContext published = publishPropertyListing();
        HomePage homePage = new HomePage(getDriver());
        PropertySearchOverlay search = homePage.propertySearch();

        homePage.clickSidebarHome();
        homePage.waitForPageReady();
        Assert.assertTrue(homePage.isOnHomePage(), "User should be on Home before search");

        String addressFragment = published.streetAddress.substring(0, Math.min(20, published.streetAddress.length()));
        search.enterSearchTerm(addressFragment);
        getUtils().waitForSeconds(1);
        search.waitForSearchResultContaining(addressFragment);

        Assert.assertTrue(search.isSearchResultDisplayed(addressFragment),
                "Search dropdown should list the published property by address");
        Assert.assertTrue(homePage.isOnHomePage(), "Search should not navigate away from Home");

        deletePublishedProperty(published.propertiesPage, published.streetAddress);
    }

    // -------------------------------------------------------------------------
    // POSITIVE — TC9: Search published listing by city
    // -------------------------------------------------------------------------

    @Test(groups = {"Positive"}, description = "Home TC9 - Search finds published property by city")
    public void testPositive_homePropertySearch_findPublishedByCity() {
        PublishedPropertyContext published = publishPropertyListing();
        HomePage homePage = new HomePage(getDriver());
        PropertySearchOverlay search = homePage.propertySearch();

        homePage.clickSidebarHome();
        homePage.waitForPageReady();

        search.enterSearchTerm(published.city);
        getUtils().waitForSeconds(1);
        search.waitForSearchResultContaining(published.streetAddress.substring(0, 15));

        Assert.assertTrue(search.isSearchResultDisplayed(published.streetAddress.substring(0, 15)),
                "City search should return the published listing");
        Assert.assertEquals(published.city, ConfigReader.get("cora.home.search.city"),
                "Test city should match configured search city");

        deletePublishedProperty(published.propertiesPage, published.streetAddress);
    }

    // -------------------------------------------------------------------------
    // POSITIVE — TC10: Search published listing by ZIP
    // -------------------------------------------------------------------------

    @Test(groups = {"Positive"}, description = "Home TC10 - Search finds published property by ZIP")
    public void testPositive_homePropertySearch_findPublishedByZip() {
        PublishedPropertyContext published = publishPropertyListing();
        HomePage homePage = new HomePage(getDriver());
        PropertySearchOverlay search = homePage.propertySearch();

        homePage.clickSidebarHome();
        homePage.waitForPageReady();

        search.enterSearchTerm(published.zipCode);
        getUtils().waitForSeconds(1);
        search.waitForSearchResultContaining(published.streetAddress.substring(0, 15));

        Assert.assertTrue(search.isSearchResultDisplayed(published.streetAddress.substring(0, 15)),
                "ZIP search should return the published listing");
        Assert.assertEquals(published.zipCode, ConfigReader.get("cora.home.search.zip"),
                "Test ZIP should match configured search ZIP");

        deletePublishedProperty(published.propertiesPage, published.streetAddress);
    }

    // -------------------------------------------------------------------------
    // NEGATIVE — TC11: No-match search shows empty results
    // -------------------------------------------------------------------------

    @Test(groups = {"Negative"}, description = "Home TC11 - Search with no match shows no results")
    public void testNegative_homePropertySearch_noMatchShowsNoResults() {
        HomePage homePage = new HomePage(getDriver());
        PropertySearchOverlay search = homePage.propertySearch();

        homePage.openAuthenticated();
        search.open();
        Assert.assertEquals(search.getPlaceholder(),
                ConfigReader.get("cora.home.search.placeholder"),
                "Search placeholder should match expected copy");

        search.enterSearchTerm(ConfigReader.get("cora.home.search.nomatch"));
        search.waitForNoSearchResults();

        Assert.assertEquals(search.getSearchResultItemCount(), 0,
                "Unmatched search should not list property cards. Panel: " + search.getResultsPanelText());
        Assert.assertTrue(search.isNoResultsDisplayed(),
                "Unmatched search should show an empty or no-results state");
        Assert.assertTrue(homePage.isOnHomePage(), "User should remain on Home");
    }

    // -------------------------------------------------------------------------
    // NEGATIVE — TC12: Clear search resets the query
    // -------------------------------------------------------------------------

    @Test(groups = {"Negative"}, description = "Home TC12 - Clear search resets input")
    public void testNegative_homePropertySearch_clearSearchResetsInput() {
        HomePage homePage = new HomePage(getDriver());
        PropertySearchOverlay search = homePage.propertySearch();

        homePage.openAuthenticated();
        search.open();
        search.enterSearchTerm(ConfigReader.get("cora.home.search.nomatch"));
        getUtils().waitForSeconds(1);
        Assert.assertFalse(search.getSearchValue().isBlank(), "Search input should contain the query");

        search.clearSearchInput();
        getUtils().waitForSeconds(1);

        if (!search.isInputDisplayed()) {
            search.open();
        }
        Assert.assertTrue(search.getSearchValue().isBlank(),
                "Search field should be empty after clear and reopen");
        Assert.assertTrue(homePage.isOnHomePage(), "User should remain on Home after clear");
    }

    private record PublishedPropertyContext(PropertiesPage propertiesPage, String streetAddress, String city,
                                            String zipCode) {
    }
}
