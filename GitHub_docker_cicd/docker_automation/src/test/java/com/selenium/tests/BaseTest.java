package com.selenium.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;

import java.net.URL;
import java.time.Duration;

public class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

@BeforeMethod
public void setUp() throws Exception {

    FirefoxOptions options = new FirefoxOptions();
    options.addArguments("--headless");

    driver = new RemoteWebDriver(
            new URL("http://firefox:4444"),
            options
    );

    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
}

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
