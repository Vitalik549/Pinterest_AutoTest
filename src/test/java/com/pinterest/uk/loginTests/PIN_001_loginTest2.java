package com.pinterest.uk.loginTests;

import com.pinterest.uk.pages.basePages.PinBaseTest;
import org.testng.annotations.Test;

import static com.pinterest.uk.helpers.StatusWebElem.VISIBLE;

public class PIN_001_loginTest2 extends PinBaseTest {

    @Test(groups = GENERAL_GROUP)
    public void loginTestMock() {
        clickLoginButton();
    }
}
