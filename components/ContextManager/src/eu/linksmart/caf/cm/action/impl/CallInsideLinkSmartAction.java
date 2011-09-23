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
 * Copyright (C) 2006-2010 University of Reading,
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
package eu.linksmart.caf.cm.action.impl;

import java.net.URL;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.log4j.Logger;


import eu.linksmart.caf.ActionConstants;
import eu.linksmart.caf.Parameter;
import eu.linksmart.caf.cm.action.ActionProcessor;
import eu.linksmart.caf.cm.action.ThenAction;
import eu.linksmart.caf.cm.exceptions.ContextManagerException;
import eu.linksmart.caf.cm.impl.CmManagerHub;
import eu.linksmart.caf.cm.impl.ContextManagerApplication;
import eu.linksmart.caf.cm.impl.util.TypeFactory;

/**
 * Implemented {@link ActionProcessor} for calling a Web Service.<p>
 * 
 * Expects the following attributes: <ol type="i"> <li>Method Name</li>
 * <li>Target HID / PID / SID</li> </ol> {@link Parameter}s passed in the
 * {@link ThenAction} are used as the arguments of the Web Service Call
 * 
 * @author Michael Crouch
 * 
 */
public class CallInsideLinkSmartAction extends ActionProcessor {

	/** The Action ID handled by this {@link ActionProcessor} */
	public static final String ACTION_ID =
			ActionConstants.INSIDE_LINK_SMART_CALL_ACTION;

	/** The {@link Logger} */
	private static final Logger logger =
			Logger.getLogger(CallInsideLinkSmartAction.class);

	/** The XSD Schema prefix */
	private static final String URI_2001_SCHEMA_XSD =
			"http://www.w3.org/2001/XMLSchema";

	/** The {@link ContextManagerApplication} */
	private ContextManagerApplication cm;

	/**
	 * Constructor for the Action, passing the {@link CmManagerHub}
	 * 
	 * @param managers
	 *            the {@link CmManagerHub}
	 */
	public CallInsideLinkSmartAction(CmManagerHub managers) {
		this.cm = managers.getCmApp();
	}

	@Override
	public boolean canProcessAction(String actionType) {
		return actionType.equalsIgnoreCase(ACTION_ID);
	}

	/**
	 * Extracts the required data from the {@link ThenAction} attributes, and
	 * makes the call to the Web Service (by HID over the LinkSmart Network),
	 * passing the {@link Parameter}s as arguments
	 * 
	 * @param action
	 *            the {@link ThenAction} to process
	 * @return whether the action is processed successfully
	 */
	@Override
	public boolean processAction(ThenAction action) {

		// Get WS Call Attributes from Action
		String trgPID = action.getAttribute(ActionConstants.WSCALL_PID);
		String trgSID = action.getAttribute(ActionConstants.WSCALL_SID);
		String trgHID = action.getAttribute(ActionConstants.WSCALL_HID);
		String wsMethod = action.getAttribute(ActionConstants.WSCALL_METHOD);
		String wsNamespace =
				action.getAttribute(ActionConstants.WSCALL_NAMESPACE);
		String wsSoapAction =
				action.getAttribute(ActionConstants.WSCALL_SOAPACTION);

		if ((wsMethod == null))
			return false;

		// Get the HID to call
		if (trgHID == null) {
			String query = "(PID==" + trgPID + ")";

			if (trgSID != null)
				query = "(" + query + "&&(SID==" + trgSID + "))";
			try {
				trgHID = cm.getHidMatchingQuery(query);
			} catch (ContextManagerException e) {
				logger.error(e.getMessage());
				return false;
			}

			if (trgHID == null) {
				logger.error("No Hid found matching query '" + query + "'");
				return false;
			}
		}

		// Call the service
		try {
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(new URL(cm.getSoapTunnellingAddress(
					trgHID, "0")));

			if ((wsNamespace == null)) {
				call.setOperationName(wsMethod);
			} else {
				call.setOperationName(new QName(wsNamespace, wsMethod));
			}

			if (wsSoapAction != null) {
				call.setSOAPActionURI(wsSoapAction);
			} else {
				call.setSOAPActionURI(wsMethod);
			}

			// get params as object types;
			Object[] tosend;
			if (action.getParameters().size() > 0) {
				tosend = new Object[action.getParameters().size()];
				Iterator<Parameter> it = action.getParameters().iterator();
				int pnum = 0;
				while (it.hasNext()) {
					Parameter param = (Parameter) it.next();
					tosend[pnum] =
							TypeFactory.getObjectAsType(param.getValue(), param
									.getType());
					QName type;
					if (param.getType().contains("#")) {
						String[] typeSpl = param.getType().split("#");
						type = new QName(typeSpl[0], typeSpl[1]);
					} else {
						type = new QName(URI_2001_SCHEMA_XSD, param.getType());
					}
					call.addParameter(param.getName(), type, ParameterMode.IN);
					pnum++;
				}
			} else {
				tosend = new Object[] {};
			}
			call.invoke(tosend);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

}
