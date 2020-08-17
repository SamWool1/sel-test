package Tests.Control;

import Tests.MyTest;

/*
 * ControlFailTest
 * 
 * Demonstrates what a test log for a passed test looks like.
 */

public class ControlPass extends MyTest {
	
	public String giveTestDescription() {
		return "Demonstrates what a test log for a failed test looks like.";
	}

	public void test() {
		updateLogText("some action");
		updateLogText("some other action");
		changeCurrError("This error should not appear");
		updateLogText("some action right before a mistake");
		changeCurrError("This error should not appear either");
		updateLogText("This action will appear in the test log as well");
		changeCurrError("but this error will not");
		updateCurrError("and neither will this line");
		updateLogText("This should be the final line in the test log before a line stating the test was successful");
	}
}
