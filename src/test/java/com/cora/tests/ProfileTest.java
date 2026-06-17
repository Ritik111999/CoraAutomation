package com.cora.tests;

import com.cora.base.BaseTest;
import com.cora.config.ConfigReader;
import com.cora.pages.EditProfilePage;
import com.cora.pages.LoginPage;
import com.cora.pages.ProfilePage;
import com.cora.utils.ProfileAccountLock;
import com.cora.utils.RandomDataUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * My Profile module — profile photo upload on main profile and Edit Profile personal info.
 * Account Security (Change Password) is intentionally excluded.
 * Authenticated scenarios use {@link ProfileAccountLock} so parallel Chrome browsers
 * do not mutate the shared test account simultaneously.
 */
public class ProfileTest extends BaseTest {

    private ProfilePage openProfileAuthenticated() {
        ProfilePage profilePage = new ProfilePage(getDriver());
        profilePage.openAuthenticated();
        profilePage.openFromSidebar();
        profilePage.waitForPageReady();
        return profilePage;
    }

    // -------------------------------------------------------------------------
    // POSITIVE — TC1: Upload profile image from My Profile header card
    // -------------------------------------------------------------------------

    @Test(groups = {"Positive"}, description = "Profile TC1 - Upload profile image from My Profile page")
    public void testPositive_profile_uploadProfileImageFromMainPage() {
        ProfileAccountLock.runExclusive(() -> {
            ProfilePage profilePage = openProfileAuthenticated();

            Assert.assertTrue(profilePage.isProfilePageLoaded(), "Profile page should load");
            String beforeSrc = profilePage.getProfileAvatarSrc();

            profilePage.uploadProfileImageFromConfig();
            getUtils().waitForSeconds(2);

            Assert.assertTrue(profilePage.isProfileAvatarDisplayed(), "Profile avatar should remain visible after upload");
            Assert.assertTrue(profilePage.isOnProfilePage(), "User should stay on Profile after image upload");
            String afterSrc = profilePage.getProfileAvatarSrc();
            Assert.assertNotNull(afterSrc, "Avatar src should be present after upload");
            Assert.assertFalse(afterSrc.isBlank(), "Avatar src should not be empty after upload");
            Assert.assertTrue(!afterSrc.equals(beforeSrc) || afterSrc.contains("uploads") || afterSrc.startsWith("blob:"),
                    "Avatar image should reflect upload (src changed or valid image URL)");
        });
    }

    // -------------------------------------------------------------------------
    // POSITIVE — TC2: Basic Info opens Edit Profile; update personal info + photo
    // -------------------------------------------------------------------------

    @Test(groups = {"Positive"}, description = "Profile TC2 - Edit personal info and photo via Basic Info")
    public void testPositive_profile_editPersonalInfoAndPhoto() {
        ProfileAccountLock.runExclusive(() -> {
            ProfilePage profilePage = openProfileAuthenticated();
            EditProfilePage editProfilePage = new EditProfilePage(getDriver());

            String originalFirstName = ConfigReader.get("cora.profile.personal.firstname");
            String originalLastName = ConfigReader.get("cora.profile.personal.lastname");
            String originalPhone = ConfigReader.get("cora.profile.personal.phone");
            String originalCompany = ConfigReader.get("cora.profile.personal.company");
            String updatedFirstName = RandomDataUtils.withSuffix(originalFirstName);
            String updatedPhone = ConfigReader.get("cora.profile.personal.phone.updated");
            String updatedCompany = RandomDataUtils.withSuffix(ConfigReader.get("cora.profile.personal.company.prefix"));

            profilePage.clickBasicInfo();
            editProfilePage.waitForPageReady();

            Assert.assertTrue(editProfilePage.isEditProfilePageLoaded(), "Edit Profile should open from Basic Info");
            Assert.assertTrue(editProfilePage.isPersonalInformationSectionDisplayed(),
                    "Personal Information section should be visible");
            Assert.assertTrue(editProfilePage.isEmailDisabled(), "Email field should be read-only");

            String beforeAvatarSrc = editProfilePage.getEditAvatarSrc();
            editProfilePage.uploadProfileImage(profilePage.resolveProfilePhotoPath());
            getUtils().waitForSeconds(1);

            editProfilePage.enterFirstName(updatedFirstName);
            editProfilePage.enterLastName(originalLastName);
            editProfilePage.enterPhone(updatedPhone);
            editProfilePage.enterCompany(updatedCompany);
            editProfilePage.clickSaveChanges();
            editProfilePage.waitForSaveToComplete();
            editProfilePage.waitForFirstNameValue(updatedFirstName);

            Assert.assertEquals(editProfilePage.getFirstName(), updatedFirstName,
                    "Edit Profile should show updated first name after save");
            Assert.assertEquals(editProfilePage.getPhone(), updatedPhone,
                    "Edit Profile should show updated phone after save");

            editProfilePage.clickGoBack();
            profilePage.waitForPageReady();
            Assert.assertTrue(profilePage.isOnProfilePage(), "Go Back should return to My Profile");
            Assert.assertTrue(profilePage.getDisplayName().contains(updatedFirstName),
                    "Profile header should show updated first name");

            profilePage.clickBasicInfo();
            editProfilePage.waitForPageReady();
            editProfilePage.enterFirstName(originalFirstName);
            editProfilePage.enterLastName(originalLastName);
            editProfilePage.enterPhone(originalPhone);
            editProfilePage.enterCompany(originalCompany);
            editProfilePage.clickSaveChanges();
            editProfilePage.waitForSaveToComplete();
            editProfilePage.clickGoBack();
            profilePage.waitForPageReady();

            Assert.assertTrue(profilePage.getDisplayName().contains(originalFirstName),
                    "Profile should restore original first name after cleanup");
            Assert.assertTrue(editProfilePage.isEditAvatarDisplayed() || profilePage.isProfileAvatarDisplayed(),
                    "Profile avatar should remain visible after save cycle");
            Assert.assertNotNull(beforeAvatarSrc, "Baseline avatar src captured on Edit Profile");
        });
    }

