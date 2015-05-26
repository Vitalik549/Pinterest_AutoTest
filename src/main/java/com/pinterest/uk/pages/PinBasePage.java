package com.pinterest.uk.pages;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.util.concurrent.TimeUnit;

public class PinBasePage {
    private long timeout = 30;
    protected Logger log = Logger.getLogger(this.getClass());
    protected WebDriver driver;

    protected PinBasePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isLoaded(String pageTitle) {
        log.info("loading a page with title ' " + "' " + driver.getTitle());
        return (driver.getTitle().contains(pageTitle));
    }

    protected boolean isElementDisplayed(By by) {
        try {
            return driver.findElement(by).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTextDisplayed(String textToBeChecked) {
        try {
            return driver.findElement(By.xpath("//*[contains(text(), '" + textToBeChecked+ "')]")).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isElementDisplayed(WebElement scope, By by) {
        try {
            return scope.findElement(by).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isElementDisplayedNow(By by) {
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        try {
            return driver.findElement(by).isDisplayed();
        } catch (Exception e) {
            return false;
        } finally {
            driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
        }
    }
}
