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
 * Copyright (C) 2006-2010 Fraunhofer SIT,
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

package eu.linksmart.security.cryptomanager.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.EncryptionMethod;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.EncryptionConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXParseException;

import com.sun.org.apache.xpath.internal.XPathAPI;

/**
 * The actual CryptoProcessor that performs XMLEncryption and XMLSignature.
 * 
 * @author Julian Schuette (julian.schuette@sit.fraunhofer.de)
 * 
 */
public class XMLEncProcessor implements CryptoMessageFormatProcessor {

	private final static Logger logger = Logger.getLogger(XMLEncProcessor.class
			.getName());
	private KeyStore ks = KeyManagerImpl.getInstance().getKeystore();
	private Hashtable<String, Key> keyCache = new Hashtable<String, Key>();
	private static final String PRIVATE_SIGNING_KEY_ALIAS = "hydrademo-rsa";
	private static final String PRIVATE_SIGNING_KEY_PASS = "hydrademo";
	private static final String ENCRYPTED_MESSAGE_NAMESPACE =
			"http://linksmart.eu/ns/security";
	private static final String ENCRYPTED_MESSAGE_NAME =
			"linksmart:EncryptedMessage";
	private static final String SIGNED_MESSAGE_NAMESPACE =
			"http://linksmart.eu/ns/security";
	private static final String SIGNED_MESSAGE_NAME = "linksmart:SignedMessage";

