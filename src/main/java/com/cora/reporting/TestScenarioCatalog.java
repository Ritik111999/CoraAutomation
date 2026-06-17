package com.cora.reporting;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Catalogue of QA scenarios with module grouping, IDs, steps, and expected results.
 */
public final class TestScenarioCatalog {

    private static final Map<String, TestScenarioDefinition> BY_KEY = new LinkedHashMap<>();

    static {
        registerLoginScenarios();
        registerHomeScenarios();
        registerPropertiesScenarios();
        registerDraftScenarios();
        registerPublishedScenarios();
        registerPropertiesNegativeScenarios();
    }

    private TestScenarioCatalog() {
    }

    public static TestScenarioDefinition lookup(String lookupKey) {
        return BY_KEY.get(lookupKey);
    }

    private static void register(
            String lookupKey,
            String moduleCode,
            String moduleName,
            String scenarioId,
            String title,
            String type,
            String preconditions,
            List<String> steps,
            String expectedResult) {
        BY_KEY.put(lookupKey, new TestScenarioDefinition(
                moduleCode, moduleName, scenarioId, title, type,
                preconditions, steps, expectedResult));
    }

    private static List<String> steps(String... lines) {
        return List.of(lines);
    }

    // -------------------------------------------------------------------------
    // 01 - Login
    // -------------------------------------------------------------------------

    private static void registerLoginScenarios() {
        String moduleCode = "01";
        String moduleName = "01 - Login";

        register("LoginTest#testPositiveLogin_validCredentials_redirectsToHome",
                moduleCode, moduleName, "LOGIN-TC-01",
                "Valid credentials redirect to home", "Positive",
                "Registered user with valid credentials",
                steps(
                        "Open login page",
                        "Enter valid email and password",
                        "Click Sign In",
                        "Verify redirect to home dashboard"
                ),
                "User is authenticated and lands on /home");

        register("LoginTest#testNegativeLogin_scenarios#INVALID_CREDENTIALS",
                moduleCode, moduleName, "LOGIN-TC-02",
                "Invalid credentials show error message", "Negative",
                "On Sign-In page",
                steps(
                        "Enter wrong email and password",
                        "Click Sign In",
                        "Verify error message"
                ),
                "Error message displayed; user remains on login page");

        register("LoginTest#testNegativeLogin_scenarios#BLANK_EMAIL",
                moduleCode, moduleName, "LOGIN-TC-03",
                "Login with empty email only", "Negative",
                "On Sign-In page",
                steps(
                        "Leave email empty",
                        "Enter valid password",
                        "Verify Sign In button state"
                ),
                "Sign In disabled; user stays on login page");

        register("LoginTest#testNegativeLogin_scenarios#BLANK_PASSWORD",
                moduleCode, moduleName, "LOGIN-TC-04",
                "Login with empty password only", "Negative",
                "On Sign-In page",
                steps(
                        "Enter valid email",
                        "Leave password empty",
                        "Verify Sign In button state"
                ),
                "Sign In disabled; user stays on login page");

        register("LoginTest#testNegativeLogin_scenarios#BOTH_FIELDS_BLANK",
                moduleCode, moduleName, "LOGIN-TC-05",
                "Login with empty credentials", "Negative",
                "On Sign-In page",
                steps(
                        "Leave email and password empty",
                        "Verify Sign In button state"
                ),
                "Sign In disabled; user stays on login page");
    }

    // -------------------------------------------------------------------------
    // 02 - Home
    // -------------------------------------------------------------------------

