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
package eu.linksmart.policy.pep.response.bundle.impl;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import org.junit.Test;

import com.sun.xacml.Obligation;
import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.ResponseCtx;

import eu.linksmart.policy.pep.impl.PepXacmlConstants;
import eu.linksmart.policy.pep.request.impl.PepRequest;
import eu.linksmart.policy.pep.response.bundle.impl.SendMailPepObligationObserver;
import eu.linksmart.policy.pep.response.bundle.impl.SendMailPepObligationObserverConfigurator;

/**
 * Unit test for {@link SendMailPepObligationObserver}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitSendMailPepObligationObserverTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testEvaluateNotApplicable() {
		SendMailPepObligationObserver observer = getInstance();
		PepRequest req = new PepRequest(new HashSet<Attribute>(), 
				"", "", "", "", "", "");
		ResponseCtx res = new ResponseCtx(new HashSet());
		try {
			Obligation obli = new Obligation(
					new URI(PepXacmlConstants.OBLIGATION_DEBUG.getUrn()), 0, 
					new ArrayList());
			boolean flag = observer.evaluate(obli, req, res);
			assertFalse(flag);
		} catch (URISyntaxException use) {
			use.printStackTrace();
			fail("Exception");
		}
	}

	@SuppressWarnings({ "unchecked", "boxing" })
	@Test
	public void testApplyConfigurations() {
		SendMailPepObligationObserver observer = getInstance();
		Hashtable confUpdates = new Hashtable();
		confUpdates.put(SendMailPepObligationObserverConfigurator.DEBUG_MODE, 
				"true");
		confUpdates.put(SendMailPepObligationObserverConfigurator.SMTP_HOST, 
		"my.little.smtp.host");
		confUpdates.put(SendMailPepObligationObserverConfigurator.SMTP_PORT, 
		"666");
		confUpdates.put(SendMailPepObligationObserverConfigurator.TLS, 
		"false");
		confUpdates.put(SendMailPepObligationObserverConfigurator.USER_NAME, 
		"MikeTheMailer");
		confUpdates.put(SendMailPepObligationObserverConfigurator.USER_PASS, 
		"password");
		confUpdates.put(SendMailPepObligationObserverConfigurator.DELIVER_ASYNCH, 
		"false");
		confUpdates.put(SendMailPepObligationObserverConfigurator.SSL, 
		"true");
		observer.applyConfigurations(confUpdates);
		assertTrue(observer.debugMode);
		assertEquals("my.little.smtp.host", observer.smtpHost);
		assertEquals("666", observer.port);
		assertFalse(observer.requiresTLS);
		assertEquals("MikeTheMailer", observer.username);
		assertEquals("password", observer.password);
		assertFalse(observer.deliverAsynch);
		assertTrue(observer.requiresSSL);
	}

	/**
	 * @return
	 * 				a {@link SendMailPepObligationObserver}
	 */
	private SendMailPepObligationObserver getInstance() {
		return new SendMailPepObligationObserver();
	}
	
}
