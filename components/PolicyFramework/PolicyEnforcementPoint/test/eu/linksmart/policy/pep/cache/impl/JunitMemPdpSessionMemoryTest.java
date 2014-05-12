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

import org.apache.log4j.Logger;
import org.junit.Test;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.xacml3.Result;

import eu.linksmart.policy.pep.cache.impl.MemPdpSessionMemory;
import eu.linksmart.policy.pep.cache.impl.PdpSessionItem;


/**
 * Unit test for {@link MemPdpSessionMemory}
 *
 * @author Marco Tiemann
 */
public class JunitMemPdpSessionMemoryTest {

	/** logger */
	private static final Logger logger 
			= Logger.getLogger(JunitMemPdpSessionMemoryTest.class);

	/** cleaner interval, must be multiple of 50 */
	private static final long CLEANER_INTERVAL = 500l;
	
	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.MemPdpSessionMemory
	 * 		#MemPdpSessionMemory()}.
	 */
	@Test
	public void testMemPdpSessionMemory() {
		MemPdpSessionMemory memory = new MemPdpSessionMemory();
		assertNotNull(memory);
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.MemPdpSessionMemory
	 * 		#getLifetime()}.
	 */
	@SuppressWarnings("boxing")
	@Test
	public void testGetLifetime() {
		MemPdpSessionMemory memory = new MemPdpSessionMemory();
		assertNotNull(memory.getLifetime());
		assertTrue(0l < memory.getLifetime());
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.MemPdpSessionMemory
	 * 		#setLifetime(long)}.
	 */
	@Test
	public void testSetLifetimeLong() {
		MemPdpSessionMemory memory = new MemPdpSessionMemory();
		memory.setLifetime(100l);
		assertEquals(100l, memory.getLifetime());
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.MemPdpSessionMemory
	 * 		#setLifetime(java.lang.String)}.
	 */
	@Test
	public void testSetLifetimeString() {
		MemPdpSessionMemory memory = new MemPdpSessionMemory();
		memory.setLifetime("100");
		assertEquals(100l, memory.getLifetime());
		memory.setLifetime("200ms");
		assertEquals(200l, memory.getLifetime());
		memory.setLifetime("200s");
		assertEquals(200000l, memory.getLifetime());
		memory.setLifetime("200m");
		assertEquals(12000000l, memory.getLifetime());
		memory.setLifetime("200h");
		assertEquals(720000000l, memory.getLifetime());
		memory.setLifetime("200d");
		assertEquals(17280000000l, memory.getLifetime());
		memory.setLifetime("12312ab");
		memory.setLifetime("a12312");
		// should not have changed here
		assertEquals(17280000000l, memory.getLifetime());
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.MemPdpSessionMemory
	 * 		#getKeepAlive()}.
	 */
	@SuppressWarnings("boxing")
	@Test
	public void testGetKeepAlive() {
		MemPdpSessionMemory memory = new MemPdpSessionMemory();
		assertNotNull(memory.getKeepAlive());
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.MemPdpSessionMemory
	 * 		#setKeepAlive(boolean)}.
	 */
	@SuppressWarnings("boxing")
	@Test
	public void testSetKeepAlive() {
		MemPdpSessionMemory memory = new MemPdpSessionMemory();
		memory.setKeepAlive(false);
		assertEquals(false, memory.getKeepAlive());
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.MemPdpSessionMemory
	 * 		#getUseCleaner()}.
	 */
	@SuppressWarnings("boxing")
	@Test
	public void testGetUseCleaner() {
		MemPdpSessionMemory memory = new MemPdpSessionMemory();
		assertNotNull(memory.getUseCleaner());
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.MemPdpSessionMemory
	 * 		#setUseCleaner(boolean)}.
	 */
	@SuppressWarnings("boxing")
	@Test
	public void testSetUseCleaner() {
		MemPdpSessionMemory memory = new MemPdpSessionMemory();
		memory.setUseCleaner(false);
		assertEquals(false, memory.getUseCleaner());
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.MemPdpSessionMemory
	 * 		#setCleanerInterval(long)}.
	 */
	@Test
	public void testSetCleanerInterval() {
		MemPdpSessionMemory memory = new MemPdpSessionMemory();
		memory.setCleanerInterval(1000l);
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.MemPdpSessionMemory
	 * 		#add(eu.linksmart.policy.pep.cache.PdpSessionItem)}.
	 */
	@Test
	public void testAddWithSortingBySessionId() {
		MemPdpSessionMemory memory = new MemPdpSessionMemory();
		memory.setUseCleaner(false);
		memory.setCleanerInterval(CLEANER_INTERVAL * 2l);
		memory.setKeepAlive(true);
		memory.setLifetime(CLEANER_INTERVAL * 100l);
		Result result = new Result(Result.DECISION_PERMIT);
		ResponseCtx response = new ResponseCtx(result);
		PdpSessionItem item1 = new PdpSessionItem("abcdef", "callAMethod", 
				response, 1l);
		memory.add(item1);
		PdpSessionItem item2 = new PdpSessionItem("abcdeg", "callAMethod", 
				response, 1l);
		memory.add(item2);
	}
	
	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.MemPdpSessionMemory
	 * 		#add(eu.linksmart.policy.pep.cache.PdpSessionItem)}.
	 */
	@Test
	public void testAddWithSortingByTimestamp() {
		MemPdpSessionMemory memory = new MemPdpSessionMemory();
		memory.setUseCleaner(false);
		memory.setCleanerInterval(CLEANER_INTERVAL * 2);
		memory.setKeepAlive(true);
		memory.setLifetime(CLEANER_INTERVAL * 100);
		Result result = new Result(Result.DECISION_PERMIT);
		ResponseCtx response = new ResponseCtx(result);
		PdpSessionItem item1 = new PdpSessionItem("abcdef", "callAMethod", 
				response, 1l);
		memory.add(item1);
		PdpSessionItem item2 = new PdpSessionItem("abcdeg", "callAMethod", 
				response, 1l);
		memory.add(item2);
	}	

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.MemPdpSessionMemory
	 * 		#find(java.lang.String, java.lang.String, long)}.
	 */
	@Test
	public void testFindKeepAliveNoCleanerNoSortByTime() {
		MemPdpSessionMemory memory = new MemPdpSessionMemory();
		memory.setUseCleaner(false);
		memory.setCleanerInterval(CLEANER_INTERVAL);
		memory.setKeepAlive(true);
		memory.setLifetime(CLEANER_INTERVAL * 2);
		memory.setSortByTime(false);
		runFindTests(memory);
	}
	
	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.MemPdpSessionMemory
	 * 		#find(java.lang.String, java.lang.String, long)}.
	 */
	@Test
	public void testFindKeepAliveCleanerNoSortByTime() {
		MemPdpSessionMemory memory = new MemPdpSessionMemory();
		memory.setUseCleaner(true);
		memory.setCleanerInterval(CLEANER_INTERVAL);
		memory.setKeepAlive(true);
		memory.setLifetime(CLEANER_INTERVAL * 2);
		memory.setSortByTime(false);
		runFindTests(memory);
	}
	
	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.MemPdpSessionMemory
	 * 		#find(java.lang.String, java.lang.String, long)}.
	 */
	@Test
	public void testFindNoKeepAliveNoCleanerNoSortByTime() {
		MemPdpSessionMemory memory = new MemPdpSessionMemory();
		memory.setUseCleaner(false);
		memory.setCleanerInterval(CLEANER_INTERVAL);
		memory.setKeepAlive(false);
		memory.setLifetime(CLEANER_INTERVAL * 2);
		memory.setSortByTime(false);
		runFindTests(memory);
	}
	
	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.MemPdpSessionMemory
	 * 		#find(java.lang.String, java.lang.String, long)}.
	 */
	@Test
	public void testFindNoKeepAliveCleanerNoSortByTime() {
		MemPdpSessionMemory memory = new MemPdpSessionMemory();
		memory.setUseCleaner(true);
		memory.setCleanerInterval(CLEANER_INTERVAL);
		memory.setKeepAlive(false);
		memory.setLifetime(CLEANER_INTERVAL * 2);
		memory.setSortByTime(false);
		runFindTests(memory);
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.MemPdpSessionMemory
	 * 		#find(java.lang.String, java.lang.String, long)}.
	 */
	@Test
	public void testFindKeepAliveNoCleanerSortByTime() {
		MemPdpSessionMemory memory = new MemPdpSessionMemory();
		memory.setUseCleaner(false);
		memory.setCleanerInterval(CLEANER_INTERVAL);
		memory.setKeepAlive(true);
		memory.setLifetime(CLEANER_INTERVAL * 2);
		memory.setSortByTime(true);
		runFindTests(memory);
	}
	
	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.MemPdpSessionMemory
	 * 		#find(java.lang.String, java.lang.String, long)}.
	 */
	@Test
	public void testFindKeepAliveCleanerSortByTime() {
		MemPdpSessionMemory memory = new MemPdpSessionMemory();
		memory.setUseCleaner(true);
		memory.setCleanerInterval(CLEANER_INTERVAL);
		memory.setKeepAlive(true);
		memory.setLifetime(CLEANER_INTERVAL * 2);
		memory.setSortByTime(true);
		runFindTests(memory);
	}
	
	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.MemPdpSessionMemory
	 * 		#find(java.lang.String, java.lang.String, long)}.
	 */
	@Test
	public void testFindNoKeepAliveNoCleanerSortByTime() {
		MemPdpSessionMemory memory = new MemPdpSessionMemory();
		memory.setUseCleaner(false);
		memory.setCleanerInterval(CLEANER_INTERVAL);
		memory.setKeepAlive(false);
		memory.setLifetime(CLEANER_INTERVAL * 2);
		memory.setSortByTime(true);
		runFindTests(memory);
	}
	
	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.MemPdpSessionMemory
	 * 		#find(java.lang.String, java.lang.String, long)}.
	 */
	@Test
	public void testFindNoKeepAliveCleanerSortByTime() {
		MemPdpSessionMemory memory = new MemPdpSessionMemory();
		memory.setUseCleaner(true);
		memory.setCleanerInterval(CLEANER_INTERVAL);
		memory.setKeepAlive(false);
		memory.setLifetime(CLEANER_INTERVAL * 2);
		memory.setSortByTime(true);
		runFindTests(memory);
	}
	
	/**
	 * Runs a number of find tests
	 * 
	 * @param theMemory
	 * 				{@link MemPdpSessionMemory}
	 */
	private void runFindTests(MemPdpSessionMemory theMemory) {
		Result result = new Result(Result.DECISION_PERMIT);
		ResponseCtx response = new ResponseCtx(result);
		PdpSessionItem item1 = new PdpSessionItem("abcdef", "callAMethod", 
				response, System.currentTimeMillis());
		theMemory.add(item1);
		// retrieve the item that was just stored
		logger.debug("Item 1");
		PdpSessionItem sesResponse = theMemory.find("abcdef", "callAMethod", 
				System.currentTimeMillis());
		assertEquals(Result.DECISION_PERMIT, ((Result) sesResponse
				.getDecision().getResults().iterator().next()).getDecision());
		assertEquals("abcdef", sesResponse.getSessionId());
		assertEquals("callAMethod", sesResponse.getParameters());
		// retrieve an item that was not stored
		logger.debug("Item 1 - missing");
		sesResponse = theMemory.find("bbb", "callAMethod", 
				System.currentTimeMillis());
		assertNull(sesResponse);
		try {
			Thread.sleep(CLEANER_INTERVAL * 2 + CLEANER_INTERVAL / 50);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
		// retrieve an item that has expired
		logger.debug("Item 1 - expired");
		sesResponse = theMemory.find("abcdef", "callAMethod", 
				System.currentTimeMillis());
		assertNull(sesResponse);
		// add some more items
		Result result1 = new Result(Result.DECISION_PERMIT);
		ResponseCtx response1 = new ResponseCtx(result1);
		item1 = new PdpSessionItem("abcdef", "callAMethod", 
				response1, System.currentTimeMillis());
		theMemory.add(item1);
		PdpSessionItem item2 = new PdpSessionItem("abcdef", "callBMethod", 
				response1, System.currentTimeMillis());
		theMemory.add(item2);
		Result result2 = new Result(Result.DECISION_DENY);
		ResponseCtx response2 = new ResponseCtx(result2);
		PdpSessionItem item3 = new PdpSessionItem("abcdeg", "callAMethod", 
				response2, System.currentTimeMillis());
		theMemory.add(item3);
		Result result3 = new Result(Result.DECISION_INDETERMINATE);
		ResponseCtx response3 = new ResponseCtx(result3);
		PdpSessionItem item4 = new PdpSessionItem("abcdea", "callAMethod", 
				response3, System.currentTimeMillis(), CLEANER_INTERVAL * 10);
		theMemory.add(item4);
		Result result4 = new Result(Result.DECISION_NOT_APPLICABLE);
		ResponseCtx response4 = new ResponseCtx(result4);
		PdpSessionItem item5 = new PdpSessionItem("abcdea", "callCMethod", 
				response4, System.currentTimeMillis());
		theMemory.add(item5);
		logger.debug("Item 1 - readded");
		// item 1
		sesResponse = theMemory.find("abcdef", "callAMethod", 
				System.currentTimeMillis());
		assertEquals(Result.DECISION_PERMIT, ((Result) sesResponse.getDecision()
				.getResults().iterator().next()).getDecision());
		assertEquals("abcdef", sesResponse.getSessionId());
		assertEquals("callAMethod", sesResponse.getParameters());
		// item 2
		sesResponse = theMemory.find("abcdef", "callBMethod", 
				System.currentTimeMillis());
		assertEquals(Result.DECISION_PERMIT, ((Result) sesResponse.getDecision()
				.getResults().iterator().next()).getDecision());
		assertEquals("abcdef", sesResponse.getSessionId());
		assertEquals("callBMethod", sesResponse.getParameters());
		// item 3
		sesResponse = theMemory.find("abcdeg", "callAMethod", 
				System.currentTimeMillis());
		assertEquals(Result.DECISION_DENY, ((Result) sesResponse.getDecision()
				.getResults().iterator().next()).getDecision());
		assertEquals("abcdeg", sesResponse.getSessionId());
		assertEquals("callAMethod", sesResponse.getParameters());
		// item 4
		sesResponse = theMemory.find("abcdea", "callAMethod", 
				System.currentTimeMillis());
		assertEquals(Result.DECISION_INDETERMINATE, ((Result) sesResponse
				.getDecision().getResults().iterator().next()).getDecision());
		assertEquals("abcdea", sesResponse.getSessionId());
		assertEquals("callAMethod", sesResponse.getParameters());	
		// item 5
		sesResponse = theMemory.find("abcdea", "callCMethod", 
				System.currentTimeMillis());
		assertEquals(Result.DECISION_NOT_APPLICABLE, ((Result) sesResponse
				.getDecision().getResults().iterator().next()).getDecision());
		assertEquals("abcdea", sesResponse.getSessionId());
		assertEquals("callCMethod", sesResponse.getParameters());	
		try {
			Thread.sleep(CLEANER_INTERVAL / 5l);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
		// item 4
		sesResponse = theMemory.find("abcdea", "callAMethod", 
				System.currentTimeMillis());
		assertEquals(Result.DECISION_INDETERMINATE, ((Result) sesResponse
				.getDecision().getResults().iterator().next()).getDecision());
		assertEquals("abcdea", sesResponse.getSessionId());
		assertEquals("callAMethod", sesResponse.getParameters());	
	}

}
