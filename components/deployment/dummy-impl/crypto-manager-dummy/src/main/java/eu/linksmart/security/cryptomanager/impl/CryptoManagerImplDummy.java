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

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;

import org.apache.felix.scr.annotations.*;
import org.apache.log4j.Logger;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.security.cryptomanager.CryptoManager;
import eu.linksmart.security.cryptomanager.SecurityLevel;

//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.transform.OutputKeys;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
//
//import org.apache.xml.security.encryption.EncryptedData;
//import org.apache.xml.security.encryption.EncryptedKey;
//import org.apache.xml.security.encryption.EncryptionMethod;
//import org.apache.xml.security.encryption.XMLCipher;
//import org.apache.xml.security.keys.KeyInfo;
//import org.apache.xml.security.signature.XMLSignature;
//import org.apache.xml.security.transforms.Transforms;
//import org.apache.xml.security.utils.Base64;
//import org.apache.xml.security.utils.Constants;
//import org.apache.xml.security.utils.EncryptionConstants;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.NodeList;
//import org.w3c.dom.Text;
//import org.xml.sax.SAXParseException;
//
//import javax.crypto.Mac;
//import javax.crypto.KeyGenerator;
//import javax.crypto.SecretKey;
//import javax.crypto.spec.SecretKeySpec;
//
//import org.bouncycastle.jce.provider.BouncyCastleProvider;
//import org.bouncycastle.asn1.x509.X509Name;
//import org.bouncycastle.jce.PrincipalUtil;
//import org.bouncycastle.jce.X509Principal;
//import org.bouncycastle.x509.X509V3CertificateGenerator;
//
//import com.sun.org.apache.xpath.internal.XPathAPI;

@Component(name="CryptoManager", immediate=true)
@Service
@Property(name="service.remote.registration", value="true")
public class CryptoManagerImplDummy implements CryptoManager {

	private static Logger logger = Logger.getLogger(CryptoManagerImplDummy.class);
	private static String SEPARATOR = System.getProperty("file.separator");

	final static public String CONFIGFOLDERPATH = "linksmart" + SEPARATOR + "eu.linksmart.security.cryptomanager" + SEPARATOR + "configuration";
	final static public String RESOURCEFOLDERPATH = "linksmart" + SEPARATOR + "eu.linksmart.security.cryptomanager" + SEPARATOR + "resources";
	
	private CryptoManagerConfigurator configurator;
	
    @Reference(name="ConfigurationAdmin",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindConfigAdmin",
            unbind="unbindConfigAdmin",
            policy=ReferencePolicy.STATIC)
    protected ConfigurationAdmin configAdmin = null;

    protected void bindConfigAdmin(ConfigurationAdmin configAdmin) {
    	System.out.println("CryptoManagerDummy::binding ConfigurationAdmin");
        this.configAdmin = configAdmin;
    }

    protected void unbindConfigAdmin(ConfigurationAdmin configAdmin) {
    	System.out.println("CryptoManagerDummy::un-binding ConfigurationAdmin");
        this.configAdmin = null;
    }

	@Activate
	protected void activate(ComponentContext context) {
		System.out.println("[activating CryptoManagerDummy]");
		configurator = new CryptoManagerConfigurator(this, context.getBundleContext(), configAdmin);
		configurator.registerConfiguration();
	}

	@Deactivate
	protected void deactivate(ComponentContext context) {
		System.out.println("de-activating CryptoManagerDummy");
	}

	@Override
	public String decrypt(String encryptedData) {
		return "";
	}

	@Override
	public String encryptAsymmetric(String documentString, String identifier,
			String format) throws Exception {
		return "";
	}

	@Override
	public String encryptSymmetric(String documentString, String identifier,
			String format) throws Exception {
		return "";
	}

	@Override
	public byte[] getEncodedPublicKeyByIdentifier(String identifier)
	throws KeyStoreException {
		return null;
	}

	@Override
	public Certificate getCertificateByIdentifier(String identifier)
	throws KeyStoreException {
		return null;
	}

