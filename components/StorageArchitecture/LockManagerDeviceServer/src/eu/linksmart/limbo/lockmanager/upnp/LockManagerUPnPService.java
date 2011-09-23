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

package eu.linksmart.limbo.lockmanager.upnp;

import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.osgi.framework.BundleContext;
import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPService;
import org.osgi.service.upnp.UPnPStateVariable;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.nio.channels.FileLock;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import eu.linksmart.storage.helper.ConfigLockRequest;
import eu.linksmart.storage.helper.FileLockRequest;
import eu.linksmart.storage.helper.LinkSmartFile;
import eu.linksmart.storage.helper.LockResult;
import eu.linksmart.storage.helper.ResponseFactory;
import eu.linksmart.storage.helper.ErrorCodes;


public class LockManagerUPnPService implements UPnPService {

	private static Logger logger = Logger.getLogger(LockManagerUPnPService.class.getName());
	final private String SERVICE_ID = "urn:upnp-org:serviceId:LockManager";
	final private String SERVICE_TYPE = "urn:schemas-upnp-org:service:LockManager:1";
	final private String VERSION ="1";
	
	private resultStateVariable result;
	private lockStateVariable lock;
	private UPnPStateVariable[] states;
	private HashMap actions = new HashMap();
	private LockManagerDeviceDevice device;
	
	private String locksPath;
	private Hashtable<String, Vector<String>> configLockSenders;
	private Hashtable<String, ReentrantReadWriteLock> configLocks;
	private Hashtable<String, Vector<String>> fileLockSenders;
	private Hashtable<String, ReentrantReadWriteLock> fileLocks;
	private BundleContext context;
	
