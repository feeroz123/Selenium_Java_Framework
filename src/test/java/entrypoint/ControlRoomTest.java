package entrypoint;

import static org.testng.Assert.assertTrue;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import base.Base;
import pageMethods.CommonPageMethods;
import pageObjectsRepo.CommonPageObj;
import utilities.Utilities;


/***
 * 
 * @author Feeroz Alam
 * @implNote This is the main entry point for the automation
 *
 */
public class ControlRoomTest extends Base {
	
	public ExtentHtmlReporter htmlReporter;
	public ExtentReports report;
	public ExtentTest test;
	
	CommonPageObj cpObj = new CommonPageObj();
	CommonPageMethods cpM = new CommonPageMethods();
	
	Utilities utils = new Utilities();
	
	private static final Logger log = LogManager.getLogger(ControlRoomTest.class.getName());
	
	
	@BeforeClass
	@Parameters("browser")
	public void setup(String browser) throws InterruptedException {
		
		htmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir")+ Utilities.getAppProperty("REPORT_DIR") + "AutomationReport.html");
		htmlReporter.config().setEncoding("utf-8");
		htmlReporter.config().setDocumentTitle("Automation Report");
		htmlReporter.config().setReportName("Message Viewer Automation Report");
		htmlReporter.config().setTheme(Theme.DARK);
		
		report = new ExtentReports();
		report.setSystemInfo("Project Name", "UIP Message Viewer");
		report.setSystemInfo("Organisation", "Aspect Software");
		report.setSystemInfo("Browser", browser.toUpperCase());
		report.attachReporter(htmlReporter);
		
		log.info("\n**************************************************************** TEST AUTOMATION STARTED ****************************************************************");
	
		launchApplication(browser);
	}

	

	//--------------------------------- General Tests ---------------------------------
	
	@Test (groups = {"general"})
	public void testApplicationLoad() {
		test = report.createTest("Testing Application Load");
		log.info("====== Testing Application Load ======");
		
		test.info("Verify presence of the company logo");
		cpM.waitTillElementNotVisible(cpObj.pageLoader, 15);
		boolean flag = exists(cpObj.mainLogo, 2, "Company Logo");
		assertTrue(flag, "Application not loaded");
	}

	
	/***
	 * Method to update Log, Extent Report and screenshot based on Test status of each method
	 * @param result
	 * @throws Exception
	 */
	@AfterMethod
	public void postTestMethodTasks(ITestResult result) throws Exception {
		String methodName = result.getMethod().getMethodName(); 
		
		if (result.getStatus() == ITestResult.FAILURE) {
			log.error(methodName + ": failed");
			
			String exceptionMessage = Arrays.toString(result.getThrowable().getStackTrace());
			test.fail("<details><summary><b><font color=red>Exception occurred, click to see details:" 
					+ "</font></b></summary>" + exceptionMessage.replaceAll(",", "<br>") + "</details> \n");
			
			String logText = "<b>Test Method " + methodName + " Failed</b>";
			Markup m = MarkupHelper.createLabel(logText, ExtentColor.RED);
			test.log(Status.FAIL, m);
			
			attachScreenshotInReport(test, methodName);
		}
		else if (result.getStatus() == ITestResult.SUCCESS) {
			log.info(methodName + ": passed");
			
			String logText = "<b>Test Method " + methodName + " Successful</b>";
			Markup m = MarkupHelper.createLabel(logText, ExtentColor.GREEN);
			test.log(Status.PASS, m);
		}
		else if (result.getStatus() == ITestResult.SKIP) {
			log.info(methodName + ": skipped");
			
			String logText = "<b>Test Method " + methodName + " Skipped</b>";
			Markup m = MarkupHelper.createLabel(logText, ExtentColor.GREY);
			test.log(Status.SKIP, m);
		}
		
	}
	
	
	@AfterClass
	public void tearDown() {
		closeApplication();
		log.info("\n**************************************************************** TEST AUTOMATION COMPLETED ****************************************************************\n\n\n");
		
		report.flush();
	}

}
