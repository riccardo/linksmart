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

import eu.linksmart.security.cryptomanager.SecurityLevel;
import eu.linksmart.utils.Configurator;
import org.osgi.service.cm.ConfigurationAdmin;

public class CryptoManagerConfigurator extends Configurator {

	/* Configuration PID & file path. */
	public final static String CM_PID = "eu.linksmart.security.cryptomanager";
	public final static String CONFIGURATION_FILE = "/configuration/CM.properties";
	
	public static final String DES_LOW = "DES.low";
	public static final String DES_MIDDLE = "DES.middle";
	public static final String DES_HIGH = "DES.high";
	
	public static final String DES_EDE_LOW = "DESede.low";
	public static final String DES_EDE_MIDDLE = "DESede.middle";
	public static final String DES_EDE_HIGH = "DESede.high";
	
	public static final String AES_LOW = "AES.low";
	public static final String AES_MIDDLE = "AES.middle";
	public static final String AES_HIGH = "AES.high";
	
	public static final String RSA_LOW = "RSA.low";
	public static final String RSA_MIDDLE = "RSA.middle";
	public static final String RSA_HIGH = "RSA.high";
	
	private CryptoManagerImpl cryptoManager;
	
	/**
	 * Constructor. Creates a new "NetworkManagerConfigurator" object
	 * 
	 * @param context the bundle's execution context
	 */
	public CryptoManagerConfigurator(CryptoManagerImpl cm, 
			BundleContext context) {
		
		super(context, Logger.getLogger(CryptoManagerConfigurator.class.getName()),
			CM_PID, CONFIGURATION_FILE);
		this.cryptoManager = cm;
	}
    public CryptoManagerConfigurator(CryptoManagerImpl cm,
                                     BundleContext context,ConfigurationAdmin configurationAdmin) {

        super(context, Logger.getLogger(CryptoManagerConfigurator.class.getName()),
                CM_PID, CONFIGURATION_FILE,configurationAdmin);
        super.init();
        this.cryptoManager = cm;
    }
	
	@Override
	public void applyConfigurations(Hashtable updates) {
		
	}
	
	public int getKeySize(SecurityLevel level, String algo){
		String value;
		if(level.equals(SecurityLevel.LOW)){
			value = (String)this.get(algo + "." + "low");
		} else if(level.equals(SecurityLevel.MIDDLE)){
			value = (String)this.get(algo + "." + "middle");
		} else {
			value = (String)this.get(algo + "." + "high");
		}
		return Integer.valueOf(value.trim());
	}

}
