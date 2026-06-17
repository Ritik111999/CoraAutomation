package com.cora.tests;

import com.cora.base.BaseTest;
import com.cora.config.ConfigReader;
import com.cora.pages.AppointmentsPage;
import com.cora.pages.FollowUpsPage;
import com.cora.pages.HomePage;
import com.cora.pages.LiveFeedPage;
import com.cora.pages.LoginPage;
import com.cora.utils.RandomDataUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Home page tests — 3 positive + 4 negative scenarios.
 * Positive: TC1 Follow-ups, TC2 Live Feed, TC3 Appointments (happy path).
 * Negative: 4 unauthenticated redirects.
 */
public class HomeTest extends BaseTest {

    // -------------------------------------------------------------------------
    // POSITIVE — TC1: My Cora Queue → Follow-ups
    // -------------------------------------------------------------------------

    @Test(groups = {"Positive"}, description = "Home TC1 - My Cora Queue to Follow-ups")
    public void testPositive_home_coraQueueFollowUps() {
        HomePage homePage = new HomePage(getDriver());
        FollowUpsPage followUpsPage = new FollowUpsPage(getDriver());

        homePage.openAuthenticated();
        Assert.assertTrue(homePage.isWelcomeMessageDisplayed(), "Welcome message should show");
        Assert.assertTrue(homePage.isQueueSectionDisplayed(), "My Cora Queue section should show");

        homePage.clickQueueViewAll();
        followUpsPage.waitForPageReady();
        Assert.assertTrue(followUpsPage.isOnFollowUpsPage(), "URL should be Follow-ups page");
        Assert.assertTrue(followUpsPage.isFollowUpsPageLoaded(), "Follow-ups heading should show");
        Assert.assertTrue(followUpsPage.isHeaderTitleDisplayed(), "Header should say Follow Ups");

        followUpsPage.clickCurrentTab();
        followUpsPage.clickExpiredTab();
        followUpsPage.clickCompletedTab();
        followUpsPage.clickDismissedTab();
        followUpsPage.clickAllTab();

        String item1 = ConfigReader.get("cora.followups.item.name.1");
        String item2 = ConfigReader.get("cora.followups.item.name.2");
        if (followUpsPage.isFollowUpItemDisplayed(item1)) {
            followUpsPage.clickFollowUpItemByName(item1);
            followUpsPage.clickViewDetails();
            followUpsPage.clickFollowUpItemByName(item1);
        }
        if (followUpsPage.isFollowUpItemDisplayed(item2)) {
            followUpsPage.clickFollowUpItemByName(item2);
            followUpsPage.clickViewDetails();
            followUpsPage.clickFollowUpItemByName(item2);
        }
        if (!followUpsPage.isFollowUpItemDisplayed(item1) && !followUpsPage.isFollowUpItemDisplayed(item2)) {
            Assert.assertTrue(followUpsPage.isEmptyStateDisplayed(), "Empty state should show when no follow-ups");
        }

        followUpsPage.clickBack();
        Assert.assertTrue(homePage.isOnHomePage(), "Back should return to Home");
    }

    // -------------------------------------------------------------------------
    // POSITIVE — TC2: Today's Live Feed
    // -------------------------------------------------------------------------

    @Test(groups = {"Positive"}, description = "Home TC2 - Today's Live Feed filters")
    public void testPositive_home_todaysLiveFeed() {
        HomePage homePage = new HomePage(getDriver());
        LiveFeedPage liveFeedPage = new LiveFeedPage(getDriver());

        homePage.openAuthenticated();
        Assert.assertTrue(homePage.isWelcomeMessageDisplayed(), "Welcome message should show");
        Assert.assertTrue(homePage.isLiveFeedSectionDisplayed(), "Today's Live Feed section should show");

        homePage.clickLiveFeedViewAll();
        liveFeedPage.waitForPageReady();
        Assert.assertTrue(liveFeedPage.isOnLiveFeedPage(), "URL should be Live Feed page");
        Assert.assertTrue(liveFeedPage.isLiveFeedPageLoaded(), "Live Feed heading should show");
        Assert.assertTrue(liveFeedPage.isHeaderTitleDisplayed(), "Header should say Live Feed");
        Assert.assertTrue(liveFeedPage.isTodaySectionDisplayed(), "Today section should show");

        liveFeedPage.selectDateRangeByVisibleText(ConfigReader.get("cora.livefeed.daterange.today"));
        liveFeedPage.selectDateRangeByVisibleText(ConfigReader.get("cora.livefeed.daterange.yesterday"));
        liveFeedPage.selectDateRangeByVisibleText(ConfigReader.get("cora.livefeed.daterange.last7days"));
        liveFeedPage.selectDateRangeByVisibleText(ConfigReader.get("cora.livefeed.daterange.allrecent"));

        liveFeedPage.selectActivityTypeByVisibleText(ConfigReader.get("cora.livefeed.activity.all"));
        liveFeedPage.selectActivityTypeByVisibleText(ConfigReader.get("cora.livefeed.activity.calls"));
        liveFeedPage.selectActivityTypeByVisibleText(ConfigReader.get("cora.livefeed.activity.inquiries"));
        liveFeedPage.selectActivityTypeByVisibleText(ConfigReader.get("cora.livefeed.activity.followups"));
        liveFeedPage.selectActivityTypeByVisibleText(ConfigReader.get("cora.livefeed.activity.appointments"));

        liveFeedPage.clickExactDateInput();
        liveFeedPage.setExactDate(ConfigReader.get("cora.livefeed.exact.date"));
        liveFeedPage.clearExactDate();

        liveFeedPage.selectDateRangeByVisibleText(ConfigReader.get("cora.livefeed.daterange.today"));
        liveFeedPage.selectActivityTypeByVisibleText(ConfigReader.get("cora.livefeed.activity.all"));
        String activitySummary = liveFeedPage.getActivitySummaryLabel().toLowerCase();
        Assert.assertTrue(
                liveFeedPage.isAppointmentScheduledActivityDisplayed() || activitySummary.contains("activity"),
                "Live feed should list at least one activity for Today");

        liveFeedPage.clickBack();
        Assert.assertTrue(homePage.isOnHomePage(), "Back should return to Home");
    }

