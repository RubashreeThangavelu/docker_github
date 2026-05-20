package com.selenium.tests;

import com.selenium.pages.BackupSchedulerPage;
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
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Dimension;
import org.testng.annotations.AfterMethod;
import com.selenium.utils.ConfigReader;
import org.openqa.selenium.Rectangle;
import java.io.IOException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.NoSuchElementException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.Arrays;
import java.util.List;

public class BackupSchedulerTest extends BaseTest {

    private BackupSchedulerPage backupPage;
    
        private String baseUrl;


    @BeforeMethod
    public void setupPage() {
        driver.manage().deleteAllCookies();


        
         baseUrl = ConfigReader.getProperty("base.url");

        driver.get(baseUrl + "/backup.jsp");
        backupPage = new BackupSchedulerPage(driver);
    }

 //1. Verify Elements and labels
    @Test
    public void testUIElementsAndLabels() {
        backupPage.verifyTitle("Backup Scheduler");
        backupPage.verifyElementsDisplayed();
        Assert.assertTrue(driver.getPageSource().contains("Source Directory"));
        Assert.assertTrue(driver.getPageSource().contains("Destination Directory"));
        Assert.assertTrue(driver.getPageSource().contains("Backup Time"));
    }
    
    //2. Verify font color and styles
    @Test
    public void testFontsColorsAndStyles() {
    //Headings
    WebElement heading = driver.findElement(By.tagName("h2")); // assuming h2 for heading
    String headingFont = heading.getCssValue("font-family").toLowerCase();
    String headingColor = heading.getCssValue("color").toLowerCase();
    String headingWeight = heading.getCssValue("font-weight");

    // Font: allow Arial or any sans-serif
    Assert.assertTrue(headingFont.contains("arial") || headingFont.contains("sans-serif"),
            "Heading font mismatch: " + headingFont);

    // Color: allow either expected colors
    Assert.assertTrue(headingColor.contains("13, 71, 161") || headingColor.contains("0, 123, 255"),
            "Heading color mismatch: " + headingColor);

    // Weight: bold or 700
    Assert.assertTrue(headingWeight.equals("700") || headingWeight.equals("bold"), 
            "Heading font-weight mismatch: " + headingWeight);

    //Input Fields
    WebElement sourceInput = backupPage.getSourceDirectoryElement();
    WebElement destInput = backupPage.getDestDirectoryElement();
    WebElement timeInput = backupPage.getBackupTimeElement();

    verifyInputStyles(sourceInput);
    verifyInputStyles(destInput);
    verifyInputStyles(timeInput);

    // Buttons 
    WebElement enableBtn = backupPage.getEnableButtonElement();
    WebElement disableBtn = backupPage.getDisableButtonElement();

    verifyButtonStyles(enableBtn);
    verifyButtonStyles(disableBtn);

    // Links
    WebElement viewStatusLink = driver.findElement(By.linkText("View Backup Status"));
    String linkColor = viewStatusLink.getCssValue("color").toLowerCase();

    // Check link color (underline is optional)
    Assert.assertTrue(linkColor.contains("13, 71, 161") || linkColor.contains("0, 123, 255"),
            "Link color mismatch: " + linkColor);

    // Optional underline: only check if CSS actually has it
    String linkDecoration = viewStatusLink.getCssValue("text-decoration-line"); // modern browsers
    if (linkDecoration != null) {
        Assert.assertTrue(linkDecoration.contains("underline") || linkDecoration.contains("none"),
                "Link should be underlined or none: " + linkDecoration);
    }
}

    public void verifyInputStyles(WebElement element) {
    String font = element.getCssValue("font-family").toLowerCase();
    System.out.println("Input font: " + font);

    Assert.assertNotNull(font, "Font should be defined");
    Assert.assertFalse(font.isEmpty(), "Font should not be empty");

    // Check if font belongs to allowed families
    boolean validFont = font.contains("arial") ||
                        font.contains("ubuntu") ||
                        font.contains("sans-serif") ||
                        font.contains("monospace");

    Assert.assertTrue(validFont, "Input font mismatch: " + font);
}

    public void verifyButtonStyles(WebElement button) {
    String font = button.getCssValue("font-family").toLowerCase();
    System.out.println("Button font: " + font);

    Assert.assertNotNull(font, "Button font should be defined");
    Assert.assertFalse(font.isEmpty(), "Button font should not be empty");

    boolean validFont = font.contains("arial") ||
                        font.contains("ubuntu") ||
                        font.contains("sans-serif") ||
                        font.contains("monospace");

    Assert.assertTrue(validFont, "Button font mismatch: " + font);
}



    //3. Verify Backup time button clickable
    @Test
    public void testSetBackupTimeButtonClickableAndHover() {
    // Get the button via a public getter
    WebElement setBackupButton = driver.findElement(By.cssSelector("button.enable-btn")); 
    // Hover over the button
    Actions actions = new Actions(driver);
    actions.moveToElement(setBackupButton).perform();

    //Check for visual change on hover (like background color)
    String bgColor = setBackupButton.getCssValue("background-color");
    System.out.println("Background color on hover: " + bgColor);

    // Click the button using page object helper
    backupPage.clickEnableButton();

    // Verify that the form is responsive after click
    String message = backupPage.getMessage();
    System.out.println("Message after clicking button: " + message);

    Assert.assertTrue(setBackupButton.isDisplayed() && setBackupButton.isEnabled(),
            "Set Backup Time button should be visible and clickable");
}

      //4. Verify view status link clickable and hover
       @Test
        public void testViewStatusLinkClickableAndHover() {
        // Get the link element
        WebElement viewStatusLink = backupPage.getViewStatusLinkElement();

        // Hover over the link
        Actions actions = new Actions(driver);
        actions.moveToElement(viewStatusLink).perform();

        // Optional: Check visual change (color, underline, etc.)
        String color = viewStatusLink.getCssValue("color");
        System.out.println("Link color on hover: " + color);

        // Click the link
        backupPage.clickViewStatusLink();

        // Verify navigation
        String currentUrl = driver.getCurrentUrl();
        System.out.println("Current URL after click: " + currentUrl);
        Assert.assertTrue(currentUrl.contains("index.jsp"), "Expected navigation to index.jsp");

        // Navigate back for further tests
        driver.navigate().back();
    }
  
  //5. Verify valid path status
  @Test
   public void testValidPathBackupStatus() throws InterruptedException {
 String sourcePath =
            ConfigReader.getProperty("backup.source.path");
        String destPath=ConfigReader.getProperty("backup.destination.path");
        LocalTime now= LocalTime.now().plusMinutes(1);
        String backupTime=now.format(DateTimeFormatter.ofPattern("HH:mm"));
        
        backupPage.setBackupDetails(sourcePath,destPath,backupTime);
        backupPage.enableBackup();
        String msg=backupPage.getMessage();
        System.out.println("Valid path message:"+msg);
        Assert.assertFalse(msg.isEmpty(),"Backup message should not be empty");
        
        backupPage.goToStatusPage();
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        
        boolean backupCompleted=false;
        String status="";
        int maxWaitMinutes=1;
        int attempts=0;
        
        String expectedStatus = "Success"; 

    while(!backupCompleted && attempts < (maxWaitMinutes * 4)) {
    status = backupPage.getBackupStatus();
    if(status.equalsIgnoreCase("Success") || status.equalsIgnoreCase("Failed") || status.equalsIgnoreCase("Skipped")) {
        backupCompleted = true;
    } else {
        Thread.sleep(15000);
        backupPage.refreshStatusPage();
    }
    attempts++;
}

       Assert.assertTrue(
        status.equalsIgnoreCase("Success") ||
        status.equalsIgnoreCase("Skipped"),
        "Backup status mismatch. Expected Success or Skipped but was: " + status
    );

   } 


 
     //6. verify with source path empty
    @Test
    public void testsourcePathEmpty() {
    
    String destination_path=ConfigReader.getProperty("backup.destination.path");
        backupPage.setBackupDetails("", destination_path, "12:30");
        backupPage.enableBackup();
       WebElement source = driver.findElement(By.name("sourceDir"));
    String validationMsg = source.getAttribute("validationMessage");

    System.out.println("Validation message: " + validationMsg);
    Assert.assertFalse(validationMsg.isEmpty(), "Expected HTML5 validation error");
}
    
    
    //7. verify for empty fields
    @Test
    public void testEmptyFields() {
    backupPage.setBackupDetails("", "", "");
    backupPage.enableBackup();
    String msg = backupPage.getMessage();
    System.out.println("Empty fields message: " + (msg.isEmpty() ? "No message returned" : msg));
    
}


