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
package eu.linksmart.qos.client.request;

/**
 * 
 * This class is used to define requirements in request to QoS Manager
 * @author Amro Al-Akkad
 *
 */
public class Requirement {


	/**
	 * Nun-functional parameter of a requirement.
	 */
  private String nfParameter;
  
  /**
   * A specified value for the related standard, e.g. moreThan is standard and value would be 80, i.e. >80.
   */
  private String standardValue;
  
  /**
   * Standard of a requirement. 
   * A standard can be: most, more, moreThan, sameAs, lessThan, less, least, notNumeric.
   */
  private Standard standard;  
  
  /**
   * Constructor for creating a requirement.
   * @param nfparameter Non functional parameter.
   * @param standard Standard
   * @param standardvalue A specified value for the related standard
   */
  public Requirement(String nfparameter, Standard standard, String standardvalue) {
	super();
	this.nfParameter = nfparameter;
	this.standard = standard;
	this.standardValue = standardvalue;
}

  /**
   * Getter for nfparameter.
   * @return Returns non functional value of requirement, means it name.
   */
public String getNfParameter() {
	return nfParameter;
}

/**
 * Setter for nfparameter.
 * @param nfparameter Non functional parameter.
 */
public void setNfParameter(String nfparameter) {
	this.nfParameter = nfparameter;
}

/**
 * Getter for standard of requirement.
 * @return Returns the standard of a requirement.
 */
public Standard getStandard() {
	return standard;
}

/**
 * Sets the standard of a requirement.
 * @param standard Standard.
 */
public void setStandard(Standard standard) {
	this.standard = standard;
}

/**
 * Sets the value for a related standard of a requirement.
 * @param standardValue The value of a standard.
 */
public void setStandardValue(String standardValue) {
	this.standardValue = standardValue;
}

/**
 * Getter for the value for a related standard of a requirement.
 * @return Returns the value for a related standard.
 */
public String getStandardValue() {
	return standardValue;
}
}