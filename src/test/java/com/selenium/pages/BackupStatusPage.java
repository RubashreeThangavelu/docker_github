package com.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.openqa.selenium.WebElement;

import java.time.Duration;

public class BackupStatusPage {

    private WebDriver driver;
    private WebDriverWait wait;
    
    
     public BackupStatusPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }
    
    private By scheduledTimeField = By.xpath("//p[strong[contains(text(),'Scheduled Time:')]]");
    private By lastFileField = By.xpath("//p[strong[contains(text(),'Last File:')]]");
    private By lastRunField = By.xpath("//p[strong[contains(text(),'Last Run:')]]");
    private By statusField = By.xpath("//p[strong[contains(text(),'Status:')]]/span");
    private By nextRunField = By.id("nextRunReadable");
    private By countdownField = By.id("countdown");
    
        // Verify page title
    public void verifyTitle(String expectedTitle) {
        Assert.assertEquals(driver.getTitle(), expectedTitle);
    }
    
        // Verify all essential elements are displayed
    public void verifyElementsDisplayed() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(scheduledTimeField));
        wait.until(ExpectedConditions.visibilityOfElementLocated(lastFileField));
        wait.until(ExpectedConditions.visibilityOfElementLocated(lastRunField));
        wait.until(ExpectedConditions.visibilityOfElementLocated(statusField));
        wait.until(ExpectedConditions.visibilityOfElementLocated(nextRunField));
        wait.until(ExpectedConditions.visibilityOfElementLocated(countdownField));
    }
    
    
    public String getLastFileName(){
    WebElement lastFileName = driver.findElement(lastRunField);
    return lastFileName.getText().replace("Last File: ", "").trim();
    }
}    
