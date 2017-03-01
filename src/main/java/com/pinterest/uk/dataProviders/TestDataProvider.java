package com.pinterest.uk.dataProviders;


import com.pinterest.uk.pinObjects.Pin;
import org.testng.annotations.DataProvider;

public class TestDataProvider {

    public static final String dpForTest_003 = "dpForTest_003";

    @DataProvider (name = "dpForTest_003")
    public static  Object[][] dpForTest_003() {
        return new Object[][]{{
                new Pin("Quality Assurance", "test")
        }};
    }
}
