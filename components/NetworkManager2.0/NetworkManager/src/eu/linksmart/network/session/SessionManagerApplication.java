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
 * This class implement the Session Manager on Application view.
 * It is instacied by the Network Manager and is in charge of the
 * session management during runtime.
 */

package eu.linksmart.network.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;


import eu.linksmart.network.backbone.BackboneManagerApplication;
import eu.linksmart.network.impl.NetworkManagerApplicationSoapBindingImpl;
import eu.linksmart.network.impl.NetworkManagerConfigurator;

/**
 * Implementation of the Session Manager
 * @author Navajas
 *
 */
public class SessionManagerApplication {

	private Logger logger = Logger.getLogger(SessionManagerApplication.class.getName());

	private String sessionIDGeneratorName;
	protected long sessionDelay;
	protected String sessionDataPath;
	private int maxSessionsClientValues;
	private int maxSessionsServerValues;
	private long sessionCleaningFrequency;
	private long sessionSynchronizationFrequency;
	private String servicePath;
	
	static private int sessionCounter = 0;
	private static Hashtable<sessionHIDs,Integer> sessionCount = 
		new Hashtable<sessionHIDs, Integer>();
	private static Hashtable<String, sessionHIDs> sessionsClientList = 
		new Hashtable<String, sessionHIDs>();
	private static Hashtable<String, Session> sessionsServerList = 
		new Hashtable<String, Session>();
	private ThreadsManager sessionThreads;

	private NetworkManagerApplicationSoapBindingImpl nm;
	
	/**
	 * Private constructor to avoid instantiation
	 * It launches the different threads needed for the synchronization between 
	 * client and server
	 * 
	 * @param nm the Network Manager application
	 */
	public SessionManagerApplication(NetworkManagerApplicationSoapBindingImpl nm) {
		this.nm = nm;
		
		sessionIDGeneratorName = (String) nm.getConfiguration().get(
			NetworkManagerConfigurator.SESSION_ID_GENERATOR);
		sessionDelay = Long.valueOf((String) nm.getConfiguration().get(
			NetworkManagerConfigurator.SESSION_DELAY));
		sessionDataPath = (String) nm.getConfiguration().get(
			NetworkManagerConfigurator.SESSION_DATA_PATH);
		maxSessionsClientValues = Integer.valueOf((String) nm.getConfiguration().get(
			NetworkManagerConfigurator.SESSION_MAX_CLIENTS));
		maxSessionsServerValues = Integer.valueOf((String) nm.getConfiguration().get(
			NetworkManagerConfigurator.SESSION_MAX_SERVERS));
		sessionCleaningFrequency = Long.valueOf((String) nm.getConfiguration().get(
			NetworkManagerConfigurator.SESSION_CLEANING_FREQ));
		sessionSynchronizationFrequency = Long.valueOf((String) nm.getConfiguration().get(
			NetworkManagerConfigurator.SESSION_SYNC_FREQ));
		servicePath = (String) nm.getConfiguration().get(
			BackboneManagerApplication.servicePath);
		
		/* Creating sessions directory. */
		File sessionsDirectory = new File(sessionDataPath);
		sessionsDirectory.mkdir();

		/* Launching the threads. */
		sessionThreads = new ThreadsManager();
		sessionThreads.setPriority(Thread.MIN_PRIORITY);
		sessionThreads.start();				
	}

	/**
	 * Factory pattern for sessionID generation
	 * It uses the 'sessionIDGeneratorName' attribute from the NM properties 
	 * file to use the adequate sessionID generator
	 * 
	 * @return The sessionID generated with the selected sessionID generator
	 */
	public String sessionIDGenerator() {
		if(sessionIDGeneratorName.equals("DUMMYGEN")) {
			return Integer.toString(sessionCounter + 1);
		}
		
		if(sessionIDGeneratorName.equals("UUID")) {
			String sessionID = java.util.UUID.randomUUID().toString();
			return sessionID;
		}
		
		if(sessionIDGeneratorName.equals("SecureRandom")) {
			SecureRandom random = null;
			try {
				random = SecureRandom.getInstance("SHA1PRNG");
			} catch (NoSuchAlgorithmException e) {
				logger.fatal(e.getMessage());
				logger.fatal(e.getStackTrace());
			}
			byte bytes[] = new byte[20];
			random.nextBytes(bytes);
			return bytes.toString();
		}
		else {
			return null;
		}
	}
	
