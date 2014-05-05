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

import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.ctx.Result;

import eu.linksmart.policy.pep.cache.impl.PdpSessionItem;


/**
 * Unit test for {@link PdpSessionItem}
 * 
 * @author Marco Tiemann
 *
 */
public class JunitPdpSessionItemTest {

	private static final String SESSION_ID = "abcdef";
	private static final String SENDER_HID = "sender";
	private static final String RECEIVER_HID = "receiver";
	private static final String PARAMETERS = "callsAMethod";
	private static ResponseCtx DECISION = null;	
	private static final Long TIMESTAMP = new Long(2l);
	
	static {
		Result result = new Result(Result.DECISION_PERMIT);
		DECISION = new ResponseCtx(result);
	}
	
	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionItem
	 * 		#PdpSessionItem(java.lang.Long)}.
	 */
	@SuppressWarnings("boxing")
	@Test
	public void testPdpSessionItemLong() {
		Long time = null;
		PdpSessionItem item = new PdpSessionItem(time);
		assertNotNull(item);
		assertEquals(time, item.getTimestamp());	
		time = System.currentTimeMillis();
		item = new PdpSessionItem(time.longValue());
		assertNotNull(item);
		assertEquals(time, item.getTimestamp());		
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionItem
	 * 		#PdpSessionItem(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testPdpSessionItemStringString() {
		String sessionId = null;
		String parameters = null;
		PdpSessionItem item = new PdpSessionItem(sessionId, parameters);
		assertNotNull(item);
		assertEquals(sessionId, item.getSessionId());
		assertEquals(parameters, item.getParameters());
		sessionId = SESSION_ID;
		parameters = PARAMETERS;
		item = new PdpSessionItem(sessionId, parameters);
		assertNotNull(item);
		assertEquals(sessionId, item.getSessionId());
		assertEquals(parameters, item.getParameters());
	}
	
	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionItem
	 * 		#PdpSessionItem(java.lang.String, java.lang.String, long)}.
	 */
	@SuppressWarnings("boxing")
	@Test
	public void testPdpSessionItemStringStringLong() {
		String sessionId = null;
		String parameters = null;
		Long timestamp = -1l;
		PdpSessionItem item = new PdpSessionItem(sessionId, parameters,
				timestamp.longValue());
		assertNotNull(item);
		assertEquals(sessionId, item.getSessionId());
		assertEquals(parameters, item.getParameters());
		assertEquals(timestamp, item.getTimestamp());
		sessionId = SESSION_ID;
		parameters = PARAMETERS;
		timestamp = TIMESTAMP;
		item = new PdpSessionItem(sessionId, parameters, timestamp.longValue());
		assertNotNull(item);
		assertEquals(sessionId, item.getSessionId());
		assertEquals(parameters, item.getParameters());
		assertEquals(timestamp, item.getTimestamp());
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionItem
	 * 		#PdpSessionItem(java.lang.String, java.lang.String, 
	 * 		java.lang.String, long)}.
	 */
	@SuppressWarnings("boxing")
	@Test
	public void testPdpSessionItemStringStringStringLong() {
		String sessionId = null;
		String parameters = null;
		Long timestamp = -1l;
		Result result = new Result(Result.DECISION_PERMIT);
		ResponseCtx response = new ResponseCtx(result);
		PdpSessionItem item = new PdpSessionItem(sessionId, parameters,
				response, timestamp.longValue());
		assertNotNull(item);
		assertEquals(sessionId, item.getSessionId());
		assertEquals(parameters, item.getParameters());
		assertEquals(response, item.getDecision());
		assertEquals(timestamp, item.getTimestamp());
		sessionId = SESSION_ID;
		parameters = PARAMETERS;
		timestamp = TIMESTAMP;
		item = new PdpSessionItem(sessionId, parameters, response,
				timestamp.longValue());
		assertNotNull(item);
		assertEquals(sessionId, item.getSessionId());
		assertEquals(parameters, item.getParameters());
		assertEquals(response, item.getDecision());
		assertEquals(timestamp, item.getTimestamp());
	}
	
	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionItem
	 * 		#PdpSessionItem(java.lang.String, java.lang.String, 
	 * 		java.lang.String, long)}.
	 */
	@SuppressWarnings("boxing")
	@Test
	public void testPdpSessionItemStringStringStringLongLong() {
		String sessionId = null;
		String parameters = null;
		Long timestamp = -1l;
		Result result = new Result(Result.DECISION_PERMIT);
		ResponseCtx response = new ResponseCtx(result);
		PdpSessionItem item = new PdpSessionItem(sessionId, parameters,
				response, timestamp.longValue(), 50l);
		assertNotNull(item);
		assertEquals(sessionId, item.getSessionId());
		assertEquals(parameters, item.getParameters());
		assertEquals(response, item.getDecision());
		assertEquals(timestamp, item.getTimestamp());
		sessionId = SESSION_ID;
		parameters = PARAMETERS;
		timestamp = TIMESTAMP;
		item = new PdpSessionItem(sessionId, parameters, response,
				timestamp.longValue(), 50l);
		assertNotNull(item);
		assertEquals(sessionId, item.getSessionId());
		assertEquals(parameters, item.getParameters());
		assertEquals(response, item.getDecision());
		assertEquals(timestamp, item.getTimestamp());
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionItem
	 * 		#PdpSessionItem(java.lang.String, java.lang.String, 
	 * 		java.lang.String, java.lang.String, java.lang.String, long, long)}.
	 */
	@SuppressWarnings("boxing")
	@Test
	public void testPdpSessionItemStringStringStringStringStringLong() {
		String sessionId = null;
		String senderHid = null;
		String receiverHid = null;
		String parameters = null;
		Long timestamp = -1l;		
		Result result = new Result(Result.DECISION_PERMIT);
		ResponseCtx response = new ResponseCtx(result);
		PdpSessionItem item = new PdpSessionItem(sessionId, senderHid, 
				receiverHid, parameters, response, timestamp.longValue());
		assertNotNull(item);
		assertEquals(sessionId, item.getSessionId());
		assertEquals(senderHid, item.getSenderHid());
		assertEquals(receiverHid, item.getReceiverHid());
		assertEquals(parameters, item.getParameters());
		assertEquals(response, item.getDecision());
		assertEquals(timestamp, item.getTimestamp());
		sessionId = SESSION_ID;
		senderHid = SENDER_HID;
		receiverHid = RECEIVER_HID;
		parameters = PARAMETERS;
		timestamp = TIMESTAMP;
		item = new PdpSessionItem(sessionId, senderHid, receiverHid, parameters, 
				response, timestamp.longValue());
		assertNotNull(item);
		assertEquals(sessionId, item.getSessionId());
		assertEquals(senderHid, item.getSenderHid());
		assertEquals(receiverHid, item.getReceiverHid());
		assertEquals(parameters, item.getParameters());
		assertEquals(response, item.getDecision());
		assertEquals(timestamp, item.getTimestamp());
	}
	
	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionItem
	 * 		#PdpSessionItem(java.lang.String, java.lang.String, 
	 * 		java.lang.String, java.lang.String, java.lang.String, long, long)}.
	 */
	@SuppressWarnings("boxing")
	@Test
	public void testPdpSessionItemStringStringStringStringStringLongLong() {
		String sessionId = null;
		String senderHid = null;
		String receiverHid = null;
		String parameters = null;
		Long timestamp = -1l;
		Result result = new Result(Result.DECISION_PERMIT);
		ResponseCtx response = new ResponseCtx(result);
		PdpSessionItem item = new PdpSessionItem(sessionId, senderHid, 
				receiverHid, parameters, response, timestamp.longValue(), 50l);
		assertNotNull(item);
		assertEquals(sessionId, item.getSessionId());
		assertEquals(senderHid, item.getSenderHid());
		assertEquals(receiverHid, item.getReceiverHid());
		assertEquals(parameters, item.getParameters());
		assertEquals(response, item.getDecision());
		assertEquals(timestamp, item.getTimestamp());
		sessionId = SESSION_ID;
		senderHid = SENDER_HID;
		receiverHid = RECEIVER_HID;
		parameters = PARAMETERS;
		timestamp = TIMESTAMP;
		item = new PdpSessionItem(sessionId, senderHid, receiverHid, parameters, 
				response, timestamp.longValue(), 50l);
		assertNotNull(item);
		assertEquals(sessionId, item.getSessionId());
		assertEquals(senderHid, item.getSenderHid());
		assertEquals(receiverHid, item.getReceiverHid());
		assertEquals(parameters, item.getParameters());
		assertEquals(response, item.getDecision());
		assertEquals(timestamp, item.getTimestamp());
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionItem
	 * 		#getSessionId()}.
	 */
	@Test
	public void testGetSessionId() {
		PdpSessionItem item = getInstance();
		assertEquals(SESSION_ID, item.getSessionId());
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionItem
	 * 		#getParameters()}.
	 */
	@Test
	public void testGetParameters() {
		PdpSessionItem item = getInstance();
		assertEquals(PARAMETERS, item.getParameters());
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionItem
	 * 		#getTimestamp()}.
	 */
	@Test
	public void testGetTimestamp() {
		PdpSessionItem item = getInstance();
		assertEquals(TIMESTAMP, item.getTimestamp());
	}
	
	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionItem
	 * 		#getTimeout()}.
	 */
	@Test
	public void testGetTimeout() {
		PdpSessionItem item = getInstance();
		assertEquals(new Long(50l), item.getTimeout());
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionItem
	 * 		#setTimestamp(java.lang.Long)}.
	 */
	@SuppressWarnings("boxing")
	@Test
	public void testSetTimestamp() {
		PdpSessionItem item = getInstance();
		assertEquals(TIMESTAMP, item.getTimestamp());
		Long time = System.currentTimeMillis();
		item.setTimestamp(time);
		assertEquals(time, item.getTimestamp());
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionItem
	 * 		#getDecision()}.
	 */
	@Test
	public void testGetDecision() {
		PdpSessionItem item = getInstance();
		assertEquals(DECISION, item.getDecision());
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionItem
	 * 		#setDecision(java.lang.String)}.
	 */
	@Test
	public void testSetDecision() {
		PdpSessionItem item = getInstance();
		assertEquals(DECISION, item.getDecision());
		Result result = new Result(Result.DECISION_PERMIT);
		ResponseCtx response = new ResponseCtx(result);
		item.setDecision(response);
		assertEquals(response, item.getDecision());
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionItem
	 * 		#getSenderHid()}.
	 */
	@Test
	public void testGetSenderHid() {
		PdpSessionItem item = getInstance();
		assertEquals(SENDER_HID, item.getSenderHid());
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionItem
	 * 		#getReceiverHid()}.
	 */
	@Test
	public void testGetReceiverHid() {
		PdpSessionItem item = getInstance();
		assertEquals(RECEIVER_HID, item.getReceiverHid());
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionItem
	 * 		#compareTo(eu.linksmart.policy.pep.cache.PdpSessionItem)}.
	 */
	@Test
	public void testCompareTo() {
		PdpSessionItem sub = getInstance();
		PdpSessionItem ref1 = new PdpSessionItem("aaaaaa", "callsAMethod");
		PdpSessionItem ref2 = new PdpSessionItem("aaaaaa", "callsBMethod");
		PdpSessionItem ref3 = new PdpSessionItem(null, null);
		PdpSessionItem ref4 = new PdpSessionItem("aaaaaa", null);
		PdpSessionItem ref5 = new PdpSessionItem("aaaaab", null);
		PdpSessionItem ref6 = new PdpSessionItem(null, "callsAMethod");
		PdpSessionItem ref7 = new PdpSessionItem(null, "callsBMethod");
		// to self
		assertEquals(0, sub.compareTo(sub));
		// to lower session ID 
		assertEquals(1, sub.compareTo(ref4));
		assertEquals(1, sub.compareTo(ref1));
		// to higher session ID
		assertEquals(-1, ref4.compareTo(sub));
		assertEquals(-1, ref1.compareTo(sub));
		// to higher method name
		assertEquals(-1, ref1.compareTo(ref2));
		// to lower method name
		assertEquals(1, ref2.compareTo(ref1));
		// to all null values
		assertEquals(1, sub.compareTo(ref3));
		// from all null values
		assertEquals(-1, ref3.compareTo(sub));
		// to self if all null values
		assertEquals(0, ref3.compareTo(ref3));
		// to self with null method call
		assertEquals(0, ref4.compareTo(ref4));
		// to self with null session ID
		assertEquals(0, ref6.compareTo(ref6));
		// with null session IDS
		assertEquals(-1, ref6.compareTo(ref7));
		assertEquals(1, ref7.compareTo(ref6));
		// to higher session ID withouth method
		assertEquals(-1, ref4.compareTo(ref5));
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionItem
	 * 		#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		PdpSessionItem item = getInstance();
		PdpSessionItem ref1 = new PdpSessionItem("aaaaaa", "callsAMethod");
		PdpSessionItem ref2 = new PdpSessionItem(null, null);
		assertTrue(item.equals(item));
		assertFalse(item.equals(ref1));
		assertFalse(item.equals(ref2));
		assertTrue(ref2.equals(ref2));
	}

	/**
	 * Test method for {@link eu.linksmart.policy.pep.cache.PdpSessionItem
	 * 		#hashCode()}.
	 */
	@SuppressWarnings("boxing")
	@Test
	public void testHashCode() {
		PdpSessionItem item = getInstance();
		assertNotNull(item.hashCode());
		PdpSessionItem ref1 = new PdpSessionItem("aaaaaa", "callsAMethod");
		assertTrue(item.hashCode() != ref1.hashCode());
	}
	
	/**
	 * @return
	 * 				the {@link PdpSessionItem}
	 */
	private static PdpSessionItem getInstance() {
		return new PdpSessionItem(SESSION_ID, SENDER_HID, RECEIVER_HID, 
				PARAMETERS, DECISION, TIMESTAMP.longValue(), 50l);
	}

}
