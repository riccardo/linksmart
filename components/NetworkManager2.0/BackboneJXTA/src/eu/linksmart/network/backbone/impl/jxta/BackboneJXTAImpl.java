package eu.linksmart.network.backbone.impl.jxta;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.MimeMediaType;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.impl.protocol.RdvAdv;
import net.jxta.logging.Logging;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager.ConfigMode;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.rendezvous.RendezVousService;
import net.jxta.rendezvous.RendezvousEvent;
import net.jxta.rendezvous.RendezvousListener;
import net.jxta.socket.JxtaMulticastSocket;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.HID;
import eu.linksmart.network.Message;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.backbone.Backbone;
import eu.linksmart.network.networkmanager.NetworkManager;
import eu.linksmart.network.routing.BackboneRouter;

/*
 * TODO #NM refactoring
 */
public class BackboneJXTAImpl implements Backbone, RendezvousListener, DiscoveryListener {

	private static String BACKBONE_JXTA = BackboneJXTAImpl.class
			.getSimpleName();

	private Logger logger = Logger.getLogger(BackboneJXTAImpl.class.getName());
	protected ComponentContext context;
	private BackboneJXTAConfigurator configurator; 

	protected String myIP;
	protected String myHID;

	private BackboneRouter bbRouter;
//	private NetworkManager nm;
//	private HIDManagerApplication hidManager;

	
	/**
	 * Standard constructor must not be used because a BackboneRouter instance
	 * is needed. Please use the appropriate constructor.
	 */
	private void init() {
	}

	/**
	 * Instantiates the JXTA backbone.
	 * 
	 * @param bbRouter
	 */
	public void init(BackboneRouter bbRouter, NetworkManager nm) {
		this.bbRouter = bbRouter;
		/*
		this.nm = nm;
		hidManager = nm.getHIDManagerApplication();
*/
		
		configurator = new BackboneJXTAConfigurator(this,
				context.getBundleContext());
		configurator.registerConfiguration();
		
		
/*
		// This constructor is private in order to act as a singleton.
		boolean logs = Boolean.parseBoolean((String) nm.getConfiguration().get(NetworkManagerConfigurator.JXTA_LOGS));
				
		if (!logs) {
			System.setProperty(Logging.JXTA_LOGGING_PROPERTY, Level.OFF.toString()); 
		}
		else if (logs) {
			System.setProperty(Logging.JXTA_LOGGING_PROPERTY, Level.ALL.toString()); 
		}
		
		this.myHID = (String) nm.getConfiguration().get(NetworkManagerConfigurator.NM_HID);
		this.name = (String) nm.getConfiguration().get(NetworkManagerConfigurator.PEER_NAME);
		this.jxtaHome = "NetworkManager/.jxta/" + this.name;
		
		this.lastNMReceivedTable = new ConcurrentHashMap<PeerID, Long>();
		this.timeSyncIDTable = new ConcurrentHashMap<PeerID, String>();
		this.attributeSenderMap = new ConcurrentHashMap<Integer, AtrributeQuerySender>();
		this.announcementValidityTime = Long.parseLong((String) nm.getConfiguration().get(
			NetworkManagerConfigurator.ANNOUNCE_VALIDITY));
		this.factor = Long .parseLong((String) nm.getConfiguration().get(
			NetworkManagerConfigurator.FACTOR));
		this.waitForRdvTime = Long .parseLong((String) nm.getConfiguration().get(
			NetworkManagerConfigurator.WAIT_FOR_RDV_TIME));
		this.synched = (String) nm.getConfiguration().get(
			NetworkManagerConfigurator.SYNCHRONIZED);
		
		String mode = ((String) nm.getConfiguration().get(NetworkManagerConfigurator.MODE));
		// Depending on the mode of configuration the node can act as a 
		// RDV(SuperNode) or as EDGE(Node)
		if (mode.equals("Node")) {
			this.mode = NetworkManager.ConfigMode.EDGE;
			logger.info("LinkSmart Network Manager configured as Node");
		}
		if (mode.equals("SuperNode")) {
			this.mode = NetworkManager.ConfigMode.SUPER;
			logger.info("LinkSmart Network Manager configured as Super Node");
		}
		if ((!mode.equals("Node")) && (!mode.equals("SuperNode"))) {
			logger.error("Wrong node mode format. Please choose between Node or SuperNode");
		}	
		
		try {
			InetAddress i = InetAddress.getLocalHost();
			this.myIP = i.getHostAddress() + ":" 
				+ System.getProperty("org.osgi.service.http.port");
		} catch (UnknownHostException e) {
			logger.error("No host could be found. Please check the network connections...");
			e.printStackTrace();
			this.myIP = "0.0.0.0:0";
		}
		
		// Call the start JXTA method in order to start the JXTA manager 
		// and create the peer group
		startJXTA();
		*/
		logger.info("JXTA Backbone Started succesfully");
	}

