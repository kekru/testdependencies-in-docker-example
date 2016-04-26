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

@RunWith(Arquillian.class)
public class MongoBeanIntegrationTest {
	// docker run --rm -it -p 8080:8080 -p 9990:9990 -p 999:999 b847e77ea144

	//	static{
	//		System.setProperty("wildfly.management.host", "192.168.0.15");
	//		System.setProperty("wildfly.management.port", "9990");
	//		System.setProperty("wildfly.http.host", "192.168.0.15");
	//		System.setProperty("wildfly.http.port", "8080");
	//	}

	@EJB
	MongoLocal mongoBean;

	MongoClient mongoClient;
	MongoDatabase db;


	@Before
	public void init() throws IOException{
		//		System.out.println(MongoBeanIntegrationTest.class.getResourceAsStream("classes/arquillianproperties.properties"));
		//		System.out.println(MongoBeanIntegrationTest.class.getResourceAsStream("/WEB-INF/classes/arquillianproperties.properties"));
		//		System.out.println(MongoBeanIntegrationTest.class.getResourceAsStream("arquillianproperties.properties"));
		//		System.out.println(MongoBeanIntegrationTest.class.getResourceAsStream("/arquillianproperties.properties"));
		//		System.out.println(MongoBeanIntegrationTest.class.getResourceAsStream("WEB-INF/classes/arquillianproperties.properties"));

		Properties p = new Properties();
		p.load(MongoBeanIntegrationTest.class.getResourceAsStream("/arquillianproperties.properties"));
		for(val pr : p.entrySet()){
			System.setProperty((String) pr.getKey(), (String) pr.getValue());
		}

		System.out.println("Property " + p.getProperty("ARQT_HalloWelt") + ", "+p.getProperty("HalloWelt"));
		//		String host = "5.45.108.198";// "192.168.0.15";
		//		int port = 27017;
		String host = System.getProperty("mongohost");
		int port = Integer.parseInt(System.getProperty("mongoport"));
		mongoClient = new MongoClient(host, port);

		db = mongoClient.getDatabase("myDB");
		assertNotNull(db);

		assertNotNull(mongoClient);
		assertNotNull(db);
		db.getCollection(MongoBean.COLLECTION_ANZEIGE).drop();
		db.getCollection(MongoBean.COLLECTION_BENUTZER).drop();
		db.getCollection(MongoBean.COLLECTION_ENTITYIDS).drop();

	}

	@After
	public void cleanUp(){
		if(mongoClient != null){//TODO Warum ist der beim mvn clean install null?
			mongoClient.close();
		}
	}


	@Deployment
	public static WebArchive archive() throws FileNotFoundException, IOException{


		//		System.setProperty("ARQT_mongohost", System.getProperty("mongohost"));
		//		System.setProperty("ARQT_mongoport", System.getProperty("mongoport"));
		//		System.setProperty("ARQT_HalloWelt", "Hallo Welt");
		Properties p = new Properties();

		p.setProperty("mongohost", System.getProperty("mongohost"));
		p.setProperty("mongoport", System.getProperty("mongoport"));

		File f = new File("arquillianproperties.properties");
		p.store(new FileOutputStream(f), "ArquillianProperties");

		File[] files = Maven.resolver().loadPomFromFile("../BeispielDocker-common/pom.xml").importRuntimeDependencies().resolve().withTransitivity().asFile();
		File[] files2 = Maven.resolver().loadPomFromFile("../BeispielDocker-ejb/pom.xml").importRuntimeDependencies().resolve().withTransitivity().asFile();

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
//		System.out.println(archive.toString(true));
		return archive;
	}

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
