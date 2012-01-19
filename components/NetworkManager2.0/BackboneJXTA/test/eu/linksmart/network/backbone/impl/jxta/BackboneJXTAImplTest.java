package eu.linksmart.network.backbone.impl.jxta;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Usually one would like to do unit testing as it is a best practice in agile
 * development.
 * 
 * Unfortunately JXTA is not uptodate in that respect. In normal circumstances
 * it is only possible to have one JXTA peer in one JVM. This makes unit testing
 * rather useless because within the same instance all should work flawlessly.
 * 
 * There are some examples how unit testing could be done here:
 * 
 * <ul>
 * <li>http://www.java.net/node/668495</li>
 * <li>http://www.java.net/node/689292</li>
 * </ul>
 * 
 * In the book "PracticalJXTA" an approach is also described. But all of this
 * would require major efforts in refactoring the JXTA backbone. This will not
 * be done in the very short term so UnitTests are currently out of scope for
 * this backbone.
 * 
 * 
 * @author Schneider
 * 
 */
public class BackboneJXTAImplTest {

	// @Test
	// public void testSendData() {
	// fail("Test not yet implemented");
	// }

	// @Test
	// public void testReceiveData() {
	// fail("Test not yet implemented");
	// }

	// @Test
	// public void testBroadcastData() {
	// fail("Test not yet implemented");
	// }

}