	/**
	 * Apply the configuration changes
	 * 
	 * @param updates
	 *            the configuration changes
	 */
	public void applyConfigurations(Hashtable updates) {
		if (updates.containsKey(BackboneJXTAConfigurator.JXTA_LOGS)) {
			if (updates.get(BackboneJXTAConfigurator.JXTA_LOGS).equals("OFF")) {
				System.setProperty(Logging.JXTA_LOGGING_PROPERTY,
						Level.OFF.toString());
			} else if (updates.get(BackboneJXTAConfigurator.JXTA_LOGS).equals(
					"ON")) {
				System.setProperty(Logging.JXTA_LOGGING_PROPERTY,
						Level.ALL.toString());
			}
		}
		// if (updates.containsKey(BackboneJXTAConfigurator.JXTA_DESCRIPTION)
		// || updates.containsKey(BackboneJXTAConfigurator.JXTA_HID)) {
		// this.myHID = (String) nm.getConfiguration().get(
		// NetworkManagerConfigurator.NM_HID);
		//
		// hidManager.setJXTAID(
		// peerID,
		// myHID,
		// (String) nm.getConfiguration().get(
		// NetworkManagerConfigurator.NM_DESCRIPTION),
		// "http://localhost:"
		// + System.getProperty("org.osgi.service.http.port")
		// + servicePath, myIP, System
		// .getProperty("org.osgi.service.http.port"), true);
		// }
	}

	protected void activate(ComponentContext context) {
		System.out.println(BACKBONE_JXTA + "started");
	}

	protected void deactivate(ComponentContext context) {
		System.out.println(BACKBONE_JXTA + "stopped");
	}

	/**
	 * Sends a message over the JXTA communication channel.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param message
	 */
	public NMResponse sendData(HID senderHID, HID receiverHID, byte[] message) {
		// TODO implement this
		return new NMResponse();
	}

	/**
	 * Receives a message over the JXTA communication channel.
	 * 
	 * @param senderHID
	 * @param receiverHID
	 * @param message
	 */
	public NMResponse receiveData(HID senderHID, HID receiverHID,
			byte[] message) {
		// TODO implement this
		return new NMResponse();
	}

	
	// ***************************************************************************************
	// here follows the JXTA specific stuff coming from the old NetworkManager Backbone.
	// ***************************************************************************************
	
	
	private String jxtaHome;
	private NetworkManager manager;
	public String name;
	public PeerGroup netPeerGroup;
	private DiscoveryService netPGDiscoveryService;
	private RendezVousService myPGRdvService;

	private String rdvlock = new String("rocknroll");
	private boolean connected = false;
	private long waitForRdvTime;
	
	public PeerID peerID;
	private ConfigMode mode;
	
	public long announcementValidityTime, factor;
	private String synched;
	
	private JxtaMulticastSocket multicastSocket;
	private MulticastSocket msocket;
	private MulticastSocketListener listener;
	
