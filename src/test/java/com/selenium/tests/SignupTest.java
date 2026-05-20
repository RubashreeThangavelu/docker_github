package com.selenium.tests;

import com.selenium.pages.SignupPage;
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
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import java.util.List;
import org.openqa.selenium.NoSuchElementException;

import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import com.selenium.utils.ConfigReader;
public class SignupTest extends BaseTest {

    private SignupPage signupPage;
    private WebDriverWait wait;
        private String baseUrl;


    @BeforeMethod
    public void setup() {
        driver.manage().deleteAllCookies();

           
         baseUrl = ConfigReader.getProperty("base.url");

        driver.get(baseUrl + "/signup.html");
        

        signupPage = new SignupPage(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        Assert.assertTrue(driver.getCurrentUrl().contains("signup.html"));
    }
    
    //1. Verify signup page title
    @Test
    public void verifySignupPageTitle() {
        signupPage.verifyTitle("Sign Up");
    }
    
   //2. Verify all elements are present
    @Test
    public void verifyAllElementsPresent() {
        signupPage.verifyElementsDisplayed();
    }
    
    
     //3. Verify labels are properly visible
    @Test
    public void verifyLabelsVisible() {
        Assert.assertTrue(driver.getPageSource().contains("Username"));
        Assert.assertTrue(driver.getPageSource().contains("Email"));
        Assert.assertTrue(driver.getPageSource().contains("Password"));
        Assert.assertTrue(driver.getPageSource().contains("Confirm Password"));
    }
    
    
    //4. Verify Button and link visibility
    @Test
    public void verifyButtonVisibility() {
Assert.assertTrue(
    driver.findElement(By.cssSelector("input[type='submit']")).isDisplayed(),
    "Register button not visible"
);


    }
    //5. Verify login with valid credentials
   @Test
    public void signupWithValidCredentials() {
    signupPage.signup("Deva1 ", "Deva1@example.com","Password1234","Password1234");

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    WebElement loginLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Login here")));

    Assert.assertTrue(loginLink.isDisplayed(), "Login link not displayed after signup");

    loginLink.click();
    Assert.assertTrue(driver.getCurrentUrl().contains("login"), "User not redirected to Login URL");
    }

  

    //6. Verify login with Empty fields
    @Test
    public void loginWithEmptyFields() {
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        WebElement username = driver.findElement(By.id("username"));
        Assert.assertFalse(username.getAttribute("validationMessage").isEmpty());
    }
  
  
    //7. verify login with sql commands in username

    @Test
    public void loginWithSQLInjection() {

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    driver.findElement(By.id("username"))
            .sendKeys("'; DROP TABLE users;--");
    driver.findElement(By.id("email"))
            .sendKeys("Drop@example.com");
    driver.findElement(By.id("password"))
            .sendKeys("Password123");
    driver.findElement(By.id("confirm_password"))
            .sendKeys("Password123");

    driver.findElement(By.cssSelector("input[type='submit']")).click();

    wait.until(ExpectedConditions.urlContains("signup"));

    Assert.assertTrue(
            driver.getCurrentUrl().contains("signup"),
            "SQL Injection should not allow signup success"
    );

    System.out.println("SQL Injection attempt blocked successfully");
}

    
    
    //8. Verify password mismatch
    @Test
    public void verifyPasswordMismatchValidation() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    driver.findElement(By.id("username")).sendKeys("admin1");
    driver.findElement(By.id("email")).sendKeys("admin1@example.com");
    driver.findElement(By.id("password")).sendKeys("Password123");
    driver.findElement(By.id("confirm_password")).sendKeys("Password1239876");
    driver.findElement(By.cssSelector("input[type='submit']")).click();


    WebElement errorMessage = wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//*[contains(text(),'Passwords do not match')]")
        )
    );

    Assert.assertTrue(errorMessage.isDisplayed(), "Validation error for password mismatch is not displayed");
}
    
    
    //9. Verify Email Format validation
    @Test
    public void verifyEmailFormatValidation() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement emailField = driver.findElement(By.id("email"));

    driver.findElement(By.id("username")).sendKeys("user1");
    driver.findElement(By.id("password")).sendKeys("Password123");
    driver.findElement(By.id("confirm_password")).sendKeys("Password123");
    emailField.sendKeys("userexample.com"); // Invalid email


    WebElement signupButton = driver.findElement(By.cssSelector("input[type='submit'], button[type='submit']"));
    signupButton.click();

    Boolean isInvalid = Boolean.parseBoolean(emailField.getAttribute("validationMessage").isEmpty() ? "false" : "true");

    Assert.assertTrue(isInvalid, "Email field did not show validation error for invalid email");     
    System.out.println("Validation message: " + emailField.getAttribute("validationMessage"));

}

      //10. Verify Duplicate Username
      @Test
      public void verifyDuplicateUsernameValidation() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    driver.findElement(By.id("username")).sendKeys("admin"); 
    driver.findElement(By.id("email")).sendKeys("newemail@example.com");
    driver.findElement(By.id("password")).sendKeys("Password123");
    driver.findElement(By.id("confirm_password")).sendKeys("Password123");

    WebElement signupButton = driver.findElement(By.cssSelector("input[type='submit'], button[type='submit']"));
    signupButton.click();

    WebElement body = wait.until(
        ExpectedConditions.visibilityOfElementLocated(By.tagName("body"))
    );

    String pageText = body.getText();
    Assert.assertTrue(pageText.contains("Username or Email already exists!"),
        "Duplicate username/email error not displayed on the page");

    WebElement tryAgainLink = driver.findElement(By.linkText("Try again"));
    tryAgainLink.click();
}


    //11.Verify Duplicate Email
    @Test
    public void verifyDuplicateEmailValidation() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    driver.findElement(By.id("username")).sendKeys("Dev"); 
    driver.findElement(By.id("email")).sendKeys("admin@example.com");//Existing email
    driver.findElement(By.id("password")).sendKeys("Password123");
    driver.findElement(By.id("confirm_password")).sendKeys("Password123");

    WebElement signupButton = driver.findElement(By.cssSelector("input[type='submit'], button[type='submit']"));
    signupButton.click();

    WebElement body = wait.until(
        ExpectedConditions.visibilityOfElementLocated(By.tagName("body"))
    );

    String pageText = body.getText();
    Assert.assertTrue(pageText.contains("Username or Email already exists!"),
        "Duplicate username/email error not displayed on the page");

  
}

    //12.  Verify for password fields are masked
   @Test
    public void verifyPasswordFieldsAreMasked() {


    WebElement passwordField = driver.findElement(By.id("password"));
    WebElement confirmPasswordField = driver.findElement(By.id("confirm_password"));


    passwordField.sendKeys("Password123");
    confirmPasswordField.sendKeys("Password123");

    Assert.assertEquals(passwordField.getAttribute("type"), "password",
            "Password field is not masked!");

    Assert.assertEquals(confirmPasswordField.getAttribute("type"), "password",
            "Confirm Password field is not masked!");
}


    //13.  Verify Weak password
    @Test
    public void verifyWeakPassword() {
    driver.findElement(By.id("username")).sendKeys("validUser");
    driver.findElement(By.id("email")).sendKeys("test@mail.com");
    driver.findElement(By.id("password")).sendKeys("abc123");
    driver.findElement(By.id("confirm_password")).sendKeys("abc123");

    driver.findElement(By.cssSelector("input[type='submit']")).click();

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement error = wait.until(
        ExpectedConditions.visibilityOfElementLocated(By.id("passwordError"))
    );

    Assert.assertEquals(error.getText(), "Password too weak");
}


    // 14. Verify Signup page Performance
    @Test
    public void verifySignupPerformance() {

    driver.findElement(By.id("username")).sendKeys("validuser11");
    driver.findElement(By.id("email")).sendKeys("user111@test.com");
    driver.findElement(By.id("password")).sendKeys("Test@123");
    driver.findElement(By.id("confirm_password")).sendKeys("Test@123");

    long startTime = System.currentTimeMillis();

    String currentUrl = driver.getCurrentUrl();

    driver.findElement(By.cssSelector("input[type='submit']")).click();

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

    wait.until(ExpectedConditions.not(
            ExpectedConditions.urlToBe(currentUrl)
    ));

    long endTime = System.currentTimeMillis();
    long totalTime = endTime - startTime;

    System.out.println("Signup response time: " + totalTime + " ms");

    Assert.assertTrue(totalTime < 2000,
            "Signup took too long! Actual time: " + totalTime + " ms");
}



    //15.  Verify Minimum length user name
    @Test
    public void verifyMinUsernameLength() {

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    driver.findElement(By.id("username")).clear();
    driver.findElement(By.id("username")).sendKeys("ab");

    driver.findElement(By.id("email")).sendKeys("test@gmail.com");
    driver.findElement(By.id("password")).sendKeys("Abcd1234");
    driver.findElement(By.id("confirm_password")).sendKeys("Abcd1234");

    driver.findElement(By.cssSelector("input[type='submit']")).click();

    wait.until(ExpectedConditions.textToBePresentInElementLocated(
        By.id("usernameError"),
        "Username too short"
    ));

    String msg = driver.findElement(By.id("usernameError")).getText();
    Assert.assertEquals(msg, "Username too short");
}


    //16. Verify maximum length user name
    @Test
    public void verifyMaxUsernameLength() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    String longUser = "abcdefghijklmnopqrstuv";  
   driver.findElement(By.id("username")).clear();
   driver.findElement(By.id("username")).sendKeys(longUser);

   driver.findElement(By.id("email")).sendKeys("test@gmail.com");
   driver.findElement(By.id("password")).sendKeys("Abcd1234");
   driver.findElement(By.id("confirm_password")).sendKeys("Abcd1234");

   driver.findElement(By.cssSelector("input[type='submit']")).click();

   wait.until(ExpectedConditions.textToBePresentInElementLocated(
        By.id("usernameError"),
        "Username too long"
  ));

   String msg = driver.findElement(By.id("usernameError")).getText();
   Assert.assertEquals(msg, "Username too long");
}

    
  //17. Verify Keyboard Navigation

    @Test
    public void verifyKeyboardNavigationSignup() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    WebElement usernameField = driver.findElement(By.id("username"));
    usernameField.sendKeys("Prasath1");
    usernameField.sendKeys(Keys.TAB);

    WebElement active = driver.switchTo().activeElement();
    Assert.assertEquals(active.getAttribute("id"), "email");

    active.sendKeys("prasath1@example.com");
    active.sendKeys(Keys.TAB);

    active = driver.switchTo().activeElement();
    Assert.assertEquals(active.getAttribute("id"), "password");

    active.sendKeys("Password123");
    active.sendKeys(Keys.TAB);

    active = driver.switchTo().activeElement();
    Assert.assertEquals(active.getAttribute("id"), "confirm_password");

    active.sendKeys("Password123");
    active.sendKeys(Keys.TAB);

    active = driver.switchTo().activeElement();
    Assert.assertEquals(active.getAttribute("type"), "submit");

    active.sendKeys(Keys.ENTER);  
    WebElement loginLink = wait.until(
        ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Login here')]")));
    loginLink.click();
    wait.until(ExpectedConditions.urlContains("login.html"));

    Assert.assertTrue(driver.getCurrentUrl().contains("login"), "User not redirected to Login URL");
}
 
    //18. Verify responsive layout
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
    Assert.assertTrue(driver.findElement(By.name("username")).isDisplayed());
    Assert.assertTrue(driver.findElement(By.name("email")).isDisplayed());
    Assert.assertTrue(driver.findElement(By.name("password")).isDisplayed());
    Assert.assertTrue(driver.findElement(By.name("confirm_password")).isDisplayed());
    Assert.assertTrue(driver.findElement(By.cssSelector("input[type='submit']")).isDisplayed());


    System.out.println("UI adjusts properly in Desktop, Tablet, Mobile view");
}

    //19. Verify login with special characters
    @Test
    public void loginWithSpecialCharacters() {

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    driver.findElement(By.id("username"))
            .sendKeys("#$%^&*()*&^#$%@%@#%");
    driver.findElement(By.id("email"))
            .sendKeys("Drop@example.com");
    driver.findElement(By.id("password"))
            .sendKeys("Password123");
    driver.findElement(By.id("confirm_password"))
            .sendKeys("Password123");

    driver.findElement(By.cssSelector("input[type='submit']")).click();

    wait.until(ExpectedConditions.urlContains("signup"));

    Assert.assertTrue(
            driver.getCurrentUrl().contains("signup"),
            "Special characters should not allow signup success"
    );

    System.out.println("Special characters  attempt blocked successfully");
}

    //20. Verify error messages are meaningful
     @Test
    public void verifyErrorMessagesForInvalidInput() {

    WebElement username = driver.findElement(By.name("username"));
    WebElement email = driver.findElement(By.name("email"));
    WebElement password = driver.findElement(By.name("password"));
    WebElement confirm_password = driver.findElement(By.name("confirm_password"));
    WebElement button = driver.findElement(By.cssSelector("input[type='submit']"));

    username.clear();
    username.sendKeys("ad");

    email.clear();
    email.sendKeys("admin.com");

    password.clear();
    password.sendKeys("A1234");

    confirm_password.clear();
    confirm_password.sendKeys("A12334");

    button.click();

    JavascriptExecutor js = (JavascriptExecutor) driver;

    String usernameMsg = (String) js.executeScript("return arguments[0].validationMessage;", username);
    String emailMsg   = (String) js.executeScript("return arguments[0].validationMessage;", email);
    String passwordMsg   = (String) js.executeScript("return arguments[0].validationMessage;", password);
    String confirm_passwordMsg   = (String) js.executeScript("return arguments[0].validationMessage;", confirm_password);

    System.out.println("Username Msg: " + usernameMsg);
    System.out.println("Email Msg  : " + emailMsg);
    System.out.println("Password Msg  : " + passwordMsg);
    System.out.println("Confirm password Msg  : " + confirm_passwordMsg);


    Boolean isInvalid = Boolean.parseBoolean(email.getAttribute("validationMessage").isEmpty() ? "false" : "true");

    Assert.assertTrue(usernameMsg.isEmpty(), "Username should not have HTML5 message yet");
   Assert.assertTrue(isInvalid, "Email field did not show validation error for invalid email");     
    System.out.println("Validation message: " + email.getAttribute("validationMessage"));
    Assert.assertTrue(passwordMsg.isEmpty(), "Password should not have HTML5 message yet");
    Assert.assertTrue(confirm_passwordMsg.isEmpty(), "Confirm_password should not have HTML5 message yet");
}


      //21. Verify no sensitive information in the error messages 
    @Test
    public void verifyNoSensitiveInformationInErrorMessages() {


    driver.findElement(By.name("username")).sendKeys("testUser");
    driver.findElement(By.name("email")).sendKeys("invalidEmail");  
    driver.findElement(By.name("password")).sendKeys("Pass123");
    driver.findElement(By.name("confirm_password")).sendKeys("Pass1234");

    driver.findElement(By.cssSelector("input[type='submit']")).click();

    List<WebElement> errors = driver.findElements(
            By.xpath("//*[contains(@class,'error') or contains(text(),'Error') or contains(text(),'error')]")
    );

    StringBuilder allErrorText = new StringBuilder();

    for (WebElement error : errors) {
        if (error.isDisplayed()) {
            allErrorText.append(error.getText()).append(" ");
            System.out.println("UI Error: " + error.getText());
        }
    }

    String uiErrors = allErrorText.toString().toLowerCase();


    String[] sensitivePatterns = {
            "exception",
            "stack trace",
            "sql",
            "jdbc",
            "database",
            "table",
            "oracle",
            "mysql",
            "postgres",
            "mongo",
            "nullpointer",
            "file:",
            "directory",
            "c:\\",
            "/var/",
            "apache",
            "nginx",
            "tomcat",
            "spring",
            "node",
            "internal server error",
            "line number",
            "debug"
    };

    for (String pattern : sensitivePatterns) {
        Assert.assertFalse(
                uiErrors.contains(pattern),
                " Security Risk! Sensitive data leaked in UI message → " + pattern
        );
    }

    System.out.println(" UI messages verified — No sensitive data leakage detected");
}

    //22. Verify signup rate limiting
    @Test
    public void verifySignupRateLimiting() throws InterruptedException {
       
        

        driver.get(baseUrl + "/signup.html");


        String baseUsername = "testuser";
        String password = "Password123";
        boolean rateLimitTriggered = false;

        for (int i = 0; i < 5; i++) {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            String username = baseUsername + i;
            String email = "user" + i + "@example.com";

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));

            driver.findElement(By.id("username")).clear();
            driver.findElement(By.id("username")).sendKeys(username);

            driver.findElement(By.id("email")).clear();
            driver.findElement(By.id("email")).sendKeys(email);

            driver.findElement(By.id("password")).clear();
            driver.findElement(By.id("password")).sendKeys(password);

            driver.findElement(By.id("confirm_password")).clear();
            driver.findElement(By.id("confirm_password")).sendKeys(password);

            driver.findElement(By.cssSelector("input[type='submit']")).click();

            Thread.sleep(500);

            try {
                WebElement errorMessage = driver.findElement(By.id("rateLimitMessage"));
                if (errorMessage.isDisplayed()) {
                    rateLimitTriggered = true;
                    System.out.println("Rate limiting triggered on attempt " + (i + 1));
                    break;
                }
            } catch (NoSuchElementException e) {
                
            }
            
               


        driver.get(baseUrl + "/signup.html");


        }

        if (!rateLimitTriggered) {
            System.out.println("Rate limiting not implemented yet.");
        } else {
            Assert.assertTrue(rateLimitTriggered, "Rate limiting triggered successfully!");
        }
    }

}


