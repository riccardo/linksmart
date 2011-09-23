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
package eu.linksmart.policy.pdp.admin.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.rmi.RemoteException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.linksmart.policy.pdp.admin.impl.FileSystemPdpAdminService;

/**
 * Unit test for {@link FileSystemPdpAdminService}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitFileSystemPdpAdminServiceTest {
	
	/** file repository location */
	private static final String FILE_LOCATION = "file";
	
	/** test policy */
	private static final String POLICY =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+ "<Policy PolicyId=\"ExamplePolicy\""  
		+ "RuleCombiningAlgId=\"urn:oasis:names:tc:xacml:1.0:rule-combining-alg" 
		+ "orithm:permit-overrides\"" 
		+ "xmlns=\"urn:oasis:names:tc:xacml:1.0:policy\""  
		+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
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
	public void testFileSystemPdpAdminService() {
		FileSystemPdpAdminService admin
				= new FileSystemPdpAdminService(FILE_LOCATION);
		assertNotNull(admin);
	}

	@Test
	public void testGetActivePolicyFolder() {
		FileSystemPdpAdminService admin
				= new FileSystemPdpAdminService(FILE_LOCATION);
		assertNotNull(admin.getActivePolicyFolder());
	}

	@Test
	public void testGetInactivePolicyFolder() {
		FileSystemPdpAdminService admin
				= new FileSystemPdpAdminService(FILE_LOCATION);
		assertNotNull(admin.getInactivePolicyFolder());
	}

	@Test
	public void testActivatePolicy() {
		FileSystemPdpAdminService admin
				= new FileSystemPdpAdminService(FILE_LOCATION);
		try {
			admin.publishPolicy("MyLittlePolicy", POLICY);
			admin.activatePolicy("MyLittlePolicy");
			admin.removePolicy("MyLittlePolicy");
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
	}

	@Test
	public void testDeactivatePolicy() {
		FileSystemPdpAdminService admin
				= new FileSystemPdpAdminService(FILE_LOCATION);
		try {
			admin.publishPolicy("MyLittlePolicy", POLICY);
			admin.activatePolicy("MyLittlePolicy");
			admin.deactivatePolicy("MyLittlePolicy");
			admin.removePolicy("MyLittlePolicy");
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
	}

	@Test
	public void testGetActivePolicyList() {
		FileSystemPdpAdminService admin
				= new FileSystemPdpAdminService(FILE_LOCATION);
		try {
			assertNotNull(admin.getActivePolicyList());
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
	}

	@Test
	public void testGetInActivePolicyList() {
		FileSystemPdpAdminService admin
				= new FileSystemPdpAdminService(FILE_LOCATION);
		try {
			assertNotNull(admin.getInActivePolicyList());
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
	}

	@Test
	public void testGetPolicy() {
		FileSystemPdpAdminService admin 
				= new FileSystemPdpAdminService(FILE_LOCATION);
		try {
			admin.publishPolicy("MyLittlePolicy", POLICY);
			assertNotNull(admin.getPolicy("MyLittlePolicy"));
			admin.removePolicy("MyLittlePolicy");
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
	}

	@Test
	public void testPublishPolicy() {
		FileSystemPdpAdminService admin 
				= new FileSystemPdpAdminService(FILE_LOCATION);
		try {
			admin.publishPolicy("MyLittlePolicy", POLICY);
			admin.removePolicy("MyLittlePolicy");
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
	}

	@Test
	public void testRemovePolicy() {
		FileSystemPdpAdminService admin 
				= new FileSystemPdpAdminService(FILE_LOCATION);
		try {
			admin.publishPolicy("MyLittlePolicy", POLICY);
			admin.removePolicy("MyLittlePolicy");
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
	}

	@Test
	public void testGetProperty() {
		FileSystemPdpAdminService admin 
				= new FileSystemPdpAdminService(FILE_LOCATION);
		try {
			admin.setProperty("ping", "pong");
			assertEquals("pong", admin.getProperty("ping"));
			fail("Exception expected");
		} catch (RemoteException re) {
			// not printing stack trace as an error is expected
		}
	}

	@Test
	public void testSetProperty() {
		FileSystemPdpAdminService admin 
				= new FileSystemPdpAdminService(FILE_LOCATION);
		try {
			admin.setProperty("ping", "pong");
			fail("Exception expected");
		} catch (RemoteException re) {
			// not printing stack trace as an error is expected
		}
	}
	
	@Before
	public void setupTempFolder() {
		new File(FILE_LOCATION).mkdir();
	}
	
	@After
	public void teardownTempFolder() {
		cleanup(new File(FILE_LOCATION));
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
