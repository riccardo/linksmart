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
package eu.linksmart.network.supernode;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;




import org.apache.log4j.Logger;

import eu.linksmart.tools.JarUtil;
import eu.linksmart.tools.PropSkip;
import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.exception.PeerGroupException;
import net.jxta.logging.Logging;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.rendezvous.RendezvousEvent;
import net.jxta.rendezvous.RendezvousListener;

public class LinksmartSuperNode implements RendezvousListener, DiscoveryListener {

	private static Logger logger = Logger.getLogger(LinksmartSuperNode.class.getName());

    static private LinksmartSuperNode linksmartSuperNode = null;	
	private PropSkip propSkip;
	public String name;
	public String description;
	public String jxtaHome;
	private NetworkConfigurator configurator;
	private  NetworkManager manager;
	private PeerGroup netPeerGroup;
	
	private LinksmartSuperNode() {
		logger.info("initializing supernode singelton");
		
		try {
			logger.info("reading configuration options");
			propSkip = new PropSkip();
			this.name = propSkip.getProperty("name");
			this.description = propSkip.getProperty("description");
			//this.jxtaHome = "NetworkManagerSuperNode/.jxta/" + this.name;
			this.jxtaHome = "NetworkManagerSuperNode/.jxta/" + this.name;
			if (propSkip.getProperty("JXTALogs").equalsIgnoreCase("OFF")) {
				System.setProperty(Logging.JXTA_LOGGING_PROPERTY, Level.OFF.toString()); 
			}
			if (propSkip.getProperty("JXTALogs").equalsIgnoreCase("ON")) {
				System.setProperty(Logging.JXTA_LOGGING_PROPERTY, Level.FINEST.toString()); 
			}
			logger.info("reading configuration options is done");
        } catch (Exception e) {
           logger.error("unable to read configuration options: " + e.getMessage());
        }
		
		logger.info("starting JXTA engine");
		startJXTA();
		logger.info("started JXTA engine successfully");
		
	}
	
    static synchronized public LinksmartSuperNode getSingleton() {

        if (linksmartSuperNode == null) {
            linksmartSuperNode = new LinksmartSuperNode();
        }
        return linksmartSuperNode;
    }
    
	//Clear the advertisement cache before starting the JXTA platform
	private void clearCache(final File rootDir) {
		try {
				if (rootDir.exists()) {
					File[] list = rootDir.listFiles();
						for (File aList : list) {
							if (aList.isDirectory()) {
								clearCache(aList);
								} 
							else {
								aList.delete();
								}
						}
		         	}
		         rootDir.delete();
		         logger.info("Cache component " + rootDir.toString() + " cleared.");
		 }
		catch (Throwable t) {
			logger.error("Unable to clear " + rootDir.toString());
			t.printStackTrace();
		}
	}
	
	public void startJXTA() {
		
		clearCache(new File(jxtaHome,"cm"));
		 
		 //JXTA platform configuration---------------------------------------------
		try {
			manager = new NetworkManager(NetworkManager.ConfigMode.SUPER, name, new File(jxtaHome).toURI());
		} catch (Exception e1) {
			logger.error("unable to instantiate JXTA NetworkManager: " + e1.getMessage());
		}
		
		//Set the JXTA configuration parameters
		
		try {
			
			configurator = manager.getConfigurator();

            // fix - disable dynamics ports
            configurator.setTcpEndPort(-1);
            configurator.setTcpStartPort(-1);
			
			configurator.setDescription(description);
		   	configurator.setTcpPublicAddress(propSkip.getProperty("extAddrTcp"), true);
	   		configurator.setHttpPublicAddress(propSkip.getProperty("extAddrHttp"), true);
	   		
	   		int httpPort = Integer.parseInt(propSkip.getProperty("extAddrHttp").split(":")[1]);
	   		int tcpPort = Integer.parseInt(propSkip.getProperty("extAddrTcp").split(":")[1]);
		   	
	   		configurator.setTcpPort(tcpPort);
		   	configurator.setHttpPort(httpPort);
			
		   	URI uri = new File("NetworkManagerSuperNode/config/seeds.txt").toURI();
			
			configurator.addRdvSeedingURI(uri);
		   	configurator.addRelaySeedingURI(uri);
		   	
		   	if (propSkip.getProperty("multicast").equalsIgnoreCase("ON"))
		   		configurator.setUseMulticast(true);
		   	else 
		   		configurator.setUseMulticast(false);
		   	
		   	//logger.info("plateform-config: " + configurator.getPlatformConfig().toString());
		   	
	   	} catch (NullPointerException e) {
	   		logger.error("Wrong configuration mode + " + e.getMessage());
	   	} catch (Exception e) {
	   		logger.error("unable to configure JXTA configurator: " + e.getMessage());
		}
	   	  
	   	try {
	   		logger.info("savig the JXTA configurations");
			configurator.save();
		} catch (Exception e) {
			logger.error("unable to save JXTA configuration: " + e.getMessage());
		}
	   	
	   	try {
	   		logger.info("starting JXTA-network");
			manager.startNetwork();
		} catch (PeerGroupException e1) {
			logger.error("unable to start JXTA network: " + e1.getMessage());
		} catch (Exception e1) {
			logger.error("unable to start JXTA network: " + e1.getMessage());
		}
	   	
		netPeerGroup= manager.getNetPeerGroup();
		   
		netPeerGroup.getRendezVousService().startRendezVous();
		netPeerGroup.getRendezVousService().addListener(this);
	}
	
	public void stopJXTA() {
		logger.info("stopping JXTA engine");
	}
	
	public void rendezvousEvent(RendezvousEvent event) {
	
		String eventDescription;
		  
		int    eventType;
		eventType = event.getType();  
		switch( eventType ) {
		  	case RendezvousEvent.RDVCONNECT:
		  			eventDescription = "RDVCONNECT";
		  			break;
		  	case RendezvousEvent.RDVRECONNECT:
		  			eventDescription = "RDVRECONNECT";
		  			break;
		  	case RendezvousEvent.RDVDISCONNECT:
		           	eventDescription = "RDVDISCONNECT";
		           	break;
		    case RendezvousEvent.RDVFAILED:
		    		eventDescription = "RDVFAILED";
		    		break;
		    case RendezvousEvent.CLIENTCONNECT:
		    		eventDescription = "CLIENTCONNECT";
		    		break;
		    case RendezvousEvent.CLIENTRECONNECT:
		    		eventDescription = "CLIENTRECONNECT";
		    		break;
		    case RendezvousEvent.CLIENTDISCONNECT:
		    		eventDescription = "CLIENTDISCONNECT";
		    		break;
		    case RendezvousEvent.CLIENTFAILED:
		    		eventDescription = "CLIENTFAILED";
		    		break;
		    case RendezvousEvent.BECAMERDV:
		    		eventDescription = "BECAMERDV";
		    		break;
		    case RendezvousEvent.BECAMEEDGE:
		    		eventDescription = "BECAMEEDGE";
		    		break;
		    default:
		    		eventDescription = "UNKNOWN RENDEZVOUS EVENT";
		   }
		
		logger.info("RendezvousEvent:  Event =  " + eventDescription + " from peer = " + event.getPeer());
	       
	}

	public void discoveryEvent(DiscoveryEvent arg0) {
	}
	
}
