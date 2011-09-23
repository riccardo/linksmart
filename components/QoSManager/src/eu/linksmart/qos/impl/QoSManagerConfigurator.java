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
package eu.linksmart.qos.impl;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.utils.Configurator;

/**
 * 
 * Class to interface with the Hydar Manager Configurator The default
 * configuration file called QM.properties is found in resources/ before
 * deployment
 * 
 * @author Amro Al-Akkad
 * 
 */
public class QoSManagerConfigurator extends Configurator {

	/**
	 * Constant for retrieving persistent identifier of QoS Manager.
	 */
	public static final String PID = "QoSManager.PID";

	/**
	 * Constant pointing to Quality-of-Service Manager configuration file.
	 */
	public static final String QM_PID = "eu.linksmart.qos";

	/**
	 * Certificate Reference of Quality-of-Service Manager.
	 */
	public static final String CERT_REF = "QoSManager.CertificateReference";

	/**
	 * Constant for retrieving the related Network Manager address of
	 * QoSManager.
	 */
	public static final String NM_ADDRESS = "QoSManager.NetworkManagerAddress";

	/**
	 * Constant pointing to Quality-of-Service Manager configuration file.
	 */
	private static final String CONFIGURATION_FILE = "/resources/QM.properties";

	/**
	 * Constant indicating if Network Manager is supposed to be called via
	 * LinkSmart.
	 */
	public static final String USE_NETWORK_MANAGER =
			"QoSManager.useNetworkManager";

	/**
	 * Constant indicating if Network Manager is supposed to be called via OSGi.
	 */
	public static final String USE_NETWORK_MANAGER_OSGI =
			"QoSManager.useNetworkManagerOSGi";

	/**
	 * Reference to QoS manager instance.
	 */
	private final QoSManagerImpl qosManager;

	/**
	 * Constructor for QoS Manager related configuration.
	 * 
	 * @param context
	 *            Service component context reference.
	 * @param component
	 *            OSGi component instance of QoSManager.
	 */
	public QoSManagerConfigurator(ComponentContext context,
			QoSManagerImpl component) {
		super(context.getBundleContext(), Logger
				.getLogger(QoSManagerConfigurator.class.getName()), QM_PID,
				CONFIGURATION_FILE);
		this.qosManager = component;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void applyConfigurations(Hashtable updates) {
		this.qosManager.applyConfigurations(updates);

	}

}
