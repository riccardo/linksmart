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
 * <b>Class BasicTypesHandling</b>
 * This class is responsible for mapping data types defined in a WSDL to a String representation
 * of the data type in the respective generated language.
 *
 */
public class BasicTypesHandling {
	
	/**
	 * <b>types</b> : HashMap mapping an LinkSmartKeyPair (defined by a language and a type) 
	 * 				  to a String representation of the data type in the respective language.
	 */
	private HashMap<LinkSmartKeyPair, String> types;
	
	/**
	 * <b>BasicTypesHandling Constructor</b>
	 * It initializes the HashMap types with data types of JSE and JME languages.
	 */
	public BasicTypesHandling(){
		this.types = new HashMap<LinkSmartKeyPair, String>();
		this.types.put(new LinkSmartKeyPair("JSE", "int"), "java.lang.Integer");
		this.types.put(new LinkSmartKeyPair("JSE", "string"), "java.lang.String");
		this.types.put(new LinkSmartKeyPair("JSE", "dateTime"), "java.util.Date");
		this.types.put(new LinkSmartKeyPair("JSE", "boolean"), "java.lang.Boolean");
		this.types.put(new LinkSmartKeyPair("JSE", "float"), "java.lang.Float");
		this.types.put(new LinkSmartKeyPair("JSE", "double"), "java.lang.Double");
		this.types.put(new LinkSmartKeyPair("JSE", "anyURI"), "java.net.URI");
		this.types.put(new LinkSmartKeyPair("JSE", "duration"), "java.util.Time");
		this.types.put(new LinkSmartKeyPair("JSE", "time"), "java.util.Time");
		this.types.put(new LinkSmartKeyPair("JSE", "date"), "java.util.Date");
		this.types.put(new LinkSmartKeyPair("JME", "int"), "java.lang.Integer");
		this.types.put(new LinkSmartKeyPair("JME", "string"), "java.lang.String");
		this.types.put(new LinkSmartKeyPair("JME", "dateTime"), "java.util.Date");
		this.types.put(new LinkSmartKeyPair("JME", "boolean"), "java.lang.Boolean");
		this.types.put(new LinkSmartKeyPair("JME", "float"), "java.lang.Float");
		this.types.put(new LinkSmartKeyPair("JME", "double"), "java.lang.Double");
		this.types.put(new LinkSmartKeyPair("JME", "anyURI"), "java.lang.String");
		this.types.put(new LinkSmartKeyPair("JME", "duration"), "java.util.Time");
		this.types.put(new LinkSmartKeyPair("JME", "time"), "java.util.Time");
		this.types.put(new LinkSmartKeyPair("JME", "date"), "java.util.Date");
		this.types.put(new LinkSmartKeyPair("JSE", "vector"), "java.util.Vector");
		this.types.put(new LinkSmartKeyPair("JME", "vector"), "java.util.Vector");
		this.types.put(new LinkSmartKeyPair("JSE", "anyType"), "java.lang.Object");
		this.types.put(new LinkSmartKeyPair("JME", "anyType"), "java.lang.Object");
		this.types.put(new LinkSmartKeyPair("JSE", "long"), "java.lang.Long");
		this.types.put(new LinkSmartKeyPair("JME", "long"), "java.lang.Long");
	}
	
	/**
	 * <b>getType</b>
	 * Returns a String representation of the data type of the given value language.
	 * @param kp LinkSmartKeyPair 
	 * @return data type in String format.
	 */
	public String getType(LinkSmartKeyPair kp){
		return this.types.get(kp);
	}
	
