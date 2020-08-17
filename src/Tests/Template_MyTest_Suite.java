package Tests;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

/*
 * Template class, shows how Suite Classes should be written. Has no purpose
 * besides demonstrational value.
 */

@SuppressWarnings("rawtypes")
public class Template_MyTest_Suite {
	public static Class[] getClasses() {
		return new Class[] { Template_MyTest.class };
	}
	
	/*
	 * All Suite classes need this to run as a main function. Can just copy-paste
	 * this over. No way to handle via inheritance, unfortunately.
	 */
	public static void main(String args[]) throws IOException {
		String[] arr = MethodHandles.lookup().lookupClass().getName().split("\\.");
		String name = arr[arr.length - 1]; 
		name = name.substring(0, name.length() - 6);
		SuiteRunner.run(getClasses(), name);
	}
}
