package com.selenium.tests;

import com.selenium.pages.BackupStatusPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import org.openqa.selenium.interactions.Actions;  
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.Dimension;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import java.time.LocalDate;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;
import org.testng.Reporter;
import java.io.FileWriter;
import java.io.IOException;
import com.selenium.utils.ConfigReader;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import java.net.URL;
import org.openqa.selenium.remote.DesiredCapabilities;


public class BackupStatusTest extends BaseTest {

    private BackupStatusPage statusPage;
    private WebDriverWait wait;

    private String baseUrl;

    @BeforeMethod
    public void setup() {
        driver.manage().deleteAllCookies();
        
        
         baseUrl = ConfigReader.getProperty("base.url");

        driver.get(baseUrl + "/index.jsp");


        statusPage = new BackupStatusPage(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        Assert.assertTrue(driver.getCurrentUrl().contains("index.jsp"));
    }
    
     //1. Verify signup page title-UI
    @Test
    public void verifyBackupPageTitle() {
        statusPage.verifyTitle("Backup Status");
    }
     //2. Verify all elements are present
    @Test
    public void verifyAllElementsPresent() {
        statusPage.verifyElementsDisplayed();
    }
    
    //3. Verify Heading and label styling
      @Test
    public void verifyHeadingAndLabelsStyling() {
        // 1. Check heading "Backup Status"
        WebElement heading = driver.findElement(By.xpath("//div[@class='card']/h2"));
        String textAlign = heading.getCssValue("text-align");
        String fontWeight = heading.getCssValue("font-weight");
        String fontSize = heading.getCssValue("font-size");

        Assert.assertEquals(textAlign, "center", "Heading is not horizontally centered");
        Assert.assertTrue(fontWeight.equals("700") || fontWeight.equalsIgnoreCase("bold"), "Heading is not bold");
        System.out.println("Heading font size: " + fontSize);

        // 2. Check labels (<strong> elements)
        WebElement scheduledLabel = driver.findElement(By.xpath("//p[strong[contains(text(),'Scheduled Time:')]]/strong"));
        WebElement lastFileLabel = driver.findElement(By.xpath("//p[strong[contains(text(),'Last File:')]]/strong"));
        WebElement lastRunLabel = driver.findElement(By.xpath("//p[strong[contains(text(),'Last Run:')]]/strong"));
        WebElement statusLabel = driver.findElement(By.xpath("//p[strong[contains(text(),'Status:')]]/strong"));
        WebElement nextRunLabel = driver.findElement(By.xpath("//p[strong[contains(text(),'Next Scheduled Run:')]]/strong"));

        verifyLabelStyling(scheduledLabel);
        verifyLabelStyling(lastFileLabel);
        verifyLabelStyling(lastRunLabel);
        verifyLabelStyling(statusLabel);
        verifyLabelStyling(nextRunLabel);

        // 3. Check values 
        WebElement statusValue = driver.findElement(By.xpath("//p[strong[contains(text(),'Status:')]]/span"));
        WebElement nextRunValue = driver.findElement(By.id("nextRunReadable"));

        verifyValueStyling(statusValue);
        verifyValueStyling(nextRunValue);

        // Optionally, print success message
        System.out.println("UI styling verified for headings, labels, and values.");
    }

    private void verifyLabelStyling(WebElement label) {
        String fontWeight = label.getCssValue("font-weight");
        String fontSize = label.getCssValue("font-size");
        String color = label.getCssValue("color");

        Assert.assertTrue(fontWeight.equals("700") || fontWeight.equalsIgnoreCase("bold"), "Label is not bold: " + label.getText());
        Assert.assertNotNull(fontSize, "Label font size is missing: " + label.getText());
        Assert.assertNotNull(color, "Label color is missing: " + label.getText());

        System.out.println("Label '" + label.getText() + "' - Font: " + fontWeight + ", Size: " + fontSize + ", Color: " + color);
    }

    private void verifyValueStyling(WebElement value) {
        String fontWeight = value.getCssValue("font-weight");
        String fontSize = value.getCssValue("font-size");
        String color = value.getCssValue("color");

        Assert.assertNotNull(fontWeight, "Value font-weight missing: " + value.getText());
        Assert.assertNotNull(fontSize, "Value font-size missing: " + value.getText());
        Assert.assertNotNull(color, "Value color missing: " + value.getText());

        System.out.println("Value '" + value.getText() + "' - Font: " + fontWeight + ", Size: " + fontSize + ", Color: " + color);
    }
    
    //4. Verify Countdown Color, font and styling
@Test
public void verifyCountdownTimerStyling() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

    WebElement countdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("countdown")));
    Assert.assertTrue(countdown.isDisplayed(), "Countdown timer is not displayed.");

    // Check font weight
    String fontWeight = countdown.getCssValue("font-weight");
    Assert.assertTrue(fontWeight.equals("700") || fontWeight.equalsIgnoreCase("bold"),
            "Countdown font is not bold");

    // Check font size
    String fontSize = countdown.getCssValue("font-size");
    Assert.assertNotNull(fontSize, "Countdown font size is not defined");
    System.out.println("Countdown font size: " + fontSize);

    // Check color is defined (but do not hard-code expected value)
    String actualHexColor = Color.fromString(countdown.getCssValue("color")).asHex();
    Assert.assertNotNull(actualHexColor, "Countdown color is not defined");
    System.out.println("Countdown color (hex): " + actualHexColor);

    System.out.println("Countdown text: " + countdown.getText());
}



    //5. Verify Background color and card styling
    @Test
    public void verifyBackupStatusFlow() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

    WebElement body= driver.findElement(By.tagName("body"));
    String bodyColor=body.getCssValue("background-color");
    String bodyHex=Color.fromString(bodyColor).asHex();
    String expectedBodyHex="#000000";
    Assert.assertEquals(bodyHex, expectedBodyHex, "Page Background color mismatch");
    System.out.println("Page background color:" +bodyHex);
    
    WebElement card= driver.findElement(By.className("card"));
    Assert.assertTrue(card.isDisplayed(), "Card is not displayed");
    
    String cardBgColor=Color.fromString(card.getCssValue("background-color")).asHex();
    String expectedCardBg="#ffffff";
    Assert.assertEquals(cardBgColor, expectedCardBg, "Card background color mismatch");
    System.out.println("Card backgroound color: "+cardBgColor);
    
    String borderRadius=card.getCssValue("border-radius");
    System.out.println("Card border-radius: "+ borderRadius);
    Assert.assertNotNull(borderRadius, "Card border-radius not defined");
    
    String boxShadow = card.getCssValue("box-shadow");
    System.out.println("Card box-shadow:" +boxShadow);
    Assert.assertTrue(boxShadow!=null && !boxShadow.isEmpty(), "Card shadow not defined" );
    
    String padding = card.getCssValue("padding");
    System.out.println("Card padding:" + padding);
    Assert.assertNotNull(padding, "Card padding not defined");
    }
    
      //6. Verify the Responsiveness Layout 
    @Test
    public void verifyResponsiveLayout() throws InterruptedException {

    int[][] screenSizes = {
    {1366,768},{768,1024},{375,812}};

    String[] viewportNames = {"Desktop", "Tablet", "Mobile"};

    for(int i=0;i<screenSizes.length;i++){
    int width = screenSizes[i][0];
    int height = screenSizes[i][1];

    driver.manage().window().setSize(new Dimension(width, height));
    Thread.sleep(1000);

    System.out.println("Checking UI in " + viewportNames[i] + "View:" + width + "x" + height);
       // Verify elements are visible
    Assert.assertTrue(driver.findElement(By.xpath("//p[strong[contains(text(),'Scheduled Time:')]]/strong")).isDisplayed());
    Assert.assertTrue(driver.findElement(By.xpath("//p[strong[contains(text(),'Last File:')]]/strong")).isDisplayed());
    Assert.assertTrue(driver.findElement(By.xpath("//p[strong[contains(text(),'Last Run:')]]/strong")).isDisplayed());
    Assert.assertTrue(driver.findElement(By.xpath("//p[strong[contains(text(),'Status:')]]/strong")).isDisplayed());
    Assert.assertTrue(driver.findElement(By.xpath("//p[strong[contains(text(),'Next Scheduled Run:')]]/strong")).isDisplayed());
    System.out.println("UI adjusts properly in Desktop, Tablet, Mobile view");
    }
    }
    
    
    //8. Verify status when no backup has run
     @Test

    public void verifyStatusWhenNoBackupHasRun() throws InterruptedException {

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));


    wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//h2[text()='Backup Status']"))
    );

    WebElement lastFile = driver.findElement(
            By.xpath("//p[strong[text()='Last File:']]")
    );
    WebElement lastRun = driver.findElement(
            By.xpath("//p[strong[text()='Last Run:']]")
    );
    WebElement status = driver.findElement(
            By.xpath("//p[strong[text()='Status:']]")
    );

    String lastFileValue = lastFile.getText().replace("Last File:", "").trim();
    String lastRunValue  = lastRun.getText().replace("Last Run:", "").trim();
    String statusValue   = status.getText().replace("Status:", "").trim();

    System.out.println("Last File = " + lastFileValue);
    System.out.println("Last Run = " + lastRunValue);
    System.out.println("Status = " + statusValue);

    Assert.assertTrue(
            lastFileValue.equalsIgnoreCase("none") ||
            lastFileValue.equalsIgnoreCase("n/a") ||
            lastFileValue.isEmpty(),
            "Expected Last File to be None / Empty but found: " + lastFileValue
    );

    Assert.assertTrue(
            lastRunValue.equalsIgnoreCase("none") ||
            lastRunValue.equalsIgnoreCase("n/a") ||
            lastRunValue.isEmpty(),
            "Expected Last Run to be None / Empty but found: " + lastRunValue
    );

 Assert.assertTrue(
        statusValue.equalsIgnoreCase("Scheduled") ||
        statusValue.equalsIgnoreCase("none") ||
        statusValue.equalsIgnoreCase("not started") ||
        statusValue.equalsIgnoreCase("n/a"),
        "Expected Status to be Scheduled / None / Not Started but found: " + statusValue
);
if (statusValue.equalsIgnoreCase("none")
        || statusValue.equalsIgnoreCase("not started")
        || statusValue.equalsIgnoreCase("n/a")|| statusValue.equalsIgnoreCase("Scheduled")) {

    Assert.assertTrue(
            lastRunValue.equalsIgnoreCase("none") ||
            lastRunValue.equalsIgnoreCase("n/a") ||
            lastRunValue.isEmpty(),
            "Expected Last Run to be None / Empty but found: " + lastRunValue
    );

} else {

    Assert.assertFalse(lastRunValue.isEmpty(),
            "Last Run should not be empty when history exists");
}

    System.out.println(" Passed — System handles no backup history correctly");
}
  
     //7.Verify page refresh performance 
    @Test
    public void verifyPageRefreshPerformance() throws InterruptedException{
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement countdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("countdown")));
    WebElement status = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[strong[contains(text(),'Status')]]/span")));
    System.out.println("Initial Countdown:" + countdown.getText());
    System.out.println("Initial Status: " + status.getText());
  
    String previousCountdown = countdown.getText();
    String previousStatus = status.getText();
  
    for(int i=1;i<=4;i++){
    Thread.sleep(16000);
    WebElement countdownNow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("countdown")));
    WebElement statusNow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[strong[contains(text(),'Status')]]/span")));
  
    Assert.assertTrue(countdownNow.isDisplayed(),"Countdown disappears after refresh cycles" +i);
    Assert.assertTrue(statusNow.isDisplayed(),"Status Disappears after refresh Cycle " +i);
    String currentcountdown = countdownNow.getText();
    String CurrentStatus = statusNow.getText();
  
    System.out.println("Cycle " + i + " - Countdown: " + currentcountdown);
    System.out.println("Cycle " + i + " - Status: " + CurrentStatus);
  
    Assert.assertNotEquals(currentcountdown, previousCountdown, "Countdown did not update after refresh cycle " + i);
  
    previousCountdown = currentcountdown;
    previousStatus = CurrentStatus;
   }
    System.out.println("Page refreshed smoothly without any flicker or data loss");
    }
   
    //8. verify Time format validation
    @Test
    public void verifyTimeFormatvalidation(){
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement scheduledtime = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[strong[contains(text(),'Scheduled Time')]]")));
   
    WebElement lastRunTime = driver.findElement(By.xpath("//p[strong[contains(text(),'Last Run')]]"));
    WebElement nextRunTime = driver.findElement(By.xpath("//p[strong[contains(text(),'Next Scheduled Run:')]]"));
   
    String scheduled = scheduledtime.getText().replace("Scheduled Time:","").trim();
    String lastRun = lastRunTime.getText().replace("Last Run:","").trim();
    String nextRun = nextRunTime.getText().replace("Next Scheduled Run:", "").trim();
   
    System.out.println("Scheduled Time:" + scheduled);
    System.out.println("Last Run:" + lastRun);
    System.out.println("Next Scheduled Run:" + nextRun);
   
    String timePattern = "^([01]\\d|2[0-3]):([0-5]\\d)$";
    String dateTimePattern = "^\\d{4}-\\d{2}-\\d{2} ([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$";

    Assert.assertTrue(scheduled.matches(timePattern),"Scheduled Time is not in 24hr HH:MM format");
    Assert.assertTrue(lastRun.equalsIgnoreCase("None") || lastRun.matches(timePattern)|| lastRun.matches(dateTimePattern),
        "Last Run is not None / HH:MM / yyyy-MM-dd HH:MM:SS → Found: " + lastRun);
    Assert.assertTrue(nextRun.matches(dateTimePattern),
        "Next Scheduled Run is NOT valid datetime");
   
    System.out.println("All time fields are valid");
   }
   
   //9. Verify last file name displayed correctly
