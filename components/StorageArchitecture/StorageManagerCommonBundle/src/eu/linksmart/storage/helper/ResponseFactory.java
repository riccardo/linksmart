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

package eu.linksmart.storage.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

//import org.apache.log4j.Logger;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * @author Felix Dickehage <skyfox@mail.uni-paderborn.de> as SkyfoxM on
 *         14.05.2009
 * 
 */
public class ResponseFactory {
	
	public static Response readResponse(String response) throws JDOMException, IOException {
		Document d = new SAXBuilder().build(response);
		Element root = d.getRootElement();
		String rootName = root.getName();
		if (rootName.equalsIgnoreCase(VoidResponse.ROOT_TYPE))
			return new VoidResponse(response);
		if (rootName.equalsIgnoreCase(BooleanResponse.ROOT_TYPE))
			return new BooleanResponse(response);
		if (rootName.equalsIgnoreCase(IntegerResponse.ROOT_TYPE))
			return new IntegerResponse(response);
		if (rootName.equalsIgnoreCase(LongResponse.ROOT_TYPE))
			return new LongResponse(response);
		if (rootName.equalsIgnoreCase(StatFSResponse.ROOT_TYPE))
			return new StatFSResponse(response);
		if (rootName.equalsIgnoreCase(StringResponse.ROOT_TYPE))
			return new StringResponse(response);
		if (rootName.equalsIgnoreCase(StringVectorResponse.ROOT_TYPE))
			return new StringVectorResponse(response);
		if (rootName.equalsIgnoreCase(LinkSmartFileResponse.ROOT_TYPE))
			return new LinkSmartFileResponse(response);
		if (rootName.equalsIgnoreCase(LinkSmartFileVectorResponse.ROOT_TYPE))
			return new LinkSmartFileVectorResponse(response);
		if (rootName.equalsIgnoreCase(DictionaryResponse.ROOT_TYPE))
			return new DictionaryResponse(response);
		if (rootName.equalsIgnoreCase(LockResultResponse.ROOT_TYPE))
			return new LockResultResponse(response);
		return null;
	}

	public static VoidResponse readVoidResponse(String response) {
		try {
			return new VoidResponse(response);
		} catch (JDOMException e) {
			//System.err.println("Non XML:\n" + response);
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			//System.err.println("IO Exception while reading from String? I do not think so....");
			e.printStackTrace();
			return null;
		}
	}

	public static BooleanResponse readBooleanResponse(String response) {
		try {
			return new BooleanResponse(response);
		} catch (JDOMException e) {
			//System.err.println("Non XML:\n" + response);
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			//System.err.println("IO Exception while reading from String? I do not think so....");
			e.printStackTrace();
			return null;
		}
	}
	
	public static DictionaryResponse readDictionaryResponse(String response) {
		try {
			return new DictionaryResponse(response);
		} catch (JDOMException e) {
			//System.err.println("Non XML:\n" + response);
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			//System.err.println("IO Exception while reading from String? I do not think so....");
			e.printStackTrace();
			return null;
		}
	}

	public static StatFSResponse readStatFSResponse(String response) {
		try {
			return new StatFSResponse(response);
		} catch (JDOMException e) {
			//System.err.println("Non XML:\n" + response);
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			//System.err.println("IO Exception while reading from String? I do not think so....");
			e.printStackTrace();
			return null;
		}
	}

	public static StringResponse readStringResponse(String response) {
		try {
			return new StringResponse(response);
		} catch (JDOMException e) {
			//System.err.println("Non XML:\n" + response);
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			//System.err.println("IO Exception while reading from String? I do not think so....");
			e.printStackTrace();
			return null;
		}
	}

	public static LongResponse readLongResponse(String response) {
		try {
			return new LongResponse(response);
		} catch (JDOMException e) {
			//System.err.println("Non XML:\n" + response);
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			//System.err.println("IO Exception while reading from String? I do not think so....");
			e.printStackTrace();
			return null;
		}
	}

	public static IntegerResponse readIntegerResponse(String response) {
		try {
			return new IntegerResponse(response);
		} catch (JDOMException e) {
			//System.err.println("Non XML:\n" + response);
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			//System.err.println("IO Exception while reading from String? I do not think so....");
			e.printStackTrace();
			return null;
		}
	}

	public static LinkSmartFileResponse readLinkSmartFileResponse(String response) {
		try {
			return new LinkSmartFileResponse(response);
		} catch (JDOMException e) {
			//System.err.println("Non XML:\n" + response);
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			//System.err.println("IO Exception while reading from String? I do not think so....");
			e.printStackTrace();
			return null;
		}
	}

	public static LinkSmartFileVectorResponse readLinkSmartFileVectorResponse(
			String response) {
		try {
			return new LinkSmartFileVectorResponse(response);
		} catch (JDOMException e) {
			//System.err.println("Non XML:\n" + response);
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			//System.err.println("IO Exception while reading from String? I do not think so....");
			e.printStackTrace();
			return null;
		}

	}