	// Initialize the XMLEncryption library as soon as this class is loaded in
	// the VM
	static {
		try {
			org.apache.xml.security.Init.init();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public ClassLoader getContextClassLoader() {
		return AccessController
				.doPrivileged(new PrivilegedAction<ClassLoader>() {

					public ClassLoader run() {
						ClassLoader cl = null;
						try {
							cl = Thread.currentThread().getContextClassLoader();
						} catch (SecurityException ex) {
						}
						return cl;
					}
				});
	}

	/**
	 * Constructor.
	 * 
	 * @throws IOException
	 * @throws SQLException
	 */
	public XMLEncProcessor() throws Exception {
		ClassLoader cl = getContextClassLoader();
		Class clazz;
		if (cl != null) {
			try {
				clazz =
						Class.forName("org.apache.xml.security.Init", false, cl);
			} catch (ClassNotFoundException ex) {
				clazz = Class.forName("org.apache.xml.security.Init");
			}
		} else {
			clazz = Class.forName("org.apache.xml.security.Init");
		}
		try {
			clazz.getDeclaredMethod("init", null).invoke(null, null);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		DBmanagement.getInstance();
	}

	/**
	 * Decrypts a Linksmart message that has been created using the
	 * <code>encryptAsymmetric</code> or <code>encryptSymmetric</code>method.
	 * <p> All relevant information for decrypting the message should be
	 * included in the message itself. In the case of an error, this method
	 * returns null.
	 */
	public String decrypt(String encryptedData) {
		String s_decrypt = null;
		try {
			// Convert input String to XML document
			javax.xml.parsers.DocumentBuilderFactory dbf =
					javax.xml.parsers.DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
			ByteArrayInputStream bis =
					new ByteArrayInputStream(encryptedData.getBytes());
			Document document = db.parse(bis);

			// Check if it seems to be a Linksmart message
			if (document.getElementsByTagName(ENCRYPTED_MESSAGE_NAME)
					.getLength() != 1) {
				logger.warn("WARN: CryptoManager.decrypt: input does not contain "
						+ ENCRYPTED_MESSAGE_NAME
						+ " element. Trying to continue.");
			}

			// Get the "EncryptedData" element from DOM tree.
			Element encryptedDataElement =
					(Element) document.getElementsByTagNameNS(
							EncryptionConstants.EncryptionSpecNS,
							EncryptionConstants._TAG_ENCRYPTEDDATA).item(0);

			// Use the cipher to create an "EncryptedData" object that is
			// required in the rest of this method
			XMLCipher cipher = XMLCipher.getInstance();
			cipher.init(XMLCipher.DECRYPT_MODE, null);
			EncryptedData encryptedDataObject =
					cipher.loadEncryptedData(document, encryptedDataElement);

			Key dek = null;
			if (encryptedDataObject.getKeyInfo().itemEncryptedKey(0) != null) {
				// Retrieve information about the Public Key used to encrypt the
				// DEK
				EncryptedKey ek =
						encryptedDataObject.getKeyInfo().itemEncryptedKey(0);
				String receiverHID =
						encryptedDataObject.getKeyInfo().getTextFromTextChild()
								.trim();
				String algorithm_name = ek.getEncryptionMethod().getAlgorithm();

				String receiverCertRef =
						KeyManagerImpl.getInstance().getPrivateKeyRefByHID(
								receiverHID);

				PrivateKey kdk;
				if (receiverCertRef == null) {
					logger.warn("No private key for HID "
							+ receiverHID
							+ " available. Trying to use the default key for decrypting the message. This is potentially insecure and happens if a non-Crypto-HID communicates.");
					receiverCertRef = "hydrademo_identifier";
					KeyManagerImpl.getInstance().addPrivateKeyForHID(
							receiverHID, receiverCertRef);
				}
				// FIXME Do this mapping externally in a hashmap
				if (algorithm_name
						.equals("http://www.w3.org/2001/04/xmlenc#rsa-1_5"))
					algorithm_name = "RSA";

				// Try to retrieve the corresponding Private Key
				kdk =
						KeyManagerImpl.getInstance().loadKeyDecryptionKey(
								receiverCertRef, algorithm_name);
				logger.debug("KDK is " + kdk);

				// Set the cipher to "Unwrap" mode and use the Private Key to
				// extract the DEK
				cipher.init(XMLCipher.UNWRAP_MODE, kdk);
				dek =
						cipher.decryptKey(ek, encryptedDataObject
								.getEncryptionMethod().getAlgorithm());
			} else {
				String identifier =
						encryptedDataObject.getKeyInfo().getTextFromTextChild();
				dek =
						KeyManagerImpl.getInstance().loadSymmetricKey(
								identifier, "AES");
				cipher = XMLCipher.getInstance();
				cipher.init(XMLCipher.DECRYPT_MODE, dek);
			}
			// Set the cipher to "decrypt" mode and use the DEK to extract the
			// payload.

			cipher.init(XMLCipher.DECRYPT_MODE, dek);
			logger.debug("Key format " + dek.getFormat());
			logger.debug("Key algo " + dek.getAlgorithm());
			logger.debug("Key size " + dek.getEncoded().length);
			cipher.doFinal(document, encryptedDataElement);

			// Convert XML document to String
			s_decrypt = document.getFirstChild().getTextContent();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			s_decrypt = null;
		}

		// Output the decrypted data
		return s_decrypt;
	}

	/**
	 * Encrypts a Linksmart message so it can be opened by the receiver using the
	 * <code>decryptAsymmetric</code> method. <p>
	 * 
	 */
	public String encryptAsymmetric(String documentString, String receiverHid,
			String format) throws Exception {
		if (documentString == null || receiverHid == null || format == null)
			throw new NullPointerException("Null parameter");

		String s_encrypt = "";

		// Create an XML document starting with a "linksmart:EncryptedMessage" root
		// element.
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		Element root =
				doc.createElementNS(ENCRYPTED_MESSAGE_NAMESPACE,
						ENCRYPTED_MESSAGE_NAME);
		root.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:linksmart",
				ENCRYPTED_MESSAGE_NAMESPACE); // ?!

		doc.appendChild(root);
		root.appendChild(doc.createTextNode(documentString));

		// Get a public key (KEK) to encrypt the symmetric key (DEK).
		String identifier =
				KeyManagerImpl.getInstance().getCertRefByHID(receiverHid);
		if (identifier == null) {
			logger.warn("Request to encrypt for HID "
					+ receiverHid
					+ " but no public key is available for this HID. Trying to continue using the default key but this is probably an error.");
			identifier = "hydrademo_identifier";
			KeyManagerImpl.getInstance().addPrivateKeyForHID(receiverHid,
					identifier);
		}
		PublicKey kek =
				KeyManagerImpl.getInstance().loadKeyEncryptionKey(identifier,
						"RSA");
		logger.debug("KEK is " + kek);
		// Get a key to encrypt the data (DEK)
		Key dek = KeyManagerImpl.getInstance().loadDataEncryptionKey();

		// Encrypt the DEK using the KEK. This is called "Wrapping"
		String algorithmURI = XMLCipher.RSA_v1dot5;
		XMLCipher keyCipher = XMLCipher.getInstance(algorithmURI);
		keyCipher.init(XMLCipher.WRAP_MODE, kek);
		EncryptedKey encryptedKey = keyCipher.encryptKey(doc, dek);

		// Add the identifier to the message. So the receiver knows which key to
		// use for decryption.
		// encryptedKey.setCarriedName(identifier);

		// Now do the "real" encryption of the content using AES.
		algorithmURI = XMLCipher.AES_128;
		XMLCipher xmlCipher = XMLCipher.getInstance(algorithmURI);
		xmlCipher.init(XMLCipher.ENCRYPT_MODE, dek);

		// Add the "KeyInfo" element to the message so the receiver gets all
		// necessary information
		EncryptedData encryptedData = xmlCipher.getEncryptedData();
		KeyInfo keyInfo = new KeyInfo(doc);
		keyInfo.add(encryptedKey);
		keyInfo.addText(receiverHid);
		encryptedData.setKeyInfo(keyInfo);
		xmlCipher.doFinal(doc, root, true);

		// Convert XML document into a String
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		DOMSource source = new DOMSource(doc);

		StreamResult sresult = new StreamResult();
		sresult.setOutputStream(new java.io.ByteArrayOutputStream());
		transformer.transform(source, sresult);
		s_encrypt =
				(new StringBuffer()).append(sresult.getOutputStream())
						.toString();
		logger.debug(s_encrypt);

		return s_encrypt;
	}

	/**
	 * Encrypts a Linksmart message so it can be opened by the receiver using the
	 * <code>decryptAsymmetric</code> method. <p>
	 * 
	 */
	public String encryptSymmetric(String documentString, String identifier,
			String format) throws Exception {
		if (documentString == null || identifier == null || format == null)
			throw new NullPointerException("Null parameter");

		String s_encrypt = "";

		// Create an XML document starting with a "linksmart:EncryptedMessage" root
		// element.
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		Element root =
				doc.createElementNS(ENCRYPTED_MESSAGE_NAMESPACE,
						ENCRYPTED_MESSAGE_NAME);
		root.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:linksmart",
				ENCRYPTED_MESSAGE_NAMESPACE); // ?!

		doc.appendChild(root);
		root.appendChild(doc.createTextNode(documentString));

		// Get a key to encrypt the data (DEK)
		// Key dek = KeyManagerImpl.getInstance().loadDataEncryptionKey();

		// Now do the "real" encryption of the content using AES.
		String algorithmURI = XMLCipher.AES_128;
		XMLCipher xmlCipher = XMLCipher.getInstance(algorithmURI);
		Key dek =
				KeyManagerImpl.getInstance()
						.loadSymmetricKey(identifier, "AES");
		xmlCipher.init(XMLCipher.ENCRYPT_MODE, dek);

		// Add the "KeyInfo" element to the message so the receiver gets all
		// necessary information
		EncryptedData encryptedData = xmlCipher.getEncryptedData();
		KeyInfo keyInfo = new KeyInfo(doc);
		keyInfo.addKeyName(identifier);
		keyInfo.addText(identifier);
		EncryptionMethod em =
				xmlCipher.createEncryptionMethod(XMLCipher.AES_128);
		encryptedData.setEncryptionMethod(em);
		encryptedData.setKeyInfo(keyInfo);
		xmlCipher.doFinal(doc, root, true);

		// Convert XML document into a String
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		DOMSource source = new DOMSource(doc);

		StreamResult sresult = new StreamResult();
		sresult.setOutputStream(new java.io.ByteArrayOutputStream());
		transformer.transform(source, sresult);
		s_encrypt =
				(new StringBuffer()).append(sresult.getOutputStream())
						.toString();
		logger.debug(s_encrypt);

		return s_encrypt;
	}

	public String sign(String data, String format, String hid) {
		// method to produce an xml signature
		String s_sign = null;

		// The BaseURI is the URI that's used to prepend to relative URIs
		String BaseURI = SIGNED_MESSAGE_NAMESPACE + "/";

		try {

			Constants.setSignatureSpecNSprefix("");
			data = Base64.encode(data.getBytes());
			// Enumeration<String> aliases = ks.aliases();

			PrivateKey privateKey = null;
			String alias = "";

			// retrieve Private Key for given HID
			String privateKeyRef =
					KeyManagerImpl.getInstance().getPrivateKeyRefByHID(hid);

			if (privateKeyRef == null) {
				logger.warn("No key for hid " + hid
						+ " available. Retrieving key with alias "
						+ PRIVATE_SIGNING_KEY_ALIAS + " from keystore");
				if (keyCache.containsKey(ks.hashCode()
						+ PRIVATE_SIGNING_KEY_ALIAS)) {
					privateKey =
							(PrivateKey) keyCache.get(ks.hashCode()
									+ PRIVATE_SIGNING_KEY_ALIAS);
				} else {
					privateKey =
							(PrivateKey) ks.getKey(PRIVATE_SIGNING_KEY_ALIAS,
									PRIVATE_SIGNING_KEY_PASS.toCharArray());
					keyCache.put(ks.hashCode() + PRIVATE_SIGNING_KEY_ALIAS,
							privateKey);
				}
				KeyManagerImpl.getInstance().addPrivateKeyForHID(hid,
						PRIVATE_SIGNING_KEY_ALIAS);
			} else {
				alias =
						DBmanagement.getInstance().getIdentifierAlias(
								privateKeyRef, "RSA");
				if (keyCache.contains(ks.hashCode() + alias)) {
					privateKey =
							(PrivateKey) keyCache.get(ks.hashCode() + alias);
				} else {
					privateKey =
							(PrivateKey) ks.getKey(alias,
									PRIVATE_SIGNING_KEY_PASS.toCharArray());
					keyCache.put(ks.hashCode() + alias, privateKey);
				}
			}

			// XML Signature needs to be namespace aware
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);

			// Create XML Document
			DocumentBuilder db = dbf.newDocumentBuilder();
			org.w3c.dom.Document doc = db.newDocument();

			Element root =
					doc.createElementNS(SIGNED_MESSAGE_NAMESPACE,
							SIGNED_MESSAGE_NAME);
			root.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:linksmart",
					SIGNED_MESSAGE_NAMESPACE);

			doc.appendChild(root);
			root.appendChild(doc.createTextNode(data));

			// Create an XML Signature object from the document, BaseURI and
			// signature algorithm (in this case DSA)
			XMLSignature sig =
					new XMLSignature(doc, BaseURI,
							XMLSignature.ALGO_ID_SIGNATURE_RSA);

			// Append the signature element to the root element before signing
			// because this is going to be an enveloped signature.
			// This means the signature is going to be enveloped by the
			// document.
			// Two other possible forms are enveloping where the document is
			// inside the signature and detached where they are separate.
			// Note that they can be mixed in 1 signature with separate
			// references as shown below.

			root.appendChild(sig.getElement());

			// NICETOHAVE schauen ob signieren nicht einfacher geht
			// create the transforms object for the Document/Reference
			Transforms transforms = new Transforms(doc);

			// First we have to strip away the signature element (it's not
			// part of the signature calculations). The enveloped transform
			// can be used for this.
			transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
			// Part of the signature element needs to be canonicalized. It
			// is a kind of normalizing algorithm for XML.
			transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);

