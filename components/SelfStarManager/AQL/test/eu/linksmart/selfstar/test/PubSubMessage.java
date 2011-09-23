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
package eu.linksmart.selfstar.test;

import java.io.Serializable;

public class PubSubMessage implements Serializable{
	
	public static final Integer aqlrequest= new Integer(6000);
	public static final Integer aqlreply = new Integer(6001);
	public static final Integer aqlrequest_ack = new Integer(6002);
	public static final Integer aql_clearforreply = new Integer(6003);
	public static final Integer aql_announce = new Integer(6004);
	
	public static Integer SUBSCRIBE=new Integer(100), PUBLISH=new Integer(101), REGULAR=new Integer(0), COUNTSUB=new Integer(102);
	
	private Integer topic;
	private Integer type;
	
	Serializable payload;
	
	// extend OutputStream/InputStream to count size of objects.
	
	// getAsDatagramPacket()...

	public PubSubMessage(Integer type, Integer topic){
		this.topic = topic;
		this.type = type;
	}
	
	public PubSubMessage(Integer topic, Serializable payload){
		this(PUBLISH,topic);
		this.payload = payload;
		if (payload==null){
			this.payload=new Serializable(){}; // ugly but true...
		}
		//System.out.println("PSM:topic is"+getTopic());
	}
	
	public Integer getTopic(){
		return topic;
	}
	
	public int getTopic_int(){
		return getTopic().intValue();
	}

	public Integer getType(){
		return type;
	}
	
	public Object getPayload(){
		return payload;
	}
	
	public String getPayloadString(){
		return (String)payload;
	}
	
	public boolean isT_SUBSCRIBE(){
		return type.equals(SUBSCRIBE);
	}
	
	public boolean isT_PUBLISH(){
		return type.equals(PUBLISH);
	}
	
	void setTopic(Integer i){
		topic=i;
	}
	
}
