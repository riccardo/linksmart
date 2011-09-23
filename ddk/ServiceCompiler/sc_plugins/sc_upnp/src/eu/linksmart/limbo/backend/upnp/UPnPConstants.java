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
package eu.linksmart.limbo.backend.upnp;

import java.util.HashMap;


public class UPnPConstants {

	static HashMap<String, Class> wsdl2JavaTypes;
	static HashMap<Class, String> java2UPnPTypes;
	static HashMap<String, String> statVarDefaultValue;
	static  {
		wsdl2JavaTypes = new HashMap<String, Class>();
		wsdl2JavaTypes.put("int", Integer.class);
		wsdl2JavaTypes.put("string", String.class);
		wsdl2JavaTypes.put("dateTime", java.util.Date.class);
		wsdl2JavaTypes.put("boolean", Boolean.class);
		wsdl2JavaTypes.put("float", Float.class);
		wsdl2JavaTypes.put("double", Double.class);
		wsdl2JavaTypes.put("anyURI", java.net.URI.class);
		wsdl2JavaTypes.put("duration", Long.class);
		wsdl2JavaTypes.put("time", Long.class);
		wsdl2JavaTypes.put("date", java.util.Date.class);
		wsdl2JavaTypes.put("vector", java.util.Vector.class);
		wsdl2JavaTypes.put("anyType", Object.class);
		wsdl2JavaTypes.put("long", Long.class);
		
		java2UPnPTypes = new HashMap<Class, String>();
		java2UPnPTypes.put(Integer.class, "TYPE_INT");
		java2UPnPTypes.put(String.class, "TYPE_STRING");
		java2UPnPTypes.put(java.util.Date.class, "TYPE_DATE");
		java2UPnPTypes.put(Boolean.class, "TYPE_BOOLEAN");
		java2UPnPTypes.put(Float.class, "TYPE_FLOAT");
		java2UPnPTypes.put(Double.class, "TYPE_NUMBER");
		java2UPnPTypes.put(java.net.URI.class, "TYPE_URI");
		java2UPnPTypes.put(Long.class, "TYPE_UI4");
		java2UPnPTypes.put(java.util.Vector.class, "TYPE_STRING"); //Converted to String because UPnP does not support Vectors
		java2UPnPTypes.put(java.lang.Object.class, "TYPE_STRING"); //Converted to String also*/
		
	    statVarDefaultValue = new HashMap<String,String>();
	    statVarDefaultValue.put("TYPE_INT", "-1");
	    statVarDefaultValue.put("TYPE_STRING", "\"\"");
	    statVarDefaultValue.put("TYPE_DATE", "new java.util.Date()");
	    statVarDefaultValue.put("TYPE_BOOLEAN", "false");
	    statVarDefaultValue.put("TYPE_FLOAT", "-1.0");
	    statVarDefaultValue.put("TYPE_NUMBER", "-1.0");
	    statVarDefaultValue.put("TYPE_URI", "URI");
	    statVarDefaultValue.put("TYPE_UI4", "-1.0");
		
	} 
	
	public static Class toClass(String value) {
		if(wsdl2JavaTypes.containsKey(value))
			return wsdl2JavaTypes.get(value);
		return null;
	}
	
	public static String toString(Class value) {
		if(java2UPnPTypes.containsKey(value))
			return java2UPnPTypes.get(value);
		return null;
	}
	
	public static String getDefault(String value) {
		if(statVarDefaultValue.containsKey(value))
			return statVarDefaultValue.get(value);
		return null;
	}
	
	
}
