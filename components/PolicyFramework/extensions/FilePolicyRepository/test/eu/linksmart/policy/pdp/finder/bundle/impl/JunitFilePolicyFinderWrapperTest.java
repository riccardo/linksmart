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
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.NetworkManagerApplication;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.finder.PolicyFinder;

import eu.linksmart.policy.pdp.PdpAdmin;
import eu.linksmart.policy.pdp.finder.bundle.impl.FilePolicyFinderConfigurator;
import eu.linksmart.policy.pdp.finder.bundle.impl.FilePolicyFinderWrapper;

/**
 * Unit test for {@link FilePolicyFinderWrapper}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitFilePolicyFinderWrapperTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testIsRequestSupported() {
		NetworkManagerApplication nmMock
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);
		
		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Book");
		ht.put("PEP.PID", "PEP:Book");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi" +
					"nder.file")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		PdpAdmin pdpAdminMock = createMock(PdpAdmin.class);
		replay(pdpAdminMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con" +
				"figurationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pdpAdminMock);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.file");
		ServiceRegistration reg = createMock(ServiceRegistration.class);	
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm" +
				".ManagedService"),	anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(1);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), eq(
					"(objectClass=eu.linksmart.policy.pdp.PdpAdmin)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pd" +
					"p.PdpAdmin", null)).andReturn(refs);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					isA(String.class));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(
				nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);
		
		FilePolicyFinderWrapper finder = new FilePolicyFinderWrapper();
		finder.activate(compCtxMock);
		assertFalse(finder.isRequestSupported());
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(filterMock);
		verify(pdpAdminMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testIsIdReferenceSupported() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);

		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Book");
		ht.put("PEP.PID", "PEP:Book");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi"
				+ "nder.file")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		Filter filterMock = createMock(Filter.class);
		replay(filterMock);

		PdpAdmin pdpAdminMock = createMock(PdpAdmin.class);
		replay(pdpAdminMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con"
				+ "figurationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pdpAdminMock);
		Dictionary d = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.file");
		ServiceRegistration reg = createMock(ServiceRegistration.class);
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm"
				+ ".ManagedService"), anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(1);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class),
					eq("(objectClass=eu.linksmart.policy.pdp.PdpAdmin)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pd"
					+ "p.PdpAdmin", null)).andReturn(refs);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class),
					isA(String.class));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);
		
		FilePolicyFinderWrapper finder = new FilePolicyFinderWrapper();
		finder.activate(compCtxMock);
		assertFalse(finder.isIdReferenceSupported());
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(filterMock);
		verify(pdpAdminMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@Test
	public void testInvalidateCache() {
		FilePolicyFinderWrapper finder = new FilePolicyFinderWrapper();
		finder.invalidateCache();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testApplyConfigurations() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);

		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Book");
		ht.put("PEP.PID", "PEP:Book");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi"
					+ "nder.file")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		Filter filterMock = createMock(Filter.class);
		replay(filterMock);

		PdpAdmin pdpAdminMock = createMock(PdpAdmin.class);
		replay(pdpAdminMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con"
				+ "figurationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pdpAdminMock);
		Dictionary d = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.file");
		ServiceRegistration reg = createMock(ServiceRegistration.class);
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm"
				+ ".ManagedService"), anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(1);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class),
					eq("(objectClass=eu.linksmart.policy.pdp.PdpAdmin)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pd"
					+ "p.PdpAdmin", null)).andReturn(refs);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class),
					isA(String.class));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);

		FilePolicyFinderWrapper finder = new FilePolicyFinderWrapper();
		finder.activate(compCtxMock);
		Hashtable updates = new Hashtable();
		updates.put(FilePolicyFinderConfigurator.SID, "lala");
		finder.applyConfigurations(updates);
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(filterMock);
		verify(pdpAdminMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFindPolicyEvaluationCtx() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);

		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Book");
		ht.put("PEP.PID", "PEP:Book");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi"
					+ "nder.file")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		Filter filterMock = createMock(Filter.class);
		replay(filterMock);

		PdpAdmin pdpAdminMock = createMock(PdpAdmin.class);
		replay(pdpAdminMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con"
				+ "figurationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pdpAdminMock);
		Dictionary d = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.file");
		ServiceRegistration reg = createMock(ServiceRegistration.class);
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm"
				+ ".ManagedService"), anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(1);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class),
					eq("(objectClass=eu.linksmart.policy.pdp.PdpAdmin)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pd"
					+ "p.PdpAdmin", null)).andReturn(refs);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class),
					isA(String.class));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);

		FilePolicyFinderWrapper finder = new FilePolicyFinderWrapper();
		finder.activate(compCtxMock);
		EvaluationCtx evalContextMock = createMock(EvaluationCtx.class);
		finder.findPolicy(evalContextMock);
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(filterMock);
		verify(pdpAdminMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFindPolicyURIInt() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);

		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Book");
		ht.put("PEP.PID", "PEP:Book");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi"
					+ "nder.file")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		Filter filterMock = createMock(Filter.class);
		replay(filterMock);

		PdpAdmin pdpAdminMock = createMock(PdpAdmin.class);
		replay(pdpAdminMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con"
				+ "figurationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pdpAdminMock);
		Dictionary d = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.file");
		ServiceRegistration reg = createMock(ServiceRegistration.class);
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm"
				+ ".ManagedService"), anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(1);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class),
					eq("(objectClass=eu.linksmart.policy.pdp.PdpAdmin)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pd"
					+ "p.PdpAdmin", null)).andReturn(refs);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class),
					isA(String.class));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);

		FilePolicyFinderWrapper finder = new FilePolicyFinderWrapper();
		finder.activate(compCtxMock);
		try {
			finder.findPolicy(new URI("uri:hello"), 1);
		} catch (URISyntaxException use) {
			use.printStackTrace();
		}
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(filterMock);
		verify(pdpAdminMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetIdentifier() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);

		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Book");
		ht.put("PEP.PID", "PEP:Book");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi"
					+ "nder.file")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		Filter filterMock = createMock(Filter.class);
		replay(filterMock);

		PdpAdmin pdpAdminMock = createMock(PdpAdmin.class);
		replay(pdpAdminMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con"
				+ "figurationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pdpAdminMock);
		Dictionary d = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.file");
		ServiceRegistration reg = createMock(ServiceRegistration.class);
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm"
				+ ".ManagedService"), anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(1);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class),
					eq("(objectClass=eu.linksmart.policy.pdp.PdpAdmin)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pd"
					+ "p.PdpAdmin", null)).andReturn(refs);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class),
					isA(String.class));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);

		FilePolicyFinderWrapper finder = new FilePolicyFinderWrapper();
		finder.activate(compCtxMock);
		assertNull(finder.getIdentifier());
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(filterMock);
		verify(pdpAdminMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInitPolicyFinder() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);

		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Book");
		ht.put("PEP.PID", "PEP:Book");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi"
					+ "nder.file")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		Filter filterMock = createMock(Filter.class);
		replay(filterMock);

		PdpAdmin pdpAdminMock = createMock(PdpAdmin.class);
		replay(pdpAdminMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(
				bundleCtxMock.getServiceReference("org.osgi.service.cm.Con"
						+ "figurationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pdpAdminMock);
		Dictionary d = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.file");
		ServiceRegistration reg = createMock(ServiceRegistration.class);
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm"
				+ ".ManagedService"), anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(1);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class),
					eq("(objectClass=eu.linksmart.policy.pdp.PdpAdmin)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pdp."
					+ "PdpAdmin", null)).andReturn(refs);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class),
					isA(String.class));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);

		FilePolicyFinderWrapper finder = new FilePolicyFinderWrapper();
		finder.activate(compCtxMock);
		finder.init(createMock(PolicyFinder.class));
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(filterMock);
		verify(pdpAdminMock);
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
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Book");
		ht.put("PEP.PID", "PEP:Book");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi"
					+ "nder.file")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		Filter filterMock = createMock(Filter.class);
		replay(filterMock);

		PdpAdmin pdpAdminMock = createMock(PdpAdmin.class);
		replay(pdpAdminMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con"
				+ "figurationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pdpAdminMock);
		Dictionary d = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.file");
		ServiceRegistration reg = createMock(ServiceRegistration.class);
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm"
				+ ".ManagedService"), anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(1);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class),
					eq("(objectClass=eu.linksmart.policy.pdp.PdpAdmin)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pd"
					+ "p.PdpAdmin", null)).andReturn(refs);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class),
					isA(String.class));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);

		FilePolicyFinderWrapper finder = new FilePolicyFinderWrapper();
		finder.activate(compCtxMock);
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(filterMock);
		verify(pdpAdminMock);
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
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Book");
		ht.put("PEP.PID", "PEP:Book");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi"
					+ "nder.file")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		Filter filterMock = createMock(Filter.class);
		replay(filterMock);

		PdpAdmin pdpAdminMock = createMock(PdpAdmin.class);
		replay(pdpAdminMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con"
				+ "figurationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pdpAdminMock);
		Dictionary d = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.file");
		ServiceRegistration reg = createMock(ServiceRegistration.class);
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm"
				+ ".ManagedService"), anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(1);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class),
					eq("(objectClass=eu.linksmart.policy.pdp.PdpAdmin)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pd"
					+ "p.PdpAdmin", null)).andReturn(refs);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class),
					isA(String.class));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		expect(bundleCtxMock.ungetService(isA(ServiceReference.class)))
				.andReturn(true);
		bundleCtxMock.removeServiceListener(isA(ServiceListener.class));
		replay(bundleCtxMock);

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);

		FilePolicyFinderWrapper finder = new FilePolicyFinderWrapper();
		finder.activate(compCtxMock);
		finder.deactivate(compCtxMock);
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(filterMock);
		verify(pdpAdminMock);
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
			confMock.update(isA(Dictionary.class));
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi"
				+ "nder.file")).andReturn(confMock);
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		Filter filterMock = createMock(Filter.class);
		replay(filterMock);

		PdpAdmin pdpAdminMock = createMock(PdpAdmin.class);
		replay(pdpAdminMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con"
				+ "figurationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pdpAdminMock);
		Dictionary d = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.file");
		ServiceRegistration reg = createMock(ServiceRegistration.class);
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm"
				+ ".ManagedService"), anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(2);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class),
					eq("(objectClass=eu.linksmart.policy.pdp.PdpAdmin)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pd"
					+ "p.PdpAdmin", null)).andReturn(refs);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class),
					isA(String.class));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);

		FilePolicyFinderWrapper finder = new FilePolicyFinderWrapper();
		finder.activate(compCtxMock);
		finder.configurationBind(confAdminMock);
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(filterMock);
		verify(pdpAdminMock);
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
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi"
					+ "nder.file")).andReturn(confMock);
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		Filter filterMock = createMock(Filter.class);
		replay(filterMock);

		PdpAdmin pdpAdminMock = createMock(PdpAdmin.class);
		replay(pdpAdminMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con"
				+ "figurationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pdpAdminMock);
		Dictionary d = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.file");
		ServiceRegistration reg = createMock(ServiceRegistration.class);
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm"
				+ ".ManagedService"), anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(2);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class),
					eq("(objectClass=eu.linksmart.policy.pdp.PdpAdmin)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pd"
					+ "p.PdpAdmin", null)).andReturn(refs);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class),
					isA(String.class));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);

		FilePolicyFinderWrapper finder = new FilePolicyFinderWrapper();
		finder.activate(compCtxMock);
		finder.configurationBind(confAdminMock);
		finder.configurationUnbind(confAdminMock);
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(filterMock);
		verify(pdpAdminMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRegister() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);

		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Yarr");
		ht.put("PEP.PID", "PEP:Yarr");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi"
					+ "nder.file")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);

		Filter filterMock = createMock(Filter.class);
		replay(filterMock);

		PdpAdmin pdpAdminMock = createMock(PdpAdmin.class);
		replay(pdpAdminMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con"
				+ "figurationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pdpAdminMock);
		Dictionary d = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.file");
		ServiceRegistration reg = createMock(ServiceRegistration.class);
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm"
				+ ".ManagedService"), anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(1);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class),
					eq("(objectClass=eu.linksmart.policy.pdp.PdpAdmin)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pd"
					+ "p.PdpAdmin", null)).andReturn(refs);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class),
					isA(String.class));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);

		FilePolicyFinderWrapper finder = new FilePolicyFinderWrapper();
		finder.activate(compCtxMock);
		finder.register(refMock);
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(filterMock);
		verify(pdpAdminMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRemove() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);

		Configuration confMock = createMock(Configuration.class);
		Hashtable ht = new Hashtable();
		ht.put("EM.PID", "EventManager:Yarr");
		ht.put("PEP.PID", "PEP:Yarr");
		expect(confMock.getProperties()).andReturn(ht);
		expectLastCall().times(2);
		replay(confMock);

		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi"
					+ "nder.file")).andReturn(confMock);
			expectLastCall().times(2);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);

		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		replay(refMock);

		Filter filterMock = createMock(Filter.class);
		replay(filterMock);

		PdpAdmin pdpAdminMock = createMock(PdpAdmin.class);
		replay(pdpAdminMock);

		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Con"
				+ "figurationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pdpAdminMock);
		Dictionary d = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pdp.finder.file");
		ServiceRegistration reg = createMock(ServiceRegistration.class);
		expect(bundleCtxMock.registerService(matches("org.osgi.service.cm"
				+ ".ManagedService"), anyObject(), eq(d))).andReturn(reg);
		expectLastCall().times(1);
		try {
			String chk = "(objectClass=eu.linksmart.policy.pdp.PdpAdmin)";
			expect(bundleCtxMock.createFilter(chk)).andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class),
					eq("(objectClass=eu.linksmart.policy.pdp.PdpAdmin)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pd"
					+ "p.PdpAdmin", null)).andReturn(refs);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class),
					isA(String.class));
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		replay(compCtxMock);

		FilePolicyFinderWrapper finder = new FilePolicyFinderWrapper();
		finder.activate(compCtxMock);
		finder.register(refMock);
		finder.remove(refMock);
		verify(nmMock);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(filterMock);
		verify(pdpAdminMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

}
