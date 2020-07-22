# My project Automation
Test Automation for My Company - My project.

<b>Tools necessary for this project:</b>
1. Java JDK 8 & above
2. Apache Maven 3.6.3
3. Git (to checkout the code)
4. Eclipse (any recent version)
5. Browsers - Chrome, Firefox, Safari (on Mac OS), Edge
Note - The chromedriver.exe file supplied in this project works with Chrome browser version 83. If the target machine has different version, please download and use the related driver file. 
The latest webdriver file should be placed in '\resources\webdrivers' location in this project.


<b>Other installation:</b>
For MicrosoftEdge HTML version 18 and above, the MicrosoftWebDriver can be installed by running the following command in an elevated Command Prompt with Administrator rights:

  DISM.exe /Online /Add-Capability /CapabilityName:Microsoft.WebDriver~~~~0.0.1.0

Reference: https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/#downloads

The MicrosoftWebDriver.exe gets installed in system and can be located in "C:\Windows\System32\" path. This path is used to set the Edge driver path as a system property.


<b>Pre-requisites:</b>
The following conditions must be satisfied for the Automation Suite to complete successfully:
  <To be updated>


<b>Execution Steps:</b>

  Method 1 - 
  1) When executing from Eclipse, right click on the 'testng.xml' file and 'Run As' TestNG test.

  Method 2 -
  1) Open a command prompt and navigate to the project home directory.
  2) Execute as -> mvn clean install


