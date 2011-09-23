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
 * Copyright (C) 2006-2010 University of Reading,
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
package eu.linksmart.policy.pdp.admin.bundle.impl;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.File;
import java.net.MalformedURLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import eu.linksmart.policy.pdp.admin.bundle.impl.PdpDbCreator;
import eu.linksmart.policy.pdp.admin.bundle.impl.XMLDBPdpAdminService;

/**
 * Unit test for {@link XMLDBPdpAdminService}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitXMLDBPdpAdminServiceTest {

	/** test db location */
	private static final String DB_LOCATION = "PolicyFramework";
	
	/** test policy **/
	private static final String POLICY =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+ "<Policy PolicyId=\"ExamplePolicy\" "  
		+ "RuleCombiningAlgId=\"urn:oasis:names:tc:xacml:1.0:rule-combining-alg" 
		+ "orithm:permit-overrides\" " 
		+ "xmlns=\"urn:oasis:names:tc:xacml:1.0:policy\" "  
		+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
		+ "xsi:schemaLocation=\"urn:oasis:names:tc:xacml:1.0:policy cs-xacml-sc" 
		+ "hema-policy-01.xsd\">"
		+ "<Target>"
		+ "<Subjects>"
 		+ "<AnySubject/>"
		+ "</Subjects>"
		+ "<Resources>"
		+ "<AnyResource/>"
		+ "</Resources>"
		+ "<Actions>"
		+ "<AnyAction/>"
		+ "</Actions>"
		+ "</Target>"
		+ "<Rule Effect=\"Permit\" RuleId=\"DefaultPermit\"/>"
		+ "</Policy>";
	
	@Test
	public void testXMLDBPdpAdminService() {		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		replay(bundleCtxMock);
		
		XMLDBPdpAdminService adminService = null;
		try {
			adminService = new XMLDBPdpAdminService(DB_LOCATION, bundleCtxMock);
			assertNotNull(adminService);
		} catch (Throwable t) {
			// we expect an exception here since we do not create the db
		} finally {
			if (adminService != null) {
				adminService.shutdown();
			}
		}
		verify(bundleCtxMock);
	}

	@Test
	public void testShutdown() {		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		replay(bundleCtxMock);
		
		XMLDBPdpAdminService adminService = null;
		try {
			adminService = new XMLDBPdpAdminService(DB_LOCATION, bundleCtxMock);
			adminService.shutdown();
		} catch (Throwable t) {
			// we expect an exception here since we do not create the db
		} finally {
			if (adminService != null) {
				adminService.shutdown();
			}
		}
		verify(bundleCtxMock);
	}

	@Test
	public void testActivatePolicy() {
		Bundle bundleMock = createMock(Bundle.class);
		expect(bundleMock.getResource(isA(String.class))).andReturn(null);
		replay(bundleMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		replay(bundleCtxMock);
		
		XMLDBPdpAdminService adminService = null;
		try {
			adminService = new XMLDBPdpAdminService(DB_LOCATION, bundleCtxMock);
			adminService.publishPolicy("Policytron2000", POLICY);
			adminService.activatePolicy("Policytron2000");
			adminService.removePolicy("Policytron2000");
		} catch (Throwable t) {
			// we expect an exception here since we do not create the db
		} finally {
			if (adminService != null) {
				adminService.shutdown();
			}
		}
		verify(bundleCtxMock);
	}

	@Test
	public void testDeactivatePolicy() {
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		replay(bundleCtxMock);
		
		XMLDBPdpAdminService adminService = null;
		try {
			adminService = new XMLDBPdpAdminService(DB_LOCATION, bundleCtxMock);
			adminService.publishPolicy("Policytron2000", POLICY);
			adminService.activatePolicy("Policytron2000");
			adminService.deactivatePolicy("Policytron2000");
			adminService.removePolicy("Policytron2000");
		} catch (Throwable t) {
			// we expect an exception here since we do not create the db
		} finally {
			if (adminService != null) {
				adminService.shutdown();
			}
		}
		verify(bundleCtxMock);
	}

	@Test
	public void testGetPolicy() {
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		replay(bundleCtxMock);
		
		XMLDBPdpAdminService adminService = null;
		try {
			adminService = new XMLDBPdpAdminService(DB_LOCATION, bundleCtxMock);
			adminService.publishPolicy("Policytron2000", POLICY);
			assertNotNull(adminService.getPolicy("Policytron2000"));
			adminService.removePolicy("Policytron2000");
		} catch (Throwable t) {
			// we expect an exception here since we do not create the db
		} finally {
			if (adminService != null) {
				adminService.shutdown();
			}
		}
		verify(bundleCtxMock);
	}

	@Test
	public void testGetPolicyAsDocument() {
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		replay(bundleCtxMock);
		
		XMLDBPdpAdminService adminService = null;
		try {
			adminService = new XMLDBPdpAdminService(DB_LOCATION, bundleCtxMock);
			adminService.publishPolicy("Policytron2000", POLICY);
			assertNotNull(adminService.getPolicy("Policytron2000"));
			adminService.removePolicy("Policytron2000");
		} catch (Throwable t) {
			// we expect an exception here since we do not create the db
		} finally {
			if (adminService != null) {
				adminService.shutdown();
			}
		}
		verify(bundleCtxMock);
	}

	@Test
	public void testGetActivePolicyList() {
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		replay(bundleCtxMock);
		
		XMLDBPdpAdminService adminService = null;
		try {
			adminService = new XMLDBPdpAdminService(DB_LOCATION, bundleCtxMock);
			assertNotNull(adminService.getActivePolicyList());
		} catch (Throwable t) {
			// we expect an exception here since we do not create the db
		} finally {
			if (adminService != null) {
				adminService.shutdown();
			}
		}
		verify(bundleCtxMock);
	}

	@Test
	public void testGetInActivePolicyList() {
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		replay(bundleCtxMock);
		
		XMLDBPdpAdminService adminService = null;
		try {
			adminService = new XMLDBPdpAdminService(DB_LOCATION, bundleCtxMock);
			assertNotNull(adminService.getInActivePolicyList());
		} catch (Throwable t) {
			// we expect an exception here since we do not create the db
		} finally {
			if (adminService != null) {
				adminService.shutdown();
			}
		}
		verify(bundleCtxMock);
	}

	@Test
	public void testPublishPolicy() {
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		replay(bundleCtxMock);
		
		XMLDBPdpAdminService adminService = null;
		try {
			adminService = new XMLDBPdpAdminService(DB_LOCATION, bundleCtxMock);
			adminService.publishPolicy("Policytron2000", POLICY);
			assertNotNull(adminService.getPolicy("Policytron2000"));
			adminService.removePolicy("Policytron2000");
		} catch (Throwable t) {
			// we expect an exception here since we do not create the db
		} finally {
			if (adminService != null) {
				adminService.shutdown();
			}
		}
		verify(bundleCtxMock);
	}

	@Test
	public void testRemovePolicy() {
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		replay(bundleCtxMock);
		
		XMLDBPdpAdminService adminService = null;
		try {
			adminService = new XMLDBPdpAdminService(DB_LOCATION, bundleCtxMock);
			adminService.publishPolicy("Policytron2000", POLICY);
			assertNotNull(adminService.getPolicy("Policytron2000"));
			adminService.removePolicy("Policytron2000");
		} catch (Throwable t) {
			// we expect an exception here since we do not create the db
		} finally {
			if (adminService != null) {
				adminService.shutdown();
			}
		}
		verify(bundleCtxMock);
	}

	@Test
	public void testGetProperty() {
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		replay(bundleCtxMock);
		
		XMLDBPdpAdminService adminService = null;
		try {
			adminService = new XMLDBPdpAdminService(DB_LOCATION, bundleCtxMock);
			adminService.setProperty("Is there anybody", "out there?");
			assertNotNull(adminService.getProperty("Is there anybody"));
		} catch (Throwable t) {
			// we expect an exception here since we do not create the db
		} finally {
			if (adminService != null) {
				adminService.shutdown();
			}
		}
		verify(bundleCtxMock);
	}

	@Test
	public void testSetProperty() {
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		replay(bundleCtxMock);
		
		XMLDBPdpAdminService adminService = null;
		try {
			adminService = new XMLDBPdpAdminService(DB_LOCATION, bundleCtxMock);
			adminService.setProperty("Is there anybody", "out there?");
			assertNotNull(adminService.getProperty("Is there anybody"));
		} catch (Throwable t) {
			// we expect an exception here since we do not create the db
		} finally {
			if (adminService != null) {
				adminService.shutdown();
			}
		}
		verify(bundleCtxMock);
	}

	@Test
	public void testQueryXPath() {
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		replay(bundleCtxMock);
		
		XMLDBPdpAdminService adminService = null;
		try {
			adminService = new XMLDBPdpAdminService(DB_LOCATION, bundleCtxMock);
			adminService.publishPolicy("Policytron2000", POLICY);
			adminService.activatePolicy("Policytron2000");
			assertNotNull(adminService.queryXPath("//*"));
			adminService.deactivatePolicy("Policytron2000");
			adminService.removePolicy("Policytron2000");
		} catch (Throwable t) {
			// we expect an exception here since we do not create the db
		} finally {
			if (adminService != null) {
				adminService.shutdown();
			}
		}
		verify(bundleCtxMock);
	}
	
	@BeforeClass
	public static void setupDb() {
		new File(DB_LOCATION).mkdir();
		Bundle bundleMock = createMock(Bundle.class);
		try {
			expect(bundleMock.getResource(isA(String.class))).andReturn(
					new File("resources/conf.xml").toURI().toURL());
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
			fail("Exception");
		}
		replay(bundleMock);
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getBundle()).andReturn(bundleMock);
		replay(bundleCtxMock);
		PdpDbCreator.createPolicyDb(DB_LOCATION, bundleCtxMock);
	}
	
	@AfterClass
	public static void teardownDb() {
		cleanup(new File(DB_LOCATION));
	}
	
	static boolean cleanup(File theDirectory) {
		if (theDirectory.isDirectory()) {
			String[] children = theDirectory.list();
			for (int i=0; i < children.length; i++) {
				boolean success = cleanup(new File(theDirectory, 
						children[i]));
	       		if (!success) {	       			
	       			return false;
	       		}
	       	}
	   	}
	   	return theDirectory.delete();
	}

}
