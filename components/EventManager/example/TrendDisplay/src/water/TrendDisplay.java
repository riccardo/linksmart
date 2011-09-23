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
package water;

import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.util.Properties;

import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import eu.linksmart.clients.RemoteWSClientProvider;
import eu.linksmart.eventmanager.EventManagerPort;
import eu.linksmart.eventmanager.EventSubscriber;
import eu.linksmart.eventmanager.Part;
import eu.linksmart.network.CryptoHIDResult;
import eu.linksmart.network.NetworkManagerApplication;

@Component(immediate=true,properties="SOAP.service.name=TrendDisplaySubscriber")
public class TrendDisplay implements EventSubscriber {
	private RemoteWSClientProvider provider;
	
	@Reference
	protected void setRemoteWSClientProvider(RemoteWSClientProvider provider) {
		this.provider = provider;
	}
	
	public void activate(ComponentContext context) {
		try {
			// Get Network Manager
			NetworkManagerApplication nm = 
				(NetworkManagerApplication) provider.getRemoteWSClient(NetworkManagerApplication.class.getName(), null, false);
			// Get Event Manager
			EventManagerPort em =
				(EventManagerPort) provider.getRemoteWSClient(EventManagerPort.class.getName(), null, false);
			// Create HID for this service
			String serviceName = (String) context.getProperties().get("SOAP.service.name");
			Properties properties = new Properties();
			properties.setProperty(NetworkManagerApplication.PID, "Water:TrendDisplay");
			properties.setProperty(NetworkManagerApplication.SID, serviceName);
			properties.setProperty(NetworkManagerApplication.DESCRIPTION, "Water Trend Display");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			properties.storeToXML(baos, "");
			String xmlAttributes = baos.toString();
			CryptoHIDResult hid = nm.createCryptoHID(xmlAttributes, "http://localhost:8082/axis/services/" + serviceName); // FIXME
			// Subscribe using HID
			em.subscribeWithHID("/water/consumption", hid.getHID());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean notify(String topic, Part[] parts) throws RemoteException {
		System.out.println("Current water level is: " + parts[0].getValue()); // FIXME
		return true;
	}
}
