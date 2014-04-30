package eu.linksmart.network.backbone.impl.jxta;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import eu.linksmart.network.VirtualAddress;

/**
 * Collection of util methods that will be used within the JXTA backbone classes
 * 
 * @author Schneider
 * 
 */
public class BackboneJXTAUtils {

	private static Logger logger = Logger.getLogger(BackboneJXTAUtils.class
			.getName());

	/**
	 * Adds an VirtualAddress to the data package so that the next JXTA backbone can get it
	 * without the need to unpack the payload.
	 * 
	 * @param aVirtualAddress
	 *            This is the VirtualAddress of the sender.
	 * @param origData
	 *            byte array of the original data
	 * @return
	 */
	public static byte[] AddVirtualAddressToData(VirtualAddress aVirtualAddress, byte[] origData) {
		if (aVirtualAddress == null) {
			throw new IllegalArgumentException("VirtualAddress must not be NULL!");
		}
		ByteBuffer aBuffer = ByteBuffer.allocate(VirtualAddress.VIRTUAL_ADDRESS_BYTE_LENGTH
				+ origData.length);
		aBuffer.position(0);
		aBuffer.put(aVirtualAddress.getBytes());
		aBuffer.put(origData);
		return aBuffer.array();
	}

	/**
	 * Gets the VirtualAddress of the sender of the data package.
	 * 
	 * @param origData
	 * @return The VirtualAddress from the data package.
	 */
	public static VirtualAddress GetVirtualAddressFromData(byte[] origData) {
		byte[] ret = new byte[VirtualAddress.VIRTUAL_ADDRESS_BYTE_LENGTH];
		GetBytes(origData, 0, VirtualAddress.VIRTUAL_ADDRESS_BYTE_LENGTH, ret, 0);
		return new VirtualAddress(ret);
	}

	/**
	 * Removes the VirtualAddress from the data package so that the payload can be
	 * processed regularly e.g. unencrypted.
	 * 
	 * @param origData
	 * @return The original payload of the data package.
	 */
	public static byte[] RemoveVirtualAddressFromData(byte[] origData) {
		int lengthWithoutVirtualAddress = origData.length - VirtualAddress.VIRTUAL_ADDRESS_BYTE_LENGTH;
		byte[] ret = new byte[lengthWithoutVirtualAddress];
		GetBytes(origData, VirtualAddress.VIRTUAL_ADDRESS_BYTE_LENGTH, origData.length, ret, 0);
		return ret;
	}

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

	/**
	 * Copies bytes from the source byte array to the destination array
	 * 
	 * @param source
	 *            The source array
	 * @param srcBegin
	 *            Index of the first source byte to copy
	 * @param srcEnd
	 *            Index after the last source byte to copy
	 * @param destination
	 *            The destination array
	 * @param dstBegin
	 *            The starting offset in the destination array
	 */
	private static void GetBytes(byte[] source, int srcBegin, int srcEnd,
			byte[] destination, int dstBegin) {
		System.arraycopy(source, srcBegin, destination, dstBegin, srcEnd
				- srcBegin);
	}

}