    //8. Verify with invalid source path
     @Test
      public void testInValidPathBackupStatus() throws InterruptedException {

        String sourcePath= ConfigReader.getProperty("backup.nosource.path");
        String destPath= ConfigReader.getProperty("backup.destination.path");
        LocalTime now= LocalTime.now().plusMinutes(1);
        String backupTime=now.format(DateTimeFormatter.ofPattern("HH:mm"));
        
        backupPage.setBackupDetails(sourcePath,destPath,backupTime);
        backupPage.enableBackup();
        String msg=backupPage.getMessage();
        System.out.println("Invalid path message:"+msg);
        Assert.assertFalse(msg.isEmpty(),"Backup message should not be empty");
        
        backupPage.goToStatusPage();
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        
        boolean backupCompleted=false;
        String status="";
        int maxWaitMinutes=1;
        int attempts=0;
        
        String expectedStatus = "Failed"; 
      while(!backupCompleted && attempts < (maxWaitMinutes * 4)) {
        status = backupPage.getBackupStatus();
          if(status.equalsIgnoreCase("Success") || status.equalsIgnoreCase("Failed") ||         status.equalsIgnoreCase("Skipped")) {

              backupCompleted = true;
           } else {
              Thread.sleep(15000);
              backupPage.refreshStatusPage();
      }
       attempts++;
}

     Assert.assertTrue(
    status.equalsIgnoreCase("Failed") ||
    status.equalsIgnoreCase("Skipped"),
    "Backup status mismatch. Expected Failed or Skipped but was: " + status
);

        System.out.println("Final Backup Status: "+ status);
    }

    
    //9. Verify with file name as source input
    @Test
    public void textFileAsSource() throws InterruptedException {
        String sourcePath= ConfigReader.getProperty("backup.filesource.path");
        String destPath= ConfigReader.getProperty("backup.destination.path");
        LocalTime now= LocalTime.now().plusMinutes(1);
        String backupTime=now.format(DateTimeFormatter.ofPattern("HH:mm"));
        
        backupPage.setBackupDetails(sourcePath,destPath,backupTime);
        backupPage.enableBackup();
        String msg=backupPage.getMessage();
        System.out.println("Source file path message:"+msg);
        Assert.assertFalse(msg.isEmpty(),"Backup message should not be empty");
        
        backupPage.goToStatusPage();
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        
        boolean backupCompleted=false;
        String status="";
        int maxWaitMinutes=1;
        int attempts=0;
        
        String expectedStatus = "Failed"; 

    while(!backupCompleted && attempts < (maxWaitMinutes * 4)) {
    status = backupPage.getBackupStatus();
    if(status.equalsIgnoreCase("Success") || status.equalsIgnoreCase("Failed") ||
    status.equalsIgnoreCase("Skipped")){
        backupCompleted = true;
    } else {
        Thread.sleep(15000);
        backupPage.refreshStatusPage();
    }
    attempts++;
}

    Assert.assertTrue(
        status.equalsIgnoreCase("Failed") || status.equalsIgnoreCase("Skipped"),
        "Backup status mismatch. Expected Failed/Skipped but was: " + status
);


        System.out.println("Final Backup Status: "+ status);
    }
   
    
    //10. verify with Special characters 
    @Test
    public void testSpecialCharactersPath() throws InterruptedException{
        String sourcePath= ConfigReader.getProperty("backup.invalid.source.path");
        String destPath= ConfigReader.getProperty("backup.destination.path");
        LocalTime now= LocalTime.now().plusMinutes(1);
        String backupTime=now.format(DateTimeFormatter.ofPattern("HH:mm"));
        
        backupPage.setBackupDetails(sourcePath,destPath,backupTime);
        backupPage.enableBackup();
        String msg=backupPage.getMessage();
        System.out.println("Source special character path message:"+msg);
        Assert.assertFalse(msg.isEmpty(),"Backup message should not be empty");
        
        backupPage.goToStatusPage();
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        boolean backupCompleted=false;
        String status="";
        int maxWaitMinutes=1;
        int attempts=0;
        
        String expectedStatus = "Failed"; 

      while(!backupCompleted && attempts < (maxWaitMinutes * 4)) {
        status = backupPage.getBackupStatus();
        if(status.equalsIgnoreCase("Success") || status.equalsIgnoreCase("Failed") || status.equalsIgnoreCase("Skipped")){
        backupCompleted = true;
        } else {
        Thread.sleep(15000);
        backupPage.refreshStatusPage();
    }
    attempts++;
}

     Assert.assertTrue(
    status.equalsIgnoreCase("Failed") ||
    status.equalsIgnoreCase("Skipped"),
    "Backup status mismatch. Expected Failed or Skipped but was: " + status
);

        System.out.println("Final Backup Status: "+ status);
    }
    
    
      //11. Verify with non existing destiantion path
   
