package Tests;

import static org.junit.Assert.fail;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/*
 * MyTest
 * 
 * Abstract class used as a basic framework for creating JUnit tests. Designed to handle as much
 * as possible by default, to allow tests to be written faster. Also creates test logs as .html 
 * files, which are linked to by the result logs.
 */

@SuppressWarnings("unused")
public abstract class MyTest {

	protected FileWriter testLog;   // Filewriter for the test log
	protected String testLogName;   // Name of the test log file
	protected boolean pass = false; // Tracks if test passes or not
	protected String testName;      // Name of the test
	protected String filePath;      // File path for the test log

	private String logText = "";            // Contains the contents of the log (non-errors)
	private String currError = "";          // Contains the errors for the log
	private String filePathPrefix           // Destintation for the file path in the computer's directories
		= System.getProperty("user.dir")
		+ "/TestLogs" + MyTotalTime.get() + "/";
	
	private String fileExtension = ".html"; // File extension, for file type and formatting
	private String font = "Consolas";       // Selected font for test log
	protected String nl = "<br />";         // Format specific newline character
	private boolean warning = false;        // Detects if warning has been added. Fails tests if so.
	private MyLocalTime time;				// Calculates the amount of time the test takes 
	
	protected WebDriver driver;             // This test's instance of the webdriver
	protected JavascriptExecutor js;        // Object for executing js in browser
	protected HashMap<String, Object> vars; // Holds variables for js
	
	/*
	 * Force programmer to provide a test description. Can be same as comment for test
	 */
	public abstract String giveTestDescription();
	
	/**************************************
	 * SET-UP, TEST, AND TEARDOWN RELATED *
	 **************************************/
	
	/*
	 * Creates driver, sets up important globals, and assigns test name and filepath
	 */
	@Before
	public void setUp() {
		// Get the starting time of this test
		time = new MyLocalTime();
		time.initStartEpoch();
		
		// Create driver, JavaScript executor, and hash for variables
		createDriver();
		js = (JavascriptExecutor) driver;
		vars = new HashMap<String, Object>();

		// Sets file path and file name for the test log
		String[] className = this.getClass().getName().split("\\.");
		testName = className[className.length - 1];
		filePath = "";
		for (int i = 1; i <= className.length - 2; i++) {
			filePath += className[i];
			if (i != className.length - 2) filePath += "/";
		}
	}
	
	/*
	 * Runs the actual test by calling the test function, which must be supplied by child classes.
	 * If the child object's test runs to completion and the test passes, pass will be set to true.
	 * If not, pass will remain false, and the test will be considered failed.
	 */
	@Test
	public void doTest() throws InterruptedException, IOException {
		// Runs test method
		test();
		
		// If test didn't stop (test did not fail), the test passes
		pass = true;
		System.out.println("Test successful");
		
		
		// Fails test if there was a warning, and prints message to console
		if (warning) {
			System.out.println("Warning detected");
		}		
		if (warning && pass) {
			fail("Warning detected. Please review test manually");
		}
	}
	
	/*
	 * This is where the code for the actual test goes. If you export a test from the IDE, this function
	 * is where the @Test section's code would go normally.
	 */
	public abstract void test() throws InterruptedException, IOException;

	/*
	 * Closes the driver, and writes to the test log.
	 */
	@After
	public void tearDown() throws IOException, InterruptedException {
		// Closes driver
		System.out.println("Ending test");
		quitDriver();
		
		// Creates test log and adds description
		createTestLog();
		testLog.write("<p><strong>" + giveTestDescription() + "</p></strong>");
		
		// Writes the logs from the test that were executed
		testLog.write(logText);
		
		// Writes ending statuses
		if (pass) {
			testLog.write("<span style=\"color: #0A0\">TEST SUCCESSFUL</span>" + nl);
		}
		else {
			testLog.write(currError);
		}
		
		if (warning) {
			testLog.write(nl + "<span style=\"color: #FFFF00; background-color: #000\">"
					         + "Warning detected. Please review test manually."
					         + "</span>" + nl);
		}
		
		// Writes time
		testLog.write(nl + "<strong>Time taken: " + time.getTimeLength() + "</strong>");
		
		// Closes div and file
		testLog.write("</ div>");
		testLog.close();
	}
	
	/* Creates the driver, with neccessary set up */
	protected void createDriver() {
		// Set chromedriver.exe location
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/chromedriver.exe");
		driver = new MyChromeDriver();
	}

	/* Quits the driver */
	protected void quitDriver() {
		driver.quit();
	}
	
	/********************
	 * TEST LOG RELATED *
	 ********************/

