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

package eu.linksmart.network.session;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

/** 
 * Session Class. This class represent a session object which is used to keep 
 * a trace of communications inside LinkSmart.
 */
public class Session implements java.io.Serializable {
	
	private String sessionID;
	private String clientHID;
	private String serverHID;
	private long sessionDelay;
	private String sessionDataPath = null;
	private long creationTime;
	private long lastAccessedTime;
	private long expirationTime;
	private long timeToLive;
	private Hashtable<String, String> paramsList = new Hashtable<String, String>();
	
	/**
	 * Session constructor
	 * 
	 * @param sessionID the session identifier assignated to this session
	 * @param clientHID the HID of the client entity which will share the session
	 * @param serverHID the HID of the server entity which will share the session
	 * @param sessionDelay the session delay
	 * @param sessionDataPath teh session data path
	 */
	public Session(String sessionID, String clientHID, String serverHID, 
			long sessionDelay, String sessionDataPath) {

		this.sessionID = sessionID;
		this.clientHID = clientHID;
		this.serverHID = serverHID;
		this.sessionDelay = sessionDelay;
		this.sessionDataPath = sessionDataPath;
		this.creationTime = System.currentTimeMillis();
		this.updateTimingInfo();
	}
	
	/**
	 * Serialize a session object. Store location is "session<sessionID>.ser"
	 */
	public void saveSession() {
		try {
			FileOutputStream file = new FileOutputStream(sessionDataPath
				+ "session" + this.sessionID + ".ser");
			ObjectOutputStream oos = new ObjectOutputStream(file);
			oos.writeObject(this);
			oos.flush();
			oos.close();
			file.flush();
			file.close();
		} catch (java.io.IOException e) {}
	}
	
	/**
	 * Update of the lastAccessedTime, expirationTime and timeToLive parameters
	 */
	public void updateTimingInfo() {
		this.lastAccessedTime = System.currentTimeMillis();
		this.expirationTime = this.lastAccessedTime + this.sessionDelay;
		this.timeToLive = this.expirationTime - this.lastAccessedTime;
	}

	/**
	 * Gets the creation time of the session
	 * 
	 * @return the creation time of the session
	 */
	public long getCreationTime() {
		return creationTime;
	}
	
	/**
	 * Sets the creation time of the session
	 * 
	 * @param creationTime the creation time of the session
	 */
	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}
	
	/**
	 * Gets the expiration time of the session
	 * 
	 * @return the expiration time of the session
	 */
	public long getExpirationTime() {
		return expirationTime;
	}
	
	/**
	 * Sets the expiration time of the session
	 * 
	 * @param expirationTime the expiration time of the session
	 */
	public void setExpirationTime(long expirationTime) {
		this.expirationTime = expirationTime;
	}
	
	/**
	 * Gets the HID of the client entity which will share the session
	 * 
	 * @return the HID of the client entity which will share the session
	 */
	public String getClientHID() {
		return clientHID;
	}
	
	/**
	 * Sets the HID of the client entity which will share the session
	 * 
	 * @param clientHID the HID of the client entity which will share the session
	 */
	public void setClientHID(String clientHID) {
		this.clientHID = clientHID;
	}
	
	/**
	 * Gets the HID of the server entity which will share the session 
	 * 
	 * @return the HID of the server entity which will share the session
	 */
	public String getServerHID() {
		return serverHID;
	}
	
	/**
	 * Sets the HID of the server entity which will share the session
	 * 
	 * @param serverHID the HID of the server entity which will share the session
	 */
	public void setServerHID(String serverHID) {
		this.serverHID = serverHID;
	}
	
	/**
	 * Gets the last accessed time
	 * 
	 * @return the last accessed time
	 */
	public long getLastAccessedTime() {
		return lastAccessedTime;
	}
	
	/**
	 * Sets the last accessed time
	 * 
	 * @param lastAccessedTime the last accessed time
	 */
	public void setLastAccessedTime(long lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}
	
	/**
	 * Gets the parameters list
	 * 
	 * @return the parameters list
	 */
	public Hashtable getParamsList() {
		return paramsList;
	}
	
	/** 
	 * Sets the parameters list
	 * 
	 * @param paramsList the parameters list
	 */
	public void setParamsList(Hashtable<String, String> paramsList) {
		this.paramsList = paramsList;
	}
	
	/**
	 * Gets the session identifier assignated to this session
	 * 
	 * @return the session identifier assignated to this session
	 */
	public String getSessionID() {
		return sessionID;
	}
	
	/**
	 * Sets the session identifier assignated to this session
	 * @param sessionID the session identifier assignated to this session
	 */
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	
	/**
	 * Gets the time to live of the session
	 * 
	 * @return the time to live of the session
	 */
	public long getTimeToLive() {
		return timeToLive;
	}
	
	/**
	 * Sets the time to live of the session
	 * 
	 * @param timeToLive the time to live of the session
	 */
	public void setTimeToLive(long timeToLive) {
		this.timeToLive = timeToLive;
	}
	
}
