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
package eu.linksmart.policy.pep.response.bundle.impl.debug;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.NetworkManagerApplication;
import com.sun.xacml.Obligation;
import com.sun.xacml.attr.DateTimeAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.ResponseCtx;

import eu.linksmart.policy.pep.impl.PepXacmlConstants;
import eu.linksmart.policy.pep.request.impl.PepRequest;
import eu.linksmart.policy.pep.response.bundle.impl.debug.DebugPepObligationObserverWrapper;

/**
 * Unit test for {@link DebugPepObligationObserverWrapper}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitDebugPepObligationObserverWrapperTest {

	@Test
	public void testActivate() {
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(
				createMock(NetworkManagerApplication.class));
		replay(compCtxMock);
		
		/* "normal" activate */
		DebugPepObligationObserverWrapper observer
				= new DebugPepObligationObserverWrapper();
		observer.activate(compCtxMock);
		verify(compCtxMock);
	}

	@Test
	public void testDeactivate() {
		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(
				createMock(NetworkManagerApplication.class));
		replay(compCtxMock);
		
		/* "normal" activate, then deactivate */
		try {
			DebugPepObligationObserverWrapper observer
					= new DebugPepObligationObserverWrapper();
			observer.activate(compCtxMock);
			observer.deactivate(compCtxMock);
		} catch (NullPointerException npe) {
			// intentionally left blank
			fail("Exception");
		}
		verify(compCtxMock);
	}

	@Test
	public void testDebugPepObligationObserverWrapper() {
		assertNotNull(new DebugPepObligationObserverWrapper());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEvaluate() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);
		
		BundleContext bnCtxMock = createMock(BundleContext.class);
		Dictionary d  = new Hashtable();
		d.put(Constants.SERVICE_PID, "eu.linksmart.policy.pep.obl.debug");
		replay(bnCtxMock);	
		
		ComponentContext cmpCtxMock = createMock(ComponentContext.class);
		expect(cmpCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(cmpCtxMock);
		
		ResponseCtx resCtxMock = createMock(ResponseCtx.class);
		replay(resCtxMock);

		DebugPepObligationObserverWrapper observer 
				= new DebugPepObligationObserverWrapper();
		ArrayList assignments = new ArrayList();
		Attribute attr = null;
		Obligation obli = null;
		try {
			/* applicable obligation */
			attr = new Attribute(new URI(
					PepXacmlConstants.OBLIGATION_DEBUG_POLICY_NAME.getUrn()), 
					"me", new DateTimeAttribute(), 
					new StringAttribute("hello"));
			assignments.add(attr);
			obli = new Obligation(new URI(
					PepXacmlConstants.OBLIGATION_DEBUG.getUrn()), 1, 
					assignments);
			observer.activate(cmpCtxMock);
			assertTrue(observer.evaluate(obli, new PepRequest(null, null, null, 
					null, null,	null, null), resCtxMock));
		} catch (URISyntaxException use) {
			use.printStackTrace();
			fail("Exception");
		} catch (NullPointerException npe) {
			fail("Exception");
		}
		assignments.clear();
		try {
			/* non-applicable obligation */
			attr = new Attribute(new URI(
					PepXacmlConstants.OBLIGATION_SEND_MESSAGE_FROM.getUrn()), 
					"me", new DateTimeAttribute(), 
					new StringAttribute("hello"));
			assignments.add(attr);
			obli = new Obligation(new URI(
					PepXacmlConstants.OBLIGATION_SEND_MESSAGE.getUrn()), 1, 
					assignments);
			observer.activate(cmpCtxMock);
			assertFalse(observer.evaluate(obli, new PepRequest(null, null, null, 
					null, null,	null, null), resCtxMock));	
		} catch (URISyntaxException use) {
			use.printStackTrace();
			fail("Exception");
		}	
		verify(nmMock);
		verify(bnCtxMock);
		verify(cmpCtxMock);
		verify(resCtxMock);
	}

}
