package eu.linksmart.network.backbone.impl.jxta;

import org.apache.log4j.Logger;

/**
 * Collection of util methods that will be used within the JXTA backbone classes
 * 
 * @author Schneider
 *
 */
public class BackboneJXTAUtils {
	
	private static Logger logger = Logger
			.getLogger(BackboneJXTAUtils.class.getName());


	/**
	 * Convert String to ByteArray.
	 * 
	 * @param s
	 * @return 
	 */
	public static byte[] ConvertStringToByteArray(String s) {
		try {
			return s.getBytes();
		} catch (Exception e) {
			logger.error("ConvertStringToByteArray with null string");
			return new byte[0];
		}
	}

	/**
	 * Convert byte array to String. Could be useful e.g. for logging purposes.
	 * 
	 * @param b
	 * @return
	 */
	public static String ConvertByteArrayToString(byte[] b) {
		try {
			return new String(b);
		} catch (Exception e) {
			logger.error("ConvertByteArrayToString with null byte-array");
			return "";
		}
	}

	
}
