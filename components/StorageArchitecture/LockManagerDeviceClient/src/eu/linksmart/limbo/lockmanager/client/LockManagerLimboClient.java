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

package eu.linksmart.limbo.lockmanager.client;

import java.io.File;

import org.apache.commons.lang.StringEscapeUtils;

import eu.linksmart.storage.helper.ConfigLockRequest;
import eu.linksmart.storage.helper.FileLockRequest;
import eu.linksmart.storage.helper.LinkSmartFile;
import eu.linksmart.storage.helper.LockResultResponse;
import eu.linksmart.storage.helper.ResponseFactory;
import eu.linksmart.storage.helper.StringVectorResponse;


public class LockManagerLimboClient {
	/**
	static String theHost;
	static int thePort;
	
	public LockManagerLimboClient(String host, int port){
		theHost = host;
		thePort = port;
	}
	*/
	private static LockManagerLimboClientPortImpl lockManagerClient = null;

	/**
	 * Establishes a connection to s running storage manager.
	 * 
	 * @param address
	 *            IP and port of storage manager.
	 * @example http://192.168.42.211:8083/storagemanage
	 */
	private static void connect(String address) {
		lockManagerClient = new LockManagerLimboClientPortImpl(address);
		if (lockManagerClient == null) {
			System.err.println("Could not connect to that adress.");
		}
	}
	
