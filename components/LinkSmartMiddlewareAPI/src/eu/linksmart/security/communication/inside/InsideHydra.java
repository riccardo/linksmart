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
package eu.linksmart.security.communication.inside;


/**
 * This interface defines methods to be used by Inside Hydra security.
 * 
 * @author Junaid Khan
 * 
 */

//InsideHydraCommunication helper methods INTERFACES

public interface InsideHydra {
	
	public static final short CONF_ENC=1;
	public static final short CONF_NULL=0;
	public static final short CONF_ENC_SIG_SPORADIC=2;
	public static final short CONF_ENC_SIG=3;
	public static final String INSIDE_SECURITY_NAMESPACE = "http://linksmart.eu/ns/security/inside";
	public static final String INSIDE_SIGNED_MESSAGE_NAMESPACE = "http://linksmart.eu/ns/security/inside_sig";
	public static final String INSIDE_PROTECTED_MESSAGE_NAME = "linksmart:InsideProtectedMessage";
	public static final String INSIDE_SIGNED_MESSAGE_NAME = "linksmart:InsideSignedProtectedMessage";
	public static final String INSIDE_NONCE_ELEMENT = "linksmart:InsideNonce";
	public static final String INSIDE_CONTENT_ELEMENT = "linksmart:InsideContent";
	public static final String INSIDE_PROTECTED_ELEMENT = "linksmart:InsideProtected";
	
	
	public String protectInsideHydra(String encstr, String receiverHID) throws Exception;

	public String unprotectInsideHydra(String encstr) throws Exception;

	public String asymEncrypt(String encstr, String receiverHID) throws Exception;

	public String AsDecrypt(String encrData) throws Exception;

	public boolean AsVerify(String message) throws Exception;
	
	public String asymSign(String string) throws Exception;
	
	
}