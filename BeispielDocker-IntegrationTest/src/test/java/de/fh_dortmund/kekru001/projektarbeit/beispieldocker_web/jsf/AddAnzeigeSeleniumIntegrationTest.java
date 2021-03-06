package de.fh_dortmund.kekru001.projektarbeit.beispieldocker_web.jsf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.List;

import lombok.val;
import lombok.extern.log4j.Log4j2;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import de.fh_dortmund.kekru001.projektarbeit.beispieldocker_ejb.beans.MongoBean;

/**
 * Seleniumtest zum Testen der Weboberflaeche
 */
@Log4j2
public class AddAnzeigeSeleniumIntegrationTest {

	/**
	 * Zu Beginn des Tests wird die Datenbank geleert
	 */
	@Before
	public void cleanDB(){
		String host = System.getProperty("mongohost");
		int port = Integer.parseInt(System.getProperty("mongoport"));
		MongoClient mongoClient = new MongoClient(host, port);

		MongoDatabase db = mongoClient.getDatabase("myDB");
		assertNotNull(db);

		assertNotNull(mongoClient);
		assertNotNull(db);
		db.getCollection(MongoBean.COLLECTION_ANZEIGE).drop();
		db.getCollection(MongoBean.COLLECTION_BENUTZER).drop();
		db.getCollection(MongoBean.COLLECTION_ENTITYIDS).drop();
	}
	
	@Test
	public void testAnzeigenAnlegen() throws Exception {

		//Auslesen der Adressen und Ports für Seleniumserver und Webseite
		String host = System.getProperty("seleniumhost");
		String port = System.getProperty("seleniumport");
		String webserverHost = System.getProperty("wildfly.http.host");
		String webserverPort = System.getProperty("wildfly.http.port");

		if(host == null || host.trim().isEmpty() || port == null || port.trim().isEmpty()){
			fail("Property not set: seleniumhost: " + host + ", seleniumport: " + port);
		}

		//Verbindung zum Seleniumserver aufbauen
		final String hub = "http://"+host+":"+port+"/wd/hub";
		log.info("Selenium connect to: " + hub);
		WebDriver driver = new RemoteWebDriver(
				new URL(hub),  
				DesiredCapabilities.firefox());

		final String url = "http://" + webserverHost + ":" + webserverPort + "/BeispielDocker-web/index.jsf";
		log.info("GET " + url);
		//Webseite ueber Selenium im Firefox aufrufen
		driver.get(url);

		val headline = driver.findElement(By.id("headline"));
		assertEquals("\u00dcberschrift 'Kleinanzeigen' nicht gefunden ", "Kleinanzeigen", headline.getText());

		List<WebElement> anzeigen = driver.findElements(By.id("anzeige0"));
		assertTrue("anzeige0 bereits vorhanden", anzeigen.isEmpty());

		driver.findElement(By.className("titelinput")).sendKeys("Sony Bravia 50 Zoll");
		driver.findElement(By.className("beschreibunginput")).sendKeys("Sony TV, sehr gut erhalten");
		driver.findElement(By.className("preisinput")).sendKeys("20000");//Preis in Cent
		driver.findElement(By.className("nameinput")).sendKeys("Max Mustermann");
		driver.findElement(By.className("emailinput")).sendKeys("max@mustermann.net");
		driver.findElement(By.className("passwortinput")).sendKeys("geheim");
		driver.findElement(By.className("sendbutton")).click();

		WebDriverWait wait = new WebDriverWait(driver, 15);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("anzeige0")));

		log.info("GET " + url);
		driver.get(url);

		anzeigen = driver.findElements(By.id("anzeige0"));
		assertEquals("anzeige0 wurde nicht gespeichert oder zu viele", 1, anzeigen.size());

		WebElement anzeige = anzeigen.get(0);
		assertEquals("Sony Bravia 50 Zoll", anzeige.findElement(By.className("titelanzeige")).getText());
		assertEquals("Sony TV, sehr gut erhalten", anzeige.findElement(By.className("textanzeige")).getText());
		assertEquals("Preis: 200.0\u20AC", anzeige.findElement(By.className("preisanzeige")).getText());
		assertEquals("Verk\u00e4ufer: Max Mustermann", anzeige.findElement(By.className("verkaeuferanzeige")).getText());

	}
}