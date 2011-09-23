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
 * NMadvertisment class represents a special type of advertisement created 
 * for the LinkSmart middleware where each Network Manager publishes its own 
 * information such as: name, endpoint address where to contact the Network 
 * Manager, the peerID, the description, time, and HID field. 
 */

package eu.linksmart.network.backbone;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.logging.Logger;

import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.Attributable;
import net.jxta.document.Document;
import net.jxta.document.Element;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocument;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.TextElement;
import net.jxta.id.ID;
import net.jxta.id.IDFactory;

public class NMadvertisement extends Advertisement 
		implements Comparable, Cloneable, Serializable {
	
	/* Creates an advertisements of NM method "receive_data". */
	private String name;
	private String endpoint;
	private ID id = ID.nullID;
	private String description;
	private String time;
	private String date;
	private String HID;

	private final static Logger logger = Logger.getLogger(NMadvertisement.class.getName());
	private final static String nameTag = "name";
	private final static String endpointTag = "endpoint";
	private final static String idTag = "ID";
	private final static String descriptionTag = "description";
	private final static String timeTag = "time";
	private final static String dateTag = "date";
	private final static String HIDTag = "HID";
	
	/**
	 * Indexable fields: Advertisement must define the indexables, in order to
	 * properly index and retrieve these advertisements locally and on the network
	 */
	private final static String[] fields =
		{idTag, nameTag, descriptionTag, HIDTag, endpointTag};
	
	/**
	 * Constructor
	 * @deprecated
	 */
	public NMadvertisement() {}
	
	/**
	 * Construct from a StructuredDocument
	 * 
	 * @param root Root element
	 * @deprecated
	 */
	public NMadvertisement(Element root) {
		TextElement doc = (TextElement) root;
		
		if (!getAdvertisementType().equals(doc.getName())) {
			throw new IllegalArgumentException("Could not construct : " 
				+ getClass().getName() + "from doc containing a " + doc.getName());
		}
		initialize(doc);
	}

	/**
	 * Construct from a InputStream
	 * 
	 * @param stream The underlaying input stream
	 * @throws IOException if an I/O error occurs.
	 * @deprecated
	 */
	public NMadvertisement(InputStream stream) throws IOException {
		StructuredDocument doc = (StructuredDocument) 
			StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, stream);
		initialize(doc);
	}
	
	/**
	 * Sets the advertisement date
	 * 
	 * @param date the advertisement date
	 * @deprecated
	 */
	public void setDate(String date) {
		this.date = date;
	}
	
	/**
	 * Sets the time
	 * 
	 * @param time the time
	 * @deprecated
	 */
	public void setTime(String time) {
		this.time = time;
	}
	
	/**
	 * Sets the description
	 * 
	 * @param description the description
	 * @deprecated
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Sets the endpoint address
	 * 
	 * @param endpoint the endpoint address for this NM
	 * @deprecated
	 */
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	/**
	 * Sets the name
	 * 
	 * @param name the name of NM
	 * @deprecated
	 */
	public void setName(String name) {		
		this.name = name;
	}
	
	/**
	 * Sets the unique id
	 * 
	 * @param id the id
	 * @deprecated
	 */
	public void setID(ID id) {
		this.id = (id == null ? null : id);
	}
	
	/**
	 * Sets the HID's managed
	 * 
	 * @param HID the LinkSmartID
	 * @deprecated
	 */
	public void setHID(String HID) {
		this.HID = HID;
	}
	
	/**
	 * {@inheritDoc}}
	 * 
	 * @param asMimeType document encoding
	 * @return the document value
	 * @deprecated
	 */
	@Override
	public Document getDocument(MimeMediaType asMimeType) {
		StructuredDocument adv = StructuredDocumentFactory.newStructuredDocument(
			asMimeType, getAdvertisementType());
		
		if (adv instanceof Attributable) {
			((Attributable) adv).addAttribute("xmlns:jxta", "http://jxta.org");
		}
		Element e;
		e = adv.createElement(idTag, getID().toString());
		adv.appendChild(e);
		e = adv.createElement(nameTag, getName().trim());
		adv.appendChild(e);
		e = adv.createElement(endpointTag, getEndpoint().trim());
		adv.appendChild(e);
		e = adv.createElement(descriptionTag, getDescription().trim());
		adv.appendChild(e);
		e = adv.createElement(timeTag, getTime().trim());
		adv.appendChild(e);
		e = adv.createElement(dateTag, getDate().toString());
		adv.appendChild(e);
		e = adv.createElement(HIDTag, getHID().toString());
		adv.appendChild(e);		
		return adv;
	}
	
	/**
	 * Gets the advertisement date
	 * 
	 * @return the advertisement date
	 * @deprecated
	 */
	public Object getDate() {		
		return date;
	}
	
	/**
	 * Gets the advertisement time
	 * 
	 * @return the advertisement time
	 * @deprecated
	 */
	public String getTime() {		
		return time;
	}
	
	/**
	 * Gets the description
	 * 
	 * @return the description
	 * @deprecated
	 */	
	public String getDescription() {		
		return description;
	}

	/**
	 * Gets the endpoint address
	 * 
	 * @return the endpoint address for this NM
	 * @deprecated
	 */	
	public String getEndpoint() {		
		return endpoint;
	}

	/**
	 * Gets the  name
	 * 
	 * @return the name
	 * @deprecated
	 */	
	public String getName() {		
		return name;
	}
	
	/**
	 * Gets the unique id
	 * @return the id
	 * @deprecated
	 */	
	@Override
	public ID getID() {
		
		return (id == null ? null : id);
	}

	/**
	 * Gets the LinkSmartID
	 * @return the LinkSmartID
	 * @deprecated
	 */	
	public String getHID() {		
		return HID;
	}
		
	/**
	 * Process an individual element from the document
	 * 
	 * @param elem the element to be processed
	 * @return true if the element was recognized, otherwise false.
	 * @deprecated
	 */	
	protected boolean handleElement(TextElement elem) {
		if (elem.getName().equals(idTag)) {
			try {
				URI id = new URI(elem.getTextValue());
				setID(IDFactory.fromURI(id));
			} catch (URISyntaxException badID) {
				throw new IllegalArgumentException("Unknown ID format in "
					+ "advertisement: " + elem.getTextValue());
			} catch (ClassCastException badID) {
				throw new IllegalArgumentException("ID is not a known id type: "
					+ elem.getTextValue());
			}
			return true;
		}
		
		if (elem.getName().equals(nameTag)) {
			setName(elem.getTextValue());
			return true;
		}
		
		if (elem.getName().equals(descriptionTag)) {
			setDescription(elem.getTextValue());
			return true;
		}
		
		if (elem.getName().equals(endpointTag)) {
			setEndpoint(elem.getTextValue());
			return true;
		}
		
		if (elem.getName().equals(dateTag)) {
			setDate(elem.getTextValue());
			return true;
		}
		
		if (elem.getName().equals(timeTag)) {
			setTime(elem.getTextValue());
			return true;
		}
		
		if (elem.getName().equals(HIDTag)) {
			setHID(elem.getTextValue());
			return true;
		}
		
		/* Element was not handled. */
		return false;
	}

	/**
	 * Initialize a NM advertisement from a portion of a structured document.
	 * 
	 * @param root document root
	 * @deprecated
	 */	
	private void initialize(Element root) {
		if (!TextElement.class.isInstance(root)) {
			throw new IllegalArgumentException(getClass().getName() + " only supports TextElement");
		}
		
		TextElement doc = (TextElement) root;
		if (!doc.getName().equals(getAdvertisementType())) {
			throw new IllegalArgumentException("Could not construct : "
				+ getClass().getName() + " from doc containig a " + doc.getName());
		}
		
		Enumeration elements = doc.getChildren();
		while (elements.hasMoreElements()) {
			TextElement elem = (TextElement) elements.nextElement();
			if (!handleElement(elem)) {
				logger.warning("Unhandleded element \'" + elem.getName() 
					+ "\' in " + doc.getName());
			}
		}
	}

	/**
	 * {@inheritDoc}}
	 * @deprecated
	 */	
	@Override
	public final String[] getIndexFields() {
		return fields;
	}
	
	/**
	 * {@inheritDoc}}
	 * @deprecated
	 */	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj instanceof NMadvertisement) {
			NMadvertisement adv = (NMadvertisement) obj;
			return getID().equals(adv.getID());
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}}
	 * @deprecated
	 */	
	public int compareTo(Object other) {
		return getID().toString().compareTo(other.toString());
		
	}
	/**
	 * All messages have a type which identifies the message
	 * 
	 * @return String "jxta:NM_Advertisement";
	 * @deprecated
	 */	
	public static String getAdvertisementType() {
		return "jxta:NM_advertisement";
	}
	
	
	
	/**
	 * Instantiator
	 */	
	public static class Instantiator implements AdvertisementFactory.Instantiator {
		
		/**
		 * Returns the identifying type of this Advertisement
		 * 
		 * @return String The type of this advertisement
		 * @deprecated
		 */	
		public String getAdvertisementType() {
			return NMadvertisement.getAdvertisementType();
		}
		
		/**
		 * Constructs an instance of advertisement matching the type specified 
		 * by the advertisementType parameter
		 * 
		 * @return The instance of Advertisement
		 * @deprecated
		 */	
		public Advertisement newInstance() {
			return new NMadvertisement();
		}
		
		/**
		 * Construct an instance of Advertisement matching the type specified 
		 * by the advertisement type parameter
		 * 
		 * @param root Specifes a portion of a StructuredDocuemt which will be 
		 * converted into an Advertisement
		 * @return The instance of Advertisement
		 * @deprecated
		 */	
		public Advertisement newInstance(net.jxta.document.Element root) {
			return new NMadvertisement(root);
		}
	}
	
}