	/**
	 * Session creation
	 * It creates a Session object for a given client that has a given 
	 * sessionID generated by sessionIDGenerator
	 * It also registers the new sessionID inside the sessionsServerList hashTable
	 * 
	 * @param clientHID The client HID that uses this session
	 * @param serverHID The server HID that uses this session
	 * @return The new Session object
	 */
	public Session createSession(String clientHID, String serverHID) {
		synchronized(sessionsServerList) {
			sessionHIDs tempHIDs = new sessionHIDs(clientHID, serverHID);
			Integer i = sessionCount.get(tempHIDs);
			if (i == null) {
				i=0;
			}
			
			if(i >= maxSessionsClientValues)  {
				logger.error("Max number of client sessions reached " + i);
				return null;
			}
			
			if(sessionCounter >= maxSessionsServerValues) {
				logger.error("Max number of server sessions reached " + sessionCounter);
				return null;
			}
	
			sessionDelay = Long.valueOf((String) nm.getConfiguration().get(
				NetworkManagerConfigurator.SESSION_DELAY));
			sessionDataPath = (String) nm.getConfiguration().get(
				NetworkManagerConfigurator.SESSION_DATA_PATH);

			String sessionID = sessionIDGenerator();	
			Session mySession = new Session(sessionID, clientHID, serverHID, 
				sessionDelay, sessionDataPath);
			/* Adding the new session to sessionsList. */
			sessionsServerList.put(sessionID, mySession);
			/* 
			 * Increase the value stored in sessionCount table in order to know
			 * the exact number of sessions for each clientHID,serverHID pair.
			 */
			sessionCounter++;
			sessionCount.put(tempHIDs, i + 1);
			return mySession;
		}
	}
	
	/**
	 * Session creation
	 * It creates a Session object for a given client that has a given 
	 * sessionID generated by sessionIDGenerator
	 * It also registers the new sessionID inside the sessionsServerList hashTable
	 * 
	 * @param sessionID The session identifier
	 * @param clientHID The client HID that uses this session
	 * @param serverHID The server HID that uses this session
	 * @return The new Session object
	 */
	public Session createSession(String sessionID, String clientHID, String serverHID) {
		synchronized(sessionsServerList) {
			sessionHIDs tempHIDs = new sessionHIDs(clientHID, serverHID);
			Integer i = sessionCount.get(tempHIDs);
			if (i == null) {
				i=0;
			}
			
			if(i >= maxSessionsClientValues) {
				logger.error("Max number of client sessions reached " + i);
				return null;
			}
			
			if(sessionCounter >= maxSessionsServerValues) {
				logger.error("Max number of server sessions reached " + sessionCounter);
				return null;
			}
			
			sessionDelay = Long.valueOf((String) nm.getConfiguration().get(
				NetworkManagerConfigurator.SESSION_DELAY));
			sessionDataPath = (String) nm.getConfiguration().get(
				NetworkManagerConfigurator.SESSION_DATA_PATH);
			
			Session mySession = new Session(sessionID, clientHID, serverHID, 
				sessionDelay, sessionDataPath);
			
			/* Adding the new session to sessionsList. */
			sessionsServerList.put(sessionID, mySession);
			/* 
			 * Increase the value stored in sessionCount table in order to know 
			 * the exact number of sessions for each clientHID,serverHID pair.
			 */
			sessionCounter++;
			sessionCount.put(tempHIDs, i + 1);
			return mySession;			
		}
	}
	
