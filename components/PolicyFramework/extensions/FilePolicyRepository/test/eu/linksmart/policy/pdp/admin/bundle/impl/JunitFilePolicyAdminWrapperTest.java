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
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.NetworkManagerApplication;

import eu.linksmart.policy.pdp.admin.bundle.impl.FilePolicyAdminConfigurator;
import eu.linksmart.policy.pdp.admin.bundle.impl.FilePolicyAdminWrapper;

/**
 * Unit test for {@link FilePolicyAdminWrapper}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitFilePolicyAdminWrapperTest {
	
	/** file repository location */
	private static final String FILE_LOCATION = "PolicyFramework";
	
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

	@SuppressWarnings("unchecked")
	@Test
	public void testApplyConfigurations() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(2);
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.ad" +
					"min.file")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu" +
				"rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.registerService(isA(String.class), anyObject(), 
				isA(Dictionary.class))).andReturn(regMock);
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		replay(compCtxMock);
		
		try {
			FilePolicyAdminWrapper admin = new FilePolicyAdminWrapper();
			admin.activate(compCtxMock);
			Hashtable updates = new Hashtable();
			updates.put(FilePolicyAdminConfigurator.FILE_PATH, "");
			admin.applyConfigurations(updates);
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(regMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetAdmin() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);

		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.ad"
					+ "min.file")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu"
						+ "rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.registerService(isA(String.class), anyObject(),
						isA(Dictionary.class))).andReturn(regMock);
		replay(bundleCtxMock);

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		replay(compCtxMock);

		try {
			FilePolicyAdminWrapper admin = new FilePolicyAdminWrapper();
			admin.activate(compCtxMock);
			assertNotNull(admin.getAdmin());
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(regMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testActivatePolicy() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);

		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.ad"
					+ "min.file")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu"
				+ "rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.registerService(isA(String.class), anyObject(),
				isA(Dictionary.class))).andReturn(regMock);
		replay(bundleCtxMock);

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		replay(compCtxMock);

		try {
			FilePolicyAdminWrapper admin = new FilePolicyAdminWrapper();
			admin.activate(compCtxMock);
			try {
				admin.removePolicy("MyLittlePolicy");
				admin.publishPolicy("MyLittlePolicy", POLICY);
				admin.activatePolicy("MyLittlePolicy");
				admin.removePolicy("MyLittlePolicy");
			} catch (RemoteException re) {
				re.printStackTrace();
				fail("Exception");
			}
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(regMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDeactivatePolicy() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(2);
		replay(confMock);		

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.ad"
					+ "min.file")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu"
				+ "rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.registerService(isA(String.class), anyObject(),
				isA(Dictionary.class))).andReturn(regMock);
		replay(bundleCtxMock);		
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		replay(compCtxMock);
		
		try {
			FilePolicyAdminWrapper admin = new FilePolicyAdminWrapper();
			admin.activate(compCtxMock);
			try {
				try {
					admin.removePolicy("MyLittlePolicy");
					admin.publishPolicy("MyLittlePolicy", POLICY);
					admin.activatePolicy("MyLittlePolicy");
					admin.removePolicy("MyLittlePolicy");
				} catch (RemoteException re) {
					re.printStackTrace();
					fail("Exception");
				}
			} catch (NullPointerException npe) {
				// intentionally left blank
			}
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(regMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetActivePolicyList() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);

		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.ad"
					+ "min.file")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu"
				+ "rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.registerService(isA(String.class), anyObject(),
				isA(Dictionary.class))).andReturn(regMock);
		replay(bundleCtxMock);

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		replay(compCtxMock);

		try {
			FilePolicyAdminWrapper admin = new FilePolicyAdminWrapper();
			admin.activate(compCtxMock);
			try {
				try {
					assertNotNull(admin.getActivePolicyList());
				} catch (RemoteException re) {
					re.printStackTrace();
					fail("Exception");
				}
			} catch (NullPointerException npe) {
				// intentionally left blank
			}
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(regMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetInActivePolicyList() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);

		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.ad"
				+ "min.file")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu"
				+ "rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.registerService(isA(String.class), anyObject(),
				isA(Dictionary.class))).andReturn(regMock);
		replay(bundleCtxMock);

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		replay(compCtxMock);

		try {
			FilePolicyAdminWrapper admin = new FilePolicyAdminWrapper();
			admin.activate(compCtxMock);
			try {			
				assertNotNull(admin.getInActivePolicyList());
			} catch (RemoteException re) {
				re.printStackTrace();
				fail("Exception");
			}
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(regMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetPolicy() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);

		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.ad"
					+ "min.file")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu"
				+ "rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.registerService(isA(String.class), anyObject(),
				isA(Dictionary.class))).andReturn(regMock);
		replay(bundleCtxMock);

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		replay(compCtxMock);

		try {
			FilePolicyAdminWrapper admin = new FilePolicyAdminWrapper();
			admin.activate(compCtxMock);
			try {
				admin.removePolicy("MyLittlePolicy");
				admin.publishPolicy("MyLittlePolicy", POLICY);
				assertNotNull(admin.getPolicy("MyLittlePolicy"));
				admin.removePolicy("MyLittlePolicy");
			} catch (RemoteException re) {
				re.printStackTrace();
				fail("Exception");
			}
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(regMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetProperty() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);

		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.ad"
					+ "min.file")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu"
				+ "rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.registerService(isA(String.class), anyObject(),
				isA(Dictionary.class))).andReturn(regMock);
		replay(bundleCtxMock);

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		replay(compCtxMock);

		try {
			FilePolicyAdminWrapper admin = new FilePolicyAdminWrapper();
			admin.activate(compCtxMock);
			try {
				admin.setProperty("HELLO", "world");
				assertEquals("world", admin.getProperty("HELLO"));
				fail("Exception not thrown");
			} catch (RemoteException re) {
				// not printed, because expected
			}
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(regMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPublishPolicy() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);

		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.ad"
					+ "min.file")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu"
				+ "rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.registerService(isA(String.class), anyObject(),
				isA(Dictionary.class))).andReturn(regMock);
		replay(bundleCtxMock);

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		replay(compCtxMock);

		try {
			FilePolicyAdminWrapper admin = new FilePolicyAdminWrapper();
			admin.activate(compCtxMock);
			try {
				admin.removePolicy("MyLittlePolicy");
				admin.publishPolicy("MyLittlePolicy", POLICY);
				assertNotNull(admin.getPolicy("MyLittlePolicy"));
				admin.removePolicy("MyLittlePolicy");
			} catch (RemoteException re) {
				re.printStackTrace();
				fail("Exception");
			}
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(regMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRemovePolicy() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);

		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.ad"
					+ "min.file")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu"
				+ "rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.registerService(isA(String.class), anyObject(),
				isA(Dictionary.class))).andReturn(regMock);
		replay(bundleCtxMock);

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		replay(compCtxMock);

		try {
			FilePolicyAdminWrapper admin = new FilePolicyAdminWrapper();
			admin.activate(compCtxMock);
			try {
				
				admin.publishPolicy("MyLittlePolicy", POLICY);
				assertNotNull(admin.getPolicy("MyLittlePolicy"));
				admin.removePolicy("MyLittlePolicy");
			} catch (RemoteException re) {
				re.printStackTrace();
				fail("Exception");
			}
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(regMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSetProperty() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);

		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.ad"
					+ "min.file")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu"
				+ "rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.registerService(isA(String.class), anyObject(),
				isA(Dictionary.class))).andReturn(regMock);
		replay(bundleCtxMock);

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		replay(compCtxMock);

		try {
			FilePolicyAdminWrapper admin = new FilePolicyAdminWrapper();
			admin.activate(compCtxMock);
			try {
				admin.setProperty("HELLO", "world");
				assertEquals("world", admin.getProperty("HELLO"));
				fail("Exception not thrown");
			} catch (RemoteException re) {
				// not printed, because exception not thrown
			}
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(regMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testActivate() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);

		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.ad"
					+ "min.file")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu"
				+ "rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.registerService(isA(String.class), anyObject(),
				isA(Dictionary.class))).andReturn(regMock);
		replay(bundleCtxMock);

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		replay(compCtxMock);

		try {
			FilePolicyAdminWrapper admin = new FilePolicyAdminWrapper();
			admin.activate(compCtxMock);
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(regMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDeactivate() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);

		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.ad"
					+ "min.file")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu"
				+ "rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.registerService(isA(String.class), anyObject(),
				isA(Dictionary.class))).andReturn(regMock);
		replay(bundleCtxMock);

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		replay(compCtxMock);
		
		try {
			FilePolicyAdminWrapper admin = new FilePolicyAdminWrapper();
			admin.activate(compCtxMock);
			admin.deactivate(compCtxMock);
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(regMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConfigurationBind() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);

		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Book");
		ht.put("PEP.PID", "PEP:Book");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(3);
		try {
			confMock.update(ht);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.ad" +
					"min.file")).andReturn(confMock);
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);
			
		ServiceRegistration regMock = createMock(ServiceRegistration.class);	
		replay(regMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con" +
				"figurationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.admin.file");
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm" +
				".ManagedService"),	anyObject(), eq(d))).andReturn(regMock);
		expectLastCall().times(2);
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(
				createMock(NetworkManagerApplication.class));
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		replay(compCtxMock);
		
		try {
			FilePolicyAdminWrapper admin = new FilePolicyAdminWrapper();
			admin.activate(compCtxMock);
			admin.configurationBind(confAdminMock);
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(regMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConfigurationUnbind() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);

		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Book");
		ht.put("PEP.PID", "PEP:Book");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(3);
		try {
			confMock.update(ht);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.ad"
					+ "min.file")).andReturn(confMock);
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		regMock.unregister();
		replay(regMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con"
				+ "figurationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		Dictionary d = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.admin.file");
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm"
				+ ".ManagedService"), anyObject(), eq(d))).andReturn(
				regMock);
		expectLastCall().times(2);
		replay(bundleCtxMock);

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(
				createMock(NetworkManagerApplication.class));
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		replay(compCtxMock);

		try {
			FilePolicyAdminWrapper admin = new FilePolicyAdminWrapper();
			admin.activate(compCtxMock);
			admin.configurationBind(confAdminMock);
			admin.configurationUnbind(confAdminMock);
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(regMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
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
