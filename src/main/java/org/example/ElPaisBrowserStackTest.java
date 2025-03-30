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

    // 🔹 BrowserStack Credentials (Replace with your actual credentials)
    private static final String USERNAME = "rohithvasudevan_pAriKW";
    private static final String AUTOMATE_KEY = "2aKqVfSQAGLZsx9gHUdC";
    private static final String BROWSERSTACK_URL = "https://" + USERNAME + ":" + AUTOMATE_KEY + "@hub.browserstack.com/wd/hub";


    // 🔹 BrowserStack Setup
    @BeforeClass
    @Parameters({"browser", "os"})
    public void setup(@Optional("Chrome") String browser, @Optional("Windows 10") String os) throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browserName", browser);
        caps.setCapability("browserVersion", "latest");

        // ✅ Corrected W3C Compliant Capabilities
        Map<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("os", os);  // ✅ Use "Windows", "OS X", "iOS", "Android"
        bstackOptions.put("osVersion", "latest"); // ✅ Specify OS Version if needed
        bstackOptions.put("sessionName", "El Pais Scraping Test");


        if (os.equalsIgnoreCase("Windows 10") || os.equalsIgnoreCase("Windows 11")) {
            bstackOptions.put("os", "Windows");
            bstackOptions.put("osVersion", os.replace("Windows ", ""));
        }
        else if (os.equalsIgnoreCase("macOS Ventura") || os.equalsIgnoreCase("macOS Sonoma")) {
            bstackOptions.put("os", "OS X");
            bstackOptions.put("osVersion", os.replace("macOS ", ""));
        }
        else if (os.equalsIgnoreCase("Android") || os.equalsIgnoreCase("iOS")) {
            bstackOptions.put("device", browser); // Set mobile device name
            bstackOptions.put("realMobile", "true");
        }
        else {
            throw new IllegalArgumentException("Unsupported OS: " + os);
        }

        bstackOptions.put("sessionName", "El Pais Scraping Test");
        caps.setCapability("bstack:options", bstackOptions);

        driver = new RemoteWebDriver(new URL(BROWSERSTACK_URL), caps);
        System.out.println("✅ Running test on: " + browser + " | OS: " + os);

        caps.setCapability("bstack:options", bstackOptions);

        if (browser.equalsIgnoreCase("Chrome") || browser.equalsIgnoreCase("Edge")) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-infobars");
            options.addArguments("--disable-popup-blocking");
            options.addArguments("--remote-debugging-port=9222");
            caps.setCapability(ChromeOptions.CAPABILITY, options);
        }

        else if (browser.equalsIgnoreCase("Firefox")) {
            FirefoxOptions options = new FirefoxOptions();
            caps.setCapability(FirefoxOptions.FIREFOX_OPTIONS, options);
        }
        else if (browser.equalsIgnoreCase("Safari")) {
            SafariOptions options = new SafariOptions();
            caps.setCapability("safari.options", options);
        }
        else if (browser.contains("iPhone") || browser.contains("iPad")) {
            bstackOptions.put("device", browser);
            bstackOptions.put("realMobile", "true");
        }
        else if (browser.contains("Samsung") || browser.contains("Pixel") || browser.contains("Galaxy")) {
            bstackOptions.put("device", browser);
            bstackOptions.put("realMobile", "true");
        }

        driver = new RemoteWebDriver(new URL(BROWSERSTACK_URL), caps);
        System.out.println("✅ Running test on: " + browser + " | OS: " + os);
    }


    public WebDriver getDriver() {
        return driver;
    }

    // 🔹 Test Case - Scrape, Translate & Analyze
    @Test
    public void testElPaisScraping(String browser, String version, String os) {
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
    @AfterMethod
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void handleCookiePopup(WebDriver driver) {
            try {
                WebElement acceptButton = driver.findElement(By.xpath("//*[contains(text(), 'Accept') or contains(text(), 'Aceptar')]"));
                acceptButton.click();
                System.out.println("✅ Accepted Cookies");
                driver.manage().window().maximize();
                System.out.println("✅ Browser maximized!");
            } catch (NoSuchElementException e) {
                System.out.println("⚠️ No cookie popup found, continuing...");
            }
    }
}


