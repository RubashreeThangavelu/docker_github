package com.selenium.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.annotations.AfterMethod;
import org.openqa.selenium.interactions.Actions;  
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import com.selenium.utils.ConfigReader;
import java.time.Duration;

public class DashboardTest extends BaseTest {

    private WebDriverWait wait;
    private String baseUrl;

    @BeforeMethod
    public void goToDashboard() {
     
        baseUrl = ConfigReader.getProperty("base.url");
        driver.get(baseUrl + "/welcome.jsp");

        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // 1. Verify page background color
    @Test
    public void verifyPageBackgroundColor() {
        String bgColor = driver.findElement(By.tagName("body")).getCssValue("background");
        Assert.assertTrue(bgColor.contains("rgb(227, 242, 253)"), "Background color mismatch");
    }
    
    // 2. Verify font style
    @Test
    public void verifyFontStyles() {
        WebElement heading = driver.findElement(By.tagName("h1"));
        String fontFamily = heading.getCssValue("font-family").toLowerCase();
        Assert.assertTrue(fontFamily.contains("segoe ui"), "Font family mismatch, found: " + fontFamily);
    }
    
    // 3. Verify page title
    @Test
    public void verifyPageTitle() {
        String title = driver.getTitle();
        Assert.assertEquals(title, "Welcome to My Web App", "Dashboard page title mismatch");
    }
    
    // 4. verify page heading
    @Test
    public void verifyPageHeading() {
        String heading = driver.findElement(By.tagName("h1")).getText();
        Assert.assertEquals(heading, "Welcome to Backup Scheduler", "Dashboard heading mismatch");
    }
    
    // 5. verify button is clickable 
    @Test
    public void verifyGoToLoginButtonEffect() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        By buttonLocator = By.xpath("//a[text()='Go to Login Page']");
        WebElement goToLoginButton = wait.until(ExpectedConditions.visibilityOfElementLocated(buttonLocator));

        Assert.assertTrue(goToLoginButton.isDisplayed(), "Login button is not visible");
        Assert.assertTrue(goToLoginButton.isEnabled(), "Login button is not clickable");

        Actions actions = new Actions(driver);
        actions.moveToElement(goToLoginButton).perform();

        String hoverColor = goToLoginButton.getCssValue("background-color");
        System.out.println("Button hover color: " + hoverColor);
    }

    // 6. Verify visibility of all text
    @Test
    public void verifyAllTextVisible() {
        Assert.assertTrue(driver.findElement(By.tagName("h1")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.cssSelector(".stats-box p")).isDisplayed());
    }
    
    // 7. Verify page reloads to login page correctly
    @Test
    public void verifyGoToLoginButton() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            By buttonLocator = By.xpath("//a[text()='Go to Login Page']");
            WebElement goToLoginButton = wait.until(ExpectedConditions.elementToBeClickable(buttonLocator));
            goToLoginButton.click();

