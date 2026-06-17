package com.cora.tests;

import com.cora.base.BaseTest;
import com.cora.config.ConfigReader;
import com.cora.pages.LoginPage;
import com.cora.reporting.ExtentTestManager;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Login page — 1 positive + 4 negative scenarios.
 * With parallel="methods" and @DataProvider(parallel=true), TestNG opens up to 5 Chrome browsers at once.
 * Each scenario is fully independent: open → act → assert → quit (via @AfterMethod).
 */
public class LoginTest extends BaseTest {

    private static final String ASSERT_ERROR_MESSAGE = "ERROR_MESSAGE";
    private static final String ASSERT_SIGN_IN_DISABLED = "SIGN_IN_DISABLED";

    // -------------------------------------------------------------------------
    // POSITIVE — independent @Test (1 Chrome instance)
    // -------------------------------------------------------------------------

    @Test(groups = {"Positive"}, description = "Login | Positive | Valid credentials redirect to home")
    public void testPositiveLogin_validCredentials_redirectsToHome() {
        LoginPage loginPage = new LoginPage(getDriver());

        ExtentTestManager.getTest().info("Navigating to login page independently");
        loginPage.open();

        Assert.assertTrue(loginPage.isWelcomeHeadingDisplayed(),
                "Welcome heading should be visible before login");

        String username = ConfigReader.get("cora.login.valid.username");
        String password = ConfigReader.get("cora.login.valid.password");
        ExtentTestManager.getTest().info("Entering valid credentials for: " + username);

        loginPage.login(username, password);

        String expectedPath = ConfigReader.get("cora.login.expected.success.path");
        ExtentTestManager.getTest().info("Waiting for redirect to path: " + expectedPath);
        getUtils().waitForUrlContains(expectedPath);

        String currentUrl = getDriver().getCurrentUrl();
        ExtentTestManager.getTest().info("Landed on URL: " + currentUrl);

        Assert.assertTrue(currentUrl.contains(expectedPath),
                "User should be redirected to home page after successful login");
    }

    // -------------------------------------------------------------------------
    // NEGATIVE — single @Test + parallel DataProvider (4 Chrome instances)
    // -------------------------------------------------------------------------

    @DataProvider(name = "loginNegativeData", parallel = true)
    public Object[][] loginNegativeData() {
        return ConfigReader.getIndexedDataSet(
                "cora.login.negative",
                "scenario",
                "username",
                "password",
                "assertion.type",
                "expected"
        );
    }

    @Test(groups = {"Negative"},
            dataProvider = "loginNegativeData",
            description = "Login | Negative | Invalid / blank field scenarios")
    public void testNegativeLogin_scenarios(String scenario, String username, String password,
                                             String assertionType, String expected) {
        LoginPage loginPage = new LoginPage(getDriver());

        ExtentTestManager.getTest().info("Scenario: " + scenario);
        ExtentTestManager.getTest().info("Navigating to login page independently");
        loginPage.open();

        Assert.assertTrue(loginPage.isWelcomeHeadingDisplayed(),
                "Welcome heading should be visible on login page");

        if (username != null && !username.isBlank()) {
            ExtentTestManager.getTest().info("Entering email: " + username);
            loginPage.enterEmail(username);
        } else {
            ExtentTestManager.getTest().info("Leaving email field blank");
        }

        if (password != null && !password.isBlank()) {
            ExtentTestManager.getTest().info("Entering password");
            loginPage.enterPassword(password);
        } else {
            ExtentTestManager.getTest().info("Leaving password field blank");
        }

        if (ASSERT_ERROR_MESSAGE.equals(assertionType)) {
            ExtentTestManager.getTest().info("Submitting login form");
            loginPage.clickSignIn();

            ExtentTestManager.getTest().info("Validating error message: " + expected);
            Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                    "Error message should be displayed for scenario: " + scenario);
            Assert.assertEquals(loginPage.getErrorMessage(), expected,
                    "Unexpected error message for scenario: " + scenario);
            Assert.assertTrue(loginPage.isOnLoginPage(),
                    "User should remain on login page for scenario: " + scenario);
            return;
        }

        if (ASSERT_SIGN_IN_DISABLED.equals(assertionType)) {
            ExtentTestManager.getTest().info("Validating Sign In button is disabled");
            Assert.assertFalse(loginPage.isSignInButtonEnabled(),
                    "Sign In should be disabled for scenario: " + scenario);
            Assert.assertTrue(loginPage.isOnLoginPage(),
                    "User should remain on login page for scenario: " + scenario);
            return;
        }

        Assert.fail("Unknown assertion type in config: " + assertionType);
    }
}
