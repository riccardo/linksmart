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
package eu.linksmart.qos;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The interface for the Quality-of-Service Component.<p> Provides
 * functionalities for retrieving best-suitable services by computing a ranking
 * list while taking into consideration a set of mean requirements (more, less)
 * and optionally a single extreme requirement (most, least) for device/service
 * properties.
 * 
 * @author Amro Al-Akkad
 * 
 */
public interface QoSManager extends Remote {

	/**
	 * This method expects a XML string as input and returns a XML string as
	 * output as well.
	 * 
	 * @param xmlString
	 *            this string must represent an XML document, for instance
	 * 
	 *            <?xml version="1.0" encoding="UTF-8" standalone="no"?>
	 *            <request xmlns="http://qosmanager.linksmart.eu\">
	 *            <serviceQualities> <quality>qos:playsvideo</quality>
	 *            </serviceQualities> <serviceInputQualities> <input>avi</input>
	 *            </serviceInputQualities> <serviceOutputQualities> <output/>
	 *            </serviceOutputQualities> <requirements> <requirement>
	 *            <property>Resolution</property> <standard>most</standard>
	 *            <value/> </requirement> <requirement>
	 *            <property>Colors</property> <standard>more</standard> <value/>
	 *            </requirement> <requirement> <property>Screensize</property>
	 *            <standard>more</standard> <value/> </requirement>
	 *            <requirement> <property>PowerConsumption</property>
	 *            <standard>less</standard> <value/> </requirement>
	 *            </requirements> </request>
	 * 
	 * @return this will return an xml string, for instance <?xml version="1.0"
	 *         encoding="UTF-8" standalone="no"?> <ResultList
	 *         xmlns:urn="linksmart:qos" urn:eu.linksmartmiddleware.qos=""> <Rank>
	 *         <Position>1</Position> <ServiceName>playVideo1</ServiceName>
	 *         <HID>playVideo1HID</HID> <Rate>5</Rate>
	 *         <AveragePercentage>75%</AveragePercentage> <Details> <Detail>
	 *         <Property>Resolution</Property> <Value>1024*768</Value>
	 *         <Unit>pixels</Unit> <Percentage>90%</Percentage> </Detail>
	 *         <Detail> <Property>ScreenSize</Property> <Value>34</Value>
	 *         <Unit>inches</Unit> <Percentage>40%</Percentage> </Detail>
	 *         <Detail> <Property>PowerConsumption</Property> <Value>56</Value>
	 *         <Unit>watts</Unit> <Percentage>100%</Percentage> </Detail>
	 *         <Detail> <Property>Colors</Property> <Value>256</Value>
	 *         <Unit>dots</Unit> <Percentage>70%</Percentage> </Detail>
	 *         </Details> </Rank> </ResultList>
	 * @throws RemoteException
	 */
	String getRankingList(String xmlString) throws RemoteException;

	/**
	 * see similar to get ranking list for input and output. Optimally this
	 * method reuses getRankingList method.
	 * 
	 * @param xmlString
	 *            this string must represent an XML document
	 * @return will return a XML
	 * @throws RemoteException
	 */
	String getBestSuitableService(String xmlString) throws RemoteException;

	/**
	 * Retrieves specific QoS properties from LinkSmart ontology
	 * 
	 * @param query
	 *            Query to retrieve specific QoS properties from LinkSmart ontology
	 * @return the result of querying ontology.
	 * @throws RemoteException
	 */
	String getQoSProperties(String query) throws RemoteException;
}
