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
package eu.linksmart.qos.client;

public interface QoSManagerService extends javax.xml.rpc.Service {
    public java.lang.String getQoSManagerAddress();

    public eu.linksmart.qos.QoSManager getQoSManager() throws javax.xml.rpc.ServiceException;

    public eu.linksmart.qos.QoSManager getQoSManager(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
