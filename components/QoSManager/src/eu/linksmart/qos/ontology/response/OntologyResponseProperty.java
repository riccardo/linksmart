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

/**
 * Models a response of the ontology manager.
 * 
 * @author Amro Al-Akkad
 *
 */
public class OntologyResponseProperty {
	
	/**
	 * Name of a property.
	 */
	private final String name;
	
	/**
	 * Used to describe if the value of a property is numeric or not
	 */
	private PropertyType propertyType;
	
	/**
	 * Unit of a property. This is not always necessary specified in ontology.
	 */
	private final String unit;

	/**
	 * Represents the value of a property.
	 */
	private final String value;

	/**
	 * Constructor for creating a property.
	 * @param name Name of a property.
	 * @param unit Unit of a property. This is not always necessary specified in ontology.
	 * @param value Value of a property.
	 */
	public OntologyResponseProperty(String name, String unit, String value) {
		super();
		this.name = name;
		this.unit = unit;
		this.value = value;
		this.propertyType = PropertyType.NUMERIC;
	}

	/**
	 * Getter for name.
	 * @return Returns the name of a property.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter for unit.
	 * @return Returns the unit of a property.
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * Getter for value.
	 * @return Returns the value of a property.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Setter for property.
	 * @param propertyType Property type to set.
	 */
	public void setPropertyType(PropertyType propertyType) {
		this.propertyType = propertyType;
	}

	/**
	 * Getter for property type.
	 * @return Returns the type (numeric or not numeric) of a property.
	 */
	public PropertyType getPropertyType() {
		return propertyType;
	}

}