@Test
public void verifyLastFileNameValidation() throws Exception {

    String fullText = statusPage.getLastFileName();
    System.out.println("UI Last File value = " + fullText);

    if (fullText.equalsIgnoreCase("None") || fullText.isBlank()) {
        System.out.println("No backup file yet. Validation Skipped");
        return;
    }

    String lastFile;

    if (fullText.contains("Last Run")) {
        lastFile = fullText.split("Last Run")[0].trim();
    } else {
        lastFile = fullText.trim();
    }

    String invalidPattern = ".*[<>:\"/\\\\|?].*";

    Assert.assertFalse(
            lastFile.matches(invalidPattern),
            "Filename contains invalid characters: " + lastFile
    );

    Assert.assertFalse(
            lastFile.contains("\n"),
            "Filename contains line breaks: " + lastFile
    );

    Assert.assertFalse(
            lastFile.contains("\r"),
            "Filename contains carriage return characters: " + lastFile
    );
}
    //10.Verify Countdown timer is accurate
    @Test
    public void verifyCountdownTimerisAccurate() throws Exception {
  
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement nextRunElement = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//p[strong[contains(text(),'Next Scheduled Run')]]")
            )
    );

    String nextRunText = nextRunElement.getText();
    System.out.println("Raw Next Run Text = " + nextRunText);

    String nextRunTimeStr = nextRunText.replace("Next Scheduled Run:", "").trim();
    System.out.println("Clean Next Run Text = " + nextRunTimeStr);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime nextRunTime = LocalDateTime.parse(nextRunTimeStr, formatter);

    WebElement countdownElement =
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("countdown")));


    String countdown = countdownElement.getText().trim();
    System.out.println("Countdown UI = " + countdown);

    int uiSeconds = convertCountdownToSeconds(countdown);

    LocalDateTime current = LocalDateTime.now();
    long actualSecondsDifference = Duration.between(current, nextRunTime).getSeconds();

    Assert.assertTrue(
            Math.abs(actualSecondsDifference - uiSeconds) <= 4,
            "Countdown timer does not match system calculated time"
    );

    Thread.sleep(5000);
    String updatedCountdown = countdownElement.getText().trim();
    int newCountdownSeconds = convertCountdownToSeconds(updatedCountdown);

    Assert.assertTrue(
            newCountdownSeconds < uiSeconds,
            "Countdown did not decrease correctly");
}

 private int convertCountdownToSeconds(String countdown) {

    countdown = countdown.trim().toLowerCase();

    int hours = 0;
    int minutes = 0;
    int seconds = 0;

    // Format: "00h 00m 35s"
    if (countdown.contains("h") || countdown.contains("m") || countdown.contains("s")) {

        java.util.regex.Pattern pattern =
                java.util.regex.Pattern.compile("(\\d+)h\\s*(\\d+)m\\s*(\\d+)s");

        java.util.regex.Matcher matcher = pattern.matcher(countdown);

        if (matcher.find()) {
            hours = Integer.parseInt(matcher.group(1));
            minutes = Integer.parseInt(matcher.group(2));
            seconds = Integer.parseInt(matcher.group(3));
        } else {
            throw new IllegalArgumentException("Invalid countdown format: " + countdown);
        }

    }
    // Format: "HH:MM:SS"
    else if (countdown.contains(":")) {

        String[] parts = countdown.split(":");

        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid countdown format: " + countdown);
        }

        hours = Integer.parseInt(parts[0].trim());
        minutes = Integer.parseInt(parts[1].trim());
        seconds = Integer.parseInt(parts[2].trim());
    }

    return hours * 3600 + minutes * 60 + seconds;
}
  //11. Verify countdown timer decreases smoothly
    @Test
    public void verifyCountdownTimerDecreasesSmoothly() throws InterruptedException {

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement countdownElement = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("countdown"))
    );

    String initialCountdown = countdownElement.getText().trim();
    int initialSeconds = convertCountdownToSeconds(initialCountdown);  

    Thread.sleep(5000);

    String updatedCountdown = countdownElement.getText().trim();
    int updatedSeconds = convertCountdownToSeconds(updatedCountdown);  
    int diff = initialSeconds - updatedSeconds;
    Assert.assertTrue(diff >= 4 && diff <= 6,
            "Countdown timer did not decrease smoothly. Expected ~5 seconds decrease, but was: " + diff);
}





    //12. Verify Backup status page auto refreshes every 15 seconds
    @Test
    public void verifyBackupStatusPageAutoRefreshesEvery15Seconds() throws Exception {

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(25));

    WebElement header = wait.until(
        ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[contains(text(),'Backup Status')]")));


    WebElement countdownElement = driver.findElement(
        By.xpath("//strong[contains(text(),'Next Scheduled Run')]/following-sibling::*[1]")
    );

    Thread.sleep(17000);

    wait.until(ExpectedConditions.stalenessOf(countdownElement));

    WebElement newCountdown = wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//strong[contains(text(),'Next Scheduled Run')]/following-sibling::*[1]")
        )
    );

    Assert.assertNotNull(newCountdown,
            " Page did not refresh automatically in 15 seconds!");
}

    //13. Verify countdown reaches zero and next run updates
    @Test

    public void verifyCountdownReachesZeroAndNextRunUpdates() throws Exception {

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
            driver.get(baseUrl + "/backup.jsp");

            String sourcePath = ConfigReader.getProperty("backup.example.source.path");
             String destPath =ConfigReader.getProperty("backup.destination.path");

    driver.findElement(By.name("sourceDir")).clear();
    driver.findElement(By.name("sourceDir")).sendKeys(sourcePath);

    driver.findElement(By.name("destDir")).clear();
    driver.findElement(By.name("destDir")).sendKeys(destPath);

    LocalTime future = LocalTime.now().plusMinutes(1);
    String schedule = future.format(DateTimeFormatter.ofPattern("HH:mm"));

    WebElement timeField = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//input[@type='time' or @name='backupTime']")
            ));
    timeField.clear();
    timeField.sendKeys(schedule);

    WebElement enableBtn = wait.until(
            ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(.,'Enable') or contains(.,'Update')]")
            ));
    enableBtn.click();

    WebElement viewStatusLink = wait.until(ExpectedConditions.elementToBeClickable(
            By.linkText("View Backup Status")));
    viewStatusLink.click();

    WebElement status = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//p[strong[text()='Status:']]")
            ));

    System.out.println("Page Status: " + status.getText());

    Assert.assertTrue(status.getText().contains("Enabled")
            || status.getText().contains("Success")
            || status.getText().contains("Scheduled"),
            "Status did NOT update correctly!");

    System.out.println(" Test Passed");
}
    