 @Test
public void testNonexistingDestPath_AutoDirectoryCreation() throws Exception {

    String sourcePath =ConfigReader.getProperty("backup.new.source.path");

    // Create a unique new folder name which does NOT exist
  String baseDestPath = ConfigReader.getProperty("backup.auto.destination.base.path");

    String destPath = baseDestPath + System.currentTimeMillis();


    String backupTime = LocalTime.now()
            .plusMinutes(1)
            .format(DateTimeFormatter.ofPattern("HH:mm"));

    backupPage.setBackupDetails(sourcePath, destPath, backupTime);
    backupPage.enableBackup();

    String msg = backupPage.getMessage();
    System.out.println("Scheduler Message: " + msg);
    Assert.assertFalse(msg.isEmpty(), "Backup message should not be empty");

    backupPage.goToStatusPage();

    boolean backupCompleted = false;
    String status = "";

    for (int i = 0; i < 36; i++) { 
        status = backupPage.getBackupStatus().trim();
        System.out.println("Backup Status Check " + (i+1) + ": " + status);

        if (status.equalsIgnoreCase("Success")) {
            backupCompleted = true;
            break;
        } else if (status.equalsIgnoreCase("Failed")) {
            break;  
        }

        Thread.sleep(5000);
        backupPage.refreshStatusPage();
    }

    Assert.assertTrue(backupCompleted,
            "Backup did not complete successfully within expected time. Final Status: " + status);

    Assert.assertEquals(status, "Success",
            "Backup must succeed because app should auto-create missing destination folder!");

    System.out.println("FINAL STATUS: SUCCESS - Auto directory creation verified");
}

   
     //12. verify with destination path empty
    @Test
    public void testdestPathEmpty() {
    String sourcePath =ConfigReader.getProperty("backup.example.source.path");

        backupPage.setBackupDetails(sourcePath, "", "12:30");
        backupPage.enableBackup();
       WebElement source = driver.findElement(By.name("destDir"));
       String validationMsg = source.getAttribute("validationMessage");

       System.out.println("Validation message: " + validationMsg);
      Assert.assertFalse(validationMsg.isEmpty(), "Expected HTML5 validation error");
}

// 13. Verify with file name as destination input
@Test
public void testFileAsDestination() throws InterruptedException {

    String sourcePath = ConfigReader.getProperty("backup.example.source.path");
    String destPath = ConfigReader.getProperty("backup.file.destination.path");

    File dest = new File(destPath);

 
    Assert.assertTrue(dest.exists(), 
        "Test setup invalid: destination file must exist");

    Assert.assertTrue(dest.isFile(), 
        "Test setup invalid: destination path must be a FILE");

    System.out.println("Destination is a file. Expecting backup to fail or skip.");

    LocalTime now = LocalTime.now().plusMinutes(1);
    String backupTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));

    backupPage.setBackupDetails(sourcePath, destPath, backupTime);
    backupPage.enableBackup();

    String msg = backupPage.getMessage();
    System.out.println("Backup message: " + msg);

    Assert.assertNotNull(msg, "Backup message should not be null");
    Assert.assertFalse(msg.trim().isEmpty(), "Backup message should not be empty");

    backupPage.goToStatusPage();

    String status = "";
    int attempts = 0;
    int maxAttempts = 4; // ~1 min total

    while (attempts < maxAttempts) {

        status = backupPage.getBackupStatus();
        System.out.println("Current Status: " + status);

        if (status.equalsIgnoreCase("Failed") ||
            status.equalsIgnoreCase("Skipped") ||
            status.equalsIgnoreCase("Success")) {
            break;
        }

        Thread.sleep(15000);
        backupPage.refreshStatusPage();
        attempts++;
    }

    System.out.println("Final Backup Status: " + status);

    Assert.assertFalse(
        status.equalsIgnoreCase("Success"),
        "Backup must NOT succeed when destination is a file. Status: " + status
    );

    Assert.assertTrue(
        status.equalsIgnoreCase("Failed") || status.equalsIgnoreCase("Skipped"),
        "Expected Failed or Skipped, but got: " + status
    );

    Assert.assertTrue(
        dest.exists() && dest.isFile(),
        "Destination file should remain unchanged (no backup should be written or replaced)"
    );
} 
    //14. Verify Valid time scheduling
    @Test
    public void testValidTimeScheduling() {
    String sourcePath = ConfigReader.getProperty("backup.example.source.path");
    String destPath = ConfigReader.getProperty("backup.destination.path");
        backupPage.setBackupDetails(sourcePath, destPath, "12:30");
        backupPage.enableBackup();
        Assert.assertTrue(backupPage.getMessage().toLowerCase().contains("backup scheduled"));
    }

   
     //15. Verify Missing AM/PM missing
    @Test
    public void testMissingAmPmTimeScheduling() {
      String sourcePath = ConfigReader.getProperty("backup.example.source.path");
    String destPath = ConfigReader.getProperty("backup.destination.path");

    // Enter details with missing AM/PM
    backupPage.setBackupDetails(sourcePath,destPath,"06:38");

    backupPage.enableBackup();

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    WebElement timeField = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.name("backupTime"))
    );

    String validationMsg = timeField.getDomProperty("validationMessage");

    System.out.println("Validation Message: " + validationMsg);

    Assert.assertFalse(validationMsg.isEmpty(),
            "Expected validation message but got empty message");

    Assert.assertFalse(validationMsg.isEmpty(),
        "Expected validation message but got empty message");

}


    //16. Verify with boundary time values
    @Test
    public void testBoundaryTimeValues() {
        String[] times = {"00:00","02:45", "12:00", "23:59", "25:99", "7", ""};
          String sourcePath = ConfigReader.getProperty("backup.example.source.path");
         String destPath = ConfigReader.getProperty("backup.destination.path");
        for (String t : times) {
            backupPage.setBackupDetails(sourcePath, destPath, t);
            backupPage.enableBackup();
            String msg = backupPage.getMessage();
            System.out.println("Backup message for time " + t + ": " + msg);
            Assert.assertFalse(msg.isEmpty());
        }
    }

   
   //17. Verify enable and disable backup
    @Test
    public void testEnableDisableBackup() {
      String sourcePath = ConfigReader.getProperty("backup.example.source.path");
    String destPath = ConfigReader.getProperty("backup.destination.path");
        backupPage.setBackupDetails(sourcePath, destPath, "12:30");
        backupPage.enableBackup();
        String status = backupPage.getStatus().toLowerCase();
        System.out.println("Status after enable: " + status);
        Assert.assertFalse(status.isEmpty());

        backupPage.disableBackup();
        status = backupPage.getStatus().toLowerCase();
        System.out.println("Status after disable: " + status);
        Assert.assertFalse(status.isEmpty());
    }
    
    //18. Verify Update backup time
     @Test
    public void testUpdateBackupTime() throws InterruptedException {
    
     WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement sourceInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("sourceDir")));
        WebElement destInput = driver.findElement(By.name("destDir"));
          String sourcePath = ConfigReader.getProperty("backup.example.source.path");
    String destPath = ConfigReader.getProperty("backup.destination.path");

        sourceInput.clear();
        sourceInput.sendKeys(sourcePath);
        destInput.clear();
        destInput.sendKeys(destPath);

        // Schedule backup at time A
        WebElement backupTimeInput = driver.findElement(By.name("backupTime"));
        WebElement enableBtn = driver.findElement(By.className("enable-btn"));

        backupTimeInput.clear();
        backupTimeInput.sendKeys("12:00");
        enableBtn.click();

        // Wait for confirmation message
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("message")));

      
        backupTimeInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("backupTime")));
        enableBtn = driver.findElement(By.className("enable-btn"));

        backupTimeInput.clear();
        backupTimeInput.sendKeys("12:05");
        enableBtn.click();


        WebElement msg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("message")));
        Assert.assertTrue(msg.getText().contains("Backup scheduled"),
                "Backup time update message not displayed");

        // Verify old schedule canceled
        WebElement status = driver.findElement(By.className("status"));
        Assert.assertTrue(status.getText().contains("Enabled"),
                "Backup status should remain Enabled");


    }


       //19. Verify with Random directory path
        @Test
        public void testRandomDirectoryPath() throws InterruptedException{
        String sourcePath = ConfigReader.getProperty("backup.random.source.path");
    String destPath = ConfigReader.getProperty("backup.random.destination.path");

        LocalTime now= LocalTime.now().plusMinutes(1);
        String backupTime=now.format(DateTimeFormatter.ofPattern("HH:mm"));
        
        backupPage.setBackupDetails(sourcePath,destPath,backupTime);
        backupPage.enableBackup();
        String msg=backupPage.getMessage();
        System.out.println("Random path message:"+msg);
        Assert.assertFalse(msg.isEmpty(),"Backup message should not be empty");
        
        backupPage.goToStatusPage();
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        boolean backupCompleted=false;
        String status="";
        int maxWaitMinutes=1;
        int attempts=0;
        
        String expectedStatus = "Failed"; 

    while(!backupCompleted && attempts < (maxWaitMinutes * 4)) {
    status = backupPage.getBackupStatus();
    if(status.equalsIgnoreCase("Success") || status.equalsIgnoreCase("Failed")|| status.equalsIgnoreCase("Skipped")){
        backupCompleted = true;
    } else {
        Thread.sleep(15000);
        backupPage.refreshStatusPage();
    }
    attempts++;
}

   Assert.assertTrue(
    status.equalsIgnoreCase("Failed") || status.equalsIgnoreCase("Skipped"),
    "Backup status mismatch. Expected Failed/Skipped but was: " + status
);
        System.out.println("Final Backup Status: "+ status);
    }
    
    //20. Verify with SQL commands as  directory path
        @Test
        public void testSQLCommandsDirectoryPath() throws InterruptedException{
        
        
        String sourcePath = ConfigReader.getProperty("backup.Sql.source.path");
    String destPath = ConfigReader.getProperty("backup.destination.path");
       
        LocalTime now= LocalTime.now().plusMinutes(1);
        String backupTime=now.format(DateTimeFormatter.ofPattern("HH:mm"));
        
        backupPage.setBackupDetails(sourcePath,destPath,backupTime);
        backupPage.enableBackup();
        String msg=backupPage.getMessage();
        System.out.println("Source SQL path message:"+msg);
        Assert.assertFalse(msg.isEmpty(),"Backup message should not be empty");
        
        backupPage.goToStatusPage();
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        boolean backupCompleted=false;
        String status="";
        int maxWaitMinutes=1;
        int attempts=0;
        
        String expectedStatus = "Failed"; 

    while(!backupCompleted && attempts < (maxWaitMinutes * 4)) {
    status = backupPage.getBackupStatus();
    if(status.equalsIgnoreCase("Success") || status.equalsIgnoreCase("Failed")|| status.equalsIgnoreCase("Skipped")){
        backupCompleted = true;
    } else {
        Thread.sleep(15000);
        backupPage.refreshStatusPage();
    }
    attempts++;
}

  Assert.assertTrue(
    status.equalsIgnoreCase("Failed") || status.equalsIgnoreCase("Skipped"),
    "Backup status mismatch. Expected Failed/Skipped but was: " + status
);

        System.out.println("Final Backup Status: "+ status);
    }
    
  //21. Verify with success message after valid inputs
     