            wait.until(ExpectedConditions.urlContains("login.html"));
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.endsWith("login.html"), "Button did not redirect to login page!");
            System.out.println("Navigation to login page verified.");

            By usernameField = By.id("username");
            By passwordField = By.id("password");
            By loginButton = By.cssSelector("input[type='submit']");

            WebElement usernameElem = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
            WebElement passwordElem = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField));
            WebElement loginBtnElem = wait.until(ExpectedConditions.visibilityOfElementLocated(loginButton));

            Assert.assertTrue(usernameElem.isDisplayed(), "Username field is not visible");
            Assert.assertTrue(passwordElem.isDisplayed(), "Password field is not visible");
            Assert.assertTrue(loginBtnElem.isDisplayed(), "Login button is not visible");

            Assert.assertTrue(loginBtnElem.isEnabled(), "Login button is not clickable");

            System.out.println("Login page elements verified and login button is clickable.");

        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            Assert.fail("Go to Login Page button test failed: " + e.getMessage());
        }
    }

    // 8.Verify backup statistics visiblity
    @Test
    public void verifyBackupStatsVisible() {

        WebElement lastBackupTime = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("lastBackupTime")));
        WebElement totalBackups = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("totalBackups")));
        WebElement filesLastBackup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filesLastBackup")));
        WebElement backupStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("backupStatus")));

        Assert.assertTrue(lastBackupTime.isDisplayed(), "Last Backup Time not visible");
        Assert.assertTrue(totalBackups.isDisplayed(), "Total Backups not visible");
        Assert.assertTrue(filesLastBackup.isDisplayed(), "Files in Last Backup not visible");
        Assert.assertTrue(backupStatus.isDisplayed(), "Backup Status not visible");
    }

    // 9. Verify backup auto refresh
    @Test
    public void verifyAutoRefresh() throws InterruptedException {
        WebElement statusBefore = driver.findElement(By.id("backupStatus"));
        String valueBefore = statusBefore.getText();
        Thread.sleep(16000); 
        WebElement statusAfter = driver.findElement(By.id("backupStatus"));
        Assert.assertNotNull(statusAfter.getText(), "Auto-refresh failed");
    }

    // 10. Verify backup status for failed backup
    @Test
    public void verifyFailedBackupStatus() {
        WebElement status = driver.findElement(By.id("backupStatus"));
        String text = status.getText();
        if(text.equalsIgnoreCase("Failed")) {
            String color = status.getCssValue("color");
            Assert.assertTrue(color.contains("255, 0, 0"));
        }
    }

    // 11. Verify dashboard after browser refresh
    @Test
    public void verifyDashboardAfterRefresh() {
        driver.navigate().refresh();
        Assert.assertTrue(driver.findElement(By.tagName("h1")).isDisplayed());
    }

    // 12. Verify dashboard after backward/forward
    @Test
    public void verifyDashboardNavigation() {
        driver.navigate().back();
        driver.navigate().forward();
        Assert.assertTrue(driver.findElement(By.tagName("h1")).isDisplayed());
    }

    // 13. Verify alignment of statistics
    @Test
    public void verifyStatsAlignment() {
        try {
            WebElement statsBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".stats-box")));

            java.util.List<WebElement> statsElements = statsBox.findElements(By.tagName("p"));

            int referenceX = statsElements.get(0).getLocation().getX();

            for (WebElement stat : statsElements) {
                int currentX = stat.getLocation().getX();
                System.out.println(stat.getText() + " X position: " + currentX);

                Assert.assertTrue(Math.abs(currentX - referenceX) <= 5,
                        "Statistics not aligned vertically: " + stat.getText());
            }

            System.out.println("All statistics are aligned correctly.");

        } catch (Exception e) {
            System.err.println("Stats alignment test failed: " + e.getMessage());
            Assert.fail("Stats alignment test failed: " + e.getMessage());
        }
    }

    // 14. Verify No overlapping of elements
    @Test
    public void verifyNoOverlappingElements() {

        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
        WebElement goToLoginButton = driver.findElement(By.cssSelector("a.button"));
        WebElement statsBox = driver.findElement(By.cssSelector(".stats-box"));

        Assert.assertTrue(heading.isDisplayed());
        Assert.assertTrue(goToLoginButton.isDisplayed());
        Assert.assertTrue(statsBox.isDisplayed());

        java.awt.Rectangle headingRect = new java.awt.Rectangle(
                heading.getLocation().getX(),
                heading.getLocation().getY(),
                heading.getSize().getWidth(),
                heading.getSize().getHeight()
        );

        java.awt.Rectangle buttonRect = new java.awt.Rectangle(
                goToLoginButton.getLocation().getX(),
                goToLoginButton.getLocation().getY(),
                goToLoginButton.getSize().getWidth(),
                goToLoginButton.getSize().getHeight()
        );

        java.awt.Rectangle statsRect = new java.awt.Rectangle(
                statsBox.getLocation().getX(),
                statsBox.getLocation().getY(),
                statsBox.getSize().getWidth(),
                statsBox.getSize().getHeight()
        );

        Assert.assertFalse(headingRect.intersects(buttonRect));
        Assert.assertFalse(headingRect.intersects(statsRect));
        Assert.assertFalse(buttonRect.intersects(statsRect));
    }

    // 15. Verify screen reader compatibility
    @Test
    public void verifyScreenReaderCompatibility() {

        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
        WebElement loginButton = driver.findElement(By.cssSelector("a.button"));

        Assert.assertTrue(heading.getText().length() > 0);
        Assert.assertTrue(loginButton.getText().length() > 0);
    }

    // 16. Verify Dashboard handles large backup count
    @Test
    public void verifyDashboardHandlesLargeBackupCount() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        WebElement statsBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".stats-box")));

        WebElement totalBackupsElem = statsBox.findElement(By.id("totalBackups"));

        String totalBackups = totalBackupsElem.getText().trim();

        Assert.assertTrue(totalBackups.matches("\\d{1,7}"));
        Assert.assertTrue(totalBackupsElem.isDisplayed());
    }

    // 17. Verify page load speed
    @Test
    public void verifyPageLoadSpeed() {
        long startTime = System.currentTimeMillis(); 

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(d -> d.findElement(By.tagName("h1")).isDisplayed());

        long endTime = System.currentTimeMillis(); 
        long loadTime = endTime - startTime; 

        Assert.assertTrue(loadTime <= 2000);
    }

    // 18. verify backup Summary consistency
    @Test
    public void verifyBackupSummaryConsistency() throws InterruptedException {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        WebElement dashLastFile = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[strong[contains(text(),'Files in Last Backup:')]]")));

        WebElement dashLastRun = driver.findElement(By.xpath("//p[strong[contains(text(),'Last Backup Time:')]]/span"));
        WebElement dashStatus = driver.findElement(By.xpath("//p[strong[contains(text(),'Status:')]]/span"));
        WebElement dashNextRun = driver.findElement(By.xpath("//p[strong[contains(text(),'Total Backups Completed:')]]/span"));

        String dashLastFileText = dashLastFile.getText().replace("Files in Last Backup:", "").trim();
        String dashLastRunText = dashLastRun.getText().replace("Last Backup Time:", "").trim();
        String dashStatusText = dashStatus.getText().replace("Status:", "").trim();
        String dashNextRunText = dashNextRun.getText().replace("Total Backups Completed:", "").trim();

        driver.get(baseUrl + "/index.jsp");

        WebElement statusLastFile = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[strong[contains(text(),'Last File:')]]")));

        WebElement statusLastRun = driver.findElement(By.xpath("//p[strong[contains(text(),'Last Run:')]]"));
        WebElement statusStatus = driver.findElement(By.xpath("//p[strong[contains(text(),'Status:')]]"));
        WebElement statusNextRun = driver.findElement(By.xpath("//p[strong[contains(text(),'Next Scheduled Run:')]]"));

        Assert.assertEquals(statusLastRun.getText(), dashLastRunText);
        Assert.assertEquals(statusStatus.getText(), dashStatusText);
    }

    // 19. Verify welcome page when no backup exists
    @Test
    public void verifyWelcomePageWhenNoBackupsExist() {

        WebElement lastBackupTime = driver.findElement(By.id("lastBackupTime"));
        WebElement totalBackups = driver.findElement(By.id("totalBackups"));
        WebElement filesLastBackup = driver.findElement(By.id("filesLastBackup"));
        WebElement statusField = driver.findElement(By.id("backupStatus"));

        Assert.assertTrue(lastBackupTime.isDisplayed());
        Assert.assertTrue(totalBackups.isDisplayed());
        Assert.assertTrue(filesLastBackup.isDisplayed());
        Assert.assertTrue(statusField.isDisplayed());
    }

    // 20. verify backup time reflects system time
    @Test
    public void verifyBackupTimeReflectsSystemClock() throws InterruptedException {

        driver.get(baseUrl + "/backup.jsp");

        String sourcePath = ConfigReader.getProperty("backup.temp.source.path");
        String destPath = ConfigReader.getProperty("backup.destination.path");

        driver.findElement(By.name("sourceDir")).sendKeys(sourcePath);
        driver.findElement(By.name("destDir")).sendKeys(destPath);

        LocalTime backupTime = LocalTime.now().plusMinutes(2);
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

        driver.findElement(By.name("backupTime")).clear();
        driver.findElement(By.name("backupTime")).sendKeys(backupTime.format(timeFormat));

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.enable-btn"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("View Backup Status"))).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(180));

        boolean completed = false;
        String finalStatus = "";

        for (int i = 0; i < 36; i++) {

            String statusText = driver.findElement(By.xpath("//span[contains(@class,'status')]")).getText().trim();

            if ("Success".equalsIgnoreCase(statusText)) {
                completed = true;
                finalStatus = "Success";
                break;
            } else if ("Failed".equalsIgnoreCase(statusText)) {
                completed = true;
                finalStatus = "Failed";
                break;
            }

            Thread.sleep(5000);
        }

        Assert.assertTrue(completed);

        driver.get(baseUrl + "/welcome.jsp");
        driver.navigate().refresh();

        WebDriverWait dashWait = new WebDriverWait(driver, Duration.ofSeconds(40));

        dashWait.until(d -> !d.findElement(By.id("lastBackupTime")).getText().trim().isEmpty());

        Assert.assertFalse(driver.findElement(By.id("lastBackupTime")).getText().trim().isEmpty());
    }
}
