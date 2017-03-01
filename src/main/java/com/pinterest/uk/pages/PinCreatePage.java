package com.pinterest.uk.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.xpath;

public class PinCreatePage extends MenuPage {

    public PinCreatePage(WebDriver driver) {
        super(driver);
    }

    private By popup = className("TwoPaneModal");

    public HomePage saveToBoard(String boardName) {
        $(popup).find(className("allBoards")).find(xpath(".//*[contains(@class,'item')][contains(.,'" + boardName + "')]"))
                .shouldBe(visible).hover().find(saveButton).shouldBe(visible).click();
        return new HomePage(driver);
    }

}
