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
 * This class represents a detail in the QoS Xml-based response to the client
 * 
 * @author Amro Al-Akkad
 * 
 */
public class Detail {

	/**
	 * Parameter of a detail.
	 */
	private String parameter;

	/**
	 * Value of a detail.
	 */
	private String value;

	/**
	 * Unit of a detail.
	 */
	private String unit;

	/**
	 * Getter for parameter of a detail.
	 * 
	 * @return Returns the parameter of a detail.
	 */
	public String getParameter() {
		return parameter;
	}

	/**
	 * Sets the parameter of a detail.
	 * 
	 * @param parameter
	 *            Parameter of a detail.
	 */
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	/**
	 * Getter for value of a detail.
	 * 
	 * @return Returns the value of a detail.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value of a detail.
	 * 
	 * @param value
	 *            Value of a detail.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Getter for the unit of a detail.
	 * 
	 * @return Returns the unit of a detail.
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * Sets the unit of a detail.
	 * 
	 * @param unit
	 *            Unit of a detail.
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

}