    // -------------------------------------------------------------------------
    // POSITIVE — TC3: Appointments — Add & Block Time
    // -------------------------------------------------------------------------

    @Test(groups = {"Positive"}, description = "Home TC3 - Appointments add and block time")
    public void testPositive_home_appointmentsManagement() {
        HomePage homePage = new HomePage(getDriver());
        AppointmentsPage appointmentsPage = new AppointmentsPage(getDriver());

        homePage.openAuthenticated();
        Assert.assertTrue(homePage.isWelcomeMessageDisplayed(), "Welcome message should show");
        Assert.assertTrue(homePage.isAppointmentViewsSectionDisplayed(), "Appointment Views section should show");

        homePage.clickAppointmentViewsViewAll();
        appointmentsPage.waitForPageReady();
        Assert.assertTrue(appointmentsPage.isOnAppointmentsPage(), "URL should be Appointments page");
        Assert.assertTrue(appointmentsPage.isAppointmentsPageLoaded(), "Appointments heading should show");
        Assert.assertTrue(appointmentsPage.isHeaderTitleDisplayed(), "Header should say Appointments");
        Assert.assertTrue(appointmentsPage.isCalendarMonthDisplayed(), "Calendar month should show");
        Assert.assertTrue(appointmentsPage.isThisMonthSectionDisplayed(), "This month sidebar should show");

        appointmentsPage.clickAllTab();
        appointmentsPage.clickUpcomingTab();
        appointmentsPage.clickPastTab();
        appointmentsPage.clickCancelledTab();

        appointmentsPage.clickAddAppointment();
        appointmentsPage.enterClientName(RandomDataUtils.clientName());
        appointmentsPage.enterPhoneNumber(RandomDataUtils.phoneNumber());
        appointmentsPage.enterNotes(RandomDataUtils.notes());
        appointmentsPage.clickSaveAppointment();

        appointmentsPage.waitForToast(ConfigReader.get("cora.appointments.toast.added"));
        Assert.assertTrue(appointmentsPage.isToastDisplayed(
                ConfigReader.get("cora.appointments.toast.added")), "Add appointment toast should show");
        appointmentsPage.waitForAddAppointmentModalClosed();

        appointmentsPage.clickBlockTime();
        appointmentsPage.clickBlockTimeConfirm();

        appointmentsPage.waitForToast(ConfigReader.get("cora.appointments.toast.blocked"));
        Assert.assertTrue(appointmentsPage.isToastDisplayed(
                ConfigReader.get("cora.appointments.toast.blocked")), "Block time toast should show");
        Assert.assertTrue(appointmentsPage.isSidebarEntryDisplayed(
                ConfigReader.get("cora.appointments.sidebar.blocked")), "Blocked time should show in sidebar");

        appointmentsPage.clickBack();
        Assert.assertTrue(homePage.isOnHomePage(), "Back should return to Home");
    }

    // -------------------------------------------------------------------------
    // NEGATIVE — Unauthenticated access to protected pages (4 parallel rows)
    // -------------------------------------------------------------------------

    @DataProvider(name = "homeUnauthenticatedData", parallel = true)
    public Object[][] homeUnauthenticatedData() {
        return ConfigReader.getIndexedDataSet(
                "cora.home.negative.unauth",
                "scenario",
                "path",
                "expected"
        );
    }

    @Test(groups = {"Negative"},
            dataProvider = "homeUnauthenticatedData",
            description = "Home | Negative | Protected pages redirect to login without session")
    public void testNegative_home_unauthenticatedRedirect(String scenario, String path, String expectedLoginPath) {
        String targetUrl = ConfigReader.get("base.url") + path;
        getDriver().get(targetUrl);

        getUtils().waitForUrlContains(expectedLoginPath);

        LoginPage loginPage = new LoginPage(getDriver());
        Assert.assertTrue(loginPage.isOnLoginPage(),
                "Should redirect to login for scenario: " + scenario);
        Assert.assertTrue(loginPage.isWelcomeHeadingDisplayed(),
                "Login page should load for scenario: " + scenario);
    }
}
