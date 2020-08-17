package Tests.Control;

import static org.junit.Assert.fail;

import Tests.MyTest;

/*
 * ControlFailTest
 * 
 * Demonstrates what a test log for a failed test looks like.
 */

public class ControlFail extends MyTest {
	
	public String giveTestDescription() {
		return "Demonstrates what a test log for a failed test looks like.";
	}

	public void test() {
		updateLogText("some action");
		updateLogText("some other action");
		changeCurrError("This error should not appear");
		updateLogText("some action right before a mistake");
		changeCurrError("This is what an error looks like in a test log");
		updateCurrError("Errors can take up as many lines as needed");
		fail("The test will always fail here");
		updateLogText("This action should not appear in the test log");
	}
}