	/**
	 * Session close
	 * It closes a Session object with a given sessionID updating the 
	 * sessionsServerList hashTable
	 * 
	 * @param sessionID The sessionID of the session to be deleted
	 */
	public void closeSession(String sessionID) {
		synchronized (sessionsServerList) {
			/* Removing the old session to sessionsList. */
			Session mySession = sessionsServerList.remove(sessionID);
			sessionCounter--;
			sessionHIDs tempHIDs = new sessionHIDs(mySession.getClientHID(), 
				mySession.getServerHID());
			/* 
			 * Now reduce the number of sessions open for the pair 
			 * clientHID,serverHID  by updating the sessionCount table
			 */
			Integer i = sessionCount.get(tempHIDs) - 1;
			if (i == 0) {
				sessionCount.remove(tempHIDs);
			}
			else {
				sessionCount.put(tempHIDs, i);
			}			
		}
	}

	/**
	 * Loading a session
	 * It instanciates a session object with the serialized session data and 
	 * insert it inside sessionsServerList
	 * 
	 * @param sessionID The sessionID of the session to be instancied
	 * @return A boolean indicating the result of the loading operation
	 */
	public boolean loadSession(String sessionID) {
		Session mySession = null;
		
		try {
			FileInputStream file = new FileInputStream(sessionDataPath
				+ "session" + sessionID + ".ser");
			ObjectInputStream ois = new ObjectInputStream(file);
			mySession = (Session) ois.readObject();
			ois.close();
			file.close();
			synchronized (sessionsServerList) {
				if(mySession != null) {
					/* Adding the new session to sessionsList. */
					sessionsServerList.put(sessionID, mySession);
				}
				else {
					return false;				
				}
			}
		} catch (java.io.IOException e) {
			logger.fatal(e.getMessage());
			logger.fatal(e.getStackTrace());
			return false;
		} catch (ClassNotFoundException e) {
			logger.fatal(e.getMessage());
			logger.fatal(e.getStackTrace());
			return false;
		}
		
		return true;
	}
	
	/**
	 * Operation to request the value of a given session data
	 * 
	 * @param sessionID The sessionID of the session to be asked
	 * @param key The requested parameter
	 * @return The value of the requested parameter
	 */
	public String getSessionParameter(String sessionID, String key) {
		synchronized (sessionsServerList) {
			Session mySession = sessionsServerList.get(sessionID);
			mySession.updateTimingInfo();
			return (String) mySession.getParamsList().get(key);
		}
	}
	
	/**
	 * Operation to set the value of a given session data
	 * 
	 * @param sessionID The sessionID of the session to be asked
	 * @param key The requested parameter
	 * @param value The new value of the requested parameter
	 */
	public void setSessionParameter(String sessionID, String key, String value) {
		synchronized (sessionsServerList) {
			Session mySession = sessionsServerList.get(sessionID);
			mySession.getParamsList().put(key, value);
			mySession.updateTimingInfo();
			mySession.saveSession();			
		}
	}

	/**
	 * Operation to find the active sessionIDs for a given client at server side
	 *  
	 * @param senderHID The HID of the client that need to synchronise its 
	 * clientSessionsList with the serverSessionsList of this host
	 * @param receiverHID The HID of the server which is contacted for synchronization
	 * @return A vector that contains the active sessionIDs
	 */	
	public java.util.Vector synchronizeSessionsList(String senderHID, String receiverHID) {
		synchronized (sessionsServerList) {
			Vector<String> vect = new Vector<String>();
			Enumeration<String> sessionsList_set = sessionsServerList.keys();
			
			while(sessionsList_set.hasMoreElements()) {
				String mySessionID = sessionsList_set.nextElement();
				String clientHID = sessionsServerList.get(mySessionID).getClientHID();
				String serverHID = sessionsServerList.get(mySessionID).getServerHID();
				
				if(clientHID.equals(senderHID) && serverHID.equals(receiverHID)) {
					logger.debug("SERVER ADVISES CLIENT THAT IT HAS " + mySessionID);
					vect.addElement(mySessionID);
				}
			}
			
			return vect;			
		}
	}
	
