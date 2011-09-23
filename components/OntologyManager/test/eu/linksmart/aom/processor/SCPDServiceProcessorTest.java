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
 * Copyright (C) 2006-2010 Technical University of Kosice
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

package eu.linksmart.aom.processor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.junit.Test;
import org.openrdf.model.impl.ValueFactoryImpl;


import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.GraphData;
import eu.linksmart.aom.ontology.GraphLoader;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Service;
import eu.linksmart.aom.processor.Processor;
import eu.linksmart.aom.processor.SCPDServiceProcessor;
import eu.linksmart.aom.processor.SCPDServiceProcessor.StateVariable;
import eu.linksmart.aom.testutil.DataLoader;
import eu.linksmart.aom.testutil.StubData;

public class SCPDServiceProcessorTest extends StubData {

	private Element service(String type) {
		Element service = mock(Element.class);
		when(service.getChildTextTrim(
				anyString(), 
				any(Namespace.class))
		).thenReturn(type);
		return service;

	}

	@Test
	public void testProcess(){
		SCPDServiceProcessor p = spy(new SCPDServiceProcessor(repositoryStub()));
		doReturn(mock(Graph.class)).when(p).processService(any(Element.class), any(Graph.class));

		Element hydraservice = service("urn:schemas-upnp-org:hydraservice::1");
		Element powerService = service("urn:schemas-upnp-org:powerservice::1");
		Element smsService = service("urn:schemas-upnp-org:smsservice::1");
		Element bluetoothService = service("urn:schemas-upnp-org:bluetoothservice::1");
		Element locationService = service("urn:schemas-upnp-org:locationservice::1");
		Element energyService = service("urn:schemas-upnp-org:energyservice::1");
		Element memoryService = service("urn:schemas-upnp-org:memoryservice::1");

		List<Element> sl = new ArrayList<Element>();
		sl.add(hydraservice);
		sl.add(powerService);
		sl.add(smsService);
		sl.add(bluetoothService);
		sl.add(locationService);
		sl.add(energyService);
		sl.add(memoryService);

		Element services = mock(Element.class);

		when(services.getChildren(
				anyString(), 
				any(Namespace.class))
		).thenReturn(sl);

		Graph g = new Graph("http://device.uri");
		p.process(services, g);

		verify(p).processService(smsService, g);
		verify(p, never()).processService(hydraservice, g);
		verify(p, never()).processService(powerService, g);
		verify(p, never()).processService(bluetoothService, g);
		verify(p, never()).processService(locationService, g);
		verify(p, never()).processService(energyService, g);
		verify(p, never()).processService(memoryService, g);
	}

	@Test
	public void testProcessService() throws Exception {
		SCPDServiceProcessor p = spy(new SCPDServiceProcessor(repositoryStub()));
		doReturn(mock(Map.class)).when(p).stateTable(any(Element.class));
		doReturn(mock(Graph.class)).when(p).processActions(
				any(List.class), any(Graph.class), any(Map.class));

		XPath xp = XPath.newInstance("//d:service[4]");
		xp.addNamespace(Namespace.getNamespace("d", Processor.SCPD_DEVICE_NS));

		p.processService(
				(Element)xp.selectSingleNode(DataLoader.parse("test/resources/scpd/scpd.xml")),
				new Graph("http://device.uri")
		);

		verify(p, times(1)).processActions(any(List.class), any(Graph.class), any(Map.class));
		verify(p, times(1)).stateTable(any(Element.class));
	}

	@Test
	public void testStateTable() throws Exception {
		SCPDServiceProcessor p = new SCPDServiceProcessor(repositoryStub());

		XPath xp = XPath.newInstance("//d:service[4]//s:serviceStateTable");
		xp.addNamespace(Namespace.getNamespace("d", Processor.SCPD_DEVICE_NS));
		xp.addNamespace(Namespace.getNamespace("s", Processor.SCPD_SERVICE_NS));


		Map<String, StateVariable> actual = p.stateTable(
				(Element)xp.selectSingleNode(DataLoader.parse("test/resources/scpd/scpd.xml"))
		);
		Map<String, StateVariable> expected = new HashMap<String, StateVariable>();
		expected.put("NumberOfMessages", new StateVariable("NumberOfMessages", "i2", false));
		expected.put("Message", new StateVariable("Message", "string", false));
		expected.put("A_ARG_TYPE_ReadMessage_status", new StateVariable("A_ARG_TYPE_ReadMessage_status", "string", false));
		expected.put("A_ARG_TYPE_SendSMS_phonenumber", new StateVariable("A_ARG_TYPE_SendSMS_phonenumber", "string", false));
		expected.put("Index", new StateVariable("Index", "i2", false));

		assertEquals(expected, actual);

	}

