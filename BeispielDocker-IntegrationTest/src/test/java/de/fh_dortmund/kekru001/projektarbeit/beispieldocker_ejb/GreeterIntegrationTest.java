package de.fh_dortmund.kekru001.projektarbeit.beispieldocker_ejb;

import static org.junit.Assert.assertEquals;

import java.net.URL;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class GreeterIntegrationTest {

//	static{
//		System.setProperty("wildfly.http.host", "192.168.0.15");
//		System.setProperty("wildfly.http.port", "8080");
//		System.setProperty("wildfly.management.host", "192.168.0.15");
//		System.setProperty("wildfly.management.port", "9990");
//	}
	
	@Inject
	Greeter greeter;

	@ArquillianResource  
	URL deploymentUrl; 

	@Deployment
	public static JavaArchive archive(){
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class)
				.addClass(Greeter.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
		//		System.out.println(archive.toString(true));
		return archive;
	}

	@Test
	public void testGreeting() {
		System.out.println(deploymentUrl);
		assertEquals("Hello, Earthling!", greeter.createGreeting("Earthling"));
		greeter.greet(System.out, "earthling");
	}

}
