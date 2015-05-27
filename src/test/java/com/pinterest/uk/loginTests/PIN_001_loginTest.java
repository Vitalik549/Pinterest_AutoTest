package com.pinterest.uk.loginTests;

import com.pinterest.uk.pages.HomePage;
import com.pinterest.uk.pages.basePages.PinBaseTest;
import com.pinterest.uk.pages.WelcomePage;
import org.testng.Assert;
import org.testng.annotations.Test;


public class PIN_001_loginTest extends PinBaseTest {

    private WelcomePage welcomePage;
    private HomePage homePage;

    @Test(groups = GENERAL_GROUP)
    public void login() {
        homePage = clickLoginButton().loginUser(admin);
        Assert.assertTrue(homePage.isMainSearchDisplayed(), "User was not logged in");
        welcomePage = homePage.userLogOut();

        Assert.assertTrue(welcomePage.isLoginButtonDisplayed(), "User was not logged out");
    }
}
