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

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.ComponentContext;


import eu.linksmart.eventmanager.impl.EventManagerPortBindingImpl;
import eu.linksmart.eventmanager.impl.data.LocalSubscription;


/**
 * 
 * Servlet for getting the list of current subscription in the Event Manager
 *
 */
public class GetEventManagerSubscriptions extends HttpServlet{

	private static final long serialVersionUID = 1L;
	ComponentContext context;

	public GetEventManagerSubscriptions(ComponentContext context) {
		
		this.context = context;
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		List<LocalSubscription> subs = EventManagerPortBindingImpl.subscriptions.getSubscriptions();
		Iterator<LocalSubscription> it = subs.iterator();
		while (it.hasNext()) {
			LocalSubscription subscription = it.next();
			if (subscription.getHID() != null) {
				String topic = subscription.getTopic();
				String hid = subscription.getHID();
				Date date = subscription.getDate();
				int counter = subscription.getCounter();
				response.getWriter().write(topic + "|" + hid + "|" + date + "|" + counter);
			}
			else {
				String topic = subscription.getTopic();
				String url = subscription.getURL().toString();
				Date date = subscription.getDate();
				int counter = subscription.getCounter();
				response.getWriter().write(topic + "|" + url + "|" + date + "|" + counter);
			}	
			if (it.hasNext()) response.getWriter().write("<br>");
		}	
	}
}
