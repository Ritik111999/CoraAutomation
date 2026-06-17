package com.cora.base;

import com.cora.config.ConfigReader;
import com.cora.utils.BrowserFactory;
import com.cora.utils.WebElementUtils;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

/**
 * Base test class — thread-safe Chrome lifecycle for hyper-parallel TestNG execution.
 * Each @Test / DataProvider row gets its own Chrome instance via ThreadLocal.
 */
public abstract class BaseTest {

    protected static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    protected static final ThreadLocal<WebElementUtils> utilsThreadLocal = new ThreadLocal<>();
    protected static final ThreadLocal<String> threadLabelLocal = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        ConfigReader.init();

        String threadLabel = "Chrome-Thread-" + Thread.currentThread().getId();
        threadLabelLocal.set(threadLabel);

        WebDriver driver = BrowserFactory.createChromeDriver();
        configureDriver(driver);

        driverThreadLocal.set(driver);
        utilsThreadLocal.set(new WebElementUtils(driver));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception ignored) {
                // Session may already be closed after a failure
            }
        }
        driverThreadLocal.remove();
        utilsThreadLocal.remove();
        threadLabelLocal.remove();
    }

    public static WebDriver getThreadLocalDriver() {
        return driverThreadLocal.get();
    }

    public static String getThreadLabel() {
        return threadLabelLocal.get();
    }

    protected WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    protected WebElementUtils getUtils() {
        return utilsThreadLocal.get();
    }

    protected void navigateToBaseUrl() {
        getDriver().get(ConfigReader.get("base.url"));
    }

    protected void navigateTo(String path) {
        String baseUrl = ConfigReader.get("base.url");
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        getDriver().get(baseUrl + normalizedPath);
    }

    private void configureDriver(WebDriver driver) {
        int implicitWait = ConfigReader.getInt("implicit.wait.seconds", 0);
        int pageLoadTimeout = ConfigReader.getInt("page.load.timeout.seconds", 60);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));

        if (!ConfigReader.getBoolean("chrome.headless", false)) {
            driver.manage().window().maximize();
        }
    }
}
