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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * <b>Class Types</b>
 */
public class Types {

	/**
	 * <b>type</b> : String specifying the type.
	 */
	String type;
	
	/**
	 * <b>typeName</b> : String specifying the name of the type.
	 */
	String typeName;
	
	/**
	 * <b>isBasicType</b> : Boolean specifying if the type is basic.
	 */
	boolean isBasicType;
	
	/**
	 * <b>isArray</b> : Boolean specifying if the type is an array.
	 */
	boolean isArray;

	boolean isSubTypeBasic;
	
	/**
	 * <b>sequence</b> : List with all the types composing this type (Complex Type).
	 */
	List<Types> sequence;
	
	/**
	 * <b>presentationLine</b> : String specifying the presentation line of this type.
	 */
	String presentationLine;
	
	/**
	 * <b>SOAPMessageLine</b> : String specifying the SOAPMessage line of this type.
	 */
	private String SOAPMessageLine;
	
	/**
	 * <b>argumentLine</b> : String specifying the argumentLine of this type.
	 */
	String argumentLine;
	
	boolean isOptional;
	
	/**
	 * <b>Types Constructor</b>
	 * @param theTypeName String representing the name of the type
	 * @param theType String representing the type.
	 * @param array true if it is an array, false if not.
	 */
	public Types(String theTypeName, String theType, boolean array, boolean isOptional){
		this.type = theType;
		this.typeName = theTypeName;
		if(theType.equalsIgnoreCase("long") || theType.equalsIgnoreCase("java.lang.Long") || 
				theType.equalsIgnoreCase("String") || theType.equalsIgnoreCase("java.lang.String")||
					theType.equalsIgnoreCase("float") || theType.equalsIgnoreCase("java.lang.Float")||
						theType.equalsIgnoreCase("int") || theType.equalsIgnoreCase("java.lang.Integer")||
							theType.equalsIgnoreCase("double") || theType.equalsIgnoreCase("java.lang.Double")||
								theType.equalsIgnoreCase("boolean") || theType.equalsIgnoreCase("java.lang.Boolean")||
									theType.equalsIgnoreCase("java.util.Date")||theType.equalsIgnoreCase("java.net.URI") || 
										theType.equalsIgnoreCase("java.lang.Object"))
			this.isBasicType = true;
		else
			this.isBasicType = false;
		this.isArray = array;
		this.isOptional = isOptional;
		this.sequence = new LinkedList<Types>();
		this.setPresentationLine();
		this.setSOAPMessageLine();
		this.setArgumentLine();
	}
	
	/**
	 * <b>getType</b>
	 * Returns a String representing this type.
	 * @return type field.
	 */
	public String getType(){
		return this.type;
	}
	
	/**
	 * <b>getTypeName</b>
	 * Returns a String representing the name of the type.
	 * @return typeName field.
	 */
	public String getTypeName(){
		return this.typeName;
	}
	
	/**
	 * <b>isBasicType</b>
	 * Returns true if it is a basic type, false if not.
	 * @return isBasicType field.
	 */
	public boolean isBasicType(){
		return this.isBasicType;
	}
	
	/**
	 * <b>getSequence</b>
	 * Returns the sequence of types (complex type) composing this type.
	 * @return List of Types, sequence field.
	 */
	public List<Types> getSequence(){
		return this.sequence;
	}
	/**
	 * <b>getNumberOfElements</b>
	 * Returns a number > 0 of elements that compose an complex type, 0 if it is a basic type. 
	 * @return size of the sequence field.
	 */
	public int getNumberOfElements(){
		return this.sequence.size();
	}
	
	/**
	 * <b>getElements</b>
	 * Returns a String containing all the presentation lines of the types in the sequence field.
	 * @return String containint all the presentation lines of the types in the sequence field separated by "\n".
	 */
	public String getElements(){
		String result = null;
		Iterator<Types> it = this.sequence.iterator();
		while(it.hasNext()){
			result = result.concat(it.next().getPresentationLine()+"\n");
		}
		return result;
	}
	
	/**
	 * <b>getPresentationLine</b>
	 * Returns the presentationLine of this Type.
	 * @return presentationLine field.
	 */
	public String getPresentationLine(){
		return this.presentationLine;
	}
	
	/**
	 * <b>getSOAPMessageLine</b>
	 * Returns the SOAPMessageLine of this Type.
	 * @return SOAPMessageLine field.
	 */
	public String getSOAPMessageLine(){
		return this.SOAPMessageLine;
	}
	
	/**
	 * <b>getArgumentLine</b>
	 * Returns the argument line of this Type.
	 * @return argumentLine field.
	 */
	public String getArgumentLine(){
		return this.argumentLine;
	}
	
