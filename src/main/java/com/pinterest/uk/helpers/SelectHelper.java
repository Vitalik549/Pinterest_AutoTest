package com.pinterest.uk.helpers;

import com.pinterest.uk.pages.PinBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SelectHelper extends PinBasePage{

    public SelectHelper(WebDriver driver) {
        super(driver);
    }


    public WebElement findByText(String textToBeFound) {
        try {
            return driver.findElement(By.xpath("//*[contains(text(), '" + textToBeFound+ "')]"));
        } catch (Exception e) {
            return null;
        }
    }

    public WebElement findById(String ID) {
        try {
            return driver.findElement(By.id(ID));
        } catch (Exception e) {
            return null;
        }
    }

    public WebElement findByName(String name) {
        try {
            return driver.findElement(By.id(name));
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isDisplayedByText(WebDriver driver1, String textToBeChecked) {
        try {
            return driver1.findElement(By.xpath("//*[contains(text(), '" + textToBeChecked+ "')]")).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean isDisplayedById(String ID) {
        try {
            return driver.findElement(By.id(ID)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDisplayedByName(String name) {
        try {
            return driver.findElement(By.name(name)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

}