	@Test
	public void testProcessActions() throws Exception {
		SCPDServiceProcessor p = spy(new SCPDServiceProcessor(repositoryStub()));
		doReturn(mock(Graph.class)).when(p).processAction(
				any(Element.class), any(Graph.class), any(Map.class)
		);

		XPath xp = XPath.newInstance("//d:service[4]//s:action");
		xp.addNamespace(Namespace.getNamespace("d", Processor.SCPD_DEVICE_NS));
		xp.addNamespace(Namespace.getNamespace("s", Processor.SCPD_SERVICE_NS));


		List<Element> actions = 
			(List<Element>)xp.selectNodes(DataLoader.parse("test/resources/scpd/scpd.xml"));

		p.processActions(actions, new Graph("http://device.uri"), null);
		verify(p, times(4)).processAction(any(Element.class), any(Graph.class), any(Map.class));
	}

	@Test
	public void testProcessAction() throws Exception {
		SCPDServiceProcessor p = spy(new SCPDServiceProcessor(repositoryStub()));

		doReturn(mock(Graph.class)).when(p).processParameter(
				any(Element.class), 
				any(String.class), 
				any(Graph.class), 
				any(Map.class));


		XPath xp = XPath.newInstance("//d:service[4]//s:action[3]");
		xp.addNamespace(Namespace.getNamespace("d", Processor.SCPD_DEVICE_NS));
		xp.addNamespace(Namespace.getNamespace("s", Processor.SCPD_SERVICE_NS));


		Element action = 
			(Element)xp.selectSingleNode(DataLoader.parse("test/resources/scpd/scpd.xml"));

		Map<String, StateVariable> table = new HashMap<String, StateVariable>();
		table.put("Message", new StateVariable("Message", "string", false));
		table.put("A_ARG_TYPE_ReadMessage_status", new StateVariable("A_ARG_TYPE_ReadMessage_status", "string", false));
		table.put("Index", new StateVariable("Index", "i2", false));

		String gURI = "http://device.uri";
		Graph actual = p.processAction(action, new Graph(gURI), table);
		Graph expected = GraphLoader.load(
				gURI, 
				GraphData.scpdServiceBase(gURI, actual.value(Device.hasService)));
		assertEquals(expected, actual);

		verify(p, times(3)).processParameter(
				any(Element.class), 
				any(String.class), 
				any(Graph.class), 
				any(Map.class));
	}


	@Test
	public void testProcessParameter() throws Exception {
		SCPDServiceProcessor p = spy(new SCPDServiceProcessor(repositoryStub()));

		XPath xpIn = XPath.newInstance("//d:service[4]//s:action[3]/s:argumentList/s:argument[1]");
		xpIn.addNamespace(Namespace.getNamespace("d", Processor.SCPD_DEVICE_NS));
		xpIn.addNamespace(Namespace.getNamespace("s", Processor.SCPD_SERVICE_NS));

		XPath xpOut = XPath.newInstance("//d:service[4]//s:action[3]/s:argumentList/s:argument[3]");
		xpOut.addNamespace(Namespace.getNamespace("d", Processor.SCPD_DEVICE_NS));
		xpOut.addNamespace(Namespace.getNamespace("s", Processor.SCPD_SERVICE_NS));


		Element paramIn = 
			(Element)xpIn.selectSingleNode(DataLoader.parse("test/resources/scpd/scpd.xml"));

		Element paramOut = 
			(Element)xpOut.selectSingleNode(DataLoader.parse("test/resources/scpd/scpd.xml"));

		Map<String, StateVariable> table = new HashMap<String, StateVariable>();
		table.put("Message", new StateVariable("Message", "string", false));
		table.put("A_ARG_TYPE_ReadMessage_status", new StateVariable("A_ARG_TYPE_ReadMessage_status", "string", false));
		table.put("Message", new StateVariable("Message", "string", false));
		table.put("Index", new StateVariable("Index", "i2", false));

		String serviceURI = "http://service.uri";
		Graph actualIn = p.processParameter(paramIn, serviceURI, new Graph(serviceURI), table);
		String paramInURI = actualIn.value(Service.hasInput);
		Graph expectedIn = GraphLoader.load(
				serviceURI, 
				GraphData.scpdServiceParamterIn(serviceURI, paramInURI));
		assertEquals(expectedIn, actualIn);

		Graph actualOut = p.processParameter(paramOut, serviceURI, new Graph(serviceURI), table);
		String paramOutURI = actualOut.value(Service.hasOutput);
		Graph expectedOut = GraphLoader.load(
				serviceURI, 
				GraphData.scpdServiceParamterOut(serviceURI, paramOutURI));
		assertEquals(expectedOut, actualOut);
	}

}
