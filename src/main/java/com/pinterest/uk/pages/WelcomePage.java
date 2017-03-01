package com.pinterest.uk.pages;

import com.pinterest.uk.helpers.StatusWebElem;
import com.pinterest.uk.helpers.User;
import com.pinterest.uk.pages.basePages.PinBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;


public class WelcomePage extends PinBasePage {

    private By userEmail = By.id("userEmail");
    private By userPassword = By.id("userPassword");
    private By userEmailInPopup = By.name("id");
    private By userPasswordInPopup= By.name("password");
    private By logInButtonPopup= By.cssSelector("button.SignupButton");
    private By logInButton= By.xpath("//button[contains(., 'Log in')]");

    public WelcomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage loginUser(User user){
        $(userEmailInPopup).shouldBe(visible).setValue(user.login);
        $(userPasswordInPopup).setValue(user.password);
        $(logInButtonPopup).click();
        return new HomePage(driver);
    }

    public boolean isLoginButtonDisplayed() {
        return isElementDisplayed(logInButton);
    }

    public WelcomePage checkLoginButtonDisplayed(StatusWebElem statusWebElem) {
        checkElementStatus($(logInButton), statusWebElem);
        return this;
    }
}
