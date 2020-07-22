package base;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.paulhammant.ngwebdriver.NgWebDriver;

import pageObjectsRepo.CommonPageObj;
import utilities.Utilities;


public class Base {

	public static WebDriver driver;
	public static NgWebDriver ngDriver;
	public static JavascriptExecutor jsDriver;
	public static Utilities utls;
	
	Properties prop = new Properties();
	CommonPageObj cpObj = new CommonPageObj();
	
	private final Logger log = LogManager.getLogger(Base.class.getName());
		
	
	/***
	 * Method to launch the application in browser of choice
	 * @param browser
	 * @throws InterruptedException 
	 */
	public void launchApplication(String browser) throws InterruptedException {
		launchBrowser(browser);	
		
		String baseURL = Utilities.getAppProperty("URL");
		driver.get(baseURL);
		
		if(baseURL.contains("https")) {
			switch (browser.toLowerCase()) {
				case "ie" :
					click(cpObj.moreInfoLink_IE, "More Information link");
					driver.navigate().to("javascript:document.getElementById('overridelink').click()");
					break;
				case "edge" :
					click(cpObj.moreInfoLink_Edge, "Details link");
					driver.navigate().to("javascript:document.getElementById('overridelink').click()");
					break;
			}
		
			log.debug("Accepted certificated warning");
		}
		
		log.debug("Application launched");
		waitTillElementNotVisible(cpObj.pageLoader, 15);
	}
	
	/***
	 * Method to shut down the browser
	 */
	public void closeApplication() {
		driver.quit();
		log.debug("Application closed");
	}
	
	/***
	 * Method to get WebDriver and launch the browser as passed in testng.xml file
	 * as parameter
	 * 
	 * @param browser
	 * @return WebDriver
	 */
	public void launchBrowser(String browser) {

		switch (browser.toLowerCase()) {

			case "firefox":
				System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir") + "/src/test/resources/webdrivers/geckodriver.exe");
				
				FirefoxOptions firefoxOptions = new FirefoxOptions();
				firefoxOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				
				driver = new FirefoxDriver(firefoxOptions);
				log.debug("New instance of Firefox browser was launched");
				break;
	
			case "chrome":
				System.setProperty("webdriver.chrome.silentOutput", "true");
				System.setProperty("webdriver.chrome.driver",
						System.getProperty("user.dir") + "/src/test/resources/webdrivers/chromedriver.exe");
				
				ChromeOptions chromeOptions = new ChromeOptions();
				chromeOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				chromeOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
				driver = new ChromeDriver(chromeOptions);
				log.debug("New instance of Chrome browser was launched");
				break;
	
			case "edge":
				/*
				 * For MicrosoftEdge HTML version 18 and above, the MicrosoftWebDriver can be
				 * installed by running the following command in an elevated Command Prompt with
				 * Administrator rights:
				 * 
				 * DISM.exe /Online /Add-Capability /CapabilityName:Microsoft.WebDriver~~~~0.0.1.0
				 * 
				 * Reference:
				 * https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/#downloads
				 * 
				 * The MicrosoftWebDriver.exe gets installed in system and can be located in
				 * "C:\Windows\System32\" path. This path is used to set the Edge driver path as
				 * a system property.
				 */
	
				System.setProperty("webdriver.edge.driver", Utilities.getAppProperty("MS_WEBDRIVER_EXE"));
				
				EdgeOptions edgeOptions = new EdgeOptions();
				edgeOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				driver = new EdgeDriver(edgeOptions);
				log.debug("New instance of MS Edge browser was launched");
				break;
	
			case "ie":
				// The IE browser is currently not compatible with this suite. Keeping the option here in case we find a solution in future.
				/***
				 * Refer for troubleshooting issue in automation with Internet Explorer:
				 * https://github.com/SeleniumHQ/selenium/wiki/InternetExplorerDriver#required-configuration
				 */
	
				System.setProperty("webdriver.ie.driver",
						System.getProperty("user.dir") + "/src/test/resources/webdrivers/IEDriverServer.exe");
				
				InternetExplorerOptions options = new InternetExplorerOptions();
				options.setCapability("nativeEvents", false);
				options.setCapability("unexpectedAlertBehaviour", "accept");
				options.setCapability("ignoreProtectedModeSettings", true);
				options.setCapability("disable-popup-blocking", true);
				options.setCapability("enablePersistentHover", true);
				options.setCapability("ignoreZoomSetting", true);
				options.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL,false);
				options.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
				options.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
				options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				
				driver = new InternetExplorerDriver(options);
				log.debug("New instance of Internet Explorer browser was launched");
				break;
	
			default:
				// Default browser : Chrome
				System.setProperty("webdriver.chrome.silentOutput", "true");
				System.setProperty("webdriver.chrome.driver",
						System.getProperty("user.dir") + "/src/test/resources/webdrivers/chromedriver.exe");
	
				ChromeOptions chromeOptions1 = new ChromeOptions();
				chromeOptions1.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				chromeOptions1.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
				driver = new ChromeDriver(chromeOptions1);
				log.debug("New instance of Chrome browser was launched as the default one");
				break;
		}

