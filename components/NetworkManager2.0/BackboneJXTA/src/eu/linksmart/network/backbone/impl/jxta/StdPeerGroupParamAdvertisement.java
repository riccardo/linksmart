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
 * Copyright (C) 2006-2010 [Telefonica I+D]
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

/**
 * Creates a standard peer group advertisment
 */

package eu.linksmart.network.backbone.impl.jxta;
	
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.Document;
import net.jxta.document.StructuredDocumentUtils;
import net.jxta.document.StructuredTextDocument;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.Element;
import net.jxta.document.TextElement;
import net.jxta.document.MimeMediaType;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;

import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.platform.ModuleClassID;
import net.jxta.platform.ModuleSpecID;

import java.net.URI;
import java.util.Hashtable;
import java.util.Enumeration;


/**
 * StdPeerGroupParamAdvertisement
 */
public class StdPeerGroupParamAdvertisement {

	private static final String paramTag = "Parm";
	private static final String protoTag = "Proto";
	private static final String appTag = "App";
	private static final String svcTag = "Svc";
	private static final String mcidTag = "MCID";
	private static final String msidTag = "MSID";
	private static final String miaTag =
		ModuleImplAdvertisement.getAdvertisementType();
	private Hashtable servicesTable = null;
	private Hashtable protosTable = null;
	private Hashtable appsTable = null;

	/**
	 * @deprecated
	 */
	public StdPeerGroupParamAdvertisement() {
		/* Set default. */
		servicesTable = new Hashtable();
		protosTable = new Hashtable();
		appsTable = new Hashtable();
	}
	
	/** 
	 * @param root the root element
	 * @deprecated
	 */
	public StdPeerGroupParamAdvertisement(Element root)
			throws PeerGroupException {
		
		try {
			initialize(root);
		} catch(Throwable any) {
			throw new PeerGroupException(any);
		}
	}
	
	/**
	 * Gets the services
	 * @return the services
	 * @deprecated
	 */
	public Hashtable getServices() {
		return servicesTable;
	}
	
	/**
	 * Gets the protos
	 * @return the protos
	 * @deprecated
	 */
	public Hashtable getProtos() {
		return protosTable;
	}
	
	/**
	 * Gets the apps
	 * @return the apps
	 * @deprecated
	 */
	public Hashtable getApps() {
		return appsTable;
	}
	
	/**
	 * Sets the services
	 * @param servicesTable the services
	 * @deprecated
	 */
	public void setServices(Hashtable servicesTable) {
		if (servicesTable == null) {
			this.servicesTable = new Hashtable();
		}
		else {
			this.servicesTable = servicesTable;
		}
	}
	
	/**
	 * Sets the protos
	 * @param protosTable the protos
	 * @deprecated
	 */
	public void setProtos(Hashtable protosTable) {
		if (protosTable == null) {
			this.protosTable = new Hashtable();
		}
		else {
			this.protosTable = protosTable;
		}
	}
	
	/**
	 * Sets the apps
	 * @param appsTable the apps
	 * @deprecated
	 */
	public void setApps(Hashtable appsTable) {
		if (appsTable == null) {
			this.appsTable = new Hashtable();
		}
		else {
			this.appsTable = appsTable;
		}
	}
	
