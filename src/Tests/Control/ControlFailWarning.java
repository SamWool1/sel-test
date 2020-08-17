package Tests.Control;

import static org.junit.Assert.fail;

import Tests.MyTest;

/*
 * ControlFailTest
 * 
 * Demonstrates what a test log for a failed test with a warning looks like.
 */

public class ControlFailWarning extends MyTest {
	
	public String giveTestDescription() {
		return "Demonstrates what a test log for a failed test looks like.";
	}

	public void test() {
		updateLogText("some action");
		updateLogText("some other action");
		changeCurrError("This error should not appear");
		updateLogText("some action right before a mistake");
		changeCurrError("This error should not appear either");
		addWarning("This warning will not change that the test fails");
		updateLogText("This action will appear in the test log, as tests don't stop after warnings");
		addWarning("A test can have as many warnings as needed");
		changeCurrError("This is the error that will appear");
		fail("This is where the test fails");
		addWarning("This warning should not appear, as it occurs after the test fails");
	}
}
