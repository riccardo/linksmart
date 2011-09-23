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
package eu.linksmart.qos.ontology.response;

import java.util.Arrays;

/**
 * Class represents a device in QoS Model.
 * 
 * @author Amro Al-Akkad
 * 
 */
public class Device {

	/**
	 * URI of a device.
	 */
	private final String deviceURI;

	/**
	 * Services a device runs.
	 */
	private final Service[] services;
	
	/**
	 * Persistence identifier of a device.
	 */
	private final String pid;

	/**
	 * Properties of a device.
	 */
	private final OntologyResponseProperty[] properties;

	/**
	 * Field that indicates if a device runs a service that has a not numeric property not matching a requirement, 
	 * e.g. the video file format is expected to be AVI instead of MPEG. 
	 */
	private boolean disqualified;

	/**
	 * Constructor for creating a device represented in ontology.
	 * @param devicePid Persistent identifier of a device.
	 * @param deviceURI URI of a device.
	 * @param services Services a device runs.
	 * @param properties Properties of a device.
	 */
	public Device(String devicePid, String deviceURI, Service[] services,
			OntologyResponseProperty[] properties) {
		super();
		this.pid = devicePid;
		this.deviceURI = deviceURI;
		this.services = Arrays.copyOfRange(services, 0, services.length);
		this.properties = Arrays.copyOfRange(properties, 0, properties.length);
		this.disqualified = false;
	}

	/**
	 * Getter for persistence identifier.
	 * @return Returns the value of the persistence identifier.
	 */
	public String getPid() {
		return pid;
	}

	/**
	 * Getter for URI of a device.
	 * @return Returns the URI of a device.
	 */
	public String getDeviceURI() {
		return deviceURI;
	}

	/**
	 * Getter for services a device runs.
	 * @return Returns the services as an Array of <b>Service</b>.
	 */
	public Service[] getServices() {
		return Arrays.copyOfRange(services, 0, services.length);
	}

	/**
	 * Getter for properties of a device.
	 * @return Returns the properties of a device.
	 */
	public OntologyResponseProperty[] getProperties() {
		return Arrays.copyOfRange(properties, 0, properties.length);
	}

	/**
	 * Returns the qualification status of a device. See documenation of field 'disqualifield'.
	 * @return Returns TRUE if the device is disqualified, otherwise FALSE.
	 */
	public boolean isDisqualified() {
		return disqualified;
	}

	/**
	 * Sets the qualification status of a device. See documenation of field 'disqualifield'.
	 * @param disqualified If true the device is disqualified, otherwise not.
	 */
	public void setDisqualified(boolean disqualified) {
		this.disqualified = disqualified;
	}

}
