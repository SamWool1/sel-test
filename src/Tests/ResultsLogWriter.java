package Tests;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/*
 * ResultsLogWriter
 * 
 * Used to create a result log .html file, which seperates the test results into categories
 * that correspond to their packages currently, and reports the result of each test. Each
 * result log will also contain links to the test logs of each test, for user convenience.
 * Also creates a table of contents, which links to each category that directly contains a
 * test(s).
 * 
 * Optionally, can log only failed tests. This will remove passed tests from the given ArrayList
 * and HashMap references, so do not use this option until after you are done with those.
 */

@SuppressWarnings("rawtypes")
public class ResultsLogWriter {

	private String name;                          // Filename for the result log
	private FileWriter log;                       // FileWriter for this result log
	private ArrayList<Class> tests;               // List of the tests, as classes
	private HashMap<Class, NamedResult> results;  // Hash of the results for the tests, hashed 
	                                              // by the corresponding class.
	private boolean failedOnly;                   // If true, removes passed tests from test and results. Defaults to false.
	private ArrayList<ResultsLogNode> categories; // List of the categories and tests extracted from results.
	private ArrayList<String> tocLinks            // Holds the category names that will be used in the table of contents.
		= new ArrayList<String>();
	
	private String font = "Consolas";                // Selected font for the result log
	private String tab = "&nbsp;&nbsp;&nbsp;&nbsp;"; // Format specific tab of 4 spaces
	private String tab3 = "&nbsp;&nbsp;&nbsp;";      // Format specific tab of 3 spaces
	private String nl = "<br />";                    // Format specific newline character
	private String fileExtension = ".html";          // File extension, decides file type and determines formatting.
	private String space = "&nbsp;";                 // Format specific space character.
	private int spaceBuffer = 0;                     // Used in aligning test result statuses.
	private int maxDepth = 0;                        // Used in aligning test result statuses.

	/* Constructor */
	public ResultsLogWriter(String path, String name, String fileExtension, boolean failedOnly) throws IOException {
		this.name = name;
		this.log = new FileWriter(path + "/" + name + fileExtension);
		this.tests = new ArrayList<Class>();
		this.results = new HashMap<Class, NamedResult>();
		this.failedOnly = failedOnly;
		this.categories = new ArrayList<ResultsLogNode>();
	}

	/*
	 * When called, writes results to a result log, using the given hash of results. The list of tests is used
	 * to access NamedResults in the hash. Each test in the List should have a corresponding result in the hash.
	 */
	public void writeResults(ArrayList<Class> newTests, HashMap<Class, NamedResult> newResults)
			throws IOException {

		// Creates div for styling, and gives the page a header corresponding to the filename.
		write("<div style=\"font-family: " + font + "\">");
		write("<h1>" + name + "</h1>");
		
		// If there are more tests than there are results, the user is warned of this when opening the page.
		// This should never appear normally (I've never seen it occur personally).
		if (newTests.size() > newResults.size()) {
			write( "<h1 style=\"color: #f00\"" +
					   "ERROR: Mismatch in # of tests vs # of results. This page may not have accurate information." +
					   "</h1><br />"
					 );
			flush();
		}
		
		// Creates a copy of HashMap
		for (Class test : newTests) {
			tests.add(test);
			results.put(test, newResults.get(test));
		}

		// Remove all passed tests from the hash and list if this instance of 
		// ResultsLogWriter is set to only print test failures.
		if (failedOnly) {
			for (int i = 0; i < tests.size(); i++) {
				if (results.get(tests.get(i)).wasSuccessful()) {
					results.remove(tests.get(i));
					tests.remove(tests.get(i));
					i--;
				}
			}
		}

		// Create and sort categories for tests and their results
		createCategories();
		Collections.sort(categories);
		
		//Write table of contents
		writeTOC();
		// Write main body
		writeMainBody();

		// Write time taken for all tests
		write(nl + "<strong>Time taken: " + MyTotalTime.getTimeLength() + "</strong>" + nl);
		
		// Close div for styling
		write("</div>");
		close();
	}

