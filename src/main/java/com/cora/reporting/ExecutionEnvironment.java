package com.cora.reporting;

import com.cora.config.ConfigReader;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Captures execution environment metadata for web PWA automation runs.
 * Values are loaded from config.properties and can be overridden at runtime via system properties.
 */
public final class ExecutionEnvironment {

    private final String automationEngine;
    private final String targetDeviceName;
    private final String osVersion;
    private final String appVersion;
    private final String appPackage;
    private final String platformName;
    private final String hostOs;
    private final String javaVersion;
    private final String baseUrl;

    private ExecutionEnvironment(
            String automationEngine,
            String targetDeviceName,
            String osVersion,
            String appVersion,
            String appPackage,
            String platformName,
            String hostOs,
            String javaVersion,
            String baseUrl) {
        this.automationEngine = automationEngine;
        this.targetDeviceName = targetDeviceName;
        this.osVersion = osVersion;
        this.appVersion = appVersion;
        this.appPackage = appPackage;
        this.platformName = platformName;
        this.hostOs = hostOs;
        this.javaVersion = javaVersion;
        this.baseUrl = baseUrl;
    }

    public static ExecutionEnvironment fromConfig() {
        ConfigReader.init();
        return new ExecutionEnvironment(
                resolve("report.env.automation.engine", "Selenium WebDriver"),
                resolve("report.env.device.name", "Google Chrome"),
                resolve("report.env.os.version", "Desktop"),
                resolve("report.env.app.version", "Latest"),
                resolve("report.env.app.package", "Cora PWA"),
                resolve("report.env.platform.name", "Web (PWA)"),
                System.getProperty("os.name", "Unknown"),
                System.getProperty("java.version", "Unknown"),
                ConfigReader.get("base.url", "N/A")
        );
    }

    private static String resolve(String configKey, String defaultValue) {
        String systemOverride = System.getProperty(configKey);
        if (systemOverride != null && !systemOverride.isBlank()) {
            return systemOverride;
        }
        return ConfigReader.get(configKey, defaultValue);
    }

    public Map<String, String> asSystemInfoMap() {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("Automation Engine", automationEngine);
        info.put("Platform", platformName);
        info.put("Browser", targetDeviceName);
        info.put("Viewport", osVersion);
        info.put("PWA Version", appVersion);
        info.put("Application", appPackage);
        info.put("Host OS", hostOs);
        info.put("Java Version", javaVersion);
        info.put("Base URL", baseUrl);
        info.put("Execution Mode", "Parallel (methods + DataProvider)");
        return info;
    }

    public String getAutomationEngine() {
        return automationEngine;
    }

    public String getTargetDeviceName() {
        return targetDeviceName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public String getPlatformName() {
        return platformName;
    }
}