	// This hastable stores the information about the validity of data in idTable.
	private ConcurrentHashMap<PeerID, Long> lastNMReceivedTable;
	public ConcurrentHashMap<PeerID, String> timeSyncIDTable;
	public ConcurrentHashMap<Integer, AtrributeQuerySender> attributeSenderMap;

	private boolean sendNMAdvertisement = true;
		
	public static String servicePath = "/axis/services/NetworkManagerApplication";
	
	private ThreadNMAnnounce nmAnnounceThread; 

	private int counter = 0;

	
	/**
	* Starts JXTA manager. This method performs different tasks: 
	* -	configures the JXTA platform
	* - creates and joins the LinkSmart peer groupLinkSmartrently password protected peer group)
	*/
	public void startJXTA() {
		/*
		clearCache(new File(jxtaHome, "cm"));
		AdvertisementFactory.registerAdvertisementInstance(
			NMadvertisement.getAdvertisementType(), new NMadvertisement.Instantiator());
		
		// JXTA platform configuration. 
		try {
			manager = new NetworkManager(mode, name, new File(jxtaHome).toURI());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// Set the JXTA configuration parameters. 
		try {
			configurator = manager.getConfigurator();
		} catch (NullPointerException e) {
			logger.fatal("Wrong configuration mode + " + mode.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		URI uri = new File("NetworkManager/config/seeds.txt").toURI();
		
		configurator.addRdvSeedingURI(uri);
		if (Boolean.parseBoolean((String) nm.getConfiguration().get(NetworkManagerConfigurator.RELAYED))) {
			configurator.addRelaySeedingURI(uri);
		}
		else if ((String) nm.getConfiguration().get(
				NetworkManagerConfigurator.EXT_TCP_ADDR) != null){
			configurator.setTcpPublicAddress((String) nm.getConfiguration().get(
				NetworkManagerConfigurator.EXT_TCP_ADDR), true);
			configurator.setTcpStartPort(-1);
			configurator.setTcpEndPort(-1);
		}
		
		logger.debug(uri.toString());
		if (Boolean.parseBoolean( (String) nm.getConfiguration().get(NetworkManagerConfigurator.MULTICAST))) {
			configurator.setUseMulticast(true);
		}
		else {
			configurator.setUseMulticast(false);
		}

		logger.debug((String) nm.getConfiguration().get(
			NetworkManagerConfigurator.JXTA_TCP_PORT));
		configurator.setTcpPort(Integer.valueOf((String) nm.getConfiguration().get(
			NetworkManagerConfigurator.JXTA_TCP_PORT)));
		configurator.setHttpPort(Integer.valueOf((String) nm.getConfiguration().get(
			NetworkManagerConfigurator.JXTA_HTTP_PORT)));
		configurator.setPeerID(IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID));
		
		// Start JXTA network.
		try {
			manager.startNetwork();
		} catch (PeerGroupException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		netPeerGroup= manager.getNetPeerGroup();
		
		// Add the listeners for the services in the netpeergroup.
		netPGDiscoveryService =netPeerGroup.getDiscoveryService();
		netPGDiscoveryService.addDiscoveryListener(this);
		peerID = netPeerGroup.getPeerID();
		
		Boolean res = hidManager.setJXTAID(peerID, myHID, (String) nm.getConfiguration().get(
			NetworkManagerConfigurator.NM_DESCRIPTION), "http://localhost:"
			+ System.getProperty("org.osgi.service.http.port") + servicePath,
			myIP, System.getProperty("org.osgi.service.http.port"), false);
		if (!res) {
			hidManager.setHIDs(myHID + ";" + (String) nm.getConfiguration().get(
				NetworkManagerConfigurator.NM_DESCRIPTION) + ";" + "http://localhost:"
				+ System.getProperty("org.osgi.service.http.port") + servicePath, 
				peerID, myIP);
	   }

		// Add the listeners for the services in the LinkSmart group (discovery and Rdv) 
		myPGRdvService = netPeerGroup.getRendezVousService();
		myPGRdvService.addListener(this);

		if (((String) nm.getConfiguration().get(
				NetworkManagerConfigurator.MODE)).equals("SuperNode")) {
			myPGRdvService.startRendezVous();
		}
		if (((String) nm.getConfiguration().get(
				NetworkManagerConfigurator.MODE)).equals("Node")) {
			waitForRdv();
			if (!myPGRdvService.isConnectedToRendezVous()) {
				logger.info("Could not connect to LinkSmart Super Node. Will "
					+ "start without it (only local network communications)");
			}
			else {
				logger.info("Connected to LinkSmart Super Node succesfully");
			}
		}
		
		msocket = new MulticastSocket();
		multicastSocket = msocket.createMulticastSocket(netPeerGroup);
		
		listener = new MulticastSocketListener(multicastSocket);
		listener.start();
		nmAnnounceThread = new ThreadNMAnnounce();
		nmAnnounceThread.start();
		*/
	}
	
