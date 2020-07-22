package pageObjectsRepo;


/***
 * 
 * @author Feeroz Alam
 * @implNote This class file contains addresses of web elements present in home page of the application
 *
 */

public class CommonPageObj {
	
	// Internet Explorer Settings
	public String moreInfoLink_IE = "//div[@id='moreInformationAlign']//span";
	
	// MS Edge Settings
	public String moreInfoLink_Edge = "//a[@id='moreInformationDropdownLink']";
	
	public String mainLogo = "//div[@class='logo']";
	public String pageLoader = "//div[@class='lds-spinner']";
	public String overlay = "//div[contains(@class, 'overlay')]";
	
}
