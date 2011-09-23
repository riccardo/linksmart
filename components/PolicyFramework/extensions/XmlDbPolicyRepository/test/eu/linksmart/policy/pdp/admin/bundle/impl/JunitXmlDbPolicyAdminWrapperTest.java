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

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.NetworkManagerApplication;

import eu.linksmart.policy.pdp.admin.bundle.impl.XmlDbPolicyAdminConfigurator;
import eu.linksmart.policy.pdp.admin.bundle.impl.XmlDbPolicyAdminWrapper;

/**
 * Unit test for {@link XmlDbPolicyAdminWrapper}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitXmlDbPolicyAdminWrapperTest {

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
		ComponentContext ctxMock = createMock(ComponentContext.class);
		expect(ctxMock.locateService("NetworkManager")).andReturn(
				createMock(NetworkManagerApplication.class));
		expect(ctxMock.getBundleContext()).andReturn(
				createMock(BundleContext.class));
		replay(ctxMock);
		
		try {
			XmlDbPolicyAdminWrapper admin = new XmlDbPolicyAdminWrapper();
			admin.activate(ctxMock);
			Hashtable updates = new Hashtable();
			updates.put(XmlDbPolicyAdminConfigurator.DB_PATH, "");
			admin.applyConfigurations(updates);
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(ctxMock);
	}

	@Test
	public void testGetAdmin() {
		ComponentContext ctxMock = createMock(ComponentContext.class);
		expect(ctxMock.locateService("NetworkManager")).andReturn(
				createMock(NetworkManagerApplication.class));
		expect(ctxMock.getBundleContext()).andReturn(
				createMock(BundleContext.class));
		replay(ctxMock);
		
		try {
			XmlDbPolicyAdminWrapper admin = new XmlDbPolicyAdminWrapper();
			admin.activate(ctxMock);
			assertNotNull(admin.getAdmin());
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(ctxMock);
	}

	@Test
	public void testActivatePolicy() {
		ComponentContext ctxMock = createMock(ComponentContext.class);
		expect(ctxMock.locateService("NetworkManager")).andReturn(
				createMock(NetworkManagerApplication.class));
		expect(ctxMock.getBundleContext()).andReturn(
				createMock(BundleContext.class));
		replay(ctxMock);	
		
		try {
			XmlDbPolicyAdminWrapper admin = new XmlDbPolicyAdminWrapper();
			admin.activate(ctxMock);
			try {
				admin.publishPolicy("MyLittlePolicy", POLICY);
				admin.activatePolicy("MyLittlePolicy");
				admin.removePolicy("MyLittlePolicy");
			} catch (RemoteException re) {
				re.printStackTrace();
			}
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(ctxMock);
	}

	@Test
	public void testDeactivatePolicy() {
		ComponentContext ctxMock = createMock(ComponentContext.class);
		expect(ctxMock.locateService("NetworkManager")).andReturn(
				createMock(NetworkManagerApplication.class));
		expect(ctxMock.getBundleContext()).andReturn(
				createMock(BundleContext.class));
		replay(ctxMock);
		
		try {
			XmlDbPolicyAdminWrapper admin = new XmlDbPolicyAdminWrapper();
			admin.activate(ctxMock);
			try {
				try {
					admin.publishPolicy("MyLittlePolicy", POLICY);
					admin.activatePolicy("MyLittlePolicy");
					admin.removePolicy("MyLittlePolicy");
				} catch (RemoteException re) {
					re.printStackTrace();
				}
			} catch (NullPointerException npe) {
				// intentionally left blank
			}
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(ctxMock);
	}

	@Test
	public void testGetActivePolicyList() {
		ComponentContext ctxMock = createMock(ComponentContext.class);
		expect(ctxMock.locateService("NetworkManager")).andReturn(
				createMock(NetworkManagerApplication.class));
		expect(ctxMock.getBundleContext()).andReturn(
				createMock(BundleContext.class));
		replay(ctxMock);
		
		try {
			XmlDbPolicyAdminWrapper admin = new XmlDbPolicyAdminWrapper();
			admin.activate(ctxMock);
			try {
				try {
					assertNotNull(admin.getActivePolicyList());
				} catch (RemoteException re) {
					re.printStackTrace();
				}
			} catch (NullPointerException npe) {
				// intentionally left blank
			}
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(ctxMock);
	}

	@Test
	public void testGetInActivePolicyList() {
		ComponentContext ctxMock = createMock(ComponentContext.class);
		expect(ctxMock.locateService("NetworkManager")).andReturn(
				createMock(NetworkManagerApplication.class));
		expect(ctxMock.getBundleContext()).andReturn(
				createMock(BundleContext.class));
		replay(ctxMock);
		
		try {
			XmlDbPolicyAdminWrapper admin = new XmlDbPolicyAdminWrapper();
			admin.activate(ctxMock);
			try {			
				assertNotNull(admin.getInActivePolicyList());
			} catch (RemoteException re) {
				re.printStackTrace();
			}
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(ctxMock);
	}

	@Test
	public void testGetPolicy() {
		ComponentContext ctxMock = createMock(ComponentContext.class);
		expect(ctxMock.locateService("NetworkManager")).andReturn(
				createMock(NetworkManagerApplication.class));
		expect(ctxMock.getBundleContext()).andReturn(
				createMock(BundleContext.class));
		replay(ctxMock);
		
		try {
			XmlDbPolicyAdminWrapper admin = new XmlDbPolicyAdminWrapper();
			admin.activate(ctxMock);
			try {
				admin.publishPolicy("MyLittlePolicy", POLICY);
				assertNotNull(admin.getPolicy("MyLittlePolicy"));
				admin.removePolicy("MyLittlePolicy");
			} catch (RemoteException re) {
				re.printStackTrace();
			}
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(ctxMock);
	}

	@Test
	public void testGetProperty() {
		ComponentContext ctxMock = createMock(ComponentContext.class);
		expect(ctxMock.locateService("NetworkManager")).andReturn(
				createMock(NetworkManagerApplication.class));
		expect(ctxMock.getBundleContext()).andReturn(
				createMock(BundleContext.class));
		replay(ctxMock);
		
		try {
			XmlDbPolicyAdminWrapper admin = new XmlDbPolicyAdminWrapper();
			admin.activate(ctxMock);
			try {
				admin.setProperty("HELLO", "world");
				assertEquals("world", admin.getProperty("HELLO"));
			} catch (RemoteException re) {
				re.printStackTrace();
			}
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(ctxMock);
	}

	@Test
	public void testPublishPolicy() {
		ComponentContext ctxMock = createMock(ComponentContext.class);
		expect(ctxMock.locateService("NetworkManager")).andReturn(
				createMock(NetworkManagerApplication.class));
		expect(ctxMock.getBundleContext()).andReturn(
				createMock(BundleContext.class));
		replay(ctxMock);
		
		try {
			XmlDbPolicyAdminWrapper admin = new XmlDbPolicyAdminWrapper();
			admin.activate(ctxMock);
			try {
				admin.publishPolicy("MyLittlePolicy", POLICY);
				assertNotNull(admin.getPolicy("MyLittlePolicy"));
				admin.removePolicy("MyLittlePolicy");
			} catch (RemoteException re) {
				re.printStackTrace();
			}
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(ctxMock);
	}

	@Test
	public void testRemovePolicy() {
		ComponentContext ctxMock = createMock(ComponentContext.class);
		expect(ctxMock.locateService("NetworkManager")).andReturn(
				createMock(NetworkManagerApplication.class));
		expect(ctxMock.getBundleContext()).andReturn(
				createMock(BundleContext.class));
		replay(ctxMock);
		
		try {
			XmlDbPolicyAdminWrapper admin = new XmlDbPolicyAdminWrapper();
			admin.activate(ctxMock);
			try {
				admin.publishPolicy("MyLittlePolicy", POLICY);
				assertNotNull(admin.getPolicy("MyLittlePolicy"));
				admin.removePolicy("MyLittlePolicy");
			} catch (RemoteException re) {
				re.printStackTrace();
			}
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(ctxMock);
	}

	@Test
	public void testSetProperty() {
		ComponentContext ctxMock = createMock(ComponentContext.class);
		expect(ctxMock.locateService("NetworkManager")).andReturn(
				createMock(NetworkManagerApplication.class));
		expect(ctxMock.getBundleContext()).andReturn(
				createMock(BundleContext.class));
		replay(ctxMock);
		
		try {
			XmlDbPolicyAdminWrapper admin = new XmlDbPolicyAdminWrapper();
			admin.activate(ctxMock);
			try {
				admin.setProperty("HELLO", "world");
				assertEquals("world", admin.getProperty("HELLO"));
			} catch (RemoteException re) {
				re.printStackTrace();
			}
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(ctxMock);
	}

	@Test
	public void testActivate() {
		ComponentContext ctxMock = createMock(ComponentContext.class);
		expect(ctxMock.locateService("NetworkManager")).andReturn(
				createMock(NetworkManagerApplication.class));
		expect(ctxMock.getBundleContext()).andReturn(
				createMock(BundleContext.class));
		replay(ctxMock);
		
		try {
			XmlDbPolicyAdminWrapper admin = new XmlDbPolicyAdminWrapper();
			admin.activate(ctxMock);
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(ctxMock);
	}

	@Test
	public void testDeactivate() {
		ComponentContext ctxMock = createMock(ComponentContext.class);
		expect(ctxMock.locateService("NetworkManager")).andReturn(
				createMock(NetworkManagerApplication.class));
		expect(ctxMock.getBundleContext()).andReturn(
				createMock(BundleContext.class));
		replay(ctxMock);
		
		try {
			XmlDbPolicyAdminWrapper admin = new XmlDbPolicyAdminWrapper();
			admin.activate(ctxMock);
			admin.deactivate(ctxMock);
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(ctxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConfigurationBind() {
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
		}
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.ad" +
					"min.xmldb")).andReturn(confMock);
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		ServiceReference sr = createMock(ServiceReference.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con" +
				"figurationAdmin")).andReturn(sr);
		expect(bundleCtxMock.getService(sr)).andReturn(confAdminMock);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.admin.xmldb");
		ServiceRegistration reg = createMock(ServiceRegistration.class);	
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm" +
				".ManagedService"),	anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(2);
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(
				createMock(NetworkManagerApplication.class));
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		replay(compCtxMock);
		
		try {
			XmlDbPolicyAdminWrapper admin = new XmlDbPolicyAdminWrapper();
			admin.activate(compCtxMock);
			admin.configurationBind(confAdminMock);
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(confMock);
		verify(confAdminMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConfigurationUnbind() {
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
		}
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.ad" +
					"min.xmldb")).andReturn(confMock);
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		ServiceReference sr = createMock(ServiceReference.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con" +
				"figurationAdmin")).andReturn(sr);
		expect(bundleCtxMock.getService(sr)).andReturn(confAdminMock);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.admin.xmldb");
		ServiceRegistration reg = createMock(ServiceRegistration.class);	
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm" +
				".ManagedService"),	anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(2);
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(
				createMock(NetworkManagerApplication.class));
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		replay(compCtxMock);
		
		try {
			XmlDbPolicyAdminWrapper admin = new XmlDbPolicyAdminWrapper();
			admin.activate(compCtxMock);
			admin.configurationBind(confAdminMock);
			admin.configurationUnbind(confAdminMock);
		} catch (NullPointerException npe) {
			// intentionally left blank
		}		
		verify(confMock);
		verify(confAdminMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

}
