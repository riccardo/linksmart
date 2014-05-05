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
package eu.linksmart.policy.pep.request.impl;

import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.sun.xacml.ctx.Attribute;

import eu.linksmart.policy.pep.request.SoapAttrParser;

/**
 * Abstract unit test super class for (@link SoapAttrParser}s
 * 
 * @author Marco Tiemann
 *
 */
public abstract class JunitAbstractSoapAttrParser {

	/** test method request */
	private static final String METHOD_REQUEST 
			= "Content-Type: text/xml; charset=utf-8" +
				"Accept: application/soap+xml, application/dime, " +
				"multipart/related, text/*" +
				"User-Agent: Axis/1.4" +
				"Host: localhost:8082" +
				"Cache-Control: no-cache" +
				"Pragma: no-cache" +
				"SOAPAction: \"\"" +
				"Content-Length: 520" +
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/" +
				"soap/envelope/\"" +
				" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
				" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
				"<soapenv:Body><ns1:hello soapenv:encodingStyle=\"http://" +
				"schemas.xmlsoap.org/soap/encoding/\"" +
				" xmlns:ns1=\"TestLinkSmartApplicationImpl\">" +
				"<ns1:arg0 xsi:type=\"soapenc:string\"" +
				" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
				"Is it me you're looking for?</ns1:arg0>" +
				"<ns1:arg1 xsi:type=\"soapenc:string\"" +
				" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
				"Is it me you're looking for!</ns1:arg1>" +
				"</ns1:hello></soapenv:Body></soapenv:Envelope>";
	
	/** a test event request */
	private static final String EVENT_REQUEST
			= "Content-Type: text/xml; charset=utf-8" +
			"Accept: application/soap+xml, application/dime, " +
			"multipart/related, text/*" +
			"User-Agent: Axis/1.4" +
			"Host: localhost:8082" +
			"Cache-Control: no-cache" +
			"Pragma: no-cache" +
			"SOAPAction: \"\"" +
			"Content-Length: 484" +
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/" +
			"soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
			"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
			"<soapenv:Body><publish xmlns=\"http://eventmanager.linksmart.eu\">" +
			"<in0 xmlns=\"\">ExampleTopic</in0><in1 xmlns=\"\">" +
			"<item><key>HID</key><value>0.0.0.4154265465</value></item>" +
			"<item><key>ExampleValue</key><value>32</value></item></in1>" +
			"</publish></soapenv:Body></soapenv:Envelope>";
	
	/** a test .NET Device Application Catalogue and Discovery Manager request */
	private static final String NET_REQUEST
			= "SOAPACTION: \"urn:schemas-upnp-org:linksmartservice::1#SetProperty\"" +
			"Content-Type: text/xml ; charset=\"utf-8\"" +
			"Host: 127.0.0.1:8082" +
			"Content-Length: 426" +
			"<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
			"<s:Envelope s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" " +
			"xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
			"<s:Body>" +
			"<u:SetProperty xmlns:u=\"urn:schemas-upnp-org:linksmartservice::1\">" +
			"<property>linksmartidUPnPService_urn_upnp-org_serviceId_1</property>" +
			"<value>0.0.0.1007122138904673075</value>" +
			"</u:SetProperty>" +
			"</s:Body>" +
			"</s:Envelope>";
	
	/** a test KSOAP Android message */
	private static final String KSOAP_MESSAGE = 
			"Host: 134.225.210.247:8082 " +
			"User-Agent: kSOAP/2.0 " +
			"soapaction: getCertficates " +
			"Content-Type: text/xml " +
			"Connection: close " +
			"Content-Length: 345 " +
			"<v:Envelope xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" " +
			"xmlns:d=\"http://www.w3.org/2001/XMLSchema\" " +
			"xmlns:c=\"http://schemas.xmlsoap.org/soap/encoding/\" " +
			"xmlns:v=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
			"<v:Header />" +
			"<v:Body><n0:getCertficates id=\"o0\" c:root=\"1\" " +
			"xmlns:n0=\"http://rfidapp.demo.linksmartmiddleware.eu\" />" +
			"</v:Body></v:Envelope>";
	