			// Add the above Document/Reference
			sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);

			// Add in the KeyInfo for the certificate that we used the private
			// key of

			X509Certificate cert = null;

			if (alias.equals("")) {
				cert =
						(X509Certificate) ks
								.getCertificate(PRIVATE_SIGNING_KEY_ALIAS);
			} else {
				cert = (X509Certificate) ks.getCertificate(alias);
			}

			if (cert == null) {
				logger.debug("signing certificate doesn't exist!");
				KeyManagerImpl.getInstance().rsaKeygenerator(
						PRIVATE_SIGNING_KEY_ALIAS);

			}

			sig.addKeyInfo(cert);
			sig.addKeyInfo(cert.getPublicKey());
			logger.info("Using private key of format " + privateKey.getFormat());
			logger.info("Algorithm is " + privateKey.getAlgorithm());
			sig.sign(privateKey);
			logger.debug("Finished signing");

			// Transform DOM tree to XML string.
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes");

			DOMSource source = new DOMSource(doc);
			StreamResult sresult = new StreamResult();
			sresult.setOutputStream(new java.io.ByteArrayOutputStream());
			transformer.transform(source, sresult);

			s_sign =
					(new StringBuffer()).append(sresult.getOutputStream())
							.toString();

		} catch (Exception e) {
			logger.error("Signing failed", e);
			// s_sign = "Failed. " + e.getMessage();
			s_sign = null;
		}

		return s_sign; // String including the Signature
	}

	/**
	 * 
	 * 
	 */
	public String sign(String data, String format) {
		return sign(data, format, "");
	}

	public String[] getSupportedFormats() {
		ArrayList<String> supportedFormats = new ArrayList<String>();
		try {
			Enumeration<String> allAlias = ks.aliases();
			String key = "";

			while (allAlias.hasMoreElements()) {
				String alias = allAlias.nextElement();
				// Publickey Typ wird aus aktuele alias raus genommen
				key = "";

				key = ks.getCertificate(alias).getPublicKey().getAlgorithm();
				// wenn zu den alias kein publickey gefunden wuerde, wird nach
				// privatekey gesucht
				if (key == "")
					key = ks.getKey(alias, new char[0]).getAlgorithm();

				for (String ver : supportedFormats) {
					// If alias already exists - go on.
					if (ver.equals(key) || key.equals(""))
						break;
					else {
						supportedFormats.add(key);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		String[] resultArray = new String[supportedFormats.size()];
		for (int i = 0; i < supportedFormats.size(); i++) {
			resultArray[i] = (String) supportedFormats.get(i);
		}
		return resultArray;
	}

	/**
	 * Verify XML signatures
	 */
	public String verify(String data) {
		String textResult = null;
		boolean result = false;
		// The BaseURI is the URI that's used to prepend to relative URIs
		String BaseURI = SIGNED_MESSAGE_NAMESPACE + "/";
		try {
			// Convert input String to XML document
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);

			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(data.getBytes()));

			// Find Signature element.
			NodeList nlSig = doc.getElementsByTagName("Signature");

			if (nlSig.getLength() == 0) {
				logger.error("Cannot find Signature element");
				return null;
			}

			// certificate wird ausgelesen
			XMLSignature sig =
					new XMLSignature((Element) nlSig.item(0), BaseURI);
			KeyInfo key = sig.getKeyInfo();

			if (key == null) {
				logger.error("Cannot find KeyInfo element");
				return null;
			}

			X509Certificate cert = key.getX509Certificate();
			result = sig.checkSignatureValue(cert);
			if (result) {
				Text textElement =
						(Text) XPathAPI.selectSingleNode(doc,
								"/linksmart:SignedMessage/text()");
				textResult =
						new String(Base64.decode(textElement.getTextContent()));
				logger.debug("Textresult is " + textResult);
			}
		} catch (SAXParseException e) {
			logger.error("Parsing error. " + e.getMessage());
		} catch (Exception ex) {
			logger.error("Verification failed. ", ex);
		}
		return textResult;
	}
}
