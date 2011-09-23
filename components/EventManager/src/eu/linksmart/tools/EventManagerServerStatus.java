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
package eu.linksmart.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.ComponentContext;


import eu.linksmart.eventmanager.impl.EventManagerPortBindingImpl;
import eu.linksmart.eventmanager.impl.data.LocalSubscription;


/**
 * This class provides a servlet for the local status of the EventManager
 *
 */
public class EventManagerServerStatus extends HttpServlet{

	
	private static final long serialVersionUID = 1L;
	ComponentContext context;
	public EventManagerServerStatus(ComponentContext context) {
		
		this.context = context;
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType( "text/html" );
		URL cssFile = context.getBundleContext().getBundle().getResource("resources/web/EventManagerStatus.css");
		BufferedReader cssReader = new BufferedReader(new InputStreamReader(cssFile.openStream()));
		
		String temp;
		String css = "";
		while ((temp = cssReader.readLine()) != null) {
			css = css + temp;
		}
		cssReader.close();
		
		response.getWriter().println("<html><head><style type=\"text/css\">" + css +"</style></head>");
		response.getWriter().println("<body><TABLE><tr><td valign=\"middle\" WIDTH=80%><h1>" + "Status page for the local Event Manager" + "</h1></td>" + "<td align=\"right\" WIDTH=20%><img src=\"files/0.gif\" /></td></tr></TABLE>");
		response.getWriter().println("<h1>" + "Total number of subscriptions: " + EventManagerPortBindingImpl.subscriptions.getSubscriptions().size() + "</h1>");
		//Print Network Managers Discovered
		
		response.getWriter().println("<TABLE BORDER=1 class=\"stats\" WIDTH=100%>" +
		"<TR><TD class=\"hed\" WIDTH=25%>TOPIC</TD><TD class=\"hed\" WIDTH=25%>ENDPOINT</TD><TD class=\"hed\" WIDTH=25%>DATE</TD><TD class=\"hed\" WIDTH=25%>COUNTER</TD></TR>");
		String s = "";
		
		for (final LocalSubscription subscription: EventManagerPortBindingImpl.subscriptions.getSubscriptions()) {

			if (subscription.getHID() != null) {
				s = s + "<TR><TD WIDTH=25%>"+ subscription.getTopic()+ "</TD><TD WIDTH=25%>"+ subscription.getHID() +"</TD><TD WIDTH=25%>" + subscription.getDate()+"</TD><TD WIDTH=25%>" + subscription.getCounter()+"</TD></TR>";
			}
			else s = s + "<TR><TD WIDTH=25%>"+ subscription.getTopic()+ "</TD><TD WIDTH=25%>"+ subscription.getURL() +"</TD><TD WIDTH=25%>" + subscription.getDate()+"</TD><TD WIDTH=25%>" + subscription.getCounter()+"</TD></TR>";

			

			
		}
		response.getWriter().println(s);						
		
		
		
		//						
		
		response.getWriter().println("</TABLE></body></html>");
	}
}
