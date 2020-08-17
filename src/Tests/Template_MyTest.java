package Tests;

/*
 * Template class, shows how a basic JUnit LCMS Test would be written.
 * No purpose besides demonstrational value.
 */

public class Template_MyTest extends MyTest {
	
	public String giveTestDescription() {
		return "This is a description of this test" + nl
			 + "which is printed at the top of the" + nl
			 + "test log";
	}
	
	public void test() throws InterruptedException {
		// Code for tests goes here
	}
}