	/*
	 *  Creates a file for test logs and returns a FileWriter for that file.
	 *  Puts it on desktop for now, can change later by editing filePathPrefix.
	 */
	protected void createTestLog() throws IOException {
		Path myPath = Paths.get(filePathPrefix + filePath);
		File directory = new File(myPath.toString());
		if (!Files.exists(myPath)) {
			System.out.println(myPath.toString() + " not found, creating");
			directory.mkdirs();
		}
	
		testLogName = myPath.toString() + "/" + testName + fileExtension;
		System.out.print("Creating " + testLogName + "... ");
		try {
			testLog = new FileWriter(new File(testLogName));
		} catch (IOException e) {
			System.out.println("IOException - FileWriter testLog not initialized");
			e.printStackTrace();
			System.exit(1);
			testLog = null;
		}
		testLog.write("<div style=\"font-family: " + font + "\">");
		testLog.write("<h1>" + testName + "</h1>");
		System.out.println("Done\n");
	}

	/* Adds a new line to logText */
	protected void updateLogText(String logTextUpdate) {
		logText += logTextUpdate + nl;
	}

	/* Adds a new line to currError */
	protected void updateCurrError(String currErrorUpdate) {
		currError += "<span style=\"color: #F00\"><strong>ERROR:</strong> " + currErrorUpdate + "</span>" + nl;
	}
	
	/* Adds a warning to log text */
	protected void addWarning(String warningUpdate) {
		logText += "<span style=\"color: #FFFF00; background-color: #000\">"
				+ "<strong>WARNING:</strong> " + warningUpdate + "</span>" + nl;
		warning = true;
	}
	
	/* Replaces currError with given String */
	protected void changeCurrError(String currErrorChanged) {
		currError = "<span style=\"color: #F00\"><strong>ERROR:</strong> " + currErrorChanged + "</span>" + nl;
	}
	
	/* Getters */
	protected String getLogText()   { return logText;   }
	protected String getCurrError() { return currError; }	
	public String getTestName() {
		return testName;
	}
	
	/********************
	 * HELPER FUNCTIONS *
	 ********************/

	/*
	 *  Fills out a ui-autocomplete-input field. Necessary for certain fields. hiddenID refers to the
	 *  id of the hidden dropdown that appears when entering information into the field, itemXPath is
	 *  the XPath to the desired option in the dropdown to select.
	 */
	protected void fillAutocompleteManual(String input, String field, String hiddenID, String itemXPath) {
		// Enter information into the field
		WebElement autofield = driver.findElement(By.id(field));
		autofield.sendKeys(input);

		// wait for hidden dropdown to be displayed
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(hiddenID)));

		// select/click on the right option
		wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath(itemXPath), input));
		WebElement hiddenInput = driver.findElement(By.xpath(itemXPath));
		hiddenInput.click();

		// Wait until dropdown is hidden again
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(hiddenID)));
	}

	/*
	 *  Fill out a ui-autocomplete-input field, with an unrecognized value. Args are the same as above.
	 */
	protected void fillAutocompleteManualUnrecognized(String input, String field, String hiddenID, String itemXPath) {
		// Enter information into the field
		WebElement autofield = driver.findElement(By.id(field));
		autofield.sendKeys(input);

		// wait for hidden dropdown to be displayed
		WebDriverWait wait = new WebDriverWait(driver, 10);

		// Wait until dropdown is hidden again
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(hiddenID)));
	}
	
	/*
	 * Fills a jquery ui autocomplete form via JavaScript. Should bypass some issues with manual filling
	 */
	protected void fillAutocomplete(String input, String field) {
		js.executeScript(field + ".value = \"" + input + "\";");
	}

	/*
	 *  Clear a ui-autocomplete-input field. Most likely not needed over a standard clear, but
	 *  could be useful in the future.
	 */
	protected void clearAutocomplete(String field) {
		WebElement autofield = driver.findElement(By.id(field));
		autofield.clear();
	}

	/*
	 *  Get a specific raw entry from a SlickGrid element. Likely in .json format.
	 *  Returning as an ArrayList<String> doesn't seem to work, for some reason.
	 *  
	 *  If you're getting a JavaScript error that grid isn't initialized, that's because
	 *  the SlickGrid is probably named something besides "grid" (for example, on My Lists,
	 *  it's named listSearchGrid). Just check the id on the grid and change "grid" below
	 *  to the relevant id.
	 */
	protected String getSlickGridEntry(int index) {
		@SuppressWarnings("unchecked")
		ArrayList<String> rawEntry = (ArrayList<String>) js
				.executeScript("return $(grid.getDataItem(" + (index) + "));");
		
		// Remove leading '[{' and trailing '}]'
		return rawEntry.toString().substring(2, rawEntry.toString().length() - 2);
	}
	
	/* 
	 * Flips name
	 * Given : First M. Last
	 * Output: Last, First  M.
	 */
	public String flipName(String name) {
		String [] arr = name.split("\\s");
		String newName = arr[arr.length - 1].trim() + ", ";
		for (int i = 0; i < arr.length - 1; i++) {
			newName += arr[i].trim();
			
			if (newName.charAt(newName.length() - 1) != ' ') {
				newName += " ";
			}
		}
		
		return newName.trim();
	}
}
