package com.pinterest.uk.flows;

import com.pinterest.uk.dataProviders.TestDataProvider;
import com.pinterest.uk.pages.basePages.PinBaseTest;
import com.pinterest.uk.pinObjects.Pin;
import org.testng.annotations.Test;

import static com.pinterest.uk.helpers.StatusWebElem.NOT_VISIBLE;
import static com.pinterest.uk.helpers.StatusWebElem.VISIBLE;

/*
---------------------------------------------------------------------------------------
|Steps:                     | Expected Result:                                        |
|---------------------------|---------------------------------------------------------|
|1. Find %pin               | %pin is present in search result                        |
|2. Save %pin to %board     | %pin was added to %board, proper notification received  |
|3. Remove %pin from %board | %pin was removed from %board                            |
---------------------------------------------------------------------------------------
*/

//todo manage test failure (pin won't be removed => next test will fall on NOT_VISIBLE assert)

public class PIN_003_addRemovePinOnBoard extends PinBaseTest {

    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "dpForTest_003")
    public void pinAddRemoveFlow(Pin pin) {
        menu().search(pin)
                .clickSaveOnPin(pin)
                .saveToBoard(pin.getBoardName())
                .checkNotification("Saved to " + pin.getBoardName())
                .goToProfile()
                .openBoard(pin.getBoardName())
                .checkPinVisibility(pin, VISIBLE)
                .editPin(pin)
                .deletePin()
                .checkPinVisibility(pin, NOT_VISIBLE);
    }
}
