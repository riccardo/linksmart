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
package eu.linksmart.limbo.soap;

import java.util.HashMap;

/**
 * <b>Class TypesHandler</b>
 */

public class TypesHandler {
	
	/**
	 * <b>types</b> : HashMap containing all the types of a WSDL file.
	 */
	private HashMap<String, Types> types;
	/**
	 * <b>conversion</b> : It contains information on conversion of data types defined in a WSDL file
	 * 						to a String representation of the data type in the generated language. 
	 */
	private BasicTypesHandling conversion;
	
	/**
	 * <b>currentType</b> : Variable containing information about the current type being parsed.
	 */
	
	
	public TypesHandler(String theLanguage) {
		this.types = new HashMap<String, Types>(1);
		this.conversion = new BasicTypesHandling();
	}

	/**
	 * <b>getTypes</b>
	 * Returns an HashMap containing information about the types of a WSDL file.
	 * @return field types.
	 */
	public HashMap<String, Types> getTypes(){
		return this.types;
	}
	
	public void addNewType(Types type) {
		this.types.put(type.getType(), type);
	}
	/**
	 * <b>getCorrectType</b>
	 * Returns a String representation of the given type in the given language.
	 * @param theLanguage 
	 * @param theType
	 * @return String representation of a data type in a language.
	 */
	public String getCorrectType(String theLanguage, String theType){
		String s  = this.conversion.getType(new LinkSmartKeyPair(theLanguage, theType));
		if(s == null)
			return theType;
		else
			return s;
	}
	
}

