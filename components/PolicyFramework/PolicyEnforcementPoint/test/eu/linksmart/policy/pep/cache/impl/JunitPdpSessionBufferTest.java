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
package eu.linksmart.policy.pep.cache.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.xacml3.Result;

import eu.linksmart.policy.pep.cache.impl.MemPdpSessionMemory;
import eu.linksmart.policy.pep.cache.impl.PdpSessionCache;


/**
 * Unit test for {@link PdpSessionCache}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitPdpSessionBufferTest {

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionCache
	 * 		#PdpSessionBuffer()}.
	 */
	@Test
	public void testPdpSessionBuffer() {
		PdpSessionCache buffer = new PdpSessionCache();
		assertNotNull(buffer);
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionCache
	 * 		#getSessionMemory()}.
	 */
	@Test
	public void testGetSessionMemory() {
		PdpSessionCache buffer = new PdpSessionCache();
		assertNotNull(buffer.getSessionMemory());
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionCache
	 * 		#setSessionMemory(eu.linksmart.policy.pep.PdpSessionMemory)}.
	 */
	@Test
	public void testSetSessionMemory() {
		PdpSessionCache buffer = new PdpSessionCache();
		assertNotNull(buffer.getSessionMemory());
		buffer.setSessionMemory(null);
		assertNull(buffer.getSessionMemory());
		MemPdpSessionMemory memory = new MemPdpSessionMemory();
		buffer.setSessionMemory(memory);
		assertEquals(memory, buffer.getSessionMemory());
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionCache
	 * 		#add(java.lang.String, java.lang.String, java.lang.String, long)}.
	 */
	@Test
	public void testAddStringStringStringLong() {
		PdpSessionCache buffer = new PdpSessionCache();
		Result result = new Result(Result.DECISION_PERMIT);
		ResponseCtx response = new ResponseCtx(result);
		buffer.add("abcdef", "callAMethod", response, 
				System.currentTimeMillis());
		assertEquals(Result.DECISION_PERMIT, ((Result) buffer.evaluate("abcdef", 
				"callAMethod", System.currentTimeMillis())
						.getResults().iterator().next()).getDecision());
	}
	
	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionCache
	 * 		#add(java.lang.String, java.lang.String, java.lang.String, long)}.
	 */
	@Test
	public void testAddStringStringStringLongLong() {
		PdpSessionCache buffer = new PdpSessionCache();
		Result result = new Result(Result.DECISION_PERMIT);
		ResponseCtx response = new ResponseCtx(result);
		buffer.add("abcdef", "callAMethod", response, 
				System.currentTimeMillis(), 50l);
		assertEquals(Result.DECISION_PERMIT, ((Result) buffer.evaluate("abcdef", 
				"callAMethod", System.currentTimeMillis())
						.getResults().iterator().next()).getDecision());
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionCache
	 * 		#add(java.lang.String, java.lang.String, java.lang.String, 
	 * 				java.lang.String, java.lang.String, long)}.
	 */
	@Test
	public void testAddStringStringStringStringStringLong() {
		PdpSessionCache buffer = new PdpSessionCache();
		Result result = new Result(Result.DECISION_PERMIT);
		ResponseCtx response = new ResponseCtx(result);
		buffer.add("abcdef", "subHid1", "refhid1", "callAMethod", 
				response, System.currentTimeMillis());
		assertEquals(Result.DECISION_PERMIT, ((Result) buffer.evaluate("abcdef", 
				"callAMethod", System.currentTimeMillis())
						.getResults().iterator().next()).getDecision());
	}
	
	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionCache
	 * 		#add(java.lang.String, java.lang.String, java.lang.String, 
	 * 				java.lang.String, java.lang.String, long, long)}.
	 */
	@Test
	public void testAddStringStringStringStringStringLongLong() {
		PdpSessionCache buffer = new PdpSessionCache();
		Result result = new Result(Result.DECISION_PERMIT);
		ResponseCtx response = new ResponseCtx(result);
		buffer.add("abcdef", "subHid1", "refhid1", "callAMethod", 
				response, System.currentTimeMillis(), 500l);
		assertEquals(Result.DECISION_PERMIT, ((Result) buffer.evaluate("abcdef", 
				"callAMethod", System.currentTimeMillis())
						.getResults().iterator().next()).getDecision());
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionCache
	 * 		#evaluate(java.lang.String, java.lang.String, long)}.
	 */
	@Test
	public void testEvaluateStringStringLong() {
		PdpSessionCache buffer = new PdpSessionCache();
		Result result = new Result(Result.DECISION_PERMIT);
		ResponseCtx response = new ResponseCtx(result);
		buffer.add("abcdef", "subHid1", "refhid1", "callAMethod", 
				response, System.currentTimeMillis(), 500l);
		assertEquals(Result.DECISION_PERMIT, ((Result) buffer.evaluate("abcdef", 
				"callAMethod", System.currentTimeMillis())
						.getResults().iterator().next()).getDecision());
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionCache
	 * 		#evaluate(java.lang.String, java.lang.String, java.lang.String, 
	 * 				java.lang.String, long)}.
	 */
	@Test
	public void testEvaluateStringStringStringStringLong() {
		PdpSessionCache buffer = new PdpSessionCache();
		Result result = new Result(Result.DECISION_PERMIT);
		ResponseCtx response = new ResponseCtx(result);
		buffer.add("abcdef", "subHid1", "refHid1", "callAMethod", 
				response, System.currentTimeMillis(), 500l);
		assertEquals(Result.DECISION_PERMIT, ((Result) buffer.evaluate("abcdef", 
				"subHid1", "refHid1", "callAMethod", System.currentTimeMillis())
						.getResults().iterator().next()).getDecision());
	}

}
