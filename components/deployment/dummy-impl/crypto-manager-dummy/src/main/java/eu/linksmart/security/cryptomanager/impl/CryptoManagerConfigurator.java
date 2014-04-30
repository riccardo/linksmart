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
package eu.linksmart.security.cryptomanager.impl;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import eu.linksmart.utils.Configurator;
import org.osgi.service.cm.ConfigurationAdmin;

public class CryptoManagerConfigurator extends Configurator {

	/* Configuration PID & file path. */
	public final static String CM_PID = "eu.linksmart.security.cryptomanager";
	public final static String CONFIGURATION_FILE = "/configuration/CM.properties";
	
	private CryptoManagerImplDummy cryptoManager;
	
	public CryptoManagerConfigurator(CryptoManagerImplDummy cm, BundleContext context) {
		super(context, Logger.getLogger(CryptoManagerConfigurator.class.getName()), CM_PID, CONFIGURATION_FILE);
		this.cryptoManager = cm;
	}
	
    public CryptoManagerConfigurator(CryptoManagerImplDummy cm, BundleContext context,ConfigurationAdmin configurationAdmin) {
        super(context, Logger.getLogger(CryptoManagerConfigurator.class.getName()), CM_PID, CONFIGURATION_FILE, configurationAdmin);
        super.init();
        this.cryptoManager = cm;
    }
	
	@Override
	public void applyConfigurations(Hashtable updates) {
	}
	
}
