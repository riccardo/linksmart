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
package eu.linksmart.qos.test.client;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.clients.RemoteWSClientProvider;
import eu.linksmart.network.CryptoHIDResult;
import eu.linksmart.network.NetworkManagerApplication;
import eu.linksmart.qos.QoSManager;



public class Tester {
	
	private static final String QOS_TESTER_PATH = "/axis/services/QoSManagerTester";
	private static final String QOS_MANAGER_PID = "QoSManager:Otto";
	Logger LOG = Logger.getLogger(Tester.class.getName());
	private NetworkManagerApplication nm;
	
//	final String xmlQoSRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
//	"<request xmlns=\"http://qos.linksmart.eu\">" +
//	"<serviceQualities>" +
//	"<quality>service:hasCapability;service:playsVideo</quality>" +
//	"</serviceQualities>" +
//	"<requirements>" +
//	"<requirement>" +
//	"<property>device:hasHardware/hardware:hasDisplay/hardware:screenWidth</property>" +
//	"<standard>more</standard>" +
//	"</requirement>" +
//	"<requirement>" +
//	"<property>device:hasHardware/hardware:hasDisplay/hardware:screenHeight</property>" +
//	"<standard>more</standard>" +
//	"</requirement>" +
//	"<requirement>" +
//	"<property>device:hasEnergyProfile/energy:consumption/energy:modeAverage</property>" +
//	"<standard>least</standard>" +
//	"</requirement>" +
//	"<requirement>" +
//	"<property>service:serviceCost</property>" +
//	"<standard>less</standard>" +
//	"</requirement>" +
//	"<requirement>" +
//	"<property>service:hasInput/service:parameterUnit</property>" +
//	"<standard>notNumeric</standard>" +
//	"<value>unit:VideoAvi</value>" +
//	"</requirement>" +
//	"</requirements>" +
//	"</request>";
	
	private String myHID;
	private QoSManager qosManagerOSGi;
	private QoSManager qosManagerWS;
	
	public  String readFileAndReturnAsString(String filePath) {
		
		InputStream in = this.getClass().getResourceAsStream(filePath);
		BufferedInputStream f = new BufferedInputStream(in);
		
		byte[] buffer = null;
		try {
			 buffer = new byte[(int) in.available()];
	        f.read(buffer);
	    } catch (IOException e) {
			LOG.error(e.getMessage(),e.getCause());
		} finally {
	        if (f != null) try { f.close(); } catch (IOException ignored) { }
	    }
	    
		return new String(buffer);
	}

	protected void activate(ComponentContext context) throws IOException{
			
			String filePath = "/resources/qosExampleRequest.xml";
			String request = readFileAndReturnAsString(filePath);
			
			String result = null;
			this.qosManagerOSGi=getQoSManagerOSGi(context);
			try {
				if(qosManagerOSGi!=null){
//					System.out.println("request:\n");
//					System.out.println(request);
					result = this.qosManagerOSGi.
					getRankingList(request);
	//				getBestSuitableService(request);
//					getQoSProperties(request);
				}
				else {
					processViaLinkSmart(context);	
					result = qosManagerWS.getRankingList(request);
//					System.out.println("result:\n");
//					System.out.println("***");
//					System.out.println(result);
//					System.out.println("***");
				}
			}
			catch (RemoteException e) {
				LOG.error(e.getMessage(),e);
			}	
							
			System.out.println("\nresult:\n\n"+result);
		}

	private void processViaLinkSmart(ComponentContext context){
		RemoteWSClientProvider service = (RemoteWSClientProvider) context.locateService(RemoteWSClientProvider.class.getSimpleName());
		
		try {
				nm = 
					(NetworkManagerApplication) service
				.getRemoteWSClient(
					NetworkManagerApplication.class.getName(),
	//				"http://localhost:8082/axis/services/NetworkManagerApplication"
					null,
					false);
			
				createCryptoHID();
				
				// Get Remote QoSManager
				String qosManagerHID = getQoSManagerHID(myHID);
				
				
				//Change if QoSManager is running on remote machine
				String qosManagerIP="localhost";
				
				String targetUrlLinkSmartEventManager = 
					"http://" +qosManagerIP+ ":" + 
						System.getProperty("org.osgi.service.http.port")+ 
							"/SOAPTunneling/0"
								+ "/" + qosManagerHID + 
									"/0/hola";
			
			this.qosManagerWS =  
				(QoSManager) service.getRemoteWSClient(QoSManager.class.getName(), targetUrlLinkSmartEventManager, false);
			}
			catch (IOException e) {
				LOG.error(e.getMessage());
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}
		}
	
	protected void deactivate(ComponentContext context) {
		
		if(this.qosManagerOSGi!=null)
			this.qosManagerOSGi=null;
		
		if(this.qosManagerWS!=null){
			try {
				this.nm.removeHID(myHID);
				this.myHID=null;
				this.nm = null;
			} catch (RemoteException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
	
	private QoSManager getQoSManagerOSGi(ComponentContext context){
		
		return (QoSManager) context.locateService(QoSManager.class.getSimpleName());		
	}

	private String getQoSManagerHID(String requesterHID) {
		String[] results = null;
		long maxTime = 10000;
		int maxResponses = 1;
		String query = "((PID=="+QOS_MANAGER_PID+"))";
		try {
			results = nm.getHIDByAttributes(requesterHID, null, query, maxTime,
					maxResponses);
		} catch (IOException e) {
			LOG.error("Cannot find HID for QoSManager for query: " + query);
		}
		return results[0];
	
	}
	
	public String getHello(){
				
		return "Hello I'm a QoSManager tester.";
	}

	private String createCryptoHID() throws IOException {
		
		final String pid = "QoSTester:LinkSmart";
		
		String xmlAttributes = getXMLAttributeProperties(pid, "QoSTesterPort", pid);
		CryptoHIDResult result = nm.createCryptoHID(xmlAttributes, 
				"http://localhost:"+System.getProperty("org.osgi.service.http.port")+ QOS_TESTER_PATH);
		this.myHID = result.getHID();
		
		return myHID;
	}

	private String getXMLAttributeProperties(String pid, String sid, String desc)throws IOException
	{
		Properties descProps = new Properties();
		descProps.setProperty(NetworkManagerApplication.PID, pid);
		descProps.setProperty(NetworkManagerApplication.DESCRIPTION, desc);
		descProps.setProperty(NetworkManagerApplication.SID, sid);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		descProps.storeToXML(bos, "");
		return bos.toString();
	}
	
}