    private static void registerHomeScenarios() {
        String moduleCode = "02";
        String moduleName = "02 - Home";

        register("HomeTest#testPositive_home_coraQueueFollowUps",
                moduleCode, moduleName, "HOME-TC-01",
                "My Cora Queue navigates to Follow-ups", "Positive",
                "User is logged in on Home",
                steps(
                        "Open authenticated Home",
                        "Click View All on My Cora Queue",
                        "Exercise Follow-ups tabs and items",
                        "Navigate back to Home"
                ),
                "Follow-ups page loads; tabs work; back returns to Home");

        register("HomeTest#testPositive_home_todaysLiveFeed",
                moduleCode, moduleName, "HOME-TC-02",
                "Today's Live Feed filters and date range", "Positive",
                "User is logged in on Home",
                steps(
                        "Open authenticated Home",
                        "Click View All on Live Feed",
                        "Apply date range and activity filters"
                ),
                "Live Feed page loads with working filters");

        register("HomeTest#testPositive_home_appointmentsManagement",
                moduleCode, moduleName, "HOME-TC-03",
                "Appointments add appointment and block time", "Positive",
                "User is logged in on Home",
                steps(
                        "Open authenticated Home",
                        "Click View All on Appointment Views",
                        "Add appointment and block time",
                        "Verify toasts and sidebar entries"
                ),
                "Appointments page loads; add/block flows succeed");

        register("HomeTest#testNegative_home_unauthenticatedRedirect#HOME_WITHOUT_LOGIN",
                moduleCode, moduleName, "HOME-TC-04",
                "Home page blocked without login", "Negative",
                "No active session",
                steps("Navigate to /home without login", "Verify redirect"),
                "User is redirected to login page");

        register("HomeTest#testNegative_home_unauthenticatedRedirect#FOLLOWUPS_WITHOUT_LOGIN",
                moduleCode, moduleName, "HOME-TC-05",
                "Follow-ups blocked without login", "Negative",
                "No active session",
                steps("Navigate to /follow-ups without login", "Verify redirect"),
                "User is redirected to login page");

        register("HomeTest#testNegative_home_unauthenticatedRedirect#LIVEFEED_WITHOUT_LOGIN",
                moduleCode, moduleName, "HOME-TC-06",
                "Live Feed blocked without login", "Negative",
                "No active session",
                steps("Navigate to /activities without login", "Verify redirect"),
                "User is redirected to login page");

        register("HomeTest#testNegative_home_unauthenticatedRedirect#APPOINTMENTS_WITHOUT_LOGIN",
                moduleCode, moduleName, "HOME-TC-07",
                "Appointments blocked without login", "Negative",
                "No active session",
                steps("Navigate to /appointments without login", "Verify redirect"),
                "User is redirected to login page");
    }

    // -------------------------------------------------------------------------
    // 03 - Properties
    // -------------------------------------------------------------------------

    private static void registerPropertiesScenarios() {
        String moduleCode = "03";
        String moduleName = "03 - Properties";

        register("PropertiesTest#testPositive_properties_openAddPropertyForm",
                moduleCode, moduleName, "PROP-TC-01",
                "Open Add Property form from Properties list", "Positive",
                "User is logged in",
                steps(
                        "Open Properties from sidebar",
                        "Click Add Property",
                        "Verify wizard step 1"
                ),
                "Add Property wizard opens on step 1 with address fields");

        register("PropertiesTest#testPositive_properties_addPropertyWizardSteps1To6",
                moduleCode, moduleName, "PROP-TC-02",
                "Complete Add Property wizard steps 1 to 6", "Positive",
                "User is logged in on Properties",
                steps(
                        "Open Add Property wizard",
                        "Complete steps 1 through 6 with photos",
                        "Save draft or publish per config"
                ),
                "Review section visible; draft saved or listing published");

        register("PropertiesTest#testPositive_properties_navigateBackViaStepDots",
                moduleCode, moduleName, "PROP-TC-03",
                "Navigate back in wizard using step dots", "Positive",
                "User is on Add Property wizard",
                steps(
                        "Advance to step 4",
                        "Click step 2 dot",
                        "Click step 1 dot"
                ),
                "Wizard navigates backward via step indicators");

        register("PropertiesTest#testNegative_properties_publishWithoutDescription",
                moduleCode, moduleName, "PROP-TC-04",
                "Publish blocked without property description", "Negative",
                "User on wizard step 6 without description",
                steps(
                        "Complete wizard through step 6",
                        "Click Publish Listing without description"
                ),
                "Validation error shown; user remains on step 6");

        register("PropertiesTest#testNegative_properties_unauthenticatedRedirect#PROPERTIES_WITHOUT_LOGIN",
                moduleCode, moduleName, "PROP-TC-05",
                "Properties list blocked without login", "Negative",
                "No active session",
                steps("Navigate to /properties without login", "Verify redirect"),
                "User is redirected to login page");

        register("PropertiesTest#testNegative_properties_unauthenticatedRedirect#ADD_LISTING_WITHOUT_LOGIN",
                moduleCode, moduleName, "PROP-TC-06",
                "Add listing blocked without login", "Negative",
                "No active session",
                steps("Navigate to /add-listing without login", "Verify redirect"),
                "User is redirected to login page");
    }

