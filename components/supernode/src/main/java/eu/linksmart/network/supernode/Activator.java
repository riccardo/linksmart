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
package eu.linksmart.network.supernode;

import java.io.IOException;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import eu.linksmart.tools.JarUtil;

public class Activator implements BundleActivator {
	
	private static Logger LOG = Logger.getLogger(Activator.class.getName());
	
	final static private String NMPROPPATH = "NetworkManagerSuperNode/config/NM.properties";
	final static private String NMPROPJARPATH = "config/NM.properties";
	final static private String SEEDSPATH = "NetworkManagerSuperNode/config/seeds.txt";
	final static private String SEEDSJARPATH = "config/seeds.txt";
	public static Hashtable<String, String> HashFilesExtract = new Hashtable<String, String>();

	LinksmartSuperNode superNode;
	
	static {
		HashFilesExtract.put(NMPROPPATH, NMPROPJARPATH);
		HashFilesExtract.put(SEEDSPATH, SEEDSJARPATH);	
	}
	
	public void start(BundleContext context) throws Exception {
		LOG.info("SuperNode is activating");
		try {
			LOG.info("extracting configuration files from Jar");
            JarUtil.extractFilesJar(HashFilesExtract);
            LOG.info("extracting configuration files is done");
        } catch (IOException e) {
           LOG.error(e.getMessage());
        }
		
		superNode = LinksmartSuperNode.getSingleton();
	}

	public void stop(BundleContext context) throws Exception {
		LOG.info("SuperNode is de-activating");
	}

}