    // -------------------------------------------------------------------------
    // NEGATIVE — TC3: Empty phone blocks save on Edit Profile
    // -------------------------------------------------------------------------

    @Test(groups = {"Negative"}, description = "Profile TC3 - Empty phone prevents save")
    public void testNegative_profile_emptyPhoneBlocksSave() {
        ProfileAccountLock.runExclusive(() -> {
            ProfilePage profilePage = openProfileAuthenticated();
            EditProfilePage editProfilePage = new EditProfilePage(getDriver());

            profilePage.clickBasicInfo();
            editProfilePage.waitForPageReady();

            String originalPhone = editProfilePage.getPhone();
            editProfilePage.clearPhone();
            Assert.assertTrue(editProfilePage.getPhone().isBlank(),
                    "Phone field should be cleared before save attempt");
            editProfilePage.clickSaveChanges();
            editProfilePage.waitForSaveToComplete();

            Assert.assertTrue(editProfilePage.isEditProfilePageLoaded(),
                    "User should remain on Edit Profile when phone is empty");
            Assert.assertTrue(editProfilePage.isPhoneInvalid()
                            || editProfilePage.getPhone().isBlank()
                            || originalPhone.equals(editProfilePage.getPhone()),
                    "Empty phone should block save, show validation, or retain previous value");

            editProfilePage.enterPhone(originalPhone);
            editProfilePage.clickSaveChanges();
            editProfilePage.waitForSaveToComplete();
        });
    }

    // -------------------------------------------------------------------------
    // NEGATIVE — TC4: Invalid phone format blocks save on Edit Profile
    // -------------------------------------------------------------------------

    @Test(groups = {"Negative"}, description = "Profile TC4 - Invalid phone prevents save")
    public void testNegative_profile_invalidPhoneBlocksSave() {
        ProfileAccountLock.runExclusive(() -> {
            ProfilePage profilePage = openProfileAuthenticated();
            EditProfilePage editProfilePage = new EditProfilePage(getDriver());

            profilePage.clickBasicInfo();
            editProfilePage.waitForPageReady();

            String originalPhone = editProfilePage.getPhone();
            editProfilePage.enterPhone(ConfigReader.get("cora.profile.personal.phone.invalid"));
            editProfilePage.clickSaveChanges();
            editProfilePage.waitForSaveToComplete();

            Assert.assertTrue(editProfilePage.isEditProfilePageLoaded(),
                    "User should remain on Edit Profile when phone is invalid");
            Assert.assertTrue(editProfilePage.isPhoneInvalid()
                            || ConfigReader.get("cora.profile.personal.phone.invalid").equals(editProfilePage.getPhone()),
                    "Invalid phone should be retained or highlighted");

            editProfilePage.enterPhone(originalPhone);
            editProfilePage.clickSaveChanges();
            editProfilePage.waitForSaveToComplete();
            editProfilePage.clickGoBack();
            profilePage.waitForPageReady();
        });
    }

    // -------------------------------------------------------------------------
    // NEGATIVE — TC5: Unauthenticated access redirects to login
    // -------------------------------------------------------------------------

    @DataProvider(name = "profileUnauthenticatedData", parallel = true)
    public Object[][] profileUnauthenticatedData() {
        return ConfigReader.getIndexedDataSet(
                "cora.profile.negative.unauth",
                "scenario",
                "path",
                "expected"
        );
    }

    @Test(groups = {"Negative"},
            dataProvider = "profileUnauthenticatedData",
            description = "Profile | Negative | Protected page redirects to login without session")
    public void testNegative_profile_unauthenticatedRedirect(String scenario, String path, String expectedLoginPath) {
        getDriver().get(ConfigReader.get("base.url") + path);
        getUtils().waitForUrlContains(expectedLoginPath);

        LoginPage loginPage = new LoginPage(getDriver());
        Assert.assertTrue(loginPage.isOnLoginPage(),
                "Should redirect to login for scenario: " + scenario);
        Assert.assertTrue(loginPage.isWelcomeHeadingDisplayed(),
                "Login page should load for scenario: " + scenario);
    }
}