	/** {@link SoapAttrParser} */
	protected SoapAttrParser parser = null;
	
	@Before
	public abstract void setUp() throws Exception;

	@Test
	public void testGetAttributeFactory() {
		assertNotNull(parser.getAttributeFactory());
	}

	@Test
	public void testGetXmlTypeKeys() {
		assertNotNull(parser.getXmlTypeKeys());
	}

	@Test
	public void testSetXmlTypeKeys() {
		String[] keys = {"xsi:type"};
		parser.setXmlTypeKeys(keys);
		assertNotNull(parser.getXmlTypeKeys());
	}

	@Test
	public void testExtractActionsFromMethodSoapMsg() {
		Set<Attribute> attrs = parser.extractActionsFromSoapMsg(METHOD_REQUEST);
		assertNotNull(attrs);
		boolean rightMethod = false;
		boolean rightArg0 = false;
		boolean rightArg1 = false;
		for (Attribute attr : attrs) {
			String id = attr.getId().toString();
			String type = attr.getType().toString();
			String valu = attr.getValue().toString();
			if (id.equals("urn:oasis:names:tc:xacml:1.0:action:action-id")) {
				if ((type.equals("http://www.w3.org/2001/XMLSchema#string"))
					&& (valu.equals("StringAttribute: \"hello\""))) {
					rightMethod = true;
				}
			} else if (id.equals("linksmart:policy:action:arg0")) {
				if ((type.equals("http://www.w3.org/2001/XMLSchema#string"))
						&& (valu.equals("StringAttribute: \"Is it me you're " +
								"looking for?\""))) {
						rightArg0 = true;
					}
			} else if (id.equals("linksmart:policy:action:arg1")) {
				if ((type.equals("http://www.w3.org/2001/XMLSchema#string"))
						&& (valu.equals("StringAttribute: \"Is it me you're " +
								"looking for!\""))) {
						rightArg1 = true;
					}
			}
		}
		assertTrue(rightMethod);
		assertTrue(rightArg0);
		assertTrue(rightArg1);
	}
	
	@Test
	public void testExtractActionsFromEventSoapMsg() {
		Set<Attribute> attrs = parser.extractActionsFromSoapMsg(EVENT_REQUEST);
		assertNotNull(attrs);
		boolean rightMethod = false;
		boolean rightArg0 = false;
		boolean rightArg1 = false;
		for (Attribute attr : attrs) {
			String id = attr.getId().toString();
			String type = attr.getType().toString();
			String valu = attr.getValue().toString();
			if (id.equals("urn:oasis:names:tc:xacml:1.0:action:action-id")) {
				if ((type.equals("http://www.w3.org/2001/XMLSchema#string"))
					&& (valu.equals("StringAttribute: \"publish\""))) {
					rightMethod = true;
				}
			} else if (id.equals("linksmart:policy:action:arg0")) {
				if ((type.equals("http://www.w3.org/2001/XMLSchema#string"))
						&& (valu.equals("StringAttribute: \"ExampleTopic\""))) {
						rightArg0 = true;
					}
			} else if (id.equals("linksmart:policy:action:arg1")) {
				if ((type.equals("http://www.w3.org/2001/XMLSchema#string"))
						&& (valu.equals("StringAttribute: \"<![CDATA[<arg1>" +
								"<item><key>HID</key><value>0.0.0.4154265465" +
								"</value></item><item><key>ExampleValue</key>" +
								"<value>32</value></item></arg1>]]>\""))) {
						rightArg1 = true;
					}
			}
		}
		assertTrue(rightMethod);
		assertTrue(rightArg0);
		assertTrue(rightArg1);
	}

	@Test
	public void testExtractActionsFromNetSoapMsg() {
		assertTrue(parser.extractActionsFromSoapMsg(NET_REQUEST).size() > 0);
	}
	
	@Test
	public void testExtractActionsFromKSoapMsg() {
		Set<Attribute> attrs = parser.extractActionsFromSoapMsg(KSOAP_MESSAGE);
		Iterator<Attribute> atterator = attrs.iterator();
		while (atterator.hasNext()) {
			System.out.println(atterator.next().encode());
		}
	}
	
}
