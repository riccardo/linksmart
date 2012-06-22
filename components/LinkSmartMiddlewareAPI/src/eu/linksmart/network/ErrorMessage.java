package eu.linksmart.network;

public class ErrorMessage extends Message {
	
	/**
	 * Indicates error receiving message
	 */
	public final static String RECEPTION_ERROR = "ReceptionError";
	/**
	 * Indicates error consuming message
	 */
	public final static String ERROR = "ERROR";
	
	public ErrorMessage(String topic, HID senderHID, HID receiverHID,
			byte[] data) {
		super(topic, senderHID, receiverHID, data);
	}
}
