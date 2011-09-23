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
 * This class represents a ranking result
 * 
 * @author Amro Al-Akkad
 * 
 */
public class RankingResult {

	/**
	 * URI of a device.
	 */
	private String deviceURI;

	/**
	 * Service name running on a device.
	 */
	private String serviceName;

	/**
	 * Device name.
	 */
	private String deviceName;

	/**
	 * Fitting rate, i.e. how well the service and device suits the specified
	 * requirement.
	 */
	private double fittingRate;

	/**
	 * Collection for properties of a ranking result.
	 */
	private QoSResponsePropertyCollectionWrapper properties;

	/**
	 * Constructs a ranking result.
	 * 
	 * @param deviceURI
	 *            URI of a device.
	 * @param serviceName
	 *            Service name running on a device.
	 * @param deviceName
	 *            Device name.
	 * @param fittingRate
	 *            Fitting rate, i.e. how well the service and device suits the
	 *            specified requirement.
	 * @param properties
	 *            Properties of related service and device.
	 */
	public RankingResult(String deviceURI, String serviceName,
			String deviceName, double fittingRate,
			QoSResponsePropertyCollectionWrapper properties) {
		super();
		this.deviceURI = deviceURI;
		this.serviceName = serviceName;
		this.deviceName = deviceName;
		this.fittingRate = fittingRate;
		this.properties = properties;
	}

	/**
	 * Collection for QoS response property.
	 * 
	 * @return Returns a collection of QoS response properties.
	 */
	public QoSResponsePropertyCollectionWrapper getProperties() {
		return properties;
	}

	/**
	 * Sets the properties of rank element.
	 * 
	 * @param properties
	 *            Properties.
	 */
	public void setProperties(QoSResponsePropertyCollectionWrapper properties) {
		this.properties = properties;
	}

	/**
	 * Getter for service name.
	 * 
	 * @return Returns the service name.
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * Setter for service name.
	 * 
	 * @param serviceName
	 *            Service name.
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * Getter for device name.
	 * 
	 * @return Returns the device name.
	 */
	public String getDeviceName() {
		return deviceName;
	}

	/**
	 * Setter for device name.
	 * 
	 * @param deviceName
	 *            Device name.
	 */
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	/**
	 * Getter for fitting rate.
	 * 
	 * @return Returns the fitting rate.
	 */
	public double getFittingRate() {
		return fittingRate;
	}

	/**
	 * Setter for fitting rate.
	 * 
	 * @param fittingRate
	 *            Fitting rate.
	 */
	public void setFittingRate(double fittingRate) {
		this.fittingRate = fittingRate;
	}

	/**
	 * Sets the device URI.
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
	 * @return Returns the URI of a device.
	 */
	public String getDeviceURI() {
		return deviceURI;
	}

}
