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
package eu.linksmart.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropSkip {
	private static final String CONFIGURATION_FILE = "NetworkManagerSuperNode/config/NM.properties";
	private HashMap properties;

	private static Logger logger = Logger.getLogger(PropSkip.class.getName());

	public PropSkip() {
		FileInputStream f = null;
		try {
			f = new FileInputStream(CONFIGURATION_FILE);

			Properties tempProperties = new Properties();

			tempProperties.load(f);

			f.close();
			properties = new HashMap(tempProperties);

		} catch (IOException e) {
			logger.error("Unable to load properties.", e);
		}

	}

	public String getProperty(String name) {
		String value = (String) properties.get(name);
		if (value == null)
			logger.error("Unknown property: " + name);
		return value;
	}
}