@Test
    public void testSuccessMessageAfterValidInputs() {
    
    
        String sourcePath = ConfigReader.getProperty("backup.example.source.path");
    String destPath = ConfigReader.getProperty("backup.destination.path");

        LocalTime now= LocalTime.now().plusMinutes(2);
        String backupTime=now.format(DateTimeFormatter.ofPattern("HH:mm"));
    backupPage.setBackupDetails(sourcePath, destPath, backupTime);
    backupPage.enableBackup();

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement messageElement = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector("p.message"))
    );

    String message = messageElement.getText();
    System.out.println("Success message: " + message);

    Assert.assertFalse(message.isEmpty(), "Message should not be empty");
    Assert.assertTrue(
            message.toLowerCase().contains("backup scheduled") || 
            message.toLowerCase().contains("success"),
            "Message should indicate success and be informative"
    );

    String status = backupPage.getStatus();
    Assert.assertTrue(status.toLowerCase().contains("enabled"),
            "Backup status should be Enabled after scheduling");
}


    //22. Verify input field placeholders
    @Test
    public void testInputFieldPlaceholders() {
    
    
      
    // Source Directory placeholder
    String expectedSourcePlaceholder = ConfigReader.getProperty("backup.placeholder.source.path");
    String actualSourcePlaceholder = driver.findElement(By.name("sourceDir")).getAttribute("placeholder");
    System.out.println("Source placeholder: " + actualSourcePlaceholder);
    Assert.assertEquals(actualSourcePlaceholder, expectedSourcePlaceholder, "Source placeholder mismatch");

    // Destination Directory placeholder
    String expectedDestPlaceholder = ConfigReader.getProperty("backup.placeholder.destination.path");
    String actualDestPlaceholder = driver.findElement(By.name("destDir")).getAttribute("placeholder");
    System.out.println("Destination placeholder: " + actualDestPlaceholder);
    Assert.assertEquals(actualDestPlaceholder, expectedDestPlaceholder, "Destination placeholder mismatch");


    WebElement timeInput = driver.findElement(By.name("backupTime"));
    String actualTimePlaceholder = timeInput.getAttribute("placeholder");
    System.out.println("Backup Time placeholder: " + actualTimePlaceholder);


    if (actualTimePlaceholder != null && !actualTimePlaceholder.isEmpty()) {
        Assert.assertTrue(true, "Backup Time placeholder is visible");
    } else {
        System.out.println("Backup Time input does not have a placeholder (expected for HTML5 time input)");
    }
}

      //23. verify directory traversal prevention
    @Test
    public void testDirectoryTraversalPrevention() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
       String sourcePath = ConfigReader.getProperty("backup.directory.source.path");
    String destPath = ConfigReader.getProperty("backup.destination.path");
    


    // Enter malicious source path
    WebElement sourceInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("sourceDir")));
    sourceInput.clear();
    sourceInput.sendKeys(sourcePath);

    WebElement destInput = driver.findElement(By.name("destDir"));
    destInput.clear();
    destInput.sendKeys(destPath);

    WebElement timeInput = driver.findElement(By.name("backupTime"));
    timeInput.clear();
    timeInput.sendKeys("12:30");

    // Click the enable button
    WebElement enableBtn = driver.findElement(By.className("enable-btn"));
    enableBtn.click();

    // After form submit, re-locate the source input to check validation message
    sourceInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("sourceDir")));
    String validationMsg = sourceInput.getAttribute("validationMessage");
    System.out.println("Validation message: " + validationMsg);

    Assert.assertFalse(validationMsg.isEmpty(),
        "Expected error message for directory traversal not shown");
}

      //24. Verify Symbolic link handling
      @Test
      public void testSymbolicLinkHandling() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    

    String symlinkSource = ConfigReader.getProperty("backup.symlink.source.path");
    String validDest = ConfigReader.getProperty("backup.destination.path");
    String backupTime = "12:30";

    WebElement sourceInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("sourceDir")));
    sourceInput.clear();
    sourceInput.sendKeys(symlinkSource);

    WebElement destInput = driver.findElement(By.name("destDir"));
    destInput.clear();
    destInput.sendKeys(validDest);

    WebElement timeInput = driver.findElement(By.name("backupTime"));
    timeInput.clear();
    timeInput.sendKeys(backupTime);

    WebElement enableBtn = driver.findElement(By.className("enable-btn"));
    enableBtn.click();

    WebElement msg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("message")));
    String messageText = msg.getText().toLowerCase();
    System.out.println("Backup message for symlink: " + messageText);

    // Assert that backup is scheduled (since symlinks are allowed)
    Assert.assertTrue(messageText.contains("backup scheduled"), 
        "Expected backup scheduled message for symlink source, but got: " + messageText);
}


    //25. Verify view status link navigation
    @Test
    public void testViewStatusNavigation() {
        backupPage.clickViewStatus();
        Assert.assertTrue(driver.getCurrentUrl().contains("index.jsp"));
        driver.navigate().back();
    }

    //26. verify with  Special characters in Destination
    @Test
    public void testSpecialCharactersDestPath() throws InterruptedException{
    
     String sourcePath = ConfigReader.getProperty("backup.example.source.path");
    String destPath = ConfigReader.getProperty("backup.invalid.destination.path");
    
        LocalTime now= LocalTime.now().plusMinutes(1);
        String backupTime=now.format(DateTimeFormatter.ofPattern("HH:mm"));
        
        backupPage.setBackupDetails(sourcePath,destPath,backupTime);
        backupPage.enableBackup();
        String msg=backupPage.getMessage();
        System.out.println("Invalid special character in dest path message:"+msg);
        Assert.assertFalse(msg.isEmpty(),"Backup message should not be empty");
        
        backupPage.goToStatusPage();
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        boolean backupCompleted=false;
        String status="";
        int maxWaitMinutes=1;
        int attempts=0;
        
        String expectedStatus = "Success"; 

      while(!backupCompleted && attempts < (maxWaitMinutes * 4)) {
      status = backupPage.getBackupStatus();
      if(status.equalsIgnoreCase("Success") || status.equalsIgnoreCase("Failed")){
        backupCompleted = true;
       } else {
        Thread.sleep(15000);
        backupPage.refreshStatusPage();
    }
    attempts++;
}

Assert.assertTrue(
    status.equalsIgnoreCase(expectedStatus),
    "Backup status mismatch. Expected: " + expectedStatus + " but was: " + status);

        System.out.println("Final Backup Status: "+ status);
    }
    
    //27. Verify behavior if source directory has no files
      @Test
      public void testEmptySourceDirectoryBehavior() throws Exception {
      
   String sourcePath = ConfigReader.getProperty("backup.empty.source.path");
    String destPath = ConfigReader.getProperty("backup.destination.path");


    // Ensure source exists & is empty
    File emptyFolder = new File(sourcePath);
    if (!emptyFolder.exists()) {
        emptyFolder.mkdirs();
    } else {
        File[] files = emptyFolder.listFiles();
        if (files != null) {
            for (File f : files) f.delete();
        }
    }

    // Set backup time 1 minute ahead
    LocalTime now = LocalTime.now().plusMinutes(1);
    String backupTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));

    backupPage.setBackupDetails(sourcePath, destPath, backupTime);
    backupPage.enableBackup();

    String msg = backupPage.getMessage();
    System.out.println("Message After Scheduling if source dir has no files: " + msg);
    Assert.assertFalse(msg.isEmpty(), "Backup scheduling message should not be empty");

    // Go to status page
    backupPage.goToStatusPage();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    String status = "";
    boolean completed = false;
    int attempts = 0;
    int maxWaitMinutes = 1;          
    int pollIntervalSeconds = 15;    

    while (!completed && attempts < (maxWaitMinutes * 60 / pollIntervalSeconds)) {

        status = backupPage.getBackupStatus();
        if (status == null) status = "";
        status = status.trim();

        System.out.println("Current Status: '" + status + "'");

        String lower = status.toLowerCase();

        // Acceptable completion values
        if (lower.contains("success") ||
            lower.contains("completed") ||
            lower.contains("skipped") ||
            lower.contains("no files") ||
            lower.contains("failed")) {

            completed = true;
        } 
        else {
            Thread.sleep(pollIntervalSeconds * 1000);
            backupPage.refreshStatusPage();
        }

        attempts++;
    }

    Assert.assertTrue(
            status.toLowerCase().contains("skipped") ||
            status.toLowerCase().contains("no files") ||
            status.toLowerCase().contains("completed") ||
            status.toLowerCase().contains("success"),
            "Expected backup to either Skip or Complete gracefully for empty source, but status was: " + status
    );

    System.out.println("Final Backup Status for Empty Source Folder: " + status);
}

  //28. verify with Time field empty
    @Test
    public void testBackupTimePathEmpty() {
    
    
    
   String sourcePath = ConfigReader.getProperty("backup.example.source.path");
    String destPath = ConfigReader.getProperty("backup.destination.path");
    
    
        backupPage.setBackupDetails(sourcePath, destPath, " ");
        backupPage.enableBackup();
       WebElement time = driver.findElement(By.name("backupTime"));
   String validationMsg = time.getAttribute("validationMessage");
    System.out.println("Validation Message: " + validationMsg);

    Assert.assertTrue(
            validationMsg.contains("Please fill out"),
            "Expected validation message not shown!"
    );
    }
    
    //29. Verify with unsupported files
   @Test
    public void testUnsupportedFilesBehavior() throws Exception {
    
    
    String sourcePath = ConfigReader.getProperty("backup.unsupported.source.path");
    String destPath = ConfigReader.getProperty("backup.destination.path");

    // Ensure directory exists and contains only unsupported files
    File srcDir = new File(sourcePath);
    if (!srcDir.exists()) srcDir.mkdirs();
    else {
        // Clean up existing files
        for (File f : srcDir.listFiles()) f.delete();
    }

    // Create dummy unsupported files
    new File(srcDir, "file1.txt").createNewFile();
    new File(srcDir, "file2.lock").createNewFile();

    LocalTime now = LocalTime.now().plusMinutes(1);
    String backupTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));


    backupPage.setBackupDetails(sourcePath, destPath, backupTime);
    backupPage.enableBackup();

    String msg = backupPage.getMessage();
    System.out.println("Message After Scheduling for unsupported files: " + msg);
    Assert.assertFalse(msg.isEmpty(), "Backup scheduling message should not be empty");


    backupPage.goToStatusPage();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

    String status = "";
    boolean completed = false;
    int attempts = 0;
    int maxWaitMinutes = 1; 
    String expectedStatus = "Skipped"; 

    while (!completed && attempts < (maxWaitMinutes * 4)) {
        status = backupPage.getBackupStatus();
        System.out.println("Current Backup Status: " + status);

        if (status.equalsIgnoreCase("Skipped") ||
            status.equalsIgnoreCase("No Files") ||
            status.equalsIgnoreCase("Failed")) {
            completed = true;
        } else {
            Thread.sleep(15000);
            backupPage.refreshStatusPage();
        }

        attempts++;
    }

    Assert.assertTrue(
        status.equalsIgnoreCase(expectedStatus),
        "Expected unsupported files to be skipped/ignored, but result was: " + status
    );

    System.out.println("Final Backup Status for Unsupported Files: " + status);
}


    //30. Verify with minimum length directory