	/**
	 * Operation to find the active sessionIDs for a given client at server side 
	 * 
	 * @param senderHID The HID of the client that need to synchronise its 
	 * clientSessionsList with the serverSessionsList of this host
	 * @param receiverHID The HID of the server which is contacted for synchronization
	 * @return A vector that contains the active sessionIDs
	 */	
	public String synchronizeSessionsListP2P(String senderHID, String receiverHID) {
		logger.info("Received synchronization of sessions between "
			+ senderHID + " and " + receiverHID);
		
		synchronized (sessionsServerList) {			
			String sessions = "";
			Enumeration<String> sessionsList_set = sessionsServerList.keys();
						  
			while(sessionsList_set.hasMoreElements()) {
				String mySessionID = sessionsList_set.nextElement();
				String clientHID = sessionsServerList.get(mySessionID).getClientHID(); 
				String serverHID = sessionsServerList.get(mySessionID).getServerHID();
				
				if(clientHID.equals(senderHID) && serverHID.equals(receiverHID)) {
					logger.debug("SERVER ADVISES CLIENT THAT IT HAS " + mySessionID);
					sessions = sessions + mySessionID + " ";
				}
			}
			
			return sessions;			
		}
	}
	
	/**
	 * Operation to synchronize the local clientSessionsList with the 
	 * serverSessionsList of a given server
	 * 
	 * @param senderHID The HID of the client that need to synchronise its 
	 * clientSessionsList with the serverSessionsList of this host
	 * @param receiverHID The HID of the server used for this synchronization
	 */
	public void synchronizeClientSessionsList(String senderHID, String receiverHID) {
		/* If senderHID or receiverHID is null, no need to synchronize. */
		if(senderHID.equals("") || receiverHID.equals("")) {
			return;
		}
		
		int c = 0;

		logger.debug("Synchronizing own sessions between " + senderHID
			+ " and " + receiverHID);
		String sessions = nm.sessionSync.synchSessions(senderHID, receiverHID);
		StringTokenizer t = new StringTokenizer(sessions, " ");
		
		/*
		 * We clear all the sessionID corresponding to this server from the 
		 * clientSessionsList
		 */
		synchronized (sessionsClientList) {
			Enumeration<String> sessionsList_set = sessionsClientList.keys();
			
			while(sessionsList_set.hasMoreElements()) {
				String mySessionID = sessionsList_set.nextElement();
				if(sessionsClientList.get(mySessionID).clientHID.equals(senderHID)
						&& sessionsClientList.get(mySessionID).serverHID.equals(receiverHID)) {
					sessionsClientList.remove(mySessionID);
					logger.debug("CLIENT DELETE FROM ITS LIST " 
						+ mySessionID + "C = " + c);
					c = c + 1;
				}
			}
			
			/* Now we add the sessionID of vect inside clientSessionsList. */
			while(t.hasMoreTokens()) {
				String mySessionID = t.nextToken();
				logger.debug("CLIENT ADD TO ITS LIST " + mySessionID);
				sessionsClientList.put(mySessionID, 
					new sessionHIDs(senderHID, receiverHID));
			}
		}
	}	
			
	/**
	 * Operation to add a session to the clientSessionsList when the Session 
	 * Manager is acting as a client
	 * 
	 * @param sessionID The ID of the session to be added
	 * @param clientHID The HID used by this client
	 * @param serverHID The HID used by the server
	 */	
	public void addSessionLocalClient(String sessionID, String clientHID, 
			String serverHID) {
		
		synchronized (sessionsClientList) {
			sessionsClientList.put(sessionID, new sessionHIDs(clientHID, serverHID));
		}
	}
	
	/**
	 * Operation to remove a session from the clientSessionsList when the 
	 * Session Manager is acting as a client
	 * 
	 * @param sessionID The ID of the session to be removed
	 */
	public void removeSessionClient(String sessionID) {
		synchronized (sessionsClientList) {
			sessionsClientList.remove(sessionID);
		}
	}

	/**
	 * Operation to check if a sessionID is not currently used at Client side
	 * 
	 * @param sessionID The sessionID to be tested
	 * @return A boolean indicating the result of this check (true = sessionID 
	 * is used - false = sessionID is not used)
	 */   
	public boolean isSessionIDvalidClient(String sessionID) {
		synchronized (sessionsClientList) {
			for(Enumeration e = sessionsClientList.keys(); e.hasMoreElements();){
				String key = (String) e.nextElement();
				if(sessionID.equals(key)) {
					return true;
				}
			}
			return false;			
		}
	}
	
