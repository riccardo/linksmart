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
package eu.linksmart.qos.ontology.response;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import eu.linksmart.qos.ontology.response.Device;
import eu.linksmart.qos.ontology.response.TraverseOntologyXMLResponse;

public class JUnitTraverseOntologyXMLResponse {

	final String xmlOntologyResponse =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
					+ "<response><device pid=\"mySamsungTV\" uri=\"http://localhost/ontologies/Device.owl#Television_6d7c3eff_ff99_4e15_b180_ed827e6bd853/RUNTIME_4e048ac7_b255_48a0_89fe_58e7e8ab269f\">"
					+ "<deviceProperties><property name=\"hardware:screenWidth\"><value unit=\"unit:Pixel\">1440</value></property>"
					+ "<property name=\"hardware:screenHeight\"><value>900</value></property><property name=\"energy:modeAverage\">"
					+ "<value>94.4</value></property></deviceProperties><service operation=\"PlayVideo\"><serviceProperties>"
					+ "<property name=\"service:serviceCost\"><value unit=\"unit:EUR\">15</value></property><property name=\"service:parameterUnit\">"
					+ "<value>unit:VideoAvi</value></property></serviceProperties></service></device>"
					+ "<device pid=\"MyStarProjector\" uri=\"http://localhost/ontologies/Device.owl#DataProjector_fa7b41f7_a568_4fd3_9e95_f87319c73ecc/RUNTIME_448b4f62_fa43_46af_8f49_bd5db5c4acc7\">"
					+ "<deviceProperties><property name=\"hardware:screenWidth\"><value unit=\"unit:Pixel\">900</value></property><property name=\"hardware:screenHeight\">"
					+ "<value unit=\"unit:Pixel\">865</value></property><property name=\"energy:modeAverage\"><value>240</value></property></deviceProperties>"
					+ "<service operation=\"PlayVideo\"><serviceProperties><property name=\"service:serviceCost\"><value unit=\"unit:EUR\">5</value></property><property name=\"service:parameterUnit\">"
					+ "<value>unit:VideoMpeg</value></property></serviceProperties></service></device>"
					+ "<device pid=\"MyPioneerDisplay\" uri=\"http://localhost/ontologies/Device.owl#DataProjector_008ada92_b9d3_4327_9fc3_e591d1cf1001/RUNTIME_f15a17d0_a07b_44b3_b4b1_47d54ff626d4\">"
					+ "<deviceProperties><property name=\"hardware:screenWidth\"><value unit=\"unit:Pixel\">1280</value></property><property name=\"hardware:screenHeight\"><value unit=\"unit:Pixel\">768</value>"
					+ "</property><property name=\"energy:modeAverage\"><value>123.7</value></property></deviceProperties><service operation=\"PlayVideo\"><serviceProperties><property name=\"service:serviceCost\">"
					+ "<value unit=\"unit:USD\">10</value></property><property name=\"service:parameterUnit\"><value>unit:VideoAvi</value></property></serviceProperties></service></device></response>";

	@Test
	public void testTraverse() {

		TraverseOntologyXMLResponse traverseOntologyXMLResponse =
				new TraverseOntologyXMLResponse();

		traverseOntologyXMLResponse.loadRequest(xmlOntologyResponse);

		List<Device> queryResult = traverseOntologyXMLResponse.getQueryResult();

		assertNotNull(queryResult);
	}

}
