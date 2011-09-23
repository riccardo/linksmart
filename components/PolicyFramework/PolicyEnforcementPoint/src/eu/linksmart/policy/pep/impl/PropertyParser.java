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
 * Copyright (C) 2006-2010 University of Reading,
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
package eu.linksmart.policy.pep.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import eu.linksmart.policy.pep.impl.PropertyParser;

/**
 * <p>SAX parser that creates {@link Properties} from an XML data 
 * <code>String</code></p>
 * 
 * <p>This implementation should be faster than the Sun Java 
 * <code>loadFromXml</code> method in {@link Properties} and can be used when 
 * reducing time spent parsing from an XML <code>String</code> is important.</p>
 * 
 * @author Marco Tiemann
 *
 */
public class PropertyParser {
	
	/** logger */
	static final Logger logger = Logger.getLogger(PropertyParser.class);
	
	/** {@link XMLReader} */
	private XMLReader xmlReader = null;
	
	/** {@link PropContentHandler} */
	private PropContentHandler xmlContentHandler = null;
	
	/** No-args constructor */
	public PropertyParser() {
		super();
		try {
			xmlReader = XMLReaderFactory.createXMLReader();
			xmlContentHandler = new PropContentHandler();
			xmlReader.setContentHandler(xmlContentHandler);
			PropErrorHandler errorHandler = new PropErrorHandler();
			xmlReader.setErrorHandler(errorHandler);
			xmlReader.setFeature("http://apache.org/xml/features/" 
					+ "nonvalidating/load-external-dtd", false);
		} catch (SAXException sae) {
			logger.error("SAXException: " + sae.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", sae);
			}
		}
	}

	/**
	 * <p>Parses and XML <code>String</code> into a {@link Properties} 
	 * instance</p>
	 * 
	 * @param thePropXml
	 * 				the property XML <code>String</code>
	 * @return
	 * 				the {@link Properties}
	 */
	public Properties parseXml(String thePropXml) {
		ByteArrayInputStream bais = 
				new ByteArrayInputStream(thePropXml.getBytes());
		try {
			xmlReader.parse(new InputSource(bais));
			bais.close();
			return xmlContentHandler.properties;
		} catch (SAXException sae) {
			logger.error("SAXException: " + sae.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", sae);
			}
		} catch (IOException ioe) {
			logger.error("IOException: " + ioe.getLocalizedMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("Stack trace: ", ioe);
			}
		}
		return null;
	}
	
	
	/**
	 * <p>SAX {@link ErrorHandler} implementation</p>
	 * 
	 * @author Marco Tiemann
	 *
	 */
	class PropErrorHandler implements ErrorHandler {

		/* (non-Javadoc)
		 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
		 */
		@Override
		public void error(SAXParseException theException) throws SAXException {
			throw new SAXException(theException);
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ErrorHandler#fatalError(
		 * 		org.xml.sax.SAXParseException)
		 */
		@Override
		public void fatalError(SAXParseException theException) 
				throws SAXException {
			throw new SAXException(theException);
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
		 */
		@Override
		public void warning(SAXParseException theException) 
				throws SAXException {
			throw new SAXException(theException);
		}
	}
	
	
	/**
	 * <p>SAX {@link ContentHandler} implementation</p>
	 * 
	 * @author Marco Tiemann
	 *
	 */
	class PropContentHandler implements ContentHandler {
		
		/** entry text */
		private static final String ENTRY = "ENTRY";
		
		/** {@link Properties} */
		public Properties properties = null;
		
		/** key */
		private String key = null;
		
		/** value */
		private String value = null;
		
		@Override
		public void startDocument() throws SAXException {
			properties = new Properties();
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#endDocument()
		 */
		@Override
		public void endDocument() throws SAXException {
			// intentionally left blank
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, 
		 * 		java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		@Override
		public void startElement(String theUri, String theLocalName, 
				String theQName, Attributes theAttributes) throws SAXException {
			if ((theQName.equalsIgnoreCase(ENTRY)) 
					&& (theAttributes != null)) {
				int al = theAttributes.getLength();
	           	for (int i = 0; i < al; i++) {
	           		key = theAttributes.getValue(i);
	           	}	        
			}
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
		 */
		@Override
		public void characters(char[] theCh, int theStart, int theLength)
				throws SAXException {
	        value = new String(theCh, theStart, theLength);
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, 
		 * 		java.lang.String, java.lang.String)
		 */
		@Override
		public void endElement(String theUri, String theLocalName, 
				String theQName) throws SAXException {
			if (theQName.equalsIgnoreCase(ENTRY)) {
				if (key != null) {
					properties.setProperty(key, value);
				}
				key = null;
				value = null;        
			}
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
		 */
		@Override
		public void ignorableWhitespace(char[] theCh, int theStart, 
				int theLength) throws SAXException {
			// intentionally left blank
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, 
		 * 		java.lang.String)
		 */
		@Override
		public void startPrefixMapping(String thePrefix, String theUri)
				throws SAXException {
			// intentionally left blank
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
		 */
		@Override
		public void endPrefixMapping(String thePrefix) throws SAXException {
			// intentionally left blank
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#processingInstruction(
		 * 		java.lang.String, java.lang.String)
		 */
		@Override
		public void processingInstruction(String theTarget, String theData)
				throws SAXException {
			// intentionally left blank
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#setDocumentLocator(
		 * 		org.xml.sax.Locator)
		 */
		@Override
		public void setDocumentLocator(Locator theLocator) {
			// intentionally left blank
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
		 */
		@Override
		public void skippedEntity(String theName) throws SAXException {
			// intentionally left blank
		}
		
	}
	
}
