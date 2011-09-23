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


import eu.linksmart.qos.client.request.Requirement;

/**
 * 
 * This class serves to parse the QoS XML request, and builts on this the request
 * in order to retrieve values from ontology.
 * 
 * @author Amro Al-Akkad
 */
public class CreateOntologyXMLRequest {

	/**
	 * Constant referring to device.
	 */
	private static final String DEVICE = "device:";

	/**
	 * Constant referring to service.
	 */
	private static final String SERVICE = "service:";

	/**
	 * Build the request to retrieve values from ontology.
	 * 
	 * @param serviceQualities
	 *            That field actually describes a service query that could like
	 *            "service:hasCapability;service:measuresTemperature".
	 * @param requirements
	 *            Requirements can be services or device requirements. Service
	 *            requirements are formulated like
	 *            "service:hasInput/service:parameterUnit, service:serviceCost,"
	 *            , and device requirements like"device:hasHardware/hardware:hasDisplay/hardware:screenWidth,device:hasHardware/hardware:hasDisplay/hardware:screenHeight,device:hasEnergyProfile/energy:consumption/energy:modeAverage,"
	 *            .
	 * @return Returns an instance of <b>Query4Ontology</b>.
	 */
	public Query4Ontology buildRequest(String[] serviceQualities,
			Requirement[] requirements) {

		Query4Ontology query = null;

		// device query is empty, as QoS Manager concerns only services that are
		// deployed on devices
		final String deviceQuery = "";

		String serviceQuery = serviceQualities[0];
		StringBuffer serviceRequirementsBuf = new StringBuffer();
		StringBuffer deviceRequirementsBuf = new StringBuffer();

		for (int i = 0; i < requirements.length; i++) {
			Requirement requirement = requirements[i];
			String nfParam = requirement.getNfParameter();
			if (nfParam.startsWith(SERVICE)) {
				serviceRequirementsBuf.append(nfParam);
				serviceRequirementsBuf.append(',');
			} else if (nfParam.startsWith(DEVICE)) {
				deviceRequirementsBuf.append(nfParam);
				deviceRequirementsBuf.append(',');
			}
		}

		// NB: Last unnecessary comma does not affect query => removal is not
		// required.

		query =
				new Query4Ontology(serviceQuery, deviceQuery,
						serviceRequirementsBuf.toString(),
						deviceRequirementsBuf.toString());
		return query;
	}

}