	/**
	 * It waits for a RDV connection. This method blocks until there is a
	 * RDVconnect event or expires the time parameter.
	 */
	private void waitForRdv() {
		long startTime = System.currentTimeMillis();
		synchronized (rdvlock) {
			while ((!myPGRdvService.isConnectedToRendezVous())
					&& (System.currentTimeMillis() - startTime <= waitForRdvTime)) {
				
				logger.info("Awaiting rendezvous conx...");
				try {
					if (!myPGRdvService.isConnectedToRendezVous()) {
						rdvlock.wait(waitForRdvTime);
					}
				}
				catch (InterruptedException e) {
					logger.error("Interrupted exception in waitForRdv...");
				}
			}
		}
	}
   
	/**
	 * Listens for RDV events in a peer. This method is synchronized with 
	 * waitForRdv(time) and unlocks it when a RDVCONNECT event arrives
	 * 
	 * @param event a rendezvous event
	 */
	public void rendezvousEvent(RendezvousEvent event) {
		String eventDescription;
		
		int	eventType;
		eventType = event.getType();
		switch (eventType) {
			case RendezvousEvent.RDVCONNECT:
				eventDescription = "RDVCONNECT";
				connected = true;
				break;
			case RendezvousEvent.RDVRECONNECT:
				eventDescription = "RDVRECONNECT";
				connected = true;
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
				connected = true;
				break;
			case RendezvousEvent.BECAMEEDGE:
				eventDescription = "BECAMEEDGE";
				break;
			default:
				eventDescription = "UNKNOWN RENDEZVOUS EVENT";
		}
		 
		logger.info("RendezvousEvent: Event = " + eventDescription
			+ " from peer = " + event.getPeer());
		synchronized (rdvlock) {
			if (connected) {
				rdvlock.notify();
			}
		}
	}
	
	/**
	* This method gets any responses to the getRemoteAdvertisements method, 
	* NM advertisements, and processes them. Extracts the useful information 
	* from them, such as updating the idTable
	* 
	* @param event the discovery event
	*/
	public void discoveryEvent(DiscoveryEvent event) {
		DiscoveryResponseMsg response = event.getResponse();
		logger.debug("Got a discovery response with " + response.getResponseCount()
			+ " elements from peer: " + event.getSource() + "\n");
		NMadvertisement adv;
		Enumeration<Advertisement> en = response.getAdvertisements();
		Long time;
		
		if (en != null) {
			while (en.hasMoreElements()) {
				/* Store received NM advertisement. */
				Advertisement adv1 = en.nextElement();
				
				if (adv1.getAdvType().equals(RdvAdv.getAdvertisementType())) {
					RdvAdv rdvAdv = (RdvAdv) adv1;
					rdvAdv.getPeerID();
					logger.info("LinkSmart Super Node discovered " + rdvAdv.getPeerID());
					logger.info("Cannot connect to it yet (Implement me!)");
				}
				
				if (adv1.getAdvType().equals(NMadvertisement.getAdvertisementType())) {
					adv = (NMadvertisement) adv1;
					
					logger.debug(adv.getName() + " from " + adv.getEndpoint());
				}
				
				if (adv1.getAdvType().equals(DiscoveryService.GROUP)) {
					System.out.println("Grupo: \n "
						+ adv1.getDocument(MimeMediaType.TEXTUTF8));
				}
			}
		}
	}
	
