package com.cora.reporting;

import com.cora.config.ConfigReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

/**
 * Generates the Carderosity-style Test Scenario Report with steps, preconditions, and expected results.
 */
public final class ScenarioReportGenerator {

    private ScenarioReportGenerator() {
    }

    public static void generate(List<TestScenarioResult> results, ReportArtifactPaths paths, Instant generatedAt) {
        String productName = ConfigReader.get("report.product.name", "Cora PWA");
        String title = productName + " — Test Scenario Report";

        int passed = (int) results.stream().filter(TestScenarioResult::isPassed).count();
        int failed = (int) results.stream().filter(TestScenarioResult::isFailed).count();
        int skipped = (int) results.stream().filter(TestScenarioResult::isSkipped).count();
        int total = results.size();

        String extentFileName = Path.of(paths.getHtmlReportPath()).getFileName().toString();

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\">");
        html.append("<title>").append(QaReportHtmlSupport.escape(title)).append("</title>");
        html.append("<style>").append(QaReportHtmlSupport.sharedStyles()).append("</style></head><body>");

        html.append("<h1>").append(QaReportHtmlSupport.escape(title)).append("</h1>");
        html.append("<div class=\"meta\">Generated: ")
                .append(QaReportHtmlSupport.generatedTimestamp(generatedAt)).append("</div>");
        html.append("<div class=\"summary\">Total Scenarios: ").append(total)
                .append(" &nbsp;|&nbsp; Passed: ").append(passed)
                .append(" &nbsp;|&nbsp; Failed: ").append(failed);
        if (skipped > 0) {
            html.append(" &nbsp;|&nbsp; Skipped: ").append(skipped);
        }
        html.append("</div>");

        html.append("<h2>Scenario Details</h2>");

        for (TestScenarioResult result : results) {
            appendScenarioCard(html, result);
        }

        html.append("<div class=\"footer-links\">");
        html.append("<a href=\"ModuleWiseReport.html\">Module Summary</a>");
        html.append("<a href=\"").append(QaReportHtmlSupport.escape(extentFileName))
                .append("\">Extent Report</a>");
        html.append("</div>");

        html.append("</body></html>");

        write(paths.getScenarioReportPath(), html.toString());
    }

    private static void appendScenarioCard(StringBuilder html, TestScenarioResult result) {
        TestScenarioDefinition def = result.getDefinition();
        String statusClass = QaReportHtmlSupport.statusClass(result.getStatus());

        html.append("<div class=\"scenario-card\">");
        html.append("<div class=\"scenario-title\">")
                .append(QaReportHtmlSupport.escape(def.getScenarioId())).append(" — ")
                .append(QaReportHtmlSupport.escape(def.getTitle())).append(" ")
                .append("<span class=\"").append(statusClass).append("\">")
                .append(QaReportHtmlSupport.escape(def.getType())).append("</span></div>");

        html.append("<div class=\"scenario-meta\">Module: ")
                .append(QaReportHtmlSupport.escape(def.getModuleName()))
                .append(" &nbsp;|&nbsp; Type: ")
                .append(QaReportHtmlSupport.escape(def.getType())).append("</div>");

        html.append("<div class=\"scenario-block\"><strong>Preconditions:</strong> ")
                .append(QaReportHtmlSupport.escape(def.getPreconditions())).append("</div>");

        html.append("<div class=\"scenario-block\"><strong>Test Steps:</strong><ol>");
        int stepNumber = 1;
        for (String step : def.getSteps()) {
            html.append("<li>").append(QaReportHtmlSupport.escape(step)).append("</li>");
            stepNumber++;
        }
        if (stepNumber == 1) {
            html.append("<li>Execute automated scenario</li>");
        }
        html.append("</ol></div>");

        html.append("<div class=\"scenario-block\"><strong>Expected Result:</strong> ")
                .append(QaReportHtmlSupport.escape(def.getExpectedResult())).append("</div>");

        html.append("<div class=\"scenario-block\"><strong>Actual Status:</strong> ")
                .append("<span class=\"").append(statusClass).append("\">")
                .append(QaReportHtmlSupport.escape(result.getStatus().toUpperCase()))
                .append("</span>");
        if (result.isFailed() && result.getFailureMessage() != null) {
            html.append("<br><strong>Failure:</strong> ")
                    .append(QaReportHtmlSupport.escape(result.getFailureMessage()));
        }
        html.append("</div>");

        html.append("</div>");
    }

    private static void write(String path, String content) {
        try {
            Files.writeString(Path.of(path), content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write Scenario report: " + path, e);
        }
    }
}
