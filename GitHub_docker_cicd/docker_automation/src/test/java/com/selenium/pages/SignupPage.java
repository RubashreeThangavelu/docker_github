package com.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

public class SignupPage {

    private WebDriver driver;
    private WebDriverWait wait;

    private By username = By.id("username");
    private By email = By.id("email");
    private By password = By.id("password");
    private By confirm_password = By.id("confirm_password");
    private By signupButton = By.cssSelector("input[type='submit']");
   
	public SignupPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }
    
    
    // Verify page title
    public void verifyTitle(String expectedTitle) {
        Assert.assertEquals(driver.getTitle(), expectedTitle);
    }
    
    
    // Verify all essential elements are displayed
    public void verifyElementsDisplayed() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(username));
        wait.until(ExpectedConditions.visibilityOfElementLocated(email));
        wait.until(ExpectedConditions.visibilityOfElementLocated(password));
        wait.until(ExpectedConditions.visibilityOfElementLocated(confirm_password));
        wait.until(ExpectedConditions.visibilityOfElementLocated(signupButton));
    }

    public void signup(String user, String useremail, String pass, String pass1) {
    
        driver.findElement(username).clear();
        driver.findElement(username).sendKeys(user);
        
        driver.findElement(email).clear();
        driver.findElement(email).sendKeys(useremail);
        
        driver.findElement(password).clear();
        driver.findElement(password).sendKeys(pass);
        
        driver.findElement(confirm_password).clear();
        driver.findElement(confirm_password).sendKeys(pass1);
        
        driver.findElement(signupButton).click();
    
    }
// Clear fields
    public void clearFields() {
        driver.findElement(username).clear();
        driver.findElement(email).clear();
        driver.findElement(password).clear();
        driver.findElement(confirm_password).clear();
    }
    
 }
