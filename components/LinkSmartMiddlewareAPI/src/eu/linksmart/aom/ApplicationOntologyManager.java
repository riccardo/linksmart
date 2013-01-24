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
package eu.linksmart.aom;

import java.rmi.Remote;

/**
 * Application Ontology Manager WebService interface.
 * 
 * @author Peter Kostelnik
 */

public interface ApplicationOntologyManager extends Remote {

	public String sparql(String query) throws java.rmi.RemoteException;

	public String createDeviceTemplate(String scpd) throws java.rmi.RemoteException;

	public String resolveDevice(String disco) throws java.rmi.RemoteException;

	public String assignPID(String deviceURI, String pid) throws java.rmi.RemoteException;

	public String removeDevice(String deviceURI) throws java.rmi.RemoteException;

	public boolean assignDiscoveryInfo(String deviceURI, String discovery)
	throws java.rmi.RemoteException;

	public boolean assignEventModel(String deviceURI, String eventModel)
	throws java.rmi.RemoteException;

	public boolean assignEnergyProfile(String deviceURI, String energyProfile)
	throws java.rmi.RemoteException;

	public boolean assignConfiguration(String deviceURI, String configuration)
	throws java.rmi.RemoteException;

	public String getConfigurations() throws java.rmi.RemoteException;

	public String getDeviceTypes() throws java.rmi.RemoteException;

	public String getDevicesWithServices(String serviceQuery, String deviceQuery, String deviceRequirements, String serviceRequirements) throws java.rmi.RemoteException;

	public String getDevices(String deviceQuery, String deviceRequirements) throws java.rmi.RemoteException;

	// ================================================================
	// METHODS USED IN AOM IDE
	// ================================================================

	public String getPropertyAnnotationModel() throws java.rmi.RemoteException;

	public String getDeviceTree() throws java.rmi.RemoteException;

	public String getTree(String classURI, boolean includeInstances) throws java.rmi.RemoteException;

	public String getInstanceTree(String instanceURI) throws java.rmi.RemoteException;

	public String getClassLiterals(String classURI) throws java.rmi.RemoteException;

	public String addValue(String sURI, String pURI, String value, String dataType, boolean append) throws java.rmi.RemoteException;

	public String addValue(String sURI, String pURI, String oURI, boolean append) throws java.rmi.RemoteException;

	public String addFormData(String xml, boolean append) throws java.rmi.RemoteException;

	public String remove(String sURI, String pURI, String oURI) throws java.rmi.RemoteException;

	public String remove(String sURI, String pURI, String value, String dataTypeURI) throws java.rmi.RemoteException;

	public String getDevicesWithServices(String serviceQuery) throws java.rmi.RemoteException;

	public String getDevices(String deviceQuery) throws java.rmi.RemoteException;

	public String getSCPD(String deviceURI) throws java.rmi.RemoteException;

	public String dump() throws java.rmi.RemoteException;

	public boolean clean() throws java.rmi.RemoteException;

	public boolean update(String xml) throws java.rmi.RemoteException;

	public boolean createTestingRuntimeClone(String templateURI, String pid) throws java.rmi.RemoteException;

	public boolean updateValue(String deviceURI, String path) throws java.rmi.RemoteException;

	public boolean removeRunTimeDevices() throws java.rmi.RemoteException;  
}

