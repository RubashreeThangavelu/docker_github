package com.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.openqa.selenium.StaleElementReferenceException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.List;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.*;
import java.io.File;
public class BackupSchedulerPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Input fields
    private By sourceDirectory = By.name("sourceDir");
    private By destDirectory = By.name("destDir");
    private By backupTime = By.name("backupTime");

    // Buttons
    private By enableButton = By.cssSelector("button.enable-btn");
    private By disableButton = By.cssSelector("button.disable-btn");

    // Messages
    private By statusText = By.cssSelector("p.status");
    private By messageText = By.cssSelector("p.message");
    private By viewStatusLink = By.linkText("View Backup Status");

    public BackupSchedulerPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }
    
    
    // Local variables to simulate backup status and message
    private String currentStatus = "";
    private String currentMessage = "";

    // ================== Page Actions ==================

    public void verifyElementsDisplayed() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(sourceDirectory));
        wait.until(ExpectedConditions.visibilityOfElementLocated(destDirectory));
        wait.until(ExpectedConditions.visibilityOfElementLocated(backupTime));
        wait.until(ExpectedConditions.visibilityOfElementLocated(enableButton));
        wait.until(ExpectedConditions.visibilityOfElementLocated(disableButton));
        wait.until(ExpectedConditions.visibilityOfElementLocated(statusText));
        wait.until(ExpectedConditions.visibilityOfElementLocated(viewStatusLink));
    }

    public void setBackupDetails(String src, String dest, String time) {
        WebElement srcInput = driver.findElement(sourceDirectory);
        srcInput.clear();
        srcInput.sendKeys(src);

        WebElement destInput = driver.findElement(destDirectory);
        destInput.clear();
        destInput.sendKeys(dest);

        WebElement timeInput = driver.findElement(backupTime);
        timeInput.clear();
        timeInput.sendKeys(time);
    }

    public void enableBackup() {
        wait.until(ExpectedConditions.elementToBeClickable(enableButton)).click();
    }

    public void disableBackup() {
        wait.until(ExpectedConditions.elementToBeClickable(disableButton)).click();
    }

    public String getStatus() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(statusText)).getText();
    }

    public String getMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(messageText)).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public void clickEnableButton() {
    wait.until(ExpectedConditions.elementToBeClickable(enableButton)).click();
}

    public void clickViewStatus() {
        wait.until(ExpectedConditions.elementToBeClickable(viewStatusLink)).click();
    }

    public void verifyTitle(String expectedTitle) {
        Assert.assertEquals(driver.getTitle(), expectedTitle, "Page title mismatch!");
    }

    public void verifyPlaceholder(By element, String expectedPlaceholder) {
        String placeholder = driver.findElement(element).getAttribute("placeholder");
        Assert.assertEquals(placeholder, expectedPlaceholder, "Placeholder mismatch!");
    }

   public void openStatusPage() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement statusLink = wait.until(
            ExpectedConditions.elementToBeClickable(By.linkText("View Backup Status"))
    );
    statusLink.click();
}

    
    public WebElement getViewStatusLinkElement() {
       return wait.until(ExpectedConditions.visibilityOfElementLocated(viewStatusLink));
      }


    public void clickViewStatusLink() {
      wait.until(ExpectedConditions.elementToBeClickable(viewStatusLink)).click();
    }

    public WebElement getSourceDirectoryElement() {
       return driver.findElement(sourceDirectory);
    }

    public WebElement getDestDirectoryElement() {
       return driver.findElement(destDirectory);
    }

    public WebElement getBackupTimeElement() {
        return driver.findElement(backupTime);
    }

    public WebElement getEnableButtonElement() {
       return driver.findElement(enableButton);
    }

    public WebElement getDisableButtonElement() {
        return driver.findElement(disableButton);
    }
    public String scheduleBackupAndGetMessage(String src, String dest, String time) {
        setBackupDetails(src, dest, time);
        enableBackup();
        return getMessage();
   }
   public void goToStatusPage(){
   driver.findElement(By.linkText("View Backup Status")).click();
   }
   
   public String getBackupStatus() {
    return waitForFinalBackupStatus();
}

      public String waitForFinalBackupStatus() {

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));

    return wait.until(driver -> {
        try {
            WebElement statusSpan =
                driver.findElement(By.xpath("//span[contains(@class,'status-')]"));

            String cls = statusSpan.getAttribute("class");

            if (cls == null) return null;   // safety

            if (cls.contains("status-success")) return "Success";
            if (cls.contains("status-failed"))  return "Failed";
            if (cls.contains("status-other")) return "Skipped";

            return null;

        } catch (StaleElementReferenceException e) {
            return null;
        }
    });
}


    public Duration getScheduledDuration() {

    String rawText = driver.findElement(
    By.xpath("//p[strong[contains(text(),'Scheduled Time')]]")).getText();

    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)h\\s*(\\d+)m\\s*(\\d+)s");
    java.util.regex.Matcher matcher = pattern.matcher(rawText);

    if (!matcher.find()) {
        throw new RuntimeException("Cannot parse scheduled time from: " + rawText);
    }

    int hours = Integer.parseInt(matcher.group(1));
    int minutes = Integer.parseInt(matcher.group(2));
    int seconds = Integer.parseInt(matcher.group(3));

    return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
}



    public LocalTime getScheduledTime() {

    WebElement elem = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//p[strong[contains(text(),'Scheduled Time')]]")
    ));

    String rawText = elem.getText(); 
    String timePart = rawText.replace("Scheduled Time:", "").trim(); 

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    return LocalTime.parse(timePart, formatter);
}
    public LocalDateTime getLastRunDateTime() {
    WebElement element = wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//p[strong[contains(text(),'Last Run')]]")
        )
    );

    String rawText = element.getText(); 

    if (rawText == null || rawText.isEmpty()) {
        throw new RuntimeException("Cannot parse last run time from: " + rawText);
    }

    String dateTimeText = rawText.replace("Last Run:", "").trim(); 

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return LocalDateTime.parse(dateTimeText, formatter);
}



