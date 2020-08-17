package Tests;
import org.junit.runner.Result;

/*
 * NamedResult
 * 
 * Object used to assign a name (String) to a Result, in order to facilitate keeping
 * track of which JUnit test result corresponds to which test.
 */

public class NamedResult {
	
	private String name;   // Name of the corresponding JUnit test
	private Result result; // Result of the corresponding JUnit test
	
	/* Constructor */
	public NamedResult(String name, Result result) {
		this.name = name;
		this.result = result;
	}
	
	/* Getters */
	public String getName() { return name; }
	public Result getResult() { return result; }
	
	/* 
	 * Returns true if the JUnit test corresponding the the Result was successful, and false if
	 * it was not 
	 */
	public boolean wasSuccessful() { 
		return result.wasSuccessful(); 
	}
}
