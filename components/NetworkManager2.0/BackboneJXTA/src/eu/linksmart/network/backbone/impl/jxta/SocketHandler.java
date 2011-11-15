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
/**
 * Copyright (C) 2006-2010 [Telefonica I+D]
 *                         the HYDRA consortium, EU project IST-2005-034891
 *
 * This file is part of LinkSmart.
 *
 * LinkSmart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
 * version 3 as published by the Free Software Foundation.
 *
 * LinkSmart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with LinkSmart.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * The SocketHandler class provides the methods to establish a socket 
 * communication between Network Managers. 
 * 
 * @see eu.linksmart.network.backbone.BackboneManagerApplication
 */

package eu.linksmart.network.backbone.impl.jxta;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Hashtable;

import net.jxta.document.AdvertisementFactory;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaServerSocket;
import net.jxta.socket.JxtaSocket;

import org.apache.log4j.Logger;

import eu.linksmart.network.networkmanager.NetworkManager;

/*
import eu.linksmart.network.identity.HIDManagerApplication;
import eu.linksmart.network.impl.NetworkManagerApplicationSoapBindingImpl;
import eu.linksmart.network.impl.NetworkManagerConfigurator;
import eu.linksmart.network.networkmanager.NetworkManager;
import eu.linksmart.types.HID;
*/

/**
 * SocketHandler
 */
public class SocketHandler implements PipeMsgListener  {
	private static Logger logger = Logger.getLogger(SocketHandler.class.getName());
	
	/*
	BackboneManagerApplication backboneMgr;
	HIDManagerApplication hidMgr;
	private NetworkManagerApplicationSoapBindingImpl nm;
*/
	
	JxtaServerSocket serverSocket;
	LocalServer local;
	JXTASocketServer socketServer;

	private Long count;
	public int localPort;
	public Hashtable<Long, Long> bitRates;
	private InputPipe inputPipe;

	/**
	 * Constructor
	 * 
	 * @param nm the Network Manager application
	 */
	public SocketHandler(NetworkManager nm) {
		/*
		this.nm = nm;
		this.backboneMgr = nm.backboneMgr;
		this.hidMgr = nm.hidMgr;
		count = new Long(0);
		bitRates = new Hashtable<Long, Long>();
		this.localPort = Integer.valueOf((String) nm.getConfiguration().get(
			NetworkManagerConfigurator.MULTIMEDIA_PORT));
		
		try {
			this.serverSocket = new JxtaServerSocket(backboneMgr.netPeerGroup, 
				createPipeSocketAdv());
			serverSocket.setSoTimeout(0);
			socketServer = new JXTASocketServer(this);
			socketServer.start();
			local = new LocalServer(this);
			local.start();
			this.inputPipe = backboneMgr.netPeerGroup.getPipeService().createInputPipe(
				createPipeAdv(), this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	*/
	}


	/**
	 * Private counter
	 * @return the counter value
	 */
	private synchronized Long getCount() {
		Long antcount = count;
		count += 1;
		return antcount;
	}
	
	/**
	 * Creates a pipe advertisement
	 * @return the pipe advertisement
	 */
	private PipeAdvertisement createPipeAdv(){
		PipeID pipeID = null;
		PipeAdvertisement pipeAdv = (PipeAdvertisement)
			AdvertisementFactory.newAdvertisement(
				PipeAdvertisement.getAdvertisementType());
		String ppid = 
			"urn:jxta:uuid-59616261646162614E5047205032503342C3E540E0904149B8C29F1C9CB9424504";
		
		try {
			pipeID = (PipeID) IDFactory.fromURI(new URI(ppid));
		} catch (URISyntaxException e) {
			logger.error("Imposible to create PipeID for the pipe advertisement");
		}
		
		pipeAdv.setPipeID(pipeID);
		pipeAdv.setName("Input Pipe Advertisement for streaming");
		pipeAdv.setType(PipeService.UnicastType);
		logger.debug("Pipe for Streamming control created: " + pipeID);
		
		return pipeAdv;
	}
	
	/**
	 * Stores information about a pipe message event 
	 * 
	 * @param event the pipe message event
	 */
	public void pipeMsgEvent(PipeMsgEvent event) {
		Message msg = event.getMessage(); 
		MessageElement id = msg.getMessageElement("ID");
		MessageElement rateMsg = msg.getMessageElement("BitRate");
		Long receivedBitRate = Long.parseLong(rateMsg.toString());
		if (bitRates.containsKey(Long.parseLong(id.toString())))
			bitRates.put(Long.parseLong(id.toString()), receivedBitRate);
	}
	
