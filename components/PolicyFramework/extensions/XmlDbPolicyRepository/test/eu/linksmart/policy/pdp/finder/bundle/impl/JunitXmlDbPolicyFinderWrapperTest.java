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
package eu.linksmart.policy.pdp.finder.bundle.impl;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.NetworkManagerApplication;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.finder.PolicyFinder;

import eu.linksmart.policy.pdp.finder.bundle.impl.XmlDbPolicyFinderConfigurator;
import eu.linksmart.policy.pdp.finder.bundle.impl.XmlDbPolicyFinderWrapper;

/**
 * Unit test for {@link XmlDbPolicyFinderWrapper}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitXmlDbPolicyFinderWrapperTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testIsRequestSupported() {
		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Yarr");
		ht.put("PEP.PID", "PEP:Yarr");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi" +
					"nder.xmldb")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		BundleContext bundleCtxMock = createNiceMock(BundleContext.class);
		ServiceReference sr = createMock(ServiceReference.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con" +
				"figurationAdmin")).andReturn(sr);
		expect(bundleCtxMock.getService(sr)).andReturn(confAdminMock);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.xmldb");
		ServiceRegistration reg = createMock(ServiceRegistration.class);	
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm" +
				".ManagedService"),	anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(1);
		Filter filter = createMock(Filter.class);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filter);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		NetworkManagerApplication nmMock
				= createMock(NetworkManagerApplication.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(
				nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);
		
		XmlDbPolicyFinderWrapper finder = new XmlDbPolicyFinderWrapper();
		finder.activate(compCtxMock);
		assertFalse(finder.isRequestSupported());
		verify(confMock);
		verify(confAdminMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testIsIdReferenceSupported() {
		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Yarr");
		ht.put("PEP.PID", "PEP:Yarr");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi" +
					"nder.xmldb")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		BundleContext bundleCtxMock = createNiceMock(BundleContext.class);
		ServiceReference sr = createMock(ServiceReference.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con" +
				"figurationAdmin")).andReturn(sr);
		expect(bundleCtxMock.getService(sr)).andReturn(confAdminMock);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.xmldb");
		ServiceRegistration reg = createMock(ServiceRegistration.class);	
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm" +
				".ManagedService"),	anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(1);
		Filter filter = createMock(Filter.class);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filter);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}				
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		NetworkManagerApplication nmMock
				= createMock(NetworkManagerApplication.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(
				nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);
		
		XmlDbPolicyFinderWrapper finder = new XmlDbPolicyFinderWrapper();
		finder.activate(compCtxMock);
		assertFalse(finder.isIdReferenceSupported());
		verify(confMock);
		verify(confAdminMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@Test
	public void testInvalidateCache() {
		XmlDbPolicyFinderWrapper finder = new XmlDbPolicyFinderWrapper();
		finder.invalidateCache();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testApplyConfigurations() {
		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Yarr");
		ht.put("PEP.PID", "PEP:Yarr");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi" +
					"nder.xmldb")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		BundleContext bundleCtxMock = createNiceMock(BundleContext.class);
		ServiceReference sr = createMock(ServiceReference.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con" +
				"figurationAdmin")).andReturn(sr);
		expect(bundleCtxMock.getService(sr)).andReturn(confAdminMock);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.xmldb");
		ServiceRegistration reg = createMock(ServiceRegistration.class);	
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm" +
				".ManagedService"),	anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(1);
		Filter filter = createMock(Filter.class);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filter);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		NetworkManagerApplication nmMock
				= createMock(NetworkManagerApplication.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(
				nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);
		
		XmlDbPolicyFinderWrapper finder = new XmlDbPolicyFinderWrapper();
		finder.activate(compCtxMock);
		Hashtable updates = new Hashtable();
		updates.put(XmlDbPolicyFinderConfigurator.SID, "lala");
		finder.applyConfigurations(updates);
		verify(confMock);
		verify(confAdminMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFindPolicyEvaluationCtx() {
		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Yarr");
		ht.put("PEP.PID", "PEP:Yarr");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi" +
					"nder.xmldb")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		BundleContext bundleCtxMock = createNiceMock(BundleContext.class);
		ServiceReference sr = createMock(ServiceReference.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con" +
				"figurationAdmin")).andReturn(sr);
		expect(bundleCtxMock.getService(sr)).andReturn(confAdminMock);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.xmldb");
		ServiceRegistration reg = createMock(ServiceRegistration.class);	
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm" +
				".ManagedService"),	anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(1);
		Filter filter = createMock(Filter.class);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filter);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		NetworkManagerApplication nmMock
				= createMock(NetworkManagerApplication.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(
				nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);
		
		XmlDbPolicyFinderWrapper finder = new XmlDbPolicyFinderWrapper();
		finder.activate(compCtxMock);
		EvaluationCtx evalContextMock = createMock(EvaluationCtx.class);
		finder.findPolicy(evalContextMock);
		verify(confMock);
		verify(confAdminMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFindPolicyURIInt() {
		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Yarr");
		ht.put("PEP.PID", "PEP:Yarr");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi" +
					"nder.xmldb")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		BundleContext bundleCtxMock = createNiceMock(BundleContext.class);
		ServiceReference sr = createMock(ServiceReference.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con" +
				"figurationAdmin")).andReturn(sr);
		expect(bundleCtxMock.getService(sr)).andReturn(confAdminMock);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.xmldb");
		ServiceRegistration reg = createMock(ServiceRegistration.class);	
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm" +
				".ManagedService"),	anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(1);
		Filter filter = createMock(Filter.class);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filter);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		NetworkManagerApplication nmMock
				= createMock(NetworkManagerApplication.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(
				nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);
		
		XmlDbPolicyFinderWrapper finder = new XmlDbPolicyFinderWrapper();
		finder.activate(compCtxMock);
		try {
			finder.findPolicy(new URI("uri:hello"), 1);
		} catch (URISyntaxException use) {
			use.printStackTrace();
		}
		verify(confMock);
		verify(confAdminMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetIdentifier() {
		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Yarr");
		ht.put("PEP.PID", "PEP:Yarr");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi" +
					"nder.xmldb")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		BundleContext bundleCtxMock = createNiceMock(BundleContext.class);
		ServiceReference sr = createMock(ServiceReference.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con" +
				"figurationAdmin")).andReturn(sr);
		expect(bundleCtxMock.getService(sr)).andReturn(confAdminMock);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.xmldb");
		ServiceRegistration reg = createMock(ServiceRegistration.class);	
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm" +
				".ManagedService"),	anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(1);
		Filter filter = createMock(Filter.class);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filter);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
				
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		NetworkManagerApplication nmMock
				= createMock(NetworkManagerApplication.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(
				nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);
		
		XmlDbPolicyFinderWrapper finder = new XmlDbPolicyFinderWrapper();
		finder.activate(compCtxMock);
		assertNull(finder.getIdentifier());
		verify(confMock);
		verify(confAdminMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInitPolicyFinder() {
		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Yarr");
		ht.put("PEP.PID", "PEP:Yarr");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi" +
					"nder.xmldb")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		BundleContext bundleCtxMock = createNiceMock(BundleContext.class);
		ServiceReference sr = createMock(ServiceReference.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con" +
				"figurationAdmin")).andReturn(sr);
		expect(bundleCtxMock.getService(sr)).andReturn(confAdminMock);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.xmldb");
		ServiceRegistration reg = createMock(ServiceRegistration.class);	
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm" +
				".ManagedService"),	anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(1);
		Filter filter = createMock(Filter.class);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filter);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		NetworkManagerApplication nmMock
				= createMock(NetworkManagerApplication.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(
				nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);
		
		XmlDbPolicyFinderWrapper finder = new XmlDbPolicyFinderWrapper();
		finder.activate(compCtxMock);
		finder.init(createMock(PolicyFinder.class));
		verify(confMock);
		verify(confAdminMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testActivate() {
		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Yarr");
		ht.put("PEP.PID", "PEP:Yarr");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi" +
					"nder.xmldb")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		BundleContext bundleCtxMock = createNiceMock(BundleContext.class);
		ServiceReference sr = createMock(ServiceReference.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con" +
				"figurationAdmin")).andReturn(sr);
		expect(bundleCtxMock.getService(sr)).andReturn(confAdminMock);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.xmldb");
		ServiceRegistration reg = createMock(ServiceRegistration.class);	
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm" +
				".ManagedService"),	anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(1);
		Filter filter = createMock(Filter.class);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filter);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		NetworkManagerApplication nmMock
				= createMock(NetworkManagerApplication.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(
				nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);
		
		XmlDbPolicyFinderWrapper finder = new XmlDbPolicyFinderWrapper();
		finder.activate(compCtxMock);
		verify(confMock);
		verify(confAdminMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDeactivate() {
		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Yarr");
		ht.put("PEP.PID", "PEP:Yarr");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi" +
					"nder.xmldb")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		BundleContext bundleCtxMock = createNiceMock(BundleContext.class);
		ServiceReference sr = createMock(ServiceReference.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con" +
				"figurationAdmin")).andReturn(sr);
		expect(bundleCtxMock.getService(sr)).andReturn(confAdminMock);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.xmldb");
		ServiceRegistration reg = createMock(ServiceRegistration.class);	
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm" +
				".ManagedService"),	anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(1);
		Filter filter = createMock(Filter.class);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filter);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}				
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		NetworkManagerApplication nmMock
				= createMock(NetworkManagerApplication.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(
				nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);
		
		XmlDbPolicyFinderWrapper finder = new XmlDbPolicyFinderWrapper();
		finder.activate(compCtxMock);
		finder.deactivate(compCtxMock);
		verify(confMock);
		verify(confAdminMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConfigurationBind() {
		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Yarr");
		ht.put("PEP.PID", "PEP:Yarr");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(3);
		try {
			confMock.update(isA(Dictionary.class));
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi" +
					"nder.xmldb")).andReturn(confMock);
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		BundleContext bundleCtxMock = createNiceMock(BundleContext.class);
		ServiceReference sr = createMock(ServiceReference.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con" +
				"figurationAdmin")).andReturn(sr);
		expect(bundleCtxMock.getService(sr)).andReturn(confAdminMock);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.xmldb");
		ServiceRegistration reg = createMock(ServiceRegistration.class);	
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm" +
				".ManagedService"),	anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(1);
		Filter filter = createMock(Filter.class);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filter);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
				
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		NetworkManagerApplication nmMock
				= createMock(NetworkManagerApplication.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(
				nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);
		
		XmlDbPolicyFinderWrapper finder = new XmlDbPolicyFinderWrapper();
		finder.activate(compCtxMock);
		finder.configurationBind(confAdminMock);
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
		ht.put("EM.PID", "EventManager:Yarr");
		ht.put("PEP.PID", "PEP:Yarr");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(3);
		try {
			confMock.update(isA(Dictionary.class));
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi" +
					"nder.xmldb")).andReturn(confMock);
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		BundleContext bundleCtxMock = createNiceMock(BundleContext.class);
		ServiceReference sr = createMock(ServiceReference.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con" +
				"figurationAdmin")).andReturn(sr);
		expect(bundleCtxMock.getService(sr)).andReturn(confAdminMock);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.xmldb");
		ServiceRegistration reg = createMock(ServiceRegistration.class);	
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm" +
				".ManagedService"),	anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(1);
		Filter filter = createMock(Filter.class);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filter);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		NetworkManagerApplication nmMock
				= createMock(NetworkManagerApplication.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(
				nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);
		
		XmlDbPolicyFinderWrapper finder = new XmlDbPolicyFinderWrapper();
		finder.activate(compCtxMock);
		finder.configurationBind(confAdminMock);
		finder.configurationUnbind(confAdminMock);
		verify(confMock);
		verify(confAdminMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRegister() {
		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Yarr");
		ht.put("PEP.PID", "PEP:Yarr");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi" +
					"nder.xmldb")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		BundleContext bundleCtxMock = createNiceMock(BundleContext.class);
		ServiceReference sr = createMock(ServiceReference.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con" +
				"figurationAdmin")).andReturn(sr);
		expect(bundleCtxMock.getService(sr)).andReturn(confAdminMock);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.xmldb");
		ServiceRegistration reg = createMock(ServiceRegistration.class);	
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm" +
				".ManagedService"),	anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(1);
		Filter filter = createMock(Filter.class);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filter);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
				
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		NetworkManagerApplication nmMock
				= createMock(NetworkManagerApplication.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(
				nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);
		
		XmlDbPolicyFinderWrapper finder = new XmlDbPolicyFinderWrapper();
		finder.activate(compCtxMock);
		finder.register(sr);
		verify(confMock);
		verify(confAdminMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRemove() {
		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Yarr");
		ht.put("PEP.PID", "PEP:Yarr");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi" +
					"nder.xmldb")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		BundleContext bundleCtxMock = createNiceMock(BundleContext.class);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(1);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(1);
		replay(refMock);
		
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con" +
				"figurationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.xmldb");
		ServiceRegistration reg = createMock(ServiceRegistration.class);	
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm" +
				".ManagedService"),	anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(1);
		Filter filter = createMock(Filter.class);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filter);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		NetworkManagerApplication nmMock
				= createMock(NetworkManagerApplication.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(
				nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);
		
		XmlDbPolicyFinderWrapper finder = new XmlDbPolicyFinderWrapper();
		finder.activate(compCtxMock);
		finder.register(refMock);
		finder.remove(refMock);
		verify(confMock);
		verify(confAdminMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

}
