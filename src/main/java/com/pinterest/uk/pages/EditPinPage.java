package com.pinterest.uk.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class EditPinPage extends MenuPage {

    public EditPinPage(WebDriver driver) {
        super(driver);
    }

    By popup = By.className("standardForm");

    public BoardPage deletePin() {
        $(popup).find(By.className("deleteButton")).shouldBe(visible).click();
        $(By.className("ConfirmDialog")).find(By.className("confirm")).shouldBe(visible).click();
        return new BoardPage(driver);
    }
}