	/**
	 * Creates a pipe socket advertisement
	 * 
	 * @return the pipe socket advertisement
	 */
	private PipeAdvertisement createPipeSocketAdv() {		
		PipeID pipeID;
		PipeAdvertisement pipeAdv = (PipeAdvertisement)
			AdvertisementFactory.newAdvertisement(
				PipeAdvertisement.getAdvertisementType());
		String ppid = 
			"urn:jxta:uuid-59616261646162614E50472050325033295185E56CE44228B2C5A75D4EED99E704";
		
		try {
			pipeID = (PipeID) IDFactory.fromURI(new URI(ppid));
			pipeAdv.setPipeID(pipeID);
			pipeAdv.setName("Socket advertisement for Network Manager");
			pipeAdv.setType(PipeService.UnicastType);
			return pipeAdv;
		} catch (URISyntaxException e) {
			logger.error("Imposible to create PipeID for the pipe advertisement");
		}
		
		return pipeAdv;
	}


	
	/**
	 * JXTASockerServer class
	 */
	public class JXTASocketServer extends Thread {
		private boolean running;
		final static long MAXTIME = 60000;
		final static long UPDATERTIME = 30000;
		Socket socket;
		private SocketHandler socketHandler;

		/**
		 * Constructor
		 * 
		 * @param socketHandler a socket handler
		 */
		public JXTASocketServer(SocketHandler socketHandler) {
			this.socketHandler =socketHandler;
		}
		
		/**
		 * Starts the thread
		 */
		public void run() {
			setName(JXTASocketServer.class.getName());
			running = true;

			while (running) {
				try {
					serverSocket.setPerformancePreferences(MIN_PRIORITY,
						NORM_PRIORITY, MAX_PRIORITY);
					socket = serverSocket.accept();
					(new JXTAConnectionHandler(socket, socketHandler)).start();
				} catch (IOException e) {}
			}
			System.out.println("Out");		
		}
	  	
	  	/**
	  	 * Stops the thread
	  	 */
		public void stopThread() {
			running = false;
			
			try {
				serverSocket.setSoTimeout(1);
				serverSocket.close();
				this.interrupt();
			} catch (IOException e) {
				System.out.println("Hello");
			}
			
			socket = null;
		}
	}
	
	
	
	/**
	 * JXTAConnectionHandler class
	 */
	public class JXTAConnectionHandler extends Thread {
		private boolean running;
		final static long MAXTIME = 60000;
		final static long UPDATERTIME = 30000;
		final static long DEF_BITRATE = 200;
		Socket client = null;
		private SocketHandler socketHandler;
		
		/**
		 * Constructor
		 * 
		 * @param client the client
		 * @param socketHandler the socket handler
		 */
		public JXTAConnectionHandler(Socket client, SocketHandler socketHandler) {
			this.client = client;
			this.socketHandler = socketHandler;
		}
	  	
		/**
		 * Starts the thread
		 */
		public void run() {
			setName(this.getName());
			running = true;
			Long id = null;
			
			try {
				client.setTcpNoDelay(true);
				InputStream clientIn = client.getInputStream();
				DataInput clientDIS = new DataInputStream(clientIn);
				
				String s = clientDIS.readLine();
				String[] k = s.split(" ");
				String[] t = null;
				String hids = "";
				
				if (k[0].equals("GET") || k[0].equals("HEAD")) {
					t =  k[1].split("/", 4);
					id = Long.parseLong(t[1]);
					hids = t[2];
				}
				
				/* Adding the new ID to the table with the default bitrate. */
				bitRates.put(id, DEF_BITRATE);
				
				 // TODO: fix this
				URL serv = new URL("");
				/*
				URL serv = new URL(hidMgr.getEndpoint(hids));
				*/
				
				s = k[0] + " /" + t[3] + " " + k[2] + "\r\n";
				byte[] b = s.getBytes();
				
				Socket server = new Socket(serv.getHost(), serv.getPort());
				OutputStream socketOut = server.getOutputStream();
				
				socketOut.write(b);
				socketOut.flush();
				
				StreamThread forward = new StreamThread(client.getInputStream(),
					server.getOutputStream(), "JXTA -> Server");
				forward.start();
				StreamThreadwControlR reverse = new StreamThreadwControlR(
					server.getInputStream(), client.getOutputStream(),
					"Server -> JXTA", id, socketHandler);
				reverse.start();
				reverse.join();
				forward.join();
				server.close();
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			finally {
				bitRates.remove(id);
			}
		}
	  	
	  	/**
	  	 * Stops the thread
	  	 */
		public void stopThread() {
			running = false;
		}
	}
	
	
	
