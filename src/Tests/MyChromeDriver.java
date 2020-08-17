package Tests;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/*
 * MyChromeDriver
 * 
 * Can be modified as needed to support specific web applications, with automatic logging in, etc.
 */

public class MyChromeDriver extends ChromeDriver {
	

	/* Constructor */
	public MyChromeDriver() {
		super();
		this.manage().window().setSize(new Dimension(1400, 800));
	}
	
	/*
	 * Quits this driver instance.
	 */
	public void quit() {
		System.out.print("Closing driver... ");
		super.quit();
		System.out.println("Closed");
	}

	/*
	 *  Explicity waits up to 10s for the page title to match the expected title.
	 */
	public void waitForTitle(String title) {
		(new WebDriverWait(this, 10)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.getTitle().contentEquals(title);
			}
		});
	}
	
	/*
	 *  Retries click on an element if a StaleElementReferenceException is thrown.
	 *  This seems to occur when clicking on a returned result after performing a search for
	 *  a constituent infrequently. This function has not caused issues in my experience.
	 *  Waits 50ms between each attempt.
	 */
	public void clickRetry(By locator, int retries) throws InterruptedException {
		for (int i = retries; i > 0; i--) {
			try {
				this.findElement(locator).click();
				i = 0;
			} catch (StaleElementReferenceException e) {
				if (i > 0) {
					Thread.sleep(50); // improve this part in particular
					System.out.println("caught stale element"); // testing only
					continue;
				} else {
					throw e;
				}
			}
		}
	}	
	/* If no number of retries is specified, defaults to 100 */
	public void clickRetry(By locator) throws InterruptedException {
		this.clickRetry(locator, 100);
	}
	
	/*
	 *  Retries switching to a frame if a NoSuchFrameException is thrown.
	 *  There seem to be cases where an iframe will sometimes finish loading after the default frame, and
	 *  trying to switch to the iframe will cause an exception. This function is used to get around this
	 *  issue and has not caused issues in my experience. Waits 50ms between each attempt.
	 */
	public void switchToFrameRetry(int index, int retries) throws InterruptedException{
		for (int i = retries; i > 0; i--) {
			try {
				this.switchTo().frame(index);
				i = 0;
			} catch (NoSuchFrameException e) {
				if (i > 0) {
					Thread.sleep(50); // improve this part in particular
					System.out.println("caught unloaded frame"); // testing only
					continue;
				} else {
					throw e;
				}
			}
		}
	}
	/* If no number of retries is specified, defaults to 100 */
	public void switchToFrameRetry(int index) throws InterruptedException {
		this.switchToFrameRetry(index, 100);
	}
}