	/*
	 * Fills the categories list in order to facilitate writing results later. Includes a check
	 * for duplicates to prevent categories from being non-unique.
	 */
	private void createCategories() {
		for (Class test : tests) {	
			
			// Get result for this test and its name
			NamedResult currResult = results.get(test);			
			String testNameFull = results.get(test).getName();
			
			// Splits the test name by '.', to seperate the categories and the test's name.
			String[] testCategories = testNameFull.split("\\.");
			
			ResultsLogNode parentCategory = null;
			for (int i = 0; i < testCategories.length; i++) {
				
				ResultsLogNode currCategory = new ResultsLogNode(testCategories[i], i, parentCategory);
				
				// Last pass will always be the test's name itsel, so special handle
				if (i == testCategories.length - 1) {
					// Adds as a test name rather than category. Doesn't bother checking for duplicates
					currCategory.setTest(true);
					currCategory.setResult(currResult);
					categories.add(currCategory);
					
					// Calculate the space buffer and max depth for formatting when writing main body
					spaceBuffer = (spaceBuffer > currCategory.getName().length() ? 
							spaceBuffer : currCategory.getName().length()); 
					maxDepth = (maxDepth > currCategory.getDepth() ?
							maxDepth : currCategory.getDepth());
				}
				else {
					// Check if category already exists in categories list. If not, add to list with proper depth.
					int index = assignCategoryDepth(currCategory);
					
					if (index == -1) { // Not a duplicate
						categories.add(currCategory);
						parentCategory = currCategory;
					}
					else {
						parentCategory = categories.get(index);
					}
				}
				
			}
		}
	}
	
	/*
	 * Checks if a category already exists in the categories list. If it does, returns the index.
	 * Else, returns -1.
	 */
	private int assignCategoryDepth(ResultsLogNode currCategory) {		
		for (int i = 0; i < categories.size(); i++) {
			
			// A category is a duplicate if it has the same name, same depth, and same
			// parent (by reference, not value) as a preexisting category
			if (categories.get(i).getName().equals(currCategory.getName()) &&
				categories.get(i).getDepth().equals(currCategory.getDepth()) &&
				categories.get(i).getParent() == currCategory.getParent()) {
				return i;
			}
		}
		
		return -1;
	}
	
	/*
	 * Writes the table of contents with approriate links, which are generated the the
	 * ResultLogNode's toString(), which are all unique. Each entry links to a category
	 * which directly contains a test(s).
	 */
	private void writeTOC() throws IOException {		
		
		// Gets the entries for the ToC, catching repeats.
		for (ResultsLogNode category : categories) {
			ResultsLogNode parent = category.getParent();
			if (category.isTest() && !tocLinksContains(parent.toString())) {
				tocLinks.add(parent.toString());
			}
		}
		
		// Writes a header for this section
		write("<h2>Category Links</h2>");
		
		// Writes the ToC entries
		write("<ul>");
		for (String link : tocLinks) {
			write("<li><a href=\"#" + link + "\">");
			write(link);
			write("</a></li><br />");
		}
		write("</ul>");
	}
	
	/*
	 * Checks if a potential entry for the ToC already exists in the list of ToC entries.
	 */
	private boolean tocLinksContains(String name) {
		for (String link : tocLinks) {
			if (link.equals(name)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Writes the main body of the result log, which is comprised of the various categories,
	 * test names, and corespnding test results.
	 */
	private void writeMainBody() throws IOException {
		// Write a header for this section
		write("<h2>Tests & Categories</h2>");
		spaceBuffer += 3;
		for (ResultsLogNode category : categories) {
				
			// Create indentations
			for (int i = 0; i < category.getDepth(); i++) {
				write("|" + tab3);
			}
			
			// Bold category names and add ID for ToC links
			if (!category.isTest()) {
				write("<span style=\"font-weight: bold\">");
				if (tocLinksContains(category.toString())) {
					write("<span id=\"" + category.toString() + "\">");
				}
			}
			// Add link to test log for each test
			else {
				write("<a href=\"" + category.getPath() + fileExtension + "\">");
			}
			
			// Writes category
			write(category.getName());
			
			// Closes link for tests
			if (category.isTest()) 
				write("</a>");
			
			// Record if test failed or passed
			if (category.isTest()) {
				for (int i = category.getName().length(); i < spaceBuffer; i++) {
					write(space);
				}
				for (int i = category.getDepth(); i < maxDepth; i++) {
					write(tab);
				}				
				write(category.wasSuccessful());
			}
					
			// Close spans for category names
			if (!category.isTest()) {
				if (tocLinksContains(category.toString())) {
					write("</span>");
				}
				write("</span>");
			}
			
			write(nl);
			flush();
		}
	}
	
	/* Helper functions to make FileWriter calls a little simpler */
	private void flush() throws IOException { log.flush(); }
	private void write(String myString) throws IOException { log.write(myString); }
	private void close() throws IOException { log.close(); }
}
