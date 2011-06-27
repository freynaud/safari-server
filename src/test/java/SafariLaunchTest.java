
import java.net.MalformedURLException;
import java.net.URL;


import org.openqa.Driver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


public class SafariLaunchTest {

	Driver d = new Driver();
	@BeforeClass
	public void startServer() throws Exception{
		d.start();
	}
	
	@Test
	public void safariStarts() throws MalformedURLException, InterruptedException{
		WebDriver driver = new RemoteWebDriver(new URL("http://localhost:9999/wd/hub"),DesiredCapabilities.firefox());
		driver.get("http://ebay.com");
		Thread.sleep(10000);
		driver.quit();
	}
	
	@AfterClass
	public void stop() throws InterruptedException{
		d.stop();
	}
}
