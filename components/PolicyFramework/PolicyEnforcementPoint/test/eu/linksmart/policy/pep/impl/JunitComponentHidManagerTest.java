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
import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import eu.linksmart.network.CryptoHIDResult;
import eu.linksmart.network.NetworkManagerApplication;

import eu.linksmart.policy.pep.impl.ComponentHidManager;
import eu.linksmart.policy.pep.impl.PepApplication;
import eu.linksmart.policy.pep.impl.PepConfigurator;

/**
 * Unit test for {@link ComponentHidManager}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitComponentHidManagerTest {

	@Test
	public void testComponentHidManager() {
		PepApplication pepAppMock = createMock(PepApplication.class);
		replay(pepAppMock);
		
		ComponentHidManager hidMan = new ComponentHidManager(pepAppMock);
		assertNotNull(hidMan);
		verify(pepAppMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRegisterService() {
		ServiceReference refMock = createMock(ServiceReference.class);
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
			fail("Exception");
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidMock = createMock(CryptoHIDResult.class);
		expect(hidMock.getHID()).andReturn("789");
		expectLastCall().times(2);
		expect(hidMock.getCertRef()).andReturn("0");
		replay(hidMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), isA(String.class)))
					.andReturn(hidMock);
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}		
		replay(nmMock);

		BundleContext ctxMock = createMock(BundleContext.class);
		expect(ctxMock.getServiceReference("org.osgi.service.cm.Configuration" +
				"Admin")).andReturn(refMock);
		expect(ctxMock.getService(refMock)).andReturn(confAdminMock);
		replay(ctxMock);
		
		PepApplication pepAppMock = createMock(PepApplication.class);
		PepConfigurator configurator = new PepConfigurator(ctxMock, pepAppMock);
		expect(pepAppMock.getConfigurator()).andReturn(configurator);
		expectLastCall().times(8);
		expect(pepAppMock.getNM()).andReturn(nmMock);
		replay(pepAppMock);
		
		ComponentHidManager hidMan = new ComponentHidManager(pepAppMock);
		try {
			hidMan.registerService("123", "456");
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		verify(refMock);
		verify(confMock);
		verify(confAdminMock);
		verify(hidMock);
		verify(nmMock);
		verify(ctxMock);
		verify(pepAppMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRenewService() {
		ServiceReference refMock = createMock(ServiceReference.class);
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
			fail("Exception");
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidMock = createMock(CryptoHIDResult.class);
		expect(hidMock.getHID()).andReturn("789");
		expectLastCall().times(2);
		expect(hidMock.getCertRef()).andReturn("0");
		replay(hidMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), isA(String.class)))
					.andReturn(hidMock);
			nmMock.removeHID("789");
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}		
		replay(nmMock);

		BundleContext ctxMock = createMock(BundleContext.class);
		expect(ctxMock.getServiceReference("org.osgi.service.cm.Configuration" +
				"Admin")).andReturn(refMock);
		expect(ctxMock.getService(refMock)).andReturn(confAdminMock);
		replay(ctxMock);
		
		PepApplication pepAppMock = createMock(PepApplication.class);
		PepConfigurator configurator = new PepConfigurator(ctxMock, pepAppMock);
		expect(pepAppMock.getConfigurator()).andReturn(configurator);
		expectLastCall().times(4);
		expect(pepAppMock.getNM()).andReturn(nmMock);
		expectLastCall().times(2);
		replay(pepAppMock);
		
		ComponentHidManager hidMan = new ComponentHidManager(pepAppMock);
		try {
			hidMan.renewService("123", "456", true, "789");
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		verify(refMock);
		verify(confMock);
		verify(confAdminMock);
		verify(hidMock);
		verify(nmMock);
		verify(ctxMock);
		verify(pepAppMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUnregisterService() {
		ServiceReference refMock = createMock(ServiceReference.class);
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
			fail("Exception");
		}
		replay(confAdminMock);
		
		CryptoHIDResult hidMock = createMock(CryptoHIDResult.class);
		expect(hidMock.getHID()).andReturn("789");
		expectLastCall().times(2);
		expect(hidMock.getCertRef()).andReturn("0");
		replay(hidMock);
		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		try {
			expect(nmMock.createCryptoHID(isA(String.class), isA(String.class)))
					.andReturn(hidMock);
			nmMock.removeHID("789");
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		}		
		replay(nmMock);

		BundleContext ctxMock = createMock(BundleContext.class);
		expect(ctxMock.getServiceReference("org.osgi.service.cm.Configuration" +
				"Admin")).andReturn(refMock);
		expect(ctxMock.getService(refMock)).andReturn(confAdminMock);
		replay(ctxMock);
		
		PepApplication pepAppMock = createMock(PepApplication.class);
		PepConfigurator configurator = new PepConfigurator(ctxMock, pepAppMock);
		expect(pepAppMock.getConfigurator()).andReturn(configurator);
		expectLastCall().times(8);
		expect(pepAppMock.getNM()).andReturn(nmMock);
		expectLastCall().times(2);
		replay(pepAppMock);
		
		ComponentHidManager hidMan = new ComponentHidManager(pepAppMock);
		try {
			hidMan.registerService("123", "456");
		} catch (RemoteException re) {
			re.printStackTrace();
			fail("Exception");
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		hidMan.unregisterService("789");
		verify(refMock);
		verify(confMock);
		verify(confAdminMock);
		verify(hidMock);
		verify(nmMock);
		verify(ctxMock);
		verify(pepAppMock);
	}

}
