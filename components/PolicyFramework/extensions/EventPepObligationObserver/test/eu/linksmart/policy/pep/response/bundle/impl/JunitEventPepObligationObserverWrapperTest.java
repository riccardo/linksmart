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
import eu.linksmart.policy.pep.response.bundle.impl.EventPepObligationObserverWrapper;

/**
 * Unit test for  {@link EventPepObligationObserverWrapper}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitEventPepObligationObserverWrapperTest extends TestCase {

	@Test
	public void testActivate() {
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(
				createMock(NetworkManagerApplication.class));
		expect(compCtxMock.getBundleContext()).andReturn(
				createMock(BundleContext.class));
		replay(compCtxMock);

		EventPepObligationObserverWrapper observer = getInstance();
		try {
			observer.activate(compCtxMock);
		} catch (NullPointerException npe) {
			// expected exception, hence does not lead to failed test
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
		
		EventPepObligationObserverWrapper observer = getInstance();
		try {
			observer.activate(compCtxMock);
		} catch (NullPointerException npe) {
			// expected exception, hence does not lead to failed test
		}
		observer.deactivate(compCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConfigurationBind() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		String[] hids = {"1.2.3.4"};
		try {
			expect(nmMock.getHIDByAttributes(eq("0"), eq("eu.linksmart.policy" +
					".pep.obl.event"), isA(String.class), anyInt(), anyInt()))
					.andReturn(hids);
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Yarr");
		ht.put("PEP.PID", "PEP:Yarr");
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
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pep.ob" +
					"l.event")).andReturn(confMock);
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con" +
				"figurationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pep.obl.event");
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm" +
				".ManagedService"),	anyObject(), eq(d))).andReturn(regMock);
		expectLastCall().times(2);
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager"))
				.andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		replay(compCtxMock);

		EventPepObligationObserverWrapper observer = getInstance();
		try {
			observer.activate(compCtxMock);
		} catch (NullPointerException npe) {
			// expected exception, hence does not lead to failed test
		}
		observer.configurationBind(confAdminMock);
		verify(nmMock);
		verify(refMock);
		verify(regMock);
		verify(confMock);
		verify(confAdminMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConfigurationUnbind() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		String[] hids = {"1.2.3.4"};
		try {
			expect(nmMock.getHIDByAttributes(eq("0"), eq("eu.linksmart.policy" +
					".pep.obl.event"), isA(String.class), anyInt(), anyInt()))
					.andReturn(hids);
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Yarr");
		ht.put("PEP.PID", "PEP:Yarr");
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
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pep.ob" +
					"l.event")).andReturn(confMock);
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu" +
				"rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pep.obl.event");
		ServiceRegistration reg = createMock(ServiceRegistration.class);	
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm.Man" +
				"agedService"),	anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(2);
		replay(bundleCtxMock);

		ComponentContext cmpCtxMock = createMock(ComponentContext.class);
		expect(cmpCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(cmpCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		replay(cmpCtxMock);

		EventPepObligationObserverWrapper observer = getInstance();
		try {
			observer.activate(cmpCtxMock);
		} catch (NullPointerException npe) {
			// expected exception, hence does not lead to failed test
		}
		observer.configurationBind(confAdminMock);
		observer.configurationUnbind(confAdminMock);
		verify(nmMock);
		verify(refMock);
		verify(confMock);
		verify(confAdminMock);
		verify(bundleCtxMock);
		verify(cmpCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEvaluate() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		String[] hids = {"0.0.0.123"};
		try {
			expect(nmMock.getHIDByAttributes("0", "eu.linksmart.policy.pep.ob" +
					"l.event", "(PID==PEP:Yarr)", 1000, 1)).andReturn(hids);
			expectLastCall().times(2);
			expect(nmMock.getHIDByAttributes("0.0.0.123", "PEP:Yarr", 
					"(PID==EventManager:Yarr)", 1000, 1)).andReturn(hids);
			expectLastCall().times(2);
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);

		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);
				
		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Yarr");
		ht.put("PEP.PID", "PEP:Yarr");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(4);
		replay(confMock);
		
		ConfigurationAdmin caMock = createMock(ConfigurationAdmin.class);
		try {
			expect(caMock.getConfiguration("eu.linksmart.policy.pep.obl.event"))
					.andReturn(confMock);
			expectLastCall().times(4);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(caMock);

		BundleContext bnCtxMock = createMock(BundleContext.class);
		expect(bnCtxMock.getServiceReference("org.osgi.service.cm.Configurati" +
				"onAdmin")).andReturn(refMock);
		expectLastCall().times(2);
		expect(bnCtxMock.getService(refMock)).andReturn(caMock);
		expectLastCall().times(2);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pep.obl.event");
		expect(bnCtxMock.registerService(matches("org.osgi.service.cm.Managed" +
				"Service"),	anyObject(), eq(d))).andReturn(regMock);
		expectLastCall().times(2);
		replay(bnCtxMock);	

		ComponentContext cmpCtxMock = createMock(ComponentContext.class);
		expect(cmpCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(cmpCtxMock.getBundleContext()).andReturn(bnCtxMock);
		expectLastCall().times(2);
		replay(cmpCtxMock);
		
		ArrayList assignments = new ArrayList();
		Attribute attr = null;
		Obligation obli = null;
		try {
			/* not applicable */
			attr = new Attribute(new URI(
					PepXacmlConstants.OBLIGATION_DEBUG_POLICY_NAME.getUrn()), 
					"me", new DateTimeAttribute(), new StringAttribute("hello"));
			assignments.add(attr);
			obli = new Obligation(new URI(
					PepXacmlConstants.OBLIGATION_DEBUG.getUrn()), 1, 
					assignments);
		} catch (URISyntaxException use) {
			use.printStackTrace();
			fail("Exception");
		}
		EventPepObligationObserverWrapper observer = getInstance();
		try {
			observer.activate(cmpCtxMock);
			assertFalse(observer.evaluate(obli, new PepRequest(null, null, null, 
					null, null,	null, null), createMock(ResponseCtx.class)));
		} catch (NullPointerException npe) {
			// expected exception, hence does not lead to failed test
		}
		assignments.clear();
		try {
			/* not applicable */
			attr = new Attribute(new URI(
					PepXacmlConstants.OBLIGATION_DISPATCH_EVENT_KEY.getUrn()), 
					"me", new DateTimeAttribute(), new StringAttribute("hello"));
			assignments.add(attr);
			obli = new Obligation(new URI(
					PepXacmlConstants.OBLIGATION_DISPATCH_EVENT.getUrn()), 1, 
					assignments);
		} catch (URISyntaxException use) {
			use.printStackTrace();
			fail("Exception");
		}
		try {
			observer.activate(cmpCtxMock);
			assertFalse(observer.evaluate(obli, new PepRequest(null, null, null, 
					null, null,	null, null), createMock(ResponseCtx.class)));
		} catch (NullPointerException npe) {
			// expected exception, hence does not lead to failed test
		}
		verify(nmMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(caMock);
		verify(bnCtxMock);
		verify(cmpCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testApplyConfigurations() {		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		String[] hids = {"0.0.0.123"};
		try {
			expect(nmMock.getHIDByAttributes("0", "eu.linksmart.policy.pep.ob" +
					"l.event", "(PID==PEP:Yarr)", 1000, 1)).andReturn(hids);
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		ServiceReference sr = createMock(ServiceReference.class);
		replay(sr);
		
		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Yarr");
		ht.put("PEP.PID", "PEP:Yarr");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(3);
		try {
			confMock.update(ht);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin caMock = createMock(ConfigurationAdmin.class);		
		try {
			expect(caMock.getConfiguration("eu.linksmart.policy.pep.obl.event"))
					.andReturn(confMock);
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(caMock);
		
		BundleContext bnCtxMock = createMock(BundleContext.class);		
		expect(bnCtxMock.getServiceReference("org.osgi.service.cm.Configurati" +
				"onAdmin")).andReturn(sr);
		expect(bnCtxMock.getService(sr)).andReturn(caMock);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pep.obl.event");
		ServiceRegistration reg = createMock(ServiceRegistration.class);	
		expect(bnCtxMock.registerService(matches("org.osgi.service.cm.Managed" +
				"Service"),	anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(2);
		replay(bnCtxMock);	

		ComponentContext cmpCtxMock = createMock(ComponentContext.class);
		expect(cmpCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(cmpCtxMock.getBundleContext()).andReturn(bnCtxMock);
		replay(cmpCtxMock);
		
		EventPepObligationObserverWrapper observer = getInstance();
		try {
			observer.activate(cmpCtxMock);
		} catch (NullPointerException npe) {
			// intentionally left blank
		}
		observer.configurationBind(caMock);
		observer.applyConfigurations(ht);
		verify(nmMock);
		verify(sr);
		verify(confMock);
		verify(caMock);
		verify(bnCtxMock);
		verify(cmpCtxMock);
	}
	
	/**
	 * @return
	 * 				an {@link EventPepObligationObserverWrapper}
	 */
	private EventPepObligationObserverWrapper getInstance() {
		return new EventPepObligationObserverWrapper();
	}

}