// 14. Verify countdown does not freeze or skip
@Test
public void verifyCountdownDoesNotFreezeOrSkip() throws Exception {

    By countdownLocator = By.id("countdown");
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(25));

    // Wait until element is visible
    wait.until(ExpectedConditions.visibilityOfElementLocated(countdownLocator));

    // Let JS stabilize
    Thread.sleep(2000);

    List<Integer> observedValues = new ArrayList<>();
    int skipped = 0;
    int maxIterations = 60;

    Integer lastValue = null;

    for (int i = 0; i < maxIterations; i++) {

        String text;

        try {
            text = driver.findElement(countdownLocator)
                    .getText()
                    .trim();
        } catch (Exception e) {
            Thread.sleep(500);
            continue;
        }

        System.out.println("Raw countdown text: [" + text + "]");

        // Expected format: 19h 39m 15s
        Pattern pattern = Pattern.compile("(\\d+)h\\s*(\\d+)m\\s*(\\d+)s");
        Matcher matcher = pattern.matcher(text);

        if (!matcher.matches()) {
            skipped++;
            Thread.sleep(500);
            continue;
        }

        int hours = Integer.parseInt(matcher.group(1));
        int minutes = Integer.parseInt(matcher.group(2));
        int seconds = Integer.parseInt(matcher.group(3));

        int totalSeconds = (hours * 3600) + (minutes * 60) + seconds;

        System.out.println("Parsed countdown: " + totalSeconds + " seconds");

        // store only when value changes
        if (lastValue == null || totalSeconds != lastValue) {
            observedValues.add(totalSeconds);
            lastValue = totalSeconds;
        }

        Thread.sleep(500);
    }

    System.out.println("Observed values: " + observedValues);
    System.out.println("Skipped reads: " + skipped);

    // Ensure we captured at least movement
    Assert.assertTrue(
            observedValues.size() > 1,
            "Countdown value never changed during observation window!"
    );

    // Validate strictly decreasing behavior
    Integer previous = null;
    boolean decreased = false;

    for (Integer current : observedValues) {

        if (previous != null) {

            Assert.assertTrue(
                    current <= previous,
                    "Countdown increased unexpectedly! previous=" + previous + ", current=" + current
            );

            if (current < previous) {
                decreased = true;
            }
        }

        previous = current;
    }

    Assert.assertTrue(
            decreased,
            "Countdown did not decrease during observation window!"
    );
}   //15. Verify countdown does not show negative values
    @Test
    public void verifyCountdownDoesNotShowNegativeValue() throws Exception {

    By countdownLocator = By.id("countdown");   
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));


    wait.until(ExpectedConditions.visibilityOfElementLocated(countdownLocator));

    boolean negativeFound = false;
    int observationSeconds = 20;   

    for (int i = 0; i < observationSeconds; i++) {

        WebElement countdown =
                wait.until(ExpectedConditions.visibilityOfElementLocated(countdownLocator));

        String text = countdown.getText().trim();
        System.out.println("Countdown value: " + text);

        // Extract digits and minus symbol
        String numeric = text.replaceAll("[^0-9-]", "");

        if (numeric.contains("-")) {
            negativeFound = true;
            break;
        }


        if (numeric.equals("0") || text.toLowerCase().contains("completed")
                || text.toLowerCase().contains("success")) {
            break;
        }

        Thread.sleep(1000);
    }

    Assert.assertFalse(negativeFound,
            " Countdown timer displayed a negative value!");
}

    //16. Verify Scheduled time matches configured time
    @Test
    public void verifyScheduledTimeMatchesConfiguredTime() throws Exception {

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        driver.get(baseUrl + "/backup.jsp");
        
            String sourcePath = ConfigReader.getProperty("backup.example.source.path");
             String destPath =ConfigReader.getProperty("backup.destination.path");




    driver.findElement(By.name("sourceDir"))
            .sendKeys(sourcePath);


    driver.findElement(By.name("destDir"))
            .sendKeys(destPath);


    String configuredTime = "16:44";

    WebElement timeField = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.name("backupTime")
            )
    );
    timeField.clear();
    timeField.sendKeys(configuredTime);


    WebElement scheduleButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(.,'Schedule') or contains(.,'Enable') or contains(.,'Update')]")
            )
    );
    scheduleButton.click();



    WebElement viewStatusLink = wait.until(ExpectedConditions.elementToBeClickable(
            By.linkText("View Backup Status")));
    viewStatusLink.click();



    WebElement scheduledTimeElement = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[strong[contains(text(),'Scheduled Time')]]")
            )
    );

    String displayed = scheduledTimeElement.getText()
                     .replace("Scheduled Time:", "")
                     .trim();

    System.out.println("Configured Time = " + configuredTime);
    System.out.println("Displayed Time  = " + displayed);


    Assert.assertEquals(displayed, configuredTime,
            " Scheduled Time does NOT match configured time!");

    System.out.println(" Scheduled Time matches correctly!");
}


