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
package eu.linksmart.policy.pdp.impl;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.w3c.dom.Node;

import eu.linksmart.network.CryptoHIDResult;
import eu.linksmart.network.NetworkManagerApplication;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.Function;
import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.Subject;

import eu.linksmart.policy.pdp.PdpAdminError;
import eu.linksmart.policy.pdp.ext.function.impl.PdpFunctionScope;
import eu.linksmart.policy.pdp.ext.impl.PipModule;
import eu.linksmart.policy.pdp.impl.PdpApplication;
import eu.linksmart.policy.pdp.impl.PdpConfigurator;

/**
 * Unit test for {@link PdpApplication}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitPdpApplicationTest {
	
	/** file repository location */
	private static final String FILE_LOCATION = "PolicyFramework";
	
	/** test policy **/
	private static final String POLICY =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?><Policy PolicyId=\"Example" +
		"Policy\" RuleCombiningAlgId=\"urn:oasis:names:tc:xacml:1.0:rule-comb" +
		"ining-algorithm:permit-overrides\" xmlns=\"urn:oasis:names:tc:xacml:" +
		"1.0:policy\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + 
		" xsi:schemaLocation=\"urn:oasis:names:tc:xacml:1.0:policy cs-xacml-s" +
		"chema-policy-01.xsd\"><Target><Subjects><AnySubject/></Subjects><Res" +
		"ources><AnyResource/></Resources><Actions><AnyAction/></Actions></Ta" +
		"rget><Rule Effect=\"Permit\" RuleId=\"DefaultPermit\"/></Policy>";
	
	@Test
	public void testPdpApplication() {
		assertNotNull(getInstance());
	}
	
	@SuppressWarnings({ "unchecked", "boxing" })
	@Test
	public void testActivate() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(7);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(6);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp"))
					.andReturn(confMock);
			expectLastCall().times(7);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(4);
		expect(hidResultMock.getCertRef()).andReturn("456");
		expectLastCall().times(3);
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
			expectLastCall().times(2);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		PipModule pipModuleMock = createMock(PipModule.class);
		expect(pipModuleMock.getIdentifier()).andReturn("bo");
		expect(pipModuleMock.isDesignatorSupported()).andReturn(true);
		expect(pipModuleMock.isSelectorSupported()).andReturn(false);
		HashSet<Function> functions = new HashSet<Function>();
		expect(pipModuleMock.getFunctions(isA(PdpFunctionScope.class)))
				.andReturn(functions);
		expectLastCall().times(3);
		replay(pipModuleMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configur" +
				"ationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pipModuleMock);
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pdp");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					isA(String.class));
			expectLastCall().times(2);
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.policy.p" +
					"dp.ext.impl.PipModule)")).andReturn(filterMock);
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences(isA(String.class), 
					isNull(String.class))).andReturn(refs);
			expect(bundleCtxMock.registerService(isA(String.class), anyObject(), 
					isA(Dictionary.class))).andReturn(regMock);
			expectLastCall().times(2);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PdpApplication pdpApp = getInstance();
		pdpApp.activate(compCtxMock);
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}
	
	@SuppressWarnings({ "unchecked", "boxing" })
	@Test
	public void testDeactivate() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(7);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(6);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp"))
					.andReturn(confMock);
			expectLastCall().times(7);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(4);
		expect(hidResultMock.getCertRef()).andReturn("456");
		expectLastCall().times(3);
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
			expectLastCall().times(2);
			nmMock.removeHID("123");
			expectLastCall().times(2);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		PipModule pipModuleMock = createMock(PipModule.class);
		expect(pipModuleMock.getIdentifier()).andReturn("bo");
		expect(pipModuleMock.isDesignatorSupported()).andReturn(true);
		expect(pipModuleMock.isSelectorSupported()).andReturn(false);
		HashSet<Function> functions = new HashSet<Function>();
		expect(pipModuleMock.getFunctions(isA(PdpFunctionScope.class)))
				.andReturn(functions);
		expectLastCall().times(3);
		replay(pipModuleMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configur" +
				"ationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pipModuleMock);
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pdp");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					isA(String.class));
			expectLastCall().times(2);
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.policy.p" +
					"dp.ext.impl.PipModule)")).andReturn(filterMock);
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences(isA(String.class), 
					isNull(String.class))).andReturn(refs);
			expect(bundleCtxMock.registerService(isA(String.class), anyObject(), 
					isA(Dictionary.class))).andReturn(regMock);
			expectLastCall().times(2);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PdpApplication pdpApp = getInstance();
		pdpApp.activate(compCtxMock);
		pdpApp.deactivate(compCtxMock);
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}
	
	@SuppressWarnings({ "unchecked", "boxing" })
	@Test
	public void testActivatePolicy() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		Bundle[] bundles = new Bundle[0];
		expect(refMock.getUsingBundles()).andReturn(bundles);
		expectLastCall().times(5);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(7);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(6);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp"))
					.andReturn(confMock);
			expectLastCall().times(7);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(4);
		expect(hidResultMock.getCertRef()).andReturn("456");
		expectLastCall().times(3);
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
			expectLastCall().times(2);
			//expect(nmMock.getHIDByAttributes(isA(String.class), 
			//		isA(String.class), isA(String.class), eq(1000l), eq(1)))
			//				.andReturn(new String[0]);
			//expectLastCall().times(5);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		PipModule pipModuleMock = createMock(PipModule.class);
		expect(pipModuleMock.getIdentifier()).andReturn("bo");
		expect(pipModuleMock.isDesignatorSupported()).andReturn(true);
		expect(pipModuleMock.isSelectorSupported()).andReturn(false);
		HashSet<Function> functions = new HashSet<Function>();
		expect(pipModuleMock.getFunctions(isA(PdpFunctionScope.class)))
				.andReturn(functions);
		expectLastCall().times(3);
		replay(pipModuleMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu" +
				"rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pipModuleMock);
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pdp");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					isA(String.class));
			expectLastCall().times(2);
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pdp.ext.impl.PipModule)")).andReturn(filterMock);
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences(isA(String.class), 
					isNull(String.class))).andReturn(refs);
			expect(bundleCtxMock.registerService(isA(String.class), anyObject(), 
					isA(Dictionary.class))).andReturn(regMock);
			expectLastCall().times(2);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getServiceReference()).andReturn(refMock);
		expectLastCall().times(5);
		replay(compCtxMock);
		
		PdpApplication pdpApp = getInstance();
		pdpApp.activate(compCtxMock);
		try {
			pdpApp.removePolicy("MyLittlePolicy");
			pdpApp.publishPolicy("MyLittlePolicy", POLICY);
			pdpApp.activatePolicy("MyLittlePolicy");
			pdpApp.deactivatePolicy("MyLittlePolicy");
			pdpApp.removePolicy("MyLittlePolicy");
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}
	
	@SuppressWarnings({ "unchecked", "boxing" })
	@Test
	public void testGetActivePolicyList() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(7);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(6);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp"))
					.andReturn(confMock);
			expectLastCall().times(7);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(4);
		expect(hidResultMock.getCertRef()).andReturn("456");
		expectLastCall().times(3);
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
			expectLastCall().times(2);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		PipModule pipModuleMock = createMock(PipModule.class);
		expect(pipModuleMock.getIdentifier()).andReturn("bo");
		expect(pipModuleMock.isDesignatorSupported()).andReturn(true);
		expect(pipModuleMock.isSelectorSupported()).andReturn(false);
		HashSet<Function> functions = new HashSet<Function>();
		expect(pipModuleMock.getFunctions(isA(PdpFunctionScope.class)))
				.andReturn(functions);
		expectLastCall().times(3);
		replay(pipModuleMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu" +
				"rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pipModuleMock);
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pdp");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					isA(String.class));
			expectLastCall().times(2);
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pdp.ext.impl.PipModule)")).andReturn(filterMock);
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences(isA(String.class), 
					isNull(String.class))).andReturn(refs);
			expect(bundleCtxMock.registerService(isA(String.class), anyObject(), 
					isA(Dictionary.class))).andReturn(regMock);
			expectLastCall().times(2);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PdpApplication pdpApp = getInstance();
		pdpApp.activate(compCtxMock);
		try {
			assertNotNull(pdpApp.getActivePolicyList());
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}
	
	@SuppressWarnings({ "unchecked", "boxing" })
	@Test
	public void testGetInactivePolicyList() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(7);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(6);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp"))
					.andReturn(confMock);
			expectLastCall().times(7);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(4);
		expect(hidResultMock.getCertRef()).andReturn("456");
		expectLastCall().times(3);
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
			expectLastCall().times(2);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		PipModule pipModuleMock = createMock(PipModule.class);
		expect(pipModuleMock.getIdentifier()).andReturn("bo");
		expect(pipModuleMock.isDesignatorSupported()).andReturn(true);
		expect(pipModuleMock.isSelectorSupported()).andReturn(false);
		HashSet<Function> functions = new HashSet<Function>();
		expect(pipModuleMock.getFunctions(isA(PdpFunctionScope.class)))
				.andReturn(functions);
		expectLastCall().times(3);
		replay(pipModuleMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu" +
				"rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pipModuleMock);
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pdp");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					isA(String.class));
			expectLastCall().times(2);
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pdp.ext.impl.PipModule)")).andReturn(filterMock);
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences(isA(String.class), 
					isNull(String.class))).andReturn(refs);
			expect(bundleCtxMock.registerService(isA(String.class), anyObject(), 
					isA(Dictionary.class))).andReturn(regMock);
			expectLastCall().times(2);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PdpApplication pdpApp = getInstance();
		pdpApp.activate(compCtxMock);
		try {
			assertNotNull(pdpApp.getInActivePolicyList());
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}
	
	@SuppressWarnings({ "unchecked", "boxing" })
	@Test
	public void testConfigurationBind() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(8);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(7);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp"))
					.andReturn(confMock);
			expectLastCall().times(8);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(4);
		expect(hidResultMock.getCertRef()).andReturn("456");
		expectLastCall().times(3);
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
			expectLastCall().times(2);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		PipModule pipModuleMock = createMock(PipModule.class);
		expect(pipModuleMock.getIdentifier()).andReturn("bo");
		expect(pipModuleMock.isDesignatorSupported()).andReturn(true);
		expect(pipModuleMock.isSelectorSupported()).andReturn(false);
		HashSet<Function> functions = new HashSet<Function>();
		expect(pipModuleMock.getFunctions(isA(PdpFunctionScope.class)))
				.andReturn(functions);
		expectLastCall().times(3);
		replay(pipModuleMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu" +
				"rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pipModuleMock);
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pdp");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					isA(String.class));
			expectLastCall().times(2);
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pdp.ext.impl.PipModule)")).andReturn(filterMock);
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences(isA(String.class), 
					isNull(String.class))).andReturn(refs);
			expect(bundleCtxMock.registerService(isA(String.class), anyObject(), 
					isA(Dictionary.class))).andReturn(regMock);
			expectLastCall().times(3);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PdpApplication pdpApp = getInstance();
		pdpApp.activate(compCtxMock);
		pdpApp.configurationBind(confAdminMock);
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}
	
	@SuppressWarnings({ "unchecked", "boxing" })
	@Test
	public void testConfigurationUnbind() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		regMock.unregister();
		replay(regMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(8);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(7);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp"))
					.andReturn(confMock);
			expectLastCall().times(8);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(4);
		expect(hidResultMock.getCertRef()).andReturn("456");
		expectLastCall().times(3);
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
			expectLastCall().times(2);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		PipModule pipModuleMock = createMock(PipModule.class);
		expect(pipModuleMock.getIdentifier()).andReturn("bo");
		expect(pipModuleMock.isDesignatorSupported()).andReturn(true);
		expect(pipModuleMock.isSelectorSupported()).andReturn(false);
		HashSet<Function> functions = new HashSet<Function>();
		expect(pipModuleMock.getFunctions(isA(PdpFunctionScope.class)))
				.andReturn(functions);
		expectLastCall().times(3);
		replay(pipModuleMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu" +
				"rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pipModuleMock);
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pdp");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					isA(String.class));
			expectLastCall().times(2);
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pdp.ext.impl.PipModule)")).andReturn(filterMock);
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences(isA(String.class), 
					isNull(String.class))).andReturn(refs);
			expect(bundleCtxMock.registerService(isA(String.class), anyObject(), 
					isA(Dictionary.class))).andReturn(regMock);
			expectLastCall().times(3);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PdpApplication pdpApp = getInstance();
		pdpApp.activate(compCtxMock);
		pdpApp.configurationBind(confAdminMock);
		pdpApp.configurationUnbind(confAdminMock);
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}
	
	@SuppressWarnings({ "unchecked", "boxing" })
	@Test
	public void testEvaluate() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(7);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(6);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp"))
					.andReturn(confMock);
			expectLastCall().times(8);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(4);
		expect(hidResultMock.getCertRef()).andReturn("456");
		expectLastCall().times(3);
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
			expectLastCall().times(2);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		PipModule pipModuleMock = createMock(PipModule.class);
		expect(pipModuleMock.getIdentifier()).andReturn("bo");
		expect(pipModuleMock.isDesignatorSupported()).andReturn(true);
		expect(pipModuleMock.isSelectorSupported()).andReturn(false);
		HashSet<Function> functions = new HashSet<Function>();
		expect(pipModuleMock.getFunctions(isA(PdpFunctionScope.class)))
				.andReturn(functions);
		expectLastCall().times(3);
		replay(pipModuleMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu" +
				"rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pipModuleMock);
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pdp");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					isA(String.class));
			expectLastCall().times(2);
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pdp.ext.impl.PipModule)")).andReturn(filterMock);
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences(isA(String.class), 
					isNull(String.class))).andReturn(refs);
			expect(bundleCtxMock.registerService(isA(String.class), anyObject(), 
					isA(Dictionary.class))).andReturn(regMock);
			expectLastCall().times(2);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		HashSet subjects = new HashSet();
		subjects.add(new Subject(new HashSet()));
		HashSet resources = new HashSet();
		try {
			resources.add(new Attribute(new URI("urn:oasis:names:tc:xacml:1.0:res" +
					"ource:resource-id"), null, null, new StringAttribute("hio")));
		} catch (URISyntaxException use) {
			use.printStackTrace();
			fail("Exception");
		}
		RequestCtx request = new RequestCtx(subjects, resources, 
				new HashSet(), new HashSet());
		
		PdpApplication pdpApp = getInstance();
		pdpApp.activate(compCtxMock);
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			request.encode(baos);
            String reqXml = baos.toString();
			assertNotNull(pdpApp.evaluate(reqXml));
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}
	
	@SuppressWarnings({ "unchecked", "boxing" })
	@Test
	public void testEvaluateLocally() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(7);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(6);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp"))
					.andReturn(confMock);
			expectLastCall().times(8);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(4);
		expect(hidResultMock.getCertRef()).andReturn("456");
		expectLastCall().times(3);
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
			expectLastCall().times(2);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		PipModule pipModuleMock = createMock(PipModule.class);
		expect(pipModuleMock.getIdentifier()).andReturn("bo");
		expect(pipModuleMock.isDesignatorSupported()).andReturn(true);
		expect(pipModuleMock.isSelectorSupported()).andReturn(false);
		HashSet<Function> functions = new HashSet<Function>();
		expect(pipModuleMock.getFunctions(isA(PdpFunctionScope.class)))
				.andReturn(functions);
		expectLastCall().times(3);
		replay(pipModuleMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu" +
				"rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pipModuleMock);
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pdp");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					isA(String.class));
			expectLastCall().times(2);
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pdp.ext.impl.PipModule)")).andReturn(filterMock);
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences(isA(String.class), 
					isNull(String.class))).andReturn(refs);
			expect(bundleCtxMock.registerService(isA(String.class), anyObject(), 
					isA(Dictionary.class))).andReturn(regMock);
			expectLastCall().times(2);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);
		
		Node reqNodeMock = createMock(Node.class);
		replay(reqNodeMock);
		
		RequestCtx reqCtxMock = createMock(RequestCtx.class);
		HashSet subjects = new HashSet();
		subjects.add(new Subject(new HashSet()));
		HashSet resources = new HashSet();
		try {
			resources.add(new Attribute(new URI("urn:oasis:names:tc:xacml:1.0:res" +
					"ource:resource-id"), null, null, new StringAttribute("hio")));
		} catch (URISyntaxException use) {
			use.printStackTrace();
			fail("Exception");
		}
		expect(reqCtxMock.getDocumentRoot()).andReturn(reqNodeMock);
		expect(reqCtxMock.getSubjects()).andReturn(subjects);
		expect(reqCtxMock.getResource()).andReturn(resources);
		expect(reqCtxMock.getAction()).andReturn(new HashSet());
		expect(reqCtxMock.getEnvironmentAttributes()).andReturn(new HashSet());
		replay(reqCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PdpApplication pdpApp = getInstance();
		pdpApp.activate(compCtxMock);
		assertNotNull(pdpApp.evaluateLocally(reqCtxMock));
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
		verify(reqCtxMock);
		verify(reqNodeMock);
	}	
	
	@Test
	public void testGetPropertyWhenNotInitialized() {
		try {
			getInstance().getProperty("aProperty");
			fail("Did not throw exception as required");
		} catch (RemoteException e) {
			PdpAdminError err = PdpAdminError.valueOf(e.getMessage());
			if (err == null) {
				fail("Did not throw exception with appropriate error type");
			}
			return;
		}
	}
	
	@SuppressWarnings({ "unchecked", "boxing" })
	@Test
	public void testApplyConfiguration() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(9);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(8);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp"))
					.andReturn(confMock);
			expectLastCall().times(9);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(6);
		expect(hidResultMock.getCertRef()).andReturn("456");
		expectLastCall().times(4);
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
			expectLastCall().times(3);
			nmMock.removeHID("123");
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		PipModule pipModuleMock = createMock(PipModule.class);
		expect(pipModuleMock.getIdentifier()).andReturn("bo");
		expect(pipModuleMock.isDesignatorSupported()).andReturn(true);
		expect(pipModuleMock.isSelectorSupported()).andReturn(false);
		HashSet<Function> functions = new HashSet<Function>();
		expect(pipModuleMock.getFunctions(isA(PdpFunctionScope.class)))
				.andReturn(functions);
		expectLastCall().times(3);
		replay(pipModuleMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configur" +
				"ationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pipModuleMock);
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pdp");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					isA(String.class));
			expectLastCall().times(2);
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.policy.p" +
					"dp.ext.impl.PipModule)")).andReturn(filterMock);
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences(isA(String.class), 
					isNull(String.class))).andReturn(refs);
			expect(bundleCtxMock.registerService(isA(String.class), anyObject(), 
					isA(Dictionary.class))).andReturn(regMock);
			expectLastCall().times(2);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PdpApplication pdpApp = getInstance();
		pdpApp.activate(compCtxMock);
		// empty configuration
		pdpApp.applyConfigurations(new Hashtable());
		// some configuration options
		Hashtable updates = new Hashtable();
		updates.put(PdpConfigurator.PDP_PID, "mother");
		pdpApp.applyConfigurations(updates);
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "boxing" })
	public void testGetPolicy() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
			
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		Bundle[] bundles = new Bundle[0];
		expect(refMock.getUsingBundles()).andReturn(bundles);
		expectLastCall().times(5);
		replay(refMock);
			
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(7);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(6);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp"))
					.andReturn(confMock);
			expectLastCall().times(7);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			}
		replay(confAdminMock);
			
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(4);
		expect(hidResultMock.getCertRef()).andReturn("456");
		expectLastCall().times(3);
		replay(hidResultMock);
			
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
			expectLastCall().times(2);
			//expect(nmMock.getHIDByAttributes(isA(String.class), 
			//		isA(String.class), isA(String.class), eq(1000l), eq(1)))
			//				.andReturn(new String[0]);
			//expectLastCall().times(5);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
			
		PipModule pipModuleMock = createMock(PipModule.class);
		expect(pipModuleMock.getIdentifier()).andReturn("bo");
		expect(pipModuleMock.isDesignatorSupported()).andReturn(true);
		expect(pipModuleMock.isSelectorSupported()).andReturn(false);
		HashSet<Function> functions = new HashSet<Function>();
		expect(pipModuleMock.getFunctions(isA(PdpFunctionScope.class)))
				.andReturn(functions);
		expectLastCall().times(3);
		replay(pipModuleMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu" +
				"rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pipModuleMock);
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pdp");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					isA(String.class));
			expectLastCall().times(2);
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pdp.ext.impl.PipModule)")).andReturn(filterMock);
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences(isA(String.class), 
					isNull(String.class))).andReturn(refs);
			expect(bundleCtxMock.registerService(isA(String.class), anyObject(), 
					isA(Dictionary.class))).andReturn(regMock);
			expectLastCall().times(2);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);
			
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getServiceReference()).andReturn(refMock);
		expectLastCall().times(5);
		replay(compCtxMock);
			
		PdpApplication pdpApp = getInstance();
		pdpApp.activate(compCtxMock);
		try {
			pdpApp.removePolicy("MyLittlePolicy");
			pdpApp.publishPolicy("MyLittlePolicy", POLICY);
			pdpApp.activatePolicy("MyLittlePolicy");
			assertNotNull(pdpApp.getPolicy("MyLittlePolicy"));
			pdpApp.deactivatePolicy("MyLittlePolicy");
			pdpApp.removePolicy("MyLittlePolicy");
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}
	
	@SuppressWarnings({ "unchecked", "boxing" })
	@Test
	public void testSetProperty() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		Bundle[] bundles = new Bundle[0];
		expect(refMock.getUsingBundles()).andReturn(bundles);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(7);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(6);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp"))
					.andReturn(confMock);
			expectLastCall().times(7);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(4);
		expect(hidResultMock.getCertRef()).andReturn("456");
		expectLastCall().times(3);
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
			expectLastCall().times(2);
			//expect(nmMock.getHIDByAttributes(isA(String.class), 
			//		isA(String.class), isA(String.class), eq(1000l), eq(1)))
			//				.andReturn(new String[0]);
			//expectLastCall().times(1);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		PipModule pipModuleMock = createMock(PipModule.class);
		expect(pipModuleMock.getIdentifier()).andReturn("bo");
		expect(pipModuleMock.isDesignatorSupported()).andReturn(true);
		expect(pipModuleMock.isSelectorSupported()).andReturn(false);
		HashSet<Function> functions = new HashSet<Function>();
		expect(pipModuleMock.getFunctions(isA(PdpFunctionScope.class)))
				.andReturn(functions);
		expectLastCall().times(3);
		replay(pipModuleMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configur" +
				"ationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pipModuleMock);
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pdp");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					isA(String.class));
			expectLastCall().times(2);
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.policy.p" +
					"dp.ext.impl.PipModule)")).andReturn(filterMock);
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences(isA(String.class), 
					isNull(String.class))).andReturn(refs);
			expect(bundleCtxMock.registerService(isA(String.class), anyObject(), 
					isA(Dictionary.class))).andReturn(regMock);
			expectLastCall().times(2);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
			fail("Exception");
		}
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expectLastCall().times(3);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		expect(compCtxMock.getServiceReference()).andReturn(refMock);
		replay(compCtxMock);
		
		PdpApplication pdpApp = getInstance();
		pdpApp.activate(compCtxMock);
		try {
			pdpApp.setProperty("ping", "pong");
			fail("Exception expected");
		} catch (RemoteException re) {			
			// exception expected here			
		}
		try {
			pdpApp.getProperty("ping");
			fail("Exception expected");
		} catch (RemoteException re) {			
			// exception expected here			
		}
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
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
	
	/**
	 * @return
	 * 			a {@link PdpApplication} instance
	 */
	private PdpApplication getInstance() {
		return new PdpApplication();
	}

}
