package com.pinterest.uk.helpers;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@SuppressWarnings("all")
public class WaitHelper {

    private static final Logger LOGGER = Logger.getLogger(WaitHelper.class);
    private static final long DEFAULT_WAIT_MILISECONDS = 200;

    // TODO Remove unused code found by UCDetector
    // public static void waitUntilCurrentUrlContains(WebDriver driver,
    // final String searchString, long timeOutInSeconds) {
    // WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
    // LOGGER.debug("Waiting for Current url to contain" + searchString);
    // wait.until(new ExpectedCondition<Boolean>() {
    // public Boolean apply(WebDriver drv) {
    // return drv.getCurrentUrl().contains(searchString);
    // }
    // });
    // }

    public static void waitUntilElementIsLoaded(WebDriver driver,
            final String xPath, long timeOutInSeconds) {
        WebDriverWait driverWait = new WebDriverWait(driver, timeOutInSeconds);
        LOGGER.debug("Waiting for Element to be loaded from xPath" + xPath);
        driverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xPath)));
    }

    // TODO Remove unused code found by UCDetector
    // public static void waitUntilWebElementDisplayed(WebDriver driver,
    // final WebElement element, long timeOutInSeconds) {
    // WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
    // LOGGER.debug("Waiting for element to be displayed");
    // wait.until(new ExpectedCondition<Boolean>() {
    // public Boolean apply(WebDriver driv) {
    // return element.isDisplayed();
    // }
    // });
    // }

    // TODO Remove unused code found by UCDetector
    // public static void waitUntilWebElementIsNotDisplayed(WebDriver driver,
    // final WebElement element, long timeOutInSeconds) {
    // WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
    // LOGGER.debug("Waiting for element not to be displayed");
    // wait.until(new ExpectedCondition<Boolean>() {
    // public Boolean apply(WebDriver drv) {
    // return !element.isDisplayed();
    // }
    // });
    // }

    // TODO Remove unused code found by UCDetector
    // public static boolean waitUrlContains(WebDriver driver,
    // String searchString, long timeoutInSeconds) {
    // boolean contains = false;
    // long timeOutMilliSeconds = timeoutInSeconds * 1000;
    // long t0 = System.currentTimeMillis();
    // long t1 = 0;
    // while (true) {
    // String url = driver.getCurrentUrl();
    // url = url.replace("%20", " ");
    // contains = url.contains(searchString);
    // if (contains) {
    // return true;
    // }
    // t1 = System.currentTimeMillis();
    // if ((t1 - t0) > timeOutMilliSeconds) {
    // return false;
    // }
    // defaultWait();
    // LOGGER.debug("Waiting for Url to contain" + searchString);
    // }
    // }

    // TODO Remove unused code found by UCDetector
    // public static boolean waitWebElementIsDisplayed(WebElement element,
    // long timeoutInSeconds) {
    // LOGGER.debug("Waiting for element to be displayed");
    // return waitWebElementIsDisplayed(timeoutInSeconds, element);
    // }

    //TODO Remove unused code found by UCDetector
    //public static boolean waitUrlChanged(WebDriver driver, String oldUrl,
    //long timeoutInSeconds) {
    //boolean urlChanged = false;
    //String newUrl = null;
    //long timeOutMilliSeconds = timeoutInSeconds * 1000;
    //long t0 = System.currentTimeMillis();
    //long t1 = 0;
    //while (true) {
    //newUrl = driver.getCurrentUrl();
    //urlChanged = !newUrl.equals(oldUrl);
    //if (urlChanged) {
    //return true;
    //}
    //t1 = System.currentTimeMillis();
    //if ((t1 - t0) > timeOutMilliSeconds) {
    //return false;
    //}
    //defaultWait();
    //}
    //}

    private static void defaultWait() {
        waitAdditional(DEFAULT_WAIT_MILISECONDS);
    }

    public static void waitAdditional(double seconds) {
        if (seconds <= 0) {
            return;
        }
        long milliseconds = (long) (seconds * 1000);
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WebDriverException(e);
        }
    }

    private static boolean waitWebElementIsDisplayed(long timeoutInSeconds,
            WebElement... element) {
        boolean isVisible = false;
        long timeOutMilliSeconds = timeoutInSeconds * 1000;
        long t0 = System.currentTimeMillis();
        long t1 = 0;
        while (true) {
            for (int i = 0; i < element.length; i++) {
                isVisible = element[i].isDisplayed();
                if (isVisible) {
                    return true;
                }
            }
            t1 = System.currentTimeMillis();
            if ((t1 - t0) > timeOutMilliSeconds) {
                return false;
            }
            defaultWait();
        }
    }

    public static void waitUntilElementDisplayed(WebDriver driver,
            final By by, long timeOutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        LOGGER.debug("Waiting for element to be displayed");
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }
}