    // -------------------------------------------------------------------------
    // 04 - Draft Lifecycle
    // -------------------------------------------------------------------------

    private static void registerDraftScenarios() {
        String moduleCode = "04";
        String moduleName = "04 - Draft Lifecycle";

        register("PropertiesDraftLifecycleTest#testPositive_draft_createPropertyDraft",
                moduleCode, moduleName, "DRAFT-TC-01",
                "Create property draft with photos", "Positive",
                "User is logged in on Properties",
                steps(
                        "Open Add Property wizard",
                        "Fill step 1 and upload photos",
                        "Save Draft",
                        "Verify draft on Draft Properties page"
                ),
                "New draft appears on Draft Properties list");

        register("PropertiesDraftLifecycleTest#testPositive_draft_editPropertyDraft",
                moduleCode, moduleName, "DRAFT-TC-02",
                "Edit saved draft from Draft Properties", "Positive",
                "Draft created in prior step",
                steps(
                        "Open Draft Properties",
                        "Open draft for edit",
                        "Verify pre-filled address"
                ),
                "Edit wizard opens with saved draft data");

        register("PropertiesDraftLifecycleTest#testPositive_draft_deletePropertyDraft",
                moduleCode, moduleName, "DRAFT-TC-03",
                "Delete draft from Draft Properties", "Positive",
                "Draft exists on Draft Properties",
                steps(
                        "Open Draft Properties",
                        "Delete draft by address",
                        "Verify draft removed"
                ),
                "Draft card count decreases; draft no longer listed");

        register("PropertiesDraftLifecycleTest#testPositive_draft_lifecycleByCountryAndType#United States",
                moduleCode, moduleName, "DRAFT-TC-04",
                "Draft lifecycle for United States / Single Family Home", "Positive",
                "User is logged in",
                steps("Create draft for US", "Edit and verify fields", "Delete draft"),
                "Draft saves, edits, and deletes for US location");

        register("PropertiesDraftLifecycleTest#testPositive_draft_lifecycleByCountryAndType#Canada",
                moduleCode, moduleName, "DRAFT-TC-05",
                "Draft lifecycle for Canada / Condo", "Positive",
                "User is logged in",
                steps("Create draft for Canada", "Edit and verify fields", "Delete draft"),
                "Draft saves, edits, and deletes for Canada location");

        register("PropertiesDraftLifecycleTest#testPositive_draft_lifecycleByCountryAndType#United Kingdom",
                moduleCode, moduleName, "DRAFT-TC-06",
                "Draft lifecycle for United Kingdom / Townhouse", "Positive",
                "User is logged in",
                steps("Create draft for UK", "Edit and verify fields", "Delete draft"),
                "Draft saves, edits, and deletes for UK location");

        register("PropertiesDraftLifecycleTest#testPositive_draft_lifecycleByCountryAndType#Australia",
                moduleCode, moduleName, "DRAFT-TC-07",
                "Draft lifecycle for Australia / Single Family Home", "Positive",
                "User is logged in",
                steps("Create draft for Australia", "Edit and verify fields", "Delete draft"),
                "Draft saves, edits, and deletes for Australia location");

        register("PropertiesDraftLifecycleTest#testPositive_draft_lifecycleByCountryAndType#India",
                moduleCode, moduleName, "DRAFT-TC-08",
                "Draft lifecycle for India / Condo", "Positive",
                "User is logged in",
                steps("Create draft for India", "Edit and verify fields", "Delete draft"),
                "Draft saves, edits, and deletes for India location");
    }

    // -------------------------------------------------------------------------
    // 05 - Published Lifecycle
    // -------------------------------------------------------------------------