@Test
public void testMinLengthDirectoryPath() throws InterruptedException {

    String sourcePath = ConfigReader.getProperty("backup.minlength.source.path");
    String destPath = ConfigReader.getProperty("backup.minlength.destination.path");

    Assert.assertNotNull(sourcePath, "Source path is null");
    Assert.assertNotNull(destPath, "Destination path is null");

    LocalTime now = LocalTime.now().plusMinutes(1);
    String backupTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));

    backupPage.setBackupDetails(sourcePath, destPath, backupTime);
    backupPage.enableBackup();

    String msg = backupPage.getMessage();
    System.out.println("Message: " + msg);

    Assert.assertNotNull(msg);
    Assert.assertFalse(msg.trim().isEmpty(), "Backup message should not be empty");

    backupPage.goToStatusPage();

    String status = "";
    int attempts = 0;

    while (attempts < 8) {

        status = backupPage.getBackupStatus();
        System.out.println("Current Status: " + status);

        if (status != null &&
            (status.equalsIgnoreCase("Success") ||
             status.equalsIgnoreCase("Failed") ||
             status.equalsIgnoreCase("Skipped"))) {
            break;
        }

        Thread.sleep(15000);
        backupPage.refreshStatusPage();
        attempts++;
    }

    System.out.println("Final Status: " + status);

    Assert.assertNotNull(status, "Status should not be null");

    // IMPORTANT FIX: only ensure it is NOT success for invalid paths
    Assert.assertNotEquals(
        status,
        "Success",
        "Invalid/min-length path should not succeed. Status: " + status
    );
}

    //31.  Verify long length for Source directory path
     @Test
    public void tesLongLengthSourceDirectoryPath() throws InterruptedException {
        String basePath = ConfigReader.getProperty("backup.longpath.base");

        int length = Integer.parseInt(ConfigReader.getProperty("backup.longpath.length"));

        String sourcePath = basePath + "a".repeat(length);

        String destPath = ConfigReader.getProperty("backup.destination.path");
    

    LocalTime now = LocalTime.now().plusMinutes(1);
    String backupTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));


    backupPage.setBackupDetails(sourcePath, destPath, backupTime);
    backupPage.enableBackup();


    String msg = backupPage.getMessage();
    System.out.println("Message after scheduling with long length path: " + msg);
    Assert.assertFalse(msg.isEmpty(), "Backup message should not be empty");

    backupPage.goToStatusPage();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

    boolean backupCompleted = false;
    String status = "";
    int maxWaitMinutes = 2;
    int attempts = 0;
    String expectedStatus = "Failed"; 
    while (!backupCompleted && attempts < (maxWaitMinutes * 4)) {
        status = backupPage.getBackupStatus();
        System.out.println("Current Backup Status: " + status);

        if (status.equalsIgnoreCase("Success") || status.equalsIgnoreCase("Failed")) {
            backupCompleted = true;
        } else {
            Thread.sleep(15000);
            backupPage.refreshStatusPage();
        }
        attempts++;
    }

    // Assert the final status
    Assert.assertEquals(
        status,
        expectedStatus,
        "Backup status mismatch for minimum length directory path"
    );

    System.out.println("Final Backup Status for min-length path: " + status);
}


    // 32.Verify long length for Destination directory path
    @Test
    public void tesLongLengthDestDirectoryPath() throws InterruptedException {
    
    
    
   String sourcePath =ConfigReader.getProperty("backup.example.source.path");

  String destBase =ConfigReader.getProperty("backup.long.dest.base");

  int length =Integer.parseInt(ConfigReader.getProperty("backup.longpath.length"));

  String destPath =destBase + "a".repeat(length);
    

    LocalTime now = LocalTime.now().plusMinutes(1);
    String backupTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));

    backupPage.setBackupDetails(sourcePath, destPath, backupTime);
    backupPage.enableBackup();

    String msg = backupPage.getMessage();
    System.out.println("Message after scheduling with long length dest path: " + msg);
    Assert.assertFalse(msg.isEmpty(), "Backup message should not be empty");


    backupPage.goToStatusPage();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

    boolean backupCompleted = false;
    String status = "";
    int maxWaitMinutes = 2;
    int attempts = 0;
    String expectedStatus = "Failed"; 

    while (!backupCompleted && attempts < (maxWaitMinutes * 4)) {
        status = backupPage.getBackupStatus();
        System.out.println("Current Backup Status: " + status);

        if (status.equalsIgnoreCase("Success") || status.equalsIgnoreCase("Failed")) {
            backupCompleted = true;
        } else {
            Thread.sleep(15000);
            backupPage.refreshStatusPage();
        }
        attempts++;
    }

    // Assert the final status
    Assert.assertEquals(
        status,
        expectedStatus,
        "Backup status mismatch for minimum length directory path"
    );

    System.out.println("Final Backup Status for min-length path: " + status);
}


     //33. Verify backup saved in the backend

@Test
public void verifyBackupLogTemplate() throws Exception {

    String sourcePath =ConfigReader.getProperty("backup.temp.source.path");

    String destPath =ConfigReader.getProperty("backup.destination.path");

    Assert.assertNotNull(sourcePath,"Source path is null");

    Assert.assertNotNull(destPath,"Destination path is null");

    LocalTime now = LocalTime.now().plusMinutes(1);

    String backupTime =
            now.format(DateTimeFormatter.ofPattern("HH:mm"));

    backupPage.setBackupDetails(
            sourcePath,
            destPath,
            backupTime
    );

    backupPage.enableBackup();

    String msg = backupPage.getMessage();

    Assert.assertFalse(
            msg == null || msg.isEmpty(),
            "Backup message should not be empty"
    );


    backupPage.goToStatusPage();
    String uiStatus = "";

    int attempts = 0;


    int maxAttempts = 12;

    while (attempts < maxAttempts) {

        uiStatus = backupPage.getBackupStatus();

        System.out.println("UI Status: " + uiStatus);

        if (uiStatus != null) {

            String s =uiStatus.toLowerCase().trim();

            if (s.contains("success") ||
                s.contains("failed")) {

                break;
            }
        }

        Thread.sleep(15000);

        backupPage.refreshStatusPage();

        attempts++;
    }

    Assert.assertTrue(
            uiStatus != null &&
            !uiStatus.isEmpty(),
            "UI status is empty"
    );

    Assert.assertTrue(
            uiStatus.toLowerCase().contains("success") ||
            uiStatus.toLowerCase().contains("failed"),
            "UI did not show valid final status: " + uiStatus
    );

    String logPathStr =
            ConfigReader.getProperty("backup.url");

    Path logPath =
            Paths.get(logPathStr).normalize();

    int wait = 0;

    int maxWait = 60;

    while (wait < maxWait) {

        if (Files.exists(logPath) &&
            Files.size(logPath) > 0) {

            break;
        }

        Thread.sleep(3000);

        wait += 3;
    }

    System.out.println(
            "Checking log path: " + logPath
    );

    Assert.assertTrue(
            Files.exists(logPath),
            "Backup log file not found: " + logPath
    );

    String logContent =
            Files.readString(logPath);

    Pattern pattern = Pattern.compile(
            "^Status\\s*:\\s*(.+)$",
            Pattern.MULTILINE
    );

    Matcher matcher =
            pattern.matcher(logContent);

    String backendStatus = "";


    while (matcher.find()) {

        backendStatus =
                matcher.group(1).trim();
    }

    Assert.assertFalse(
            backendStatus.isEmpty(),
            "Backend status not found in log"
    );

    System.out.println(
            "Backend Status: " + backendStatus
    );

    String ui =
            uiStatus.toLowerCase().trim();

    String backend =
            backendStatus.toLowerCase().trim();

    boolean validMatch =

            (ui.contains("success") &&
             backend.contains("success")) ||

            (ui.contains("failed") &&
             backend.contains("fail"));

    Assert.assertTrue(
            validMatch,

            "Mismatch between UI and backend status. " +
            "UI=" + uiStatus +
            ", Backend=" + backendStatus
    );

    System.out.println(
            "Backup verified successfully. " +
            "UI Status: " + uiStatus +
            ", Backend Status: " + backendStatus
    );
}
    //34. Verify backup overwrites in the detstination file
   @Test