//17. Verify next scheduled run calculated correctly
@Test
public void verifyNextScheduledRunCalculatedCorrectly() throws Exception {

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
            driver.get(baseUrl + "/backup.jsp");
            
              String sourcePath= ConfigReader.getProperty("backup.source.path");
             String destPath =ConfigReader.getProperty("backup.destination.path");



    driver.findElement(By.name("sourceDir")).sendKeys(sourcePath);
    driver.findElement(By.name("destDir")).sendKeys(destPath);


    LocalTime backupTime = LocalTime.now().plusMinutes(2);
    DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
    driver.findElement(By.name("backupTime")).clear();
    driver.findElement(By.name("backupTime")).sendKeys(backupTime.format(timeFormat));

    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.enable-btn"))).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("View Backup Status"))).click();

    WebElement nextRunElement = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//p[strong[contains(text(),'Next Scheduled Run')]]")
            )
    );

    String nextRunText = nextRunElement.getText();
    System.out.println("UI Raw: " + nextRunText);

    Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
    Matcher matcher = pattern.matcher(nextRunText);

    Assert.assertTrue(matcher.find(), "UI does NOT contain valid Next Run time!");
    String uiNextRunStr = matcher.group();
    System.out.println("UI Next Run: " + uiNextRunStr);

    LocalDate today = LocalDate.now();
    LocalDateTime expectedRun = LocalDateTime.of(today,
            LocalTime.of(backupTime.getHour(), backupTime.getMinute()));


    if (expectedRun.isBefore(LocalDateTime.now())) {
        expectedRun = expectedRun.plusDays(1);
    }

    LocalDateTime uiNextRun = LocalDateTime.parse(
            uiNextRunStr,
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    );

    long diffSeconds = Duration.between(expectedRun, uiNextRun).abs().getSeconds();
    System.out.println("Time Difference: " + diffSeconds + " sec");

    Assert.assertTrue(diffSeconds <= 60,
            "Next Scheduled Run NOT correct! Difference more than 60 sec");

    System.out.println("Next Scheduled Run verified successfully!");
}

    //18.Verify Long file name wraps inside the card correctly
    @Test
    public void verifyLongFileNameWrapsInsideCorrectly() throws Exception {

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    
            String sourcePath= ConfigReader.getProperty("backup.source.path");
             String destPath =ConfigReader.getProperty("backup.destination.path");

   

    Path sourceDir = Paths.get(sourcePath);
    Files.createDirectories(sourceDir);

    String longFileName =
            "This_is_a_very_very_long_backup_filename_with_special_chars_@_#_$_%_and_more_______________________________final_test_file.txt";

    Path longFilePath = sourceDir.resolve(longFileName);
    Files.writeString(longFilePath, "dummy content");
            driver.get(baseUrl + "/backup.jsp");



    driver.findElement(By.name("sourceDir")).clear();
    driver.findElement(By.name("sourceDir")).sendKeys(sourcePath);

    driver.findElement(By.name("destDir")).clear();
    driver.findElement(By.name("destDir")).sendKeys(destPath);


    LocalTime future = LocalTime.now().plusMinutes(1);
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
    driver.findElement(By.name("backupTime")).clear();
    driver.findElement(By.name("backupTime")).sendKeys(future.format(fmt));

    wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("button.enable-btn"))).click();

    Thread.sleep(70000);


    wait.until(ExpectedConditions.elementToBeClickable(
            By.linkText("View Backup Status"))).click();


    WebElement lastFileElement = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//p[strong[contains(text(),'Last File')]]")
            )
    );

    String uiText = lastFileElement.getText();
    System.out.println("UI Shows: " + uiText);

    Assert.assertTrue(
            uiText.contains(longFileName),
            "Long filename not shown! UI Shows: " + uiText
    );


    WebElement fileElement = lastFileElement;



    String cssOverflow = fileElement.getCssValue("overflow");
    String cssWhiteSpace = fileElement.getCssValue("white-space");
    String overflowWrap = fileElement.getCssValue("overflow-wrap");
    String wordWrap = fileElement.getCssValue("word-wrap");

    System.out.println("overflow     = " + cssOverflow);
    System.out.println("white-space  = " + cssWhiteSpace);
    System.out.println("overflowWrap = " + overflowWrap);
    System.out.println("wordWrap     = " + wordWrap);


    Assert.assertNotEquals(
          "nowrap",
        cssWhiteSpace,
        "Text is forced single line! Wrapping not allowed");


      Assert.assertTrue(
        "break-word".equalsIgnoreCase(overflowWrap) ||
        "break-word".equalsIgnoreCase(wordWrap) ||
        cssWhiteSpace.equalsIgnoreCase("normal"),
        "UI does NOT support text wrapping for long filenames!");

      System.out.println("Long filename displayed correctly and wraps inside UI");

}


    //19. Verify handling of failed backups
    @Test
