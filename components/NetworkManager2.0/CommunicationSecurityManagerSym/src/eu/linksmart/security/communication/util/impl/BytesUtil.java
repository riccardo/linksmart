package eu.linksmart.security.communication.util.impl;

import java.util.Formatter;

public class BytesUtil {

	public static byte[] extractBytes(byte[] from, int sizeBits, int offsetBits){
		int size = sizeBits / 8;
		int offset = offsetBits / 8;
		byte[] temp = new byte[size];
		for(int i=0; i<size; i++){
			temp[i] = from[offset + i];
		}		
		return temp;
	}

	/**
	 * Return a printable hex representation of the array
	 * @param bytes
	 * @return
	 */
	public static String printBytes(byte[] bytes){
		if(bytes != null){
			StringBuilder sb = new StringBuilder();
			Formatter format = new Formatter();
			for(int i=0; i<bytes.length; i++){
				sb.append(format.format("%02X ", bytes[i]));
			}
			return sb.toString(); 
		}
		return null;
	}
	
	public static boolean checkEquality(byte[] a, byte[] b){
		boolean equal = true;
		//check equality of size
		if(a.length != b.length) return false;
		//check elements
		for(int i=0; i<a.length; i++){
			if(a[i]!=b[i]){
				equal = false;
				break;
			}
		}
		
		return equal;
	}
}
