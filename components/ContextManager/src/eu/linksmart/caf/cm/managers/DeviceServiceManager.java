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
package eu.linksmart.caf.cm.managers;

import org.w3c.dom.Element;

import eu.linksmart.caf.cm.engine.contexts.BaseContext;
import eu.linksmart.caf.cm.engine.contexts.Device;
import eu.linksmart.caf.cm.engine.contexts.EnergyService;
import eu.linksmart.caf.cm.engine.contexts.LocationService;
import eu.linksmart.caf.cm.engine.contexts.PowerService;
import eu.linksmart.caf.cm.engine.contexts.Service;
import eu.linksmart.caf.cm.impl.CmManagerHub;
import eu.linksmart.caf.cm.impl.util.XmlUtils;

/**
 * This manager handles the processing of addition device services, if they
 * exist, including: <ul> <li>Energy Service</li> <li>Power Service</li>
 * <li>Location Service</li> </ul>
 * 
 * @author Michael Crouch
 * 
 */
public class DeviceServiceManager extends CmInternalManager {

	/** The Id for this Internal Manager */
	public static final String MANAGER_ID =
			"eu.linksmart.caf.cm.DeviceServiceManager";

	/** The tag name of the Service ID element */
	private static final String SERVICE_ID = "serviceId";

	/** The tag name of the Service Type element */
	private static final String SERVICE_TYPE = "serviceType";

	/** The tag name of the LinkSmart ID element */
	private static final String SERVICE_HID = "linksmartId";

	/** The tag name of the Description element */
	private static final String SERVICE_DESC = "description";

	@Override
	public String getManagerId() {
		return MANAGER_ID;
	}

	@Override
	public void initialise(CmManagerHub hub) {
		//Do nothing

	}
	
	@Override
	public void completedInit() {
		//Do nothing		
	}

	@Override
	public void shutdown() {
		//Do nothing

	}

	/**
	 * Processes the service {@link Element} given, for the {@link Device}, if
	 * it is one of the defined "LinkSmart" services.
	 * 
	 * @param device
	 *            teh {@link Device}
	 * @param serviceElement
	 *            the service SCPD {@link Element}
	 */
	public void processService(Device device, Element serviceElement) {

		String serviceType =
				XmlUtils.getElementValueFromTagName(serviceElement,
						"serviceType").toLowerCase();

		if (serviceType.contains("linksmartservice")) {

		} else if (serviceType.contains("powerservice")) {
			processPowerService(device, serviceElement);
		} else if (serviceType.contains("locationservice")) {
			processLocationService(device, serviceElement);
		} else if (serviceType.contains("energyservice")) {
			processEnergyService(device, serviceElement);
		} else if (serviceType.contains("memoryservice")) {

		}
	}

	/**
	 * Process the service {@link Element} to the {@link EnergyService}
	 * 
	 * @param device
	 *            the {@link Device}
	 * @param serviceElement
	 *            the service {@link Element}
	 */
	private void processEnergyService(Device device, Element serviceElement) {
		EnergyService service = new EnergyService(device);
		processServiceElement(service, serviceElement);
	}

	/**
	 * Process the service {@link Element} to the {@link PowerService}
	 * 
	 * @param device
	 *            the {@link Device}
	 * @param serviceElement
	 *            the service {@link Element}
	 */
	private void processPowerService(Device device, Element serviceElement) {
		PowerService service = new PowerService(device);
		processServiceElement(service, serviceElement);
	}

	/**
	 * Process the service {@link Element} to the {@link LocationService}
	 * 
	 * @param device
	 *            the {@link Device}
	 * @param serviceElement
	 *            the service {@link Element}
	 */
	private void processLocationService(Device device, Element serviceElement) {
		LocationService service = new LocationService(device);
		processServiceElement(service, serviceElement);
	}

	/**
	 * Processes the base service information from the {@link Element} to the
	 * {@link Service}
	 * 
	 * @param theService
	 *            the {@link Service}
	 * @param serviceElement
	 *            the {@link Element}
	 */
	private void processServiceElement(Service theService,
			Element serviceElement) {
		if (theService == null)
			return;

		theService.setHid(XmlUtils.getElementValueFromTagName(serviceElement,
				SERVICE_HID));
		theService.setServiceId(XmlUtils.getElementValueFromTagName(
				serviceElement, SERVICE_ID));
		theService.setServiceType(XmlUtils.getElementValueFromTagName(
				serviceElement, SERVICE_TYPE));
		theService.setDescription(XmlUtils.getElementValueFromTagName(
				serviceElement, SERVICE_DESC));
	}

}
