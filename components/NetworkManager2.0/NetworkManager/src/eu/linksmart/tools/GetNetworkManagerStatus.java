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

package eu.linksmart.tools;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import eu.linksmart.network.HIDInfo;
import eu.linksmart.network.identity.IdentityManager;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.network.routing.BackboneRouter;
import eu.linksmart.utils.Part;

/**
 * NetworkManagerStatus Servlet
 */
public class GetNetworkManagerStatus extends HttpServlet {

	Logger LOG = Logger.getLogger(GetNetworkManagerStatus.class.getName());

	IdentityManager identityManager;
	private NetworkManagerCore networkManagerCore;
	private BackboneRouter backboneRouter;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the bundle's context
	 * @param nmServiceImpl
	 *            the Network Manager Service implementation
	 */
	public GetNetworkManagerStatus(NetworkManagerCore networkManagerCore,
			IdentityManager identityManager, BackboneRouter backboneRouter) {

		this.networkManagerCore = networkManagerCore;
		this.identityManager = identityManager;
		this.backboneRouter = backboneRouter;

	}

	/**
	 * Performs the HTTP GET operation
	 * 
	 * @param request
	 *            HttpServletRequest that encapsulates the request to the
	 *            servlet
	 * @param response
	 *            HttpServletResponse that encapsulates the response from the
	 *            servlet
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		Map<String, String[]> params = request.getParameterMap();
		if (params.containsKey("method")) {
			String method = params.get("method")[0];
			if (method.equals("getNetworkManagers")) {
				Set<HIDInfo> hids = identityManager
						.getHIDsByDescription("NetworkManager*");
				processHIDs(hids, response);
			} else if (method.equals("getLocalHids")) {
				Set<HIDInfo> hids = identityManager.getLocalHIDs();
				LOG.debug("Size of LocalHIDs: " + hids.size());
				processHIDs(hids, response);
			} else if (method.equals("getRemoteHids")) {
				Set<HIDInfo> hids = identityManager.getRemoteHIDs();
				LOG.debug("Size of RemoteHIDs: " + hids.size());
				processHIDs(hids, response);
			} else if (method.equals("getNetworkManagerSearch")) {
				Set<HIDInfo> hids = identityManager.getAllHIDs();
				processHIDs(hids, response);
				// TODO should be "HID entity not adapted to security issues"
				// instead of null for remote HIDs; check with Mark?
			}
		}
	}

	private void processHIDs(Set<HIDInfo> hids, HttpServletResponse response) {

		Iterator<HIDInfo> it = hids.iterator();

		while (it.hasNext()) {
			HIDInfo hidInfo = it.next();

			// Create the description from all Attributes
			String description = "";
			Part[] attr = hidInfo.getAttributes();
			for (Part part : attr) {
				description = description + part.getKey() + " = "
						+ part.getValue() + ";";
			}

			// Get the Route for this HID
			String route = backboneRouter.getBackboneType(hidInfo.getHID());

			// Here we print one row
			try {
				response.getWriter().write(
						hidInfo.getHID().toString() + "|" + description + "|"
								+ "TODO: Host" + "|" + route);
				if (it.hasNext()) {
					response.getWriter().write("<br>");
				}
			} catch (IOException e) {
				LOG.error("Unable to produce HTML. ", e);
			}

		}
	}

}
