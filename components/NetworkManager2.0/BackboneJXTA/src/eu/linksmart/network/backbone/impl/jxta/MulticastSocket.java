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

package eu.linksmart.network.backbone.impl.jxta;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;

import net.jxta.document.AdvertisementFactory;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaMulticastSocket;


public class MulticastSocket {
	public final static String SOCKETIDSTR =
		"urn:jxta:uuid-59616261646162614E5047205032503393B5C2F6CA7A41FDB0F890173088E79404";
	public PeerGroup pg;
	public InetAddress localAddr;
	
	private Logger logger = Logger.getLogger(MulticastSocket.class.getName());

	
	/**
	 * Constructor
	 */
	public MulticastSocket() {
		super();
	}
	
	/**
	 * Gets the socket advertisement
	 * 
	 * @return the socket advertisement
	 */
	public static PipeAdvertisement getSocketAdvertisement() {
		PipeID socketID = null;
		
		try {
			socketID = (PipeID) IDFactory.fromURI(new URI(SOCKETIDSTR));
		} catch (URISyntaxException use) {
			use.printStackTrace();
		}
		
		PipeAdvertisement advertisement = 
			(PipeAdvertisement) AdvertisementFactory.newAdvertisement(
				PipeAdvertisement.getAdvertisementType());
		
		advertisement.setPipeID(socketID);
		/* Set to type to propagate. */
		advertisement.setType(PipeService.PropagateType);
		advertisement.setName("Socket tutorial");
		return advertisement;
	}
	
	/**
	 * Creates a Multicast Socket
	 * 
	 * @param pg the peer group
	 * @return the Multicast Socket
	 */
	public JxtaMulticastSocket createMulticastSocket(PeerGroup pg) {
		logger.debug("Creating JxtaMulticastSocket");
		JxtaMulticastSocket mcastSocket = null;
		
		try {
			mcastSocket = new JxtaMulticastSocket(pg, getSocketAdvertisement());
			logger.debug("LocalAddress :" + mcastSocket.getLocalAddress());
			logger.debug("LocalSocketAddress :" + mcastSocket.getLocalSocketAddress());
			localAddr = mcastSocket.getLocalAddress();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		return mcastSocket;
	}
	
	/**
	 * Sends data over a JXTA Multicast Socket
	 * 
	 * @param mcastSocket the Multicast Socket
	 * @param packet the data to send
	 */
	public void sendData(JxtaMulticastSocket mcastSocket, DatagramPacket packet) {
		try {
			mcastSocket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