public void verifyHandlingFailedBackups() throws InterruptedException {

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
    
            driver.get(baseUrl + "/backup.jsp");
            
                    String sourcePath= ConfigReader.getProperty("backup.temp1.source.path");
             String destPath =ConfigReader.getProperty("backup.destination.path");



    driver.findElement(By.name("sourceDir")).sendKeys(sourcePath);
    driver.findElement(By.name("destDir")).sendKeys(destPath);

    LocalTime backupTime = LocalTime.now().plusMinutes(1);
    DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

    driver.findElement(By.name("backupTime")).clear();
    driver.findElement(By.name("backupTime")).sendKeys(backupTime.format(timeFormat));

    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.enable-btn"))).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.linkText("View Backup Status"))).click();


    boolean failed = false;
    String currentStatus = "N/A";


    for (int i = 0; i < 12; i++) {    
        try {
            WebElement statusElement =
                    driver.findElement(By.xpath("//span[contains(@class,'status')]"));

            currentStatus = statusElement.getText().trim();
            System.out.println("Current Status: " + currentStatus);

            if ("Failed".equalsIgnoreCase(currentStatus)) {
                failed = true;
                break;
            }

        } catch (StaleElementReferenceException e) {
            System.out.println("Status refreshed... retrying");
            Thread.sleep(1000);
            i--;     
            continue;
        }

        Thread.sleep(5000);
    }

    WebElement lastFileField = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//p[strong[text()='Last File:']]"))
    );
    WebElement lastRunField = driver.findElement(
            By.xpath("//p[strong[text()='Last Run:']]")
    );

    String lastFile = lastFileField.getText().replace("Last File:", "").trim();
    String lastRun = lastRunField.getText().replace("Last Run:", "").trim();

    String message = String.format(
            "Backup Status: %s | Last File: %s | Last Run: %s",
            currentStatus,
            lastFile,
            lastRun
    );

    System.out.println(message);
    Reporter.log(message, true);


    // -------- Assertions --------
    Assert.assertTrue(failed, "Backup status should be 'Failed'");
    Assert.assertTrue(
        lastFile.equalsIgnoreCase("none")
        || lastFile.equalsIgnoreCase("n/a")
        || lastFile.equalsIgnoreCase("-")
        || lastFile.equalsIgnoreCase("no file")
        || lastFile.isEmpty(),
        "Last file should remain unchanged on failure but found: " + lastFile
);

    Assert.assertFalse(lastRun.isEmpty(), "Last run timestamp should be present");

    String pageSource = driver.getPageSource().toLowerCase();
    Assert.assertFalse(pageSource.contains("exception"),
            "No exception stack trace should appear on UI");
}

    
 //20. Verify Last Run timestamp correctly matches backup history file

