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
import java.util.Hashtable;

import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import eu.linksmart.policy.pdp.admin.bundle.impl.FilePolicyAdminWrapper;
import eu.linksmart.policy.pdp.finder.bundle.impl.FilePolicyFinderConfigurator;
import eu.linksmart.policy.pdp.finder.bundle.impl.FilePolicyFinderWrapper;

/**
 * Unit test for {@link FilePolicyFinderConfigurator}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitFilePolicyFinderConfiguratorTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testApplyConfigurations() {
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi" +
					"nder.file")).andReturn(confMock);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu" +
				"rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		replay(bundleCtxMock);
		
		FilePolicyAdminWrapper adminMock
				= createMock(FilePolicyAdminWrapper.class);
		replay(adminMock);
		
		FilePolicyFinderWrapper admin
				= new FilePolicyFinderWrapper();
		FilePolicyFinderConfigurator configurator
				= new FilePolicyFinderConfigurator(bundleCtxMock, admin);
		assertNotNull(configurator);
		// empty configuration
		configurator.applyConfigurations(new Hashtable());
		// some configuration options
		Hashtable configuration = new Hashtable();
		configuration.put(FilePolicyFinderConfigurator.SID, 
				"ImALittleFinderShortAndStout");
		configurator.applyConfigurations(configuration);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(bundleCtxMock);
		verify(adminMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFilePolicyFinderConfigurator() {
		Configuration confMock = createMock(Configuration.class);
		expect(confMock.getProperties()).andReturn(new Hashtable());
		replay(confMock);
		
		ConfigurationAdmin confAdminMock = createMock(ConfigurationAdmin.class);
		try {
			expect(confAdminMock.getConfiguration("eu.linksmart.policy.pdp.fi" +
					"nder.file")).andReturn(confMock);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		replay(confAdminMock);
		
		ServiceReference refMock = createMock(ServiceReference.class);
		replay(refMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu" +
				"rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		replay(bundleCtxMock);
		
		FilePolicyAdminWrapper adminMock
				= createMock(FilePolicyAdminWrapper.class);
		replay(adminMock);
		
		FilePolicyFinderWrapper admin
				= new FilePolicyFinderWrapper();
		FilePolicyFinderConfigurator configurator
				= new FilePolicyFinderConfigurator(bundleCtxMock, admin);
		assertNotNull(configurator);
		verify(confMock);
		verify(confAdminMock);
		verify(refMock);
		verify(bundleCtxMock);
		verify(adminMock);
	}

}
