package com.pinterest.uk.pages;

import com.pinterest.uk.helpers.StatusWebElem;
import com.pinterest.uk.pinObjects.Pin;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Condition.visible;

public class BoardPage extends MenuPage {

    public BoardPage(WebDriver driver) {
        super(driver);
    }

    //todo: refactor!!!, same part as on homePage. Some general class for Pin search/usage should be created.

    public BoardPage checkPinVisibility(Pin pin, StatusWebElem statusWebElem) {
        checkElementStatus(pin.getElement(), statusWebElem);
        return this;
    }

    public EditPinPage editPin(Pin pin) {
        pin.getElement().shouldBe(visible).find(By.className("editPin")).hover().shouldBe(visible).click();
        return new EditPinPage(driver);
    }
}