	/**
	 * <b>getMessagePart</b>
	 * @param inputType
	 * @param i
	 * @return
	 */
	public String getMessagePart(String inputType, int i){
		//System.out.println("messagePartType: "+inputType);
		if(inputType.equalsIgnoreCase("boolean") || inputType.equalsIgnoreCase("java.lang.Boolean"))
			return "p.convertStringToBoolean(p.getArg("+i+").toString())";
		else if(inputType.equalsIgnoreCase("string") || inputType.equalsIgnoreCase("java.lang.String"))
			return "p.getArg("+i+").toString()";
		else if(inputType.equalsIgnoreCase("int") || inputType.equalsIgnoreCase("java.lang.Integer"))
			return "Integer.parseInt(p.getArg("+i+").toString())";
		else if(inputType.equalsIgnoreCase("float") || inputType.equalsIgnoreCase("java.lang.Float"))
			return "((Float)(p.getArg("+i+"))).floatValue()";
		else if(inputType.equalsIgnoreCase("double") || inputType.equalsIgnoreCase("java.lang.Double"))
			return "((Double)(p.getArg("+i+"))).doubleValue()";
		else if(inputType.equalsIgnoreCase("long") || inputType.equalsIgnoreCase("java.lang.Long"))
			return "((Long)(p.getArg("+i+"))).longValue()";
		else if(inputType.equalsIgnoreCase("java.util.Date"))
			return "("+inputType+")p.getArg("+i+")";
		else if(inputType.equalsIgnoreCase("java.net.URI"))
			return "("+inputType+")p.getArg("+i+")";
		else if(inputType.equalsIgnoreCase("java.util.Time"))
			return "("+inputType+")p.getArg("+i+")";
	    else if(inputType.equalsIgnoreCase("java.lang.Object"))
			return "("+inputType+")p.getArg("+i+").toString()";
		else
			return "new "+inputType+"().getFromSOAPLine(p.getArg("+i+").toString())";
	}
	
	public String getConvertionOfType(String inputType, String param) {
		if(inputType.equalsIgnoreCase("java.lang.Boolean") || inputType.equalsIgnoreCase("boolean"))
			return "new Boolean("+param+".toString()).booleanValue()";
		else if(inputType.equalsIgnoreCase("java.lang.String") || inputType.equalsIgnoreCase("String"))
			return param+".toString()";
		else if(inputType.equalsIgnoreCase("java.lang.Integer") || inputType.equalsIgnoreCase("Integer"))
			return "Integer.parseInt("+param+".toString())";
		else if(inputType.equalsIgnoreCase("java.lang.Float") || inputType.equalsIgnoreCase("Float"))
			return "((Float)("+param+")).floatValue()";
		else if(inputType.equalsIgnoreCase("java.lang.Double") || inputType.equalsIgnoreCase("Double"))
			return "((Double)("+param+")).doubleValue()";
		else if(inputType.equalsIgnoreCase("java.lang.Long") || inputType.equalsIgnoreCase("Long"))
			return "((Long)("+param+")).longValue()";
		else if(inputType.equalsIgnoreCase("java.util.Date") || inputType.equalsIgnoreCase("Date"))
			return "("+inputType+")"+param;
		else if(inputType.equalsIgnoreCase("java.net.URI"))
			return "("+inputType+")"+param;
		else if(inputType.equalsIgnoreCase("java.util.Time"))
			return "("+inputType+")"+param;
	    else if(inputType.equalsIgnoreCase("java.lang.Object"))
			return "("+inputType+")"+param+".toString()";
		else
			return "new "+inputType+"().getFromSOAPLine("+param+".toString())";
	}
	
	/**
	 * <b>getImports</b>
	 * Returns a String defining the code necessary to generate an import line of the given type.
	 * @param imports
	 * @param inputType
	 * @return 
	 */
	public String getImports(String imports, String inputType){
		if(inputType.equalsIgnoreCase("java.util.Date"))
			imports = imports.concat("import java.text;");
		if((inputType.equalsIgnoreCase("java.util.Date"))||(inputType.equalsIgnoreCase("java.net.URI"))||(inputType.equalsIgnoreCase("java.util.Time")) || (inputType.equalsIgnoreCase("java.util.Vector"))
				  || (inputType.equalsIgnoreCase("java.lang.Object")))
			imports = imports.concat("import "+inputType+";");
		return imports;
		
	}

}
