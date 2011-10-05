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
package eu.linksmart.security.trustmanager.trustmodel.password.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.apache.log4j.Logger;

import eu.linksmart.security.trustmanager.impl.TrustManagerImpl;
import eu.linksmart.security.trustmanager.trustmodel.TrustModel;
import eu.linksmart.security.trustmanager.trustmodel.password.config.PasswordTrustModelConfiguration;
import eu.linksmart.security.trustmanager.util.Base64;
import eu.linksmart.security.trustmanager.util.Util;

public class PasswordTrustModel implements TrustModel {

	/** Logger used for logging */
	private Logger logger = Logger.getLogger("trustmanager");
	/** Identifier of this Trust Model */
	public static final String IDENTIFIER = "PasswordTrustModel";
	/** Folder path for password model resources	 */
	public static final String PASSWORD_FOLDER_PATH = TrustManagerImpl.TRUSTMANAGER_RESOURCE_FOLDERPATH 
	+ Util.FILE_SEPERATOR + "password";
	/** File path for password file	 */
	public static final String PASSWORD_FILE_PATH = PASSWORD_FOLDER_PATH + Util.FILE_SEPERATOR + "password.pwd";

	private byte[] passwordHash = null;

	@Override
	public double getTrustValue(byte[] token) {
		if(passwordHash == null)
		{
			logger.error("Password has not been set for TrustManager!");
			return 0;
		}
		return (Arrays.equals(passwordHash, createHash(token))? 1.0 : 0.0);
	}

	@Override
	public String getIdentifier() {
		return IDENTIFIER;
	}

	@Override
	public void initialize() {
		//check if folder exists and if not create it
		File dir = new File(PASSWORD_FOLDER_PATH);
		if(!dir.exists()){
			dir.mkdir();
		}
		//read stored password hash
		File f = new File(PASSWORD_FILE_PATH);
		if(f.exists()){
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(f));
				passwordHash = Base64.decode(reader.readLine());
				return;
			} catch (FileNotFoundException e) {
				logger.debug("Password file not found",e);
			} catch (IOException e) {
				logger.error("PasswordTrustModel cannot read stored password");
			}finally{
				if(reader != null){
					try {
						reader.close();
					} catch (IOException e) {
						logger.error("Cannot close password file",e);
					}
				}
			}
		}else{
			logger.warn("PasswordTrustModel has not been initialized yet or stored password cannot be found");
		}
	}

	@Override
	public Class getConfigurator() {
		return PasswordTrustModelConfiguration.class;
	}

	@Override
	public Class getConfiguratorClass() {
		return PasswordTrustModelConfigurationImpl.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.linksmart.security.trustmanager.trustmodel.password.config.PasswordTrustModelConfiguration#setPassword(java.lang
	 * .String)
	 */
	public boolean setPassword(String pass){
		BufferedWriter bw = null;
		boolean stored = false;
		try {
			bw = new BufferedWriter(new FileWriter(PASSWORD_FILE_PATH));
			passwordHash = createHash(pass.getBytes());
			bw.write(Base64.encodeBytes(passwordHash));
			bw.newLine();
			stored = true;
		} catch (IOException e) {
			logger.error("Cannot open file containing password",e);
		} 
		finally{
			try {
				if(bw != null){
					bw.close();
				}
			} catch (IOException e) {
				logger.error("Error closing password file",e);
				stored = false;
			}
		}
		return stored;
	}

	/**
	 * Creates a hash from the provided string
	 * @param pass
	 * @return SHA-256 hash value of string
	 */
	private byte[] createHash(byte[] pass){
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(pass);
			return md.digest();
		} catch (NoSuchAlgorithmException e) {
			logger.error("Your JCE does not support SHA-256. Password cannot be protected and will not work!");
		}
		return null;
	}
}
