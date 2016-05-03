package de.fh_dortmund.kekru001.projektarbeit.beispieldocker_ejb.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;

import lombok.val;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import de.fh_dortmund.kekru001.projektarbeit.beispieldocker_common.beans.MongoLocal;
import de.fh_dortmund.kekru001.projektarbeit.beispieldocker_common.entities.Anzeige;
import de.fh_dortmund.kekru001.projektarbeit.beispieldocker_common.entities.Benutzer;

/**
 * Der Test wird mit dem Arquillian Testrunner gestartet und spaeter auf dem WildFly ausgefuehrt
 */
@RunWith(Arquillian.class)
public class MongoBeanIntegrationTest {	

	/**
	 * Die MongoBean soll getestet werden
	 */
	@EJB
	private MongoLocal mongoBean;

	private MongoClient mongoClient;
	private MongoDatabase db;
	
	/**
	 * Definition der Klassen und Dateien, die zu einem Archiv zusammengefasst werden.
	 * Das Archiv, zusammen mit dieser Testklasse, wird von Arquillian auf den WildFlyserver kopiert, dort deployed und diese Testklasse wird gestartet
	 */
	@Deployment
	public static WebArchive archive() throws FileNotFoundException, IOException{

		//Speicherung der Java Systemvariablen mongohost und mongoport in einer Propertiesdatei, die spaeter dem Archiv hinzugefuegt wird.
		//Die Variablen werden im Maven Build vor den Integrationstests gesetzt.
		//Sie werden auf dem Wildfly benötigt, um sich mit der MongoDB zu verbinden
		Properties p = new Properties();
		p.setProperty("mongohost", System.getProperty("mongohost"));
		p.setProperty("mongoport", System.getProperty("mongoport"));
		File f = new File("arquillianproperties.properties");
		p.store(new FileOutputStream(f), "ArquillianProperties");

		//Alle in den pom.xml definierten Abhaengigkeiten soll auch in das Archiv
		File[] files = Maven.resolver().loadPomFromFile("../BeispielDocker-common/pom.xml").importRuntimeDependencies().resolve().withTransitivity().asFile();
		File[] files2 = Maven.resolver().loadPomFromFile("../BeispielDocker-ejb/pom.xml").importRuntimeDependencies().resolve().withTransitivity().asFile();

		//Erzeugung der Archivs
		WebArchive archive = ShrinkWrap.create(WebArchive.class)
				.addClass(Benutzer.class)
				.addClass(Anzeige.class)
				.addClass(MongoLocal.class)
				.addClass(MongoBean.class)
				.addClass(MongoBeanIntegrationTest.class)
				.addAsLibraries(files)
				.addAsLibraries(files2)
				.addAsResource(f)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

		return archive;
	}


	/**
	 * Vor dem Test wird die Mongo DB geleert
	 */
	@Before
	public void init() throws IOException{		

		//Auslesen der in arquillianproperties.properties gespeicherten Properties (mongohost und mongoport) und Speicherung als Java Systemproperty
		Properties p = new Properties();
		p.load(MongoBeanIntegrationTest.class.getResourceAsStream("/arquillianproperties.properties"));
		for(val pr : p.entrySet()){
			System.setProperty((String) pr.getKey(), (String) pr.getValue());
		}
		
		//Auslesen von Host und Port der Mongo DB. Diese werden ebenfalls innerhalb der MongoBean ausgelesen
		String host = System.getProperty("mongohost");
		int port = Integer.parseInt(System.getProperty("mongoport"));
		mongoClient = new MongoClient(host, port);

		db = mongoClient.getDatabase("myDB");
		assertNotNull(db);

		assertNotNull(mongoClient);
		assertNotNull(db);
		
		//leeren der Datenbank
		db.getCollection(MongoBean.COLLECTION_ANZEIGE).drop();
		db.getCollection(MongoBean.COLLECTION_BENUTZER).drop();
		db.getCollection(MongoBean.COLLECTION_ENTITYIDS).drop();

	}

	@After
	public void cleanUp(){
		if(mongoClient != null){
			mongoClient.close();
		}
	}
	

	/**
	 * Test der Speicherfunktion und das anschliessende Abrufen der gespeicherten Datensaetze
	 */
	@Test
	public void testSaveAndFind() {
		assertNotNull(mongoBean);	

		Benutzer b1 = new Benutzer(0, "abc", "test@test.de", "geheim");
		Benutzer b2 = new Benutzer(0, "xyz", "test@qwer.de", "geheim123");
		Anzeige a1 = new Anzeige(0, "Sony Fernseher", "Der neue Sony XQ310", true, 20000, b1);
		Anzeige a2 = new Anzeige(0, "Kinderbuchsammlung", "Gebraucht aber in top Zustand", false, 20000, b1);

		a1 = mongoBean.save(a1);
		a2 = mongoBean.save(a2);
		b2 = mongoBean.save(b2);

		assertEquals(2, mongoBean.findAllAnzeige().size());
		assertEquals(2, mongoBean.findAllBenutzer().size());

		a1.setTitel("Super Fernseher");
		mongoBean.save(a1);

		assertEquals(2, mongoBean.findAllAnzeige().size());
		assertEquals(2, mongoBean.findAllBenutzer().size());

		a2 = mongoBean.findByIdAnzeige(a2.getId());

		assertEquals("Gebraucht aber in top Zustand", a2.getText());
		assertEquals("test@qwer.de", mongoBean.findByIdBenutzer(b2.getId()).getEmail());
		assertEquals("abc", mongoBean.findByIdBenutzer(a2.getErsteller().getId()).getName());		

	}

	/**
	 * Testet den Abruf aller Anzeigen zu einem Benutzer
	 */
	@Test
	public void testFindAllAnzeige(){
		assertNotNull(mongoBean);

		Benutzer b1 = new Benutzer(0, "abc", "test@test.de", "geheim");
		Anzeige a1 = new Anzeige(0, "Sony Fernseher", "Der neue Sony XQ310", true, 20000, b1);
		Anzeige a2 = new Anzeige(0, "Kinderbuchsammlung", "Gebraucht aber in top Zustand", false, 20000, b1);

		a1 = mongoBean.save(a1);
		a2 = mongoBean.save(a2);

		List<Anzeige> list = mongoBean.findAllAnzeige();
		assertEquals(2, list.size());

		assertNotSame(list.get(0).getId(), 0);
		assertNotSame(list.get(1).getId(), 0);

		if(list.get(0).getId() == a1.getId()){
			a1 = list.get(0);
			a2 = list.get(1);
		}else{
			a1 = list.get(1);
			a2 = list.get(0);
		}

		assertEquals("Der neue Sony XQ310", a1.getText());
		assertEquals("Gebraucht aber in top Zustand", a2.getText());
	}

}
