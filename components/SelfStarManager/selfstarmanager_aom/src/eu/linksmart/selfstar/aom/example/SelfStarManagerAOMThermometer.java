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
 * Copyright (C) 2006-2010 
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
package eu.linksmart.selfstar.aom.example;

import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import eu.linksmart.selfstar.aom.SelfStarHandler;
import eu.linksmart.selfstar.aom.SelfStarManager;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component(immediate=true)
public class SelfStarManagerAOMThermometer implements SelfStarHandler, ThermometerSampler {

	private EventAdmin eventAdmin;

	private final int LOW_BATTERY_SAMPLING_RATE = 10;
	private final int MEDIUM_BATTERY_SAMPLING_RATE = 5;
	private final int HIGH_BATTERY_SAMPLING_RATE = 2;
	private SelfStarManager ssm;

	private HttpService http;

	private Map<String, LinkedList<Double>> temperatureData = new HashMap<String, LinkedList<Double>>();

	private String batteryLevel;

	protected void activate(ComponentContext context) throws Exception {
		http.registerServlet("/thermometerdata", new ThermometerDataServlet(this), null, null);
		http.registerResources( "/static", "/web", null);
	}
	
	private void testPerformance() throws RemoteException {
		long startTime = System.currentTimeMillis();
		int numIterations = 100;
		for (int i = 0; i < numIterations; i++) {
			ssm.getOntologyManager().getDevices("rdf:type;device:Thermometer", "");
			//"device:hasHardware/hardware:hasBatteryLevel/hardware:batteryLevelValue");
			ssm.getOntologyManager().updateValue("http://localhost/ontologies/Device.owl#TestingThermometer/RUNTIME_dc86d69f_8253_4e64_b03f_fdb57cff4964", 
			"device:hasHardware/hardware:hasBatteryLevel/hardware:batteryLevelValue;hardware:Low");
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Updating took " + (endTime-startTime));
		System.out.println("Updating took in average " + (endTime-startTime)/(1.0*numIterations));
	}
	
	@Override
	public String[] getTopics() {
		return new String[]{"spot/reading"};
	}

	@Override
	public void update(Event event) {
		int batteryLevel = Integer.parseInt((String)event.getProperty("battery"));
		double temperature = Double.parseDouble((String)event.getProperty("result"));
		String id = (String)event.getProperty("id");
		
		if (!temperatureData.containsKey(id)) {
			temperatureData.put(id, new LinkedList<Double>());
		}
		temperatureData.get(id).add(temperature);
		for (String otherId: temperatureData.keySet()) {
			if (!id.equals(otherId)) {
				// Add same reading for other ids
				temperatureData.get(otherId).add(temperatureData.get(otherId).getLast());
			}
		}

		System.out.println("Update device: " + id + " to battery level " + batteryLevel);
		// Create device with that PID
		String uri;
		try {
			uri = getDeviceURIFromPID(id);
			if(uri == null) {
				ssm.getOntologyManager().createTestingRuntimeClone("http://localhost/ontologies/Device.owl#TestingThermometer", id);
				uri = getDeviceURIFromPID(id);
			}
			ssm.getOntologyManager().updateValue(uri,
					"device:hasHardware/hardware:hasBatteryLevel/hardware:batteryLevelValue;hardware:" 
					+ getBatteryLevelString(batteryLevel));

			System.out.println(ssm.getOntologyManager().getDevices("rdf:type;device:Thermometer", 
			"device:hasHardware/hardware:hasBatteryLevel/hardware:batteryLevelValue"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void reason() {
		int samplingPeriod = -1;

		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			String response;
			Document document;

			response = ssm.getOntologyManager().getDevices("rdf:type;device:Thermometer,device:hasHardware/hardware:hasBatteryLevel/hardware:batteryLevelValue;hardware:Low", "");
			document = builder.parse(new InputSource(new StringReader(response)));
			if (document.getDocumentElement().getChildNodes().getLength() > 0) {
				System.out.println("Low battery device detected");
				samplingPeriod = LOW_BATTERY_SAMPLING_RATE;
				batteryLevel = "Low";
			} else {
				response = ssm.getOntologyManager().getDevices("rdf:type;device:Thermometer,device:hasHardware/hardware:hasBatteryLevel/hardware:batteryLevelValue;hardware:Medium", "");
				document = builder.parse(new InputSource(new StringReader(response)));
				if (document.getDocumentElement().getChildNodes().getLength() > 0) {
					System.out.println("Medium battery device detected");
					samplingPeriod = MEDIUM_BATTERY_SAMPLING_RATE;
					batteryLevel = "Medium";
				} else {
					System.out.println("All high battery devices");
					samplingPeriod = HIGH_BATTERY_SAMPLING_RATE;
					batteryLevel = "High";
				}
			}

			String topic = "spot/message";
			Dictionary<Object,Object> properties = new Hashtable<Object,Object>();
			properties.put("sampling.period", "" + samplingPeriod);
			eventAdmin.postEvent(new Event(topic, properties));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getDeviceURIFromPID(String pid) throws Exception {
		String result = null;
		System.out.println("Get URI for " + pid + " with " + ssm.getOntologyManager());
		String response = ssm.getOntologyManager().getDevices("device:PID;\"" + pid + "\"^^xsd:string", "");
		System.out.println("REsponse: " + response);
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.parse(new InputSource(new StringReader(response)));

		NodeList devices = document.getDocumentElement().getChildNodes();
		if (devices.getLength() > 0) {
			for (int i = 0; i < devices.getLength(); i++) {
				if (devices.item(i).getNodeType() == Node.ELEMENT_NODE) {
					result = ((Element) devices.item(i)).getAttribute("uri");
					break;
				}
			}
		}

		return result;
	}

	private String getBatteryLevelString(int batteryLevel) {
		String result = "High";
		if (batteryLevel < 50) {
			result = "Low";
		} else if (batteryLevel < 80) {
			result = "Medium";
		}
		return result;
	}

	@Override
	public void setSelfStarManager(SelfStarManager selfStarManager) {
		this.ssm = selfStarManager;
	}


	@Reference
	protected void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	protected EventAdmin getEventAdmin() {
		return eventAdmin;
	}

	protected void unsetEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = null;
	}
	
	@Reference
	protected void setHttpService(HttpService http) {
		this.http = http;
	}

	protected void unsetHttpService(HttpService http) {
		this.http = null;
	}

	@Override
	public Map<String, LinkedList<Double>> getTemperatureData() {
		return temperatureData;
	}

	@Override
	public String getBatteryLevel() {
		return batteryLevel;
	}
}
