package de.fh_dortmund.kekru001.projektarbeit.beispieldocker_ejb;

import static org.junit.Assert.assertEquals;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Der Test wird mit dem Arquillian Testrunner gestartet und spaeter auf dem WildFly ausgefuehrt
 */
@RunWith(Arquillian.class)
public class GreeterIntegrationTest {
	
	@Inject
	private Greeter greeter;	

	/** 
	  * Definition der Klassen, die zu einem Archiv zusammengefasst werden.
	  * Das Archiv, zusammen mit dieser Testklasse, wird von Arquillian auf den WildFlyserver kopiert, dort deployed und diese Testklasse wird gestartet
	  */
	@Deployment
	public static JavaArchive archive(){
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class)
				.addClass(Greeter.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");		
		return archive;
	}

	/**
	 * Dieser Test wird auf dem WildFly Server gestartet
	 */
	@Test
	public void testGreeting() {		
		assertEquals("Hallo, Kevin", greeter.createGreeting("Kevin"));		
	}
	
}
