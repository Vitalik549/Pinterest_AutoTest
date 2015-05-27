package com.pinterest.uk.pages;

import com.pinterest.uk.helpers.StatusWebElem;
import com.pinterest.uk.helpers.User;
import com.pinterest.uk.pages.basePages.PinBasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;


public class WelcomePage extends PinBasePage {

    @FindBy(id = "userEmail")
    private WebElement userEmail;

    @FindBy(id = "userPassword")
    private WebElement userPassword;

    @FindBy(name = "id")
    private WebElement userEmailInPopup;

    @FindBy(name = "password")
    private WebElement userPasswordInPopup;

    @FindBy(xpath = "//button[contains(@class, 'SignupButton')]")
    private WebElement logInButtonPopup;

    @FindBy(xpath = "//button[contains(., 'Log in')]")
    private WebElement logInButton;





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
