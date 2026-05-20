package com.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

public class LoginPage {

    private WebDriver driver;
    private WebDriverWait wait;

    private By username = By.id("username");
    private By password = By.id("password");
    private By loginButton = By.cssSelector("input[type='submit']");
    private By signUpLink = By.linkText("Sign up here");


    public LoginPage(WebDriver driver) {
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
        wait.until(ExpectedConditions.visibilityOfElementLocated(password));
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginButton));
        wait.until(ExpectedConditions.visibilityOfElementLocated(signUpLink));
    }

    // Enter username
    public void login(String user, String pass) {
    
        driver.findElement(username).clear();
        driver.findElement(username).sendKeys(user);
        
        driver.findElement(password).clear();
        driver.findElement(password).sendKeys(pass);
        
        driver.findElement(loginButton).click();
    
    }

    

    // Verify sign up link
    public void clickSignUpLink() {
        driver.findElement(signUpLink).click();
    }

    // Clear fields
    public void clearFields() {
        driver.findElement(username).clear();
        driver.findElement(password).clear();
    }
}

