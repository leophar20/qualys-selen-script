import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.opencsv.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

public class sslScriptReader {
	
	
	
	private static final String COMMA_DELIMITER = ",";
	static String homePage = "https://www.ssllabs.com/ssltest";
    private static WebDriver driver = null;
    static List<String[]> data = new ArrayList<String[]>();
    static List<String> urlLinks = new ArrayList<String>();
    
	
    
    
    
    
    public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		getUrls();// getting the urls from .csv files
		System.out.println("finished importing urls");
		System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
		File file = new File("/Users/internship/Desktop/csvexample/result3.csv"); //the output of the file
		
		 try { 
		        // create FileWriter object with file as parameter 
		        FileWriter outputfile = new FileWriter(file);
		        // create CSVWriter object filewriter object as parameter 
		        CSVWriter writer = new CSVWriter(outputfile); 
		  
		        data.add(new String[] { "URl Links", "Result","TlS 1.3","TLS 1.2","TLS 1.1","TLS 1.0","SSL 3", "SSl 2", "Unsecured CipherText"}); // adding the header to the excel
		        
		   /*   driver = new ChromeDriver();
		       	driver.manage().window().maximize();
		        driver.get(homePage);
		        checkTLS("https://myvideo.kp.org/sf-00000522/uat/index.php"); */
		        
		        
		   for (String link: urlLinks) 
		       { // start of the automation
		        	driver = new ChromeDriver();
		        	driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
		        	driver.manage().window().maximize();
		            driver.get(homePage);
		            checkTLS(link);// main method that has the parameter of each link
		            driver.close();
		            
		        } 
		       
		       writer.writeAll(data); // write all the data inn excel
		  
		        // closing writer connection 
		        writer.close(); 
		        System.out.println("Writing to excel done");
		    } 
		    catch (IOException e) { 
		        // TODO Auto-generated catch block 
		        e.printStackTrace(); 
		    } 
        
        
	}
	

	
	private static void checkTLS(String links)  {// main method

	
		WebElement submit = driver.findElement(By.xpath("//*[@id=\"main\"]/div[1]/center/form/table/tbody/tr[1]/td[3]/input")); //path for the submit button
		WebElement checkBox = driver.findElement(By.xpath("//*[@id=\"hideResults\"]")); //path for the checkbox
		WebElement inputLink =  driver.findElement(By.xpath("//*[@id=\"main\"]/div[1]/center/form/table/tbody/tr[1]/td[2]/input"));//path for where to input link
		checkBox.click();
		inputLink.sendKeys(links);
		submit.click();
		
		
		System.out.println("Testing for the URL : "+ links);
		
		try {
			
			
			WebDriverWait wait = new WebDriverWait(driver,1000);
			//wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div[3]/div[5]/div[3]/div[2]/table[1]")));
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@id=\"warningBox\"]/img")));
				System.out.println("Starting Scan......");
				Thread.sleep(6000);
				if(driver.findElements(By.xpath("//*[@id=\"multiTable\"]/tbody/tr[5]/td[2]/span[1]/b/a")).size()>0)//if there is 2 links. click the second
				{
					WebElement click2ndLink = driver.findElement(By.xpath("//*[@id=\"multiTable\"]/tbody/tr[5]/td[2]/span[1]/b/a"));
					click2ndLink.click();
				}
				if ((driver.findElements(By.xpath("//*[@id=\"warningBox\"]")).size() > 0 ) && 
				(driver.findElement(By.xpath("//*[@id=\"warningBox\"]")).getText().equals("Assessment failed: Unable to connect to the server") ||
				driver.findElement(By.xpath("//*[@id=\"warningBox\"]")).getText().equals("Assessment failed: Internal Server error")||
				driver.findElement(By.xpath("//*[@id=\"warningBox\"]")).getText().equals("Assessment failed: No secure protocols supported")||
				driver.findElement(By.xpath("//*[@id=\"warningBox\"]")).getText().equals("Assessment failed: Failed to communicate with the secure server")))	
				//
					{
					
					data.add(new String[] {links," not available" });
					System.out.println(links+ " not available");
				}
				else if ((driver.findElements(By.xpath("/html/body/div[1]/div[3]/div[1]/center/div")).size() >0) && 
						driver.findElement(By.xpath("/html/body/div[1]/div[3]/div[1]/center/div")).getText().equals("Unable to resolve domain name"))
				{
					data.add(new String[] {links," not available" });
					System.out.println(links+ " not available");
				}
				
				else
				{
					clicking(); //script for clicking all the span and settings.
					
					//  script for taking the rsult
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"rating\"]")));
					//when certificate no sni.
					if((driver.findElements(By.xpath("//*[@id=\"main\"]/div[5]/div[3]/div[1]/span[1]")).size() >0) && 
					driver.findElement(By.xpath("//*[@id=\"main\"]/div[5]/div[3]/div[1]/span[1]")).getText().equals("No SNI"))
					{
						
						//*[@id="main"]/div[3]/div[4]/div[2]/table[1]/tbody/tr[6]/td[2]
						String ssl2 = driver.findElement(By.xpath("//*[@id=\"main\"]/div[5]/div[4]/div[2]/table[1]/tbody/tr[6]/td[2]")).getText();
						String ssl3 = driver.findElement(By.xpath("//*[@id=\"main\"]/div[5]/div[4]/div[2]/table[1]/tbody/tr[5]/td[2]")).getText();
						String tls1 = driver.findElement(By.xpath("//*[@id=\"main\"]/div[5]/div[4]/div[2]/table[1]/tbody/tr[4]/td[2]")).getText();
						String tls11 = driver.findElement(By.xpath("//*[@id=\"main\"]/div[5]/div[4]/div[2]/table[1]/tbody/tr[3]/td[2]")).getText();
						String tls12 = driver.findElement(By.xpath("//*[@id=\"main\"]/div[5]/div[4]/div[2]/table[1]/tbody/tr[2]/td[2]")).getText();
						String tls13 = driver.findElement(By.xpath("//*[@id=\"main\"]/div[5]/div[4]/div[2]/table[1]/tbody/tr[1]/td[2]")).getText();
						String result = "";
						
						
						if (ssl2.equals("Yes") || ssl3.equals("Yes") ||tls1.equals("Yes")  || tls11.equals("Yes"))
						{
							result = " is Not Secured";
							System.out.println(links+ result);
							System.out.println("Testing Weak CipherText");
							System.out.println(cipherGetText("//*[@id=\"main\"]/div[5]/div[4]/div[2]/table[2]"));
							data.add (new String[] { links, result ,tls13, tls12,tls11,tls1,ssl3,ssl2,cipherGetText("//*[@id=\"main\"]/div[5]/div[4]/div[2]/table[2]")});
						}
						else 
						{
							result = " is Secured ";
							System.out.println(links+ result);
							System.out.println("Testing Weak CipherText");
							System.out.println(cipherGetText("//*[@id=\"main\"]/div[5]/div[4]/div[2]/table[2]"));
							data.add (new String[] { links, result ,tls13, tls12,tls11,tls1,ssl3,ssl2,cipherGetText("//*[@id=\"main\"]/div[5]/div[4]/div[2]/table[2]") });
						}
					}
					else // statment if the there is only one certificate.
					{
				if (driver.findElements(By.xpath("//*[@id=\"main\"]/div[5]/div[3]/div[2]/table[1]/tbody/tr[1]/td[2]")).size() >0)
				{
						
				String ssl2 = driver.findElement(By.xpath("//*[@id=\"main\"]/div[5]/div[3]/div[2]/table[1]/tbody/tr[6]/td[2]")).getText();
				String ssl3 = driver.findElement(By.xpath("//*[@id=\"main\"]/div[5]/div[3]/div[2]/table[1]/tbody/tr[5]/td[2]")).getText();
				String tls1 = driver.findElement(By.xpath("//*[@id=\"main\"]/div[5]/div[3]/div[2]/table[1]/tbody/tr[4]/td[2]")).getText();
				String tls11 = driver.findElement(By.xpath("//*[@id=\"main\"]/div[5]/div[3]/div[2]/table[1]/tbody/tr[3]/td[2]")).getText();
				String tls12 = driver.findElement(By.xpath("//*[@id=\"main\"]/div[5]/div[3]/div[2]/table[1]/tbody/tr[2]/td[2]")).getText();
				String tls13 = driver.findElement(By.xpath("//*[@id=\"main\"]/div[5]/div[3]/div[2]/table[1]/tbody/tr[1]/td[2]")).getText();
				String result = "";
				
				
				if (ssl2.equals("Yes") || ssl3.equals("Yes") ||tls1.equals("Yes")  || tls11.equals("Yes"))
				{
					result = " is Not Secured";
					System.out.println(links+ result);
					System.out.println("Testing Weak CipherText");
					System.out.println(cipherGetText("//*[@id=\"main\"]/div[5]/div[3]/div[2]/table[2]"));
					data.add (new String[] { links, result ,tls13, tls12,tls11,tls1,ssl3,ssl2,cipherGetText("//*[@id=\"main\"]/div[5]/div[3]/div[2]/table[2]")});
				}
				else 
				{
					result = " is Secured ";
					System.out.println(links+ result);
					System.out.println("Testing Weak CipherText");
					System.out.println(cipherGetText("//*[@id=\"main\"]/div[5]/div[3]/div[2]/table[2]"));
					data.add (new String[] { links, result ,tls13, tls12,tls11,tls1,ssl3,ssl2,cipherGetText("//*[@id=\"main\"]/div[5]/div[3]/div[2]/table[2]")});
				}
				}
				else
				{
					
					String ssl2 = driver.findElement(By.xpath("//*[@id=\"main\"]/div[3]/div[3]/div[2]/table[1]/tbody/tr[6]/td[2]")).getText();// initializing all the variable in each protocols
					String ssl3 = driver.findElement(By.xpath("//*[@id=\"main\"]/div[3]/div[3]/div[2]/table[1]/tbody/tr[5]/td[2]")).getText();
					String tls1 = driver.findElement(By.xpath("//*[@id=\"main\"]/div[3]/div[3]/div[2]/table[1]/tbody/tr[4]/td[2]")).getText();
					String tls11 = driver.findElement(By.xpath("//*[@id=\"main\"]/div[3]/div[3]/div[2]/table[1]/tbody/tr[3]/td[2]")).getText();
					String tls12 = driver.findElement(By.xpath("//*[@id=\"main\"]/div[3]/div[3]/div[2]/table[1]/tbody/tr[2]/td[2]")).getText();
					String tls13 = driver.findElement(By.xpath("//*[@id=\"main\"]/div[3]/div[3]/div[2]/table[1]/tbody/tr[1]/td[2]")).getText();
					String result = "";
					
					
					if (ssl2.equals("Yes") || ssl3.equals("Yes") ||tls1.equals("Yes")  || tls11.equals("Yes")) // checking if the tls protocol is secured
					{
						result = " is Not Secured";
						System.out.println(links+ result);
						System.out.println("Testing Weak CipherText");
						System.out.println(cipherGetText("//*[@id=\"main\"]/div[5]/div[3]/div[2]/table[2]"));
						data.add (new String[] { links, result ,tls13, tls12,tls11,tls1,ssl3,ssl2,cipherGetText("//*[@id=\"main\"]/div[5]/div[3]/div[2]/table[2]")});
					}
					else 
					{
						result = " is Secured ";
						System.out.println(links+ result);
						System.out.println("Testing Weak CipherText");
						System.out.println(cipherGetText("//*[@id=\"main\"]/div[5]/div[3]/div[2]/table[2]"));
						data.add (new String[] { links, result ,tls13, tls12,tls11,tls1,ssl3,ssl2,cipherGetText("//*[@id=\"main\"]/div[5]/div[3]/div[2]/table[2]")});
					}
				}
				
				}	
			}
				
				System.out.println("Finish testing in this url");
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	
	private static void clicking() // method for clicking each span to reveal the protocol and cipher part of the website
	{
		if (driver.findElements(By.xpath("//*[@id=\"warningBox\"]/span/a")).size()> 0)
		{
		WebElement cont =	driver.findElement(By.xpath("//*[@id=\"warningBox\"]/span/a"));//if the ip link is not the same.
		cont.click();
		}
		if(driver.findElements(By.xpath("//*[@id=\"multiTable\"]/tbody/tr[5]/td[2]/span[1]/b/a")).size()>0)//if there is 2 links. click the second
		{
			WebElement click2ndLink = driver.findElement(By.xpath("//*[@id=\"multiTable\"]/tbody/tr[5]/td[2]/span[1]/b/a"));
			click2ndLink.click();
		}
		
		if(driver.findElements(By.xpath("//*[@id=\"showcipher2\"]")).size()>0)
		{
			WebElement clickSpan = driver.findElement(By.xpath("//*[@id=\"showcipher2\"]"));//clicking the span for cipher2.
			clickSpan.click();
		}
		if(driver.findElements(By.xpath("//*[@id=\"showcipher3\"]")).size()>0)
		{
			WebElement clickSpan = driver.findElement(By.xpath("//*[@id=\"showcipher3\"]"));//clicking the span for cipher3.
			clickSpan.click();
		}
		if(driver.findElements(By.xpath("//*[@id=\"showcipher4\"]")).size()>0)
		{
			WebElement clickSpan = driver.findElement(By.xpath("//*[@id=\"showcipher4\"]"));//clicking the span for cipher4.
			clickSpan.click();
		}
		if(driver.findElements(By.xpath("//*[@id=\"showcipher5\"]")).size()>0)
		{
			WebElement clickSpan = driver.findElement(By.xpath("//*[@id=\"showcipher5\"]"));//clicking the span for cipher5.
			clickSpan.click();
		}
	}
	
	
	private static void getUrls() // this is the method for getting all the Url Link and putting it into array.
	{
		try (BufferedReader br = new BufferedReader(new FileReader("urlLinks2.csv"))) { // put the .csv file with the url links.
		    String line;
		    while ((line = br.readLine()) != null) {
		        String[] values = line.split(COMMA_DELIMITER);
		        urlLinks.addAll(Arrays.asList(values));
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (String i : urlLinks) {
			System.out.println(i);
		}
		
	}
	
	
	private static String cipherGetText(String container) {// method to get the cipher text
		
		List<WebElement> ciphertextTable = driver.findElements(By.xpath(container));//go to the section of the feed.
		Iterator<WebElement> it = ciphertextTable.iterator();
		List<String> messageList = new ArrayList<>();
		String message = "";
		
		while(it.hasNext()) 
		{
			List<WebElement> tableLeft= it.next().findElements(By.className("tableLeft"));  // got to the class with table left at the beginning.
			int size = tableLeft.size();
			for (WebElement I: tableLeft)
			{
				List<WebElement> b = I.findElements(By.tagName("b"));
				for (WebElement a : b) {
					if(a.getText().equals("WEAK")|| a.getText().equals("INSECURE"))// the tag b have "WEAK" or "INSECURE".
					{
						messageList.add((I.getText()));
					}
				}
				
			}
		System.out.println(size+ ": amount of CipherText available");
		}
		
		
		String delim = "\n";
		StringBuilder sb = new StringBuilder();
		
		for (String i: messageList) {
			 sb.append(i.trim()).append(delim);
		}
		message = sb.toString();
		
		return message;
		
	
	}
	
	
	
}