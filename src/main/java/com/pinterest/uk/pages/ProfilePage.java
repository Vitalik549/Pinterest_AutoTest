package com.pinterest.uk.pages;

import com.codeborne.selenide.Condition;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.xpath;

public class ProfilePage extends MenuPage {

    public ProfilePage(WebDriver driver) {
        super(driver);
    }


    public BoardPage openBoard(String boardName) {
        $(xpath("//*[contains(@class,'boardCoverImage')]//*[contains(@class,'boardName')][contains(.,'" + boardName + "')]")).shouldBe(visible).click();
        return new BoardPage(driver);
    }




}