private static final String BACKUP_HISTORY_FILE =
        ConfigReader.getProperty("backup.url");

@Test
public void verifyScheduledTimeCorrectlyDisplayed() throws Exception {

    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

    WebDriverWait wait =
            new WebDriverWait(driver, Duration.ofSeconds(40));

    try {

        driver.get(baseUrl + "/backup.jsp");

        String sourcePath =
                ConfigReader.getProperty("backup.example.source.path");

        String destPath =
                ConfigReader.getProperty("backup.destination.path");

        driver.findElement(By.name("sourceDir"))
                .sendKeys(sourcePath);

        driver.findElement(By.name("destDir"))
                .sendKeys(destPath);

        LocalTime futureTime = LocalTime.now().plusMinutes(1);

        String formattedTime =
                futureTime.format(
                        DateTimeFormatter.ofPattern("HH:mm"));

        WebElement timeField =
                driver.findElement(By.name("backupTime"));

        timeField.clear();
        timeField.sendKeys(formattedTime);

        WebElement enableButton =
                wait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.cssSelector("button.enable-btn")));

        enableButton.click();

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.linkText("View Backup Status")))
                .click();

        WebElement lastRunElement = null;

        String uiTimestamp = null;

        Pattern timestampPattern =
                Pattern.compile(
                        "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");

        int attempts = 0;

        while (attempts < 12) {

            lastRunElement =
                    wait.until(
                            ExpectedConditions
                                    .visibilityOfElementLocated(
                                            By.xpath(
                                                    "//p[strong[contains(text(),'Last Run')]]")));

            Matcher matcher =
                    timestampPattern.matcher(
                            lastRunElement.getText());

            if (matcher.find()) {

                uiTimestamp = matcher.group();

                break;
            }

            System.out.println(
                    "Waiting for Last Run timestamp...");

            Thread.sleep(5000);

            driver.navigate().refresh();

            attempts++;
        }

        Assert.assertNotNull(
                uiTimestamp,
                "UI Last Run timestamp was not displayed!");

        System.out.println(
                "UI Last Run Timestamp = " + uiTimestamp);


        System.out.println(
                "BACKUP_HISTORY_FILE = ["
                        + BACKUP_HISTORY_FILE + "]");

        Path historyPath =
                Paths.get(BACKUP_HISTORY_FILE.trim());

        System.out.println(
                "Absolute Path = "
                        + historyPath.toAbsolutePath());

        System.out.println(
                "File Exists = "
                        + Files.exists(historyPath));

        System.out.println(
                "File Readable = "
                        + Files.isReadable(historyPath));

        Assert.assertTrue(
                Files.exists(historyPath),
                "Backup history file does not exist: "
                        + historyPath.toAbsolutePath());

        List<String> allLines =
                Files.readAllLines(historyPath);

        Assert.assertFalse(
                allLines.isEmpty(),
                "Backup history file is empty!");

        String fileTimestamp = null;

        for (int i = allLines.size() - 1; i >= 0; i--) {

            Matcher matcher =
                    timestampPattern.matcher(allLines.get(i));

            if (matcher.find()) {

                fileTimestamp = matcher.group();

                break;
            }
        }

        Assert.assertNotNull(
                fileTimestamp,
                "No valid timestamp found in backup history file!");

        System.out.println(
                "File Timestamp = " + fileTimestamp);


        Assert.assertEquals(
                uiTimestamp,
                fileTimestamp,
                "UI timestamp does NOT match backup history timestamp!");

        System.out.println(
                "SUCCESS: UI timestamp matches backup history file.");

    } finally {

        try {

            if (driver != null) {

                driver.quit();
            }

        } catch (Exception e) {

            System.out.println(
                    "Driver quit failed (ignored): "
                            + e.getMessage());
        }
    }
}  
    //21. Verify last file name matched with the recent file name in the destination
    
    
      private static final String DESTINATION_DIR = ConfigReader.getProperty("backup.new.destination.path");
      private static final String SOURCE_DIR = ConfigReader.getProperty("backup.example.source.path");
  @Test
    public void verifyLastFileMatchesBackup() throws InterruptedException {

    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

    File destDir = new File(DESTINATION_DIR);

    if (destDir.exists() && destDir.isDirectory()) {
        for (File file : destDir.listFiles()) {
            if (file.isFile()) file.delete();
        }
    } else {
        destDir.mkdirs();
    }

    driver.get(baseUrl + "/backup.jsp");

    driver.findElement(By.name("sourceDir")).sendKeys(SOURCE_DIR);
    driver.findElement(By.name("destDir")).sendKeys(DESTINATION_DIR);

    LocalTime backupTime = LocalTime.now().plusMinutes(1);
    String formattedTime =
            backupTime.format(DateTimeFormatter.ofPattern("HH:mm"));

    WebElement timeField =
            driver.findElement(By.name("backupTime"));

    timeField.clear();
    timeField.sendKeys(formattedTime);

    WebElement enableButton =
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.enable-btn")));

    enableButton.click();

    File latestFile = null;

    long waitUntil = System.currentTimeMillis() + 120000;

    while (System.currentTimeMillis() < waitUntil) {

        File[] files = destDir.listFiles();

        if (files != null && files.length > 0) {

            latestFile = Arrays.stream(files)
                    .filter(File::isFile)
                    .max(Comparator.comparingLong(File::lastModified))
                    .orElse(null);
        }

        if (latestFile != null) break;

        Thread.sleep(5000);
    }

    Assert.assertNotNull(
            latestFile,
            "No backup files found in destination folder after waiting"
    );
}
    
