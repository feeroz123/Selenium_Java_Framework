package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import base.Base;


public class Utilities extends Base {
	
	private final static Logger log = LogManager.getLogger(Utilities.class.getName());
	
	/***
	 * Method to take screenshot and return the path of the screenshot file
	 * @param WebDriver
	 * 
	 * @throws Exception
	 */
	public static String takeScreenshot(String methodName ) throws Exception {
		String fileName = getScreenshotName(methodName);
		String directory = System.getProperty("user.dir") + getAppProperty("SCREENSHOT_DIR");
		new File(directory).mkdirs();
		String screenshotFile = directory + fileName;
		
		try {
			File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(screenshot, new File(screenshotFile));
			log.info("Screenshot captured as: " + screenshotFile);
		} 	catch (Exception e) {
			log.error("*** Screenshot capture failed");
			e.printStackTrace();
		}
		return screenshotFile;
	}
	
	/***
	 * Method to generate and return the file name of the screenshot file
	 * @param methodName
	 * @return
	 */
	public static String getScreenshotName(String methodName) {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileDate = sdf.format(d);
		String fileName = methodName + fileDate + ".png";
		log.debug("Screenshot name generated");
		return fileName;
	}
	
	/***
	 * Method to return a random non-zero integer number within the given limit
	 * 
	 * @return
	 */
	public int getRandomInt(int limit) {
		Random random = new Random();
		int randomInteger = Math.abs(random.nextInt(limit));
		log.debug("Random integer generated as: " + randomInteger);
		return randomInteger;
	}
	
	/***
	 * Method to generate a random AlphaNumeric String
	 * 
	 * @author kickdrum
	 *
	 */
	public String getAlphaNumericString(int string_size) {
			String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
			StringBuilder sb = new StringBuilder(string_size);

			for (int i = 0; i < string_size; i++) {
				int index = (int) (AlphaNumericString.length() * Math.random());
				sb.append(AlphaNumericString.charAt(index));
			}
			return sb.toString();
		}

	/***
	 * Method to read a property key value from application.properties file
	 * @param propertyKey
	 * @return
	 */
	public static String getAppProperty(String propertyKey) {
		Properties props = new Properties();

		try {
			InputStream appProps = new FileInputStream(System.getProperty("user.dir") + "/resources/configs/application.properties");
			props.load(appProps);
			log.debug("The 'application.properties' file was loaded");
		} catch (IOException e) {
			log.error("*** The 'application.properties' file was not found");
			e.printStackTrace();
		}
		String result = props.getProperty(propertyKey);
		log.debug("Retrieved the value of "+ propertyKey +" from 'application.properties' file");
		return String.valueOf(result);
	}
	
	
	
}
