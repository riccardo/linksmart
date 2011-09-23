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

import java.util.Comparator;

/**
 * Compares instances of class Device according to its property values.
 * 
 * @author Amro Al-Akkad
 * 
 */
public class DeviceSortByPropertyValue implements Comparator<Device> {

	/**
	 * Property name to sort on.
	 */
	private final String desiredPropertyName;

	/**
	 * Order to sort on (ascending / descending).
	 */
	private final boolean ascending;

	/**
	 * Constructor of specific comparator to sort devices upon a specific
	 * property.
	 * 
	 * @param desiredPropertyName
	 *            Specific name of a property.
	 * @param ascending
	 *            The order to sort on. If TRUE is passed the sorting will be
	 *            ascending, if FALSE it will be descending.
	 */
	public DeviceSortByPropertyValue(String desiredPropertyName,
			boolean ascending) {
		super();
		this.desiredPropertyName = desiredPropertyName;
		this.ascending = ascending;
	}

	@Override
	public int compare(Device o1, Device o2) {

		double o1desiredPropertyValue, o2desiredPropertyValue;

		o1desiredPropertyValue = retrieveDesiredValue(o1);

		o2desiredPropertyValue = retrieveDesiredValue(o2);

		// for ascending order if 'less' or 'least' are set
		if (this.ascending) {
			return Double.compare(o1desiredPropertyValue,
					o2desiredPropertyValue);
		}
		// for descendant order, if 'more' or 'most' are set
		else {
			return Double.compare(o2desiredPropertyValue,
					o1desiredPropertyValue);
		}
	}

	/**
	 * Retrieves a value of a device property.
	 * 
	 * @param device
	 *            Device to examine.
	 * @return Returns the value as <b>Double</b>.
	 */
	private double retrieveDesiredValue(Device device) {

		OntologyResponseProperty[] deviceProperties = device.getProperties();

		for (OntologyResponseProperty deviceProperty : deviceProperties) {
			if (desiredPropertyName.contains(deviceProperty.getName())) {
				return Double.parseDouble(deviceProperty.getValue());
			}
		}

		// if not found check also service related properties

		OntologyResponseProperty[] serviceProperties =
				device.getServices()[0].getServiceProperties();

		for (OntologyResponseProperty serviceProperty : serviceProperties) {
			if (desiredPropertyName.contains(serviceProperty.getName())) {
				return Double.parseDouble(serviceProperty.getValue());
			}
		}

		// if device has not such a property,
		// assign -1 as an indicator for descendant (there won't be any negative
		// property values),
		// and largest double value for ascendant order.

		if (ascending) {
			return 1.797693134862315708145274237317e+308;
		} else {
			return -1;
		}

	}

}