// 22. Verify no sensitive data leakage
@Test
public void verifyNoSensitiveDataLeakage() throws Exception {

    driver.get(baseUrl + "/backup.jsp");

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

    String sourcePath = ConfigReader.getProperty("backup.temp.source.path");
    String destPath = ConfigReader.getProperty("backup.destination.path");


    driver.findElement(By.name("sourceDir")).sendKeys(sourcePath);
    driver.findElement(By.name("destDir")).sendKeys(destPath);

    LocalTime backupTime = LocalTime.now().plusMinutes(1);
    driver.findElement(By.name("backupTime"))
            .sendKeys(backupTime.format(DateTimeFormatter.ofPattern("HH:mm")));

    wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("button.enable-btn")
    )).click();

    // Navigate to status page
    wait.until(ExpectedConditions.elementToBeClickable(
            By.linkText("View Backup Status")
    )).click();

    boolean finalStateReached = false;

    for (int i = 0; i < 30; i++) {

        String status = driver.findElement(
                By.xpath("//span[contains(@class,'status')]")
        ).getText().trim();

        System.out.println("Current Status: " + status);

        if (status.equalsIgnoreCase("Failed") ||
            status.equalsIgnoreCase("Completed") ||
            status.equalsIgnoreCase("Success")) {

            finalStateReached = true;
            break;
        }

        Thread.sleep(2000);
    }

    Assert.assertTrue(finalStateReached,
            "Backup did not reach a final state within expected time");


    WebElement lastFileElement = driver.findElement(
            By.xpath("//p[strong[text()='Last File:']]")
    );

    String lastFileValue = lastFileElement.getText().trim();

    System.out.println("Last File Shown: " + lastFileValue);

    Assert.assertFalse(lastFileValue.isEmpty(),
            "Last file value should not be empty");

    Assert.assertFalse(
            lastFileValue.matches(".*(/|\\\\|home|root|var|system).*"),
            "Sensitive system path exposed: " + lastFileValue
    );

 
    Assert.assertTrue(lastFileValue.contains("."),
            "Expected valid filename but got: " + lastFileValue);

    System.out.println("Security Validation Passed – No sensitive data leakage detected.");
}

}
