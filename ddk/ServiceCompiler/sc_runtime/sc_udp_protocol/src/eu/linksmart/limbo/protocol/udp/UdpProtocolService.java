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
 * Copyright (C) 2006-2010
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
package eu.linksmart.limbo.protocol.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Component;

import eu.linksmart.limbo.protocol.ProtocolService;

@Component(immediate=true)
public class UdpProtocolService implements ProtocolService {
	private DatagramSocket serverSocket;
	InetAddress fromAddress;
	int fromPort;
	private String received;
	private int receiveIndex;
	private BundleContext context;
	private String propertykey="serverendpoint.udp";

	protected void activate(ComponentContext context) {
		this.context = context.getBundleContext();
	}

	@Override
	public void start() throws IOException {
		String portString = System.getProperty("org.osgi.service.http.port.udp");
		if (portString == null) {
			portString = context.getProperty("org.osgi.service.http.port.udp");
		}
		if (portString == null) {
			portString = "8081";
		}
		int port = Integer.parseInt(portString);
		System.out.println("Starting UdpProtocolService on port: " + port);
		serverSocket = new DatagramSocket(port);
		System.setProperty(propertykey, "127.0.0.1:"+port);
	}

	private void assertReceived() throws IOException {
		if (serverSocket != null) {
			if (received == null) {
				byte[] receiveData = new byte[2048]; // FIXME
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				fromAddress = receivePacket.getAddress();
				fromPort = receivePacket.getPort();
				received = new String(receiveData);
				receiveIndex = 0;
			}
		}
	}

	@Override
	public synchronized String receive() throws IOException {
		assertReceived();
		// Receive is line-oriented
		int next = received.indexOf("\r\n", receiveIndex);
		String result = received.substring(receiveIndex, next);
		receiveIndex = next + 2;
		return result;
	}

	@Override
	public synchronized int receiveChar() throws IOException {
		assertReceived();
		int result = received.charAt(receiveIndex);
		receiveIndex += 1;
		return result;
	}

	@Override
	public boolean send(String message) throws IOException {
		byte[] sendData = message.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, fromAddress, fromPort);
		serverSocket.send(sendPacket);
		return true;
	}

	@Override
	public void reset() throws IOException {
		received = null;
		receiveIndex = 0;
	}

	@Override
	public void stop() throws IOException {
		reset();
		serverSocket.close();
		serverSocket = null;
		fromAddress = null;
		fromPort = 0;
		System.setProperty(propertykey, "");
	}

	@Override
	public InetAddress getClientAddress() throws IOException {
		return fromAddress;
	}
}
