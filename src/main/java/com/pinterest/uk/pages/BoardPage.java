package com.pinterest.uk.pages;

import com.codeborne.selenide.SelenideElement;
import com.pinterest.uk.helpers.StatusWebElem;
import com.pinterest.uk.pinObjects.Pin;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class BoardPage extends MenuPage {

    public BoardPage(WebDriver driver) {
        super(driver);
    }

    //todo: refactor!!!, same part as on homePage. Some general class for Pin search/usage should be created.
    public SelenideElement getPin(Pin pin){
        return $(By.xpath(".//*[contains(@class,'item')]//*[contains(@class,'pinDescription')][contains(.,'" + pin.getDescription() + "')]//ancestor::*[contains(@class,'item')]"));
    }

    public BoardPage checkPinVisibility(Pin pin, StatusWebElem statusWebElem) {
        checkElementStatus(getPin(pin), statusWebElem);
        return this;
    }


    public EditPinPage editPin(Pin pin) {
        getPin(pin).shouldBe(visible).find(By.className("editPin")).hover().shouldBe(visible).click();
        return new EditPinPage(driver);
    }

}
