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
 * Copyright (C) 2006-2010 [University of Paderborn]
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

package eu.linksmart.limbo.sm.networkmanager.client;

import eu.linksmart.limbo.sm.networkmanager.client.types.*;

public interface NetworkManagerApplicationLimboClientPort {

		public String createHID(long in0, int in1 );

	public void closeSession(String in0 );

	public Vector getHIDs();

	public String renewHID(long in0, int in1, String in2 );

	public String createHIDwDesc(long in0, int in1, String in2, String in3 );

	public Vector getContextHIDs(String in0, String in1 );

	public void removeHID(String in0 );

	public String getHIDsbyDescriptionAsString(String in0 );

	public Vector getHostHIDs();

	public Vector getHIDsbyDescription(String in0 );

	public String createHIDwDesc(String in0, String in1 );

	public String getNMPosition();

	public String getContextHIDsAsString(String in0, String in1 );

	public void addSessionRemoteClient(String in0, String in1, String in2 );

	public String addContext(long in0, String in1 );

	public void setSessionParameter(String in0, String in1, String in2 );

	public String renewHIDInfo(String in0, String in1, String in2 );

	public String getHostHIDsAsString();

	public Vector synchronizeSessionsList(String in0, String in1 );

	public String getHIDsAsString();

	public String startNM();

	public NMResponse receiveData(String in0, String in1, String in2, String in3 );

	public String openSession(String in0, String in1 );

	public String getSessionParameter(String in0, String in1 );

	public String createHID();

	public void removeAllHID();

	public NMResponse sendData(String in0, String in1, String in2, String in3 );

	public String stopNM();


	
}