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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.isNull;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.easymock.EasyMock;
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

import eu.linksmart.network.CryptoHIDResult;
import eu.linksmart.network.NetworkManagerApplication;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.ParsingException;
import com.sun.xacml.attr.BagAttribute;
import com.sun.xacml.cond.EvaluationResult;
import com.sun.xacml.cond.Function;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;

import eu.linksmart.policy.pdp.ext.function.impl.PdpFunctionScope;
import eu.linksmart.policy.pdp.ext.impl.PipModule;
import eu.linksmart.policy.pdp.impl.PdpApplication;

/**
 * Policy conformance unit test for {@link PdpApplication}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitPdpApplicationConformanceTest {

	/** file repository location */
	private static final String FILE_LOCATION = "PolicyFramework";	
	
	/** conformance test data zip file */
	private static final String TESTS_ZIP_FILE 
			= "resources" + File.separator + "tests.zip";
	
	/**
	 * Test method for {@link eu.linksmart.policy.pdp.impl.PdpApplication
	 * 		#evaluateLocally(com.sun.xacml.ctx.RequestCtx)}.
	 */
	@SuppressWarnings({ "boxing", "unchecked" })
	@Test
	public void testEvaluateLocally() {
		Filter filterMock = createMock(Filter.class);
		replay(filterMock);
		
		ServiceRegistration regMock = createMock(ServiceRegistration.class);
		replay(regMock);
		
		Bundle bundleMock = createMock(Bundle.class);
		ServiceReference refMock = createMock(ServiceReference.class);
		ServiceReference[] refs = new ServiceReference[1];
		refs[0] = refMock;
		expect(bundleMock.getRegisteredServices()).andReturn(refs);
		expectLastCall().times(10000);
		replay(bundleMock);
		
		Bundle[] bundles = new Bundle[1];
		bundles[0] = bundleMock;
		expect(refMock.getUsingBundles()).andReturn(bundles);
		expectLastCall().times(10000);
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
		
		BagAttribute aValMock = createMock(BagAttribute.class);
		expect(aValMock.isEmpty()).andReturn(true);
		expectLastCall().times(50);
		replay(aValMock);
		
		EvaluationResult evalResultMock = createMock(EvaluationResult.class);
		expect(evalResultMock.indeterminate()).andReturn(false);
		expectLastCall().times(50);
		expect(evalResultMock.getAttributeValue()).andReturn(aValMock);
		expectLastCall().times(50);
		replay(evalResultMock);
		
		PipModule pipModuleMock = createMock(PipModule.class);
		expect(pipModuleMock.getIdentifier()).andReturn("bo");
		expect(pipModuleMock.isDesignatorSupported()).andReturn(true);
		expect(pipModuleMock.isSelectorSupported()).andReturn(false);
		HashSet<Function> functions = new HashSet<Function>();
		expect(pipModuleMock.getFunctions(isA(PdpFunctionScope.class)))
				.andReturn(functions);
		expectLastCall().times(3);
		expect(pipModuleMock.findAttribute(isA(URI.class), isA(URI.class), 
				isA(URI.class), isA(URI.class), isA(EvaluationCtx.class), 
				EasyMock.anyInt())).andReturn(evalResultMock);
		expectLastCall().times(50);
		expect(pipModuleMock.findAttribute(isA(URI.class), isA(URI.class), 
				EasyMock.isNull(URI.class), isA(URI.class), 
				isA(EvaluationCtx.class), EasyMock.anyInt()))
				.andReturn(evalResultMock);
		expectLastCall().times(50);
		expect(pipModuleMock.findAttribute(isA(URI.class), isA(URI.class), 
				isA(URI.class), EasyMock.isNull(URI.class), 
				isA(EvaluationCtx.class), EasyMock.anyInt()))
				.andReturn(evalResultMock);
		expectLastCall().times(50);
		expect(pipModuleMock.findAttribute(isA(URI.class), isA(URI.class), 
				EasyMock.isNull(URI.class), EasyMock.isNull(URI.class), 
				isA(EvaluationCtx.class), EasyMock.anyInt()))
				.andReturn(evalResultMock);
		expectLastCall().times(50);
		replay(pipModuleMock);
		
		BundleContext bundleCtxMock = createMock(BundleContext.class);
		expect(bundleCtxMock.getServiceReference("org.osgi.service.cm.Configu" +
				"rationAdmin")).andReturn(refMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(pipModuleMock);
		expect(bundleCtxMock.getService(refMock)).andReturn(confAdminMock);
		expectLastCall().times(10000);
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
			ServiceReference[] refs2 = new ServiceReference[1];
			refs2[0] = refMock;
			expect(bundleCtxMock.getServiceReferences(isA(String.class), 
					isNull(String.class))).andReturn(refs2);
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
		expectLastCall().times(10000);
		replay(compCtxMock);
		
		PdpApplication pdpApp = new PdpApplication();
		pdpApp.activate(compCtxMock);
		Set<TestData> testData = loadTestData();
		int i = 0;
		for (TestData td : testData) {
			String polId = td.policy.substring(td.policy.indexOf("PolicyId"), 
					td.policy.indexOf("RuleCombiningAlgId"));
			System.out.println("Test: " + polId);
			int c = 0;
			boolean b = false;
			while ((!b) && (c < 3)) { 
				try {
					c++;
					b = pdpApp.publishPolicy("testPolicy" + i, td.policy);
					b = pdpApp.activatePolicy("testPolicy" + i);	
				} catch (RemoteException re) {
					re.printStackTrace();
					try {
						pdpApp.removePolicy("testPolicy" + i);
					} catch (RemoteException rre) {
						rre.printStackTrace();
					}
				}
			}
			String dec = "";
			String exp = "";
			try {
				RequestCtx request = RequestCtx.getInstance(
						new ByteArrayInputStream(td.request.getBytes()));
				ResponseCtx response = pdpApp.evaluateLocally(request);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				response.encode(baos);
				dec = baos.toString();
				dec = dec.substring(dec.indexOf("<Decision>") + 10, 
						dec.indexOf("</Decision>"));
				exp = td.response.substring(
						td.response.indexOf("<Decision>") + 10, 
						td.response.indexOf("</Decision>"));
				System.out.println("dec: " + dec + ", exp: " + exp);

			} catch (ParsingException pe) {
				pe.printStackTrace();
			}
			try {
				pdpApp.removePolicy("testPolicy" + i);
			} catch (RemoteException re) {
				re.printStackTrace();
			}
			// we do not check responses for the following policies
			if (
					/* NotApplicable instead of Indeterminate (different 
					   interpretation of spec) */
					(!polId.contains("IIA1"))
					// Requires lookup of a role (i.e. a PIP)
					&& (!polId.contains("IIA002")) 		
					// error when validating policy is desired test outcome
					&& (!polId.contains("IIC003"))	
					// error when validating policy is desired test outcome
					&& (!polId.contains("IIC012")) 			
					// error when validating policy is desired test outcome
					&& (!polId.contains("IIC014")) 		
					// fails because of white spaces
					&& (!polId.contains("IIC101"))		
					// fails because of white spaces
					&& (!polId.contains("IIC164"))
					/* regex exception: "Dangling meta character '*' near index 
					   2 .**This  is.* IT!.*", bug in Sun XACML MatchFunction */
					&& (!polId.contains("IIC165"))
					/* regex exception: "Dangling meta character '*' near index 
					   2 .**This  is.* IT!.*", bug in Sun XACML MatchFunction */
					&& (!polId.contains("IIC166"))
					// fails because of white spaces
					&& (!polId.contains("IIC171"))	
					// fails because of white spaces
					&& (!polId.contains("IIC172")) 		
					// fails because of white spaces
					&& (!polId.contains("IIC173")) 		
					// fails because of white spaces
					&& (!polId.contains("IIC174"))		
					// fails because of white spaces
					&& (!polId.contains("IIC175")) 		
					/* policy combination is tested but not required by 
					   specification */
					&& (!polId.contains("IID029")) 			
					/* cannot work in this test, because referenced policies are 
					   not stored */
					&& (!polId.contains("IIE003")) 		
					// optional
					&& (!polId.contains("IIIG001")) 	
					// optional
					&& (!polId.contains("IIIG002"))			
					// optional
					&& (!polId.contains("IIIG004")) 	
					// optional
					&& (!polId.contains("IIIG006")) 	
					// optional
					&& (!polId.contains("IIIF001")) 
					// optional
					&& (!polId.contains("IIIC002")) 		
					// optional
					&& (!polId.contains("IIIC003"))			
					// optional
					&& (!polId.contains("IIIF003")) 		
					// optional
					&& (!polId.contains("IIIF007"))			
					// optional
					&& (!polId.contains("IIIF006"))) {		
				assertTrue(dec.equalsIgnoreCase(exp));
			}
			i++;				
		}
	}
	
	@Before
	public void setupTempFolder() {
		new File(FILE_LOCATION).mkdir();
	}
	
	@After
	public void teardownTempFolder() {
		cleanup(new File(FILE_LOCATION));
	}
	
	/**
	 * Loads conformance test data from a file
	 * 
	 * @return
	 * 				the loaded {@link TestData} instances
	 */
	private Set<TestData> loadTestData() {
		HashMap<String, TestData> testData = new HashMap<String, TestData>();
		try {
			FileInputStream inFile = new FileInputStream(TESTS_ZIP_FILE);
			ZipInputStream inZip = new ZipInputStream(inFile);
			ZipEntry ntr;
			byte[] buffer = new byte[1024];
			while((ntr = inZip.getNextEntry()) != null) {
				String fileName = ntr.getName();
				if (fileName.startsWith("II")) {
					int n = 0;
					ByteArrayOutputStream outBytes
							= new ByteArrayOutputStream();
					while ((n = inZip.read(buffer, 0, 1024)) > -1) {
						outBytes.write(buffer, 0, n);
					}						
					if (fileName.contains("Policy")) {
						String testId = fileName.substring(0, 
								fileName.indexOf("Policy"));
						getTestDataReference(testData, testId).policy 
								= outBytes.toString();
					} else if (fileName.contains("Request")) {
						String testId = fileName.substring(0, 
								fileName.indexOf("Request"));
						getTestDataReference(testData, testId).request 
								= outBytes.toString();
					} else if (fileName.contains("Response")) {
						String testId = fileName.substring(0, 
								fileName.indexOf("Response"));
						getTestDataReference(testData, testId).response 
								= outBytes.toString();
					}
				}
				inZip.closeEntry();
			}
			inZip.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			fail("Exception");
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Exception");
		}
		return new HashSet<TestData>(testData.values());
	}
	
	/**
	 * @param theTestData
	 * 				the test data <code>Map</code>
	 * @param theTestId
	 * 				the test ID to get a reference for
	 * @return
	 * 				the reference for the argument <code>theTestId</code>
	 */
	private TestData getTestDataReference(HashMap<String, TestData> theTestData, 
			String theTestId) {
		if (!theTestData.containsKey(theTestId)) {
			theTestData.put(theTestId, new TestData());
		}
		return theTestData.get(theTestId);
	}
	
	/** Conformance test data value class */
	final class TestData {
		
		/** policy <code>String</code> */
		public String policy;
		
		/** request <code>String</code> */
		public String request;
		
		/** response <code>String</code> */
		public String response;
		
	}
	
	/**
	 * Recursively deletes files and subdirectories in the argument directory as 
	 * well as that directory itself
	 * 
	 * @param theDirectory
	 * 				the directory to delete
	 * @return
	 * 				a success indicator flag
	 */
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
