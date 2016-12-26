package com.pinterest.uk.pages.basePages;

import com.pinterest.uk.helpers.BaseTest;
import com.pinterest.uk.helpers.EnvironmentPropertiesHandler;
import com.pinterest.uk.helpers.User;
import com.pinterest.uk.helpers.UserPool;
import com.pinterest.uk.pages.HomePage;
import com.pinterest.uk.pages.MenuPage;
import com.pinterest.uk.pages.WelcomePage;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.io.TemporaryFilesystem;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Selenide.screenshot;

public class PinBaseTest extends BaseTest {

    static {
        PropertyConfigurator.configure("./src/test/resources/log4j.properties");
    }
    private static final Logger LOGGER = Logger.getLogger(PinBaseTest.class);

    protected WelcomePage welcomePage;
    protected HomePage homePage;
    protected User admin;


    public static final String GENERAL_GROUP = "for tests where auto-login is not required";

    private final EnvironmentPropertiesHandler properties = EnvironmentPropertiesHandler.getInstance();

    public WebDriver getDriver() {
        return driver;
    }


    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        super.setUp();
        manageDriver();
        admin = UserPool.getFreeAdminUser();
        Assert.assertTrue(admin != null, "There were no free users for the last " + UserPool.MAX_WAITING_SECONDS + " seconds in UserPool");
        LOGGER.info(Calendar.getInstance().getTime() + ": Test \"" + method.getDeclaringClass().getName() + "\" was started with user " + admin.getFullNaming());
        startTestDependingOnGroups(method);
    }


    public void takeScreenShotOnFailure(ITestResult testResult) throws IOException {
        try {
            if (testResult.getStatus() == ITestResult.FAILURE) {
                String timeFormat = new SimpleDateFormat("hh.mm.ss").format(new Date());
                String testName = testResult.getMethod().getTestClass().getRealClass().getSimpleName();
                screenshot(testName + "_" + timeFormat);
            }
        } catch (Throwable exception) {
            LOGGER.info("Failed to make screenshot for " + testResult.getTestClass().getName());
            LOGGER.info("Exception message: " + exception.getMessage());
        }

    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult testResult) throws IOException {
        takeScreenShotOnFailure(testResult);

        TemporaryFilesystem tempFS = TemporaryFilesystem.getDefaultTmpFS();
        tempFS.deleteTemporaryFiles();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Throwable e) {
                LOGGER.error("Failed to close browser", e);
            }

        }
        if (admin != null) {
            UserPool.releaseAdminUser(admin);
        }
    }

    public void startTestDependingOnGroups(Method method) {
        ArrayList<String> groupsList = new ArrayList<>(Arrays.asList(method.getDeclaredAnnotation(Test.class).groups()));
        if (groupsList.contains(GENERAL_GROUP)) {
            welcomePage = new WelcomePage(driver);
        } else {
            try {
                homePage = clickLoginButton().loginUser(admin);
            } catch (Throwable e) {
                LOGGER.error("Failed to auto-login with user " + admin.login, e);
            }

        }
    }

    private void manageDriver() {
        String url = properties.getProperty(EnvironmentPropertiesHandler.BASE_URL);
        //driver.manage().window().setPosition(new Point(1920, 24));
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(5, TimeUnit.SECONDS);
        driver.get(url);
    }

    public WelcomePage clickLoginButton() {
       // SelenideElement loginButton = $(By.xpath("//button[contains(., 'Log in')]"));
        return new WelcomePage(driver);
    }

    public MenuPage menu() {
        return new MenuPage(driver);
    }
}
