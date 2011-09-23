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
package eu.linksmart.qos.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * This class is used for reading in XML files and return them as string.
 * 
 * @author Amro Al-Akkad
 * 
 */
public class Util {

	/**
	 * Logger.
	 */
	static final Logger LOG = Logger.getLogger(Util.class.getName());

	/**
	 * Reads in a file
	 * 
	 * @param filePath
	 *            Path of file to read in.
	 * @return A string representing a XML document
	 * @throws java.io.IOException
	 */
	public static String readFileAsString(final String filePath)
			throws java.io.IOException {
		byte[] buffer = new byte[(int) new File(filePath).length()];
		BufferedInputStream f = null;
		try {
			f = new BufferedInputStream(new FileInputStream(filePath));
			f.read(buffer);
		} finally {
			if (f != null) {
				try {
					f.close();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e.getCause());
				}
			}
		}
		return new String(buffer);
	}

	/**
	 * Reads in a XML doc and returns it as a string.
	 * 
	 * @param filePath
	 *            Path of file to read in.
	 * @return A string representing a XML document
	 */
	public final String readFileAndReturnAsString(final String filePath) {

		InputStream in = this.getClass().getResourceAsStream(filePath);
		BufferedInputStream f = new BufferedInputStream(in);

		byte[] buffer = null;
		try {
			buffer = new byte[(int) in.available()];
			f.read(buffer);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e.getCause());
		} finally {
			if (f != null) {
				try {
					f.close();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e.getCause());
				}
			}
		}

		return new String(buffer);
	}

}