	/**
	 * <b>addTypeToSequence</b>
	 * Adds a Type to the sequence List of this Type.
	 * @param theType String representation of the Type to add.
	 */
	public void addTypeToSequence(Types theType){
		this.sequence.add(theType);
		this.setArgumentLine();
	}
	
	/**
	 * <b>isSubTypeBasic</b>
	 * Returns true if the Type in the given position in the sequence is a basic type, false is not.
	 * @param position integer specifying the position in the sequence.
	 * @return true if it is a basic type, false if not.
	 */
	public boolean isSubTypeBasic(int position){
		return this.sequence.get(position).isBasicType();
	}
	
	/**
	 * <b>isArray</b>
	 * Returns true if this type is an Array, false if not.
	 * @return boolean specifying if this Type is an array or not.
	 */
	public boolean isArray(){
		return this.isArray;
	}
	
	/**
	 * <b>setPresentationLine</b>
	 * Sets the presentationLine of this Type.
	 */
	public void setPresentationLine(){
		this.presentationLine = "";
		this.presentationLine = this.presentationLine.concat(this.type);
		if(this.isArray)
			this.presentationLine = "java.util.Vector";
	}
	
	/**
	 * <b>setSOAPMessageLine</b>
	 * Sets the SOAPMessageLine of this Type.
	 */
	public void setSOAPMessageLine(){
		this.SOAPMessageLine = "";
		if(this.type.equalsIgnoreCase("java.util.Date"))
			this.SOAPMessageLine = this.SOAPMessageLine.concat("this."+this.getTypeName()+"= new java.text.SimpleDateFormat("+'"'+"yyyy-MM-dd\'T\'HH:mm:ss.SSSZ"+'"'+").parse(SOAPLine.substring(before.length(), SOAPLine.indexOf(end)),new java.text.ParsePosition(0));");
		else if(this.type.equalsIgnoreCase("java.net.URI"))
			this.SOAPMessageLine = this.SOAPMessageLine.concat("try {\n\tthis."+this.getTypeName()+"= new java.net.URI(SOAPLine.substring(before.length(), SOAPLine.indexOf(end)));\n} catch (java.net.URISyntaxException e) {}");
		else if(this.type.equalsIgnoreCase("java.lang.Integer"))
			this.SOAPMessageLine = this.SOAPMessageLine.concat("this."+this.getTypeName()+"= Integer.parseInt(SOAPLine.substring(before.length(), SOAPLine.indexOf(end)));");
		else if(this.type.equalsIgnoreCase("java.lang.Boolean"))
			this.SOAPMessageLine = this.SOAPMessageLine.concat("this."+this.getTypeName()+"= this.convertStringToBoolean(SOAPLine.substring(before.length(), SOAPLine.indexOf(end)));");
		else if(this.type.equalsIgnoreCase("java.langFloat"))
			this.SOAPMessageLine = this.SOAPMessageLine.concat("this."+this.getTypeName()+"= Float.parseFloat(SOAPLine.substring(before.length(), SOAPLine.indexOf(end)));");
		else if(this.type.equalsIgnoreCase("java.lang.Double"))
			this.SOAPMessageLine = this.SOAPMessageLine.concat("this."+this.getTypeName()+"= Double.parseDouble(SOAPLine.substring(before.length(), SOAPLine.indexOf(end)));");
		else if(this.type.equalsIgnoreCase("java.lang.Object"))
			this.SOAPMessageLine = this.SOAPMessageLine.concat("this."+this.getTypeName()+"= new Object(SOAPLine.substring(before.length(), SOAPLine.indexOf(end)));");
		else
			this.SOAPMessageLine = this.SOAPMessageLine.concat("this."+this.getTypeName()+"= SOAPLine.substring(before.length(), SOAPLine.indexOf(end));");
	}
	
	/**
	 * <b>setArgumentLine</b>
	 * Sets the argumentLine of this Type.
	 */
	public void setArgumentLine(){
		this.argumentLine = "";
		for(int i=0; i<this.sequence.size(); i++){
			if(this.sequence.get(i).isArray){
				if(i==this.sequence.size()-1)
					this.argumentLine = this.argumentLine.concat("java.util.Vector the"+this.sequence.get(i).getTypeName());
				else
					this.argumentLine = this.argumentLine.concat("java.util.Vector the"+this.sequence.get(i).getTypeName()+", ");
			}
			else{
				if(i==this.sequence.size()-1)
					this.argumentLine = this.argumentLine.concat(this.sequence.get(i).getType()+" the"+this.sequence.get(i).getTypeName());
				else
					this.argumentLine = this.argumentLine.concat(this.sequence.get(i).getType()+" the"+this.sequence.get(i).getTypeName()+", ");
			}
		}
	}

	public boolean isOptional() {
		return isOptional;
	}

	public void setOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}

}
