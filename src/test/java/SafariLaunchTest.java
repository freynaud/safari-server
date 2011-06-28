import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.openqa.Driver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SafariLaunchTest {

	Driver d = new Driver();

	@BeforeClass
	public void startServer() throws Exception {
		d.start();
	}

	@Test
	public void safariStarts() throws InterruptedException, IOException {
		WebDriver driver = null;
		try {
			driver = new RemoteWebDriver(new URL("http://localhost:9999/wd/hub"), DesiredCapabilities.firefox());
			driver.get("http://ebay.com");
			driver.get("http://ebay.co.uk");
		} finally {
			driver.quit();

		}

	}
	

	@Test(expectedExceptions = WebDriverException.class)
	public void safariBadUrl() throws MalformedURLException {
		WebDriver driver = null;
		try {
			driver = new RemoteWebDriver(new URL("http://localhost:9999/wd/hub"), DesiredCapabilities.firefox());
			driver.get("http://ebay.com");
			driver.get("htfgdkfghklsjdfh");
		} finally {
			driver.quit();
		}

	}

	@Test(invocationCount = 10, threadPoolSize = 10, timeOut = 20000)
	public void safariStartsInParallel() throws MalformedURLException, InterruptedException {
		WebDriver driver = null;
		try {
			driver = new RemoteWebDriver(new URL("http://localhost:9999/wd/hub"), DesiredCapabilities.firefox());
			driver.get("http://ebay.co.uk");
		} finally {
			driver.quit();
		}

	}

	@AfterClass
	public void stop() throws InterruptedException {
		d.stop();
	}
}
