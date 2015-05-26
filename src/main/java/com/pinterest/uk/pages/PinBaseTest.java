package com.pinterest.uk.pages;

import com.pinterest.uk.helpers.BaseTest;
import com.pinterest.uk.helpers.EnvironmentPropertiesHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.io.TemporaryFilesystem;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.util.concurrent.TimeUnit;

public class PinBaseTest extends BaseTest {
    protected HomePage homePage;
    private final EnvironmentPropertiesHandler properties = EnvironmentPropertiesHandler.getInstance();

    public WebDriver getDriver() {
        return driver;
    }

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        super.setUp();
        driver.manage().window().setPosition(new Point(1920, 24));
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
        String url = properties.getProperty(getEnvironment() + "." + EnvironmentPropertiesHandler.BASE_URL);
        driver.get(url);
        driver.manage().window().maximize();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        TemporaryFilesystem tempFS = TemporaryFilesystem.getDefaultTmpFS();
        tempFS.deleteTemporaryFiles();
        driver.quit();
    }

    public WelcomePage clickLoginButton() {
        driver.findElement(By.xpath("//*[@class = 'buttonText'][text() = 'Log in']")).click();
        return new WelcomePage(driver);
    }

    public WelcomePage userLogOut() {
        driver.findElement(By.xpath("//*[contains(@class, 'profileName')]")).click();
        driver.findElement(By.xpath("//*[@class = 'Button DropdownButton Module btn rounded userProfileMenu']")).click();
        driver.findElement(By.xpath("//*[@href= '/logout/']")).click();
        return new WelcomePage(driver);
    }


}