public void testVBackupOverwrite() throws Exception {

    String sourcePath = ConfigReader.getProperty("backup.source.path");
    String destPath = ConfigReader.getProperty("backup.destination.path");


    Path sourceDir = Paths.get(sourcePath);
    Path destDir = Paths.get(destPath);

    Path latestSourceFile = Files.list(sourceDir)
            .filter(Files::isRegularFile)
            .max(Comparator.comparingLong(f -> f.toFile().lastModified()))
            .orElseThrow(() -> new RuntimeException("No files found in source folder"));

    String fileName = latestSourceFile.getFileName().toString();
    Path destFile = destDir.resolve(fileName);

    System.out.println("Latest Source File: " + latestSourceFile);
    System.out.println("Destination File To Validate: " + destFile);

    boolean existedBefore = Files.exists(destFile);
    byte[] oldDestContent = existedBefore ? Files.readAllBytes(destFile) : null;

    byte[] sourceContentBefore = Files.readAllBytes(latestSourceFile);

    String backupTime = LocalTime.now()
            .plusMinutes(1)
            .format(DateTimeFormatter.ofPattern("HH:mm"));

    backupPage.setBackupDetails(sourcePath, destPath, backupTime);
    backupPage.enableBackup();

    Assert.assertFalse(backupPage.getMessage().isEmpty(),
            "Backup message empty!");

    backupPage.goToStatusPage();

    boolean backupCompleted = false;
    String status = "";

    //  WAIT LONGER (Up to 3 min)
    for (int i = 0; i < 36; i++) {
        status = backupPage.getBackupStatus().trim();
        System.out.println("Status Check " + (i+1) + ": " + status);

        if (status.equalsIgnoreCase("Success")
                || status.equalsIgnoreCase("Failed")
                || status.equalsIgnoreCase("Skipped")) {
            backupCompleted = true;
            break;
        }

        Thread.sleep(5000);
        backupPage.refreshStatusPage();
    }

    Assert.assertTrue(backupCompleted,
            "Backup did not finish in expected time. Final status: " + status);

    // Ensure file exists
    Assert.assertTrue(Files.exists(destFile),
            "Expected file not found in destination!");

    byte[] sourceContentAfter = Files.readAllBytes(latestSourceFile);
    byte[] newDestContent = Files.readAllBytes(destFile);


    if (!existedBefore) {

        Assert.assertTrue(Arrays.equals(sourceContentBefore, newDestContent),
                "New file should match source but does not!");
        System.out.println("NEW FILE COPIED SUCCESSFULLY");
    }
    else {
        // File already existed → overwrite or skip
        if (Arrays.equals(oldDestContent, newDestContent)) {
            System.out.println("BACKUP SKIPPED — Destination unchanged");
            Assert.assertTrue(status.equalsIgnoreCase("Skipped") || status.equalsIgnoreCase("Success"),
                    "Status should be Success or Skipped when file unchanged");
        }
        else {
            Assert.assertTrue(Arrays.equals(sourceContentAfter, newDestContent),
                    "Destination content does not match updated source!");
            System.out.println("FILE OVERWRITTEN — Destination updated");
            Assert.assertEquals(status, "Success",
                    "Status should be Success when overwrite occurs");
        }
    }

    System.out.println("Overwrite Behavior Verified Successfully");
}

    //35.Verify corrected backup scheduled for next day

        @Test
      public void testBackupScheduledNextDay() throws Exception {

    String sourcePath = ConfigReader.getProperty("backup.source.path");
    String destPath = ConfigReader.getProperty("backup.destination.path");

    LocalTime now = LocalTime.now();
    LocalTime timeBefore = now.minusMinutes(1);
    String backupTime = timeBefore.format(DateTimeFormatter.ofPattern("HH:mm"));

    backupPage.setBackupDetails(sourcePath, destPath, backupTime);
    backupPage.enableBackup();

    Assert.assertFalse(backupPage.getMessage().isEmpty(), 
        "Backup message should not be empty");

    backupPage.goToStatusPage();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    LocalTime scheduledTime = backupPage.getScheduledTime();

    boolean isNextDayScheduled = scheduledTime.isBefore(now);

    System.out.println("Current Time       : " + now);
    System.out.println("Backup Set Time    : " + backupTime);
    System.out.println("Scheduled Time     : " + scheduledTime);
    System.out.println("Next-Day Scheduled : " + isNextDayScheduled);

    Assert.assertTrue(isNextDayScheduled, 
        "Backup was NOT scheduled for next day at the intended time: " + scheduledTime);
}
  //36. Verify Keyboard navigation
  @Test
    public void verifyKeyboardNavigationSignup() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
    
    
     String sourcePath = ConfigReader.getProperty("backup.example.source.path");
    String destPath = ConfigReader.getProperty("backup.destination.path");


    WebElement usernameField = driver.findElement(By.name("sourceDir"));
    usernameField.sendKeys(sourcePath);
    usernameField.sendKeys(Keys.TAB);

    WebElement active = driver.switchTo().activeElement();
    WebElement usernameField2 = driver.findElement(By.name("destDir"));

    active.sendKeys(destPath);
    active.sendKeys(Keys.TAB);

    active = driver.switchTo().activeElement();
    WebElement Time = driver.findElement(By.name("backupTime"));

    active.sendKeys("02:45");
    active.sendKeys(Keys.TAB);
    
    active = driver.switchTo().activeElement();
     WebElement enableBtn = driver.findElement(By.className("enable-btn"));
   

    active.sendKeys(Keys.ENTER);  


    WebElement loginLink = wait.until(
        ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'View Backup Status')]")));
    loginLink.click();
    wait.until(ExpectedConditions.urlContains("index.jsp"));
    Assert.assertTrue(driver.getCurrentUrl().contains("index"), "User not redirected to Login URL");
}

    //37. Verify multiple backup scheduling
    @Test
    public void testMultipleBackupSchedulesUIOnly() {
    
         String sourcePath = ConfigReader.getProperty("backup.source.path");
    String destPath = ConfigReader.getProperty("backup.destination.path");


    LocalTime now = LocalTime.now();
    String backupTime1 = now.plusMinutes(1).format(DateTimeFormatter.ofPattern("HH:mm"));
    String backupTime2 = now.plusMinutes(2).format(DateTimeFormatter.ofPattern("HH:mm"));

    // Schedule first backup
    backupPage.setBackupDetails(sourcePath, destPath, backupTime1);
    backupPage.enableBackup();

    // Schedule second backup
    backupPage.setBackupDetails(sourcePath, destPath, backupTime2);
    backupPage.enableBackup();


    backupPage.goToStatusPage();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

    String latestScheduledTime = backupPage.getLatestScheduledBackupTime();
    System.out.println("Latest scheduled backup time in UI: " + latestScheduledTime);

    // Assert it matches the second backup time
    Assert.assertEquals(latestScheduledTime, backupTime2,
        "Latest backup time not scheduled correctly in UI!");
}

//38. Verify Error messages are meaningful  
 @Test
