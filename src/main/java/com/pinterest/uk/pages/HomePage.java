package com.pinterest.uk.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends PinBasePage {

    @FindBy(id = "sysmenu.shippers")
    private WebElement shippersLink;

    public HomePage(WebDriver driver) {
        super(driver);
    }


   public boolean isDisplayedText() {
       try {
           return driver.findElement(By.name("q")).isDisplayed();
       }catch (NoSuchElementException e){
           return false;
       }
   }

}
