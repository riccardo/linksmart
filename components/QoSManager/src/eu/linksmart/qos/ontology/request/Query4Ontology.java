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
package eu.linksmart.qos.ontology.request;

/**
 * This class serves to represent a query to send to ontology.
 * 
 * @author Amro Al-Akkad
 *
 */
public class Query4Ontology {

	/**
	 * Query to retrieve a set of services from ontology.
	 */
	private final String serviceQuery;

	/**
	 * Query to retrieve a set of devices from ontology.
	 */
	private final String deviceQuery;

	/**
	 * Requirements the services matching the service query need to fulfill.
	 */
	private final String serviceRequirements;

	/**
	 * Requirements the devices matching the service query need to fulfill.
	 */
	private final String deviceRequirements;

	/**
	 * Constructor for creating a query in order to retrieve devices and its services from ontology 
	 * while considering service and device requirements.
	 * @param serviceQuery Query for retrieving services.
	 * @param deviceQuery Query for retrieving devices.
	 * @param serviceRequirements Requirements for services.
	 * @param deviceRequirements Requirements for devices.
	 */
	public Query4Ontology(String serviceQuery, String deviceQuery,
			String serviceRequirements, String deviceRequirements) {
		super();
		this.serviceQuery = serviceQuery;
		this.deviceQuery = deviceQuery;
		this.serviceRequirements = serviceRequirements;
		this.deviceRequirements = deviceRequirements;
	}

	/**
	 * Getter for service query.
	 * 
	 * @return Returns the service query.
	 */
	public String getServiceQuery() {
		return serviceQuery;
	}

	/**
	 * Getter for device query.
	 * 
	 * @return Returns the device query.
	 */
	public String getDeviceQuery() {
		return deviceQuery;
	}

	/**
	 * Getter for service requirements.
	 * 
	 * @return Returns the service requirements.
	 */
	public String getServiceRequirements() {
		return serviceRequirements;
	}

	/**
	 * Getter for device requirements.
	 * 
	 * @return Returns the device requirements.
	 */
	public String getDeviceRequirements() {
		return deviceRequirements;
	}

}
