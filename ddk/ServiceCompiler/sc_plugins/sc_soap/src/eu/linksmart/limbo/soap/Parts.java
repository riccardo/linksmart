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

/**
 * <b>Class Parts</b>
 * 
 * This Class represents the Parts that constitute a 
 * input argument in the methods, these parts are defined in
 * the Messages in a WSDL file. Also it has a default Result Line
 * for the generation. It variates according to the type of the Part
 * (e.g. String -> "return null;" , int -> "return -1;").
 * All the methods in this class are basic selectors and modifiers.
 *  
 */
public class Parts {

	/**
	 * <b>name</b> : The name of this part.
	 */
	private String name;
	
	/**
	 * <b>type</b> : The type of this part.
	 */
	private String type;
	
	/**
	 * <b>DefaultReturnLine</b> : The return code line of this method.  
	 */
	private String DefaultReturnLine;
	
	/**
	 * <b>messageString</b> : message if this part.
	 */
	private String messageString;
	
	/**
	 * <b>Parts Constructors</b>
	 * Creates a new instance of a Part given a name and type of
	 * a part. Result Line is asigned on Limbo.
	 * 
	 * @param theName - Name of the Part
	 * @param theType - Type of the Part
	 */
	public Parts(String theName, String theType){
		this.name = theName;
		this.type = theType;
	}
	
	/**
	 * <b>getName</b>
	 * Returns the name of this part.
	 * @return field name of this part.
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * <b>getType</b>
	 * Returns the type of this part.
	 * @return field type of this part.
	 */
	public String getType(){
		return this.type;
	}
	
	/**
	 * <b>getMessage</b>
	 * Returns a SOAP line defining the message of this part in String format.
	 * @return SOAP line defining the message of this part.
	 */
	public String getMessage(){
		if(this.type.equalsIgnoreCase("String")||this.type.equalsIgnoreCase("java.lang.String")
				|| this.type.equalsIgnoreCase("float")||this.type.equalsIgnoreCase("java.lang.Float")
				|| this.type.equalsIgnoreCase("int")||this.type.equalsIgnoreCase("java.lang.Integer")
				|| this.type.equalsIgnoreCase("double")||this.type.equalsIgnoreCase("java.lang.Double")
				||this.type.equalsIgnoreCase("boolean")|| this.type.equalsIgnoreCase("java.lang.Boolean") 
				|| this.type.equalsIgnoreCase("long") || this.type.equalsIgnoreCase("java.lang.Long"))
			this.messageString = this.getName();
		else if(this.type.equalsIgnoreCase("java.util.Date")||this.type.equalsIgnoreCase("java.net.URI") || this.type.equalsIgnoreCase("java.lang.Object"))
			this.messageString = this.getName()+".toString()";
		else
			this.messageString = this.getName()+".setSOAPMessageLine()";
		return this.messageString;
	}
	
	/**
	 * <b>getResultLine</b>
	 * Returns the DefaultReturnLine of this Part.
	 * @return String defining the default return line of this Part.
	 */
	public String getResultLine(){
		return this.DefaultReturnLine;
	}
	
	/**
	 * <b>setResultLine</b>
	 * Sets the field DefaultReturnLine to the given value.
	 * @param theResultLine new DefaulReturn value.
	 */
	public void setResultLine(String theResultLine){
		this.DefaultReturnLine = theResultLine;
	}
	
}
