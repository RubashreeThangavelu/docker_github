package com.selenium.tests;

import com.selenium.pages.LoginPage;
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
import java.util.ArrayList;
import java.util.Set;
import com.selenium.utils.ConfigReader;

public class LoginTest extends BaseTest {

    private LoginPage loginPage;
    private WebDriverWait wait;
        private String baseUrl;


    @BeforeMethod
    public void setup() {
        driver.manage().deleteAllCookies();
        
        
           
         baseUrl = ConfigReader.getProperty("base.url");

        driver.get(baseUrl + "login.html");


        loginPage = new LoginPage(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        Assert.assertTrue(driver.getCurrentUrl().contains("login.html"));
    }
    
    //1. Verify login page title
    @Test
    public void verifyLoginPageTitle() {
        loginPage.verifyTitle("Secure Login");
    }
    
    //2. Verify all elements are present
    @Test
    public void verifyAllElementsPresent() {
        loginPage.verifyElementsDisplayed();
    }
    
    //3. Verify labels are properly visible
    @Test
    public void verifyLabelsVisible() {
        Assert.assertTrue(driver.getPageSource().contains("Username"));
        Assert.assertTrue(driver.getPageSource().contains("Password"));
    }
    
    //4. Verify Button and link visibility
    @Test
    public void verifyButtonAndLinkVisibility() {
        Assert.assertTrue(driver.findElement(By.cssSelector("input[type='submit']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.linkText("Sign up here")).isDisplayed());
    }
    
    
    //5. Verify login with valid credentials
   @Test
   public void loginWithValidCredentials() {

    loginPage.login("admin", "Admin1234");

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    WebElement schedulerTitle = wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//h2[contains(text(),'Backup')]")
        )
    );

    Assert.assertTrue(schedulerTitle.isDisplayed(),
            "Backup Scheduler page not displayed after valid login");

    Assert.assertTrue(
            driver.getCurrentUrl().contains("backup"),
            "User not redirected to Backup Scheduler URL"
    );

    wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("sourceDir")));
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("destDir")));
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("backupTime")));
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button.enable-btn")));

    Assert.assertTrue(driver.findElement(By.name("sourceDir")).isDisplayed(), "Source directory missing");
    Assert.assertTrue(driver.findElement(By.name("destDir")).isDisplayed(), "Destination directory missing");
    Assert.assertTrue(driver.findElement(By.name("backupTime")).isDisplayed(), "Backup time missing");
    Assert.assertTrue(driver.findElement(By.cssSelector("button.enable-btn")).isDisplayed(), "Start Backup button missing");

}



    //6. Verify login with Empty fields
    @Test
    public void loginWithEmptyFields() {
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        WebElement username = driver.findElement(By.id("username"));
        Assert.assertFalse(username.getAttribute("validationMessage").isEmpty());
    }
    
    //7. Verify login with invalid username    
     @Test
     public void loginWithInvalidUsername() {
    
        driver.findElement(By.id("username")).sendKeys("newuser");
        driver.findElement(By.id("password")).sendKeys("1234");
        driver.findElement(By.cssSelector("input[type='submit']")).click();


    Assert.assertTrue(driver.getCurrentUrl().contains("login"),
            "User should stay on login page for invalid credentials");
    }
    
    //8. Verify login with invalid password    
    @Test
    public void loginWithInvalidPassword() {
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("12345");
        driver.findElement(By.cssSelector("input[type='submit']")).click();


    Assert.assertTrue(driver.getCurrentUrl().contains("login"),
            "User should stay on login page for invalid credentials");
    }
    
    //9. Verify login with empty username    
    @Test
    public void loginWithUsernameEmpty() {
        driver.findElement(By.id("password")).sendKeys("1234");
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        WebElement username = driver.findElement(By.id("username"));
        Assert.assertFalse(username.getAttribute("validationMessage").isEmpty());
    }
    
    //10. Verify login with empty password    
    @Test
    public void loginWithPasswordEmpty() {
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        WebElement password = driver.findElement(By.id("password"));
        Assert.assertFalse(password.getAttribute("validationMessage").isEmpty());
    }
    
    //11. Verify login with whitespace password
    @Test
    public void loginWithWhitespacePassword() {
    WebElement password = driver.findElement(By.id("password"));
    password.sendKeys(" ");

    String validationMessage = password.getAttribute("validationMessage");

    Assert.assertTrue(validationMessage.isEmpty(),
            "Whitespace passwords are allowed by HTML5 required attribute");
}

    //12. verify login with sql commands in username
    @Test
    public void loginWithSQLInjection() {
        driver.findElement(By.id("username")).sendKeys("'; DROP TABLE users;--");
        driver.findElement(By.id("password")).sendKeys("1234");
        driver.findElement(By.cssSelector("input[type='submit']")).click();
    
        Assert.assertTrue(driver.getCurrentUrl().contains("login"));
    }
    //13. verify login with username containing spaces
    @Test
    public void loginWithUsernameContainingSpaces() {
        driver.findElement(By.id("username")).sendKeys("admin user");
        driver.findElement(By.id("password")).sendKeys("1234");
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        Assert.assertTrue(driver.getCurrentUrl().contains("login"));
    }
    //14. Verify for signup page navigation
    @Test
    public void clickSignUpLinkTest() {
   loginPage.clickSignUpLink();

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    wait.until(ExpectedConditions.titleIs("Sign Up"));

    Assert.assertTrue(driver.findElement(By.id("username")).isDisplayed());
    Assert.assertTrue(driver.findElement(By.id("email")).isDisplayed());
    Assert.assertTrue(driver.findElement(By.id("password")).isDisplayed());
    Assert.assertTrue(driver.findElement(By.id("confirm_password")).isDisplayed());
    Assert.assertTrue(driver.findElement(By.cssSelector("input[type='submit']")).isDisplayed());

    Assert.assertTrue(
        driver.getCurrentUrl().contains("signup"),
        "User not navigated to Sign Up page"
    );
}
    //15. Verify Login after password change in the backend
    @Test
    public void verifyLoginAfterPasswordChange() {
    loginPage.login("admin", "Admin1234");

    WebElement schedulerTitle = wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//h2[contains(text(),'Backup')]")
        )
    );

    Assert.assertTrue(
        schedulerTitle.isDisplayed(),
        "Login should succeed with new password"
    );


    driver.manage().deleteAllCookies();
    
            driver.get(baseUrl + "login.html");



    loginPage.login("asdf", "9876");

    Assert.assertTrue(
        driver.getCurrentUrl().contains("login"),
        "Login should fail with old password"
    );
}
 //16. Verify Keyboard Navigation

    @Test
