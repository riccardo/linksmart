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


public class NMResponse {

	private java.util.Vector data;
	private java.util.Vector sessionID;

	private String SOAPMessageLine;
	

	public NMResponse(){
		this.SOAPMessageLine = "";
		this.data = new java.util.Vector();
		this.sessionID = new java.util.Vector();
	}
	
	public NMResponse(java.util.Vector thedata, java.util.Vector thesessionID){
		this.SOAPMessageLine = "";
		data = thedata;
		sessionID = thesessionID;
	}
	
	public boolean convertStringToBoolean(String target){
		if(target.equalsIgnoreCase("true"))
			return true;
		return false;
	}
	
	public java.util.Vector getdata(){
		return this.data;
	}
	
	public int data_getElements(){
		return this.data.size();
	}
	
	public String data_getElement(int position){
		return ((String)this.data.elementAt(position));
	}
	public java.util.Vector getsessionID(){
		return this.sessionID;
	}
	
	public int sessionID_getElements(){
		return this.sessionID.size();
	}
	
	public String sessionID_getElement(int position){
		return ((String)this.sessionID.elementAt(position));
	}
	
	public void data_add(String newdata){
		data.addElement(newdata);
	}
	public void sessionID_add(String newsessionID){
		sessionID.addElement(newsessionID);
	}

	public NMResponse getFromSOAPLine(String SOAPLine){
		String begin = "";
		String end = "";
		int last;
		begin = "<data>";
		end = "</data>";
		last = SOAPLine.indexOf(end);
		while(last <= SOAPLine.length()-end.length()){
			String part = SOAPLine.substring(SOAPLine.indexOf(">")+1, SOAPLine.indexOf(end));
			this.data_add(part);
			SOAPLine = SOAPLine.substring(last+end.length());
			last = SOAPLine.indexOf(end);
		}
		begin = "<sessionID>";
		end = "</sessionID>";
		last = SOAPLine.indexOf(end);
		while(last <= SOAPLine.length()-end.length()){
			String part = SOAPLine.substring(SOAPLine.indexOf(">")+1, SOAPLine.indexOf(end));
			this.sessionID_add(part);
			SOAPLine = SOAPLine.substring(last+end.length());
			last = SOAPLine.indexOf(end);
		}
		return this;
	}
	
	public String getSOAPMessageLine(){return this.SOAPMessageLine;}
	
	public String setSOAPMessageLine(){
		this.SOAPMessageLine = "";
		for(int i=0; i<data_getElements();i++){
			this.SOAPMessageLine = this.SOAPMessageLine.concat("<data>");
			this.SOAPMessageLine = this.SOAPMessageLine.concat(data.elementAt(i).toString());
			this.SOAPMessageLine = this.SOAPMessageLine.concat("</data>");
		}
		this.SOAPMessageLine = "";
		for(int i=0; i<sessionID_getElements();i++){
			this.SOAPMessageLine = this.SOAPMessageLine.concat("<sessionID>");
			this.SOAPMessageLine = this.SOAPMessageLine.concat(sessionID.elementAt(i).toString());
			this.SOAPMessageLine = this.SOAPMessageLine.concat("</sessionID>");
		}
		return this.SOAPMessageLine;
	}

}