	@Override
	public Vector<String> getSupportedFormats() {
		Vector<String> formats = new Vector<String>();
		return formats;
	}

	@Override
	public String sign(String data, String format) {
		return "";
	}

	@Override
	public String sign(String data, String format, String identifier) {
		return "";
	}

	@Override
	public String storePublicKey(String encodedCert, String algorithm_id) {
		return "";
	}

	@Override
	public boolean storePublicKeyWithFriendlyName(String friendlyName, String encodedCert, String algorithm_id) throws SQLException {
		return true;
	}

	@Override
	public String verify(String data) {
		return "";
	}

	@Override
	public String generateCertificateWithAttributes(String xmlAttributes,
			String virtualAddress) throws SQLException, NoSuchAlgorithmException,
			IOException, KeyStoreException, CertificateException,
			InvalidKeyException, SecurityException, SignatureException,
			IllegalStateException, NoSuchProviderException {
		return "";
	}
	
	@Override
	public Properties getAttributesFromCertificate(String identifier)
	throws SQLException, KeyStoreException,
	CertificateEncodingException {
		return new Properties();
	}

	@Override
	public PrivateKey getPrivateKeyByIdentifier(String identifier) {
		return null;
	}

	@Override
	public boolean generateKeyFromPasswordWithFriendlyName(String friendlyName, String password, int keyLength, String algo) throws SQLException, KeyStoreException{
		return true;
	}

	@Override
	public String generateKeyFromPassword(String password, int keyLength, String algo){
		return "";
	}
	
	@Override
	public String generateSymmetricKey() throws SQLException,
	NoSuchAlgorithmException, IOException, KeyStoreException,
	CertificateException, InvalidKeyException, SecurityException,
	SignatureException, IllegalStateException, NoSuchProviderException {
		return "";
	}

	@Override
	public String generateSymmetricKey(String algo) throws SQLException,
			NoSuchAlgorithmException, IOException, KeyStoreException,
			CertificateException, InvalidKeyException, SecurityException,
			SignatureException, IllegalStateException, NoSuchProviderException {
		return "";
	}
	
	@Override
	public String generateSymmetricKey(int keySize, String algo) throws SQLException,
	NoSuchAlgorithmException, IOException, KeyStoreException,
	CertificateException, InvalidKeyException, SecurityException,
	SignatureException, IllegalStateException, NoSuchProviderException {
		return "";
	}

	@Override
	public boolean generateSymmetricKeyWithFriendlyName(String friendlyName, int keysize, String algo) throws InvalidKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException, SecurityException, SignatureException, IllegalStateException, NoSuchProviderException, SQLException, IOException
	{
		return true;		
	}

	@Override
	public String storeSymmetricKey(String algo, String key)
	throws SQLException, NoSuchAlgorithmException, IOException,
	KeyStoreException, CertificateException, InvalidKeyException,
	SecurityException, SignatureException, IllegalStateException,
	NoSuchProviderException {
		return "";
	}

	@Override
	public boolean storeSymmetricKeyWithFriendlyName(String friendlyName, String algo, String key) throws SQLException{
		return true;
	}

	@Override
	public boolean addCertificateForService(String virtualAddress, String certRef) {
		return true;
	}

	@Override
	public boolean addPrivateKeyForService(String virtualAddress, String certRef) {
		return true;
	}

	@Override
	public String getCertificateReference(String virtualAddress) {
		return "";
	}

	@Override
	public String getPrivateKeyReference(String virtualAddress) {
		return "";
	}

	@Override
	public int getKeySize(SecurityLevel level, String algo){
		return 1;
	}

	@Override
	public byte[] calculateMac(String identifier, String data, String algorithm) throws NoSuchAlgorithmException, KeyStoreException, InvalidKeyException{
		return null;
	}

	@Override
	public boolean identifierExists(String identifier){
		return true;
	}

	@Override
	public boolean deleteEntry(String identifier) {
		return true;
	}
}