	/**
	 * Operation to check if a sessionID is not currently used at Server side
	 * 
	 * @param sessionID The sessionID to be tested
	 * @return A boolean indicating the result of this check (true = sessionID 
	 * is used - false = sessionID is not used)
	 */   
	public boolean isSessionIDvalidServer(String sessionID) {
		synchronized (sessionsServerList) {
			for(Enumeration e = sessionsServerList.keys(); e.hasMoreElements();){
				String key = (String) e.nextElement();
				if(sessionID.equals(key)) {
					return true;
				}
			}
			return false;			
		}
	}
		
	/**
	 * Getting the current sessionID generator name
	 * 
	 * @return Name of the current sessionID generator
	 */   
	public String getSessionIDGeneratorName() {
		return sessionIDGeneratorName;
	}

		/**
	 * Setting the current sessionID generator name
	 * 
	 * @param sessionIDGeneratorName Name of the current sessionID generator
	 */  
	public void setSessionIDGeneratorName(String sessionIDGeneratorName) {
		sessionIDGeneratorName = sessionIDGeneratorName;
	}
	
	
	
	/**
	 * Class containing both client and server HIDs that share a common session
	 * It is stored inside the clientSessionList on client side
	 */ 	
	public class sessionHIDs {
		
		String clientHID;
		String serverHID;
		
		/**
		 * Constructor
		 * 
		 * @param clientHID the HID of the client entity which will share the session
		 * @param serverHID the HID of the server entity which will share the session
		 */
		public sessionHIDs(String clientHID, String serverHID) {
			this.clientHID = clientHID;
			this.serverHID = serverHID;
		}

		/**
		 * Returns the hashCode
		 * @return the hashCode
		 */
		@Override
		public int hashCode() {
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result + ((clientHID == null) ? 0 : clientHID.hashCode());
			result = PRIME * result + ((serverHID == null) ? 0 : serverHID.hashCode());
			return result;
		}

		/**
		 * Returns true if the param "obj" is equal to this
		 * 
		 * @param obj the object to compare
		 * @return true if the param obj is equal to this
		 */
		@Override
		public boolean equals(Object obj) {
			boolean result = false;
			
			if (this == obj) {
				result = true;
			}
			
			if (obj == null){
				result = false;
			}
			
			if (getClass() != obj.getClass()){
				result = false;
			}
			
			final sessionHIDs other = (sessionHIDs) obj;
			if ((clientHID != null) && (serverHID != null)) {
				if ((clientHID.equals(other.clientHID))
						&& (serverHID.equals(other.serverHID))) {
					result = true;
				}
				else {
					result = false;
				}
			}
			else {
				result = false;
			}
			
			return result;
		}
	}
	
	
	
	/**
	 * Dedicated thread to remove the expired sessions from the sessionsServerList
	 * Its latency is configurated inside the NM property file
	 */ 	
	public class SessionsCleanUp extends Thread {
		
		Enumeration<String> sessionsList_set;
		Vector <String> v;
		Iterator<String> itrV;
		boolean started = true;
		
		/**
		 * Stops the thread
		 */
		public void stopThread() {
			this.started = false;
		}
		
