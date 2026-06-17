package com.cora.reporting;

import com.cora.config.ConfigReader;

import java.io.File;
import java.nio.file.Path;

/**
 * Holds resolved PDF report artifact paths for the current execution run (Carderosity naming).
 */
public final class ReportArtifactPaths {

    private final String reportDirectory;
    private final String extentHtmlTempPath;
    private final String extentPdfPath;
    private final String moduleWisePdfPath;
    private final String scenarioPdfPath;

    public ReportArtifactPaths(String reportDirectory, String extentHtmlTempPath, String extentPdfPath) {
        this.reportDirectory = reportDirectory;
        this.extentHtmlTempPath = extentHtmlTempPath;
        this.extentPdfPath = extentPdfPath;

        String productName = ConfigReader.get("report.product.name", "Cora PWA");
        this.moduleWisePdfPath = reportDirectory + File.separator + productName + " — Module Wise QA Report.pdf";
        this.scenarioPdfPath = reportDirectory + File.separator + productName + " — Test Scenario Report.pdf";
    }

    public String getReportDirectory() {
        return reportDirectory;
    }

    /** Temporary Extent Spark HTML used only during the run; deleted after PDF is written. */
    public String getExtentHtmlTempPath() {
        return extentHtmlTempPath;
    }

    /** Carderosity-style extended report (Extent + screenshots). */
    public String getExtentPdfPath() {
        return extentPdfPath;
    }

    public String getModuleWisePdfPath() {
        return moduleWisePdfPath;
    }

    public String getScenarioPdfPath() {
        return scenarioPdfPath;
    }

    public File extentPdfFile() {
        return Path.of(extentPdfPath).toFile();
    }
}
