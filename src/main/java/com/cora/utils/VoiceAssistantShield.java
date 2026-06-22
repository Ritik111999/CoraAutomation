package com.cora.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * Voice assistant / AI FAB is out of test scope. This utility disables its pointer events
 * so it cannot intercept Selenium clicks on underlying UI (Properties menus, Home View All, etc.).
 * We never open, assert, or interact with voice assistant features.
 */
public final class VoiceAssistantShield {

    private static final String NEUTRALIZE_SCRIPT =
            "const nodes = document.querySelectorAll("
                    + "'button[aria-label=\"Open voice assistant\"], img[alt=\"Voice assistant\"]'"
                    + ");"
                    + "nodes.forEach(el => {"
                    + "  el.style.pointerEvents='none';"
                    + "  const host = el.closest('button') || el.parentElement;"
                    + "  if (host) { host.style.pointerEvents='none'; host.style.zIndex='-1'; }"
                    + "});";

    private VoiceAssistantShield() {
    }

    public static void neutralize(WebDriver driver) {
        if (!(driver instanceof JavascriptExecutor js)) {
            return;
        }
        try {
            js.executeScript(NEUTRALIZE_SCRIPT);
        } catch (Exception ignored) {
            // FAB not on this page
        }
    }
}
