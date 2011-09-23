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
package eu.linksmart.selfstar.aql.osgisensor;

import eu.linksmart.selfstar.aql.db.Schema;
import eu.linksmart.selfstar.aql.db.SchemaException;
import eu.linksmart.selfstar.aql.db.Tuple;

public class ClientConnectionSchema extends Schema{

	int did, cid, targetadr,protocol;
	
	private static ClientConnectionSchema instance;
	
	public static ClientConnectionSchema instance(){
		if (instance==null)
			instance=new ClientConnectionSchema();
		return instance;
	}

	
	public ClientConnectionSchema(){
		super("ClientConnectionSchema");
		addField("DeviceID", String.class,30);
		addField("ClientID",String.class);
		addField("TargetEndpoint",String.class);
		addField("Protocol", String.class,150);
		did=getIntFromName("DeviceID");
		cid=getIntFromName("ClientID");
		targetadr=getIntFromName("TargetEndpoint");
		protocol=getIntFromName("Protocol");
	}
	
	public Tuple newTuple(String DeviceID, String ClientID, String TargetEndpoint, String Protocol){
		Tuple t=newTuple();
		try{
			t.setValue(did, DeviceID);
			t.setValue(cid, ClientID);
			t.setValue(targetadr, TargetEndpoint);
			t.setValue(protocol, Protocol);
		} catch (SchemaException se){
			//this won't happen;
			se.printStackTrace();
		}
		return t;

	}
}