	public static StringVectorResponse readStringVectorResponse(String response) {
		try {
			return new StringVectorResponse(response);
		} catch (JDOMException e) {
			//System.err.println("Non XML:\n" + response);
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			//System.err.println("IO Exception while reading from String? I do not think so....");
			e.printStackTrace();
			return null;
		}
	}

	public static LockResultResponse readLockResultResponse(String response) {
		try {
			return new LockResultResponse(response);
		} catch (JDOMException e) {
			System.err.println("Non XML:\n" + response);
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.err.println("IO Exception while reading from String? I do not think so....");
			e.printStackTrace();
			return null;
		}
	}
		
	public static String createVoidResponse(int errorcode, String errorMessage) {
		return new VoidResponse(errorcode, errorMessage).toXMLString();
	}

	public static String createBooleanResponse(int errorcode,
			String errorMessage, Boolean result) {
		return new BooleanResponse(errorcode, errorMessage, result)
				.toXMLString();
	}
	
	public static String createDictionaryResponse(int errorcode,
			String errorMessage, Dictionary<String, String> result) {
		return new DictionaryResponse(errorcode, errorMessage, result)
				.toXMLString();
	}

	public static String createStatFSResponse(int errorcode,
			String errorMessage, StatFS result) {
		return new StatFSResponse(errorcode, errorMessage, result)
				.toXMLString();
	}

	public static String createStringResponse(int errorcode,
			String errorMessage, String result) {
		return new StringResponse(errorcode, errorMessage, result)
				.toXMLString();
	}

	public static String createIntegerResponse(int errorcode,
			String errorMessage, Integer result) {
		return new IntegerResponse(errorcode, errorMessage, result)
				.toXMLString();
	}

	public static String createLongResponse(int errorcode, String errorMessage,
			Long result) {
		return new LongResponse(errorcode, errorMessage, result).toXMLString();
	}

	public static String createLinkSmartFileResponse(int errorcode,
			String errorMessage, LinkSmartFile result) {
		return new LinkSmartFileResponse(errorcode, errorMessage, result)
				.toXMLString();
	}

	public static String createLinkSmartFileVectorResponse(int errorcode,
			String errorMessage, Collection<LinkSmartFile> result) {
		return new LinkSmartFileVectorResponse(errorcode, errorMessage, result)
				.toXMLString();
	}

	public static String createStringVectorResponse(int errorcode,
			String errorMessage, Collection<String> result) {
		return new StringVectorResponse(errorcode, errorMessage, result)
				.toXMLString();
	}
	

	public static String createLockResultResponse(int errorcode,
			String errorMessage, LockResult result) {
		return new LockResultResponse(errorcode, errorMessage, result)
				.toXMLString();
	}
	
	public static Dictionary<String, String> xmlToDictionary(Reader r)
			throws JDOMException, IOException {
		if (r == null) {
			return new Hashtable<String, String>();
		}
		Document d = new SAXBuilder().build(r);
		Element root = d.getRootElement();
		return xmlDataToDictionary(root);
	}

	public static Dictionary<String, String> xmlRequestToDictionary(
			String xmlData) throws JDOMException, IOException {
		if (xmlData == null) {
			return new Hashtable<String, String>();
		}
		return xmlToDictionary(new StringReader(xmlData));
	}

	public static Dictionary<String, String> xmlDataToDictionary(Element e) {
		if (e == null) {
			return new Hashtable<String, String>();
		}
		Dictionary<String, String> result = new Hashtable<String, String>();
		for (Object o : e.getChildren()) {
			Element entry = (Element) o;
			String key = entry.getAttributeValue("key");
			result.put(key, entry.getText());
		}
		return result;
	}
	
	public static void storeDictionaryToFile(Dictionary<String, String> data, File file) throws IOException {
		System.out.println("Shall store something....");
		Element e = ResponseFactory.dictionaryToXML(data);
		Document d = new Document(e);
		XMLOutputter out = new XMLOutputter();
		FileWriter fw = new FileWriter(file);
		out.output(d, fw);
		fw.flush();
		fw.close();
	}
	
	public static Dictionary<String, String> loadDictionaryFromFile(File file) throws IOException {
		FileReader fr = new FileReader(file);
		Document document;
		try {
			document = new SAXBuilder().build(fr);
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		return ResponseFactory.xmlDataToDictionary(document.getRootElement());
	}

	public static Element dictionaryToXML(Dictionary<String, String> data) {
		if (data == null) {
			data = new Hashtable<String, String>();
		}
		Element root = new Element("properties");
		Enumeration<String> keys = data.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = data.get(key);
			Element entry = new Element("property");
			entry.setAttribute("key", key);
			entry.addContent(value);
			root.addContent(entry);
		}
		return root;
	}

	public static String dictionaryToXMLString(Dictionary<String, String> data) {
		if (data == null) {
			data = new Hashtable<String, String>();
		}
		Document d = new Document(dictionaryToXML(data));
		XMLOutputter out = new XMLOutputter();
		return out.outputString(d);
	}
}
