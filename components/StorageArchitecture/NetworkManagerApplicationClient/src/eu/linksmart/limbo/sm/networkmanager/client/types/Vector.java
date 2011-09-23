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

package eu.linksmart.limbo.sm.networkmanager.client.types;


public class Vector {

	private java.util.Vector item;

	private String SOAPMessageLine;
	

	public Vector(){
		this.SOAPMessageLine = "";
		this.item = new java.util.Vector();
	}
	
	public Vector(java.util.Vector theitem){
		this.SOAPMessageLine = "";
		item = theitem;
	}
	
	public boolean convertStringToBoolean(String target){
		if(target.equalsIgnoreCase("true"))
			return true;
		return false;
	}
	
	public java.util.Vector getitem(){
		return this.item;
	}
	
	public int item_getElements(){
		return this.item.size();
	}
	
	public java.lang.Object item_getElement(int position){
		return ((java.lang.Object)this.item.elementAt(position));
	}
	
	public void item_add(java.lang.Object newitem){
		item.addElement(newitem);
	}

	public Vector getFromSOAPLine(String SOAPLine){
		String begin = "";
		String end = "";
		int last;
		begin = "<item>";
		end = "</item>";
		last = SOAPLine.indexOf(end);
		while(last <= SOAPLine.length()-end.length()){
			String part = SOAPLine.substring(SOAPLine.indexOf(">")+1, SOAPLine.indexOf(end));
			this.item_add(part);
			SOAPLine = SOAPLine.substring(last+end.length());
			last = SOAPLine.indexOf(end);
		}
		return this;
	}
	
	public String getSOAPMessageLine(){return this.SOAPMessageLine;}
	
	public String setSOAPMessageLine(){
		this.SOAPMessageLine = "";
		for(int i=0; i<item_getElements();i++){
			this.SOAPMessageLine = this.SOAPMessageLine.concat("<item>");
			this.SOAPMessageLine = this.SOAPMessageLine.concat(item.elementAt(i).toString());
			this.SOAPMessageLine = this.SOAPMessageLine.concat("</item>");
		}
		return this.SOAPMessageLine;
	}

}