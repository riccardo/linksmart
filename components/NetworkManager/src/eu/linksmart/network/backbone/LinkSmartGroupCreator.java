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
 * Represents an LinkSmart Peer Group factory. Creates a Peer Group for LinkSmart. 
 * It offers two possibilities in order to create the Peer Group:
 * - createLinkSmartPeerGroup(String groupName) -> No secure peerGroup
 * - createLinkSmartPwdProtectedPeerGroup(String groupName, String login,
 *     String passwd) -> Passwd protected group
 * 
 * Also provides the means for discovering, joining and authenticating any 
 * peer into the recently created peergroup. For this purpose, it provides 2 methods:
 * - discoverPeerGroup() Discovers the LinkSmart group
 * - joinPeerGroup(PeerGroup LinkSmartGroup, String login, String passwd)
 */

package eu.linksmart.network.backbone;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;


import net.jxta.credential.AuthenticationCredential;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.Element;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocument;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.StructuredTextDocument;
import net.jxta.document.TextElement;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.ID;
import net.jxta.id.IDFactory;
import net.jxta.impl.membership.passwd.PasswdMembershipService;
import net.jxta.membership.Authenticator;
import net.jxta.membership.MembershipService;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.ModuleSpecID;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;

/**
 * LinkSmartGroupCreator
 */
public class LinkSmartGroupCreator {
	
	private Logger logger = Logger.getLogger(LinkSmartGroupCreator.class.getName());
	private PeerGroup rootGroup;
	private DiscoveryService rootGroupDiscoveryService;
	private PeerGroupID gid;	
	String desc = "LinkSmart Peer Group";
	String specID = 
		"urn:jxta:uuid-309B33F10EDF48738183E3777A7C3DE9C5BFE5794E974DD99AC7D409F5686F3306";
	PeerGroup linksmartGroup;

	/**
	 * Constructor
	 * @param rootPeerGroup the root peer group
	 * @param gid the group ID
	 * @deprecated
	 */
	public LinkSmartGroupCreator(PeerGroup rootPeerGroup, String gid) {
		try {
			this.gid = (PeerGroupID) IDFactory.fromURI(new URI(gid));
		} catch (URISyntaxException e) {
			System.out.println("Not possible to create group: ID is not valid");
			e.printStackTrace();
		}
		this.rootGroup = rootPeerGroup;
	}
	
