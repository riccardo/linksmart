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
package eu.linksmart.policy.pep.response.impl;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.NetworkManagerApplication;
import com.sun.xacml.Obligation;
import com.sun.xacml.ctx.ResponseCtx;

import eu.linksmart.policy.pep.request.impl.PepRequest;
import eu.linksmart.policy.pep.response.PepObligationObserver;
import eu.linksmart.policy.pep.response.impl.BundlePepObligationObserverWrapper;

/**
 * Unit test for {@link BundlePepObligationObserverWrapper}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitBundlePepObligationObserverWrapperTest {

	@Test
	public void testBundlePepObligationObserverWrapper() {
		PepObligationObserver observerMock 
				= createMock(PepObligationObserver.class);
		replay(observerMock);
		
		assertNotNull(new BundlePepObligationObserverWrapper(observerMock));
		verify(observerMock);
	}

	@SuppressWarnings("boxing")
	@Test
	public void testEvaluate() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);
		
		ComponentContext compCtxMock = createMock(ComponentContext.class);		
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PepObligationObserver observerMock 
				= createMock(PepObligationObserver.class);
		expect(observerMock.evaluate(isA(Obligation.class), 
				isA(PepRequest.class), isA(ResponseCtx.class))).andReturn(true);
		replay(observerMock);
		
		BundlePepObligationObserverWrapper wrapper 
				= new BundlePepObligationObserverWrapper(observerMock);
		wrapper.activate(compCtxMock);
		assertTrue(wrapper.evaluate(createMock(Obligation.class), 
				createMock(PepRequest.class), createMock(ResponseCtx.class)));
		verify(nmMock);
		verify(compCtxMock);
		verify(observerMock);
	}

	@Test
	public void testActivate() {		
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);		

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);
		
		PepObligationObserver observerMock 
				= createMock(PepObligationObserver.class);
		replay(observerMock);
		
		BundlePepObligationObserverWrapper wrapper 
				= new BundlePepObligationObserverWrapper(observerMock);
		wrapper.activate(compCtxMock);
		verify(nmMock);
		verify(compCtxMock);
		verify(observerMock);
	}

	@Test
	public void testDeactivate() {
		NetworkManagerApplication nmMock 
				= createMock(NetworkManagerApplication.class);
		replay(nmMock);		

		ComponentContext compCtxMock = createMock(ComponentContext.class);
		expect(compCtxMock.locateService("NetworkManager")).andReturn(nmMock);
		replay(compCtxMock);

		PepObligationObserver observerMock 
				= createMock(PepObligationObserver.class);
		replay(observerMock);

		BundlePepObligationObserverWrapper wrapper 
				= new BundlePepObligationObserverWrapper(observerMock);
		wrapper.activate(compCtxMock);
		wrapper.deactivate(compCtxMock);
		verify(nmMock);
		verify(compCtxMock);
		verify(observerMock);		
	}

}
