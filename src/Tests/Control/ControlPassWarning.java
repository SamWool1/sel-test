package Tests.Control;

import Tests.MyTest;

/*
 * ControlFailTest
 * 
 * Demonstrates what a test log for a passed test with a warning looks like.
 */

public class ControlPassWarning extends MyTest {
	
	public String giveTestDescription() {
		return "Demonstrates what a test log for a failed test looks like.";
	}

	public void test() {
		updateLogText("some action");
		updateLogText("some other action");
		changeCurrError("This error should not appear");
		updateLogText("some action right before a mistake");
		changeCurrError("This error should not appear either");
		addWarning("This warning will cause the test to fail, despite normally passing");
		updateLogText("This action will appear in the test log, as tests don't stop after warnings");
		addWarning("A test can have as many warnings as needed");
	}
}
