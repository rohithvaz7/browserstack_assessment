package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.net.URL;

public class BrowserStackConfig {
    private static final String USERNAME = "rohithvasudevan_pAriKW";
    private static final String AUTOMATE_KEY = "2aKqVfSQAGLZsx9gHUdC";
    private static final String BROWSERSTACK_URL = "https://" + USERNAME + ":" + AUTOMATE_KEY + "@hub-cloud.browserstack.com/wd/hub";

    public static WebDriver createDriver(String browser, String os) throws Exception {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browserName", browser);
        caps.setCapability("platformName", os);

        return new RemoteWebDriver(new URL(BROWSERSTACK_URL), caps);
    }
}


