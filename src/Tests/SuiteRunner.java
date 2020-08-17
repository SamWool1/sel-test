package Tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.runner.JUnitCore;

/*
 * Helper class for the Suites. Runs and writes results to a result log specific to that suite.
 */

@SuppressWarnings("rawtypes")
public abstract class SuiteRunner {

	private final static int PREFIX_LENGTH = 12;
	private final static String FILE_EXTENSION = ".html";

	/*
	 * Adds classes to an ArrayList Necessary for result writer, as standard JUnit
	 * suite wasn't kept in mind when designing ResultsLogWriter
	 */
	public static void addSuite(ArrayList<Class> tests, Class[] classes) {
		for (int i = 0; i < classes.length; i++) {
			tests.add(classes[i]);
		}
	}

	public static void run(Class[] classes, String suiteName) throws IOException {
		System.out.println("Current suite: " + suiteName);

		// Init tests amd HashMap
		HashMap<Class, NamedResult> results = new HashMap<Class, NamedResult>();
		ArrayList<Class> tests = new ArrayList<Class>();
		for (Class curr : classes) {
			tests.add(curr);
		}

		// Make directory if none exists
		Path myPath = Paths.get(System.getProperty("user.dir") + "/TestLogs" + MyTotalTime.get());
		File directory = new File(myPath.toString());
		if (!directory.exists()) {
			directory.mkdirs();
		}

		// Create space buffer
		int spaceBuffer = 0;
		for (Class test : tests) {
			String testName = test.toString().substring(PREFIX_LENGTH);
			if (testName.length() > spaceBuffer) {
				spaceBuffer = testName.length();
			}
		}
		spaceBuffer += 3;

		// Init time start for tests
		MyTotalTime.initStartEpoch();

		// Run and print test results to console
		for (Class test : tests) {
			// Gets tests name
			String testName = test.toString().substring(PREFIX_LENGTH);
			System.out.println("Starting test " + testName);

			// Runs tests and stores result as a NamedResult
			results.put(test, new NamedResult(testName, JUnitCore.runClasses(test)));
		}

		// Init end time
		MyTotalTime.initEndEpoch();

		// Print test results summary to console
		for (Class test : tests) {
			System.out.print("RESULTS: " + results.get(test).getName());

			for (int j = results.get(test).getName().length(); j < spaceBuffer; j++) {
				System.out.print(" ");
			}

			if (results.get(test).wasSuccessful()) {
				System.out.println(" SUCCESSFUL");
			} else {
				System.out.println(" FAILED");
			}
		}

		// Write result log with passed and failed tests
		ResultsLogWriter resultLog = new ResultsLogWriter(myPath.toString(), 
				"ResultsLog" + suiteName, FILE_EXTENSION, false);
		resultLog.writeResults(tests, results);

		// Print some summary information to console
		System.out.println("Done");
		System.out.println("\nExiting program");
		System.out.println("Working directory was: " + System.getProperty("user.dir"));
		System.out.println("Time taken was: " + MyTotalTime.getTimeLength());
	}
}
