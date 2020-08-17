package Tests.Control;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;

public class AJAXTest {
	ChromeDriver driver;

	@Before
	public void setup() {
		// Init driver
		driver = new ChromeDriver();
	}

	@Test
	public void test() {
		// Open ajax page
		driver.get("https://www.w3schools.com/xml/tryit.asp?filename=tryajax_first");

		String expected = "AJAX\nAJAX is not a programming language.\nAJAX is a technique for "
				+ "accessing web servers from a web page.\nAJAX stands for Asynchronous JavaScript"
				+ " And XML.";

		driver.switchTo().frame("iframeResult");
		driver.findElement(By.cssSelector("#demo button")).click();

		// Will need to include waits
		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return driver.findElement(By.cssSelector("body")).getText().equals(expected);
			}
		});
	}

	@After
	public void teardown() {
		driver.close();
	}
}
