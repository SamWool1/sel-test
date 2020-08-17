import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import Tests.MyTotalTime;
import Tests.NamedResult;
import Tests.ResultsLogWriter;
import Tests.Control.*;

import org.junit.runner.JUnitCore;

/*
 * Handles the main execution of tests and calls to ResultsLogWriter.
 */

@SuppressWarnings({ "rawtypes" })
public class SelTest {

	private final static int PREFIX_LENGTH = 12; // Length of the prefix for test names
	
	public static void main(String[] args) throws IOException, InterruptedException {

		String fileExtension = ".html"; // File type for ResultsLogFull
		String nl = "<br />";           // Appropriate newline character for corresponding file type
		String space = "&nbsp;";        // Appropriate space character for corresponding file type
		String font = "Consolas";       // Selected font for ResultsLogFull

		// Create lists for tests and results
		HashMap<Class, NamedResult> results = new HashMap<Class, NamedResult>();
		ArrayList<Class> tests = new ArrayList<Class>();

		/* Add tests to the test list */
		
		// Control tests, demonstrate different test outcomes
		addControls(tests);

		// Creates spaceBuffer, for formatting in ResultsLogFull
		int spaceBuffer = 0;
		for (Class test : tests) {
			String testName = test.toString().substring(PREFIX_LENGTH);
			if (testName.length() > spaceBuffer) {
				spaceBuffer = testName.length();
			}
		}
		spaceBuffer += 3;

		// Create a directory for files, if none exists
		String resultsFilename = "ResultsLogFull" + fileExtension;
		System.out.print("\n\nCreating " + resultsFilename + "... ");
		Path myPath = Paths.get(System.getProperty("user.dir") + "/TestLogs" + MyTotalTime.get());
		File directory = new File(myPath.toString());
		if (!directory.exists()) {
			directory.mkdirs();
		}
		
		// Create file for results, updates with links and results as tests run
		FileWriter resultsLogFull = new FileWriter(myPath.toString() + "/" + resultsFilename);
		System.out.println("Done");
		resultsLogFull.write("<div style=\"font-family: " + font + "\">");
		resultsLogFull.write("<h1>ResultsLogFull</h1>");
		resultsLogFull.flush();
		
		// Init start time for tests
		MyTotalTime.initStartEpoch();
		
		// Run and print test results to console and ResultsLogFull
		for (Class test : tests) {	
			// Gets tests name
			String testName = test.toString().substring(PREFIX_LENGTH);
			System.out.println("Starting test " + testName);
			
			// Runs tests and stores result as a NamedResult
			results.put(test, new NamedResult(testName, JUnitCore.runClasses(test)));
			
			// Create and write link to test log for ResultLogFull
			String link = "./";
			link += testName.replace('.', '/') + fileExtension;
			resultsLogFull.write("<a href=\"" + link + "\">" + testName + "</a>");
			
			// Formatting stuff
			for (int j = testName.length(); j < spaceBuffer; j++) {
				resultsLogFull.write(space);
			}

			// Writes result to ResultLogFull
			if (results.get(test).wasSuccessful()) {
				resultsLogFull.write(space + "<span style=\"color: #0A0\">SUCCESSFUL</span>" + nl);
			} else {
				resultsLogFull.write(space + "<span style=\"color: #F00\">FAILED</span>" + nl);
			}
			
			resultsLogFull.flush();
		}
		
		// Close file and exit
		System.out.println("Closing " + resultsFilename + "... ");
		resultsLogFull.write("</ div>");
		resultsLogFull.close();
		
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
		ResultsLogWriter resultLog = new ResultsLogWriter(myPath.toString(), "ResultsLog", fileExtension, false);
		resultLog.writeResults(tests, results);
		
		// Write result log with failed tests only
		ResultsLogWriter resultLogFail = new ResultsLogWriter(myPath.toString(), "ResultsLogFail", fileExtension, true);
		resultLogFail.writeResults(tests, results);

		// Print some summary information to console
		System.out.println("Done");
		System.out.println("\nExiting program");
		System.out.println("Working directory was: " + System.getProperty("user.dir"));		
		System.out.println("Time taken was: " + MyTotalTime.getTimeLength());
	}

	/*
	 * Adds classes to an ArrayList Necessary for result writer, as standard JUnit
	 * suite wasn't kept in mind when designing ResultsLogWriter
	 */	
	public static void addSuite(ArrayList<Class> tests, Class[] classes) {
		for (int i = 0; i < classes.length; i++) {
			tests.add(classes[i]);
		}
	}
	
	/* Add control tests */
	public static void addControls(ArrayList<Class> tests) {
		tests.add(ControlFail.class);
		tests.add(ControlFailWarning.class);
		tests.add(ControlPass.class);
		tests.add(ControlPassWarning.class);	
		
//		 tests.add(AJAXTest.class);
	}
}