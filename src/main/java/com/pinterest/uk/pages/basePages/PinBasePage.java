package com.pinterest.uk.pages.basePages;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.pinterest.uk.helpers.StatusWebElem;
import com.pinterest.uk.helpers.WaitHelper;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.pinterest.uk.helpers.StatusWebElem.*;

public abstract class PinBasePage {

    private long timeout = 30;
    protected Logger log = Logger.getLogger(this.getClass());
    protected WebDriver driver;

    protected PinBasePage(WebDriver driver) {
        this.driver = driver;
    }

    protected boolean isLoaded(String pageTitle) {
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

    protected boolean isTextDisplayed(String textToBeChecked) {
        try {
            return driver.findElement(By.xpath("//*[contains(text(), '" + textToBeChecked + "')]")).isDisplayed();
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

    protected WebElement findTextEquals(String textToBeFound) {
        try {
            return driver.findElement(By.xpath("//*[contains(text(), '" + textToBeFound + "')]"));
        } catch (Exception e) {
            return null;
        }
    }

    public WebElement findTextContains(String textToBeFound) {
        try {
            return driver.findElement(By.xpath("//*[text() = '" + textToBeFound + "']"));
        } catch (Exception e) {
            return null;
        }
    }

    protected WebElement findById(String ID) {
        try {
            return driver.findElement(By.id(ID));
        } catch (Exception e) {
            return null;
        }
    }

    protected WebElement findByName(String name) {
        try {
            return driver.findElement(By.id(name));
        } catch (Exception e) {
            return null;
        }
    }

    protected boolean isDisplayedTextContains(WebDriver driver1, String textToBeChecked) {
        try {
            return driver1.findElement(By.xpath("//*[contains(text(), '" + textToBeChecked + "')]")).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    protected boolean isDisplayedTextEquals(WebDriver driver1, String textToBeChecked) {
        try {
            return driver1.findElement(By.xpath("//*[text() = '" + textToBeChecked + "']")).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    protected boolean isDisplayedById(String ID) {
        try {
            return driver.findElement(By.id(ID)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isElementDisplayed(WebElement webElement) {
        try {
            return webElement.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected static void checkElementStatus(SelenideElement selenideElement, StatusWebElem expectedStatus) {
        if (expectedStatus.equals(VISIBLE)) {
            $(selenideElement).shouldBe(visible);
        } else if (expectedStatus.equals(ENABLED)) {
            $(selenideElement).shouldBe(visible, enabled);
        } else if (expectedStatus.equals(DISABLED)) {
            $(selenideElement).shouldBe(visible, disabled);
        } else if (expectedStatus.equals(NOT_VISIBLE)) {
            isElementsNotVisibleNow($(selenideElement));
        } else {
            Assert.assertTrue(false, "Incorrect expected status. Possible values: ENABLED / DISABLED / NOT_AVAILABLE");
        }
    }

    public static void isElementsNotVisibleNow(SelenideElement... selenideElements) {
        WebDriver driver = WebDriverRunner.getWebDriver();
        WaitHelper.setImplicitWait(driver, 0.5);
        for (SelenideElement elem : selenideElements) {
            $(elem).shouldNotBe(visible);
        }
        WaitHelper.setImplicitWaitDefault(driver);
    }
}
