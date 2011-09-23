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
 * Copyright (C) 2006-2010,
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

package eu.linksmart.security.trustmanager.trustmodel.x509.config;

import java.io.ByteArrayInputStream;
import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.linksmart.security.trustmanager.trustmodel.TrustModelRegistry;
import eu.linksmart.security.trustmanager.trustmodel.x509.X509TrustModel;
import eu.linksmart.security.trustmanager.util.Base64;

public class X509TrustModelConfigurationImpl implements X509TrustModelConfiguration {

	/** The logger, required for logging */
	private Logger logger = Logger.getLogger("trustmanager");
	
	@Override
	public boolean addCertificate(String alias, String cert) {
		
		TrustModelRegistry registry = TrustModelRegistry.getInstance();
		X509TrustModel x509model = (X509TrustModel) registry.getTrustModel(X509TrustModel.IDENTIFIER);
		
		if (x509model == null) {
			logger.debug("X509 Model not loaded!");
			return false;
		}
		
		byte[] xc = Base64.decode(cert);
		
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X509");
			X509Certificate certificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(xc));
			x509model.addCertificate(alias, certificate);
		} catch (CertificateException e) {
			logger.debug("addTrustAnchorToPolicy: Certificate creation failed!");
			logger.debug(e);
			return false;
		} catch (KeyStoreException ke){
			logger.error("Error saving certificate: ", ke);
		}
		return true;
		
	}
	
	@Override
	public boolean removeCertificateByAlias(String alias) {
		TrustModelRegistry registry = TrustModelRegistry.getInstance();
		X509TrustModel x509model = (X509TrustModel) registry.getTrustModel(X509TrustModel.IDENTIFIER);
		
		return x509model.removeCertificatebyAlias(alias);
	}
	
	@Override
	public boolean removeCertificate(String cert) {	
		TrustModelRegistry registry = TrustModelRegistry.getInstance();
		X509TrustModel x509model = (X509TrustModel) registry.getTrustModel(X509TrustModel.IDENTIFIER);
		
		if (x509model == null) {
			logger.debug("X509 Model not loaded!");
			return false;
		}
		
		byte[] xc = Base64.decode(cert);
		
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X509");
			X509Certificate certificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(xc));
			return x509model.removeCertificate(certificate);
		} catch (CertificateException e) {
			logger.debug("Certificate creation failed!");
			logger.debug(e);
			return false;
		}		
	}
	
	@Override
	public String[] getRootCertificates() {	
		//not implemented
		return null;	
	}
	
}
