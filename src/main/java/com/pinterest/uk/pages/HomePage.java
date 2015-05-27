package com.pinterest.uk.pages;

import com.pinterest.uk.pinObjects.Pin;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class HomePage extends MenuPage {

    public HomePage(WebDriver driver) {
        super(driver);
    }

    String pinSelector = "*[contains(@class,'item')]"; //todo: probably any other elem will contain "item" as part of class, to be replaced with 100% unique selector
    By allPins = By.xpath(".//" + pinSelector);

    By pinDescription = By.xpath(".//*[contains(@class,'pinDescription')]");
    By pinSocialIcons = By.xpath(".//*[contains(@class,'SocialIconsCounts')]");
    By pinUserName = By.xpath(".//*[contains(@class,'creditName')]");
    By pinBoardName = By.xpath(".//*[contains(@class,'creditTitle')]");
    By pinSaveButton = By.xpath(".//button[contains(.,'Save')]");


    private By pinByDescription(String description) {
        return By.xpath(".//*[contains(@class,'pinDescription')][contains(.,'" + description + "')]//ancestor::" + pinSelector);
    }

    public PinCreatePage clickSaveOnPin(Pin pin) {
        $(allPins).find(pinByDescription(pin.getDescription())).shouldBe(visible.because("Pin with description <" + pin.getDescription() + "> is expected to be found"))
                .find(pinSaveButton).hover().shouldBe(visible).click();
        return new PinCreatePage(driver);
    }

    public HomePage checkNotification(String expectedText) {
        //$(By.className("ToastBase")) todo  check later correct selector
        Assert.assertEquals($(By.className("RepinSuccessToast")).shouldBe(visible).getText().trim(), expectedText, "Incorrect text in appeared notification");
        return this;
    }
}
