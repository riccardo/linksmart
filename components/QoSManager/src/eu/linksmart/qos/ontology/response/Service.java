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
 * This class represents a service that a device runs retrieved from ontology.
 * 
 * @author Amro Al-Akkad
 * 
 */
public class Service {

	/**
	 * Properties a service contains.
	 */
	private OntologyResponseProperty[] serviceProperties;

	/**
	 * Service operation.
	 */
	private String operation;

	/**
	 * Constructor expecting two parameters to fill field.
	 * 
	 * @param operation
	 *            Name of the service operation.
	 * @param serviceProperties
	 *            Array containing service properties.
	 */
	public Service(String operation,
			OntologyResponseProperty[] serviceProperties) {
		super();
		this.operation = operation;
		this.serviceProperties =
				Arrays.copyOfRange(serviceProperties, 0,
						serviceProperties.length);
	}

	/**
	 * Getter for service properties.
	 * 
	 * @return Returns an array containing all service properties.
	 */
	public OntologyResponseProperty[] getServiceProperties() {
		OntologyResponseProperty[] servicePropertiesArray =
				Arrays.copyOfRange(serviceProperties, 0,
						serviceProperties.length);

		return servicePropertiesArray;
	}

	/**
	 * Getter for retrieving service operation.
	 * 
	 * @return Returns the name of the service operation.
	 */
	public String getOperation() {
		return operation;
	}

}
