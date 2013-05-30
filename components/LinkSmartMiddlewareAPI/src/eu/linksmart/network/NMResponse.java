/**
 * NMResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.network;

public class NMResponse  implements java.io.Serializable {
	public static final int STATUS_SUCCESS = 0;
	public static final int STATUS_ERROR = 1;
	public static final int STATUS_TIMEOUT = 2;
	
    private java.lang.String message;
    private byte[] messageBytes;
    private Message messageObject;
    private int status;

    public NMResponse() {
    }

    public NMResponse(int status) {
           this.status = status;
    }
    
    public Message getMessageObject() {
    	return messageObject;
    }
    
    public void setMessageObject(Message msg) {
    	messageObject = msg;
    }

    /**
     * Gets the message value for this NMResponse.
     * 
     * @return message if no msg object else null
     */
    public java.lang.String getMessage() {
        return message;
    }


    /**
     * Sets the message value for this NMResponse.
     * 
     * @param message
     */
    public void setMessage(java.lang.String message) {
        this.message = message;
    }
    
    public void setMessageBytes(byte[] messageBytes) {
    	this.messageBytes = messageBytes;
    }
    
    public byte[] getMessageBytes() {
    	return this.messageBytes;
    }


    /**
     * Gets the status value for this NMResponse.
     * 
     * @return status
     */
    public int getStatus() {
        return status;
    }


    /**
     * Sets the status value for this NMResponse.
     * 
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof NMResponse)) return false;
        NMResponse other = (NMResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.message==null && other.getMessage()==null) || 
             (this.message!=null &&
              this.message.equals(other.getMessage()))) &&
            this.status == other.getStatus();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getMessage() != null) {
            _hashCode += getMessage().hashCode();
        }
        _hashCode += getStatus();
        __hashCodeCalc = false;
        return _hashCode;
    }

}