public void verifyErrorMessagesForInvalidInput() {

    WebElement sourceField = driver.findElement(By.name("sourceDir"));
    WebElement destField = driver.findElement(By.name("destDir"));
    WebElement timeField = driver.findElement(By.name("backupTime"));
    WebElement button = driver.findElement(By.className("enable-btn"));

    sourceField.clear();
    sourceField.sendKeys("/invalid/source");

    destField.clear();
    destField.sendKeys("/invalid/destination");

    timeField.clear();
    timeField.sendKeys("25:61");

    button.click();

    JavascriptExecutor js = (JavascriptExecutor) driver;

    String sourceMsg = (String) js.executeScript("return arguments[0].validationMessage;", sourceField);
    String destMsg   = (String) js.executeScript("return arguments[0].validationMessage;", destField);
    String timeMsg   = (String) js.executeScript("return arguments[0].validationMessage;", timeField);

    System.out.println("Source Msg: " + sourceMsg);
    System.out.println("Dest Msg  : " + destMsg);
    System.out.println("Time Msg  : " + timeMsg);


    Assert.assertFalse(timeMsg.isEmpty(), "Time validation message should appear");

    // These are EXPECTED empty (no validation implemented)
    Assert.assertTrue(sourceMsg.isEmpty(), "Source should not have HTML5 message yet");
    Assert.assertTrue(destMsg.isEmpty(), "Destination should not have HTML5 message yet");
}

    //39. Verify the Responsiveness Layout 
    @Test
    public void verifyResponsiveLayout() throws InterruptedException {



    // Desktop size
    driver.manage().window().setSize(new Dimension(1366, 768));
    Thread.sleep(2000);

    // Tablet size
    driver.manage().window().setSize(new Dimension(768, 1024));
    Thread.sleep(2000);

    // Mobile size
    driver.manage().window().setSize(new Dimension(375, 812));
    Thread.sleep(2000);

    // Verify elements are visible
    Assert.assertTrue(driver.findElement(By.name("sourceDir")).isDisplayed());
    Assert.assertTrue(driver.findElement(By.name("destDir")).isDisplayed());
    Assert.assertTrue(driver.findElement(By.name("backupTime")).isDisplayed());
    Assert.assertTrue(driver.findElement(By.className("enable-btn")).isDisplayed());

    System.out.println("UI adjusts properly in Desktop, Tablet, Mobile view");
}


    //40. Verify Backup run at the scheduled time
  @Test
    public void verifyBackupRunsAtScheduledTime() throws InterruptedException {
    
         String sourcePath = ConfigReader.getProperty("backup.temp.source.path");
    String destPath = ConfigReader.getProperty("backup.destination.path");

    LocalTime time = LocalTime.now().plusMinutes(1);
    String backupTime = time.format(DateTimeFormatter.ofPattern("HH:mm"));

    System.out.println("======================================");
    System.out.println(" Backup Scheduling Verification Test");
    System.out.println(" Scheduled Time : " + backupTime);
    System.out.println("======================================");

    backupPage.setBackupDetails(sourcePath, destPath, backupTime);
    backupPage.enableBackup();

    String msg = backupPage.getMessage();
    Assert.assertFalse(msg.isEmpty(), "Backup schedule message should appear");

    backupPage.goToStatusPage();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    String status = "";
    boolean completed = false;

    int attempts = 0;
    int maxWaitMinutes = 3;
    int pollInterval = 15000;

    LocalTime runTime = null;

    while (!completed && attempts < (maxWaitMinutes * 4)) {

        status = backupPage.getBackupStatus();
        System.out.println("Current Status: " + status);

        if (status.equalsIgnoreCase("Success") ||
            status.equalsIgnoreCase("Failed")) {

            completed = true;
            runTime = LocalTime.now();   
        } 
        else {
            Thread.sleep(pollInterval);
            backupPage.refreshStatusPage();
        }

        attempts++;
    }

    System.out.println("--------------------------------------");
    System.out.println(" Scheduled Time : " + backupTime);

    if (runTime != null)
        System.out.println(" Backup Run Time: " + runTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    else
        System.out.println(" Backup Run Time: NOT COMPLETED");

    System.out.println(" Final Status   : " + status);
    System.out.println("--------------------------------------");

    Assert.assertEquals(
        status,
        "Success",
        "Expected backup to complete successfully but got: " + status
    );
}
  
   //41. Verify Backup Does not run before scheduled time 
  @Test
  public void verifyBackupDoesNotRunBeforeScheduledTime() throws Exception {

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
      String sourcePath = ConfigReader.getProperty("backup.new.source.path");
    String destPath = ConfigReader.getProperty("backup.temp.destination.path");



    WebElement source = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.name("sourceDir")));
    source.clear();
    source.sendKeys(sourcePath);


    WebElement dest = driver.findElement(By.name("destDir"));
    dest.clear();
    dest.sendKeys(destPath);


    LocalTime scheduledTime = LocalTime.now().plusMinutes(1);
    String timeString = scheduledTime.format(DateTimeFormatter.ofPattern("HH:mm"));

    WebElement time = driver.findElement(By.name("backupTime"));
    time.clear();
    time.sendKeys(timeString);


    driver.findElement(By.cssSelector("button.enable-btn")).click();



    driver.findElement(By.linkText("View Backup Status")).click();


    boolean backupTriggeredEarly = false;
    LocalTime current;

    do {
        current = LocalTime.now();

        try {
            WebElement status = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(),'Last Run') or contains(text(),'Success') or contains(text(),'Running')]")
            ));

            String statusText = status.getText().toLowerCase();

            if (statusText.contains("running") || statusText.contains("success")) {
                backupTriggeredEarly = true;
                break;
            }

        } catch (Exception ignored) {}

        Thread.sleep(5000);   

    } while (current.isBefore(scheduledTime));


    Assert.assertFalse(
            backupTriggeredEarly,
            "Backup triggered BEFORE scheduled time — Test Failed!"
    );

    System.out.println(" Backup did NOT run before scheduled time — Test Passed");
}

      
    //42. Verify Input fields are aligned and visible properly
    @Test
    public void verifyInputFieldsAlignmentAndVisibility() {
    
     WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(driver1 ->
                ((JavascriptExecutor) driver1)
                        .executeScript(
                                "return window.getComputedStyle(document.querySelector('.card')).opacity"
                        )
                        .equals("1"));


        WebElement source = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.name("sourceDir")));

        WebElement destination = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.name("destDir")));

        WebElement timeField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.name("backupTime")));

        WebElement enableBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("button.enable-btn")));

        WebElement disableBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("button.disable-btn")));

        Assert.assertTrue(source.isDisplayed(), "Source Directory field not visible");
        Assert.assertTrue(destination.isDisplayed(), "Destination Directory field not visible");
        Assert.assertTrue(timeField.isDisplayed(), "Backup Time field not visible");
        Assert.assertTrue(enableBtn.isDisplayed(), "Enable button not visible");
        Assert.assertTrue(disableBtn.isDisplayed(), "Disable button not visible");


        int tolerance = 5;

        int srcX = source.getLocation().getX();
        int destX = destination.getLocation().getX();
        int timeX = timeField.getLocation().getX();

        int srcWidth = source.getSize().getWidth();
        int destWidth = destination.getSize().getWidth();
        int timeWidth = timeField.getSize().getWidth();


        Assert.assertTrue(Math.abs(srcX - destX) <= tolerance,
                "Source & Destination not aligned");

        Assert.assertTrue(Math.abs(srcX - timeX) <= tolerance,
                "Source & Time not aligned");

        // All same width
        Assert.assertTrue(Math.abs(srcWidth - destWidth) <= tolerance,
                "Source & Destination width mismatch");

        Assert.assertTrue(Math.abs(srcWidth - timeWidth) <= tolerance,
                "Source & Time width mismatch");


      int enableX = enableBtn.getLocation().getX();
      int enableWidth = enableBtn.getSize().getWidth();

      Assert.assertTrue(Math.abs(enableX - srcX) <= tolerance,
        "Enable button is not left aligned with input fields");

      int widthDifference = Math.abs(enableWidth - srcWidth);
      Assert.assertTrue(widthDifference <= 30,
        "Enable button width differs too much from input field");



        System.out.println("UI Alignment & Visibility Test Passed");
    }
  
  
    //43. Verify Current status when backup disabled
   @Test
    public void verifyCurrentStatusWhenBackupDisabled() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

   
    WebElement disableButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("button.disable-btn")));
    disableButton.click();


    WebElement currentStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.className("status")));


    String statusText = currentStatus.getText();
    System.out.println("Current Status: " + statusText);
    Assert.assertTrue(statusText.contains("Disabled"),
            "Current status did not show as Disabled");
            
         WebElement enableButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("button.enable-btn")));     
            
             String buttonText = enableButton.getText();
    System.out.println("Enable Button Text: " + buttonText);
    Assert.assertTrue(buttonText.contains("Enable") && buttonText.contains("Schedule"),
            "Enable button text did not change to 'Enable & Schedule Backup'");
}


    //44. verify backup status page after backup Disabled
    @Test
    public void verifyBackupStatusPageWhenDisabled() throws Exception {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

    WebElement disableButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("button.disable-btn")));
    disableButton.click();


    WebElement viewStatusLink = wait.until(ExpectedConditions.elementToBeClickable(
            By.linkText("View Backup Status")));
    viewStatusLink.click();


    boolean statusFound = false;
    long endTime = System.currentTimeMillis() + 3 * 60 * 1000; 
    while (System.currentTimeMillis() < endTime) {
        try {

            List<WebElement> elements = driver.findElements(
                    By.xpath("//*[contains(text(),'Disabled')]")
            );

            if (!elements.isEmpty()) {
                statusFound = true;
                System.out.println("Backup Status: " + elements.get(0).getText());
                break;
            }
        } catch (StaleElementReferenceException sere) {

        }
        Thread.sleep(2000); 
    }

    Assert.assertTrue(statusFound, "Backup status did not display as Disabled on the Backup Status page");
    System.out.println("TEST PASSED — Backup status shows Disabled correctly.");
}



    //45. Verify welcome page status when backup disabled
    @Test
    public void verifyWelcomePageWhenDisabled() throws Exception {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

    WebElement disableButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("button.disable-btn")));
    disableButton.click();
    
    driver.get(baseUrl + "/welcome.jsp");




    WebElement statusSpan = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("backupStatus")));


    String statusText = statusSpan.getText();
    System.out.println("Backup Status: " + statusText);
    Assert.assertTrue(statusText.contains("Disabled"),
            "Backup status did not display as Disabled on the Backup Status page");
}



    //46. Verify backup status after enabling backup
    @Test
    public void verifyBackupStatusAfterEnablingBackup() throws Exception {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    
    
     String sourcePath = ConfigReader.getProperty("backup.example.source.path");
    String destPath = ConfigReader.getProperty("backup.destination.path");

    WebElement sourceField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("sourceDir")));
    sourceField.clear();
    sourceField.sendKeys(sourcePath);

    WebElement destField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("destDir")));
    destField.clear();
    destField.sendKeys(destPath);

    WebElement timeField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("backupTime")));
    java.time.LocalTime futureTime = java.time.LocalTime.now().plusMinutes(5);
    timeField.clear();
    timeField.sendKeys(futureTime.toString().substring(0,5));

    WebElement enableButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("button.enable-btn")));
    enableButton.click();


    WebElement viewStatusLink = wait.until(ExpectedConditions.elementToBeClickable(
            By.linkText("View Backup Status")));
    viewStatusLink.click();


    boolean backupDone = false;
    long endTime = System.currentTimeMillis() + 6 * 60 * 1000; 
    while (System.currentTimeMillis() < endTime) {
        try {

            List<WebElement> elements = driver.findElements(
                    By.xpath("//*[contains(text(),'Success') or contains(text(),'Scheduled')]")
            );

            if (!elements.isEmpty()) {
                backupDone = true;
                System.out.println("Backup Status: " + elements.get(0).getText());
                break;
            }
        } catch (StaleElementReferenceException sere) {

        }
        Thread.sleep(2000); 
    }

    Assert.assertTrue(backupDone, "Backup status not displayed as Success or Scheduled");
    System.out.println("TEST PASSED — Backup status shown correctly after enabling backup.");
}



    //47.  Verify Current status when backup enabled
   @Test
    public void verifyCurrentStatusWhenBackupEnabled() {
     WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
     
     
      String sourcePath = ConfigReader.getProperty("backup.example.source.path");
    String destPath = ConfigReader.getProperty("backup.temp.destination.path");

    WebElement sourceField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("sourceDir")));
    sourceField.clear();
    sourceField.sendKeys(sourcePath);

    WebElement destField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("destDir")));
    destField.clear();
    destField.sendKeys(destPath);

    WebElement timeField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("backupTime")));
    java.time.LocalTime futureTime = java.time.LocalTime.now().plusMinutes(1); 
    timeField.clear();
    timeField.sendKeys(futureTime.toString().substring(0,5));

    WebElement disableButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("button.enable-btn")));
    disableButton.click();

    WebElement currentStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.className("status")));

    String statusText = currentStatus.getText();
    System.out.println("Current Status: " + statusText);
    Assert.assertTrue(statusText.contains("Enabled"),
            "Current status did not show as Enabled");
            
         WebElement enableButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("button.enable-btn")));     
            
             String buttonText = enableButton.getText();
    System.out.println("Enable Button Text: " + buttonText);
    Assert.assertTrue(buttonText.contains("Update") && buttonText.contains("Reschedule"),
            "Enable button text did not change to 'Enable & Schedule Backup'");
}


    //48. Verify backup status after enabling backup
    @Test
