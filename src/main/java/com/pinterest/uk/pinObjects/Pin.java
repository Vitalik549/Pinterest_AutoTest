package com.pinterest.uk.pinObjects;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class Pin {

    public Pin(String description, String boardName) {
        this.description = description;
        this.boardName = boardName;
    }

    private String description;
    private String userName;
    private String boardName;

    public SelenideElement getElement() {
        return $(By.xpath(".//*[contains(@class,'item')]//*[contains(@class,'pinDescription')][contains(.,'" + this.getDescription() + "')]//ancestor::*[contains(@class,'item')]"));
    }

    public String getDescription() {
        return description;
    }

    public String getUserName() {
        return userName;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }
}
