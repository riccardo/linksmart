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
package eu.linksmart.security.protocols.net;


/**
 * 
 * This class encapsulates most of the Constants used in this project.
 * 
 * @author Stephan Heuser - stephan.heuser@sit.fraunhofer.de
 *
 */

public class Consts {
    public static final int SERVER_TIMEOUT = 10000;
    public static final int CLIENT_TIMEOUT = 10000;
    
    public static final String OSGI = "osgi"; 

    protected static final int DEFAULT_PORT = 6001;
    protected static final int DEFAULT_SERVER_TIMEOUT = 30000;
    protected static final int DEFAULT_CLIENT_TIMEOUT = 30000;
    protected static final boolean DEFAULT_CLOSE_TABS_AFTER_DISCONNECT = false;

    public static final String HOSTNAME_PARAM = "hostname";
    public static final String PORT_PARAM = "port";
    public static final String FILEPART_PARAM = "filePart";
    public static final String LISTPART_PARAM = "listPart";
    public static final String FOLDER_ID_PARAM = "folderId";
    public static final String FILE_ID_PARAM = "fileId";
    public static final String OUTPUT_FILENAME_PARAM = "outputFilename";
    public static final String CLIENT_HOSTNAME_PARAM = "clientHostname";
    public static final String CLIENT_IP_PARAM = "clientIP";
    public static final String CLIENT_CONNECT_DATE_PARAM = "clientConnectDate";
    public static final String CLIENT_STATUS_PARAM = "clientStatus";
    
    public static final String SENDER_HID = "senderHid";
    public static final String RECEIVER_HID = "receiverHid";
    public static final String SESSION_ID = "sessionId";
    
    public static final int ROOT_FOLDER_ID = -1;
    public static final int FILE_PART_SIZE = 32768;
    public static final int LIST_PART_SIZE = 32768;
    
	public static final String SERVER = "server";
	public static final String CLIENT = "client";
	public static final String SERVEREVENTPROXYSERVICE = "SecureSessionServerEventProxy";
	public static final String CLIENTEVENTPROXYSERVICE = "SecureSessionServerEventProxy";
	public static final String TRUE = "true";
	public static final long CERTIFICATE_EXCHANGE_TIMEOUT = 30000;
    
}