		jsDriver = (JavascriptExecutor) driver;
		ngDriver = new NgWebDriver(jsDriver).withRootSelector("\"app-root\"");
		
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		driver.manage().window().maximize();
	}
	
	
	public WebElement getElement(String xpath) {
		WebElement element = null;
		try {
			waitTillElementVisible(xpath, 10);
			element = driver.findElement(By.xpath(xpath));
			log.debug("Found the web element with xpath: " + xpath);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Web element was not found: " + xpath);
		}
		return element;
	}
	
	public List<WebElement> getElements(String xpath) {
		List<WebElement> elementList = null;
		try {
			waitTillElementVisible(xpath, 10);
			waitTillElementClickable(xpath, 5);
			elementList = driver.findElements(By.xpath(xpath));
			log.debug("Found the web elements with xpath: " + xpath);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Web elements were not found: " + xpath);
		}
		return elementList;
	}
	
	public void scrollElementIntoView(WebElement element) {
		jsDriver.executeScript("arguments[0].scrollIntoView(true);", element);
		log.debug("Scrolled element into view: " + element);
	}
	
	/***
	 * Enhanced method to enter text.
	 * 
	 * @param str_Xpath
	 * @param str_Data
	 * @param Str_description
	 */
	public void enterText(String str_Xpath, String str_Data, String Str_description) {
		try {
			waitTillElementVisible(str_Xpath, 10);
			waitTillElementClickable(str_Xpath, 5);
			driver.findElement(By.xpath(str_Xpath)).clear();
			driver.findElement(By.xpath(str_Xpath)).sendKeys(str_Data);
			log.debug("Entered: " + str_Data + " in: " + Str_description);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("*** Fail - Error while entering text");
			Assert.fail();
		}
	}

	/***
	 * Method to perform Click action on a given xpath of a web element
	 * 
	 * @param str_Xpath
	 * @param Str_description
	 */
	public void click(String str_Xpath, String Str_description) {
		try {
			waitTillElementVisible(str_Xpath, 10);
			waitTillElementClickable(str_Xpath, 5);
			driver.findElement(By.xpath(str_Xpath)).click();
			log.debug("Clicked on: " + Str_description);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("*** Fail - Exception occurred while clicking on: " + Str_description);
			Assert.fail();
		}
	}
	
	/***
	 * Method to perform Click action on a given web element
	 * 
	 * @param str_Xpath
	 * @param Str_description
	 */
	public void clickUsingWebElement(WebElement element, String Str_description) {
		try {
			new WebDriverWait(driver, 5).until(ExpectedConditions.elementToBeClickable(element));
			element.click();
			log.debug("Clicked on element: " + Str_description);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("*** Fail - Exception occurred while clicking on: " + Str_description);
			Assert.fail();
		}
	}
	
	/***
	 * Method to perform Click action on a given web element using Javascript
	 * This is for elements that are present, but difficult to click on.
	 * @param str_Xpath
	 * @param Str_description
	 */
	public void clickUsingJS(String str_Xpath, String Str_description) {
		try {
			WebElement element = getElement(str_Xpath);
			jsDriver.executeScript("arguments[0].click();", element);
			log.debug("Clicked on: " + Str_description);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("*** Fail - Exception occurred while clicking on: " + Str_description);
			Assert.fail();
		}	
	}

	/***
	 * Method to perform Click action on a given web element using Javascript
	 * This is for elements that are present, but difficult to click on.
	 * @param element
	 * @param Str_description
	 */
	public void clickElementUsingJS(WebElement element, String Str_description) {
		try {
			jsDriver.executeScript("arguments[0].click();", element);
			log.debug("Clicked on: " + Str_description);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("*** Fail - Exception occurred while clicking on: " + Str_description);
			Assert.fail();
		}	
	}

	/***
	 * Method to wait until Web element is present in screen
	 * 
	 * @param Str_ElementXpath
	 */
	public void waitTillElementVisible(String Str_ElementXpath, int timeoutSecs) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, timeoutSecs);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(Str_ElementXpath)));
			log.debug("Waited for visibility of: " + Str_ElementXpath);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("*** Fail - Exception occurred while checking for visibility of: " + Str_ElementXpath);
		}
	}

	/***
	 * Method to wait until Web element becomes clickable in screen
	 * 
	 * @param Str_ElementXpath
	 */
	public void waitTillElementClickable(String Str_ElementXpath, int timeoutSecs) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, timeoutSecs);
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(Str_ElementXpath)));
			log.debug("Waited for ability to click on: " + Str_ElementXpath);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("*** Fail - Exception occurred while checking for clickability of: " + Str_ElementXpath);
		}

	}
	
	/***
	 * Method to wait until element becomes invisible
	 * @param Str_ElementXpath
	 */
	public void waitTillElementNotVisible(String Str_ElementXpath, int timeoutSecs) {
		try {
			WebElement element = null;
			element = driver.findElement(By.xpath(Str_ElementXpath));
			WebDriverWait wait = new WebDriverWait(driver, timeoutSecs);
			wait.until(ExpectedConditions.invisibilityOf(element));
			log.debug("Waited for invisibility of: " + Str_ElementXpath);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("*** Fail - Exception occurred while checking for presence of: " + Str_ElementXpath);
		}
	}
	
	/***
	 * Method to get the text from a web element
	 * @param Str_ElementXpath
	 * @return
	 */
	public String getElementTextByID(String element_id, String element_description) {
		String element_text = "";
		try {
			element_text = driver.findElement(By.id(element_id)).getAttribute("value");
			log.debug("Got element text for element: " + element_description + " as: " + element_text);
		} catch (Exception e) {
			e.getMessage();
			log.error("*** Fail - Error while getting element text for element: " + element_description);
		}

		return element_text;
	}
	
	/***
	 * Method to get the text from a web element
	 * @param Str_ElementXpath
	 * @return
	 */
	public String getElementText(String Str_ElementXpath, String element_description) {
		String element_text = "";
		try {
			element_text = driver.findElement(By.xpath(Str_ElementXpath)).getText();
			log.debug("Got element text for element: " + element_description + " as: " + element_text);
		} catch (Exception e) {
			e.getMessage();
			log.error("*** Fail - Error while getting element text for element: " + element_description);
		}
		return element_text;
	}
	
	
	/***
	 * Method to verify if a Web element exists on the screen
	 * @param Str_XpathOfElement
	 * @param timeoutSecs
	 * @param Str_description
	 */
	public boolean exists(String Str_XpathOfElement, int timeoutSecs, String Str_description) {
		Boolean status = false;
		try {
			waitTillElementClickable(Str_XpathOfElement, timeoutSecs);
			status = driver.findElement(By.xpath(Str_XpathOfElement)).isDisplayed();
			if (status != null && status == true) {
				log.debug("Validated that " + Str_description + " exists");
				return true;
			} else {
				log.debug(Str_description + " does not exist");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("*** Fail - Error while checking for existence of element: " + Str_description);
			return false;
		}
	}


	/***
	 * Method to scroll down the page till the passed Web element location
	 * @param str_Xpath
	 * @throws Exception
	 */
	public void scrollDownUsingXpath(String str_Xpath) throws Exception {
		try {
			WebElement e = driver.findElement(By.xpath(str_Xpath));
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].scrollIntoView();", e);
			log.debug("Scrolled down");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("*** Fail - Error while scrolling down");
		}
	}

	/***
	 * Method to verify if a Web element does not exist on the screen
	 * 
	 * @param Str_XpathOfElement
	 * @param Str_description
	 */
	public boolean notExists(String Str_XpathOfElement, String Str_description) {
		try {
			Boolean status = (driver.findElement(By.xpath(Str_XpathOfElement))).isDisplayed();
			if (!status) {
				log.debug("Validate That " + Str_description + " not displayed");
				return true;
			} else {
				log.debug(Str_description + "  Displayed");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("*** Fail - Error while checking non existence of element: " + Str_description);
			return false;
		}
	}


	/***
	 * Method to select a value from a drop down list
	 * @param str_DropDownXpath
	 * @param str_DropDownValue
	 */
	public void selectItemFromDropDown(String str_DropDownXpath, String str_DropDownValue) {
		try {
			click(str_DropDownXpath, "List box");
			click(str_DropDownValue, "Drop down value");
			log.debug("Item was selected from drop down");
		}
		catch (Exception | Error e) {
			e.printStackTrace();
			log.error("*** Fail - Error while selecting item from drop down");
		}
	}

	/***
	 * Method to click on OK button in Alert window
	 */
	public void clickOKOnAlert() {
		try {
			Alert alert = driver.switchTo().alert();
			alert.accept();
			log.debug("Clicked OK on alert");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("*** Fail - Error while clicking OK on alert");
		}
	}

	/***
	 * Method to click on Cancel button in Alert window
	 */
	public void clickCancelOnAlert() {
		try {
			Alert alert = driver.switchTo().alert();
			alert.dismiss();
			log.debug("Clicked Cancel on Alert");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("*** Fail - Error while clicking Cancel on alert");
		}
	}
	
	/***
	 * Method to call screenshot utility method
	 * @param methodName
	 * @return
	 * @throws Exception
	 */
	public String getScreenshot(String methodName) throws Exception {
		log.debug("Capturing screenshot");
		return Utilities.takeScreenshot(methodName);
	}
	
	/***
	 * Method to take and attach screenshot in Extent report
	 * @param test
	 * @param methodName
	 * @throws Exception
	 */
	public void attachScreenshotInReport(ExtentTest test, String methodName) throws Exception {
		String screenshotPath = getScreenshot(methodName);
		try {
			log.debug("Received screenshot file name: " + screenshotPath);
			test.log(Status.INFO, "<b><font color=blue>" + "Screenshot:" + "</font></b>",
					MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
		} catch (IOException e) {
			test.fail("Test failed, cannot attach screenshot");
		}
	}

	
	/***
	 * Method to get current window's handle
	 * @return
	 */
	public String getCurrentWindowHandle() {
		log.debug("Getting current window's handle");
		return driver.getWindowHandle();
	}
	
	/***
	 * Method to get current browser tab URL
	 * @return
	 */
	public String getCurrentTabURL() {
		log.debug("Getting URL of current tab");
		return driver.getCurrentUrl();
	}
	
	/***
	 * A stable Method to open a new tab in existing browser
	 * @throws Exception
	 */
	public void openNewTab() throws Exception {
		log.debug("Opening new browser tab using Robot");
		Robot robot = new Robot();                          
		robot.keyPress(KeyEvent.VK_CONTROL); 
		robot.keyPress(KeyEvent.VK_T); 
		robot.keyRelease(KeyEvent.VK_CONTROL); 
		robot.keyRelease(KeyEvent.VK_T);
		waitForSecs(2); 	// Mandatory wait to allow opening new tab, without this it is not stable
	}
	
	/***
	 * Method to close current browser tab
	 */
	public void closeCurrentTab() {
		log.debug("Closing current tab");
		driver.close();
	}
	
	/***
	 * Method to verify if the browser tab URL contains the expected URL string
	 * @param expected_url
	 * @return
	 */
	public boolean verifyTabOpened(String expected_url) {
		String newTabURL = getCurrentTabURL();
		log.debug("Current tab URL : " + newTabURL);
		if (newTabURL.contains(expected_url)) {
			log.debug("Tab contains the expected URL");
			closeCurrentTab();
			return true;
		} else {
			log.debug("Tab does not contain the expected URL");
			return false;
		}
	}
	
	/***
	 * Method to verify that the new browser tab URL contains the expected URL string
	 * @param parentWindowHandle
	 * @param expected_url
	 * @return
	 */
	public boolean verifyNewTabURLAndReturn(String expected_url) {
		boolean result = false;

		try {
			ArrayList<String> handles = new ArrayList<String>(driver.getWindowHandles());
			driver.switchTo().window(handles.get(1));
			log.debug("Switched to new tab");
			result = verifyTabOpened(expected_url);
			driver.switchTo().window(handles.get(0));
			log.debug("Switched back to default page");
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
			log.error("*** Fail - Error happened at new tab");
		}

		return result;
	}
	
	/***
	 * Method to open the application in a new tab of the same browser
	 * @throws Exception 
	 */
	public void openApplicationInNewTab(String parent_handle) throws Exception {
		log.debug("Opening new tab in the same browser window");
		openNewTab();

		try {
			ArrayList<String> handles = new ArrayList<String>(driver.getWindowHandles());
			log.debug("Window handles found: " + handles);
			driver.switchTo().window(handles.get(1));
			log.debug("Switched to the new tab");
			log.debug("Opening the application in New tab handle: " + driver.getWindowHandle());
			String baseURL = Utilities.getAppProperty("URL");
			driver.get(baseURL);
			log.debug("Application opened in a new tab");
			
		} catch (Exception e) {
			log.debug("*** Fail - New tab was not opened");
			e.printStackTrace();
			Assert.fail();
		}

	}
	
	/***
	 * Method to switch to the window/tab handle provided
	 * @param parentWindowHandle
	 */
	public void switchToTab(String handle) {
		log.debug("Switching to tab/window: " + handle);
		if (handle != null) {
		driver.switchTo().window(handle);
		} 
		else {
			log.debug("***Fail - Switching to tab/window failed");
			Assert.fail();
		}		
	}
	
	/***
	 * Method to get the total duration in seconds from the passed web elemnt's xpath field. 
	 * The web element value should be convertible from String to Integer
	 * @param element_xpath
	 * @return timeValueSecs
	 */
	public int getTimeValueSecs(String element_xpath, String element_description) {
		log.debug("Fetching time value from progress bar");
		String elapsedTime = getElementText(element_xpath, element_description);
		
		String[] timeParts = elapsedTime.split(":");
		int minutePart = Integer.parseInt(timeParts[0]);
		int secsPart = Integer.parseInt(timeParts[1]);
		int timeValueSecs = (minutePart * 60) + secsPart;

		log.debug("Fetched time value as: " + timeValueSecs);	
		return timeValueSecs;
	}
	
	/**
	 * Generic method to wait for passed number of seconds
	 * @param seconds
	 * @throws InterruptedException
	 */
	public void waitForSecs(int seconds) throws InterruptedException {
		log.debug("Waiting for " + seconds + " seconds");
		Thread.sleep(seconds * 1000);
	}
	
  

} 
