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
package eu.linksmart.caf.cm.engine.contexts;

/**
 * Class representing a {@link Device} service
 * 
 * @author Michael Crouch
 * 
 */
public abstract class Service extends MemberedContext {

	/** The serviceId */
	private String serviceId;

	/** The serviceType */
	private String serviceType;

	/** The hid of the service */
	private String hid;

	/** The service description */
	private String description;

	/** The associated {@link Device} */
	private Device device;

	/**
	 * Constructor
	 * 
	 * @param device
	 *            the {@link Device}
	 * @param serviceName
	 *            the name of the service
	 */
	public Service(Device device, String serviceName) {
		super(serviceName + ":" + device.getUdn());
		this.device = device;
	}

	/**
	 * Gets the {@link Device}
	 * 
	 * @return the {@link Device}
	 */
	public Device getDevice() {
		return device;
	}

	/**
	 * Sets the {@link Device}
	 * 
	 * @param device
	 *            the {@link Device} to set
	 */
	public void setDevice(Device device) {
		this.device = device;
	}

	/**
	 * Gets the serviceId
	 * 
	 * @return the serviceId
	 */
	public String getServiceId() {
		return serviceId;
	}

	/**
	 * Sets the serviceId
	 * 
	 * @param serviceId
	 *            the serviceId to set
	 */
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	/**
	 * Gets the serviceType
	 * 
	 * @return the serviceType
	 */
	public String getServiceType() {
		return serviceType;
	}

	/**
	 * Sets the serviceType
	 * 
	 * @param serviceType
	 *            the serviceType to set
	 */
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	/**
	 * Gets the hid
	 * 
	 * @return the hid
	 */
	public String getHid() {
		return hid;
	}

	/**
	 * Sets the hid
	 * 
	 * @param hid
	 *            the hid to set
	 */
	public void setHid(String hid) {
		this.hid = hid;
	}

	/**
	 * Gets the description
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description
	 * 
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
