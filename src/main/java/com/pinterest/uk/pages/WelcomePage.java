package com.pinterest.uk.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


public class WelcomePage extends PinBasePage {

    @FindBy(id = "userEmail")
    private WebElement userEmail2;

    @FindBy(id = "userPassword")
    private WebElement userPassword2;

    @FindBy(name = "username_or_email")
    private WebElement userEmail;

    @FindBy(name = "password")
    private WebElement userPassword;

    @FindBy(xpath = "//*[@class = 'buttonText'][text() = 'Log in']")
    private WebElement logInButton;

    private String login = "Vitalik549@gmail.com";
    private String password = "1111222334";

    public WelcomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage loginUser(){
        userEmail.sendKeys(login);
        userPassword.sendKeys(password);
        logInButton.click();
        return new HomePage(driver);
    }

}
