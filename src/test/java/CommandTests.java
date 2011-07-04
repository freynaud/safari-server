import java.io.IOException;
import java.net.URL;

import org.openqa.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CommandTests {

	protected final int load = 1;

	Driver d = new Driver();

	@BeforeClass
	public void startServer() throws Exception {
		d.start();
	}

	@Test(invocationCount = load, threadPoolSize = load)
	public void getTitle() throws InterruptedException, IOException {
		WebDriver driver = null;
		try {
			driver = new RemoteWebDriver(new URL("http://localhost:9999/wd/hub"), DesiredCapabilities.firefox());
			driver.get("http://ebay.co.uk");
			System.out.println(driver.getTitle());
		} finally {
			driver.quit();

		}

	}

	@Test(invocationCount = load, threadPoolSize = load, expectedExceptions = NoSuchElementException.class)
	public void getElementNeg() throws InterruptedException, IOException {
		WebDriver driver = null;
		try {
			driver = new RemoteWebDriver(new URL("http://localhost:9999/wd/hub"), DesiredCapabilities.firefox());
			driver.get("http://ebay.co.uk");
			WebElement el = driver.findElement(By.id("test"));
		} finally {
			driver.quit();

		}
	}

	@Test(invocationCount = load, threadPoolSize = load)
	public void getElement() throws InterruptedException, IOException {
		WebDriver driver = null;
		try {
			driver = new RemoteWebDriver(new URL("http://localhost:9999/wd/hub"), DesiredCapabilities.firefox());
			driver.get("http://ebay.co.uk");
			WebElement el = driver.findElement(By.id("registerLink"));
		} finally {
			driver.quit();

		}
	}

	@Test(invocationCount = load, threadPoolSize = load)
	public void setValue() throws InterruptedException, IOException {
		WebDriver driver = null;
		try {
			driver = new RemoteWebDriver(new URL("http://localhost:9999/wd/hub"), DesiredCapabilities.firefox());
			driver.get("http://ebay.co.uk");
			WebElement el = driver.findElement(By.id("_nkw"));
			el.sendKeys("ferret");
		} finally {
			driver.quit();
		}
	}

	@Test(invocationCount = load, threadPoolSize = load)
	public void setValueGoogle() throws InterruptedException, IOException {
		WebDriver driver = null;
		try {
			driver = new RemoteWebDriver(new URL("http://localhost:9999/wd/hub"), DesiredCapabilities.firefox());
			driver.get("http://google.com");
			WebElement el = driver.findElement(By.name("q"));
			el.sendKeys("ferret");
		} finally {
			driver.quit();
		}
	}

	@Test(invocationCount = load, threadPoolSize = load)
	public void click() throws InterruptedException, IOException {
		WebDriver driver = null;
		try {
			driver = new RemoteWebDriver(new URL("http://localhost:9999/wd/hub"), DesiredCapabilities.firefox());
			driver.get("http://ebay.co.uk");
			WebElement el = driver.findElement(By.id("registerLink"));
			el.click();
		} finally {
			driver.quit();
		}
	}

	@Test(invocationCount = load, threadPoolSize = load)
	public void getURL() throws InterruptedException, IOException {
		WebDriver driver = null;
		try {
			String url = "http://www.ebay.co.uk/";
			driver = new RemoteWebDriver(new URL("http://localhost:9999/wd/hub"), DesiredCapabilities.firefox());
			driver.get(url);
			Assert.assertEquals(driver.getCurrentUrl(), url);
		} finally {
			driver.quit();
		}
	}

	@AfterClass
	public void stop() throws InterruptedException {
		d.stop();
	}
}