	/**
	 * Creates a unprotected group
	 * 
	 * @param groupName the name of the group
	 * @return PeerGroup peer group created
	 * @deprecated
	 */
	public PeerGroup createLinkSmartPeerGroup(String groupName){
		/* Create the new application group, and publish its various advertisements. */
		try {
			ModuleImplAdvertisement implAdv =
				rootGroup.getAllPurposePeerGroupImplAdvertisement();
			ModuleSpecID modSpecID = (ModuleSpecID) IDFactory.fromURI(new URI(specID));
			implAdv.setModuleSpecID(modSpecID);

			PeerGroupID groupID = gid;
			linksmartGroup = rootGroup.newGroup(groupID, implAdv, groupName, desc);
			PeerGroupAdvertisement pgadv = linksmartGroup.getPeerGroupAdvertisement();
			
			rootGroupDiscoveryService = rootGroup.getDiscoveryService();
			rootGroupDiscoveryService.publish(implAdv);
			rootGroupDiscoveryService.remotePublish(null, implAdv);
			rootGroupDiscoveryService.remotePublish(null, pgadv);

			/* Join the group. */
			if (linksmartGroup != null) {
				AuthenticationCredential cred =
					new AuthenticationCredential(linksmartGroup, null, null);
				MembershipService membershipService = linksmartGroup.getMembershipService();
				Authenticator authenticator = membershipService.apply(cred);
				if (authenticator.isReadyForJoin()) {
					membershipService.join(authenticator);
					logger.debug("Joined group: " + linksmartGroup);
				}
				else {
					logger.debug("Impossible to join the group");
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Exiting.");
			System.exit(1);
		}
		
		return linksmartGroup;
	}
	
	/**
	 * Creates a password protected group
	 * 
	 * @param groupName name of the group
	 * @param login login to create the group
	 * @param passwd password to create the group
	 * @return a peer group
	 * @deprecated
	 */
	public PeerGroup createLinkSmartPwdProtectedPeerGroup(String groupName, 
			String login, String passwd){
		
		PeerGroupAdvertisement linksmartGroupAdvertisement;
		
		/* Create a Peer group module implementation adv. */
		ModuleImplAdvertisement passwdMembershipModuleImplAdvertisement;
		passwdMembershipModuleImplAdvertisement =
			this.createPasswdMembershipPeerGroupModuleImplAdv(rootGroup);
		
		/* Publish it in parent group. */
		DiscoveryService rootGroupDiscoveryService =
			rootGroup.getDiscoveryService();
		
		try {
			rootGroupDiscoveryService.publish(passwdMembershipModuleImplAdvertisement, 
				PeerGroup.DEFAULT_LIFETIME, PeerGroup.DEFAULT_EXPIRATION);
			rootGroupDiscoveryService.remotePublish(passwdMembershipModuleImplAdvertisement,
				PeerGroup.DEFAULT_EXPIRATION);
		} catch (java.io.IOException e) {
			logger.error("Can't publish passwdMemebershipModuleImplAdv ");
			System.exit(1);
		}
		
		/* Now create the Peer Group Advertisement. */
		linksmartGroupAdvertisement = 
			this.createPeerGroupAdv(passwdMembershipModuleImplAdvertisement, 
				groupName, login, passwd);
		
		/* Publish it in the parent group. */
		try {
			rootGroupDiscoveryService.publish(linksmartGroupAdvertisement,
				PeerGroup.DEFAULT_LIFETIME, PeerGroup.DEFAULT_EXPIRATION);
			rootGroupDiscoveryService.remotePublish(linksmartGroupAdvertisement,
				PeerGroup.DEFAULT_EXPIRATION);
		} catch (java.io.IOException e) {
			logger.error("Can't publish peerGroupAdvertisement ");
			System.exit(1);
		}
		
		/* Create the Peer Group. */
		if (linksmartGroupAdvertisement == null) {
			System.err.println("LinkSmartGroupAdvertisement is null!");
		}
		try {
			linksmartGroup=rootGroup.newGroup(linksmartGroupAdvertisement);
		} catch (net.jxta.exception.PeerGroupException e) {
			logger.error("Can't create group from advertisement");
			e.printStackTrace();
			return null;
		}
		
		return linksmartGroup;
	}
	
	/**
	 * Create a peer Group advertisement. In order to create a private group, we 
	 * use a fixed group ID in order to create always the same group
	 * 
	 * @param passwdMembershipModuleImplAdvertisement the password
	 * @param groupName the group name
	 * @param login the login
	 * @param passwd the password
	 * @return the peer group advertisement
	 * @deprecated
	 */
	private PeerGroupAdvertisement createPeerGroupAdv(
		ModuleImplAdvertisement passwdMembershipModuleImplAdvertisement,
		String groupName, String login, String passwd) {
		
		/* Creates the adv for the peer group. */
		PeerGroupAdvertisement linksmartGroupAdvertisement =
			(PeerGroupAdvertisement) AdvertisementFactory.newAdvertisement(
				PeerGroupAdvertisement.getAdvertisementType());
		
		/* 
		 * Instead of creating a new groupID each time by using
		 * linksmartGroupAdvertisement.setGroupID(IDFactory.newPeerGroupID())
		 * a fixed group ID is used so it creates allways the same group
		 */
		PeerGroupID groupID = null;
		groupID = gid;

		linksmartGroupAdvertisement.setPeerGroupID(groupID);
		linksmartGroupAdvertisement.setModuleSpecID(
			passwdMembershipModuleImplAdvertisement.getModuleSpecID());
		linksmartGroupAdvertisement.setName(groupName);
		linksmartGroupAdvertisement.setDescription("Peer group with membership: "
			+ "Login and password needed");
		
		/*
		 * Create the Structured document containing the login and password 
		 * information. Put them into Param section of Peer Group
		 */
		if (login != null) {
			StructuredTextDocument loginAndPasswd = (StructuredTextDocument)
				StructuredDocumentFactory.newStructuredDocument(
					new MimeMediaType("text/xml"), "Param");
			String loginAndPasswdString = login + ":"
				+ PasswdMembershipService.makePsswd(passwd) + ":";
			TextElement loginElement = loginAndPasswd.createElement("login",
				loginAndPasswdString);
			loginAndPasswd.appendChild(loginElement);
			/* Include it on PeerGroupadveertisement. */
			linksmartGroupAdvertisement.putServiceParam(PeerGroup.membershipClassID,
				loginAndPasswd);
		}
		return linksmartGroupAdvertisement;
	}
	
	/**
	 * Create the module implementation advertisement for pwd protected peer group
	 * 
	 * @param rootGroup the root group
	 * @return the module implementation advertisement
	 * @deprecated
	 */
	private ModuleImplAdvertisement createPasswdMembershipPeerGroupModuleImplAdv(
			PeerGroup rootGroup) {
		
		ModuleImplAdvertisement allPurposePeerGroupImplAdv = null;
		
		try {
			allPurposePeerGroupImplAdv = 
				rootGroup.getAllPurposePeerGroupImplAdvertisement();
		} catch (java.lang.Exception e) {
			logger.error("Can't execute: getAllPurposePeerGroupImplAdvertisement()");
			System.exit(1);
		}
		
		ModuleImplAdvertisement passwdMembershipPeerGroupModuleImplAdv =
			allPurposePeerGroupImplAdv;
		ModuleImplAdvertisement passwdMembershipServiceModuleImplAdv = null;
		StdPeerGroupParamAdvertisement passwdMembershipPeerGroupParamAdv = null;
		
		try {
			passwdMembershipPeerGroupParamAdv =	new StdPeerGroupParamAdvertisement(
				allPurposePeerGroupImplAdv.getParam());
		} catch (PeerGroupException e) {
			e.printStackTrace();
		}
	
		Hashtable allPurposePeerGroupServicesHashtable =
			(Hashtable) passwdMembershipPeerGroupParamAdv.getServices();		
		Enumeration allPurposePeerGroupServicesEnnumeration = 
			allPurposePeerGroupServicesHashtable.keys();
		
		boolean membershipServiceFound = false;
		
		while ((membershipServiceFound) 
				&& (allPurposePeerGroupServicesEnnumeration.hasMoreElements())) {
			Object allPurposePeerGroupServiceID = 
				allPurposePeerGroupServicesEnnumeration.nextElement();
			if (allPurposePeerGroupServiceID.equals(PeerGroup.membershipClassID)) {
				ModuleImplAdvertisement allPurposePeerGroupMembershipServiceModuleImplAdv =
					(ModuleImplAdvertisement) allPurposePeerGroupServicesHashtable.
						get(allPurposePeerGroupServiceID);
				/* The passwdMembershipServiceModuleImplAdv is created. */
				passwdMembershipServiceModuleImplAdv = 
					this.createPasswdMembershipServiceModuleImplAdv(
						allPurposePeerGroupMembershipServiceModuleImplAdv);
			}
			
			allPurposePeerGroupServicesHashtable.remove(allPurposePeerGroupServiceID);
			
			/*
			 * Remove all purpose Membership Service and replace it by the 
			 * Passwd Membership Service
			 */
			allPurposePeerGroupServicesHashtable.put(PeerGroup.membershipClassID, 
				passwdMembershipServiceModuleImplAdv);
			membershipServiceFound = true;
			
			passwdMembershipPeerGroupModuleImplAdv.setParam((Element) 
				passwdMembershipPeerGroupParamAdv.getDocument(MimeMediaType.XMLUTF8));
			
			if (!passwdMembershipPeerGroupModuleImplAdv.getModuleSpecID().
					equals(PeerGroup.allPurposePeerGroupSpecID)) {
				passwdMembershipPeerGroupModuleImplAdv.setModuleSpecID(IDFactory.
					newModuleSpecID(passwdMembershipPeerGroupModuleImplAdv.
						getModuleSpecID().getBaseClass()));
			} else {
				ID passwdGrpModSpecID = ID.create(URI.create("urn" + "jxta:uuid-" 
					+ "DeadBeefDeafBabaFeedVabe00000001" + "04" + "06"));
				passwdMembershipPeerGroupModuleImplAdv.
					setModuleSpecID((ModuleSpecID) passwdGrpModSpecID);
			}			
		}
		return passwdMembershipPeerGroupModuleImplAdv;
	}
	
	/**
	 * Create the module implementation  advertisement
	 * 
	 * @param allPurposePeerGroupMembershipServiceModuleImplAdv the purpose 
	 * peer group membership service module
	 * @return the module implementation advertisment
	 * @deprecated
	 */
	private ModuleImplAdvertisement createPasswdMembershipServiceModuleImplAdv(
			ModuleImplAdvertisement allPurposePeerGroupMembershipServiceModuleImplAdv) {
		/* Creates a new ModuleImplAdvertisement for the membership servic. */
		ModuleImplAdvertisement passwdMembershipServiceModuleImplAdv = 
			(ModuleImplAdvertisement) AdvertisementFactory.newAdvertisement(
				ModuleImplAdvertisement.getAdvertisementType());
		passwdMembershipServiceModuleImplAdv.setModuleSpecID(
			PasswdMembershipService.passwordMembershipSpecID);
		passwdMembershipServiceModuleImplAdv.setDescription("Module Implementation "
			+ "Advertisement for the Password protected Membership Service");
		passwdMembershipServiceModuleImplAdv.setCompat(
			allPurposePeerGroupMembershipServiceModuleImplAdv.getCompat());
		passwdMembershipServiceModuleImplAdv.setUri(
			allPurposePeerGroupMembershipServiceModuleImplAdv.getUri());
		passwdMembershipServiceModuleImplAdv.setProvider(
			allPurposePeerGroupMembershipServiceModuleImplAdv.getProvider());
		return passwdMembershipServiceModuleImplAdv;
	}
	
	/**
	 * Discover peer group advertisements.
	 *   
	 * @return The advertisement of the peer group discovered
	 * @deprecated
	 */
	public PeerGroupAdvertisement discoverPeerGroup() {
		/* First discover the peer group
		 * In most cases we should use discovery listeners so that
		 * we can do the discovery asynchroneously.
		 * Here I won't, for increased simplicity and because
		 * The Peer Group Advertisement is in the local cache for sure
		 */ 
 
		DiscoveryService myNetPeerGroupDiscoveryService = null; 
		if (rootGroup != null) {
			myNetPeerGroupDiscoveryService = rootGroup.getDiscoveryService();
		}
		else {
			logger.error("Can't join  Peer Group since it's parent is null");
			System.exit(1);
		}
		
		Enumeration<Advertisement> pgLocalAdvEnumeration = null;
		PeerGroupAdvertisement linksmartGroupAdvertisement = null;
		try {
			pgLocalAdvEnumeration = myNetPeerGroupDiscoveryService.
				getLocalAdvertisements(DiscoveryService.GROUP,
					"GID", gid.toString());
		} catch (java.io.IOException e) {
			logger.error("Can't Discover Local Adv");
		}

		if (pgLocalAdvEnumeration != null) {
			while (pgLocalAdvEnumeration.hasMoreElements()) {
				PeerGroupAdvertisement pgAdv = null;
				pgAdv = (PeerGroupAdvertisement) pgLocalAdvEnumeration.nextElement();
				if (pgAdv.getPeerGroupID().equals(gid)) {
					linksmartGroupAdvertisement=pgAdv;
				}
			}
		}
		else {
			/* If no group advertisement was found, it will have to be created. */
			linksmartGroupAdvertisement=null;
		}
		
		return linksmartGroupAdvertisement;
	}
	
	/**
	 * Join the peer Group
	 * 
	 * @param linksmartGroup The peer group to be joined
	 * @param login The login for this pwd protected peer group
	 * @param passwd The password for this pwd protected peer group
	 * @deprecated
	 */
	public void joinPeerGroup(PeerGroup linksmartGroup, String login, String passwd) {
		StructuredDocument creds = null;
		try {
			AuthenticationCredential authCred = 
				new AuthenticationCredential(linksmartGroup, null, creds);
			MembershipService membershipService = linksmartGroup.getMembershipService();
			Authenticator auth = membershipService.apply(authCred);
			completeAuth(auth, login, passwd);
			if (!auth.isReadyForJoin()) {
				logger.error("Authentication Faliure: Not joined in the group");
			}
			membershipService.join(auth);
		} catch (Exception e) {
			logger.error("Authentication Faliure: Login was not correct");
			e.printStackTrace();
		}
	}
		
	/**
	 * Autenticates the peer inside the peer group
	 * @param auth the authenticator
	 * @param login the login
	 * @param passwd the password
	 * @deprecated
	 */
	private void completeAuth(Authenticator auth, String login, String passwd)
			throws Exception {

		Method[] methods = auth.getClass().getMethods();
		Vector authMethods = new Vector();
		/* Find out with fields of the application needs to be filled
		 * Alias Go through the methods of the Authenticator class and
		 * copy them sorted by name into a vector.
		 */
		for(int eachMethod = 0; eachMethod < methods.length; eachMethod++) {
			if (methods[eachMethod].getName().startsWith("setAuth")) {
				if (Modifier.isPublic(methods[eachMethod].getModifiers())) {
					/* Sorted insertion. */
					for(int doInsert = 0; doInsert <= authMethods.size(); doInsert++) {
						int insertHere = -1;
						if (doInsert == authMethods.size()) {
							insertHere = doInsert;
						}
						else {
							if (methods[eachMethod].getName().compareTo(
									((Method) authMethods.elementAt(doInsert)).
										getName()) <= 0) {
								insertHere = doInsert;
							}
						}
						
						if (-1 != insertHere) {
							authMethods.insertElementAt(methods[eachMethod], insertHere);
							break;
						}
					}
				}
			}
		}
		
		Object [] AuthId = {login};
		Object [] AuthPasswd = {passwd};
		for (int eachAuthMethod = 0; eachAuthMethod<authMethods.size(); eachAuthMethod++) {
			Method doingMethod = (Method) authMethods.elementAt(eachAuthMethod);
			String authStepName = doingMethod.getName().substring(7);
			if (doingMethod.getName().equals("setAuth1Identity")) {
				/* Found identity Method, providing identity. */
				doingMethod.invoke(auth, AuthId);
			}
			else {
				if (doingMethod.getName().equals("setAuth2_Password")) {
					/* Found Passwd Method, providing passwd. */
					doingMethod.invoke(auth, AuthPasswd);
				}
			}
		}
	}

}
