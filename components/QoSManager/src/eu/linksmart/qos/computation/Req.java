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
package eu.linksmart.qos.computation;

/**
 * Class represents a requirement in the QoS model for computation.
 * 
 * @author Amro Al-Akkad
 * 
 */
public class Req {

	/**
	 * Type of a requirement (mean or extreme).
	 */
	private RequirementType requirementType;

	/**
	 * Service name.
	 */
	private String serviceName;

	/**
	 * Value of a requirement.
	 */
	private double value;

	/**
	 * Not numeric value. This field is optional.
	 */
	private String notNumericValue;

	/**
	 * Name of a requirement.
	 */
	private String name;

	/**
	 * Percentage of a requirement.
	 */
	private double percentage;

	/**
	 * Unit of a requirement.
	 */
	private String unit;

	/**
	 * URI of a device.
	 */
	private String deviceURI;

	/**
	 * Persistent identifier of a device.
	 */
	private String devicePID;

	/**
	 * Flag indicating if the requirement is fulfilled or not.
	 */
	private boolean disqualified;

	/**
	 * Getter for type of requirement.
	 * 
	 * @return Returns the type of a requirement.
	 */
	public RequirementType getRequirementType() {
		return requirementType;
	}

	/**
	 * Sets the type of a requirement.
	 * 
	 * @param requirementType
	 *            Type of a requirement.
	 */
	public void setRequirementType(RequirementType requirementType) {
		this.requirementType = requirementType;
	}

	/**
	 * Getter for the name of a requirement.
	 * 
	 * @return Returns the name of a requirement.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of a requirement.
	 * 
	 * @param name
	 *            Name of requirement.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter for related service name of a requirement.
	 * 
	 * @return Returns the related service name.
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * Setter for related service name of a requirement.
	 * 
	 * @param serviceName
	 *            Related service name.
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * Getter for requirement value.
	 * 
	 * @return Returns the value.
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Setter for requirement value.
	 * 
	 * @param value
	 *            Value.
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * Getter for percentage of a requirement, i.e. how well the requirement is
	 * matched by a device/service.
	 * 
	 * @return Returns percentage.
	 */
	public double getPercentage() {
		return percentage;
	}

	/**
	 * Setter for percentage of a requirement.
	 * 
	 * @param percentage
	 *            Percentage.
	 */
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	/**
	 * Getter for the unit of a requirement.
	 * 
	 * @return Returns the unit of a requirement.
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * Setter for unit of a requirement.
	 * 
	 * @param unit
	 *            Unit of a requirement.
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * Setter for URI of a device.
	 * 
	 * @param deviceURI
	 *            URI of a device.
	 */
	public void setDeviceURI(String deviceURI) {
		this.deviceURI = deviceURI;
	}

	/**
	 * Getter for URI of a device.
	 * 
	 * @return Returns the URI of a device.
	 */
	public String getDeviceURI() {
		return deviceURI;
	}

	/**
	 * Setter fir device PID.
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
	 * Getter for unique ID.
	 * 
	 * @return Returns the Unique ID of the device and related service.
	 */
	public String getUniqueID() {
		return serviceName + "," + devicePID + ".";
	}

	/**
	 * Setter for <b>notNumericValue</b>.
	 * 
	 * @param notNumericValue
	 *            Indicates if the requirement is numeric or not.
	 */
	public void setNotNumericValue(String notNumericValue) {
		this.notNumericValue = notNumericValue;
	}

	/**
	 * Getter for <b>notNumericValue</b>.
	 * 
	 * @return Returns status if the requirement is numeric or not.
	 */
	public String getNotNumericValue() {
		return notNumericValue;
	}

	/**
	 * Getter for disqualified flag.
	 * 
	 * @return Returns the status of disqualified flag.
	 */
	public boolean isDisqualified() {

		return disqualified;
	}

	/**
	 * Setter for disqualified flag.
	 * 
	 * @param val
	 *            Flag indicating if the requirement is disqualified referring
	 *            to a device/service.
	 */
	public void setDisqualified(boolean val) {
		this.disqualified = val;
	}

}