	/**
	 * Initialize
	 * @param root the root element
	 * @throws Exception
	 * @deprecated
	 */
	public void initialize(Element root) throws Exception {
		if (!TextElement.class.isInstance(root)) {
			throw new IllegalArgumentException(getClass().getName() 
				+ " only supports TextElement");
		}
		
		TextElement doc = (TextElement) root;
		if (!doc.getName().equals(paramTag)) {
			throw new IllegalArgumentException("Could not construct : " 
				+ getClass().getName() + "from doc containing a " + doc.getName());
		}

		/* Set defaults. */
		servicesTable = new Hashtable();
		protosTable = new Hashtable();
		appsTable = new Hashtable();
		
		int appCount = 0;
		Enumeration modules = doc.getChildren();
		while (modules.hasMoreElements()) {
			Hashtable theTable;
			
			TextElement module = (TextElement) modules.nextElement();
			String tagName = module.getName();
			if (tagName.equals(svcTag)) {
				theTable = servicesTable;
			} else if (tagName.equals(appTag)) {
				theTable = appsTable;
			} else if (tagName.equals(protoTag)) {
				theTable = protosTable;
			} else {
				continue;
			}
			ModuleSpecID specID = null;
			ModuleClassID classID = null;
			ModuleImplAdvertisement inLineAdv = null;
	 
			try {
				if (module.getTextValue() != null) {
					specID = (ModuleSpecID) IDFactory.fromURI(new
						URI(module.getTextValue()));
				}
				
				/* Check for children anyway. */
				Enumeration fields = module.getChildren();
				while (fields.hasMoreElements()) {
					TextElement field = (TextElement) fields.nextElement();
					if (field.getName().equals(mcidTag)) {
						classID = (ModuleClassID) IDFactory.fromURI(new
							URI(module.getTextValue()));
						continue;
					}
					
					if (field.getName().equals(msidTag)) {
						specID = (ModuleSpecID) IDFactory.fromURI(new 
							URI(module.getTextValue()));
						continue;
					}
					
					if (field.getName().equals(miaTag)) {
						inLineAdv = (ModuleImplAdvertisement)
							AdvertisementFactory.newAdvertisement(field);
						continue;
					}
				}
			} catch (Exception any) {
				continue;
			}
	 
			if (inLineAdv == null && specID == null) {
				continue;
			}

			Object theValue;
			if (inLineAdv == null) {
				theValue = specID;
			}
			else {
				specID = inLineAdv.getModuleSpecID();
				theValue = inLineAdv;
			}
			
			if (classID == null) {
				classID = specID.getBaseClass();
			}
	 
			/* 
			 * For applications, the role does not matter. We just create
			 * a unique role ID on the fly. When outputing the add we get 
			 * rid of it to save space.
			 */
			if (theTable == appsTable) {
				/* Only the first (or only) one may use the base class. */
				if (appCount++ != 0) {
					classID = IDFactory.newModuleClassID(classID);
				}
			}
			
			theTable.put(classID, theValue);
		}
	}
	
	/**
	 * Gets the document
	 * @param encodeAs the encoding
	 * @return the document
	 * @deprecated
	 */
	public Document getDocument(MimeMediaType encodeAs) {
		StructuredTextDocument doc = null;
		
		doc = (StructuredTextDocument)
			StructuredDocumentFactory.newStructuredDocument(encodeAs, paramTag);
		
		outputModules(doc, servicesTable, svcTag, encodeAs);
		outputModules(doc, protosTable, protoTag, encodeAs);
		outputModules(doc, appsTable, appTag, encodeAs);
		return doc;
	}
		
	/**
	 * outputmodules
	 * @param doc the documents
	 * @param modulesTable the modules
	 * @param mainTag the main tag
	 * @param encodeAs the encoding
	 * @deprecated
	 */
	private void outputModules(StructuredTextDocument doc, Hashtable modulesTable,
			String mainTag, MimeMediaType encodeAs) {
		
		Enumeration allClasses = modulesTable.keys();
		while (allClasses.hasMoreElements()) {
			ModuleClassID mcid = (ModuleClassID) allClasses.nextElement();
			Object val = modulesTable.get(mcid);
			
			/* 
			 * For applications, we ignore the role ID. It is not meaningfull,
			 * and a new one is assigned on the fly when loading this adv.
			 */
			if (val instanceof Advertisement) {
				TextElement m = doc.createElement(mainTag);
				doc.appendChild(m);
				
				if (!(modulesTable == appsTable || mcid.equals(mcid.getBaseClass()))) {
					/* It is not an app and there is a role ID. Output it. */
					TextElement i = doc.createElement(mcidTag, mcid.toString());
					m.appendChild(i);
				}
				
				StructuredTextDocument advdoc = (StructuredTextDocument)
					((Advertisement) val).getDocument(encodeAs);
				StructuredDocumentUtils.copyElements(doc, m, advdoc);
			}
			else if (val instanceof ModuleSpecID) {
				TextElement m;
				
				if (modulesTable == appsTable || mcid.equals(mcid.getBaseClass())) {
					/*
					 * Either it is an app or there is no role ID.
					 * So the specId is good enough.
					 */
					m = doc.createElement(mainTag, ((ModuleSpecID) val).toString());
					doc.appendChild(m);
				}
				else {
					/* The role ID matters, so the classId must be separate. */
					m = doc.createElement(mainTag);
					doc.appendChild(m);

					TextElement i;
					i = doc.createElement(mcidTag, mcid.toString());
					m.appendChild(i);
					
					i = doc.createElement(msidTag, ((ModuleSpecID) val).toString());
					m.appendChild(i);
				}
			}
		}
	}
		 
}
