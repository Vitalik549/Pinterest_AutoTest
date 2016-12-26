package com.pinterest.uk.pages;

import com.pinterest.uk.helpers.StatusWebElem;
import com.pinterest.uk.helpers.WaitHelper;
import com.pinterest.uk.pages.basePages.PinBasePage;
import com.pinterest.uk.pinObjects.Pin;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class MenuPage extends PinBasePage {


    private By mainSearchField = By.name("q");
    protected By saveButton = By.xpath(".//button[contains(.,'Save')]");
    protected By logoutButton = By.cssSelector("a[href='/logout/']");

    public MenuPage(WebDriver driver) {
        super(driver);
    }

    public void waitSystemBusy() {
        $(By.className("Spinner")).shouldBe(not(visible));
    }

    public boolean isMainSearchDisplayed() {
        return isElementDisplayed(mainSearchField);
    }

    public MenuPage checkMainSearchDisplayed(StatusWebElem statusWebElem) {
        //alternative check method for test 2 example
        checkElementStatus($(mainSearchField), statusWebElem);
        return this;
    }

    public HomePage search(Pin pin) {
        $(mainSearchField).shouldBe(visible).setValue(pin.getDescription()).pressEnter();
        return new HomePage(driver);
    }

    public WelcomePage userLogOut() {
        goToProfile();
        $(By.xpath("//*[contains(@class, 'userMenuButton')]")).shouldBe(visible).click();
        $(logoutButton).shouldBe(visible).click();
        $(logoutButton).shouldNotBe(visible);
        WaitHelper.waitForAjax(driver, 10);
        return new WelcomePage(driver);
    }

    public ProfilePage goToProfile(){
        $(By.className("UserNavigateButton")).shouldBe(visible).click();
        return new ProfilePage(driver);
    }




}
