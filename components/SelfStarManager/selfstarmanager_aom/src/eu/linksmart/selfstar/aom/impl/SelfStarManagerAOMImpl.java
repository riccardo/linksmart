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
package eu.linksmart.selfstar.aom.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import eu.linksmart.aom.ApplicationOntologyManager;

import eu.linksmart.selfstar.aom.SelfStarHandler;
import eu.linksmart.selfstar.aom.SelfStarManager;

@Component(immediate=true, provide={SelfStarManager.class})
public class SelfStarManagerAOMImpl implements SelfStarManager, EventHandler {
	private ApplicationOntologyManager aom;
	private SelfStarHandler handler;


	protected void activate(ComponentContext context) {
		handler.setSelfStarManager(this);
		
		Hashtable<Object,Object> ht = new Hashtable<Object,Object>();
		ht.put(EventConstants.EVENT_TOPIC, handler.getTopics());
		context.getBundleContext().registerService(EventHandler.class.getName(), this, ht);
	}

	@Reference
	protected void setSelfStarHandler(SelfStarHandler handler) {
		System.out.println("Set SSh");
		this.handler = handler;
	}

	protected void unsetSelfStarHandler(SelfStarHandler handler) {
		this.handler = null;
	}
	
	@Reference
	protected void setOntologyManager(ApplicationOntologyManager aom) throws IOException {
		this.aom = aom;
		System.out.println("Reading ontology store");
		aom.update(readFile("conf/ontology.owl"));
		aom.removeRunTimeDevices();
	}

	public ApplicationOntologyManager getOntologyManager() {
		return aom;
	}

	protected void unsetOntologyManager(ApplicationOntologyManager aom) {
		this.aom = null;
	}

	private String readFile( String file ) throws IOException {
		BufferedReader reader = new BufferedReader( new FileReader(file));
		String line  = null;
		StringBuilder result = new StringBuilder();
		String ls = System.getProperty("line.separator");
		while((line = reader.readLine() ) != null) {
			result.append(line);
			result.append(ls);
		}
		return result.toString();
	}

	@Override
	public final void handleEvent(Event event) {
		try {
			handler.update(event);
			handler.reason();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
