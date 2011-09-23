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
/**
 * Copyright (C) 2006-2010
 *                         the HYDRA consortium, EU project IST-2005-034891
 *
 * This file is part of LinkSmart.
 *
 * LinkSmart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
 * version 3 as published by the Free Software Foundation.
 *
 * LinkSmart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with LinkSmart.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.linksmart..limbo;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.*;
import org.junit.runner.JUnitCore;
import org.xml.sax.SAXException;

import com.martiansoftware.jsap.JSAPResult;

import static org.junit.Assert.*;

public class TestLimbo {

/*	@Test public void smokeTestLimbo() throws Exception {
	MyLimboFactory.create(new String[] {"test/eu/linksmart/limbo/SMS.wsdl"});
	assertTrue(new File("generated/eu/linksmart/limbo/SMSOpsImpl.java").exists());
}

	@Test public void testLimboPublishWSDL() throws Exception {
	MyLimboFactory.create(new String[] {"-s", "jse", "-o", "osgi", "test/eu/linksmart/limbo/esnLock-inet.wsdl"});
	assertTrue(new File("generated/eu/linksmart/limbo/esnLock_inetServlet.java").exists());
}

	@Test public void testUseOfOntology() throws Exception {
	MyLimboFactory.create(new String[] {"test/eu/linksmart/limbo/th03-ontology.wsdl"});
	assertTrue(new File("generated/eu/linksmart/limbo/th03_ontologyServlet.java").exists());
}

	@Test public void testEventManagerClientGeneration() throws Exception {
	MyLimboFactory.create(new String[] {"-s", "jse", "-t", "client", "test/eu/linksmart/limbo/EventManagerService.wsdl"});
	assertTrue(new File("generated/eu/linksmart/limbo/client/EventManagerServiceLimboClientPortImpl.java").exists());
}
		@Test public void testEventSubscriberClientGeneration() throws Exception {
		MyLimboFactory.create(new String[] {"-s", "jse", "-t", "client", "test/eu/linksmart/limbo/EventSubscriberService.wsdl"});
		assertTrue(new File("generated/eu/linksmart/limbo/client/EventSubscriberServiceLimboClientPortImpl.java").exists());
}
		@Test public void testEventSubscriberServerGeneration() throws Exception {
		MyLimboFactory.create(new String[] {"-s", "jse", "-t", "server", "test/eu/linksmart/limbo/EventSubscriberService.wsdl"});
		assertTrue(new File("generated/eu/linksmart/limbo/EventSubscriberServiceOpsImpl.java").exists());
}
	@Test public void testGPSClientGeneration() throws Exception {
		MyLimboFactory.create(new String[] {"-s", "jse", "-t", "server", "test/eu/linksmart/limbo/SMS.wsdl"});
		assertTrue(new File("generated/eu/linksmart/limbo/client/GPSLimboClientPort.java").exists());
}
	
	@Test public void testOSGILockGenerationGeneration() throws Exception {
		MyLimboFactory.create(new String[] {"-s", "jse", "-t", "server", "-o", "osgi", "test/eu/linksmart/limbo/GPS.wsdl"});
		assertTrue(new File("generated/eu/linksmart/limbo/GPSServlet.java").exists());
}
	@Test public void testOSGIAbloyGenerationGeneration() throws Exception {
		MyLimboFactory.create(new String[] {"-s", "jse", "-t", "service", "-o", "osgi", "test/eu/linksmart/limbo/abloy_el582.wsdl"});
		assertTrue(new File("generated/eu/linksmart/limbo/GPSServlet.java").exists());
}*/
	@Test public void testEventManagerGeneration() throws Exception {
		//MyLimboFactory.create(new String[] {"-s", "jse", "-t", "client", "-o", "standalone", "test/eu/linksmart/limbo/EventManagerService.wsdl"});
		//assertTrue(new File("generated/eu/linksmart/limbo/EventManagerServiceService.java").exists());
}
/*	@Test public void testThermometerOSGiGeneration() throws Exception {
		MyLimboFactory.create(new String[] {"-s", "jse", "-t", "client", "-o", "osgi", "test/eu/linksmart/limbo/th03-ontology.wsdl"});
		//assertTrue(new File("generated/eu/linksmart/limbo/EventManagerServiceService.java").exists());
}*/
	/*@Test public void testSecurityManagerApplicationGeneration() throws Exception {
		MyLimboFactory.create(new String[] {"-s", "jse", "-t", "server", "-o", "osgi", "test/eu/linksmart/limbo/magna_32.wsdl"});
		assertTrue(new File("generated/eu/linksmart/limbo/EventManagerServiceService.java").exists());
}*/

	
	public static void main(String ... args) {
		JUnitCore.main(TestLimbo.class.getName());
		
	}
}
/*class MyLimboFactory extends Limbo {
	public MyLimboFactory(JSAPResult config)  throws SAXException, ParserConfigurationException, IOException {
		super(config);
	}
	public static Limbo create(String[] argv) throws Exception {
		return new Limbo(getJSAPConfiguration("test", "test expl").parse(argv));
	}
}*/
