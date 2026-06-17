package com.cora.reporting;

import org.testng.ITestResult;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Maps TestNG metadata to Extent assignCategory tags:
 * - Positive_Scenario / Negative_Scenario
 * - Functional area (Login, Home, Properties, etc.)
 */
public final class ScenarioCategoryMapper {

    private ScenarioCategoryMapper() {
    }

    public static List<String> resolveCategories(ITestResult result, TestScenarioDefinition scenario) {
        Set<String> categories = new LinkedHashSet<>();

        categories.add(resolveScenarioType(result));
        if (scenario != null) {
            categories.add(scenario.categoryTag());
        } else {
            categories.add(resolveFunctionalArea(result));
        }

        return new ArrayList<>(categories);
    }

    private static String resolveScenarioType(ITestResult result) {
        String[] groups = result.getMethod().getGroups();
        if (groups != null) {
            for (String group : groups) {
                if ("Positive".equalsIgnoreCase(group)) {
                    return "Positive_Scenario";
                }
                if ("Negative".equalsIgnoreCase(group)) {
                    return "Negative_Scenario";
                }
            }
        }
        return "Uncategorized_Scenario";
    }

    private static String resolveFunctionalArea(ITestResult result) {
        String className = result.getTestClass().getRealClass().getSimpleName();

        return switch (className) {
            case "LoginTest" -> "Login";
            case "HomeTest" -> "Home";
            case "PropertiesTest" -> "Properties";
            case "PropertiesDraftLifecycleTest" -> "Properties_Draft";
            case "PropertiesPublishedLifecycleTest" -> "Properties_Published";
            case "PropertiesNegativeCountriesAndStatusTest" -> "Properties_Negative";
            default -> toTitleToken(className.replace("Test", ""));
        };
    }

    private static String toTitleToken(String raw) {
        if (raw == null || raw.isBlank()) {
            return "General";
        }
        return raw.substring(0, 1).toUpperCase(Locale.US) + raw.substring(1);
    }
}
