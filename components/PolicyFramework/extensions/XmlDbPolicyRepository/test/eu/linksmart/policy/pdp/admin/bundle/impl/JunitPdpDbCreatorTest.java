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
import java.net.MalformedURLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import eu.linksmart.policy.pdp.admin.bundle.impl.PdpDbCreator;

/**
 * Unit test for {@link PdpDbCreator}
 * 
 * Test is largely empty, because we do not want to create files locally
 * 
 * @author Marco Tiemann
 *
 */
public class JunitPdpDbCreatorTest {
	
	/** DB location **/
	private static final String DB_LOCATION = "db";
	
	@Test
	public void testDoesDbExist() {
		assertFalse(PdpDbCreator.doesDbExist(null));
		assertFalse(PdpDbCreator.doesDbExist(DB_LOCATION));
	}

	@Test
	public void testCreatePolicyDb() {
		Bundle bundleMock = createMock(Bundle.class);
		try {
			expect(bundleMock.getResource(isA(String.class))).andReturn(
					new File("resources/conf.xml").toURI().toURL());
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
			fail("Exception");
		}
		replay(bundleMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getBundle()).andReturn(bundleMock);
		replay(bundleCtxMock);
		
		PdpDbCreator.createPolicyDb(DB_LOCATION, bundleCtxMock);
		verify(bundleMock);
		verify(bundleCtxMock);
	}

	@Test
	public void testGetFileAsString() {
		Bundle bundleMock = createMock(Bundle.class);
		try {
			expect(bundleMock.getResource(isA(String.class))).andReturn(
					new File("resources/conf.xml").toURI().toURL());
			expectLastCall().times(2);
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
			fail("Exception");
		}
		replay(bundleMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getBundle()).andReturn(bundleMock);
		expectLastCall().times(2);
		replay(bundleCtxMock);
		
		PdpDbCreator.createPolicyDb(DB_LOCATION, bundleCtxMock);
		assertNotNull(PdpDbCreator.getFileAsString(DB_LOCATION +File.separator 
				+ "conf.xml", bundleCtxMock));
		verify(bundleMock);
		verify(bundleCtxMock);
	}
	
	@Before
	public void setupTempFolder() {
		new File(DB_LOCATION).mkdir();
	}
	
	@After
	public void teardownTempFolder() {
		cleanup(new File(DB_LOCATION));
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
