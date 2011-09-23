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
 * <b>Class LinkSmartKeyPair</b>
 */

public class LinkSmartKeyPair {
	
	/**
	 * <b>language</b> : String representing a programming language
	 */
	private String language;
	/**
	 * <b>type</b> : Data type in String format.
	 */
	private String type;
	
	/**
	 * <b>LinkSmartKeyPair Constructor</b>
	 * Constructs a new instance of LinkSmartKeyPair.
	 * @param theLanguage String representation of a language.
	 * @param theType String representation of a data type.
	 */
	public LinkSmartKeyPair(String theLanguage, String theType){
		this.language = theLanguage;
		this.type = theType;
	}
	
	/**
	 * <b>getLanguage</b>
	 * Returns the language of this LinkSmartKeyPair.
	 * @return String representing a programming language.
	 */
	public String getLanguage(){
		return this.language;
	}
	
	/**
	 * <b>getType</b>
	 * Returns the type of this LinkSmartKeyPair.
	 * @return String representing a data type.
	 */
	public String getType(){
		return this.type;
	}
	
	/**
	 * <b>getKey</b>
	 * Returns a key of this LinkSmartKeyPair.
	 * @return language appended with type.
	 */
	public String getKey(){
		return this.language+this.type;	
	}
	
	/**
	 * <b>equals</b>
	 * Returns true if this LinkSmartKeyPair is equal to the given parameter.
	 * @param obj LinkSmartKeyPair to compare with
	 * @return true if this LinkSmartKeyPair equals to the given LinkSmartKeyPair, false if not.
	 */
	public boolean equals(Object obj){
		LinkSmartKeyPair kp = (LinkSmartKeyPair)obj;
		if((this.language.equalsIgnoreCase(kp.language))&&(this.type.equalsIgnoreCase(kp.type)))
			return true;
		return false;
	}
	
	/**
	 * <b>hashCode</b>
	 * Returns an hash code of this LinkSmartKeyPair
	 * @return the hash code of the language member plus the hash code of the type member.
	 */
	public int hashCode() {
		return this.language.hashCode()+this.type.hashCode();
	}
	
}
