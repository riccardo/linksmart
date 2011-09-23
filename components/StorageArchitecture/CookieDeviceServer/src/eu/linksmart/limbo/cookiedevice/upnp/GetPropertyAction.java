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

package eu.linksmart.limbo.cookiedevice.upnp;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPStateVariable;



public class GetPropertyAction implements UPnPAction {

	final private String NAME = "GetProperty";

	final private String PROPERTY = "property";

	final private String PROPERTYVALUE = "propertyvalue";
	final private String[] IN_ARG_NAMES = new String[]{PROPERTY};
	final private String[] OUT_ARG_NAMES = new String[]{PROPERTYVALUE};

	private PropertyStateVariable Property;
	private PropertyValueStateVariable PropertyValue;
	private LinkSmartServicePortUPnPService service;

	public GetPropertyAction(PropertyStateVariable Property,PropertyValueStateVariable PropertyValue, LinkSmartServicePortUPnPService service) {
		this.Property = Property;
		this.PropertyValue = PropertyValue;
		this.service = service;
	}

	public String[] getInputArgumentNames() {
		return  IN_ARG_NAMES;
	}

	public String getName() {
		return NAME;
	}

	public String[] getOutputArgumentNames() {
		return  OUT_ARG_NAMES;
	}

	public String getReturnArgumentName() {
		return null;
	}

	public UPnPStateVariable getStateVariable(String argumentName) {

		if (argumentName.equals(PROPERTY)) return Property;
		else if (argumentName.equals(PROPERTYVALUE)) return PropertyValue;	
		else return null;
	}

	public Dictionary invoke(Dictionary args) throws Exception {

		Hashtable result = new Hashtable();

		java.lang.String res = this.service.GetProperty( (java.lang.String)args.get(PROPERTY));
		result.put(PROPERTYVALUE, res);
		return result;
	}

}






