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
 * Copyright (C) 2006-2010 []
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

package eu.linksmart.security.axis;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.transport.http.HTTPConstants;

/**
 * There are some known bugs in the Axis 1.4 implementation that we need to work
 * around. This class provides methods to help dealing with that problem.
 * 
 * @author Julian Schuette
 */
class AxisWorkarounds {

	/**
	 * This code is a workaround to fix a (semi-)wrong configuration in the
	 * deploy.wsdd file. In case the URLMapper handler is not instantiated
	 * before this handler here, the method MessageContext.getTargetService()
	 * would return an empty string. This method fixes the problem.
	 * 
	 * @param msgContext the message context
	 * @throws AxisFault
	 * @return MessageContext
	 */
	protected static MessageContext fixTargetServiceSpec(MessageContext msgContext) 
	throws AxisFault {

		// Check if we need to apply the workaround
		if (msgContext.getService() == null) {

			// First try: Get service name from Servlet path
			String path = (String) msgContext.getProperty(
					HTTPConstants.MC_HTTP_SERVLETPATHINFO);
			if ((path != null) && (path.length() >= 1)) {
				if (path.startsWith("/"))
					path = path.substring(1); // chop the extra "/"
				System.out.println("Use new path: " + path);
				msgContext.setTargetService(path);
			} else {
				// Second try: Get service name from wsdl port name
				if (msgContext.containsProperty("wsdl.portName")) {
					msgContext.setTargetService(((QName) msgContext.getProperty(
					"wsdl.portName")).getLocalPart());
				}
			}
		}
		return msgContext;
	}

	protected static boolean isLocalCall(MessageContext msgContext, boolean isClient){
		if(isClient){
			//client checks service url
			URL url;
			try {
				url = new URL((String)msgContext.getProperty("transport.url"));
			} catch (MalformedURLException e) {
				return false;
			}
			return url.getAuthority().startsWith("localhost") || url.getAuthority().startsWith("127.0.0.1");
		}
		else{
			//server checks remote addrs
			String addr = (String)msgContext.getProperty(Constants.MC_REMOTE_ADDR);
			return addr.startsWith("localhost") || addr.startsWith("127.0.0.1");
		}
	}
}