    private static void registerPublishedScenarios() {
        String moduleCode = "05";
        String moduleName = "05 - Published Lifecycle";

        register("PropertiesPublishedLifecycleTest#testPositive_published_createAndPublishProperty",
                moduleCode, moduleName, "PUB-TC-01",
                "Create and publish property from wizard", "Positive",
                "User is logged in on Properties",
                steps(
                        "Complete wizard and publish listing",
                        "Verify property on Properties list"
                ),
                "Published property card appears on Properties list");

        register("PropertiesPublishedLifecycleTest#testPositive_published_editPropertyFromList",
                moduleCode, moduleName, "PUB-TC-02",
                "Edit published property via options menu", "Positive",
                "Published property exists on list",
                steps(
                        "Open property options menu",
                        "Click Edit",
                        "Verify address pre-filled"
                ),
                "Edit wizard opens with published property data");

        register("PropertiesPublishedLifecycleTest#testPositive_published_deletePropertyFromList",
                moduleCode, moduleName, "PUB-TC-03",
                "Delete published property via options menu", "Positive",
                "Published property exists on list",
                steps(
                        "Open property options menu",
                        "Click Delete",
                        "Verify property removed"
                ),
                "Property no longer appears on Properties list");

        register("PropertiesPublishedLifecycleTest#testPositive_published_listingStatusBadges#ACTIVE",
                moduleCode, moduleName, "PUB-TC-04",
                "Publish listing with ACTIVE status badge", "Positive",
                "User is logged in",
                steps("Publish with ACTIVE status", "Verify badge", "Delete property"),
                "ACTIVE badge displayed on property card");

        register("PropertiesPublishedLifecycleTest#testPositive_published_listingStatusBadges#PENDING",
                moduleCode, moduleName, "PUB-TC-05",
                "Publish listing with PENDING status badge", "Positive",
                "User is logged in",
                steps("Publish with PENDING status", "Verify badge", "Delete property"),
                "PENDING badge displayed on property card");

        register("PropertiesPublishedLifecycleTest#testPositive_published_listingStatusBadges#OFF-MARKET",
                moduleCode, moduleName, "PUB-TC-06",
                "Publish listing with OFF-MARKET status badge", "Positive",
                "User is logged in",
                steps("Publish with OFF-MARKET status", "Verify badge", "Delete property"),
                "OFF-MARKET badge displayed on property card");

        register("PropertiesPublishedLifecycleTest#testPositive_published_listingStatusBadges#COMING-SOON",
                moduleCode, moduleName, "PUB-TC-07",
                "Publish listing with COMING-SOON status badge", "Positive",
                "User is logged in",
                steps("Publish with COMING-SOON status", "Verify badge", "Delete property"),
                "COMING-SOON badge displayed on property card");
    }

    // -------------------------------------------------------------------------
    // 06 - Properties Validation
    // -------------------------------------------------------------------------

    private static void registerPropertiesNegativeScenarios() {
        String moduleCode = "06";
        String moduleName = "06 - Properties Validation";

        register("PropertiesNegativeCountriesAndStatusTest#testNegative_draft_emptyRequiredFieldsShouldBlockProceeding#United States",
                moduleCode, moduleName, "PROPNEG-TC-01",
                "Empty street address blocks wizard on US form", "Negative",
                "User on Add Property step 1",
                steps("Leave street address empty", "Click Next"),
                "Wizard remains on step 1");

        register("PropertiesNegativeCountriesAndStatusTest#testNegative_draft_emptyRequiredFieldsShouldBlockProceeding#Canada",
                moduleCode, moduleName, "PROPNEG-TC-02",
                "Empty street address blocks wizard on Canada form", "Negative",
                "User on Add Property step 1",
                steps("Leave street address empty for Canada", "Click Next"),
                "Wizard remains on step 1");

        register("PropertiesNegativeCountriesAndStatusTest#testNegative_draft_countryStateMismatchResetsState",
                moduleCode, moduleName, "PROPNEG-TC-03",
                "Changing country resets state selection", "Negative",
                "User on Add Property step 1 with US + Florida",
                steps("Select United States and Florida", "Change country to Canada", "Verify state"),
                "State no longer remains Florida after country change");

        register("PropertiesNegativeCountriesAndStatusTest#testNegative_published_invalidListingStatus#SOLD",
                moduleCode, moduleName, "PROPNEG-TC-04",
                "SOLD status does not show ACTIVE badge", "Negative",
                "User completes publish wizard with SOLD status",
                steps("Publish with SOLD status", "Verify badge on card"),
                "ACTIVE badge is not displayed for SOLD listing");

        register("PropertiesNegativeCountriesAndStatusTest#testNegative_published_invalidListingStatus#UNKNOWN",
                moduleCode, moduleName, "PROPNEG-TC-05",
                "UNKNOWN listing status is not selectable", "Negative",
                "User on wizard step 5",
                steps("Attempt to select UNKNOWN status"),
                "Unsupported status cannot be selected; test handles safely");
    }
}