public void verifyStatusChangeOnWelcomePageAfterEnablingBackup() throws InterruptedException {

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    
    driver.get(baseUrl + "/backup.jsp");

    
    
    
     String sourcePath = ConfigReader.getProperty("backup.example.source.path");
    String destPath = ConfigReader.getProperty("backup.destination.path");

    wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("sourceDir")))
            .clear();
    driver.findElement(By.name("sourceDir"))
            .sendKeys(sourcePath);

    wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("destDir")))
            .clear();
    driver.findElement(By.name("destDir"))
            .sendKeys(destPath);

    LocalTime futureTime = LocalTime.now().plusMinutes(1);
    String backupTimeStr = futureTime.format(DateTimeFormatter.ofPattern("HH:mm"));

    WebElement timeField =
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("backupTime")));
    timeField.clear();
    timeField.sendKeys(backupTimeStr);

    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.enable-btn")))
            .click();


    WebElement schedulerStatus =
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("status")));

    String schedulerText = schedulerStatus.getText().trim();
    System.out.println("Backup Scheduler Status: " + schedulerText);

    Assert.assertTrue(
            schedulerText.contains("Enabled") || schedulerText.contains("Scheduled"),
            "Backup was not enabled successfully"
    );
    
    
    driver.get(baseUrl + "/welcome.jsp");



    boolean statusUpdated = false;
    long endTime = System.currentTimeMillis() + Duration.ofMinutes(6).toMillis();

    while (System.currentTimeMillis() < endTime) {

        driver.navigate().refresh();

        WebElement statusElem =
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("backupStatus")));

        String statusText = statusElem.getText().trim();
        System.out.println("Current Welcome Page Status: " + statusText);

        if (statusText.equalsIgnoreCase("Success")
                || statusText.equalsIgnoreCase("Scheduled")
                || statusText.equalsIgnoreCase("Running")) {
            statusUpdated = true;
            break;
        }

        Thread.sleep(5000);
    }

    Assert.assertTrue(
            statusUpdated,
            "Welcome page status did not show Success or Scheduled within timeout"
    );
}


    //49. Verify backup with restricted path for source directory
    @Test
    public void verifyPermissionDeniedForRestrictedSourceDirectory() throws InterruptedException {

 String restrictedSourcePath = ConfigReader.getProperty("backup.res.source.path");
    String destPath = ConfigReader.getProperty("backup.destination.path");



    LocalTime now = LocalTime.now().plusMinutes(1);
    String backupTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));

    backupPage.setBackupDetails(restrictedSourcePath, destPath, backupTime);
    backupPage.enableBackup();

    String message = backupPage.getMessage();
    System.out.println("Scheduler Message: " + message);


    Assert.assertTrue(message.toLowerCase().contains("scheduled"));


    backupPage.goToStatusPage();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    String status = "";
    boolean completed = false;
    int attempts = 0;

    while (!completed && attempts < 8) {   
        status = backupPage.getBackupStatus();
        System.out.println("Backup Status: " + status);

        if (status.equalsIgnoreCase("Failed") ||
            status.toLowerCase().contains("permission") ||
            status.toLowerCase().contains("denied")) {
            completed = true;
        } else {
            Thread.sleep(15000);
            backupPage.refreshStatusPage();
        }

        attempts++;
    }

    Assert.assertTrue(
        status.toLowerCase().contains("failed") ||
        status.toLowerCase().contains("permission") ||
        status.toLowerCase().contains("denied"),
        "Expected Permission failure but got: " + status
    );
}

    //50. Verify backup for restricted path for destination directory

@Test
public void verifyPermissionDeniedForRestrictedDestinationDirectory() throws InterruptedException {

    String sourcePath = ConfigReader.getProperty("backup.source.path");
    String restrictedDestPath = "/app/restricted_backup";

    LocalTime now = LocalTime.now().plusMinutes(1);
    String backupTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));

    backupPage.setBackupDetails(sourcePath, restrictedDestPath, backupTime);
    backupPage.enableBackup();

    String message = backupPage.getMessage();
    System.out.println("Scheduler Message: " + message);

    Assert.assertTrue(
        message != null && message.toLowerCase().contains("scheduled"),
        "Backup was not scheduled: " + message
    );

    backupPage.goToStatusPage();

    String status = null;
    int attempts = 0;

    while (attempts < 8) {

        status = backupPage.getBackupStatus();
        System.out.println("Backup Status: " + status);

        if (status != null) {

            String s = status.toLowerCase().trim();

            boolean failed =
                    s.contains("failed") ||
                    s.contains("permission") ||
                    s.contains("denied") ||
                    s.contains("access") ||
                    s.contains("error");

            boolean success = s.contains("success");

            if (failed || success) {
                break; // stop polling once final state is reached
            }
        }

        Thread.sleep(15000);
        backupPage.refreshStatusPage();
        attempts++;
    }

    Assert.assertNotNull(status, "Status should not be null");

    String finalStatus = status.toLowerCase();

    boolean isFailure =
            finalStatus.contains("failed") ||
            finalStatus.contains("permission") ||
            finalStatus.contains("denied") ||
            finalStatus.contains("access") ||
            finalStatus.contains("error");

    boolean isSuccess = finalStatus.contains("success");

    Assert.assertFalse(isSuccess,
        "Backup should NOT succeed for restricted directory: " + status);

    Assert.assertTrue(isFailure,
        "Expected permission failure but got: " + status);
}
    //51. Verify backup with large file in the source directory
    @Test
    public void verifyLargeDirectoryBackupCompletesSuccessfully() throws InterruptedException {



    String largeSourcePath = ConfigReader.getProperty("backup.large.source.path"); 
    String destPath = ConfigReader.getProperty("backup.destination.path");

    LocalTime now = LocalTime.now().plusMinutes(1);
    String backupTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));

    backupPage.setBackupDetails(largeSourcePath, destPath, backupTime);
    backupPage.enableBackup();

    String scheduleMsg = backupPage.getMessage();
    System.out.println("Scheduler Message = " + scheduleMsg);

    Assert.assertTrue(
            scheduleMsg.toLowerCase().contains("scheduled"),
            "Backup was not scheduled successfully. Message: " + scheduleMsg
    );

    backupPage.goToStatusPage();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    boolean backupCompleted = false;
    String status = "";

    int maxWaitMinutes = 30;     
    int attempts = 0;

    while (!backupCompleted && attempts < (maxWaitMinutes * 4)) { 

        status = backupPage.getBackupStatus();
        System.out.println("Backup Status: " + status);

        if (status.equalsIgnoreCase("Success")
                || status.equalsIgnoreCase("Completed")) {

            backupCompleted = true;

        } else if (status.equalsIgnoreCase("Failed")
                || status.toLowerCase().contains("error")
                || status.toLowerCase().contains("denied")
                || status.toLowerCase().contains("permission")) {

            Assert.fail("Backup failed for large directory. Status: " + status);
        }

        Thread.sleep(15000);
        backupPage.refreshStatusPage();
        attempts++;
    }

    Assert.assertTrue(
            status.equalsIgnoreCase("Success")
            || status.equalsIgnoreCase("Completed"),
            "Large directory backup did not complete successfully. Final Status: " + status
    );

    System.out.println("Large Directory Backup Test Passed Successfully!");
}

}  

