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
package eu.linksmart.aom.impl;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import eu.linksmart.utils.Configurator;


public class OntologyManagerConfigurator extends Configurator{
	/* Configuration PID & file path. */
	public final static String OM_PID = "eu.linksmart.ontologymanager";
	public final static String CONFIGURATION_FILE = "/resources/OM.properties";

	public static final String PID = "OntologyManager.PID";
	public static final String USE_NETWORK_MANAGER = "OntologyManager.useNetworkManager";
	public static final String NETWORK_MANAGER_ADDRESS = "OntologyManager.NetworkManagerAddress";
	public static final String CERTIFICATE_REF = "OntologyManager.CertificateReference";

	private ApplicationOntologyManagerSoapBindingImpl ontologyManager;

	/**
	 * Constructor. Creates a new "NetworkManagerConfigurator" object
	 * 
	 * @param trustManager
	 *            the network manager implementation
	 * @param context
	 *            the bundle's execution context
	 */
	public OntologyManagerConfigurator(ApplicationOntologyManagerSoapBindingImpl ontologyManager,
			BundleContext context) {

		super(context, Logger.getLogger(OntologyManagerConfigurator.class
				.getName()), OM_PID, CONFIGURATION_FILE);
		this.ontologyManager = ontologyManager;
	}

	/**
	 * Apply the configuration changes
	 * 
	 * @param updates
	 *            the configuration changes
	 */
	@Override
	public void applyConfigurations(Hashtable updates) {
		this.ontologyManager.applyConfigurations(updates);
	}
}
