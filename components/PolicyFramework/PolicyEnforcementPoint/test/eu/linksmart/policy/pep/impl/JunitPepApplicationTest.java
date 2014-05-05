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
package eu.linksmart.policy.pep.impl;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

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

import eu.linksmart.network.CryptoHIDResult;
import eu.linksmart.network.NetworkManagerApplication;

import eu.linksmart.policy.pdp.PolicyDecisionPoint;
import eu.linksmart.policy.pep.cache.impl.PdpSessionCache;
import eu.linksmart.policy.pep.impl.PepApplication;
import eu.linksmart.policy.pep.impl.PepConfigurator;
import eu.linksmart.policy.pep.request.SoapAttrParser;
import eu.linksmart.policy.pep.response.PepObligationObserver;

/**
 * Unit test for {@link PepApplication}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitPepApplicationTest {
	
	/** test method request */
	private static final String METHOD_REQUEST 
			= "Content-Type: text/xml; charset=utf-8" +
				"Accept: application/soap+xml, application/dime, " +
				"multipart/related, text/*" +
				"User-Agent: Axis/1.4" +
				"Host: localhost:8082" +
				"Cache-Control: no-cache" +
				"Pragma: no-cache" +
				"SOAPAction: \"\"" +
				"Content-Length: 520" +
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/" +
				"soap/envelope/\"" +
				" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
				" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
				"<soapenv:Body><ns1:hello soapenv:encodingStyle=\"http://" +
				"schemas.xmlsoap.org/soap/encoding/\"" +
				" xmlns:ns1=\"TestLinkSmartApplicationImpl\">" +
				"<ns1:arg0 xsi:type=\"soapenc:string\"" +
				" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
				"Is it me you're looking for?</ns1:arg0>" +
				"<ns1:arg1 xsi:type=\"soapenc:string\"" +
				" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
				"Is it me you're looking for!</ns1:arg1>" +
				"</ns1:hello></soapenv:Body></soapenv:Envelope>";

	@SuppressWarnings("unchecked")
	@Test
	public void testGetNM() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(2);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(2);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(4);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pep"))
					.andReturn(confMock);
			expectLastCall().times(4);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(2);
		expect(hidResultMock.getCertRef()).andReturn("456");
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configur" +
				"ationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(createMock(
				PepObligationObserver.class));
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pep");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pep.response.PepObligationObserver)"))
							.andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), eq("" +
					"(objectclass=eu.linksmart.policy.pep.response.PepObligat" +
					"ionObserver)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pe" +
					"p.response.PepObligationObserver", null)).andReturn(refs);
			
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}		
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PepApplication pepApp = new PepApplication();
		pepApp.activate(compCtxMock);
		assertNotNull(pepApp.getNM());
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
		verify(hidResultMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRequestAccessDecision() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(2);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(2);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(5);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(4);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pep"))
					.andReturn(confMock);
			expectLastCall().times(5);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(2);
		expect(hidResultMock.getCertRef()).andReturn("456");
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		PolicyDecisionPoint pdpMock = createMock(PolicyDecisionPoint.class);
		try {
			expect(pdpMock.evaluate(isA(String.class))).andReturn("");
			expectLastCall().times(2);
		} catch (RemoteException re) {
			re.printStackTrace();
		}
		replay(pdpMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configur" +
				"ationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(createMock(
				PepObligationObserver.class));
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pep");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pep.response.PepObligationObserver)"))
							.andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), eq("" +
					"(objectclass=eu.linksmart.policy.pep.response.PepObligat" +
					"ionObserver)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pe" +
					"p.response.PepObligationObserver", null)).andReturn(refs);
			ServiceReference pdpRefMock = createMock(ServiceReference.class);
			ServiceReference[] pdpRefs = new ServiceReference[1];
			pdpRefs[0] = pdpRefMock;
			replay(pdpRefMock);
			expect(bundleCtxMock.getServiceReferences(eq("eu.linksmart.policy.pd" +
					"p.PolicyDecisionPoint"),  isA(String.class)))
					.andReturn(pdpRefs);
			expect(bundleCtxMock.getService(pdpRefMock)).andReturn(pdpMock);
			
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}		
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PepApplication pepApp = new PepApplication();
		pepApp.activate(compCtxMock);
		assertNotNull(pepApp.requestAccessDecision("", "", "", "", 
				METHOD_REQUEST, "1"));
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRequestCachedAccessDecision() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(2);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(2);
		replay(refMock);

		Hashtable pepConf = new Hashtable();
		pepConf.put(PepConfigurator.PEP_USE_PDP_CACHING, "true");
		pepConf.put(PepConfigurator.PEP_USE_LOCAL_SESSIONS, "true");
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(pepConf);
		expectLastCall().times(5);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(4);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pep"))
					.andReturn(confMock);
			expectLastCall().times(5);
		} catch (IOException ioe) { 
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(2);
		expect(hidResultMock.getCertRef()).andReturn("456");
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		PolicyDecisionPoint pdpMock = createMock(PolicyDecisionPoint.class);
		try {
			// this must be called exactly twice
			expect(pdpMock.evaluate(isA(String.class))).andReturn("");
			expectLastCall().times(2);
		} catch (RemoteException re) {
			re.printStackTrace();
		}
		replay(pdpMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configur" +
				"ationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(createMock(
				PepObligationObserver.class));
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pep");
		configuration.put(PepConfigurator.PEP_USE_PDP_CACHING, "true");
		configuration.put(PepConfigurator.PEP_USE_LOCAL_SESSIONS, "true");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), isA(Hashtable.class))).andReturn(regMock);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pep.response.PepObligationObserver)"))
							.andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), eq("" +
					"(objectclass=eu.linksmart.policy.pep.response.PepObligat" +
					"ionObserver)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pe" +
					"p.response.PepObligationObserver", null)).andReturn(refs);
			ServiceReference pdpRefMock = createMock(ServiceReference.class);
			ServiceReference[] pdpRefs = new ServiceReference[1];
			pdpRefs[0] = pdpRefMock;
			replay(pdpRefMock);
			expect(bundleCtxMock.getServiceReferences(eq("eu.linksmart.policy" +
					".pdp.PolicyDecisionPoint"), isA(String.class)))
					.andReturn(pdpRefs);
			expectLastCall().times(2);
			expect(bundleCtxMock.getService(pdpRefMock)).andReturn(pdpMock);
			expectLastCall().times(2);
			
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}		
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PepApplication pepApp = new PepApplication();
		pepApp.activate(compCtxMock);
		assertNotNull(pepApp.requestAccessDecision("123", "", "456", "", 
				METHOD_REQUEST, "1"));
		// should return the cached request now
		assertNotNull(pepApp.requestAccessDecision("123", "", "456", "", 
				METHOD_REQUEST, "1"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRequestAccessDecisionWMethod() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(2);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(2);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(5);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(4);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pep"))
					.andReturn(confMock);
			expectLastCall().times(5);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(2);
		expect(hidResultMock.getCertRef()).andReturn("456");
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		PolicyDecisionPoint pdpMock = createMock(PolicyDecisionPoint.class);
		try {
			expect(pdpMock.evaluate(isA(String.class))).andReturn("");
		} catch (RemoteException re) {
			re.printStackTrace();
		}
		replay(pdpMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu" +
				"rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(createMock(
				PepObligationObserver.class));
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pep");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pep.response.PepObligationObserver)"))
							.andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), eq("" +
					"(objectclass=eu.linksmart.policy.pep.response.PepObligat" +
					"ionObserver)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pe" +
					"p.response.PepObligationObserver", null)).andReturn(refs);
			ServiceReference pdpRefMock = createMock(ServiceReference.class);
			ServiceReference[] pdpRefs = new ServiceReference[1];
			pdpRefs[0] = pdpRefMock;
			replay(pdpRefMock);
			expect(bundleCtxMock.getServiceReferences(eq("eu.linksmart.policy.pd" +
					"p.PolicyDecisionPoint"), isA(String.class)))
					.andReturn(pdpRefs);
			expect(bundleCtxMock.getService(pdpRefMock)).andReturn(pdpMock);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}		
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PepApplication pepApp = new PepApplication();
		pepApp.activate(compCtxMock);
		assertNotNull(pepApp.requestAccessDecisionWMethod("", "", "", "", 
				"can'ttouchthis", "1"));
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFlushCache() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(2);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(2);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(4);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pep"))
					.andReturn(confMock);
			expectLastCall().times(5);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(2);
		expect(hidResultMock.getCertRef()).andReturn("456");
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu" +
				"rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(createMock(
				PepObligationObserver.class));
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pep");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pep.response.PepObligationObserver)"))
							.andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), eq("" +
					"(objectclass=eu.linksmart.policy.pep.response.PepObligat" +
					"ionObserver)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pe" +
					"p.response.PepObligationObserver", null)).andReturn(refs);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}		
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PepApplication pepApp = new PepApplication();
		pepApp.activate(compCtxMock);
		pepApp.flushCache();
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testApplyConfigurations() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(4);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(4);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(4);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pep"))
					.andReturn(confMock);
			expectLastCall().times(5);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(2);
		expect(hidResultMock.getCertRef()).andReturn("456");
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configur" +
				"ationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(createMock(
				PepObligationObserver.class));
		expect(bundleCtxMock.getService(refMock)).andReturn(createMock(
				PepObligationObserver.class));
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pep");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pep.response.PepObligationObserver)"))
							.andReturn(filterMock);
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pep.response.PepObligationObserver)"))
					.andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
			expectLastCall().times(2);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), eq("" +
					"(objectclass=eu.linksmart.policy.pep.response.PepObligat" +
					"ionObserver)"));
			expectLastCall().times(2);
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pe" +
					"p.response.PepObligationObserver", null)).andReturn(refs);
			expectLastCall().times(2);
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}		
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PepApplication pepApp = new PepApplication();
		pepApp.activate(compCtxMock);
		pepApp.applyConfigurations(new Hashtable());
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetConfigurator() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(2);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(2);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(4);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pep"))
					.andReturn(confMock);
			expectLastCall().times(5);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(2);
		expect(hidResultMock.getCertRef()).andReturn("456");
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configur" +
				"ationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(createMock(
				PepObligationObserver.class));
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pep");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pep.response.PepObligationObserver)"))
							.andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), eq("" +
					"(objectclass=eu.linksmart.policy.pep.response.PepObligat" +
					"ionObserver)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pe" +
					"p.response.PepObligationObserver", null)).andReturn(refs);
			
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}		
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PepApplication pepApp = new PepApplication();
		pepApp.activate(compCtxMock);
		assertNotNull(pepApp.getConfigurator());
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetAttributeParser() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(2);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(2);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(4);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pep"))
					.andReturn(confMock);
			expectLastCall().times(5);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(2);
		expect(hidResultMock.getCertRef()).andReturn("456");
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configur" +
				"ationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(createMock(
				PepObligationObserver.class));
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pep");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pep.response.PepObligationObserver)"))
							.andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), eq("" +
					"(objectclass=eu.linksmart.policy.pep.response.PepObligat" +
					"ionObserver)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pe" +
					"p.response.PepObligationObserver", null)).andReturn(refs);
			
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}		
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PepApplication pepApp = new PepApplication();
		pepApp.activate(compCtxMock);		
		assertNotNull(pepApp.getAttributeParser());
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSetAttrParser() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(2);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(2);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(4);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pep"))
					.andReturn(confMock);
			expectLastCall().times(5);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(2);
		expect(hidResultMock.getCertRef()).andReturn("456");
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configur" +
				"ationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(createMock(
				PepObligationObserver.class));
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pep");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pep.response.PepObligationObserver)"))
							.andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), eq("" +
					"(objectclass=eu.linksmart.policy.pep.response.PepObligat" +
					"ionObserver)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pe" +
					"p.response.PepObligationObserver", null)).andReturn(refs);
			
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}		
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PepApplication pepApp = new PepApplication();
		pepApp.activate(compCtxMock);
		pepApp.setAttrParser(createMock(SoapAttrParser.class));
		assertNotNull(pepApp.getAttributeParser());
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetSessionCache() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(2);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(2);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(4);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pep"))
					.andReturn(confMock);
			expectLastCall().times(5);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(2);
		expect(hidResultMock.getCertRef()).andReturn("456");
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configur" +
				"ationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(createMock(
				PepObligationObserver.class));
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pep");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pep.response.PepObligationObserver)"))
							.andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), eq("" +
					"(objectclass=eu.linksmart.policy.pep.response.PepObligat" +
					"ionObserver)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pe" +
					"p.response.PepObligationObserver", null)).andReturn(refs);
			
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}		
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PepApplication pepApp = new PepApplication();
		pepApp.activate(compCtxMock);		
		pepApp.setSessionCache(new PdpSessionCache());
		assertNotNull(pepApp.getSessionCache());
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSetSessionCache() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(2);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(2);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(4);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pep"))
					.andReturn(confMock);
			expectLastCall().times(5);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(2);
		expect(hidResultMock.getCertRef()).andReturn("456");
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configur" +
				"ationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(createMock(
				PepObligationObserver.class));
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pep");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pep.response.PepObligationObserver)"))
							.andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), eq("" +
					"(objectclass=eu.linksmart.policy.pep.response.PepObligat" +
					"ionObserver)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pe" +
					"p.response.PepObligationObserver", null)).andReturn(refs);
			
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}		
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PepApplication pepApp = new PepApplication();
		pepApp.activate(compCtxMock);
		pepApp.setSessionCache(new PdpSessionCache());
		assertNotNull(pepApp.getSessionCache());
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testIsUseSessionCache() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(2);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(2);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(4);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pep"))
					.andReturn(confMock);
			expectLastCall().times(5);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(2);
		expect(hidResultMock.getCertRef()).andReturn("456");
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configur" +
				"ationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(createMock(
				PepObligationObserver.class));
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pep");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pep.response.PepObligationObserver)"))
							.andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), eq("" +
					"(objectclass=eu.linksmart.policy.pep.response.PepObligat" +
					"ionObserver)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pe" +
					"p.response.PepObligationObserver", null)).andReturn(refs);
			
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}		
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PepApplication pepApp = new PepApplication();
		pepApp.activate(compCtxMock);
		assertFalse(pepApp.isUseSessionCache());
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSetUseSessionCache() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(2);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(2);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(4);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pep"))
					.andReturn(confMock);
			expectLastCall().times(5);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(2);
		expect(hidResultMock.getCertRef()).andReturn("456");
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configur" +
				"ationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(createMock(
				PepObligationObserver.class));
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pep");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pep.response.PepObligationObserver)"))
							.andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), eq("" +
					"(objectclass=eu.linksmart.policy.pep.response.PepObligat" +
					"ionObserver)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pe" +
					"p.response.PepObligationObserver", null)).andReturn(refs);
			
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}		
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PepApplication pepApp = new PepApplication();
		pepApp.activate(compCtxMock);
		pepApp.setUseSessionCache(true);
		pepApp.setUseSessionCache(false);
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetObligationObservers() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(2);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(2);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(4);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pep"))
					.andReturn(confMock);
			expectLastCall().times(5);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(2);
		expect(hidResultMock.getCertRef()).andReturn("456");
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configur" +
				"ationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(createMock(
				PepObligationObserver.class));
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pep");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pep.response.PepObligationObserver)"))
							.andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), eq("" +
					"(objectclass=eu.linksmart.policy.pep.response.PepObligat" +
					"ionObserver)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pe" +
					"p.response.PepObligationObserver", null)).andReturn(refs);
			
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}		
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PepApplication pepApp = new PepApplication();
		pepApp.activate(compCtxMock);
		assertNotNull(pepApp.getObligationObservers());
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSetObligationObservers() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(2);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(2);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(4);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pep"))
					.andReturn(confMock);
			expectLastCall().times(5);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(2);
		expect(hidResultMock.getCertRef()).andReturn("456");
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configur" +
				"ationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(createMock(
				PepObligationObserver.class));
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pep");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pep.response.PepObligationObserver)"))
							.andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), eq("" +
					"(objectclass=eu.linksmart.policy.pep.response.PepObligat" +
					"ionObserver)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pe" +
					"p.response.PepObligationObserver", null)).andReturn(refs);
			
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}		
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PepApplication pepApp = new PepApplication();
		pepApp.activate(compCtxMock);
		ArrayList<PepObligationObserver> observers
				= new ArrayList<PepObligationObserver>();
		pepApp.setObligationObservers(observers);
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testActivate() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(2);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(2);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(4);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pep"))
					.andReturn(confMock);
			expectLastCall().times(5);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(2);
		expect(hidResultMock.getCertRef()).andReturn("456");
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configur" +
				"ationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(createMock(
				PepObligationObserver.class));
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pep");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pep.response.PepObligationObserver)"))
							.andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), eq("" +
					"(objectclass=eu.linksmart.policy.pep.response.PepObligat" +
					"ionObserver)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pe" +
					"p.response.PepObligationObserver", null)).andReturn(refs);
			
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}		
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PepApplication pepApp = new PepApplication();
		pepApp.activate(compCtxMock);
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDeactivate() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(2);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(2);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(4);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(3);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pep"))
					.andReturn(confMock);
			expectLastCall().times(5);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(2);
		expect(hidResultMock.getCertRef()).andReturn("456");
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
			nmMock.removeHID("123");
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configur" +
				"ationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(createMock(
				PepObligationObserver.class));
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pep");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pep.response.PepObligationObserver)"))
							.andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), eq("" +
					"(objectclass=eu.linksmart.policy.pep.response.PepObligat" +
					"ionObserver)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pe" +
					"p.response.PepObligationObserver", null)).andReturn(refs);
			
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}		
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PepApplication pepApp = new PepApplication();
		pepApp.activate(compCtxMock);
		pepApp.deactivate(compCtxMock);
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConfigurationBind() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(2);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(2);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(5);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(4);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pep"))
					.andReturn(confMock);
			expectLastCall().times(5);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(2);
		expect(hidResultMock.getCertRef()).andReturn("456");
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configur" +
				"ationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(createMock(
				PepObligationObserver.class));
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pep");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		expectLastCall().times(2);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pep.response.PepObligationObserver)"))
							.andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), eq("" +
					"(objectclass=eu.linksmart.policy.pep.response.PepObligat" +
					"ionObserver)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pe" +
					"p.response.PepObligationObserver", null)).andReturn(refs);
			
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}		
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PepApplication pepApp = new PepApplication();
		pepApp.activate(compCtxMock);
		pepApp.configurationBind(confAdminMock);
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConfigurationUnbind() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		regMock.unregister();
		replay(regMock);
		
		Bundle bndlMock = createMock(Bundle.class);
		expect(bndlMock.getSymbolicName()).andReturn("test");
		expectLastCall().times(2);
		replay(bndlMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		expect(refMock.getBundle()).andReturn(bndlMock);
		expectLastCall().times(2);
		replay(refMock);
		
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		expectLastCall().times(5);
		try {
			confMock.update(isA(Dictionary.class));
			expectLastCall().times(4);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pep"))
					.andReturn(confMock);
			expectLastCall().times(5);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidResultMock = createMock(CryptoHIDResult.class);
		expect(hidResultMock.getHID()).andReturn("123");
		expectLastCall().times(2);
		expect(hidResultMock.getCertRef()).andReturn("456");
		replay(hidResultMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), 
					isA(String.class))).andReturn(hidResultMock);
		} catch (RemoteException re) {			
			re.printStackTrace();
			fail("Exception");
		}
		replay(nmMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configur" +
				"ationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(createMock(
				PepObligationObserver.class));
		Hashtable configuration = new Hashtable();
		configuration.put("service.pid", "eu.linksmart.policy.pep");
		expect(bundleCtxMock.registerService(eq("org.osgi.service.cm.ManagedS" +
				"ervice"), anyObject(), eq(configuration))).andReturn(regMock);
		expectLastCall().times(2);
		try {
			expect(bundleCtxMock.createFilter("(objectClass=eu.linksmart.poli" +
					"cy.pep.response.PepObligationObserver)"))
							.andReturn(filterMock);
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), 
					eq("(objectClass=eu.linksmart.policy.pep.response.PepObli" +
							"gationObserver)"));
			bundleCtxMock.addServiceListener(isA(ServiceListener.class), eq("" +
					"(objectclass=eu.linksmart.policy.pep.response.PepObligat" +
					"ionObserver)"));
			ServiceReference[] refs = new ServiceReference[1];
			refs[0] = refMock;
			expect(bundleCtxMock.getServiceReferences("eu.linksmart.policy.pe" +
					"p.response.PepObligationObserver", null)).andReturn(refs);
			
		} catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}		
		replay(bundleCtxMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.getBundleContext()).andReturn(bundleCtxMock);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PepApplication pepApp = new PepApplication();
		pepApp.activate(compCtxMock);
		pepApp.configurationBind(confAdminMock);
		pepApp.configurationUnbind(confAdminMock);
		verify(filterMock);
		verify(regMock);
		verify(refMock);
		verify(confMock);
		verify(nmMock);
		verify(bundleCtxMock);
		verify(compCtxMock);
	}

}
