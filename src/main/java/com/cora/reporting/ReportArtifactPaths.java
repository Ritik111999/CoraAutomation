package com.cora.reporting;

import java.io.File;
import java.nio.file.Path;

/**
 * Holds resolved report artifact paths for the current execution run.
 */
public final class ReportArtifactPaths {

    private final String reportDirectory;
    private final String htmlReportPath;
    private final String pdfReportPath;
    private final String latestHtmlAliasPath;
    private final String moduleWiseReportPath;
    private final String scenarioReportPath;

    public ReportArtifactPaths(String reportDirectory, String htmlReportPath, String pdfReportPath) {
        this.reportDirectory = reportDirectory;
        this.htmlReportPath = htmlReportPath;
        this.pdfReportPath = pdfReportPath;
        this.latestHtmlAliasPath = reportDirectory + File.separator + "ExtentReport_LATEST.html";
        this.moduleWiseReportPath = reportDirectory + File.separator + "ModuleWiseReport.html";
        this.scenarioReportPath = reportDirectory + File.separator + "ScenarioReport.html";
    }

    public String getReportDirectory() {
        return reportDirectory;
    }

    public String getHtmlReportPath() {
        return htmlReportPath;
    }

    public String getPdfReportPath() {
        return pdfReportPath;
    }

    public String getLatestHtmlAliasPath() {
        return latestHtmlAliasPath;
    }

    public String getModuleWiseReportPath() {
        return moduleWiseReportPath;
    }

    public String getScenarioReportPath() {
        return scenarioReportPath;
    }

    public File htmlFile() {
        return Path.of(htmlReportPath).toFile();
    }

    public File pdfFile() {
        return Path.of(pdfReportPath).toFile();
    }
}
