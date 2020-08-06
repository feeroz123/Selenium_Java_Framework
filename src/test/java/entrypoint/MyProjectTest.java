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
public class MyProjectTest extends Base {
	
	public ExtentHtmlReporter htmlReporter;
	public ExtentReports report;
	public ExtentTest test;
	
	// Test Rail integration variables
	public APIClient testRail_client;
	public String TEST_SUITE_ID;
	public Long TEST_RUN_ID;
	public String TC_ID; // Test Case Id is specific to Test Suite in which it is present
	
	CommonPageObj cpObj = new CommonPageObj();
	CommonPageMethods cpM = new CommonPageMethods();
	
	Utilities utils = new Utilities();
	
	private static final Logger log = LogManager.getLogger(MyProjectTest.class.getName());
	
	
	@BeforeClass
	@Parameters("browser")
	public void setup(String browser) throws InterruptedException {
		// Test Rail integration variables
		testRail_client = Utilities.getTestRailConnClient();
		TEST_SUITE_ID = Utilities.getAppProperty("TR_TEST_SUITE_ID");
		TEST_RUN_ID = Utilities.getNewTestRun(testRail_client, TEST_SUITE_ID);
		
		htmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir")+ Utilities.getAppProperty("REPORT_DIR") + "AutomationReport.html");
		htmlReporter.config().setEncoding("utf-8");
		htmlReporter.config().setDocumentTitle("Automation Report");
		htmlReporter.config().setReportName("MyProjectTest Automation Report");
		htmlReporter.config().setTheme(Theme.DARK);
		
		report = new ExtentReports();
		report.setSystemInfo("Project Name", "My project");
		report.setSystemInfo("Organisation", "My Company");
		report.setSystemInfo("Browser", browser.toUpperCase());
		report.attachReporter(htmlReporter);
		
		log.info("\n**************************************************************** TEST AUTOMATION STARTED ****************************************************************");
	
		launchApplication(browser);
	}

	/**
	 * Resets the Test Case Id before every Test method, to avoid multiple test results in same test case
	 */
	@BeforeMethod
	public void resetTCID() {
		log.info("Resetting Test Case Id to Null");
		TC_ID = "";
	}

	//--------------------------------- General Tests ---------------------------------
	
	@Test (groups = {"general"})
	public void testApplicationLoad() {
		// Test Rail integration - Original Test Case Id of this test in Test Rail inside the Test Suite
		TC_ID="207";
		
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
			Utilities.updateTestRailTestResults(testRail_client, TEST_RUN_ID, TC_ID, statuses.TC_FAILED_STATUS);
			
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
			Utilities.updateTestRailTestResults(testRail_client, TEST_RUN_ID, TC_ID, statuses.TC_PASSED_STATUS);
			
			String logText = "<b>Test Method " + methodName + " Successful</b>";
			Markup m = MarkupHelper.createLabel(logText, ExtentColor.GREEN);
			test.log(Status.PASS, m);
		}
		else if (result.getStatus() == ITestResult.SKIP) {
			log.info(methodName + ": skipped");
			Utilities.updateTestRailTestResults(testRail_client, TEST_RUN_ID, TC_ID, statuses.TC_BLOCKED_STATUS);
			
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
