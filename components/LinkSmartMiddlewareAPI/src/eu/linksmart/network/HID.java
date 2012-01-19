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

import java.nio.ByteBuffer;
import java.util.Random;

/**
 * Class to store HID information. An HID consists of three context ids and a
 * device ID. It looks like this: contextID-3.contextID-2.contextID-1.deviceID.
 * An example is 0.0.0.8248725583067352822.
 */
public class HID {

	private long deviceID = 0;
	private long contextID1 = 0;
	private long contextID2 = 0;
	private long contextID3 = 0;
	private int level;

	/**
	 * The length of a HID in binary format, as number of bytes
	 */
	public static final int HID_BYTE_LENGTH = 32;

	Random rnd = new Random();

	/**
	 * HID constructor. Creates an HID from an HID in String representation
	 * 
	 * @param strHID
	 *            The String representation of the HID to be created
	 */
	public HID(String strHID) {
		String[] vectorHID = null;
		int level = 0;
		vectorHID = strHID.split("\\.");
		long context;
		try {
			for (int i = 0; i < vectorHID.length & i < 4; i++) {
				context = Long.parseLong(vectorHID[i]);
				if (context != 0) {
					level = 3 - i;
					break;
				}
			}
			this.deviceID = Long.parseLong(vectorHID[3]);
			this.contextID1 = Long.parseLong(vectorHID[2]);
			this.contextID2 = Long.parseLong(vectorHID[1]);
			this.contextID3 = Long.parseLong(vectorHID[0]);
			this.level = level;
		} catch (Exception e) {
			this.deviceID = 0;
			this.contextID1 = 0;
			this.contextID2 = 0;
			this.contextID3 = 0;
			this.level = 0;
		}
	}

	/**
	 * HID constructor. Creates an HID with a random deviceID and level = 0
	 * 
	 * @param hidMgr
	 *            the HIDManager
	 */
	public HID() {
		this.deviceID = Math.abs(rnd.nextLong());
	}

	/**
	 * HID constructor. Creates an HID from an existing HID
	 * 
	 * @param oldHID
	 *            The existing HID
	 */
	public HID(HID oldHID) {
		this.deviceID = oldHID.deviceID;
		this.contextID1 = oldHID.contextID1;
		this.contextID2 = oldHID.contextID2;
		this.contextID3 = oldHID.contextID3;
		this.level = oldHID.level;
	}

	/**
	 * HID constructor. Constructs the HID from the given binary representation.
	 * 
	 * @param data
	 *            the HID values, as a 32-byte array, as results from toBytes()
	 */
	public HID(byte[] data) {
		if (data.length != HID_BYTE_LENGTH) {
			throw new IllegalArgumentException("HID data must be 32 bytes long");
		}
		ByteBuffer buffer = ByteBuffer.allocate(HID_BYTE_LENGTH);
		buffer.put(data);
		buffer.position(0);
		this.contextID3 = buffer.getLong();
		this.contextID2 = buffer.getLong();
		this.contextID1 = buffer.getLong();
		this.deviceID = buffer.getLong();
	}

	/**
	 * Sets the deviceID of the current HID
	 * 
	 * @param deviceID
	 *            The DeviceID to be assigned
	 */
	public void setDeviceID(long deviceID) {
		this.deviceID = Math.abs(deviceID);
	}

	/**
	 * Sets the level of the current HID
	 * 
	 * @param level
	 *            The level to be assigned
	 */
	public void setLevel(int level) {
		if ((level >= 0) & (level < 4))
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
		return String.valueOf(contextID3) + "." + String.valueOf(contextID2)
				+ "." + String.valueOf(contextID1) + "."
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

// TODO generated by AXIS - maybe better than the above code?
//    private boolean __hashCodeCalc = false;
//    public synchronized int hashCode() {
//        if (__hashCodeCalc) {
//            return 0;
//        }
//        __hashCodeCalc = true;
//        int _hashCode = 1;
//        if (getBytes() != null) {
//            for (int i=0;
//                 i<java.lang.reflect.Array.getLength(getBytes());
//                 i++) {
//                java.lang.Object obj = java.lang.reflect.Array.get(getBytes(), i);
//                if (obj != null &&
//                    !obj.getClass().isArray()) {
//                    _hashCode += obj.hashCode();
//                }
//            }
//        }
//        _hashCode += new Long(getContextID1()).hashCode();
//        _hashCode += new Long(getContextID2()).hashCode();
//        _hashCode += new Long(getContextID3()).hashCode();
//        _hashCode += new Long(getDeviceID()).hashCode();
//        _hashCode += getLevel();
//        __hashCodeCalc = false;
//        return _hashCode;
//    }
	
	
	/**
	 * Returns true if the object "obj" is "equal to" this one.
	 * 
	 * @param obj
	 *            the object to compare
	 * @return true if the object "obj" is "equal to" this one
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof HID)) {
			return false;
		}

		HID o = (HID) obj;
		return (o.deviceID == deviceID) && (o.contextID1 == contextID1)
				&& (o.contextID2 == contextID2) && (o.contextID3 == contextID3)
				&& (o.level == level);
	}

	/**
	 * Sets the context of the given level
	 * 
	 * @param contextID
	 *            the context to set
	 * @param level
	 *            the level to set the context
	 */
	public void setContextID(long contextID, int level) {
		switch (level) {
		case 1:
			this.contextID1 = Math.abs(contextID);
			break;
		case 2:
			this.contextID2 = Math.abs(contextID);
			break;
		case 3:
			this.contextID3 = Math.abs(contextID);
			break;
		default:
			break;
		}
	}

	/**
	 * Gets the context of the given level TODO check whether this method is
	 * referenced anywhere
	 * 
	 * @param level
	 *            the level to get the context
	 * @return the context of the given level
	 */
	// public Long getContext(int level) {
	// switch (level) {
	// case 1:
	// return this.contextID1;
	// case 2:
	// return this.contextID2;
	// case 3:
	// return this.contextID3;
	// default:
	// return null;
	// }
	// }

	/**
	 * Gets the HID in a binary representation: * Bytes 0..7 contextID3 * Bytes
	 * 8..15 contextID2 * Bytes 16..23 contextID1 * Bytes 24..31 deviceID
	 * 
	 * @return the binary representation of the HID, as a byte[HID_BYTE_LENGTH]
	 */
	public byte[] getBytes() {
		ByteBuffer buffer = ByteBuffer.allocate(HID_BYTE_LENGTH);
		buffer.putLong(contextID3);
		buffer.putLong(contextID2);
		buffer.putLong(contextID1);
		buffer.putLong(deviceID);
		return buffer.array();
	}

}