	public static void main(String argv[]){
	    /**
		String port = "http://localhost:8082";
	    if (argv.length != 0) {
				port = argv[0];
		}
	
		LockManagerLimboClientPortImpl theClient = new LockManagerLimboClientPortImpl(new TCPProtocol(), port); 
		*/
		
		/*Insert method calls here*/
		
		try {
			if (argv.length < 2) {
				printHelp();
				System.exit(0);
			}
			String wsAdress = argv[0];
			String command = argv[1];
			if (command.equalsIgnoreCase("getLockInfo")) {
				if ((argv.length != 3)&&(argv.length != 4)) {
					printHelp();
					System.exit(0);
				}
				String id = argv[2];
				connect(wsAdress);
				if (argv.length == 3){
					ConfigLockRequest lockrequest = new ConfigLockRequest("null",id, ConfigLockRequest.WRITE_TYPE);
					/**
					System.out.println("lockrequest: "+lockrequest);
					System.out.println("-------------------------");
					System.out.println("lockrequest.toXMLString: "+lockrequest.toXMLString());
					System.out.println("-------------------------");
					System.out.println("escapeXml: "+StringEscapeUtils.escapeXml(lockrequest.toXMLString()));
					System.out.println("-------------------------");
					*/
					String response = lockManagerClient.getLockInfo(StringEscapeUtils.escapeXml(lockrequest.toXMLString()));
					/*
					System.out.println("response = lockManagerClient.getLockInfo(lockrequest.toXMLString()): "+response);
					System.out.println("-------------------------");
					*/
					response = StringEscapeUtils.unescapeXml(response);
					/**
					System.out.println("unescapeXML(response): "+ response);
					System.out.println("-------------------------");
					System.out.println("ResponseFactory.readConfigLockResultResponse(response)"+ ResponseFactory.readConfigLockResultResponse(response));
					*/
					LockResultResponse vr = new LockResultResponse(response);
					System.out.println("answer: "+vr.toXMLString());
					if (vr.getErrorCode() != 0) {
						System.err.println("Error " + vr.getErrorCode() + ": "
								+ vr.getErrorMessage());
						System.exit(vr.getErrorCode());
					
					}
				}else if (argv.length == 4){
						    String fileName = argv[3];
						    File file = new File(id+"/"+fileName);
						    if (!file.exists()){
						    	System.out.println("file don't exist.");
						    	file.createNewFile();
						    }else{
						    	System.out.println("file exists.");
						    }
						    System.out.println("file:"+file.getName()+", path: "+file.getPath());
						    LinkSmartFile linksmartFile = new LinkSmartFile(file.getAbsolutePath(), true, file.length(),
									file.lastModified(), file.lastModified(),0,null);
						    System.out.println("file:"+linksmartFile.getName()+",path: "+linksmartFile.getPath());
							FileLockRequest lockrequest = new FileLockRequest("null",id,linksmartFile,FileLockRequest.WRITE_TYPE);
							//
							System.out.println("lockrequest: "+lockrequest);
							System.out.println("-------------------------");
							System.out.println("lockrequest.toXMLString: "+lockrequest.toXMLString());
							System.out.println("-------------------------");
							System.out.println("escapeXml: "+StringEscapeUtils.escapeXml(lockrequest.toXMLString()));
							System.out.println("-------------------------");
							System.out.println("getFile: "+lockrequest.getFile().toXML());
							System.out.println("-------------------------");
							//
							String response = lockManagerClient.getLock(StringEscapeUtils.escapeXml(lockrequest.toXMLString()));
							//*
							System.out.println("response = lockManagerClient.getLockInfo(lockrequest.toXMLString()): "+response);
							System.out.println("-------------------------");
							//*
							response = StringEscapeUtils.unescapeXml(response);
							System.out.println("unescapeXML(response): "+ response);
							System.out.println("-------------------------");
							LockResultResponse vr = new LockResultResponse(response);
							if (vr.getErrorCode() != 0) {
								System.err.println("Error " + vr.getErrorCode() + ": "
										+ vr.getErrorMessage());
								System.exit(vr.getErrorCode());
							
							}
					}

			}  else if (command.equalsIgnoreCase("getLockTypes")) {
						if (argv.length != 2) {
							printHelp();
							System.exit(0);
						}
						connect(wsAdress);
						String response = lockManagerClient.getLockTypes();
						if (response == null){
							System.out.println("response is null");
						}
						response = StringEscapeUtils.unescapeXml(response);
						StringVectorResponse vr = ResponseFactory.readStringVectorResponse(response);
						if (vr.getErrorCode() != 0) {
							System.err.println("Error " + vr.getErrorCode() + ": "+ vr.getErrorMessage());
							System.exit(vr.getErrorCode());
						}
						for (String type : vr.getResult()) {
							System.out.println("vr.getResult() "+type);
						}
				} else if (command.equalsIgnoreCase("getLock")) {
					if (argv.length != 5) {
						printHelp();
						System.exit(0);
					}
					String sender = argv[2];
					String id = argv[3];
					short lockType = Short.parseShort(argv[4]);
					connect(wsAdress);
					
					ConfigLockRequest lockrequest = new ConfigLockRequest(sender,id, lockType);
					
					System.out.println("lockrequest: "+lockrequest);
					System.out.println("-------------------------");
					System.out.println("lockrequest.toXMLString: "+lockrequest.toXMLString());
					System.out.println("-------------------------");
					System.out.println("escapeXml: "+StringEscapeUtils.escapeXml(lockrequest.toXMLString()));
					System.out.println("-------------------------");
					
					String response = lockManagerClient.getLock(StringEscapeUtils.escapeXml(lockrequest.toXMLString()));
					
					System.out.println("response = lockManagerClient.getLockInfo(lockrequest.toXMLString()): "+response);
					System.out.println("-------------------------");
					
					response = StringEscapeUtils.unescapeXml(response);
					/**
					System.out.println("unescapeXML(response): "+ response);
					System.out.println("-------------------------");
					System.out.println("ResponseFactory.readConfigLockResultResponse(response)"+ ResponseFactory.readConfigLockResultResponse(response));
					*/
					LockResultResponse vr = new LockResultResponse(response);
					System.out.println("answer: "+vr.toXMLString());
					if (vr.getErrorCode() != 0) {
						System.err.println("Error " + vr.getErrorCode() + ": "
								+ vr.getErrorMessage());
						System.exit(vr.getErrorCode());
					
					}

				}else {
					printHelp();
				}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public static void printHelp() {
		System.out.println("lockManager <LMAdress> command (Params)");
		System.out.println("@Example: http://localhost:8082/services/lockmanager getLockTypes");
		System.out.println("");
		System.out.println("Commands:");
		System.out.println("    getLockTypes: lists all supported Lock Types: FILE_LOCK und CONFIG_LOCK");
		System.out.println("    lock:  get a lock an a file / FSD");
		System.out.println("             Needed Params:");
		System.out.println("                 config: Path to a file / FSD");
		System.out.println("    unlock:    release lock an a file / FSD ");
		System.out.println("             Needed Params:");
		System.out.println("                 config: Path to a file / FSD");
		System.out.println("    getLockInfo:  get lock-info for a FSD");
		System.out.println("             Needed Params:");
		System.out.println("                string: Path to FSD");
		System.out.println("    getLockInfo:  get lock-info for a file");
		System.out.println("             Needed Params:");
		System.out.println("                string: Path to FSD");
		System.out.println("                string: Name of file");
	}
}