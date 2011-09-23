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

package eu.linksmart.limbo.lockmanager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class UDPProtocol implements ServerProtocol {
	
	private InetAddress clientHost;
	private int clientPort;
	private DatagramSocket sd;
	private int port;
	private int bufferSize = 10240; 
	private String url;
	
	public UDPProtocol() {
		super();
	}
	public UDPProtocol(int port) {
		this.port=port;
		try {
			this.sd =  new DatagramSocket( this.port );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createConnectors(int port) {
		if (this.port != port)
		 {
		   this.port=port;
		   try {
			   this.sd = new DatagramSocket( this.port ) ;
		     } catch (SocketException e) {
			    e.printStackTrace();
		        }
		 }
		try {
			this.url = "http://"+InetAddress.getLocalHost().getHostAddress()+":"+this.port;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public String getServerURL() {
		return this.url;
	}

	public String getClientHost() {
		return this.clientHost.toString();
	}

	public String receive() {
		String request = "";
		byte[] buffer = new byte[bufferSize];
		DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
		
		try {
			sd.receive(dp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.clientHost = dp.getAddress();
		this.clientPort = dp.getPort();
		request = new String(buffer);
		request = request.trim();
		return request;
	}

	public void send(String result) {
		DatagramPacket response = new DatagramPacket(result.getBytes(), result.getBytes().length, this.clientHost, this.clientPort); 
		try {
			sd.send(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		try {
			sd.close();	
			sd.setReuseAddress(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
