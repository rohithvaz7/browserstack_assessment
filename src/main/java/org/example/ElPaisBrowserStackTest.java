package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.testng.annotations.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElPaisBrowserStackTest {
    private WebDriver driver;

    // 🔹 BrowserStack Credentials
    private static final String USERNAME = "";
    private static final String AUTOMATE_KEY = "";
    private static final String BROWSERSTACK_URL = "https://" + USERNAME + ":" + AUTOMATE_KEY + "@hub.browserstack.com/wd/hub";

    // 🔹 BrowserStack Setup
    @BeforeClass
    @Parameters({"browser", "os"})
    public void setup(String browser, String os) throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browserName", browser);
        caps.setCapability("browserVersion", "latest");

        // ✅ W3C-Compliant BrowserStack Capabilities
        Map<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("sessionName", "El Pais Scraping Test");
        bstackOptions.put("acceptInsecureCerts", true); // Try this


        if (os.startsWith("Windows")) {
            bstackOptions.put("os", "Windows");
            bstackOptions.put("osVersion", os.replace("Windows ", ""));
        } else if (os.startsWith("macOS")) {
            bstackOptions.put("os", "OS X");
            bstackOptions.put("osVersion", os.replace("macOS ", ""));
        } else if (os.equalsIgnoreCase("Android") || os.equalsIgnoreCase("iOS")) {
            bstackOptions.put("device", browser); // Mobile device name
            bstackOptions.put("realMobile", "true");
        } else {
            throw new IllegalArgumentException("Unsupported OS: " + os);
        }

        caps.setCapability("bstack:options", bstackOptions);

        if (browser.equalsIgnoreCase("Chrome") || browser.equalsIgnoreCase("Edge")) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-infobars");
            options.addArguments("--disable-popup-blocking");
            caps.setCapability(ChromeOptions.CAPABILITY, options);
        } else if (browser.equalsIgnoreCase("Firefox")) {
            FirefoxOptions options = new FirefoxOptions();
            caps.setCapability(FirefoxOptions.FIREFOX_OPTIONS, options);
        } else if (browser.equalsIgnoreCase("Safari")) {
            SafariOptions options = new SafariOptions();
            caps.setCapability("safari.options", options);
        }

        driver = new RemoteWebDriver(new URL(BROWSERSTACK_URL), caps);
        System.out.println("✅ Running test on: " + browser + " | OS: " + os);
    }

    public WebDriver getDriver() {
        return driver;
    }

    // 🔹 Test Case - Scrape, Translate & Analyze
    @Test
    @Parameters({"browser", "os"})
    public void testElPaisScraping(String browser, String os) {
        System.out.println("\nRunning on: " + browser + " - " + os);

        // Navigate & Scrape Articles
        driver.get("https://elpais.com/");
        handleCookiePopup(driver);
        List<String> spanishTitles = Scraper.scrapeArticles(driver);

        // Translate Titles
        List<String> translatedTitles = TranslatorUtil.translateTitles(spanishTitles);

        // Analyze Frequency
        WordAnalyzer.analyzeWordFrequency(translatedTitles);
    }

    // 🔹 Cleanup
    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.quit();
            System.out.println("✅ Browser session ended.");
        }
    }

    // 🔹 Handle Cookie Popup & Maximize Window
    private void handleCookiePopup(WebDriver driver) {
        try {
            Thread.sleep(4000);
            WebElement acceptButton = driver.findElement(By.xpath("//*[contains(text(), 'Accept') or contains(text(), 'Aceptar')]"));
            acceptButton.click();
            System.out.println("✅ Accepted Cookies");
            driver.manage().window().maximize();
            System.out.println("✅ Browser maximized!");
        } catch (NoSuchElementException | InterruptedException e) {
            System.out.println("⚠️ No cookie popup found, continuing...");
        }
    }
}
