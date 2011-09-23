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
package eu.linksmart.limbo.protocol.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Component;

import eu.linksmart.limbo.protocol.ProtocolService;

@Component(immediate=true)
public class TcpProtocolService implements ProtocolService {
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	private BundleContext context;
	private String propertykey="serverendpoint.tcp";

	protected void activate(ComponentContext context) {
		this.context = context.getBundleContext();
	} 	
	
	@Override
	public void start() throws IOException {
		String portString = System.getProperty("org.osgi.service.http.port");
		if (portString == null) {
			portString = context.getProperty("org.osgi.service.http.port");
		}
		if (portString == null) {
			portString = "8080";
		}
		int port = Integer.parseInt(portString);
        serverSocket = new ServerSocket(port);
        System.setProperty(propertykey, "127.0.0.1:"+port);
		System.out.println("Started TcpProtocolService on port: " + port);
	}

	@Override
	public InetAddress getClientAddress() throws IOException {
		assertClientSocket();
		return clientSocket.getInetAddress();
	}
	
	@Override
	public String receive() throws IOException {
		assertClientSocket();
		return in.readLine();
	}

	private void assertClientSocket() throws IOException {
		if (clientSocket == null) {
			clientSocket = serverSocket.accept();
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		}
	}
	
	@Override
	public int receiveChar() throws IOException {
		assertClientSocket();
		return in.read();
	}

	@Override
	public boolean send(String message) throws IOException {
		if (out == null) {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
		}
		out.print(message);
		return true;
	}
	
	@Override
	public void stop() throws IOException {
		serverSocket.close();
		serverSocket = null;
		System.setProperty(propertykey, "");
	}

	@Override
	public void reset() throws IOException {
		out.close();
		clientSocket = null;
		out = null;
		in = null;
	}
}
