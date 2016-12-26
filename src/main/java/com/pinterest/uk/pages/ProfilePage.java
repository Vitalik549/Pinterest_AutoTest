package com.pinterest.uk.pages;

import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.xpath;

public class ProfilePage extends MenuPage {

    public ProfilePage(WebDriver driver) {
        super(driver);
    }

    public BoardPage openBoard(String boardName) {
        $(xpath("//*[contains(@class,'cardWrapper')]//*[contains(@class,'flex-auto')]/div[contains(.,'" + boardName + "')]")).shouldBe(visible).click();
        return new BoardPage(driver);
    }




}