	/**
	 * Clears the cache
	 * @param rootDir the root directory
	 */
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
		} catch (Throwable t) {
			logger.error("Unable to clear " + rootDir.toString());
			t.printStackTrace();
		}
	}
	
	/**
	 * Sends an attributes query
	 * 
	 * @param senderHID the sender LinkSmart ID
	 * @param senderPID the sender Persistent ID
	 * @param query the query to send
	 * @param maxTime the maximum time the query is valid
	 * @param maxResponses the maximum number of responses
	 * @return the result of the query
	 */
	public Vector<String> sendAttributesQuery(String senderHID, String senderPID, 
			String query, long maxTime, int maxResponses) {
		/*
		String message;
		Integer i = count();
		if (senderPID != null) {
			message = HID_RESOLUTION + "@&" + senderHID + "@&"
				+ senderPID + "@&" + query+ "@&" + i;
		}
		else  {
			message = HID_RESOLUTION + "@&" + senderHID + "@&"
				+ senderPID + "@&" + query + "@&" + i;
		}
		
		AtrributeQuerySender sender = new AtrributeQuerySender(i, maxTime, maxResponses);
		attributeSenderMap.put(i, sender);
		Vector<String> result = sender.sendMulticastMessage(message);
		attributeSenderMap.remove(i);
		sender = null;
		
		return result;
		*/
		return null;
	}
	
	/**
	 * @param hid the HID
	 * @param i the index of the attribute
	 */
	public void receivedQueryResponse(String hid, Integer i) {
		try {
			attributeSenderMap.get(i).notification(hid);
			logger.info("Got response: " + hid);
		} catch (Exception e) {
			logger.error("Could not find thread to notify");
		}
	}

	/**
	 * Private counter
	 * @return the counter
	 */
	private synchronized Integer count() {
		counter = counter + 1;
		return counter;
	}
	
	/**
	 * Stops JXTA
	 */
	public void stopJXTA() {
		/*
		listener.stopThread();
		nmAnnounceThread.stopThread();
		multicastSocket.disconnect();
		multicastSocket.close();
		
		manager = null;
		netPeerGroup.globalRegistry.unRegisterInstance(
			netPeerGroup.getPeerGroupID(), netPeerGroup);
		netPeerGroup.stopApp();
		netPeerGroup.unref();
		netPeerGroup = null;

		try {
			nmAnnounceThread.join();
			listener.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		listener = null;
		nmAnnounceThread = null;
		msocket = null;
		multicastSocket = null;
		
		System.gc();
		System.runFinalization();
		System.gc();
		*/
	}

	
	
	/**
	 * The ThreadNMAnnounce class provides a thread in order to announce the 
	 * host HIDs contained in idTable. First it creates the advertisement and 
	 * publishes it into the peerGroup. Then updates the idTable and finally
	 * sends a request for remoteAdvertisements. Then it sleeps for an 
	 * announcementValidityTime. In the updateHIDs method, checks if the 
	 * different HIDs are not valid because didn't receive any notice from
	 * them in for a k*AnnouncementValidityTime
	 */
	public class ThreadNMAnnounce extends Thread {
		
		/*
		public static final long NM_ADVERTISEMENT_TIME = 60 * 1000;
		String hids, name, endpoint, time , data;
		private long lastNMAdvertisement = 0;
		private Logger logger = Logger.getLogger(ThreadNMAnnounce.class.getName());
		private boolean running;
		
		// Starts the ThreadNMAnnounce thread. Once started, this thread keeps 
		// publishing and discovering NM advertisements
		public void run() {
			setName("NMAnnounceThread");
			running = true;
			
			while (running) {
				try {
					if (logger.isDebugEnabled()) {
						logger.debug("IDTableStatus: \n" + hidManager.printHIDtable());
					}
					
					if (lastNMAdvertisement == 0) {
						hids = hidManager.getHIDsFromID(peerID);
						hidManager.clearUpdateQueue();
						if (hids != null) {
							name = NM_HELLO;
							endpoint = myIP;
							time = synched;

							logger.debug("Sending NM Hello with the "
								+ "following hids: " + hids);
							data = name + "@&" + hids + "@&" + peerID 
								+ "@&" + endpoint +"@&" + time;
							
							synchronized (msocket) {
								msocket.sendData(multicastSocket, 
									new DatagramPacket(data.getBytes(), data.length()));
								}
							
							lastNMAdvertisement = System.currentTimeMillis();
							sendNMAdvertisement = false;
						}
					}
					else if (((System.currentTimeMillis() - lastNMAdvertisement)
							> NM_ADVERTISEMENT_TIME) || sendNMAdvertisement) {

						// NMAdvertisement
						hids = hidManager.getHIDsFromID(peerID);
						hidManager.clearUpdateQueue();
						if (hids != null) {
							name = NM_ADVERTISEMENT;
							
							// Fill the NM advertisement. 
							endpoint = myIP;
							time = hidManager.getTableHash();
							
							logger.debug("Sending NM advertisement with the "
								+ "following hids: " + hids);
							data = name + "@&" + hids + "@&" + peerID 
								+ "@&" + endpoint +"@&" + time;
							
							synchronized (msocket) {
								msocket.sendData(multicastSocket, 
									new DatagramPacket(data.getBytes(), data.length()));
							}
							
							lastNMAdvertisement = System.currentTimeMillis();
							sendNMAdvertisement = false;
						}
						else {
							logger.error("The hids were null");
						}
					}
					else {
						hids = hidManager.getHIDsUpdate();
						name = NM_UPDATE;
						endpoint = myIP;
						time = synched;
						
						logger.debug("Sending NM advertisement with the "
							+ "following hids: " + hids);
						data = name + "@&" + hids + "@&" + peerID 
							+ "@&" + endpoint +"@&" + time;
						
						synchronized (msocket) {
							msocket.sendData(multicastSocket, 
								new DatagramPacket(data.getBytes(), data.length()));
						}
					}
					
					updateHIDs();
					Thread.sleep(announcementValidityTime);
				} catch (InterruptedException e) {
					logger.error("Interrupted Exception when publishing "
						+ "the NM advertisement", e);
				}
			}
			
		}
		*/
		
		/**
		 * Updates the HIDs
		 */
		private void updateHIDs() {
			/*
			Enumeration<PeerID> keys = lastNMReceivedTable.keys();
			PeerID k;
			Long time;
			logger.debug("Last time received table : " + lastNMReceivedTable.toString());
			
			while (keys.hasMoreElements()) {
				k = keys.nextElement();
				time = System.currentTimeMillis();
				
				if ((time - lastNMReceivedTable.get(k))
						> factor*announcementValidityTime) {
					
					logger.debug("HIDs validity time expired. "
						+ "Deleting HIDs from ID = " + k);
					hidManager.removeHIDsFromID(k);
					lastNMReceivedTable.remove(k);
					logger.debug("Removing ID = " + k + " from "
						+ "peerIDtable and timeSyncIDTable");
					timeSyncIDTable.remove(k);
					logger.debug("Removed ID = " + k + " from "
						+ "peerIDtable and timeSyncIDTable");
				}
			}
			*/
		}
		
		/**
		 * Creates an HID message
		 * 
		 * @param type the type of the message
		 * @return the message created
		 */
		public String[] createHIDMessage(String type) {
			return null;
		}
		
		/**
		 * Stops the ThreadNMAnnounce thread
		 */
		public void stopThread() {
			/*
			this.running = false;
			*/
		}
	}
	
	
	
	/**
	 * Multicast Socket Listener
	 */
	public class MulticastSocketListener extends Thread {
		JxtaMulticastSocket m;
		boolean running;
		
		/**
		 * Constructor
		 * 
		 * @param m the JXTA multicast socket
		 */
		public MulticastSocketListener(JxtaMulticastSocket m) {
			this.running = true;
			this.m = m;
		}
		
		/**
		 * Starts the thread
		 */
		public void run() {
			/*
			setName("MulticastListener");
			byte[] buffer;
			DatagramPacket receivedData;
			String hids, name, endpoint, time, add, delete;
			String[] hidsArray, hidUpdate;
			String sw;
			PeerID id;
			while (running) {
					buffer =  new byte[64000];
					receivedData = new DatagramPacket(buffer, buffer.length);
					
					try {
						m.receive(receivedData);
					} catch (IOException e2) {
						logger.error(getName() + ": Unable to receive data in " + m.toString(), e2);
					}
					
					sw = new String(receivedData.getData(), 0, receivedData.getLength());
					
					String[] token = sw.split("@&");
					name = token[0];
					
					// NM_ADVERTISEMENT or NM_HELLO 
					if (name.equals(NM_ADVERTISEMENT) || name.equals(NM_HELLO)) {
						logger.debug(name + " received!");
						hids = token[1];
						try {
							id = (PeerID) IDFactory.fromURI(new URI(token[2]));
							endpoint = token[3];
							time = token[4];
							
							if (id != peerID) {
								if (name.equals(NM_HELLO)) {
									sendNMAdvertisement = true;
								}
								lastNMReceivedTable.put(id, System.currentTimeMillis());

								try {
									timeSyncIDTable.put(id, time);
								} catch (NullPointerException e) {
									logger.error("ID field from NM advertisement "
										+ "could not be decoded");
								}
								
								if (!hidManager.checkUpToDate2(id, time)) {
									// Clear HIDs from IP before storing new HIDs. 
									// As the NM advertisement contains	every HID 
									// a NM is responsible for, cleaning it will 
									// allow removing old HIDs and duplications.
									logger.debug("Cleaning table before updating "
										+ "it for " + id);
									try {
										hidManager.removeHIDsFromID(id);
									} catch (Exception e) {
										logger.error(e.getCause());
									}
									
									// Set the receivedHIDs using the method 
									// provided in the identity manager
									hidManager.setHIDs(hids, id, endpoint);
								}
							}
						} catch (URISyntaxException e1) {
							e1.printStackTrace();
						}
					}
					
					// NM_UPDATE 
					if (name.equals(NM_UPDATE)) {
						logger.trace("NMUpdate received!");
						hids = token[1];
						
						try {
							id = (PeerID) IDFactory.fromURI(new URI(token[2]));
							endpoint = token[3];
							time = token[4];
							
							if (id != peerID) {
								lastNMReceivedTable.put(id, System.currentTimeMillis());
								
								try {
									timeSyncIDTable.put(id, time);
								} catch (NullPointerException e) {
									logger.error("ID field from NM advertisement "
										+ "could not be decoded");
								}
								
								hidsArray = hids.split(" ");
								add = "";
								delete = "";
								for (int i = 0; i < hidsArray.length; i++) {
									hidUpdate = hidsArray[i].split(";", 2);
									if (hidUpdate.length == 2) {
										if (hidUpdate[0].equals("A")) {
											add = add + hidUpdate[1] + " ";
										}
										if (hidUpdate[0].equals("D")) {
											delete = delete + hidUpdate[1] + " ";
										}
									}
								}
								hidManager.setHIDs(add, id, endpoint);
								hidManager.deleteHIDs(delete, id);
							}
						} catch (URISyntaxException e1) {
							e1.printStackTrace();
						}
					}
					
					// HID_RESOLUTION 
					if (name.equals(HID_RESOLUTION)) {
						if (token.length == 4) {
							String senderHID = token[1];
							String query = token[2];
							String i = token[3];
							if (!hidManager.getHostHIDs().contains(senderHID)) {
								(new HIDAttrChecker(senderHID, null, query, i)).start();
							}
						}
						else if (token.length == 5) {
							String senderHID = token[1];
							String senderPID = token[2];
							String query = token[3];
							String i = token[4];
							if (!hidManager.getHostHIDs().contains(senderHID)) {
								(new HIDAttrChecker(senderHID, senderPID, query, i)).start();
							}
						}
					}
			}
			*/
		}
		
		/**
		 * Stops the thread
		 */
		public void stopThread() {
			m.close();
			this.running = false;
		}
	}
	
	
	 
	/**
	 * This class is in charge of checking if is there any local HID that 
	 * contains the requested attribute query. If it finds one HID that 
	 * matches the request, it will first contact the PEP to know if is 
	 * there any policy that restricts sending the attributes from that HID.
	 * If the policy decision is ok, it will send a message to the 
	 * destination HID to notify the query sender that is the owner of that 
	 * information and send the HID.
	 */
	public class HIDAttrChecker extends Thread {
		private String senderHID;
		private String senderPID;
		private String query;
		private String i;
		
		/**
		 * Constructor
		 * 
		 * @param senderHID the sender LinkSmart ID
		 * @param senderPID the sender Persistent ID
		 * @param query the query
		 * @param i unique integer
		 */
		public HIDAttrChecker(String senderHID, String senderPID, String query, String i) {
			this.senderHID = senderHID;
			this.senderPID = senderPID;
			this.query = query;
			this.i = i;
		}
		
		/**
		 * Starts the thread
		 */
		public void run() {
			/* Verify if I have the attributes from the message in the local idTable. */
			/*
			Vector<String> resultHID = hidManager.getHostHIDbyAttributes(query, 0);
			
			if (!resultHID.isEmpty()) {
				String resHIDs = "";
				Enumeration en = resultHID.elements();
				while (en.hasMoreElements()) {
					resHIDs = resHIDs + (String) en.nextElement() + " ";
				}
				
				// If ok, send pipe message.
				nm.pipeSyncHandler.sendAsyncMessage((String) hidManager.getLocalNMHID(), 
					senderHID, resHIDs, PipeSyncHandler.HID_ATTRIBUTE_QUERY_RESPONSE, 
					Integer.parseInt(i));
			}
			*/
		}
	}
	
	
	 
	/**
	 * This class is in charge of checking if is there any local HID that 
	 * contains the requested attribute query. If it finds one HID that 
	 * matches the request, it will first contact the PEP to know if is 
	 * there any policy that restricts sending the attributes from that HID.
	 * If the policy decision is ok, it will send a message to the destination 
	 * HID to notify the query sender that is the owner of that information 
	 * and send the HID.
	 */
	public class AtrributeQuerySender {
		private String lock = "lock";
		private Vector<String> hids;
		private Integer count;
		private long maxTime;
		private int maxResponses;
		
		/**
		 * Constructor
		 * 
		 * @param count the conter
		 * @param maxTime the maximum time
		 * @param maxResponses the maxumum number of responses
		 */
		public AtrributeQuerySender(Integer count, long maxTime, int maxResponses) {
			this.count = count;
			this.maxTime = maxTime;
			this.maxResponses = maxResponses;
			this.hids = new Vector<String>();
		}
		
		/**
		 * Makes a notification
		 * @param hid the HID
		 */
		public void notification(String hid) {
			String[] hids = hid.split(" ");
			for (int i = 0; i < hids.length; i++) {
				this.hids.add(hids[i]);
			}
			if (this.hids.size() == maxResponses) {
				synchronized (lock) {
					lock.notify();
				}
			}
		}
		
		/**
		 * Sends a multicast message
		 * 
		 * @param message the message
		 * @return the HIDs
		 */
		public Vector<String> sendMulticastMessage(String message) {
			/*
			synchronized (msocket) {
				msocket.sendData(multicastSocket, 
					new DatagramPacket(message.getBytes(), message.length()));
				}
			
			synchronized (lock) {
				try {
					lock.wait(maxTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			logger.debug("Received response in " + count);
			*/
			return hids;
		}
	}

	
}