		/**
		 * Starts the thread
		 */
		public void run() {
			while (started) {
				try {
					if (!sessionsServerList.isEmpty()) {
						sessionsList_set = sessionsServerList.keys();
						
						v = new Vector<String>();
						synchronized (sessionsServerList) {
							while(sessionsList_set.hasMoreElements()) {
								String mySessionID = sessionsList_set.nextElement();
								/* We test if the session has expired. */
								if(System.currentTimeMillis() 
										> sessionsServerList.get(mySessionID).getExpirationTime()) {
									v.add(mySessionID);
								}
							}
							
							/* Removing expired sessions from sessionsServerList. */
							itrV = v.iterator();
							
							while(itrV.hasNext()) {
								String mySessionID = itrV.next();
								logger.debug("SERVER DELETE FROM ITS LIST (EXPIRATED) "
									+ mySessionID);
								Session mySession = sessionsServerList.remove(mySessionID);
								File ser = new File(sessionDataPath + "session" 
									+ mySessionID + ".ser");
								
								boolean deletion = false;
								if (ser.exists()) {
									deletion = ser.delete();
									if(!deletion) {
										/*
										 * If deletion is not possible, we delete 
										 * the file when runtime ends
										 */
										ser.deleteOnExit();
									}
								}
								
								/*
								 * Now reduce the number of sessions open for 
								 * the pair clientHID,serverHID by updating the 
								 * sessionCount table
								 */
								sessionHIDs tempHIDs = 
									new sessionHIDs(mySession.getClientHID(), 
										mySession.getServerHID());
								
								if (sessionCount.get(tempHIDs) != null) {
									Integer i = sessionCount.get(tempHIDs) -1;
									if (i == 0) {
										sessionCount.remove(tempHIDs);
									}
									else {
										sessionCount.put(tempHIDs, i);
									}
									
									/* Only used for dummy generator since rev 80.*/
									sessionCounter--;
								}
								else {
									logger.error("null tempHIDs");
								}
							}
						}
					}
					
					try {
						Thread.sleep(sessionCleaningFrequency);
					} catch (InterruptedException e) {
						return;
					}
				} catch (Exception e) {
					logger.error("Error ", e);
				}
			}
		}
	}
	
	
	
	/**
	 * Dedicated thread to synchronize the sessionsClientList with the sessionsServerList
	 * Its latency is configurated inside the NM property file
	 */ 	
	public class SessionsSynchronization extends Thread {
		String clientHID = "";
		String serverHID = "";
		boolean started = true;
		
		/**
		 * Stops the thread
		 */
		public void stopThread() {
			this.started = false;
		}
		
		/**
		 * Starts the thread
		 */
		public void run() {
			while (started) {
				try {
					if(sessionsClientList.isEmpty()) {
						synchronizeClientSessionsList("", "");
					}
					else {
						/* Launching the sessionsCleanUp thread for each local HID. */
						Enumeration<String> sessionsList_set = sessionsClientList.keys();

						Vector<sessionHIDs> tempV = new Vector<sessionHIDs>();
						sessionHIDs tempSessionHIDs = null;
						
						while(sessionsList_set.hasMoreElements()) {
							String mySessionID = sessionsList_set.nextElement();
							sessionHIDs s = sessionsClientList.get(mySessionID);
							
							if (s != null) {
								clientHID = s.clientHID;
								serverHID = s.serverHID;
								/*
								 * We control if we did not ever synchronize 
								 * for the same (clientHID, serverHID) couple
								 */
								
								boolean synchr = true;
								tempSessionHIDs = new sessionHIDs(clientHID, serverHID);
								if(!tempV.isEmpty()) {
									Iterator<sessionHIDs> tempItr = tempV.iterator();
									while(tempItr.hasNext()) {
										sessionHIDs hids = tempItr.next();
										if(hids.equals(tempSessionHIDs)) {
											synchr = false;
											/* 
											 * We already synchronized for this 
											 * (clientHID, senderHID)
											 */
										}
									}
								}
								
								if(synchr) {
									tempV.add(new sessionHIDs(clientHID, serverHID));
									synchronizeClientSessionsList(clientHID, serverHID);
									/*
									 * We remember the (clientHID, serverHID) used 
									 * to avoid doing the same synchronization twice
									 */
								}
							}
						}
						tempV.removeAllElements();
					}

					try {
						Thread.sleep(sessionSynchronizationFrequency);
					} catch (InterruptedException e) {
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Error " + e.getStackTrace());
				}
			}
		}
	}
	

	
	/**
	 * General thread to manage the other threads
	 */ 	
	public class ThreadsManager extends Thread {
		SessionsSynchronization sessionClientCleaning;
		SessionsCleanUp sessionCleaning;
		boolean started;
		
		/**
		 * Launches the SessionsCleanUp thread and the SessionsSyncrhonization thread
		 */
		public void run() {
			started = true;
			
			/* Launching the sessionsCleanUp thread. */
			sessionCleaning = new SessionsCleanUp();
			sessionCleaning.setPriority(Thread.MIN_PRIORITY);
			
			sessionClientCleaning = new SessionsSynchronization();
			sessionClientCleaning.setPriority(Thread.MIN_PRIORITY);
			
			sessionCleaning.start();
			sessionClientCleaning.start();
		}
		
