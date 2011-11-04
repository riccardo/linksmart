package eu.linksmart.security.communication.utils;

/***
 * Provides interface for cookie mechanism in core security.
 * Cookies change regularly to provide assurance that
 * message is no replay.
 * @author Vinkovits
 *
 */
public interface CookieProvider {
	
	//interval between changing cookies in seconds
	public static int GENERATION_INTERVAL = 60 * 2;
	//identifier of the SOAP header containing the cookie
	public static String COOKIE_PROPERTY_NAME = "SecCookie";
	public static String COOKIE_PREFIX = "linksmart";
	
	boolean checkCookie(String cookie);
	String getCookie();
}
