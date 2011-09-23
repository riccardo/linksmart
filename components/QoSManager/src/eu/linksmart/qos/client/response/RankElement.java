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
package eu.linksmart.qos.client.response;

/**
 * This class represents a rank element in the QoS response to its client.
 * 
 * @author Amro Al-Akkad
 * 
 */
public class RankElement {

	/**
	 * Service name.
	 */
	private String servicename;

	/**
	 * Persistent identifier of a device.
	 */
	private String devicePID;

	/**
	 * Calculated average fit criteria of a service.
	 */
	private String average;

	/**
	 * URI of a device.
	 */
	private String deviceURI;

	/**
	 * Flag indicating if device and service running on it fulfill the
	 * requirements.
	 */
	private boolean disqualified = false;

	/**
	 * Getter for service name.
	 * 
	 * @return Returns the service name.
	 */
	public String getServicename() {
		return servicename;
	}

	/**
	 * Setter for service name.
	 * 
	 * @param servicename
	 *            Service name.
	 */
	public void setServicename(String servicename) {
		this.servicename = servicename;
	}

	/**
	 * Setter for device URI.
	 * 
	 * @param deviceURI
	 *            Device URI.
	 */
	public void setDeviceURI(String deviceURI) {
		this.deviceURI = deviceURI;

	}

	/**
	 * Getter for device URI.
	 * 
	 * @return Returns the device URI.
	 */
	public String getDeviceURI() {
		return this.deviceURI;
	}

	/**
	 * Setter for device PID.
	 * 
	 * @param devicePID
	 *            Persistent identifier of a device.
	 */
	public void setDevicePID(String devicePID) {
		this.devicePID = devicePID;
	}

	/**
	 * Getter for device PID.
	 * 
	 * @return Returns the persistent identifier of a device.
	 */
	public String getDevicePID() {
		return devicePID;
	}

	/**
	 * Sets the average of a rank element.
	 * 
	 * @param average
	 *            Average of a rank element.
	 */
	public void setAverage(String average) {
		this.average = average;
	}

	/**
	 * Getter for average of a rank element.
	 * 
	 * @return Returns the average of a rank element.
	 */
	public String getAverage() {
		return average;
	}

	/**
	 * Sets if the rank is qualified or disqualified, i.e. it fulfills the
	 * requirements or not.
	 * 
	 * @param disqualified
	 *            Qualification status.
	 */
	public void setDisqualified(boolean disqualified) {
		this.disqualified = disqualified;
	}

	/**
	 * Getter for qualification status.
	 * 
	 * @return Returns the flag indicating if the device/service in this rank
	 *         fulfills the specified requirement.
	 */
	public boolean isDisqualified() {
		return disqualified;
	}

}
