/*
 * In case of German law being applicable to this license agreement, the following warranty and liability terms shall apply:
 *
 * 1. Licensor shall be liable for any damages caused by wilful intent or malicious concealment of defects.
 * 2. Licensor's liability for gross negligence is limited to foreseeable, contractually typical damages.
 * 3. Licensor shall not be liable for damages caused by slight negligence, except in cases 
 *    of violation of essential contractual obligations (cardinal obligations). Licensee's claims for 
 *    such damages shall be statute barred within 12 months subsequent to the delivery of the software.
 * 4. As the Software is licensed on a royalty free basis, any liability of the Licensor for indirect damages 
 *    and consequential damages - except in cases of intent - is excluded.
 *
 * This limitation of liability shall also apply if this license agreement shall be subject to law 
 * stipulating liability clauses corresponding to German law.
 */
package eu.linksmart.qos.client.request;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import eu.linksmart.qos.client.request.TraverseXMLRequest;

public class JUnitTraverseQoSXMLRequest {

	final String xmlQoSRequest =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
					+ "<request xmlns=\"http://qos.linksmart.eu\">"
					+ "<serviceQualities>"
					+ "<quality>service:hasCapability;service:playsVideo</quality>"
					+ "</serviceQualities>"
					+ "<requirements>"
					+ "<requirement>"
					+ "<property>device:hasHardware/hardware:hasDisplay/hardware:screenWidth</property>"
					+ "<standard>more</standard>"
					+ "</requirement>"
					+ "<requirement>"
					+ "<property>device:hasHardware/hardware:hasDisplay/hardware:screenHeight</property>"
					+ "<standard>more</standard>"
					+ "</requirement>"
					+ "<requirement>"
					+ "<property>device:hasEnergyProfile/energy:consumption/energy:modeAverage</property>"
					+ "<standard>least</standard>"
					+ "</requirement>"
					+ "<requirement>"
					+ "<property>service:serviceCost</property>"
					+ "<standard>less</standard>"
					+ "</requirement>"
					+ "<requirement>"
					+ "<property>service:hasInput/service:parameterUnit</property>"
					+ "<standard>notNumeric</standard>"
					+ "<value>unit:VideoAvi</value>" + "</requirement>"
					+ "</requirements>" + "</request>";

	@Test
	public void test() {

		TraverseXMLRequest traverse = new TraverseXMLRequest();

		traverse.loadRequest(xmlQoSRequest);

		assertNotNull(traverse.getServiceQualities());

		assertNotNull(traverse.getRequirements());
	}

}