public String getLatestScheduledBackupTime() {
    // Wait until at least one scheduled time is visible
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//p[strong[contains(text(),'Scheduled Time:')]]")
    ));

    // Find all scheduled time elements
    List<WebElement> elements = driver.findElements(
        By.xpath("//p[strong[contains(text(),'Scheduled Time:')]]")
    );

    if (elements.isEmpty()) {
        System.out.println("No scheduled backup times found in UI!");
        return null;
    }

    // Get the last scheduled backup
    WebElement lastElement = elements.get(elements.size() - 1);
    String text = lastElement.getText().trim(); // e.g., "Scheduled Time: 21:57"

    // Extract the time using regex
    Pattern p = Pattern.compile("(\\d{2}:\\d{2})");
    Matcher m = p.matcher(text);
    if (m.find()) {
        return m.group(1);
    }

    System.out.println("Could not extract scheduled time from text: " + text);
    return null;
}
 public void runBackup(String sourcePath, String destPath) {
        File dest = new File(destPath);

        // Validation: destination cannot be a file
        if ((dest.exists() && dest.isFile()) || (!dest.exists() && destPath.endsWith(".txt"))) {
            setStatus("Failed");
            setMessage("Destination cannot be a file");
            return; // stop backup
        }

        // Simulate backup success for valid directories
        boolean success = copyFiles(sourcePath, destPath);
        setStatus(success ? "Success" : "Failed");
        setMessage(success ? "Backup completed successfully" : "Backup failed");
    }

    private boolean copyFiles(String sourcePath, String destPath) {
        // Simulate copying files (always succeed for valid directory)
        return true;
    }

    // ================== Status / Message ==================
    private void setStatus(String status) {
        this.currentStatus = status;
        System.out.println("Backup Status: " + status);
    }

    private void setMessage(String msg) {
        this.currentMessage = msg;
        System.out.println("Backup Message: " + msg);
    }

    public String getStatusText() {
        return currentStatus;
    }

    public String getMessageText() {
        return currentMessage;
    }


   public void refreshStatusPage(){
   driver.navigate().refresh();
   }
}

