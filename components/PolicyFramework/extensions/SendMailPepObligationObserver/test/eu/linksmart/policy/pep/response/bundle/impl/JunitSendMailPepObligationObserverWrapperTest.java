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
package eu.linksmart.policy.pep.response.bundle.impl;

import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import junit.framework.TestCase;

import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.NetworkManagerApplication;
import com.sun.xacml.Obligation;
import com.sun.xacml.attr.DateTimeAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.ResponseCtx;

import eu.linksmart.policy.pep.impl.PepXacmlConstants;
import eu.linksmart.policy.pep.request.impl.PepRequest;
import eu.linksmart.policy.pep.response.bundle.impl.SendMailPepObligationObserverWrapper;

/**
 * Unit test for  {@link SendMailPepObligationObserverWrapper}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitSendMailPepObligationObserverWrapperTest extends TestCase {

	@Test
	public void testActivate() {
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(
				createMock(NetworkManagerApplication.class));
		expect(compCtxMock.getBundleContext()).andReturn(
				createMock(BundleContext.class));
		replay(compCtxMock);

		SendMailPepObligationObserverWrapper observer = getInstance();
		try {
			observer.activate(compCtxMock);
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		verify(compCtxMock);
	}

	@Test
	public void testDeactivate() {
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(
				createMock(NetworkManagerApplication.class));
		expect(compCtxMock.getBundleContext()).andReturn(
				createMock(BundleContext.class));
		replay(compCtxMock);
		
		SendMailPepObligationObserverWrapper observer = getInstance();
		try {
			observer.activate(compCtxMock);
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		observer.deactivate(compCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConfigurationBind() {
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		NetworkManagerApplication nm 
				= createMock(NetworkManagerApplication.class);
		expect(compCtxMock.locateService("NetworkManager"))
				.andReturn(nm);
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		ServiceReference sr = createMock(ServiceReference.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu" +
				"rationAdmin")).andReturn(sr);
		ConfigurationAdmin mockConfAdmin = createMock(ConfigurationAdmin.class);
		Configuration mockConfiguration = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("user", "user");
		ht.put("pass", "w0rd");
		expect(mockConfiguration.getProperties()).andReturn(ht);
		expectLastCall().times(4);
		try {
			mockConfiguration.update(ht);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(mockConfiguration);
		try {
			expect(mockConfAdmin.getConfiguration("eu.linksmart.policy.pep.ob" +
					"l.mail")).andReturn(mockConfiguration);
			expectLastCall().times(4);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(mockConfAdmin);
		expect(bundleCtxMock.getService(sr)).andReturn(mockConfAdmin);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pep.obl.mail");
		ServiceRegistration reg = createMock(ServiceRegistration.class);	
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm" +
				".ManagedService"),	anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(2);
		replay(bundleCtxMock);	
		expect(compCtxMock.getBundleContext()).andReturn(
				bundleCtxMock);
		replay(compCtxMock);

		SendMailPepObligationObserverWrapper observer = getInstance();
		try {
			observer.activate(compCtxMock);
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		observer.configurationBind(mockConfAdmin);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConfigurationUnbind() {
		ComponentContext cmpCtxMock = createMock(ComponentContext.class);
		NetworkManagerApplication nm 
				= createMock(NetworkManagerApplication.class);
		expect(cmpCtxMock.locateService("NetworkManager")).andReturn(nm);
		BundleContext bnCtxMock = createMock(BundleContext.class);
		ServiceReference sr = createMock(ServiceReference.class);
		expect(bnCtxMock.getServiceReference("org.osgi.service.cm.Configurati" +
				"onAdmin")).andReturn(sr);
		ConfigurationAdmin caMock = createMock(ConfigurationAdmin.class);
		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("user", "user");
		ht.put("pass", "w0rd");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(4);
		try {
			confMock.update(ht);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confMock);
		try {
			expect(caMock.getConfiguration("eu.linksmart.policy.pep.obl.mail"))
					.andReturn(confMock);
			expectLastCall().times(4);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(caMock);
		expect(bnCtxMock.getService(sr)).andReturn(caMock);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pep.obl.mail");
		ServiceRegistration reg = createMock(ServiceRegistration.class);	
		expect(bnCtxMock.registerService(matches("org.osgi.service.cm.Managed" +
				"Service"),	anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(2);
		replay(bnCtxMock);	
		expect(cmpCtxMock.getBundleContext()).andReturn(bnCtxMock);
		expectLastCall().times(2);
		replay(cmpCtxMock);

		SendMailPepObligationObserverWrapper observer = getInstance();
		try {
			observer.activate(cmpCtxMock);
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		observer.configurationBind(caMock);
		observer.configurationUnbind(caMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEvaluate() {
		ComponentContext cmpCtxMock = createMock(ComponentContext.class);
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		String[] hids = {"0.0.0.123"};
		try {
			expect(nmMock.getHIDByAttributes("0", "eu.linksmart.policy.pep.ob" +
					"l.mail", "(PID==PEP:Book)", 1000, 1)).andReturn(hids);
			expect(nmMock.getHIDByAttributes("0.0.0.123", "PEP:Book", 
					"(PID==EventManager:Book)", 1000, 1)).andReturn(hids);
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		expect(cmpCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		BundleContext bnCtxMock = createMock(BundleContext.class);
		ServiceReference sr = createMock(ServiceReference.class);
		expect(bnCtxMock.getServiceReference("org.osgi.service.cm.Configurati" +
				"onAdmin")).andReturn(sr);
		ConfigurationAdmin caMock = createMock(ConfigurationAdmin.class);
		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("user", "user");
		ht.put("pass", "w0rd");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(4);
		try {
			confMock.update(ht);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confMock);
		try {
			expect(caMock.getConfiguration("eu.linksmart.policy.pep.obl.mail"))
					.andReturn(confMock);
			expectLastCall().times(4);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(caMock);
		expect(bnCtxMock.getService(sr)).andReturn(caMock);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pep.obl.mail");
		ServiceRegistration reg = createMock(ServiceRegistration.class);	
		expect(bnCtxMock.registerService(matches("org.osgi.service.cm.Managed" +
				"Service"),	anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(2);
		replay(bnCtxMock);	
		expect(cmpCtxMock.getBundleContext()).andReturn(bnCtxMock);
		expectLastCall().times(2);
		replay(cmpCtxMock);
		
		ArrayList assignments = new ArrayList();
		Attribute attr = null;
		try {
			attr = new Attribute(new URI(
					PepXacmlConstants.OBLIGATION_DEBUG_POLICY_NAME.getUrn()), 
					"me", new DateTimeAttribute(), new StringAttribute("hello"));
		} catch (URISyntaxException use) {
			use.printStackTrace();
			fail("Exception");
		}
		assignments.add(attr);
		Obligation obli = null;
		try {
			obli = new Obligation(new URI(
					PepXacmlConstants.OBLIGATION_DEBUG.getUrn()), 1, assignments);
		} catch (URISyntaxException use) {
			use.printStackTrace();
		}

		SendMailPepObligationObserverWrapper observer = getInstance();
		try {
			observer.activate(cmpCtxMock);
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		observer.configurationBind(caMock);
		observer.evaluate(obli, new PepRequest(null, null, null, null, null, 
				null, null), createMock(ResponseCtx.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testApplyConfigurations() {
		ComponentContext cmpCtxMock = createMock(ComponentContext.class);
		NetworkManagerApplication nm 
				= createMock(NetworkManagerApplication.class);
		expect(cmpCtxMock.locateService("NetworkManager")).andReturn(nm);
		BundleContext bnCtxMock = createMock(BundleContext.class);
		ServiceReference sr = createMock(ServiceReference.class);
		expect(bnCtxMock.getServiceReference("org.osgi.service.cm.Configurati" +
				"onAdmin")).andReturn(sr);
		ConfigurationAdmin caMock = createMock(ConfigurationAdmin.class);
		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("user", "user");
		ht.put("pass", "w0rd");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(4);
		try {
			confMock.update(ht);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confMock);
		try {
			expect(caMock.getConfiguration("eu.linksmart.policy.pep.obl.mail"))
					.andReturn(confMock);
			expectLastCall().times(4);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(caMock);
		expect(bnCtxMock.getService(sr)).andReturn(caMock);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pep.obl.mail");
		ServiceRegistration reg = createMock(ServiceRegistration.class);	
		expect(bnCtxMock.registerService(matches("org.osgi.service.cm.Managed" +
				"Service"),	anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(2);
		replay(bnCtxMock);	
		expect(cmpCtxMock.getBundleContext()).andReturn(bnCtxMock);
		expectLastCall().times(2);
		replay(cmpCtxMock);
		
		SendMailPepObligationObserverWrapper observer = getInstance();
		try {
			observer.activate(cmpCtxMock);
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		observer.configurationBind(caMock);
		observer.applyConfigurations(ht);
	}
	
	/**
	 * @return
	 * 				an {@link SendMailPepObligationObserverWrapper}
	 */
	private SendMailPepObligationObserverWrapper getInstance() {
		return new SendMailPepObligationObserverWrapper();
	}

}
