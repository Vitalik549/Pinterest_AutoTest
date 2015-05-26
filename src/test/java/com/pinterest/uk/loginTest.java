package com.pinterest.uk;

import com.pinterest.uk.helpers.*;
import com.pinterest.uk.pages.*;
import com.pinterest.uk.pages.WelcomePage;
import org.testng.Assert;
import org.testng.annotations.Test;



public class loginTest extends PinBaseTest {

    private WelcomePage welcomePage;
    private SelectHelper selectHelper;
    private HomePage homePage;



    @Test
    public void loginTest() {
        welcomePage = clickLoginButton();
        homePage = welcomePage.loginUser();

        //Assert.assertFalse(selectHelper.isDisplayedByText(driver,"Invite friends to Pinterest"), "User was not logged in");
        Assert.assertTrue(homePage.isDisplayedText(), "User was not logged in");

        welcomePage = userLogOut();
      //  Assert.assertTrue(selectHelper.isDisplayedByText("Invite friends to Pinterest"),"User was not logged in");
        Assert.assertFalse(homePage.isDisplayedText(), "User was not logged out");






    }

}