		/**
		 * Stop all the threads
		 */
		public void stopThreads() {
			started = false;
			try {
				sessionCleaning.stopThread();
				sessionClientCleaning.stopThread();
				sessionCleaning.join();
				sessionClientCleaning.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets the max sessions client values
	 * 
	 * @return the max sessions client values
	 */
	public int getMaxSessionsClientValues() {
		return maxSessionsClientValues;
	}

	/** 
	 * Sets the max sessions server values 
	 * 
	 * @return the max sessions server values
	 */
	public int getMaxSessionsServerValues() {
		return maxSessionsServerValues;
	}

	/**
	 * Gets the session cleaning frequency
	 * 
	 * @return the session cleaning frequency
	 */
	public long getSessionCleaningFrequency() {
		return sessionCleaningFrequency;
	}
	
	/**
	 * Sets tje the session cleaning frequency
	 * 
	 * @param sessionCleaningFrequency the session cleaning frequency
	 */
	public void setSessionCleaningFrequency(long sessionCleaningFrequency) {
		sessionCleaningFrequency = sessionCleaningFrequency;
	}
	
	/**
	 * Gets the session count
	 * 
	 * @return the session count
	 */
	public Hashtable<sessionHIDs, Integer> getSessionCount() {
		return sessionCount;
	}

	/**
	 * Sets the session count
	 * 
	 * @param sessionCount teh session count
	 */
	public void setSessionCount(Hashtable<sessionHIDs, Integer> sessionCount) {
		SessionManagerApplication.sessionCount = sessionCount;
	}

	/**
	 * Gets the session counter
	 * 
	 * @return the session counter
	 */
	public int getSessionCounter() {
		return sessionCounter;
	}

	/**
	 * Sets the session counter
	 * 
	 * @param sessionCounter the session counter
	 */
	public void setSessionCounter(int sessionCounter) {
		SessionManagerApplication.sessionCounter = sessionCounter;
	}

	/**
	 * Gets the session data path
	 * 
	 * @return the session data path
	 */
	public String getSessionDataPath() {
		return sessionDataPath;
	}

	/**
	 * Gets the session delay
	 * 
	 * @return the session delay
	 */
	public long getSessionDelay() {
		return sessionDelay;
	}

	/**
	 * Gets the sessions client list
	 * 
	 * @return the session client list
	 */
	public static Hashtable<String, sessionHIDs> getSessionsClientList() {
		return sessionsClientList;
	}

	/**
	 * Sets the session client list
	 * 
	 * @param sessionsClientList the session client list
	 */
	public static void setSessionsClientList(
			Hashtable<String, sessionHIDs> sessionsClientList) {
		
		SessionManagerApplication.sessionsClientList = sessionsClientList;
	}

	/**
	 * Gets the session server list
	 * 
	 * @return the session server list
	 */
	public Hashtable<String, Session> getSessionsServerList() {
		return sessionsServerList;
	}
	
	/**
	 * Sets the session server list
	 * 
	 * @param sessionsServerList the session server list
	 */
	public void setSessionsServerList(
			Hashtable<String, Session> sessionsServerList) {
		
		SessionManagerApplication.sessionsServerList = sessionsServerList;
	}

	/**
	 * Gets the session synchronization frequency
	 * 
	 * @return the session synchronization frequency
	 */
	public long getSessionSynchronizationFrequency() {
		return sessionSynchronizationFrequency;
	}

	/**
	 * Sets the session synchronization frequency
	 * 
	 * @param sessionSynchronizationFrequency the session synchronization frequency
	 */
	public void setSessionSynchronizationFrequency(
			long sessionSynchronizationFrequency) {
		
		this.sessionSynchronizationFrequency = sessionSynchronizationFrequency;
	}
	
	/**
	 * Stops the session manager
	 */
	public void stopSessionManager() {
		sessionThreads.stopThreads();
	}
	
}
