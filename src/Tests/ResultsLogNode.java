package Tests;
/*
 * ResultsLogNode
 * 
 * Used in ResultsLogWriter. Used to represent either a unique category or a unique test.
 * Aids in properly organizing the categories, and for saving various information related
 * to each test/category. 
 */

public class ResultsLogNode implements Comparable<ResultsLogNode>{
	
	private String name = null;     // Name of the category/test
	private Integer depth = null;   // The "depth", used for calculating and finding indentations.
	private boolean isTest = false; // If false, this node represents a category (default).
	private ResultsLogNode parent;  // The immediate supercategory, whose depth = this.depth - 1
	private NamedResult result;     // Only initialized if this is a test node. Saves that test's result + name.
	
	/* Constructor */
	public ResultsLogNode (String name, Integer depth, ResultsLogNode parent) {
		this.name = name;
		this.depth = depth;
		this.parent = parent;
	}
	
	/* Setters */
	public void setTest(boolean isTest) { 
		this.isTest = isTest; 
	}	
	public void setResult(NamedResult result) {
		this.result = result;
	}
	
	/* Getters */
	public String getName() { return name; }
	public Integer getDepth() { return depth; }
	public boolean isTest() { return isTest; } // Equivalent to "getTest()"
	public ResultsLogNode getParent() { return parent; }
	public NamedResult getResult() { return result; }
	
	/* 
	 * Returns a different String depending on whether or not the corresponding test was successful.
	 * Used to aid in writing the test result in the result logs. Returns an empty string if called on
	 * a category instead of a test.
	 */
	public String wasSuccessful() {
		if (isTest && result.wasSuccessful()) {
			return "<span style=\"color: #0A0\">SUCCESSFUL</span>";
		}
		else if (isTest && !result.wasSuccessful()){
			return "<span style=\"color: #F00\">FAILED</span>";
		}
		else {
			System.out.println("logic error - wasSuccessful() called on category");
			return "";
		}
	}
	
	/*
	 * Returns a path to a local file, which should correspond to the test log for the coresponding test.
	 * Used to create links to test logs in the result log.
	 */
	public String getPath() {
		String path;
		if (parent == null)
			path = "./" + name;
		else
			path = parent.getPath() + "/" + name;
		
		return path;
	}
	
	/*
	 * The full name of this category. Calls parents in order to ensure the name is unique - tests for
	 * Lists and Constituents may have the same name (i.e. "Edit"), but different parents, so their
	 * toString() will be different.
	 */
	public String toString() {
		if (parent == null)
			return name;
		else
			return parent + "." + name;
	}

	/*
	 * Used for sorting. After a List containing ResultLogNodes is sorted, the test and subcategories
	 * will be ordered properly in relation to their respective categories, which aids greatly when
	 * writing the result log.
	 */
	public int compareTo(ResultsLogNode o) {
		return this.toString().compareTo(o.toString());
	}
}