	/**
	 * ConnectionHandler class
	 */
	public class ConnectionHandler extends Thread {
		private boolean running;
		final static long MAXTIME = 60000;
		final static long UPDATERTIME = 30000;
		private Long id;
		Socket client = null;
		private SocketHandler socketHandler;
		
		/**
		 * Constructor
		 * 
		 * @param client the client
		 * @param socketHandler the socket handler
		 */
		public ConnectionHandler(Socket client, SocketHandler socketHandler) {
			this.client = client;
			this.socketHandler = socketHandler;
			id = getCount();
	  	}

		/**
		 * Starts the thread
		 */
		public void run() {
			setName(this.getName());
			running = true;
			 // TODO: fix this
			
			/*
			try {
				InputStream clientIn = client.getInputStream();
				DataInput clientDIS = new DataInputStream(clientIn);
				
				String s = clientDIS.readLine();
				String[] k = s.split(" ");
				
				if (k.length > 3) {
					s = s.replace(" ", "%20");
					if (s.contains("GET")){
						s = s.replace("GET%20", "GET ");
					}
					else if (s.contains("HEAD")){
						s = s.replace("HEAD%20", "HEAD ");
					}
					s = s.replace("%20HTTP", " HTTP");
				}
				
				String[] t = null;
				String hids = "";
				k = s.split(" ");
				
				if (k[0].equals("GET") || k[0].equals("HEAD")) {
					t =  k[1].split("/", 3);
					hids = t[1];
					s = k[0] + " " + "/" + id + k[1] + " " + k[2];
				}
				
				HID hid = new HID(hids);
				
				s = s + "\r\n";
				byte[] b = s.getBytes();
				PeerID peerID = hidMgr.getIDfromHID(hid);
				
				if (peerID != null) {
					JxtaSocket server = new JxtaSocket(backboneMgr.netPeerGroup,
						peerID, createPipeSocketAdv(), 120000, true);
					server.setTcpNoDelay(true);
					OutputStream socketOut = server.getOutputStream();
					
					socketOut.write(b);
					socketOut.flush();
					StreamThread forward = new StreamThread(client.getInputStream(), 
						server.getOutputStream(), "Renderer -> JXTA");
					forward.start();
					
					StreamThreadwControlS reverse = new StreamThreadwControlS(
						server.getInputStream(), client.getOutputStream(), 
						"JXTA -> Renderer", peerID, id, socketHandler);
					reverse.start();
					reverse.join();
					forward.join();
					System.out.println("Reverse / Forward joined");
					server.close();
					client.close();
				}
				else {
					logger.error("Null PeerID");
				}
			} catch (IOException e) {
				System.err.println("IOEXCEPTION " + e);
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			*/
		}
		
		/**
		 * Stops the thread
		 */
		public void stopThread() {
			running = false;
		}
	}
	
	
	
	/**
	 * @author Telefonica I+D
	 */
	public class LocalServer extends Thread {
		private boolean running;
		private ServerSocket server;
		private SocketHandler socketHandler;
		
		/**
		 * Constructor
		 * 
		 * @param socketHandler the socket handler
		 */
		public LocalServer(SocketHandler socketHandler) {
			this.socketHandler= socketHandler;
		}

		/**
		 * Starts the thread
		 */
		public void run() {
			setName(this.getName());
			running = true;
			Socket client = null;
			
			try {
				server = new ServerSocket(localPort, 5);
				while (running) {
					client = server.accept();
					(new ConnectionHandler(client, socketHandler)).start();
				}
			} catch (IOException ioe) { }
		}
		
		/**
		 * Stops the thread
		 */
		public void stopThread() {
			running = false;
			this.interrupt();
			
			try {
				server.close();
			} catch (IOException e) {}
			
			server = null;
		}
	}

	/**
	 * Stop the sockets
	 */
	public void stopSockets() {
		local.stopThread();
		socketServer.stopThread();
	}

}
