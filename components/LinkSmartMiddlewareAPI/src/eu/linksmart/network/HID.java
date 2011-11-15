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

package eu.linksmart.network;

import java.util.Random;

/**
 * Class to store HID information
 */
public class HID {
	
	private long deviceID = 0;
	private long contextID1 = 0;
	private long contextID2=0;
	private long contextID3=0;
	private int level;
	
		
	Random rnd = new Random();

	/**
	 * HID constructor.
	 * Creates an HID from an HID in String representation
	 * 
	 * @param strHID The String representation of the HID to be created
	 */	
	public HID(String strHID) {
		String[] vectorHID = null;
		int level = 0; 
		vectorHID = strHID.split("\\.");
		long context;
		boolean l = false;
		try {
			for (int i = 0; i < vectorHID.length; i++) {
				context = Long.parseLong(vectorHID[i]);
				if ((!l) && (context != 0)) {
					level = 3 - i;
					l = true;
				}
				this.deviceID = Long.parseLong(vectorHID[3]);
				this.contextID1 = Long.parseLong(vectorHID[2]);
				this.contextID2 = Long.parseLong(vectorHID[1]);
				this.contextID3 = Long.parseLong(vectorHID[0]);
				this.level = level;
			}
		} catch (NumberFormatException e) {
			this.deviceID = 0;
			this.contextID1 = 0;
			this.contextID2 = 0;
			this.contextID3 = 0;
			this.level = 0;
		} catch (Exception e) {
			this.deviceID = 0;
			this.contextID1 = 0;
			this.contextID2 = 0;
			this.contextID3 = 0;
			this.level = 0;
		}
	}

	/**
	 * HID constructor.
	 * Creates an HID with a random deviceID and level = 0
	 * 
	 * @param hidMgr the HIDManager 
	 */	
	public HID() {
		long deviceID = Math.abs(rnd.nextLong());
		
		this.deviceID = deviceID;
	}
	
	/**
	 * HID constructor.
	 * Creates an HID from an existing HID
	 * 
	 * @param oldHID The existing HID
	 */	
	public HID(HID oldHID) {		
		this.deviceID = oldHID.deviceID;
		this.contextID1 = oldHID.contextID1;
		this.contextID2 = oldHID.contextID2;
		this.contextID3 = oldHID.contextID3;
		this.level = oldHID.level;
	}

	/**
	 * Sets the deviceID of the current HID
	 *
	 * @param deviceID The DeviceID to be assigned
	 */
	public void setDeviceID(long deviceID) {
		this.deviceID = Math.abs(deviceID);
	}
	
	/**
	 * Sets the contextID1 of the current HID
	 * 
	 * @param contextID1 The conxtextID1 to be assigned
	 */
	public void setContextID1(long contextID1) {
		this.contextID1 = Math.abs(contextID1);
	}

	/**
	 * Sets the contextID2 of the current HID
	 * 
	 * @param contextID2 The conxtextID2 to be assigned
	 */
	public void setContextID2(long contextID2) {
		this.contextID2 = Math.abs(contextID2);
	}

	/**
	 * Sets the contextID3 of the current HID
	 * 
	 * @param contextID3 The conxtextID3 to be assigned
	 */
	public void setContextID3(long contextID3) {
		this.contextID3 = Math.abs(contextID3);
	}

	/**
	 * Sets the level of the current HID
	 * 
	 * @param level The level to be assigned
	 */
	public void setLevel(int level)  {
		if ((level>=0) & (level<4))
			this.level = level;
			
	}

	/**
	 * Gets the level assigned
	 * 
	 * @return The level of this HID
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Gets the deviceID assigned
	 * 
	 * @return The deviceID of this HID
	 */
	public long getDeviceID() {
		return deviceID;
	}

	/**
	 * Gets the contextID1 assigned
	 * 
	 * @return The contextID1 of this HID
	 */
	public long getContextID1() {
		return contextID1;
	}

	/**
	 * Gets the contextID2 assigned
	 * 
	 * @return The contextID2 of this HID
	 */
	public long getContextID2() {
		return contextID2;
	}
	/**
	 * Gets the contextID3 assigned
	 * 
	 * @return The contextID3 of this HID
	 */
	public long getContextID3() {
		return contextID3;
	}
	/**
	 * Gets the level assigned
	 * 
	 * @return The number of nested contexts (level) of this HID
	 */	
	public int level() {
		return level;
	}	

	/**
	 * Returns the string representation of the HID
	 * 
	 * @return The string representation of the HID
	 */		
	public String toString() {	
		return String.valueOf(contextID3) + "."
			+ String.valueOf(contextID2) + "."
			+ String.valueOf(contextID1) + "."
			+ String.valueOf(deviceID);	
	}

	/**
	 * Returns a hash code value for the object
	 * 
	 * @return a hash code value for the object
	 */
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	/**
	 * Returns true if the object "obj" is "equal to" this one.
	 * 
	 * @param obj the object to compare
	 * @return true if the object "obj" is "equal to" this one 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if(!(obj instanceof HID)) {
			return false;
		}
		
		HID o = (HID) obj;
		return (o.deviceID == deviceID)
			&& (o.contextID1 == contextID1)
			&& (o.contextID2 == contextID2)
			&& (o.contextID3 == contextID3)
			&& (o.level == level);
	}

	/**
	 * Sets the context of the given level
	 * 
	 * @param contextID the context to set
	 * @param level the level to set the context
	 */
	public void setContext(long contextID, int level) {
		switch (level) {
			case 1:
				this.contextID1 = contextID;
				break;
			case 2:
				this.contextID2 = contextID;
				break;
			case 3:
				this.contextID3 = contextID;
				break;
			default:
				break;
		}
	}
	
	/**
	 * Gets the context of the given level
	 * 
	 * @param level the level to get the context
	 * @return the context of the given level
	 */
	public Long getContext(int level) {
		switch (level) {
			case 1:
				return this.contextID1;
			case 2:
				return this.contextID2;
			case 3:
				return this.contextID3;
			default: 
				return null;
		}
	}
	
}