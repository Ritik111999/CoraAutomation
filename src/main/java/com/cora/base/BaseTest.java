package com.cora.base;

import com.cora.config.ConfigReader;
import com.cora.utils.WebElementUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.time.Duration;

/**
 * Base test class providing thread-safe WebDriver lifecycle management.
 * Uses ThreadLocal to support parallel TestNG execution.
 */
public abstract class BaseTest {

    protected static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    protected static final ThreadLocal<WebElementUtils> utilsThreadLocal = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser"})
    public void setUp(@Optional String browserParam) {
        ConfigReader.init();

        String browser = browserParam != null && !browserParam.isBlank()
                ? browserParam
                : ConfigReader.get("browser", "chrome");

        WebDriver driver = createDriver(browser.toLowerCase());
        configureDriver(driver);

        driverThreadLocal.set(driver);
        utilsThreadLocal.set(new WebElementUtils(driver));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
        }
        driverThreadLocal.remove();
        utilsThreadLocal.remove();
    }

    public static WebDriver getThreadLocalDriver() {
        return driverThreadLocal.get();
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

    private WebDriver createDriver(String browser) {
        return switch (browser) {
            case "firefox" -> new FirefoxDriver(buildFirefoxOptions());
            case "edge" -> new EdgeDriver(buildEdgeOptions());
            case "chrome" -> new ChromeDriver(buildChromeOptions());
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
    }

    private void configureDriver(WebDriver driver) {
        int implicitWait = ConfigReader.getInt("implicit.wait.seconds", 0);
        int pageLoadTimeout = ConfigReader.getInt("page.load.timeout.seconds", 30);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
        driver.manage().window().maximize();
    }

    private ChromeOptions buildChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");
        return options;
    }

    private FirefoxOptions buildFirefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        options.addPreference("dom.webnotifications.enabled", false);
        return options;
    }

    private EdgeOptions buildEdgeOptions() {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");
        return options;
    }
}
