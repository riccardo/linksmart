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
package eu.linksmart.limbo.http;

import java.io.IOException;
import java.net.SocketException;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;
import eu.linksmart.limbo.protocol.ProtocolService;

@Component
public class LimboHttpService implements HttpService {

	private Collection<LimboServletRegistration> registrations = new LinkedList<LimboServletRegistration>();
	private Map<ProtocolService, Boolean> started = new HashMap<ProtocolService, Boolean>();
	private Thread runThread;

	@Reference(type='*')
	public void addProtocol(ProtocolService protocol) throws IOException {
		try {
			protocol.start();
			startListening(protocol);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeProtocol(ProtocolService protocol) throws IOException {
		stopListening(protocol);
		protocol.stop();
	}

	@Override
	public HttpContext createDefaultHttpContext() {
		// Ignore
		return null;
	}

	private synchronized void startListening(final ProtocolService protocol) {
		started.put(protocol, true);
		runThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (started.get(protocol)) {
					try {
						handleMessage(protocol, protocol.receive());
					} catch (SocketException e) {
						// Ignore -- probably because of close
						if (started.get(protocol)) {
							e.printStackTrace();
						}
					} catch (ServletException e) {
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		runThread.setDaemon(true);
		runThread.start();
	}

	private void stopListening(ProtocolService protocol) {
		started.put(protocol, false);
	}

	private synchronized void handleMessage(ProtocolService protocol, String message) throws ServletException, IOException {
		// Handle request line 
		LimboServletRequest request = new LimboServletRequest(protocol.getClientAddress(), message);
		// Handle parameters
		String line = protocol.receive();
		while (!line.equals("")) {
			request.setHeader(line);
			line = protocol.receive();
		}
		String content_length = request.getHeader("Content-Length");
		if (content_length != null) {
			StringBuffer buffer = new StringBuffer();
			// Read entity body of length content_length
			int length = Integer.parseInt(content_length);
			while (length > 0) {
				buffer.append((char)protocol.receiveChar()); // FIXME: Assumes character data
				length -= 1;
			}
			request.setEntityBody(buffer);
		}


		LimboServletResponse response = new LimboServletResponse();
		for (LimboServletRegistration registration: registrations) {
			if (request.getContextPath().equals(registration.getAlias()) ||
					request.getContextPath().startsWith(registration.getAlias() + "/")) {
				registration.getServlet().service(request, response);
				StringBuffer responseBuffer = new StringBuffer();
				responseBuffer.append("HTTP/1.0 " + response.getStatusCode() + "OK\r\n"); // FIXME: other than OK?
				for (String key: response.getHeaders().keySet()) {
					responseBuffer.append(key + ": " + response.getHeaders().get(key) + "\r\n");
				}
				responseBuffer.append("Content-Length: " + response.getOutput().length() + "\r\n");
				responseBuffer.append("\r\n");
				responseBuffer.append(response.getOutput().toString());
				protocol.send(responseBuffer.toString());
				protocol.reset();
				return;
			}
		}

		protocol.send("HTTP/1.0 400 ERROR\r\n\r\n");
		protocol.reset();
		return;
	}	

	@Override
	public synchronized void registerResources(String alias, String name, HttpContext context)
	throws NamespaceException {
		// Ignore

	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void registerServlet(String alias, Servlet servlet,
			Dictionary initparams, HttpContext context)
	throws ServletException, NamespaceException {
		LimboServletRegistration registration = new LimboServletRegistration(alias, servlet, initparams, context);
		registrations.add(registration);
	}

	@Override
	public synchronized void unregister(String alias) {
		LimboServletRegistration remove = null;
		for (LimboServletRegistration registration: registrations) {
			if (registration.getAlias() == alias) {
				remove = registration;
				break;
			}
		}
		registrations.remove(remove);
	}
}