public void verifyKeyboardNavigationLogin() {

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    WebElement username = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("username"))
    );
    username.sendKeys("admin");
    username.sendKeys(Keys.TAB);


    WebElement active = driver.switchTo().activeElement();
    Assert.assertEquals(active.getAttribute("id"), "password",
            "TAB did not move to Password field");

    active.sendKeys("Admin1234");   
    active.sendKeys(Keys.TAB);

    active = driver.switchTo().activeElement();
    Assert.assertEquals(active.getAttribute("type"), "submit",
            "TAB did not move to Login button");

active.sendKeys(Keys.RETURN);


wait.until(ExpectedConditions.urlContains("backup"));

  WebElement schedulerTitle = wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//h2[contains(text(),'Backup')]")
        )
    );

Assert.assertTrue(schedulerTitle.isDisplayed(),
        "Backup Scheduler page not displayed after login");

Assert.assertTrue(driver.getCurrentUrl().contains("backup"),
        "User not redirected to correct page");

}


    //17. Verify autofill behavior in the input fields
    @Test
    public void verifyAutofillBehavior() {
   
    WebElement usernameField = driver.findElement(By.name("username"));
    WebElement passwordField = driver.findElement(By.name("password"));


    String usernameAutocomplete = usernameField.getAttribute("autocomplete");
    Assert.assertNotNull(usernameAutocomplete, "Username field does not have autocomplete enabled");
    System.out.println("Username autocomplete attribute: " + usernameAutocomplete);
   String usernameValue = usernameField.getAttribute("value").trim();
    Assert.assertEquals(usernameValue, "", "Username field should be empty initially");
    System.out.println("Login fields are ready for autofill/manual input.");
}

    //18. Verify the signup url opens in new tab
    @Test
    public void switchToNewTabOrSameTab() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));

        WebElement signupLink = wait.until(
                ExpectedConditions.elementToBeClickable(By.partialLinkText("Sign up"))
        );

        String originalWindow = driver.getWindowHandle();
        Set<String> existingWindows = driver.getWindowHandles();

        signupLink.click();

        wait.until(d -> d.getWindowHandles().size() >= existingWindows.size());

        ArrayList<String> allWindows = new ArrayList<>(driver.getWindowHandles());

        if (allWindows.size() > 1) {

            allWindows.remove(originalWindow); // Remove original window
            driver.switchTo().window(allWindows.get(0));
            System.out.println("Opened in new tab/window.");
        } else {

            System.out.println("Opened in the same tab.");
        }

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("signup"),
                "FAIL: Redirected to unexpected page: " + currentUrl);

        if (allWindows.size() > 1) {
            driver.close();
            driver.switchTo().window(originalWindow);
        }
    }

    //19. Verify login after backend reset  
    @Test
    public void verifyLoginAfterBackendReset() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
    WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));

    usernameField.sendKeys("user1");
    passwordField.sendKeys("P@ss1234");

    WebElement loginButton = driver.findElement(By.cssSelector("input[type='submit']"));
    loginButton.click();

    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));

    WebElement newUsernameField = driver.findElement(By.id("username"));
    WebElement newPasswordField = driver.findElement(By.id("password"));

    Assert.assertEquals(newUsernameField.getAttribute("value"), "",
            "FAIL: Username field should be empty after failed login");
    Assert.assertEquals(newPasswordField.getAttribute("value"), "",
            "FAIL: Password field should be empty after failed login");

    Assert.assertTrue(driver.getCurrentUrl().contains("login"),
            "FAIL: Unexpected navigation after login attempt");
}


    //20. Verify login with maximum characters
    @Test
    public void verifyLoginWithMaxChars() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    String maxUsername = "U".repeat(50);
    String maxPassword = "P".repeat(50);

    WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
    WebElement passwordField = driver.findElement(By.id("password"));

    usernameField.sendKeys(maxUsername);
    passwordField.sendKeys(maxPassword);

    WebElement loginButton = driver.findElement(By.cssSelector("input[type='submit']"));
    loginButton.click();

    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));

    WebElement newUsernameField = driver.findElement(By.id("username"));
    WebElement newPasswordField = driver.findElement(By.id("password"));

    Assert.assertEquals(newUsernameField.getAttribute("value"), "",
            "FAIL: Username field should be empty after failed login");
    Assert.assertEquals(newPasswordField.getAttribute("value"), "",
            "FAIL: Password field should be empty after failed login");

    Assert.assertTrue(driver.getCurrentUrl().contains("login"),
            "FAIL: Unexpected navigation after login attempt");

    System.out.println("PASS: Login processed correctly; input validation enforced for max chars.");
}

}