	public LockManagerUPnPService(LockManagerDeviceDevice device, BundleContext context) {
		this.device = device;
		this.context = context;
		result = new resultStateVariable();
		lock = new lockStateVariable();
		this.states = new UPnPStateVariable[]{result,lock};
		UPnPAction getLockInfo = new getLockInfoAction(lock,result, this);
		actions.put(getLockInfo.getName(),getLockInfo);
		UPnPAction getLock = new getLockAction(lock,result, this);
		actions.put(getLock.getName(),getLock);
		UPnPAction getLockTypes = new getLockTypesAction(result, this);
		actions.put(getLockTypes.getName(),getLockTypes);
		String lockManagerPath = LockManagerDeviceUPnPActivator.getLMPath();
		try {
			System.out.println("vor init");
			init(lockManagerPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void init(String locksPath) throws Exception {
		System.out.println("in init");
		this.locksPath = locksPath;
		System.out.println("locksPath: "+locksPath);
		configLockSenders = new Hashtable<String, Vector<String>>();
		configLocks = new Hashtable<String, ReentrantReadWriteLock>();
		fileLockSenders = new Hashtable<String, Vector<String>>();
		fileLocks = new Hashtable<String, ReentrantReadWriteLock>();
		System.out.println("configLockSenders"+configLockSenders.toString()+configLockSenders.get("file"));
		System.out.println("configLocks"+configLocks.toString()+configLocks.get("file"));
		Vector<String > senders = null;
		if (locksPath != null) {
			// readLocks from FS
			File[] files = new File(locksPath).listFiles();
			if (files == null) {
				System.out.println("No Files");
			} else if (files.length!=0){
				System.out.println("files!=null"+files.length);
				for (int i = 0; i < files.length; i++){
					System.out.println("filesPath: "+files[i].getAbsolutePath());
					FileInputStream out = new FileInputStream(files[i].getName());
					FileLock fl = out.getChannel().lock();
					if (fl!=null){
						XMLDecoder dec = null; 
						//FileInputStream in = new FileInputStream(files[i].getName());
						dec = new XMLDecoder(out); 
						String id = (String) dec.readObject();
						String file = (String) dec.readObject();
						String  sender = (String) dec.readObject(); 
						Short type =(Short) dec.readObject();
						ReentrantReadWriteLock lock = (ReentrantReadWriteLock) dec.readObject();
						synchronized (this) {
							if (file == "NULL"){//configLock
								configLocks.put(id,lock);
								if (configLockSenders.containsKey(id)){
									senders = configLockSenders.get(id);
									senders.add(sender);
									configLockSenders.remove(id);
									configLockSenders.put(id,senders);
								}else { senders.add(sender);
								configLockSenders.put(id,senders);
								}
								System.out.println("FsdID: " + id + " has a lock of type: " + type + " from sender: " + sender);
							} else { //fileLock
								//String keys = "filelock://" + id + file;
								String keys = id + file;
								fileLocks.put(keys, lock);
								if (fileLockSenders.containsKey(keys)){
									senders = fileLockSenders.get(keys);
									senders.add(sender);
									fileLockSenders.remove(keys);
									fileLockSenders.put(id,senders);
								}else { senders.add(sender);
								fileLockSenders.put(keys,senders);
								}
								System.out.println("File: " + id + "." + file + " has a lock of type: " + type + " from sender: " + sender);
							}
						}
						if ( dec != null ) {
							dec.close(); 
						}
						fl.release();
					}
					out.close();
				}
			}else{
				System.out.println("The Dictionary: "+ locksPath+" is empty.");
			}
		}
	}
	

	public UPnPAction getAction(String name) {
		return (UPnPAction)actions.get(name);
	}

	public UPnPAction[] getActions() {
		return (UPnPAction[])(actions.values()).toArray(new UPnPAction[]{});
	}

	public String getId() {
		return SERVICE_ID;
	}

	public UPnPStateVariable getStateVariable(String name) {
		if (name.equals(result.getName())) return result;
		else if (name.equals(lock.getName())) return lock;
		return null;
	}

	public UPnPStateVariable[] getStateVariables() {
		return states;
	}

	public String getType() {
		return SERVICE_TYPE;
	}

	public String getVersion() {
		return VERSION;
	}
    

	private String getFullPath(String fsid, LinkSmartFile file) {
		//return "filelock://" + fsid + "/" + file.getPath();
		return fsid + "/" + file.getName();
	}
	
	private void fileDelete(ConfigLockRequest request) throws IOException{
		String lockFile = request.getFsdID()+request.getSender()+".xml";
		String path = locksPath + lockFile;
		File f = new File(path);
		if (f.exists()){
			RandomAccessFile out = new RandomAccessFile(f,"rw");
			FileLock fl = out.getChannel().lock();
			if (fl != null){
				System.out.println("File locked");
				f.delete();
				fl.release();
				System.out.println("File released and deleted");
			}
			out.close();	
		} else System.out.println("LockFile not exists!");	
	}
	
	private void fileDelete(FileLockRequest request) throws IOException{
		String lockFile = request.getFsdID()+request.getFile().getName()+request.getSender()+".xml";
		String path = locksPath + "/"+lockFile;
		File f = new File(path);
		if (f.exists()){
			RandomAccessFile out = new RandomAccessFile(f,"rw");
			FileLock fl = out.getChannel().lock();
			if (fl != null){
				System.out.println("File locked");
				f.delete();
				fl.release();
				System.out.println("File released and deleted");
			}
			out.close();	
		} else System.out.println("LockFile not exists!");
	}
	
	private void fileAdd(ConfigLockRequest request, ReentrantReadWriteLock lock) throws IOException{
		String lockFile = request.getFsdID()+request.getSender()+".xml";
		String path = locksPath + "/" +lockFile;
		File f = new File(path);
			if (!f.exists()){
				f.createNewFile();
				FileOutputStream out = new FileOutputStream(f);
				FileLock fl = out.getChannel().lock();
				if (fl!=null) {
					 FileOutputStream fo = new FileOutputStream(f);
					 XMLEncoder enc = null; 
					 enc = new XMLEncoder(fo);  
					 enc.writeObject(request.getFsdID());
					 enc.writeObject("NULL");
					 enc.writeObject(request.getSender());
					 enc.writeObject(request.getLockType());
					 enc.writeObject(lock);
					 if ( enc != null )  enc.close(); 
				}
				out.close();
			} else{
				System.out.println("LockFile exists!");
		       	}	
	}	
		       
	
	private void fileAdd(FileLockRequest request, ReentrantReadWriteLock lock) throws IOException{
		String lockFile = request.getFsdID()+request.getFile().getName()+request.getSender()+".xml";
		String path = locksPath +"/"+ lockFile;
        File f = new File(path);
			if (!f.exists()){
				f.createNewFile();
				FileOutputStream out = new FileOutputStream(f);
				FileLock fl = out.getChannel().lock();
				if (fl!=null){
					 XMLEncoder enc = null; 
					 FileOutputStream fo = new FileOutputStream(f);
					 enc = new XMLEncoder(fo); 
					 enc.writeObject(request.getFsdID());
					 enc.writeObject(request.getFile().getName()); 
					 enc.writeObject(request.getSender()); 
					 enc.writeObject(request.getLockType());
					 enc.writeObject(lock);
					 if ( enc != null ) enc.close();
					 fl.release();
				}
				out.close();
			} else{
				System.out.println("LockFile exists!");
		       	}
	}	
	
	private java.lang.String processConfigLockRequest(java.lang.String lockrequest) {
		System.out.println("ich bin in processConfigLockRequest");
		System.out.println(lockrequest);
		ConfigLockRequest request = null;
		try {
			request = new ConfigLockRequest(lockrequest);
			System.out.println("Request= "+request.toXMLString());
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//ConfigLockRequest request = new ConfigLockRequest(root);
		if (request.getLockType() == ConfigLockRequest.RELEASE_TYPE) {
            ReentrantReadWriteLock lock = null;
            Vector<String> senders = null;
            synchronized (this) {
            	lock = configLocks.get(request.getFsdID());
            	//System.out.println("lock: "+lock.toString());
            	senders = configLockSenders.get(request.getFsdID());
            	//System.out.println("sender: "+senders.toString());
            	if ((lock == null) || (!senders.contains(request.getSender()))) {
            		System.out.println("return here");
            		return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(ErrorCodes.EC_LOCK_NOT_SET,
							"Sender " + request.getSender()
							+ " does not hold a ConfigLock on fsd "
							+ request.getFsdID(), null));
				}
            }
            if (lock.isWriteLocked()) {
            	synchronized (this) {
            		lock.writeLock().unlock();
				    senders.remove(request.getSender());
					configLocks.remove(request.getFsdID());
					configLockSenders.remove(request.getFsdID());
					// TODO: Update File system: delete the file FsdID+Sender".xml"
					try {
						fileDelete(request);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			   }
			} else {
				synchronized (this) {
					lock.readLock().unlock();
					senders.remove(request.getSender());
					configLockSenders.remove(request.getFsdID());
					if (senders == null){
						configLocks.remove(request.getFsdID());
					} else {
						configLockSenders.put(request.getFsdID(),senders);
					}
					// TODO: Update File system: delete the file FsdID+Sender".xml"
					try {
						fileDelete(request);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			LockResult result = new LockResult(request, LockResult.LOCK_GRANTED, null);
			return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(ErrorCodes.EC_NO_ERROR, null,result));
		} else if (request.getLockType() == ConfigLockRequest.READ_TYPE) {
			ReentrantReadWriteLock lock = null;
			Vector<String> senders = null;
			synchronized (this) {
				lock = configLocks.get(request.getFsdID());
				senders = configLockSenders.get(request.getFsdID());
				if  ((lock != null) && (senders.contains(request.getSender()))){
					return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(ErrorCodes.EC_LOCK_ALLREADY_SET_BY_SENDER, "Sender " + request.getSender() + " holds allready a ConfigLock on FSD " + request.getFsdID(), null));
				}
				if (lock == null) {	
					if (senders != null){
						configLockSenders.remove(request.getFsdID());
					}
					senders = new Vector<String>();
				} 
				if ((lock != null)&&(senders != null)){
					if (lock.isWriteLocked()){
				    	return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(ErrorCodes.EC_LOCK_ALLREADY_SET_BY_SENDER, "Sender " + senders.toString() + " holds allready a Write ConfigLock on FSD " + request.getFsdID(), null));   
				    }
					else{configLockSenders.remove(request.getFsdID());//for later update
				    }
				}
			    if ((lock != null)&&(senders == null)){
			    	configLocks.remove(request.getFsdID());//for later update
			    }       
			}
			lock = new ReentrantReadWriteLock();
			lock.readLock().lock();
			synchronized (this) {
				configLocks.put(request.getFsdID(), lock);
				senders.add(request.getSender());
				configLockSenders.put(request.getFsdID(), senders);
				//update File System: 
				try {
					fileAdd(request,lock);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
			LockResult result = new LockResult(request, LockResult.LOCK_GRANTED, null);
			return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(ErrorCodes.EC_NO_ERROR, null,result));
		} else if (request.getLockType() == ConfigLockRequest.WRITE_TYPE) {
			ReentrantReadWriteLock lock = null;
			Vector<String> senders = null;
			synchronized (this) {
				lock = configLocks.get(request.getFsdID());
				senders = configLockSenders.get(request.getFsdID());
				if  ((lock != null) && (senders != null)) {
					return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(ErrorCodes.EC_LOCK_ALLREADY_SET_BY_SENDER, "Senders " + senders.toString() + " hold allready a ConfigLock on FSD " + request.getFsdID(), null));
				}
				if ((lock != null)&& (senders == null)){
					configLocks.remove(request.getFsdID());
				}
				if ((lock == null) && (senders!=null)){
					configLockSenders.remove(request.getFsdID());
				}	
			}
			lock = new ReentrantReadWriteLock();
			lock.writeLock().lock();
			synchronized (this) {
				configLocks.put(request.getFsdID(), lock);
				senders = new Vector<String>();
				senders.add(request.getSender());
				configLockSenders.put(request.getFsdID(), senders);
				//update File System
				try {
					fileAdd(request,lock);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		    LockResult result = new LockResult(request, LockResult.LOCK_GRANTED, null);
			return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(ErrorCodes.EC_NO_ERROR, null,result));
		} else {
			return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(ErrorCodes.EC_ARG_ERROR, "Unknown Lock Type", null));
		}
	}
	
	private java.lang.String processLockRequest(java.lang.String lockrequest) {
	    
		System.out.println("Ich bin in processLockRequest!");
		FileLockRequest request = null;
		String path=null;
		try {
			request = new FileLockRequest(lockrequest);
			System.out.println("Request= "+request.toXMLString());
			path = getFullPath(request.getFsdID(), request.getFile());
			System.out.println("Path: "+path);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (request.getLockType() == FileLockRequest.RELEASE_TYPE) {
			ReentrantReadWriteLock lock = null;
			Vector<String> senders = null;
			synchronized (this) {
				lock = fileLocks.get(path);
				senders = fileLockSenders.get(path);
				if ((lock == null) || (!senders.contains(request.getSender()))) {
					return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(ErrorCodes.EC_LOCK_NOT_SET,
							"Sender " + request.getSender()
							+ " does not hold a FileLock on that entry fsd "
							+ request.getFsdID()
							+"."+request.getFile().getName(), null));
				}
			}
			if (lock.isWriteLocked()) {
				synchronized (this) {
					lock.writeLock().unlock();
					senders.remove(request.getSender());
					fileLocks.remove(path);
					fileLockSenders.remove(path);
				// TODO: Update File system
					try {
						fileDelete(request);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}	
			} else {
				synchronized (this) {
					lock.readLock().unlock();
					senders.remove(request.getSender());
					fileLockSenders.remove(path);
					if (senders == null){
						fileLocks.remove(path);
					} else {
						fileLockSenders.put(path,senders);
					}
					// TODO: Update File system
					try {
						fileDelete(request);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}	
			}	
			LockResult result = new LockResult(request, LockResult.LOCK_GRANTED, null);
			return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(ErrorCodes.EC_NO_ERROR, null,result));
		} else if (request.getLockType() == FileLockRequest.READ_TYPE) {
			ReentrantReadWriteLock lock = null;
			Vector<String> senders = null;
			synchronized (this) {
				lock = fileLocks.get(path);
				senders = fileLockSenders.get(path);
				if ((lock != null) && (senders.contains(request.getSender()))) {
					return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(ErrorCodes.EC_LOCK_ALLREADY_SET_BY_SENDER, "Sender " + request.getSender() + " holds allready a FileLock on that file " + path, null));
				}
				if (lock == null) {	
					if (senders != null){
						fileLockSenders.remove(path);
					}
					senders = new Vector<String>();
				} 
				if ((lock != null)&&(senders != null)){
					if (lock.isWriteLocked()){
				    	return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(ErrorCodes.EC_LOCK_ALLREADY_SET_BY_SENDER, "Sender " + senders.toString() + " holds allready a Write ConfigLock on FSD " + path, null));   
				    }
					else{fileLockSenders.remove(path);//for later update
				    }
				}
			    if ((lock != null)&&(senders == null)){
			    	fileLocks.remove(path);//for later update
			    }       
			}
			lock = new ReentrantReadWriteLock();
			lock.readLock().lock();
			synchronized (this) {
				fileLocks.put(path, lock);
				senders.add(request.getSender());
				fileLockSenders.put(path, senders);
				//update File System: 
				try {
					fileAdd(request,lock);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
			LockResult result = new LockResult(request, LockResult.LOCK_GRANTED, null);
			return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(ErrorCodes.EC_NO_ERROR, null,result));
		} else if (request.getLockType() == FileLockRequest.WRITE_TYPE) {
			ReentrantReadWriteLock lock = null;
			Vector<String> senders = null;
			synchronized (this) {
				lock = fileLocks.get(path);
				senders = fileLockSenders.get(path);
				if ((lock != null) && (senders != null) ) {
					return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(ErrorCodes.EC_LOCK_ALLREADY_SET_BY_SENDER, "Senders " + senders.toString() + " hold allready a FileLock on that entry on FSD " + request.getFsdID(), null));
				}
				if ((lock != null)&& (senders == null)){
					fileLocks.remove(path);
				}
				if ((lock == null) && (senders!=null)){
					fileLockSenders.remove(path);
				}	
			}
			lock = new ReentrantReadWriteLock();
			lock.writeLock().lock();
			synchronized (this) {
				fileLocks.put(path, lock);
				senders = new Vector<String>();
				senders.add(request.getSender());
				fileLockSenders.put(path, senders);
				//update File System
				try {
					fileAdd(request,lock);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
			LockResult result = new LockResult(request, LockResult.LOCK_GRANTED, null);
			return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(ErrorCodes.EC_NO_ERROR, null,result));
		} else {
			return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(ErrorCodes.EC_ARG_ERROR, "Unknown Lock Type", null));
		}
	}
	
	private String processIsFileLocked(java.lang.String lock) {
		System.out.println("Ich bin in IsFileLocked!");
		FileLockRequest request = null;
		String path=null;
		try {
			request = new FileLockRequest(lock);
			System.out.println("Request= "+request.toXMLString());
			path = getFullPath(request.getFsdID(), request.getFile());
			System.out.println("Path: "+path);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    short lockType;
	       synchronized (this) {
	    	   System.out.println("Ich bin in synchronized!");
		       if (fileLocks.get(path) == null) {
		    	   System.out.println("fileLocks.get(path) == null");
		    	   LockResult result = new LockResult(request, LockResult.LOCK_REJECT, null);
		    	   System.out.println(result.toXMLString());
		    	   return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(ErrorCodes.EC_NO_ERROR, null, result));
		       }else {//lock!=null
		    	   if(fileLockSenders.get(path)!=null){  
		    	   	 	if (fileLocks.get(path).isWriteLocked()){
		    	   	 		lockType = 2;
		    	   	 	} else {
		    	   	 		lockType = 1;
		    	   	 	} 
		    	   	 	FileLockRequest concurrent = new FileLockRequest(fileLockSenders.get(path).toString(), request.getFsdID(), request.getFile(), lockType);
		    	   	 	LockResult result = new LockResult(request, LockResult.LOCK_GRANTED,concurrent);
		    	   	 	return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(ErrorCodes.EC_NO_ERROR,null,result));	 
	            		}
		        	else {
		        		return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(ErrorCodes.EC_LOCK_NOT_SET, "no senders",null));
		        	}
	       	}	  
		}
	}
	
	private String processIsConfigLocked(java.lang.String lock) {
		System.out.println("ich bin ran");
		System.out.println(lock);
		ConfigLockRequest request = null;
		try {
			request = new ConfigLockRequest(lock);
			System.out.println("Request= "+request.toXMLString());
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	    //Vector<String> senders = new Vector<String>();
	    short lockType;
	      synchronized (this) {
	    	  System.out.println("in synchronized");
	    	  String id = request.getFsdID();
		      if (configLocks.get(id) == null) {
		    	  System.out.println("look==null");
		    	   LockResult result = new LockResult(request, LockResult.LOCK_REJECT, null);
		    	   System.out.println(result.toXMLString());
		    	   return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(ErrorCodes.EC_NO_ERROR, null, result));
		       }else{ //lock!=null
		    	   if (configLockSenders.get(id) != null){  
		    	   	 	if (configLocks.get(id).isWriteLocked()){
		    	   	 		lockType = 2;
		    	   	 	} else {
		    	   	 		lockType = 1;
		    	   	 	} 
		    	   	 	ConfigLockRequest concurrent = new ConfigLockRequest(configLockSenders.get(id).toString(), request.getFsdID(), lockType);
		    	   	 	LockResult result = new LockResult(request, LockResult.LOCK_GRANTED,concurrent);
		    	   	 	return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(ErrorCodes.EC_NO_ERROR,null,result));	 
	           		}
		        	else {
		        		return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(ErrorCodes.EC_LOCK_NOT_SET, "no senders",null));
		        	}
	      	}
		  }
	}
	
	public java.lang.String getLock(java.lang.String lock ){
		logger.info("LockManager.getLock called");
		System.out.println(lock);
		try {
			logger.debug("Before unescape:\n" + lock);
			String lockUnescaped = StringEscapeUtils.unescapeXml(lock);
			logger.debug("After unescape:\n" + lockUnescaped);
			Document d;
				d = new SAXBuilder().build(new StringReader(lockUnescaped));
			Element root = d.getRootElement();
			if ((root == null)) {
				return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(2 , "configuration contained LockManager",null));
			}
			if (root.getName()=="FileLockRequest") {
				System.out.println("getFileLock");
				return processLockRequest(lockUnescaped);
			}else {
				System.out.println("getConfigLock");
				return processConfigLockRequest(lockUnescaped);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(1, "IOError while trying to find LockManagerDevices",null));
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.debug(lock);
			return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(2, "JDom error reading lockRequest", null));
		}
	}
	
	public java.lang.String getLockInfo(java.lang.String lock ){
		logger.info("LockManager.getLockInfo called");
		try {
			logger.debug("Before unescape:\n" + lock);
			String lockUnescaped = StringEscapeUtils.unescapeXml(lock);
			logger.debug("After unescape:\n" + lockUnescaped);
			System.out.println("After unescape is ok");
			Document d;
				d = new SAXBuilder().build(new StringReader(lockUnescaped));
			Element root = d.getRootElement();
			System.out.println("root= "+ root.toString());
			System.out.println("root= "+ root.getName());
			System.out.println("sender= "+ root.getAttributeValue("sender"));
			System.out.println("linksmartFile= "+ root.getChild("linksmartFile"));
			System.out.println("fsdID= "+ root.getAttributeValue("fsdID"));
			System.out.println("lockType= "+ root.getAttributeValue("lockType"));
			if (root == null) {
				System.out.println("root=nll");
				return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(2, "configuration contained LockManger",null));
			}
			if (root.getName()=="FileLockRequest") {
				System.out.println("FileLockInfo");
				return processIsFileLocked(lockUnescaped);
			}else{
				System.out.println("ConfigLockInfo");
				return processIsConfigLocked(lockUnescaped);
			} 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(1, "IOError while trying to find LockMangerDevices",null));
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			System.out.println("Here some problem");
			e.printStackTrace();
			logger.debug(lock);
			return StringEscapeUtils.escapeXml(ResponseFactory.createLockResultResponse(2, "JDom error reading lockRequest",null));
		}
	}
	

	public java.lang.String  getLockTypes(){
		  logger.debug("LockManager.getLockTypes called");
		  Vector<String> result = new Vector<String>();
	      result.add("FILE_LOCK");
	      result.add("CONFIG_LOCK");
	      String answer = StringEscapeUtils.escapeXml(ResponseFactory.createStringVectorResponse(0, null, result));
	      logger.debug(answer);
	      return answer;
	  }


}



